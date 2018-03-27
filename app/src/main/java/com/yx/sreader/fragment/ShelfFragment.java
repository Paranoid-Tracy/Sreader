package com.yx.sreader.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yx.sreader.R;
import com.yx.sreader.adapter.ShelfAdapter;
import com.yx.sreader.database.BookList;
import com.yx.sreader.view.DragGridView;

import java.util.List;

/**
 * Created by iss on 2018/3/26.
 */

public class ShelfFragment extends Fragment {
    private View view;
    private ShelfAdapter adapter;
    private List<BookList> bookLists;
    private DragGridView bookShelf;
    public ShelfFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                              Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shelf,container,false);
        bookShelf = (DragGridView) view.findViewById(R.id.bookShelf);
        adapter = new ShelfAdapter(getContext(),bookLists);
        bookShelf.setAdapter(adapter);
        return view;
    }}