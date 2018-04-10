package com.yx.sreader.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yx.sreader.OnRecItemClickListener;
import com.yx.sreader.R;
import com.yx.sreader.bean.BookInfo;
import com.yx.sreader.service.WebService;
import com.yx.sreader.util.CornersTransform;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by iss on 2018/4/8.
 */

public class RecommendAdapter extends RecyclerView.Adapter {
    private OnRecItemClickListener onRecItemClickListener;
    private List<BookInfo> lists;
    private Context mContext;


    public RecommendAdapter(Context context,List<BookInfo> lists){

        this.mContext = context;
        this.lists = lists;
    }

    public void add(List<BookInfo> newlist) {
        Collections.addAll(newlist);
    }

    //点击接口
    public void setOnItemClickListener(OnRecItemClickListener listener) {
        this.onRecItemClickListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new ViewHolder(view, onRecItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.bookname.setText(lists.get(position).getBookname());
        vh.bookintroduction.setText(lists.get(position).getBookintroduction());
        vh.author.setText(lists.get(position).getAuthor());
        //if (lists.get(position).getBookimage() == null) {
            Glide.with(mContext).load(lists.get(position).getBookimage()).transform(new CornersTransform(mContext,50)).into(vh.bookimage);
        //}

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnRecItemClickListener onRecItemClickListener;
        private TextView bookname;
        private TextView bookintroduction;
        private TextView author;
        private ImageView bookimage;
        public ViewHolder(View itemView,OnRecItemClickListener onRecItemClickListener) {
            super(itemView);
            bookname = (TextView)itemView.findViewById(R.id.tvRecommendTitle);
            bookintroduction = (TextView)itemView.findViewById(R.id.tvBookDetail);
            bookimage = (ImageView)itemView.findViewById(R.id.ivRecommendCover);
            author = (TextView)itemView.findViewById(R.id.tvBookAuthor) ;

            //设置点击事件
            this.onRecItemClickListener = onRecItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRecItemClickListener != null) {
                onRecItemClickListener.onClick(v, getLayoutPosition());
            }
        }
    }


}
