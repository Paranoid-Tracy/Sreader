package com.yx.sreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yx.sreader.R;


/**
 * Created by iss on 2018/3/27.
 */

public class RecommendFragment extends Fragment {
    private View view;

    public RecommendFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend, container, false);
        return view;
    }

}
