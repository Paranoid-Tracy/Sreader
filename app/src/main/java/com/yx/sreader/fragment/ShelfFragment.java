package com.yx.sreader.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.appcompat.R.anim;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;


import com.yx.sreader.R;
import com.yx.sreader.activity.MainActivity;
import com.yx.sreader.activity.ReadActivity;
import com.yx.sreader.adapter.ShelfAdapter;
import com.yx.sreader.database.BookList;
import com.yx.sreader.view.DragGridView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
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
    private int bookViewPosition;
    private int itemPosition;



    public ShelfFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shelf, container, false);
        bookShelf = (DragGridView) view.findViewById(R.id.bookShelf);
        bookLists = new ArrayList<>();
        bookLists = DataSupport.findAll(BookList.class);
        adapter = new ShelfAdapter(getContext(), bookLists);
        bookShelf.setAdapter(adapter);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("我的书架");
        bookShelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!(bookLists.size()==0)) {
                    if(!bookLists.get(position).getBookpath().startsWith("http")) {
                        setBookViewPosition(position);
                        adapter.setItemToFirst(position);
                        String bookpath = bookLists.get(position).getBookpath();
                        String bookname = bookLists.get(position).getBookname();
                        Intent intent = new Intent();
                        intent.setClass(getContext(), ReadActivity.class);
                        intent.putExtra("bookpath", bookpath);
                        intent.putExtra("bookname", bookname);
                        startActivity(intent);
                        getActivity().overridePendingTransition(anim.abc_grow_fade_in_from_bottom, anim.abc_shrink_fade_out_from_bottom);
                    } else {
                        Toast.makeText(getContext(),"请下载后浏览",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        /*view.setFocusable(true);//这个和下面的这个命令必须要设置了，才能监听back事件。
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(backListener);*/

        return view;
    }
    public void setBookViewPosition(int position) {
        this.bookViewPosition = position;
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
    /*private View.OnKeyListener backListener= new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(event.getAction() == KeyEvent.ACTION_DOWN){
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(DragGridView.getShowDeleteButton()) {
                        DragGridView.setIsShowDeleteButton(false);
                        //要保证是同一个adapter对象,否则在Restart后无法notifyDataSetChanged
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            return true;
        }
    };*/

    public void onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
            if(DragGridView.getShowDeleteButton()) {
                DragGridView.setIsShowDeleteButton(false);
                //要保证是同一个adapter对象,否则在Restart后无法notifyDataSetChanged
                adapter.notifyDataSetChanged();
            }
        }

    }



}