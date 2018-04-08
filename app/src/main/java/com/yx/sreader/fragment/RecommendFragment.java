package com.yx.sreader.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yx.sreader.R;
import com.yx.sreader.adapter.PagerAdapter;


/**
 * Created by iss on 2018/3/27.
 */

public class RecommendFragment extends Fragment {
    private View view;
    private PagerAdapter adapter;

    public RecommendFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend, container, false);
        initView(view);
        return view;
    }

    public void initView(View rootView) {
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        //关键的一个知识点getChidFragmentManager
        adapter = new PagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(7);

        //TabLayout
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //显示当前那个标签页
//        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
    }

}
