package com.yx.sreader.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yx.sreader.R;
import com.yx.sreader.activity.ReadActivity;
import com.yx.sreader.adapter.MarkAdapter;
import com.yx.sreader.database.BookMarks;
import com.yx.sreader.util.BookPageFactory;
import com.yx.sreader.util.CommonUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iss on 2018/4/4.
 */

public class BookMarkFragment extends Fragment implements AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener,View.OnClickListener{

    private ListView markListview;
    private List<BookMarks> bookMarksList;
    private String mArgument;
    public static final String ARGUMENT = "argument";
    private BookPageFactory bookPageFactory;
    private int itemPosition;
    private PopupWindow deleteMarkPop;
    private View delateMarkPopView;
    private static int begin;
    private String bookpath;

    public BookMarkFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark,container,false);
        markListview = (ListView) view.findViewById(R.id.marklistview);
        initDeleteMarkPop();
        markListview.setOnItemClickListener(this);
        markListview.setOnItemLongClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString(ARGUMENT);
        }
        bookMarksList = new ArrayList<>();
        bookMarksList = DataSupport.where("bookpath = ?",mArgument).find(BookMarks.class);
        MarkAdapter markAdapter = new MarkAdapter(getActivity(), bookMarksList);
        markListview.setAdapter(markAdapter);
        if(!bookMarksList.isEmpty()) {
            notifyDataRefresh();
        }
        return view;
    }

    /**
     * 用于从Activity传递数据到Fragment
     * @param argument
     * @return
             */
    public static BookMarkFragment newInstance(String argument)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        BookMarkFragment bookMarkFragment = new BookMarkFragment();
        bookMarkFragment.setArguments(bundle);

        return bookMarkFragment;
    }

    /**
     * 初始化浮动菜单
     */
    private void initDeleteMarkPop() {
        delateMarkPopView = getActivity().getLayoutInflater().inflate(R.layout.delete_mark_pop,null);
        delateMarkPopView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        float width = (float)CommonUtil.getScreenWidth(getContext());
        Log.v("屏幕宽度"," " + width);
        float widthDp = CommonUtil.convertPixelsToDp(getContext(),width);
        Log.v("屏幕宽度dp"," " + widthDp);
        int curSize = Math.round((int)width *CommonUtil.calcuPro(widthDp));
        Log.v("偏移宽度"," " + curSize);
        deleteMarkPop = new PopupWindow(delateMarkPopView, curSize ,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteMarkPop.setOutsideTouchable(true);
        deleteMarkPop.setAnimationStyle(R.style.popwin_anim_style);
    }

    /**
     * 显示删除浮动菜单
     * @param view
     * @param position
     */
    private void showDeleteMarkPop(View view,int position) {

        TextView deleteMark_TV = (TextView) delateMarkPopView.findViewById(R.id.delete_mark_tv);
        TextView deleteAllMark_TV = (TextView) delateMarkPopView.findViewById(R.id.delte_allmark_tv);
        deleteMark_TV.setOnClickListener(this);
        deleteAllMark_TV.setOnClickListener(this);
        int popHeight = deleteMarkPop.getContentView().getMeasuredHeight();//注意获取高度的方法
        deleteMarkPop.showAsDropDown(view, 0, -view.getHeight() - popHeight);
    }

    /**
     * 删除后重新从数据库获取数据
     */
    public void notifyDataRefresh () {
        bookMarksList = new ArrayList<>();
        bookMarksList = DataSupport.where("bookpath = ?", mArgument).find(BookMarks.class);
        MarkAdapter markAdapter = new MarkAdapter(getActivity(),bookMarksList);
        markListview.setAdapter(markAdapter);
        markAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(deleteMarkPop.isShowing()) {
            deleteMarkPop.dismiss();
        }else {
            begin = bookMarksList.get(position).getBegin();
            bookpath = bookMarksList.get(position).getBookpath();
            Intent intent = new Intent();
            intent.setClass(getActivity(), ReadActivity.class);
            intent.putExtra("begin", begin);
            intent.putExtra("bookpath", bookpath);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().overridePendingTransition(android.support.v7.appcompat.R.anim.abc_grow_fade_in_from_bottom, android.support.v7.appcompat.R.anim.abc_shrink_fade_out_from_bottom);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        itemPosition = position;
        if(bookMarksList.size() > position) {
            showDeleteMarkPop(view, position);
        }
        Log.d("bookmarkfragment","是否执行到这里");
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //删除单个书签
            case R.id.delete_mark_tv:
                int id = bookMarksList.get(itemPosition).getId();
                DataSupport.delete(BookMarks.class,id);
                notifyDataRefresh();
                // Log.d("bookmarkfragment","删除书签");
                deleteMarkPop.dismiss();
                break;
            //删除全部书签
            case R.id.delte_allmark_tv:
                // Log.d("bookmarkfragment","清空书签");
                String bookpath = bookMarksList.get(itemPosition).getBookpath();
                DataSupport.deleteAll(BookMarks.class,"bookpath = ?",bookpath);
                notifyDataRefresh();
                deleteMarkPop.dismiss();
                break;
        }
    }

    @Override
    public void onStop(){
        if(!bookMarksList.isEmpty()) {
            notifyDataRefresh();
        }
        super.onStop();
    }

    @Override
    public void onPause(){
        if(!bookMarksList.isEmpty()) {
            notifyDataRefresh();
        }
        super.onPause();
    }

}
