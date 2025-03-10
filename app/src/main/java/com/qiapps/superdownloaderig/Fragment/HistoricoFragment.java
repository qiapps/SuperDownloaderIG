package com.qiapps.superdownloaderig.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qiapps.qiads.QINativeAds;
import com.qiapps.qiads.QIUtils;

import java.util.ArrayList;

import com.qiapps.superdownloaderig.Activity.CustomApplication;
import com.qiapps.superdownloaderig.Activity.MainActivity;
import com.qiapps.superdownloaderig.Adapter.LineAdapter;
import com.qiapps.superdownloaderig.Helper.UserPreferences;
import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricoFragment extends Fragment {

    private View view;
    private MainActivity activity;
    private RecyclerView mRecyclerView;
    private LineAdapter adapter;
    private ProgressBar progressBar;
    private TextView empty;


    public HistoricoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_historico, container, false);
        activity = (MainActivity)getActivity();
        mRecyclerView = view.findViewById(R.id.list);
        progressBar = view.findViewById(R.id.progress);
        empty = view.findViewById(R.id.txt_empty);
        hideEmpty();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        setupRecycler(activity.getInstagramResources(), activity);
    }

    public void setupRecycler(ArrayList<InstagramResource>resources, MainActivity mainActivity){
        try {
            if (resources.size() > 0) {
                adapter = new LineAdapter(resources, mainActivity);
                mRecyclerView.setAdapter(adapter);
                hideEmpty();
            } else {
                mRecyclerView.setVisibility(View.GONE);
                showEmpty();
            }
        }catch (Exception e){

        }

    }

    private void showEmpty(){
        empty.setVisibility(View.VISIBLE);
    }

    private void hideEmpty(){
        empty.setVisibility(View.GONE);
    }

    public void onItemDeleted(InstagramResource ir){
        if(adapter != null) {
            if(ir != null){
                adapter.deleteItem(ir);
            }
        }
    }

}
