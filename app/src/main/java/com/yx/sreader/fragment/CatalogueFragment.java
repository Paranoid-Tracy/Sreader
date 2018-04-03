package com.yx.sreader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yx.sreader.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by iss on 2018/4/3.
 */

public class CatalogueFragment extends Fragment {
    private ListView catalogueListView;
    private String mArgument;
    public static final String ARGUMENT = "argument";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        catalogueListView = (ListView)view.findViewById(R.id.catalogue);
        //catalogueListView.setOnItemClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString(ARGUMENT);
        }
        /*bookCatalogueList = new ArrayList<>();
        bookCatalogueList = DataSupport.where("bookpath = ?", mArgument).find(BookCatalogue.class);
        CatalogueAdapter catalogueAdapter = new CatalogueAdapter(getActivity(),bookCatalogueList);
        catalogueListView.setAdapter(catalogueAdapter);*/
        return view;
    }
    public static CatalogueFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        CatalogueFragment catalogueFragment = new CatalogueFragment();
        catalogueFragment.setArguments(bundle);
        return  catalogueFragment;
    }
}
