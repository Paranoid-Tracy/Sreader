package com.yx.sreader.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.support.v7.appcompat.R.anim;

import com.bumptech.glide.Glide;
import com.yx.sreader.R;

/**
 * Created by iss on 2018/3/23.
 */

public class WelcomeActivity extends AppCompatActivity {
    private ImageView imageView;
    private static int DURATION = 1500;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_welcome);
        imageView = (ImageView)this.findViewById(R.id.wel_img);
        Glide.with(this).load(R.mipmap.start).crossFade(DURATION).into(imageView);
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

}
