package com.yx.sreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yx.sreader.Callback;
import com.yx.sreader.R;
import com.yx.sreader.adapter.MyExpandableListViewAdapter;
import com.yx.sreader.database.BookDownload;

import org.litepal.crud.DataSupport;

/**
 * Created by iss on 2018/4/10.
 */

public class DownLoadFragment extends Fragment implements Callback {
    private View view;
    private ExpandableListView expandableListView;
    private TextView textView;
    public  MyExpandableListViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download, container, false);
        expandableListView = (ExpandableListView)view.findViewById(R.id.elv_ing);
        textView = (TextView)view.findViewById(R.id.downloading);
        textView.setText("我的下载");
        adapter = new MyExpandableListViewAdapter(getContext());

        expandableListView.setAdapter(adapter);
        adapter.setCallback(this);
        //init();
        //initView();
        return view;
    }

    @Override
    public void click(String db) {
        DataSupport.deleteAll(BookDownload.class,"bookname=?",db);
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(),"该项已移除",Toast.LENGTH_SHORT).show();
    }
}
