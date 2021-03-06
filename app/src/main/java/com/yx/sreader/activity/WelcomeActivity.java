package com.yx.sreader.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.support.v7.appcompat.R.anim;

import com.bumptech.glide.Glide;
import com.yx.sreader.R;
import com.yx.sreader.service.WebService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by iss on 2018/3/23.
 */

public class WelcomeActivity extends AppCompatActivity {
    private ImageView imageView;
    private static int DURATION = 1500;
    private static List<String>[] listbookinfo = new List[7];
    private static boolean IsInNetwork = false;
    private static Handler handler = new Handler();
    private String[] info = new String[7];
    private String [] db = {"Book","History","Youth", "Inspirational", "Biography","Science", "Philosophy"};
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_welcome);
        imageView = (ImageView)this.findViewById(R.id.wel_img);
        Glide.with(this).load(R.mipmap.start).crossFade(DURATION).into(imageView);
        init();
        startAppDelay();
    }
    private void startAppDelay(){
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        },DURATION);
    }

    private void startApp(){
        startActivity(new Intent(this,MainActivity.class));
        overridePendingTransition(anim.abc_grow_fade_in_from_bottom, anim.abc_shrink_fade_out_from_bottom);
        finish();
    }

    /**
     * 开启线程初始化服务器数据供后续推荐列表使用
     */
    private void init(){
        new Thread(new MyThread()).start();
    }


    public static List<String>[] getListbookinfo() {
        return listbookinfo;
    }

    public static boolean isInNetwork(){
        return IsInNetwork;
    }


    public class MyThread implements Runnable {
        @Override
        public void run() {
            for(int i = 0; i < 7 ;i++){
                info[i] = WebService.executeHttpGet(db[i]);
            }
            // info = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //System.out.printf("当前获取数据"+info);
                    if(!(info[0].equals("no"))) {
                        for(int i = 0; i < 7 ;i++) {
                            listbookinfo[i] = stringToList(info[i]);
                            IsInNetwork = true;
                        }
                    }
                }
            });
        }
    }

    /**
     * 将服务器接收转化的String型再转成List方便取出子项
     * @param strs
     * @return
     */
    private List<String> stringToList(String strs){
        String str[] = strs.split(",");
        return Arrays.asList(str);
    }


}
