package com.yx.sreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yx.sreader.R;

/**
 * Created by iss on 2018/4/4.
 */

public class BookMarkFragment extends Fragment {

    private ListView markListview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark,container,false);
        markListview = (ListView) view.findViewById(R.id.marklistview);
        return view;
    }
}
