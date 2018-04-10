package com.yx.sreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.yx.sreader.OnRecItemClickListener;
import com.yx.sreader.R;
import com.yx.sreader.activity.BookDetailActivity;
import com.yx.sreader.adapter.PagerAdapter;
import com.yx.sreader.adapter.RecommendAdapter;
import com.yx.sreader.bean.BookInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iss on 2018/4/7.
 */

public class PagerFragment extends Fragment {
    private static int position;
    private View view;
    private List<BookInfo> newList;
    private RecommendAdapter adapter;
    private RecyclerView rv;
    private BookInfo bookInfo;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static PagerFragment newInstance(int pos){
        PagerFragment pagerFragment = new PagerFragment();
        position = pos;
        return pagerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pager, container, false);
        initView();
        return view;
    }

    private void initView() {
        newList = new ArrayList<BookInfo>();
        adapter = new RecommendAdapter(getContext(),newList);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setRefreshing(true);
        addData();
        //createThread();
        //seeRefresh();
    }

    private void addData(){
        for(int i =0; i < 10; i++ ) {
            bookInfo = new BookInfo();
            bookInfo.setBookname("当前书名");
            bookInfo.setBookintroduction("这本书啦啦啦啦啦啦啦啦啦啦啦阿拉啦，好啊啊啊啊啊好 啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊" +
                    "啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊");
            bookInfo.setAuthor("张飞");
            bookInfo.setBookimage("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=4154742693,3551412937&fm=27&gp=0.jpg");
            newList.add(0, bookInfo);
        }
        adapter.add(newList);
        adapter.setOnItemClickListener(new OnRecItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getContext(), BookDetailActivity.class);
                i.putExtra("bookname",newList.get(position).getBookname());
                i.putExtra("bookintroduction",newList.get(position).getBookintroduction());
                i.putExtra("author",newList.get(position).getAuthor());
                i.putExtra("bookpath",newList.get(position).getBookpath());
                i.putExtra("bookimage",newList.get(position).getBookimage());
                startActivity(i);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

}
