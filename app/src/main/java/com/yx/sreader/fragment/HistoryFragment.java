package com.yx.sreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.yx.sreader.OnRecItemClickListener;
import com.yx.sreader.R;
import com.yx.sreader.activity.BookDetailActivity;
import com.yx.sreader.activity.MainActivity;
import com.yx.sreader.activity.WelcomeActivity;
import com.yx.sreader.adapter.PagerAdapter;
import com.yx.sreader.adapter.RecommendAdapter;
import com.yx.sreader.bean.BookInfo;
import com.yx.sreader.service.WebService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by iss on 2018/4/7.
 */

public class HistoryFragment extends Fragment {
    private int position;
    private View view;
    private List<BookInfo> newList;
    private RecommendAdapter adapter;
    private RecyclerView rv;
    private BookInfo bookInfo;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String  dbname = "History";
    private static List<String> listbookinfo = WelcomeActivity.getListbookinfo()[1];
    private static boolean IsInNetwork = WelcomeActivity.isInNetwork();
    private static Handler handler = new Handler();
    private String info;
    private ReentrantLock lock = new ReentrantLock();

    public static HistoryFragment newInstance(){
        HistoryFragment pagerFragment = new HistoryFragment();
        return pagerFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pager, container, false);
        //init();
        initView();
        Log.v("位置遍历",""+position);
        return view;
    }

    private  void initView() {
        newList = new ArrayList<BookInfo>();
        adapter = new RecommendAdapter(getContext(),newList);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipeRefreshLayout.setRefreshing(true);
        if(isInNetwork()) {
            //lock.lock();
            addData();
            //lock.unlock();
            setAdapter();
        }
        //createThread();
        seeRefresh();
    }

    private void addData() {
        if (!(listbookinfo.size() == 0)) {
            for (int i = 0; i < listbookinfo.size() / 6; i++) {
                bookInfo = new BookInfo();
                bookInfo.setAuthor(((String) listbookinfo.get(0 + i * 6)).substring(9));
                bookInfo.setBookname(((String) listbookinfo.get(1 + i * 6)).substring(10));
                bookInfo.setBookpath(((String) listbookinfo.get(2 + i * 6)).substring(6));
                bookInfo.setBookimage("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=4154742693,3551412937&fm=27&gp=0.jpg");
                bookInfo.setBookintroduction(((String) listbookinfo.get(5 + i * 6)).substring(14));
                newList.add(0, bookInfo);
            }

        }
    }

    private void setAdapter(){
        adapter.add(newList);
        adapter.setOnItemClickListener(new OnRecItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getContext(), BookDetailActivity.class);
                i.putExtra("author", newList.get(position).getAuthor());
                i.putExtra("bookname", newList.get(position).getBookname());
                i.putExtra("bookpath", newList.get(position).getBookpath());
                i.putExtra("bookimage", newList.get(position).getBookimage());
                i.putExtra("bookintroduction", newList.get(position).getBookintroduction());
                startActivity(i);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void exchangeData(){
        Collections.reverse(newList);
    }

    public void seeRefresh() {
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        //监听刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isInNetwork()) {
                    exchangeData();
                    setAdapter();
                }

            }
        });
    }

    private void init(){
        new Thread(new MyThread()).start();
    }


    public static List<String> getListbookinfo() {
        return listbookinfo;
    }

    public static boolean isInNetwork(){
        return IsInNetwork;
    }


    public class MyThread implements Runnable {
        @Override
        public void run() {
            info = WebService.executeHttpGet(dbname);
            // info = WebServicePost.executeHttpPost(username.getText().toString(), password.getText().toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //System.out.printf("当前获取数据"+info);
                    if(!(info.equals("no"))) {
                        listbookinfo = stringToList(info);
                        IsInNetwork = true;
                    }
                }
            });
        }
    }

    private List<String> stringToList(String strs){
        String str[] = strs.split(",");
        return Arrays.asList(str);
    }
}
