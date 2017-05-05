package zeus.minhquan.lifemanager.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.AlarmFloatingActionButton;
import zeus.minhquan.lifemanager.appcore.AlarmListItemTouchHelperCallback;
import zeus.minhquan.lifemanager.appcore.DividerItemDecoration;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;

/**
 * Created by QuanT on 5/03/2017.
 */
public class ToDoListFragment extends Fragment {

    private Database mDatabase = null;
    private TaskAdapter mAdapter = null;
    private RecyclerView mAlarmRecyclerView;
    private RelativeLayout mEmptyView;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingLayout;
    private TaskListListener mCallbacks;
    private int count=0;
    private Context context;


    private static SimpleDateFormat mDateFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        mDatabase = LifeManagerApplication.getInstance().getToDoCB().getDatabase();
        mAlarmRecyclerView = (RecyclerView) view
                .findViewById(R.id.task_recycler_view);
        mAlarmRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));

        Toolbar toolbar = (Toolbar) view
                .findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.todo_list_title);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        AlarmFloatingActionButton fab = (AlarmFloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add task
                displayCreateDialog();
            }
        });
        mEmptyView = (RelativeLayout) view.findViewById(R.id.empty_view);

        mCollapsingLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);

        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);

        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        ItemTouchHelper.Callback callback = new AlarmListItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mAlarmRecyclerView);


        return view;
    }

    public void updateUI() {
        Query query = getQuery();
        if (mAdapter == null) {
            mAdapter = new TaskAdapter(context, query.toLiveQuery());
            mAlarmRecyclerView.setAdapter(mAdapter);
            Toast toast = Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getContext(), "not null", Toast.LENGTH_SHORT);
            toast.show();
            mAdapter.setQuery(query.toLiveQuery());
            mAdapter.notifyDataSetChanged();
        }

        if (count==0) {
            mAlarmRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            enableCollapsingBehaviour(false);
        } else {
            android.util.Log.d("Task","Non empty");
            mAlarmRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            enableCollapsingBehaviour(true);
        }
    }

    private Query getQuery() {
        return LifeManagerApplication.getInstance().getToDoCB().getListsView().createQuery();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (TaskListListener) context;
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public interface TaskListListener {
        void onTaskSelected(Document list);
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private RelativeLayout mContainer;
        Document mList;

        public TaskHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.text_task);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.list_task_item_container);
        }

        public void bindTask(Document list) {
            mList = list;
            String title = (String) list.getProperty("title");
            android.util.Log.d("Task","Title"+title);
            Toast toast = Toast.makeText(getContext(), title, Toast.LENGTH_SHORT);
            toast.show();
            if (title == null || title.isEmpty()) {
                mTitleTextView.setVisibility(View.GONE);
            } else {
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setText(title);
            }
            Log.d("Bind Task","Task");

        }

        public void setFirstItemDimensions() {
            int itemHeightTall = getContext().getResources().getDimensionPixelSize(R.dimen.alarm_list_item_height_tall);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, itemHeightTall);
            mContainer.setLayoutParams(params);

            int tallItemPadding = itemHeightTall - getContext().getResources().getDimensionPixelSize(R.dimen.alarm_list_item_height);
            mContainer.setPadding(0, tallItemPadding, 0, 0);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onTaskSelected(mList);
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<ToDoListFragment.TaskHolder>
            implements AlarmListItemTouchHelperCallback.ItemTouchHelperAdapter {
        private LiveQuery query;
        private QueryEnumerator enumerator;
        private Context context;

        public TaskAdapter(Context context, LiveQuery query) {
           // this.context = context;
            this.query = query;
            this.query.addChangeListener(new LiveQuery.ChangeListener() {
                @Override
                public void changed(final LiveQuery.ChangeEvent event) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enumerator = event.getRows();
                            notifyDataSetChanged();
                        }
                    });

                }
            });
            this.query.start();

        }

        public void setQuery(LiveQuery query) {
            this.query = query;
        }

        public void invalidate() {
            if (query != null)
                query.stop();
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.view_list, parent, false);
            return new TaskHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            Document list = (Document) getItem(position);
            Toast toast = Toast.makeText(getContext(), "Bind", Toast.LENGTH_SHORT);
            toast.show();
            if (position == 0) {
                holder.setFirstItemDimensions();
            }
            holder.bindTask(list);
            android.util.Log.d("Bind","Binded");
        }

        @Override
        public int getItemCount() {
            count = enumerator != null ? enumerator.getCount() : 0;
            return count;
        }

        @Override
        public void onItemDismiss(int position) {
            Document list = (Document) mAdapter.getItem(position);
            String owner = (String) list.getProperties().get("owner");

            if (owner == null || owner.equals("p:" + LifeManagerApplication.getInstance().getToDoCB().getCurrentUserId()))
                deleteList(list);
            else
                LifeManagerApplication.getInstance().getToDoCB().showErrorMessage("Only owner can delete the list", null);
            notifyItemRemoved(position);
        }

        public Object getItem(int i) {
            return enumerator != null ? enumerator.getRow(i).getDocument() : null;
        }


        @Override
        public void onItemDismissCancel(int position) {
            notifyItemChanged(position);
        }

        private void deleteList(final Document list) {
            final Query query = LifeManagerApplication.getInstance().getToDoCB().getTasksView().createQuery();
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
                        while (tasks.hasNext()) {
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
            if (getItemCount() == 0) {
                updateUI();
            }

        }
    }

    private void displayCreateDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
     //   android.util.Log.d("Context",context.toString());
        alert.setTitle(getResources().getString(R.string.title_dialog_new_list));

        LayoutInflater inflater = this.getLayoutInflater(null);
        final View view = inflater.inflate(R.layout.view_dialog_input, null);
        final EditText input = (EditText) view.findViewById(R.id.text);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String title = input.getText().toString();
                    if (title.length() == 0) {

                        return;
                    }
                    android.util.Log.d("Todo title", title);
                    create(title);
                } catch (CouchbaseLiteException e) {
                    Log.e(LifeManagerApplication.TAG, "Cannot create a new list", e);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private Document create(String title) throws CouchbaseLiteException {
        String currentTimeString = mDateFormatter.format(new Date());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "list");
        properties.put("title", title);
        properties.put("created_at", currentTimeString);
        properties.put("members", new ArrayList<String>());

        String userId = LifeManagerApplication.getInstance().getToDoCB().getCurrentUserId();
        if (userId != null)
            properties.put("owner", "p:" + userId);

        Document document = mDatabase.createDocument();
        document.putProperties(properties);


        return document;
    }
}

