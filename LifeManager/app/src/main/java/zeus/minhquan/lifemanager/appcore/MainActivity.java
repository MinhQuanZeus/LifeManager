package zeus.minhquan.lifemanager.appcore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.controllerRemind.RemindActivity;

public class MainActivity extends BaseActivity {
    private ImageView iv_alarm, iv_todo;
    private ImageView iv_remind;
    private RecyclerView mMenuRecyclerView;
    private MenuAdapter menuAdapter;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        mDrawerList.setItemChecked(position, true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setLogo(R.drawable.ic_menu_black_24dp);
        View logoView = getToolbarLogoView(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //logo clicked
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
            }
        });
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mCollapsingLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        mMenuRecyclerView = (RecyclerView) findViewById(R.id.menu_recycler_view);
        mMenuRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(1, "Alarms", "Sleep if you can", R.drawable.ic_alarm_black_24dp, R.color.orange));
        menuList.add(new Menu(2, "Remind", "Don't forget your event", R.drawable.ic_event_black_24dp, R.color.green));
        menuList.add(new Menu(3, "To do lists", "Don't forget your task", R.drawable.ic_check_list_black_24dp, R.color.blue));
        menuAdapter = new MenuAdapter(menuList);
        mMenuRecyclerView.setAdapter(menuAdapter);

    }

    private void showActivity(int id) {
        Intent intent;
        switch (id) {
            case 1:
                intent = new Intent(MainActivity.this, AlarmMainActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(MainActivity.this, RemindActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(MainActivity.this, ToDoMainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class Menu {
        private int id;
        private String title;
        private String description;
        private int image;
        private int background;

        public Menu(int id, String title, String description, int image, int background) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.image = image;
            this.background = background;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getImage() {
            return image;
        }

        public int getBackground() {
            return background;
        }
    }

    private class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView titleView;
        private TextView desView;
        private RelativeLayout mContainer;
        private Menu menu;

        public MenuHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.iv_menu_item);
            titleView = (TextView) itemView.findViewById(R.id.tv_menu_item_title);
            desView = (TextView) itemView.findViewById(R.id.tv_menu_item_des);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.list_menu_item_container);
        }

        public void bindMenu(Menu menu) {
            this.menu = menu;
            imageView.setImageResource(menu.getImage());
            titleView.setText(menu.getTitle());
            desView.setText(menu.getDescription());
        }

        @Override
        public void onClick(View v) {
            showActivity(menu.getId());
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuHolder> {
        List<Menu> menuList;

        public MenuAdapter(List<Menu> menuList) {
            this.menuList = menuList;
        }

        @Override
        public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.list_main_menu_item, parent, false);
            return new MenuHolder(view);
        }

        @Override
        public void onBindViewHolder(MenuHolder holder, int position) {
            Menu menu = menuList.get(position);
            holder.bindMenu(menu);
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }
    }
}
