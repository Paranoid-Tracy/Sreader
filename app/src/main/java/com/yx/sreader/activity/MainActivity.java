package com.yx.sreader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.yx.sreader.R;
import com.yx.sreader.fragment.ShelfFragment;
import com.yx.sreader.view.DragGridView;


/**
 * Created by iss on 2018/3/23.
 */

public class MainActivity extends AppCompatActivity{
    private BottomNavigationBar mBottomNavigationBar;
    private ShelfFragment mShelfFragment;
    private DragGridView bookshelf;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        mShelfFragment = new ShelfFragment();
        getWindow().setBackgroundDrawable(null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mShelfFragment = new ShelfFragment();
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
    }
    private  void setDefaultFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment,mShelfFragment).commit();
    }
}
