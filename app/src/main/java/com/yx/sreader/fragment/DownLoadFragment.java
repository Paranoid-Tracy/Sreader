package com.yx.sreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.yx.sreader.R;
import com.yx.sreader.adapter.MyExpandableListViewAdapter;

/**
 * Created by iss on 2018/4/10.
 */

public class DownLoadFragment extends Fragment {
    private View view;
    private ExpandableListView expandableListView;
    private TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download, container, false);
        expandableListView = (ExpandableListView)view.findViewById(R.id.elv_ing);
        textView = (TextView)view.findViewById(R.id.downloading);
        textView.setText("我的下载");

        MyExpandableListViewAdapter adapter = new MyExpandableListViewAdapter(getContext());

        expandableListView.setAdapter(adapter);
        //init();
        //initView();
        return view;
    }
}
