package com.yx.sreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yx.sreader.R;
import com.yx.sreader.util.CornersTransform;

/**
 * Created by iss on 2018/4/8.
 */

public class BookDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView bookname;
    private TextView bookintro;
    private TextView author;
    private ImageView bookimage;
    private Intent data;
    private boolean LongIntro = true;
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
        data = getIntent();
        bookname.setText(data.getStringExtra("bookname"));
        bookintro.setOnClickListener(this);
        bookintro.setText(data.getStringExtra("bookintroduction"));
        author.setText(data.getStringExtra("author"));
        Glide.with(this).load(data.getStringExtra("bookimage")).transform(new CornersTransform(this,50)).into(bookimage);

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

        }
    }
}
