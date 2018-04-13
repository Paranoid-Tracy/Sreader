package com.yx.sreader.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.yx.sreader.fragment.BiographyFragment;
import com.yx.sreader.fragment.HistoryFragment;
import com.yx.sreader.fragment.InspirationalFragment;
import com.yx.sreader.fragment.PagerFragment;
import com.yx.sreader.fragment.PhilosophyFragment;
import com.yx.sreader.fragment.ScienceFragment;
import com.yx.sreader.fragment.YouthFragment;
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
        Log.v("调用位置",""+position);
        switch (position){
            case 0:
                return PagerFragment.newInstance();
            case 1:
                return HistoryFragment.newInstance();

            case 2:
                return YouthFragment.newInstance();

            case 3:
                return InspirationalFragment.newInstance();

            case 4:
                return BiographyFragment.newInstance();

            case 5:
                return ScienceFragment.newInstance();
            
            default:
                return PhilosophyFragment.newInstance();


        }
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
