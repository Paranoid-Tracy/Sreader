package com.yx.sreader.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.yx.sreader.R;
import com.yx.sreader.activity.MainActivity;
import com.yx.sreader.adapter.ShelfAdapter;
import com.yx.sreader.database.BookList;
import com.yx.sreader.view.DragGridView;

import java.util.List;

import static com.yx.sreader.R.string.my_shelf;

/**
 * Created by iss on 2018/3/26.
 */

public class ShelfFragment extends Fragment {
    private View view;
    private ShelfAdapter adapter;
    private List<BookList> bookLists;
    private DragGridView bookShelf;

    public ShelfFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shelf, container, false);
        bookShelf = (DragGridView) view.findViewById(R.id.bookShelf);
        adapter = new ShelfAdapter(getContext(), bookLists);
        bookShelf.setAdapter(adapter);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("我的书架");

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(),com.yx.sreader.activity.FileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}