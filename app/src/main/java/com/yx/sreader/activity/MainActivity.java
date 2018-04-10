package com.yx.sreader.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.yx.sreader.R;
import com.yx.sreader.adapter.ShelfAdapter;
import com.yx.sreader.fragment.RecommendFragment;
import com.yx.sreader.fragment.ShelfFragment;
import com.yx.sreader.service.WebService;
import com.yx.sreader.view.DragGridView;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by iss on 2018/3/23.
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
    private BottomNavigationBar mBottomNavigationBar;
    private ShelfFragment mShelfFragment;
    private RecommendFragment mRecommendFragment;
    private DragGridView bookshelf;
    private String mCurrentFragment;
    private static Boolean isExit = false;
    private static Handler handler = new Handler();
    private String info;
    private static List<String> listbookinfo;



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
        init();
        //getDatasync();

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
        mCurrentFragment = "mShelfFragment";
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        System.out.print("qqq");
        if(mCurrentFragment.equals("mShelfFragment")&&DragGridView.getShowDeleteButton()){
            mShelfFragment.onKeyDown(keyCode, event);
            return true;
        }
        else {
            exitBy2Click();
            return false;
        }
    }

    private void exitBy2Click() {
        // press twice to exit
        Timer tExit;
        if (!isExit) {
            isExit = true; // ready to exit

            Toast.makeText(
                    this,
                    this.getResources().getString(R.string.press_twice_to_exit),
                    Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // cancel exit
                }
            }, 2000); // 2 seconds cancel exit task

        } else {
            finish();
            // call fragments and end streams and services
            System.exit(0);
        }
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
    public void getDatasync(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://" + "172.23.0.168:8080" + "/BookInfo/BookLet?"+"xiaoming")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.d("kwwl","response.code()=="+response.code());
                        Log.d("kwwl","response.message()=="+response.message());
                        Log.d("kwwl","res=="+response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void init(){
        new Thread(new MyThread()).start();
    }

    public static List<String> getListbookinfo() {
        return listbookinfo;
    }


    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeHttpGet("xiaoming");
            // info = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.printf("当前获取数据"+info);
                    listbookinfo = stringToList(info);
                }
            });
        }
    }

    private List<String> stringToList(String strs){
        String str[] = strs.split(",");
        return Arrays.asList(str);
    }


}
