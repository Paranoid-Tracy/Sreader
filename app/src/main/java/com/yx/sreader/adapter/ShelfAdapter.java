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
       return 10;
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
