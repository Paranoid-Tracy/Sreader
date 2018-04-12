package com.yx.sreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yx.sreader.R;
import com.yx.sreader.database.BookDownload;
import com.yx.sreader.database.BookList;
import com.yx.sreader.view.FlikerProgressBar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iss on 2018/4/10.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] group = new String[]{"下载列表"};
    private List<BookDownload> bookDownloads;
    private String[][] childs= new String[][]{{ "习大大","李克强","普京", "金正恩", "安倍晋三"},
            {"刘铁男","万庆良","周永康", "徐才厚", "谷俊山", "令计划","郭伯雄","苏荣","陈水扁","蒋洁敏","李东生","白恩培" },
            { "马云", "麻花藤", "李彦宏", "周鸿祎","雷布斯","库克" },
            {"李冰冰","范冰冰","李小璐","杨颖","周冬雨","Lady GaGa","千颂伊","尹恩惠"}};



    /**
     * 构造,因为布局填充器填充布局需要使用到Context,通过构造来传递
     * */
    public MyExpandableListViewAdapter (Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getGroupCount() {
        return group.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        bookDownloads = new ArrayList<>();
        bookDownloads = DataSupport.findAll(BookDownload.class);
        return bookDownloads.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return group[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return bookDownloads.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_elv_group,null);

        ImageView iv_group_icon = (ImageView) view.findViewById(R.id.iv_group_icon);
        TextView tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
        TextView tv_group_number = (TextView) view.findViewById(R.id.tv_group_number);

        tv_group_name.setText(group[groupPosition]);
        //tv_group_number.setText(childs[groupPosition].length+"/"+childs[groupPosition].length);

        /*isExpanded 子列表是否展开*/
        if(isExpanded){
            iv_group_icon.setImageResource(R.drawable.return_button);
        }else {
            iv_group_icon.setImageResource(R.drawable.return_button);
        }

        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_elv_child,null);

        ImageView iv_child_icon = (ImageView) view.findViewById(R.id.iv_child_icon);
        TextView tv_child_info = (TextView) view.findViewById(R.id.tv_child_info);
        TextView tv_child_name = (TextView) view.findViewById(R.id.tv_child_name);
        tv_child_name.setText(bookDownloads.get(childPosition).getBookname());
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
