package com.yx.sreader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yx.sreader.R;
import com.yx.sreader.activity.ReadActivity;
import com.yx.sreader.adapter.CatalogueAdapter;
import com.yx.sreader.database.BookCatalogue;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iss on 2018/4/3.
 */

public class CatalogueFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView catalogueListView;
    private String mArgument;
    public static final String ARGUMENT = "argument";
    private List<BookCatalogue> bookCatalogueList;
    private int catalogueStartPos;
    private String bookPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        catalogueListView = (ListView)view.findViewById(R.id.catalogue);
        catalogueListView.setOnItemClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString(ARGUMENT);
        }
        bookCatalogueList = new ArrayList<>();
        bookCatalogueList = DataSupport.where("bookpath = ?", mArgument).find(BookCatalogue.class);
        CatalogueAdapter catalogueAdapter = new CatalogueAdapter(getActivity(),bookCatalogueList);
        catalogueListView.setAdapter(catalogueAdapter);
        return view;
    }
    public static CatalogueFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        CatalogueFragment catalogueFragment = new CatalogueFragment();
        catalogueFragment.setArguments(bundle);
        return  catalogueFragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        catalogueStartPos = bookCatalogueList.get(position).getBookCatalogueStartPos();
        bookPath = bookCatalogueList.get(position).getBookpath();
        Intent intent = new Intent();
        intent.setClass(getActivity(), ReadActivity.class);
        intent.putExtra("begin", catalogueStartPos);
        intent.putExtra("bookpath", bookPath);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(android.support.v7.appcompat.R.anim.abc_grow_fade_in_from_bottom, android.support.v7.appcompat.R.anim.abc_shrink_fade_out_from_bottom);
    }
}
