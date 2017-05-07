package zeus.minhquan.lifemanager.appcore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.adapters.NavigationDrawerListAdapter;
import zeus.minhquan.lifemanager.controllerRemind.RemindActivity;
import zeus.minhquan.lifemanager.models.Items;

public class BaseActivity extends AppCompatActivity {
    protected static int position;
    private static boolean isLaunch = true;
    protected FrameLayout frameLayout;
    protected ListView mDrawerList;
    protected String[] listArray = {"Home", "Alarm", "Remind", "To do list"};
    protected ArrayList<Items> _items;
    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int preScreen = 0;

    public static View getToolbarLogoView(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_navigation_drawer);

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        _items = new ArrayList<Items>();
        _items.add(new Items("Alarm", "Item One Description", R.drawable.alarm_clock_icon));
        _items.add(new Items("Remind", "Item Two Description", R.drawable.remind));
        _items.add(new Items("To do list", "Item Three Description", R.drawable.todo));

        View header = (View) getLayoutInflater().inflate(R.layout.list_view_header_layout, null);
        mDrawerList.addHeaderView(header);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new NavigationDrawerListAdapter(this, _items));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                openActivity(position);
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,						/* host Activity */
                mDrawerLayout, 				/* DrawerLayout object */
                R.drawable.ic_menu_black_24dp,     /* nav drawer image to replace 'Up' caret */
                R.string.open_drawer,       /* "open drawer" description for accessibility */
                R.string.close_drawer)      /* "close drawer" description for accessibility */ {
            @Override
            public void onDrawerClosed(View drawerView) {
//                getActionBar().setTitle(listArray[position]);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        if (isLaunch) {
            isLaunch = false;
            openActivity(0);
        }
    }

    protected void openActivity(int position) {
        mDrawerLayout.closeDrawer(mDrawerList);
        BaseActivity.position = position; //Setting currently selected position in this field so that it will be available in our child activities.

        switch (position) {
            case 0:
                startActivity(new Intent(this, MainActivity.class));
                preScreen = 0;
                break;
            case 1:
                startActivity(new Intent(this, AlarmMainActivity.class));
                preScreen = 1;
                break;
            case 2:
                startActivity(new Intent(this, RemindActivity.class));
                preScreen = 2;
                break;
            case 3:
                startActivity(new Intent(this, ToDoMainActivity.class));
                preScreen = 3;
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openActivity(preScreen);
        mDrawerList.setSelection(preScreen);
    }
}
