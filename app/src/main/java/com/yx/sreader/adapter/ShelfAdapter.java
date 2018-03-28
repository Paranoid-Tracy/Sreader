package com.yx.sreader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yx.sreader.DragGridListener;
import com.yx.sreader.R;
import com.yx.sreader.database.BookList;
import com.yx.sreader.view.DragGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iss on 2018/3/26.
 */

public class ShelfAdapter extends BaseAdapter implements DragGridListener {

    private Context mContex;
    private List<BookList> bilist;
    private static LayoutInflater inflater = null;
    private String booKpath,bookname;
    private int mHidePosition = -1;
    private Typeface typeface;
    protected List<AsyncTask<Void, Void, Boolean>> myAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();
    private int[] firstLocation;

    public ShelfAdapter (Context context, List<BookList> bilist){
        this.mContex = context;
        this.bilist = bilist;
        //typeface = Typeface.createFromAsset(mContex.getAssets(),"font/QH.ttf");
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(bilist.size()<10){
            return 10; //背景书架的draw需要用到item的高度
        }else{

            return bilist.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return bilist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.item_shelf, null);
            viewHolder = new ViewHolder();
            viewHolder.view = (TextView) contentView.findViewById(R.id.imageView1);
            viewHolder.view.setTypeface(typeface);
            viewHolder.deleteItem_IB = (ImageButton) contentView.findViewById(R.id.item_close_Im);
            contentView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) contentView.getTag();
        }
        if (bilist.size() == 0) {
            //   viewHolder.view.setBackgroundResource(R.drawable.cover_default_new);
            viewHolder.view.setClickable(false);
            viewHolder.view.setVisibility(View.INVISIBLE);
            viewHolder.deleteItem_IB.setVisibility(View.INVISIBLE);
        } else {
            if (bilist.size() > position) {

                //   viewHolder.view.setBackgroundResource(R.drawable.cover_default_new);
                final String fileName = bilist.get(position).getBookname();
                final String filePath = bilist.get(position).getBookpath();
                viewHolder.view.setText(fileName);

                if (DragGridView.getShowDeleteButton()) {
                    viewHolder.deleteItem_IB.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.deleteItem_IB.setVisibility(View.INVISIBLE);
                }
                bookname = fileName;
                booKpath = filePath;

                if (position == mHidePosition) {
                    contentView.setVisibility(View.INVISIBLE);
                } else {
                    contentView.setVisibility(View.VISIBLE);//DragGridView  解决复用问题

                }

            } else {
                //   viewHolder.view.setBackgroundResource(R.drawable.cover_default_new);
                viewHolder.view.setClickable(false);
                viewHolder.deleteItem_IB.setClickable(false);
                viewHolder.view.setVisibility(View.GONE);
                viewHolder.deleteItem_IB.setVisibility(View.GONE);

            }
        }

        return contentView;
    }
    class ViewHolder {
        ImageButton deleteItem_IB;
        TextView view;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {

    }

    @Override
    public void setHideItem(int hidePosition) {

    }

    @Override
    public void removeItem(int deletePosition) {

    }

    @Override
    public void setItemToFirst(int openPosition) {

    }

    @Override
    public void notifyDataRefresh() {

    }
}
