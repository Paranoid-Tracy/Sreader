package com.yx.sreader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yx.sreader.activity.ReadActivity;
import com.yx.sreader.fragment.BookMarkFragment;
import com.yx.sreader.fragment.CatalogueFragment;

/**
 * Created by iss on 2018/4/3.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {

    private final String[] titles = { "目录", "书签"};

    CatalogueFragment catalogueFragment;
    BookMarkFragment bookMarkFragment;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (catalogueFragment == null) {
                    //  bookMarkFragment = new BookMarkFragment();
                    //创建bookMarkFragment实例时同时把需要intent中的值传入
                    catalogueFragment = CatalogueFragment.newInstance(ReadActivity.getBookPath());
                    // bookMarkFragment = BookMarkFragment.newInstance(MarkActivity.getBookpath_intent());
                }
                //catalogueFragment = new CatalogueFragment();
                return catalogueFragment;

            case 1:
                /*if (bookMarkFragment == null) {
                    //catalogueFragment = new CatalogueFragment();
                    //  catalogueFragment = CatalogueFragment.newInstance(MarkActivity.getBookpath_intent());
                    bookMarkFragment = BookMarkFragment.newInstance(MarkActivity.getBookpath_intent());
                }*/
                bookMarkFragment = new BookMarkFragment();
                return bookMarkFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
