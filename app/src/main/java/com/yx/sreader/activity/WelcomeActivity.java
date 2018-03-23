package com.yx.sreader.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yx.sreader.R;

/**
 * Created by iss on 2018/3/23.
 */

public class WelcomeActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        if(Build.VERSION.SDK_INT >= 21){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.welcome_activity);
        imageView = (ImageView)this.findViewById(R.id.wel_img);
    }
}
