package com.yx.sreader.fragment;

import android.content.Context;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mark,container,false);
        markListview = (ListView) view.findViewById(R.id.marklistview);
        initDeleteMarkPop();
        //markListview.setOnItemClickListener(this);
        markListview.setOnItemLongClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString(ARGUMENT);
        }
        bookMarksList = new ArrayList<>();
        bookMarksList = DataSupport.where("bookpath = ?",mArgument).find(BookMarks.class);
        MarkAdapter markAdapter = new MarkAdapter(getActivity(), bookMarksList);
        markListview.setAdapter(markAdapter);
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    }
}
