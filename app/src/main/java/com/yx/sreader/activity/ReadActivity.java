package com.yx.sreader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yx.sreader.R;
import com.yx.sreader.adapter.MarkAdapter;
import com.yx.sreader.adapter.MyPagerAdapter;
import com.yx.sreader.database.BookMarks;
import com.yx.sreader.fragment.BookMarkFragment;
import com.yx.sreader.util.BookPageFactory;
import com.yx.sreader.util.CommonUtil;
import com.yx.sreader.view.PageWidget;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by iss on 2018/3/29.
 */

public class ReadActivity extends AppCompatActivity implements View.OnClickListener ,SeekBar.OnSeekBarChangeListener,SlidingMenu.OnCloseListener,SlidingMenu.OnOpenListener{

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
    private BookPageFactory bookPageFactory;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;
    private int fontsize = 30; // 字体大小
    private int light; // 亮度值
    protected long count = 1;
    private WindowManager.LayoutParams lp;
    private String ccc = null;// 记录是否为快捷方式调用
    private static int begin1;
    private Boolean show = false;// popwindow是否显示
    private View popupWindow,toolPop,toolpop1, toolpop2, toolpop3, toolpop4;
    private PopupWindow mPopupWindow,mToolpop, mToolpop1, mToolpop2,
            mToolpop3, mToolpop4;
    private int curPos = 0,
                prePos = 0;
    private LinearLayout layout;
    private TextView fontSize, readLight, bookMark, readJump,readSet;
    private TextView btn_mark_add,btn_mark_my,lightPlus,linghtDecrease;
    private SeekBar seekBar1, seekBar2, seekBar4;
    private TextView jumpOk, jumpCancel,fontBig,fontSmall;
    private static int jumpcencel_begin;
    private TextView markEdit4;
    private Boolean isNight; // 白天和晚上
    private ImageButton imageBtn_light,pop_return ;
    private SlidingMenu mSlidingMenu;
    private View iv_filter;

    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private DisplayMetrics dm;
    private ImageButton button_back;
    private TextView title;
    private BookMarkFragment bookMarkFragment;
    private ListView markListview;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保存屏幕常亮
        hideSystemUI();
        iv_filter = (View)this.findViewById(R.id.cover);
        //StatusBarCompat.setStatusBarColor(this, R.color.read_background_paperYellow, false);
        mSlidingMenu = new SlidingMenu(this);
        mSlidingMenu.setFadeEnabled(true);
        mSlidingMenu.setFadeDegree(0.4f);
        mSlidingMenu.setMode(SlidingMenu.LEFT);     //设置从左弹出/滑出SlidingMenu
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);   //设置占满屏幕
        mSlidingMenu.attachToActivity(this,SlidingMenu.SLIDING_WINDOW);    //绑定到哪一个Activity对象
        mSlidingMenu.setMenu(R.layout.slidingmenulayout);                   //设置弹出的SlidingMenu的布局文件
        mSlidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);       //设置SlidingMenu所占的偏移
        mSlidingMenu.setOnCloseListener(this);
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
        setPop(); //初始化POPUPWINDOW
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
        begin1 = intent.getIntExtra("begin", 0);
        if(begin1 == 0) {
            begin = sp.getInt(bookPath + "begin", 0);
        }else {
            begin = begin1;
        }
        if (isNight) {
            bookPageFactory.setBgBitmap(BookPageFactory.decodeSampledBitmapFromResource(
                    this.getResources(), R.drawable.main_bg, screenWidth, readHeight));
            bookPageFactory.setM_textColor(Color.rgb(128, 128, 128));
        } else {
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
            bookPageFactory.setM_fontSize(fontsize);
            bookPageFactory.onDraw(mCurPageCanvas);
             //Log.d("ReadActivity", "sp中的size" + size);
            word = bookPageFactory.getFirstTwoLineText();// 获取当前阅读位置的前两行文字,用作书签
            editor.putInt(bookPath + "begin", begin).apply();
             Log.d("ReadActivity", "第一页首两行文字是" + word);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    bookPageFactory.getBookInfo();  //获取章节目录
                }
            }).start();
        } catch (IOException e1) {
            Log.e(TAG, "打开电子书失败", e1);
            Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
        }


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
                            if(!show){
                                showSystemUI();
                                pop();
                                show = true;
                            }else {
                                hideSystemUI();

                            }
                            return false;
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
     * 初始化所有POPUPWINDOW
     */
    private void setPop(){
        popupWindow = this.getLayoutInflater().inflate(R.layout.popup_window,null);
        mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        toolPop = this.getLayoutInflater().inflate(R.layout.toolpop,null);
        mToolpop = new PopupWindow(toolPop,ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        toolpop1 = this.getLayoutInflater().inflate(R.layout.tool_size, null);
        mToolpop1 = new PopupWindow(toolpop1, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        toolpop2 = this.getLayoutInflater().inflate(R.layout.tool_light, null);
        mToolpop2 = new PopupWindow(toolpop2, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        toolpop3 = this.getLayoutInflater().inflate(R.layout.tool_mark, null);
        mToolpop3 = new PopupWindow(toolpop3, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        toolpop4 = this.getLayoutInflater().inflate(R.layout.tool_jump, null);
        mToolpop4 = new PopupWindow(toolpop4, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);



    }
    private void pop(){
        mPopupWindow.showAtLocation(mPageWidget, Gravity.NO_GRAVITY, 0, 0);
        fontSize = (TextView) popupWindow.findViewById(R.id.bookBtn_size);
        readLight = (TextView) popupWindow.findViewById(R.id.bookBtn_light);
        bookMark = (TextView) popupWindow.findViewById(R.id.bookBtn_mark);
        readJump = (TextView) popupWindow.findViewById(R.id.bookBtn_jump);
        //readSet = (TextView) popupWindow.findViewById(R.id.readSet);
        layout = (LinearLayout) popupWindow.findViewById(R.id.bookpop_bottom);//主要为了夜间模式时设置背景
        //System.out.println("高度"+layout.getTop());
        fontSize.setTypeface(typeface);//设置字体
        readLight.setTypeface(typeface);
        bookMark.setTypeface(typeface);
        readJump.setTypeface(typeface);
        //readSet.setTypeface(typeface);
        TextView blank_view = (TextView) popupWindow.findViewById(R.id.blank_view);
        pop_return = (ImageButton) popupWindow.findViewById(R.id.pop_return);
        imageBtn_light = (ImageButton) popupWindow.findViewById((R.id.imageBtn_light));
        getDayOrNight();
        if (isNight) {
            //layout.setBackgroundResource(R.drawable.tmall_bar_bg);
            imageBtn_light.setImageResource(R.drawable.menu_light_icon2);
        } else {
            //layout.setBackgroundResource(R.drawable.tmall_bar_bg);
            imageBtn_light.setImageResource(R.drawable.menu_daynight_icon);
        }
        fontSize.setOnClickListener(this);
        readLight.setOnClickListener(this);
        bookMark.setOnClickListener(this);
        readJump.setOnClickListener(this);
        //readSet.setOnClickListener(this);
        blank_view.setOnClickListener(this);
        pop_return.setOnClickListener(this);
        imageBtn_light.setOnClickListener(this);


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

    public void setToolPop(int a) {
        if (a == prePos && a != 0) {
            if (mToolpop.isShowing()) {
                popDismiss();
            } else {
                mToolpop.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
                        screenWidth * 45 / 320);
                switch (a){
                    case 1:
                        clickFont();
                        break;
                    case 2:
                        clickLight();
                        break;
                    case 3:
                        clickBookMarks();
                        break;
                    case 4:
                        clickJump();
                        break;

                }

            }
        }else {
            if (mToolpop.isShowing()) {
                // 对数据的记录
                popDismiss();
            }
            mToolpop.showAtLocation(mPageWidget, Gravity.BOTTOM, 0,
                    screenWidth * 45 / 320);
            switch (a){
                case 1:
                    clickFont();
                    break;
                case 2:
                    clickLight();
                    break;
                case 3:
                    clickBookMarks();
                    break;
                case 4:
                    clickJump();
                    break;

            }

        }
        prePos = a;

    }

    /**
     * 关闭弹出pop
     */
    public void popDismiss() {
        mToolpop.dismiss();
        mToolpop1.dismiss();
        mToolpop2.dismiss();
        mToolpop3.dismiss();
        mToolpop4.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.blank_view :
                if (show) {
                    show = false;
                    hideSystemUI();
                    mPopupWindow.dismiss();
                    popDismiss();
                }
                break;
            // 字体按钮
            case R.id.bookBtn_size:
                curPos = 1;
                setToolPop(curPos);
                break;

            // 亮度按钮
            case R.id.bookBtn_light:
                curPos = 2;
                setToolPop(curPos);
                break;
            // 书签按钮
            case R.id.bookBtn_mark:
                curPos = 3;
                setToolPop(curPos);
                break;
            //跳转
            case R.id.bookBtn_jump:
                curPos = 4;
                setToolPop(curPos);
                jumpcencel_begin = begin;
                break;
            //跳转确定按钮
            case R.id.jump_ok:
                clear();
                hideSystemUI();
                bookPageFactory.setM_mbBufBegin(begin);
                bookPageFactory.setM_mbBufEnd(begin);
                postInvalidateUI();
                break;
            //跳转取消按钮
            case R.id.jump_cancel:
                clear();
                hideSystemUI();
                bookPageFactory.setM_mbBufBegin(jumpcencel_begin);
                bookPageFactory.setM_mbBufEnd(jumpcencel_begin);
                postInvalidateUI();
                break;
            // 夜间模式按钮
            case R.id.imageBtn_light:
                if (isNight) {
                    isNight = false;
                    //layout.setBackgroundResource(R.drawable.tmall_bar_bg);
                    bookPageFactory.setM_textColor(Color.rgb(50, 65, 78));
                    imageBtn_light.setImageResource(R.drawable.menu_daynight_icon);

                    // pagefactory.setBgBitmap(BitmapFactory.decodeResource(
                    //         this.getResources(), R.drawable.bg));
                    Bitmap bmp=Bitmap.createBitmap(screenWidth,screenHeight, Bitmap.Config.RGB_565);
                    Canvas canvas=new Canvas(bmp);
                    canvas.drawColor(Color.rgb(250,249,222));
                    bookPageFactory.setBgBitmap(bmp);
                } else {
                    isNight = true;
                    //layout.setBackgroundResource(R.drawable.tmall_bar_bg);
                    bookPageFactory.setM_textColor(Color.rgb(128, 128, 128));
                    imageBtn_light.setImageResource(R.drawable.menu_light_icon2);
                    bookPageFactory.setBgBitmap(BitmapFactory.decodeResource(
                            this.getResources(), R.drawable.main_bg));
                }
                setDayOrNight();
                bookPageFactory.setM_mbBufBegin(begin);
                bookPageFactory.setM_mbBufEnd(begin);
                postInvalidateUI();
                break;
            //顶部返回按钮
            case R.id.pop_return:
                finish();
                break;
            //我的书签
            case R.id.Btn_mark_my:

                if (iv_filter.isShown())
                    iv_filter.setVisibility(View.GONE);
                else {
                    iv_filter.setVisibility(View.VISIBLE);
                }
                mSlidingMenu.toggle(true);
                if (show) {
                    show = false;
                    hideSystemUI();
                    mPopupWindow.dismiss();
                    popDismiss();
                }
                break;
            // 添加书签按钮
            case R.id.Btn_mark_add:
                word = word.trim();
                while (word.startsWith(" ")) {
                    word.substring(1,word.length()).trim();
                }
                BookMarks bookMarks = new BookMarks();
                List<BookMarks> bookMarks1 = DataSupport.where("text = ?",word).find(BookMarks.class);
                try {
                    if(!bookMarks1.isEmpty()){
                        Toast.makeText(ReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                    }else {
                        SimpleDateFormat sf = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm ss");
                        String time = sf.format(new Date());
                        bookMarks.setTime(time);
                        bookMarks.setBegin(begin);
                        bookMarks.setText(word);
                        bookMarks.setBookpath(bookPath);
                        bookMarks.save();

                        Toast.makeText(ReadActivity.this, "书签添加成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    Toast.makeText(ReadActivity.this, "添加书签失败", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ReadActivity.this, "添加书签失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /**
     * 记录配置文件中字体大小
     */
    private void setSize(int fontsize) {
        try {
            //  fontsize = seekBar1.getProgress() + defaultFontSize;
            editor.putInt("size", fontsize);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "setSize-> Exception error", e);
        }
    }

    /**
     * 记录配置文件中亮度值
     */
    private void setLight() {
        try {
            light = seekBar2.getProgress();
            editor.putInt("light", light);

            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "setLight-> Exception error", e);
        }
    }

    /**
     * 设置夜间还是白天阅读模式
     *
     * */
    private void setDayOrNight () {
        try {
            if (isNight) {
                editor.putBoolean("night", true);
            } else {
                editor.putBoolean("night", false);
            }
            editor.apply();
        }catch (Exception e) {
            Log.e(TAG, "setDayOrNight-> Exception error", e);
        }
    }


    /**
     * 刷新界面
     */
    public void postInvalidateUI() {
        mPageWidget.abortAnimation();
        bookPageFactory.onDraw(mCurPageCanvas);
        try {
            bookPageFactory.currentPage();
            begin = bookPageFactory.getM_mbBufBegin();// 获取当前阅读位置
            word = bookPageFactory.getFirstTwoLineText();// 获取当前阅读位置的首两行文字
        } catch (IOException e1) {
            Log.e(TAG, "postInvalidateUI->IOException error", e1);
        }

        bookPageFactory.onDraw(mNextPageCanvas);
        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
        mPageWidget.postInvalidate();

    }

    /**
     * 点击字体
     */
    private void clickFont(){
        mToolpop1.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        if (CommonUtil.getBottomStatusHeight(mContext) != 0) {
            int popofset = 40 * scale + CommonUtil.getBottomStatusHeight(mContext);
            mToolpop1.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, popofset);
        } else
            mToolpop1.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 120);
        seekBar1 = (SeekBar) toolpop1.findViewById(R.id.seekBar_size);
        fontBig = (TextView) toolpop1.findViewById(R.id.size_plus);
        fontSmall = (TextView) toolpop1.findViewById(R.id.size_decrease);
        fontBig.setTypeface(typeface);
        fontSmall.setTypeface(typeface);
        fontsize = sp.getInt("size", defaultFontSize);
        seekBar1.setProgress(fontsize - minFontSize);
        seekBar1.setOnSeekBarChangeListener(this);
    }

    /**
     * 点击亮度
     */
    private void clickLight(){
        mToolpop2.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        if(CommonUtil.getBottomStatusHeight(mContext)!=0) {
            int popofset = 120 +CommonUtil.getBottomStatusHeight(mContext);
            mToolpop2.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, popofset);
        }else
            mToolpop2.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 120);
        seekBar2 = (SeekBar) toolpop2.findViewById(R.id.seekBar_light);
        lightPlus = (TextView) toolpop2.findViewById(R.id.light_plus);
        linghtDecrease = (TextView) toolpop2.findViewById(R.id.light_decrease);
        lightPlus.setTypeface(typeface);
        linghtDecrease.setTypeface(typeface);
        getLight();
        seekBar2.setProgress(light);
        //System.out.print("亮度" + light);
        seekBar2.setOnSeekBarChangeListener(this);

    }

    /**
     * 点击书签
     */
    private void clickBookMarks(){
        mToolpop3.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        if(CommonUtil.getBottomStatusHeight(mContext)!=0) {
            int popofset = 120 + CommonUtil.getBottomStatusHeight(mContext);
            mToolpop3.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, popofset);
        }else
            mToolpop3.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 120);
        btn_mark_add = (TextView) toolpop3.findViewById(R.id.Btn_mark_add);
        btn_mark_my = (TextView) toolpop3.findViewById(R.id.Btn_mark_my);
        btn_mark_add.setTypeface(typeface);
        btn_mark_my.setTypeface(typeface);
        btn_mark_add.setOnClickListener(this);
        btn_mark_my.setOnClickListener(this);
    }

    /**
     * 点击跳转
     */
    private void clickJump(){
        mToolpop4.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        if(CommonUtil.getBottomStatusHeight(mContext)!=0) {
            int popofset = 120 +CommonUtil.getBottomStatusHeight(mContext);
            mToolpop4.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, popofset);
        }else
            mToolpop4.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 120);
        int bc = CommonUtil.getBottomStatusHeight(mContext);
        // Log.d("ReadActivity","虚拟功能键栏高度是"+bc);
        mToolpop4.showAtLocation(mPageWidget, Gravity.BOTTOM, 0, 500);
        jumpOk = (TextView) toolpop4.findViewById(R.id.jump_ok);
        jumpCancel = (TextView) toolpop4.findViewById(R.id.jump_cancel);
        seekBar4 = (SeekBar) toolpop4.findViewById(R.id.seekBar_jump);
        markEdit4 = (TextView) toolpop4.findViewById(R.id.markEdit4);
        jumpOk.setTypeface(typeface);
        jumpCancel.setTypeface(typeface);
        markEdit4.setTypeface(typeface);
        // begin = sp.getInt(bookPath + "begin", 1);
        float fPercent = (float) (begin * 1.0 / bookPageFactory.getM_mbBufLen());
        DecimalFormat df = new DecimalFormat("#0");
        String strPercent = df.format(fPercent * 100) + "%";
        markEdit4.setText(strPercent);
        seekBar4.setProgress(Integer.parseInt(df.format(fPercent * 100)));
        seekBar4.setOnSeekBarChangeListener(this);
        jumpOk.setOnClickListener(this);
        jumpCancel.setOnClickListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        switch (seekBar.getId()) {
            // 字体进度条
            case R.id.seekBar_size:
                fontsize = seekBar1.getProgress() + minFontSize;
                //Log.d("ReadActivity","size的大小"+fontsize);
                setSize(fontsize);
                bookPageFactory.setM_fontSize(fontsize);
                bookPageFactory.setM_mbBufBegin(begin);
                bookPageFactory.setM_mbBufEnd(begin);
                postInvalidateUI();
                break;
            // 亮度进度条
            case R.id.seekBar_light:
                light = seekBar2.getProgress();
                setLight();
                lp.screenBrightness = light / 10.0f < 0.01f ? 0.01f : light / 10.0f;
                getWindow().setAttributes(lp);
                break;
            // 跳转进度条
            case R.id.seekBar_jump:
                int s = seekBar4.getProgress();
                markEdit4.setText(s + "%");
                begin = (bookPageFactory.getM_mbBufLen() * s) / 100;
                editor.putInt(bookPath + "begin", begin).commit();
                bookPageFactory.setM_mbBufBegin(begin);
                bookPageFactory.setM_mbBufEnd(begin);
                //100%的位置不能作为起点
                try {
                    if (s == 100) {
                        bookPageFactory.prePage();
                        bookPageFactory.getM_mbBufBegin();
                        begin = bookPageFactory.getM_mbBufBegin();
                        editor.putInt(bookPath + "begin",begin).commit();
                        bookPageFactory.setM_mbBufBegin(begin);
                        //bookPageFactory.setM_mbBufBegin(begin);

                    }
                } catch (IOException e) {
                    Log.e(TAG, "onProgressChanged seekBar4-> IOException error", e);
                }
                postInvalidateUI();
                break;
        }

    }
    /**
     * 记录数据 并清空popupwindow
     */
    private void clear() {

        show = false;
        mPopupWindow.dismiss();
        popDismiss();
    }

    /**
     * 初始化SlidingMenu
     */
    private void initSliding(){
        dm = getResources().getDisplayMetrics();
        typeface = Typeface.createFromAsset(this.getAssets(),"font/QH.ttf");
        button_back = (ImageButton) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.bookname);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        setTabsValue();
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pagerSlidingTabStrip.setViewPager(viewPager);
        button_back.setOnClickListener(this);
        title.setText(bookName);
        title.setTypeface(typeface);

    }

    /**
     * 设置切换栏菜单文字样式
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        pagerSlidingTabStrip.setShouldExpand(true);//所有初始化要在setViewPager方法之前
        // 设置Tab的分割线是透明的
        pagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        pagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        pagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        pagerSlidingTabStrip.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        //设置Tab标题文字的字体
        pagerSlidingTabStrip.setTypeface(typeface,0);
        // 设置Tab Indicator的颜色
        pagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#45c01a"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        //    pagerSlidingTabStrip.setSelectedTextColor(Color.parseColor("#45c01a"));
        // 取消点击Tab时的背景色
        pagerSlidingTabStrip.setTabBackground(0);

        // pagerSlidingTabStrip.setDividerPadding(18);
    }
    public static String getBookPath() {
        return bookPath;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClose() {
        if (iv_filter.isShown())
            iv_filter.setVisibility(View.GONE);
    }

    @Override
    public void onOpen() {

    }

    @Override
    protected void onResume(){
        initSliding();
        super.onResume();
    }
}
