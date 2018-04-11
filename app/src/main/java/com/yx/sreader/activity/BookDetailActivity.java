package com.yx.sreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yx.sreader.R;
import com.yx.sreader.util.CornersTransform;
import com.yx.sreader.util.DownloadUtil;
import com.yx.sreader.view.FlikerProgressBar;

import java.io.File;
import java.io.IOException;

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
        bookname = (TextView)this.findViewById(R.id.tvBookListTitle);
        bookintro = (TextView)this.findViewById(R.id.tvlongIntro);
        author = (TextView)this.findViewById(R.id.tvBookListAuthor);
        bookimage = (ImageView)this.findViewById(R.id.ivBookCover);
        flikerProgressBar = (FlikerProgressBar)this.findViewById(R.id.round_bar);
        flikerProgressBar.setStart(true);
        flikerProgressBar.setOnClickListener(this);
        data = getIntent();
        bookname.setText(data.getStringExtra("bookname"));
        bookintro.setOnClickListener(this);
        bookintro.setText(data.getStringExtra("bookintroduction"));
        author.setText(data.getStringExtra("author"));
        Glide.with(this).load(data.getStringExtra("bookimage")).transform(new CornersTransform(this,50)).into(bookimage);
        path = data.getStringExtra("bookpath");

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
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tvlongIntro:
                if (LongIntro) {
                    bookintro.setMaxLines(20);
                    LongIntro = false;
                } else {
                    bookintro.setMaxLines(4);
                    LongIntro = true;
                }
            case R.id.round_bar:
                if(flikerProgressBar.isStart()){
                    flikerProgressBar.setStart(false);
                    download();
                }
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
}
