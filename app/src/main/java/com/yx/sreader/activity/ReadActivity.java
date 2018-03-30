package com.yx.sreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yx.sreader.R;
import com.yx.sreader.util.BookPageFactory;
import com.yx.sreader.view.PageWidget;

import java.io.IOException;

/**
 * Created by iss on 2018/3/29.
 */

public class ReadActivity extends AppCompatActivity {

    private static final String TAG = "ReadActivity";
    public static String words;
    private static String word = "";// 记录当前页面的文字


    private static String bookPath,bookName;// 记录读入书的路径及书名
    private static int begin = 0;// 记录的书籍开始位置

    private Context mContext;
    private Typeface typeface;
    private  int scale;
    int screenHeight;
    int readHeight; // 电子书显示高度
    int screenWidth;
    private int defaultFontSize = 0;
    private int minFontSize = 0;
    private int maxFontSize = 0;
    private static Bitmap mCurPageBitmap, mNextPageBitmap;
    public static Canvas mCurPageCanvas, mNextPageCanvas;
    private PageWidget mPageWidget;
    private Boolean isNight; // 亮度模式,白天和晚上
    private BookPageFactory bookPageFactory;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private int fontsize = 30; // 字体大小
    private int light; // 亮度值
    protected long count = 1;
    private WindowManager.LayoutParams lp;
    private String ccc = null;// 记录是否为快捷方式调用
    private static int begin1;







    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保存屏幕常亮
        hideSystemUI();
        mContext = this.getBaseContext();
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(),"font/QH.ttf");
        scale = (int)mContext.getResources().getDisplayMetrics().density;
        //获取屏幕宽高
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);
        screenWidth = displaysize.x;
        screenHeight = displaysize.y;
        readHeight = screenHeight - screenWidth / 320;

        defaultFontSize = (int) mContext.getResources().getDimension(R.dimen.reading_default_text_size) ;  //text size
        minFontSize = (int) mContext.getResources().getDimension(R.dimen.reading_min_text_size);
        maxFontSize = (int) mContext.getResources().getDimension(R.dimen.reading_max_text_size);


        mCurPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);      //android:LargeHeap=true  use in  manifest application
        mNextPageBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);
        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        bookPageFactory = new BookPageFactory(screenWidth, readHeight,this);// 书工厂


        mPageWidget = new PageWidget(this, screenWidth, readHeight);// 页面
        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.layout_read);
        rlayout.addView(mPageWidget);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        fontsize = getSize();// 获取配置文件中的size大小
        light = getLight();// 获取配置文件中的light值
        isNight = getDayOrNight();
        count = sp.getLong(bookPath + "count", 1);

        lp = getWindow().getAttributes();
        lp.screenBrightness = light / 10.0f < 0.01f ? 0.01f : light / 10.0f;
        getWindow().setAttributes(lp);
        //获取intent中的携带的信息
        Intent intent = getIntent();
        bookPath = intent.getStringExtra("bookpath");
        bookName = intent.getStringExtra("bookname");
        ccc = intent.getStringExtra("ccc");
        begin1 = intent.getIntExtra("bigin", 0);
        if(begin1 == 0) {
            begin = sp.getInt(bookPath + "begin", 0);
        }else {
            begin = begin1;
        }
        if (false) {
            /*pagefactory.setBgBitmap(BookPageFactory.decodeSampledBitmapFromResource(
                    this.getResources(), R.drawable.main_bg, screenWidth, readHeight));
            pagefactory.setM_textColor(Color.rgb(128, 128, 128));*/
        } else {
            //bookPageFactory.setBgBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.bg));
            //bookPageFactory.setBgBitmap(BookPageFactory.decodeSampledBitmapFromResource(
            //   this.getResources(),R.drawable.bg,screenWidth,readHeight));
            Bitmap bmp = Bitmap.createBitmap(screenWidth,screenHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(getResources().getColor(R.color.read_background_paperYellow));
            bookPageFactory.setM_textColor(getResources().getColor(R.color.read_textColor));
            bookPageFactory.setBgBitmap(bmp);
            bookPageFactory.setM_textColor(Color.rgb(28, 28, 28));
        }
        mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
        try {
            bookPageFactory.openbook(bookPath, begin);
            //bookPageFactory.setM_fontSize(fontsize);
            bookPageFactory.onDraw(mCurPageCanvas);
            // Log.d("ReadActivity", "sp中的size" + size);
            //word = pagefactory.getFirstTwoLineText();// 获取当前阅读位置的前两行文字,用作书签
            //editor.putInt(bookPath + "begin", begin).apply();
            // Log.d("ReadActivity", "第一页首两行文字是" + word);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //pagefactory.getBookInfo();  //获取章节目录
                }
            }).start();
        } catch (IOException e1) {
            Log.e(TAG, "打开电子书失败", e1);
            Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
        }


        //setPop(); //初始化POPUPWINDOW
        //mPageWidget.setBitmaps(mCurPageBitmap,mCurPageBitmap);
        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                //触点在中间
                if(v == mPageWidget){
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        mPageWidget.abortAnimation();
                        mPageWidget.calcCornerXY(event.getX(),event.getY());
                        bookPageFactory.onDraw(mCurPageCanvas);
                        int x = (int)event.getX();
                        int y = (int)event.getY();
                        if(x > screenWidth/3 &&x < screenWidth *2/3 &&
                                y > screenHeight/3 && y < screenHeight *2/3){

                        }
                        //触点在左侧
                        if(x < screenWidth /2){
                            try {
                                bookPageFactory.prePage();
                                begin = bookPageFactory.getM_mbBufBegin();// 获取当前阅读位置
                                word = bookPageFactory.getFirstTwoLineText();// 获取当前阅读位置的首行文字
                            } catch (IOException e1) {
                                Log.e(TAG, "onTouch->prePage error", e1);
                            }
                            if (bookPageFactory.isFirstPage()) {
                                Toast.makeText(mContext, "当前是第一页", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            bookPageFactory.onDraw(mNextPageCanvas);


                        }
                        //触点在右侧
                        else if(x >= screenWidth /2){
                            try{
                                bookPageFactory.nextPage();
                                begin = bookPageFactory.getM_mbBufBegin();// 获取当前阅读位置
                                word = bookPageFactory.getFirstTwoLineText();// 获取当前阅读位置的首行文字

                            }catch (IOException e){

                            }
                            if (bookPageFactory.isLastPage()) {
                                Toast.makeText(mContext, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            bookPageFactory.onDraw(mNextPageCanvas);
                        }
                        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);


                    }
                    ret = mPageWidget.doTouchEvent(event);
                    return ret;

                }

                return false;
            }
        });



    }
    /**
     * 隐藏菜单。沉浸式阅读
     */
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    /**
     * 获取夜间还是白天阅读模式
     */
    private boolean getDayOrNight() {
        return sp.getBoolean("night", false);
    }

    /**
     * 读取配置文件中字体大小
     */
    private int getSize() {
        return sp.getInt("size", defaultFontSize);
    }
    /**
     * 读取配置文件中亮度值
     */
    private int getLight() {
        return sp.getInt("light", 3);
    }
}
