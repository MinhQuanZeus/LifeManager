package zeus.minhquan.lifemanager.appcore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;
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

import zeus.minhquan.lifemanager.R;

/**
 * Created by QuanT on 5/03/2017.
 */
public class ToDoListFragment extends Fragment implements AlarmFloatingActionButton.OnVisibilityChangedListener {

    private static SimpleDateFormat mDateFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private Database mDatabase = null;
    private TaskAdapter mAdapter = null;
    private RecyclerView mAlarmRecyclerView;
    private RelativeLayout mEmptyView;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingLayout;
    private TaskListListener mCallbacks;
    private Context context;
    private boolean mShowAddButtonInToolbar;

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
//        View logoView = ((ToDoMainActivity) getActivity()).getToolbarLogoView(toolbar);
//        logoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //logo clicked
//                if (((ToDoMainActivity) getActivity()).mDrawerLayout.isDrawerOpen(((ToDoMainActivity) getActivity()).mDrawerList)) {
//                    ((ToDoMainActivity) getActivity()).mDrawerLayout.closeDrawer(((ToDoMainActivity) getActivity()).mDrawerList);
//                } else {
//                    ((ToDoMainActivity) getActivity()).mDrawerLayout.openDrawer(((ToDoMainActivity) getActivity()).mDrawerList);
//                }
//            }
//        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);


        AlarmFloatingActionButton fab = (AlarmFloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add task
                displayCreateDialog();
            }
        });
        fab.setVisibilityListener(this);
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
        } else {

            mAdapter.setQuery(query.toLiveQuery());
            mAdapter.notifyDataSetChanged();
        }

        if (LifeManagerApplication.getInstance().getToDoCB().getListsView().getTotalRows() == 0) {

            mAlarmRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            enableCollapsingBehaviour(false);
        } else {
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

    private void displayCreateDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        //   android.util.Log.d("Context",context.toString());
        alert.setTitle(getResources().getString(R.string.title_dialog_new_list));

        LayoutInflater inflater = this.getLayoutInflater(null);
        final View view = inflater.inflate(R.layout.view_dialog_input, null);
        final EditText input = (EditText) view.findViewById(R.id.text);
        input.requestFocus();
        alert.setView(view);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try {
                    String title = input.getText().toString();
                    if (title.length() == 0) {
                        return;
                    }
                    create(title);
                    updateUI();

                } catch (CouchbaseLiteException e) {
                    Log.e(LifeManagerApplication.TAG, "Cannot create a new menu_todo_list", e);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        // Dialog
        AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialog.show();
    }

    private Document create(String title) throws CouchbaseLiteException {
        String currentTimeString = mDateFormatter.format(new Date());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "menu_todo_list");
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

    @Override
    public void visibilityChanged(int visibility) {
        if (View.INVISIBLE == visibility) {
            mShowAddButtonInToolbar = true;
        } else if (View.VISIBLE == visibility) {
            mShowAddButtonInToolbar = false;
        }
        android.util.Log.d("Show add",mShowAddButtonInToolbar+"");
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_todo_list, menu);
        MenuItem add = menu.findItem(R.id.create);
        add.setVisible(mShowAddButtonInToolbar);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.create) {
            displayCreateDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface TaskListListener {
        void onTaskSelected(Document list);
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Document mList;
        private TextView mTitleTextView;
        private RelativeLayout mContainer;

        public TaskHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.text_task);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.list_task_item_container);
        }

        public void bindTask(Document list) {
            mList = list;
            if (list != null) {
                String title = (String) list.getProperty("title");
                if (title == null || title.isEmpty()) {
                    mTitleTextView.setVisibility(View.GONE);
                } else {
                    mTitleTextView.setVisibility(View.VISIBLE);
                    mTitleTextView.setText(title);
                }
            }
        }

        public void setFirstItemDimensions() {
            int itemHeightTall = getContext().getResources().getDimensionPixelSize(R.dimen.alarm_list_item_height_tall);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, itemHeightTall);
            mContainer.setLayoutParams(params);

            int tallItemPadding = itemHeightTall - getContext().getResources().getDimensionPixelSize(R.dimen.alarm_list_item_height);
            int leftPadding = getContext().getResources().getDimensionPixelSize(R.dimen.todo_list_item_left_padding);
            mContainer.setPadding(leftPadding, tallItemPadding, 0, 0);
        }


        @Override
        public void onClick(View v) {
            mCallbacks.onTaskSelected(mList);
            showTasks(mList);
        }

        private void showTasks(Document list) {
            Intent intent = new Intent(getActivity(), TaskActivity.class);
            intent.putExtra(TaskActivity.INTENT_LIST_ID, list.getId());
            intent.putExtra(TaskActivity.INTENT_LIST_TITLE, list.getProperties().get("title").toString());
            startActivity(intent);
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<ToDoListFragment.TaskHolder>
            implements AlarmListItemTouchHelperCallback.ItemTouchHelperAdapter {
        private LiveQuery query;
        private QueryEnumerator enumerator;

        public TaskAdapter(final Context context, LiveQuery query) {
            this.query = query;
            this.query.addChangeListener(new LiveQuery.ChangeListener() {
                @Override
                public void changed(final LiveQuery.ChangeEvent event) {
                    ((Activity) context).runOnUiThread(new Runnable() {
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
            final Document list = (Document) getItem(position);
            if (position == 0) {
                holder.setFirstItemDimensions();
            }
            holder.bindTask(list);
        }


        @Override
        public int getItemCount() {
            return enumerator != null ? enumerator.getCount() : 0;
        }

        @Override
        public void onItemDismiss(int position) {
            Document list = (Document) mAdapter.getItem(position);
            String owner = (String) list.getProperties().get("owner");

            if (owner == null || owner.equals("p:" + LifeManagerApplication.getInstance().getToDoCB().getCurrentUserId()))
                deleteList(list);
            else
                LifeManagerApplication.getInstance().getToDoCB().showErrorMessage("Only owner can delete the menu_todo_list", null);
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
                        Log.e(LifeManagerApplication.TAG, "Cannot delete menu_todo_list", e);
                        return false;
                    }
                    return true;
                }
            });
            if (LifeManagerApplication.getInstance().getToDoCB().getListsView().getTotalRows() == 0) {
                updateUI();
            }

        }
    }
}

