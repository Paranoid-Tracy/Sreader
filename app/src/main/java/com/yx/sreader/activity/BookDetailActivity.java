package com.yx.sreader.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yx.sreader.R;
import com.yx.sreader.database.BookDownload;
import com.yx.sreader.database.BookList;
import com.yx.sreader.util.CornersTransform;
import com.yx.sreader.util.DownloadUtil;
import com.yx.sreader.util.FileUtil;
import com.yx.sreader.view.DrawableCenterButton;
import com.yx.sreader.view.FlikerProgressBar;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by iss on 2018/4/8.
 */

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView bookname;
    private TextView bookintro;
    private TextView author;
    private ImageView bookimage;
    private FlikerProgressBar flikerProgressBar;
    private Intent data;
    private String path;
    private boolean LongIntro = true;
    private DrawableCenterButton btnJoinCollection;
    protected List<AsyncTask<Void, Void, Boolean>> myAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();
    private String bookName;
    private String imageurl;
    //Thread downLoadThread;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_book_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar_book_detail);
        toolbar.setTitle("书籍详情");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        btnJoinCollection = (DrawableCenterButton)this.findViewById(R.id.btnJoinCollection);
        btnJoinCollection.setMovementMethod(LinkMovementMethod.getInstance());
        btnJoinCollection.setClickable(true);
        bookname = (TextView)this.findViewById(R.id.tvBookListTitle);
        bookintro = (TextView)this.findViewById(R.id.tvlongIntro);
        author = (TextView)this.findViewById(R.id.tvBookListAuthor);
        bookimage = (ImageView)this.findViewById(R.id.ivBookCover);
        flikerProgressBar = (FlikerProgressBar)this.findViewById(R.id.round_bar);
        flikerProgressBar.setStart(true);
        flikerProgressBar.setOnClickListener(this);
        btnJoinCollection.setOnClickListener(this);
        //btnJoinCollection.setOnTouchListener(this);

        data = getIntent();
        path = data.getStringExtra("bookpath");
        bookName = data.getStringExtra("bookname");
        imageurl = data.getStringExtra("bookimage");
        bookname.setText(bookName);
        bookintro.setOnClickListener(this);
        bookintro.setText(data.getStringExtra("bookintroduction"));
        author.setText(data.getStringExtra("author"));
        Glide.with(this).load(imageurl).transform(new CornersTransform(this,50)).into(bookimage);


    }


    private void download() {
        String url = "http://172.23.0.168:8080//BookInfo/%E9%A3%9E%E5%A4%A9.txt";

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "=============onFailure===============");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", "=============onResponse===============");
                Log.e("TAG", "request headers:" + response.request().headers());
                Log.e("TAG", "response headers:" + response.headers());
                ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressStart(long totalBytes) {
                        super.onUIProgressStart(totalBytes);
                        Log.e("TAG", "onUIProgressStart:" + totalBytes);
                        Toast.makeText(getApplicationContext(), "开始下载：" + totalBytes, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                        Log.e("TAG", "=============start===============");
                        Log.e("TAG", "numBytes:" + numBytes);
                        Log.e("TAG", "totalBytes:" + totalBytes);
                        Log.e("TAG", "percent:" + percent);
                        Log.e("TAG", "speed:" + speed);
                        Log.e("TAG", "============= end ===============");
                        flikerProgressBar.setProgress((int) (100 * percent));
                        //roundProgressbar.setProgress(msg.arg1);

                        //downloadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + " MB/秒");
                    }

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressFinish() {
                        super.onUIProgressFinish();
                        flikerProgressBar.finishLoad();
                        Log.e("TAG", "onUIProgressFinish:");
                        Toast.makeText(getApplicationContext(), "结束下载", Toast.LENGTH_SHORT).show();
                    }
                });

                BufferedSource source = responseBody.source();

                File outFile = new File("sdcard/temp1.txt");
                outFile.delete();
                outFile.getParentFile().mkdirs();
                outFile.createNewFile();

                BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                source.readAll(sink);
                sink.flush();
                source.close();
            }
        });

    }

    public void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
        myAsyncTasks.add(asyncTask.execute());
    }

    public void saveBooktoSqlite (final String bookName,final String key,final String imagepath,final BookList bookList ) {

        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                //可以进行界面上的初始化操作
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    String sql = "SELECT id FROM booklist WHERE bookname =? and bookpath =? and image =?";
                    Cursor cursor = DataSupport.findBySQL(sql, bookName, key,imagepath);
                    if (!cursor.moveToFirst()) { //This method will return false if the cursor is empty
                        bookList.save();
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //  return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                } else {
                    Toast.makeText(getApplicationContext(), bookName+"已在书架了", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnJoinCollection:
                BookList bookList = new BookList();
                //File file = new File(path);
                //String bookName = FileUtil.getFileNameNoEx(file.getName());
                bookList.setBookname(data.getStringExtra("bookname"));
                bookList.setImage(data.getStringExtra("bookimage"));
                bookList.setBookpath(path);
                saveBooktoSqlite(bookName, path,imageurl, bookList);//开启线程存储书到数据库
                Log.v("传入路径",path);
                break;

            case R.id.tvlongIntro:
                if (LongIntro) {
                    bookintro.setMaxLines(20);
                    LongIntro = false;
                } else {
                    bookintro.setMaxLines(4);
                    LongIntro = true;
                }
                break;
            case R.id.round_bar:
                if(flikerProgressBar.isStart()){
                    flikerProgressBar.setStart(false);
                    download();
                    BookDownload bookDownload = new BookDownload();
                    bookDownload.setBookname(bookName);
                    bookDownload.setBookimage(imageurl);
                    bookDownload.setBookpath(path);
                    bookDownload.setAuthor(data.getStringExtra("author"));
                    saveBooktoSqlite1(bookName,path,imageurl,bookDownload);

                }
                break;
                /*else if(!flikerProgressBar.isFinish()){
                    flikerProgressBar.toggle();
                    flikerProgressBar.toggle();

                    if(flikerProgressBar.isStop()){
                    } else {
                        //downLoad();
                    }

                }*/

        }
    }

    public void saveBooktoSqlite1 (final String bookName,final String key,final String imagepath,final BookDownload bookDownload ) {

        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                //可以进行界面上的初始化操作
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    String sql = "SELECT id FROM bookdownload WHERE bookname =? and bookpath =? and image =?";
                    Cursor cursor = DataSupport.findBySQL(sql, bookName, key,imagepath);
                    if (!cursor.moveToFirst()) { //This method will return false if the cursor is empty
                        bookDownload.save();
                        Log.v("是否写入",bookDownload.getBookname());
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                } else {
                    //Toast.makeText(getApplicationContext(), bookName+"已在书架了", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /*@Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.btnJoinCollection:
                BookList bookList = new BookList();
                File file = new File(path);
                String bookName = FileUtil.getFileNameNoEx(file.getName());
                bookList.setBookname(data.getStringExtra("bookname"));
                bookList.setImage(data.getStringExtra("bookimage"));
                bookList.setBookpath(path);
                saveBooktoSqlite(bookName, path,imageurl, bookList);//开启线程存储书到数据库
                break;
        }
        return false;
    }*/
}
