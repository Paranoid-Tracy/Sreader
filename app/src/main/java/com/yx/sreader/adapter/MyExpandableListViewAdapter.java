package com.yx.sreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.yx.sreader.R;
import com.yx.sreader.database.BookDownload;
import com.yx.sreader.Callback;

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
    private SwipeLayout swipeLayout;
    private Callback callback;


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
            iv_group_icon.setImageResource(R.drawable.expand1);
        }else {
            iv_group_icon.setImageResource(R.drawable.expand);
        }

        return view;

    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_elv_child,null);

        ImageView iv_child_icon = (ImageView) view.findViewById(R.id.iv_child_icon);
        TextView tv_child_info = (TextView) view.findViewById(R.id.tv_child_info);
        TextView tv_child_name = (TextView) view.findViewById(R.id.tv_child_name);
        LinearLayout llEdit = (LinearLayout) view.findViewById(R.id.llEdit);
        swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right,swipeLayout.findViewWithTag("Bottom2"));
        tv_child_name.setText(bookDownloads.get(childPosition).getBookname());
        Glide.with(context).load(bookDownloads.get(childPosition).getBookimage()).into(iv_child_icon);

        llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.click(bookDownloads.get(childPosition).getBookname());

            }
        });
        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                swipeLayout.close(true);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                swipeLayout = layout;
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }
}
