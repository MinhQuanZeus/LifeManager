package zeus.minhquan.lifemanager.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.TransactionalTask;
import com.couchbase.lite.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.AlarmFloatingActionButton;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;
import zeus.minhquan.lifemanager.utils.LiveQueryAdapter;

/**
 * Created by QuanT on 5/2/2017.
 */

public class ListActivity extends AppCompatActivity {
    private Database mDatabase = null;
    private ListAdapter mAdapter = null;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingLayout;

    private static SimpleDateFormat mDateFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        LifeManagerApplication application = (LifeManagerApplication) getApplication();
        mDatabase = application.getToDoCB().getDatabase();
        AlarmFloatingActionButton fab = (AlarmFloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayCreateDialog();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.todo_list_title);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mCollapsingLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        Query query = getQuery();
        mAdapter = new ListAdapter(this, query.toLiveQuery());

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Document list = (Document) mAdapter.getItem(i);
                showTasks(list);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int pos, long id) {
                PopupMenu popup = new PopupMenu(ListActivity.this, view);
                popup.inflate(R.menu.list_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Document list = (Document) mAdapter.getItem(pos);
                        String owner = (String) list.getProperties().get("owner");
                        LifeManagerApplication application = (LifeManagerApplication) getApplication();
                        if (owner == null || owner.equals("p:" + application.getToDoCB().getCurrentUserId()))
                            deleteList(list);
                        else
                            application.getToDoCB().showErrorMessage("Only owner can delete the list", null);
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });

        enableCollapsingBehaviour(true);

        mDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }


    private void enableCollapsingBehaviour(boolean enableCollapse) {
        if (!enableCollapse) {
            mAppBarLayout.setExpanded(true);
        }
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingLayout.getLayoutParams();
        int scrollFlags = enableCollapse ?
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL :
                AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
        params.setScrollFlags(scrollFlags);
        mCollapsingLayout.setLayoutParams(params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.create:
                displayCreateDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mAdapter.invalidate();
        super.onDestroy();
    }

    private void displayCreateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.title_dialog_new_list));

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.view_dialog_input, null);
        final EditText input = (EditText) view.findViewById(R.id.text);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String title = input.getText().toString();
                    if (title.length() == 0)
                        return;
                    create(title);
                } catch (CouchbaseLiteException e) {
                    Log.e(LifeManagerApplication.TAG, "Cannot create a new list", e);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) { }
        });

        alert.show();
    }

    private Query getQuery() {
        LifeManagerApplication application = (LifeManagerApplication) getApplication();
        return application.getToDoCB().getListsView().createQuery();
    }

    private Document create(String title) throws CouchbaseLiteException {
        String currentTimeString = mDateFormatter.format(new Date());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "list");
        properties.put("title", title);
        properties.put("created_at", currentTimeString);
        properties.put("members", new ArrayList<String>());

        LifeManagerApplication application = (LifeManagerApplication) getApplication();
        String userId = application.getToDoCB().getCurrentUserId();
        if (userId != null)
            properties.put("owner", "p:" + userId);

        Document document = mDatabase.createDocument();
        document.putProperties(properties);

        return document;
    }

    private void deleteList(final Document list) {
        LifeManagerApplication application = (LifeManagerApplication) getApplication();
        final Query query = application.getToDoCB().getTasksView().createQuery();
        query.setDescending(true);

        List<Object> startKeys = new ArrayList<Object>();
        startKeys.add(list.getId());
        startKeys.add(new HashMap<String, Object>());

        List<Object> endKeys = new ArrayList<Object>();
        endKeys.add(list.getId());

        query.setStartKey(startKeys);
        query.setEndKey(endKeys);

        mDatabase.runInTransaction(new TransactionalTask() {
            @Override
            public boolean run() {
                try {
                    QueryEnumerator tasks = query.run();
                    while(tasks.hasNext()) {
                        QueryRow task = tasks.next();
                        task.getDocument().getCurrentRevision().deleteDocument();
                    }
                    list.delete();
                } catch (CouchbaseLiteException e) {
                    Log.e(LifeManagerApplication.TAG, "Cannot delete list", e);
                    return false;
                }
                return true;
            }
        });
    }

    private void showTasks(Document list) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(TaskActivity.INTENT_LIST_ID, list.getId());
        intent.putExtra(TaskActivity.INTENT_LIST_TITLE,list.getProperties().get("title").toString());
        startActivity(intent);
    }


    private class ListAdapter extends LiveQueryAdapter {
        public ListAdapter(Context context, LiveQuery query) {
            super(context, query);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_list, null);
            }

            final Document list = (Document) getItem(position);
            TextView text = (TextView) convertView.findViewById(R.id.text_task);
            text.setText((String) list.getProperty("title"));
            return convertView;
        }
    }
}