package com.yx.sreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.yx.sreader.R;
import com.yx.sreader.fragment.RecommendFragment;
import com.yx.sreader.fragment.ShelfFragment;
import com.yx.sreader.view.DragGridView;


/**
 * Created by iss on 2018/3/23.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
    private BottomNavigationBar mBottomNavigationBar;
    private ShelfFragment mShelfFragment;
    private RecommendFragment mRecommendFragment;
    private DragGridView bookshelf;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        mShelfFragment = new ShelfFragment();
        mRecommendFragment = new RecommendFragment();
        getWindow().setBackgroundDrawable(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initBotNavigationBar();

    }
    public void initBotNavigationBar(){
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                );
        mBottomNavigationBar.setBarBackgroundColor("#FCFCFC");
        mBottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.comment, "书架").setInActiveColor(R.color.colorAccent).setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.mipmap.comment, "推荐").setInActiveColor(R.color.colorAccent).setActiveColorResource(R.color.colorAccent))
                .addItem(new BottomNavigationItem(R.mipmap.comment, "下载").setInActiveColor(R.color.colorAccent).setActiveColorResource(R.color.colorAccent))
                .setFirstSelectedPosition(0)
                .initialise();
        setDefaultFragment();
        mBottomNavigationBar.setTabSelectedListener(this);
    }
    private  void setDefaultFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment,mShelfFragment).commit();
    }

    @Override
    public void onTabSelected(int position) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                ft.replace(R.id.main_fragment, mShelfFragment).commit();
                break;
            case 1:
                ft.replace(R.id.main_fragment, mRecommendFragment).commit();
                break;
           /* case 2:
                ft.replace(R.id.main_fragment, videoFragment).commit();
                break;*/
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);  //加载菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        *//*if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,FileAcitvity.class);
            startActivity(intent);
            return true;
        }
        if (id==android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }*//*
        return super.onOptionsItemSelected(item);
    }*/
}
