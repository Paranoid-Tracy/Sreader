package com.yx.sreader.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yx.sreader.fragment.PagerFragment;
import com.yx.sreader.util.CommonUtil;

/**
 * Created by iss on 2018/4/7.
 */

public class PagerAdapter extends FragmentPagerAdapter{
    private String[] titles = {"文学","历史","青春","励志","传记","科学","哲学"};
    public int COUNT = titles.length;
    private Context mContext;

    public PagerAdapter(FragmentManager fm , Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        return PagerFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
