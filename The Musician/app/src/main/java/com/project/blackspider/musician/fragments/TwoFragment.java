package com.project.blackspider.musician.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.blackspider.musician.R;
import com.project.blackspider.musician.adapters.AudioAdapter;
import com.project.blackspider.musician.interfaces.AudioAdapterListener;
import com.project.blackspider.musician.view.CustomRecycler.IndexFastScrollRecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class TwoFragment extends Fragment {
    public static IndexFastScrollRecyclerView mRecyclerView;
    public static AudioAdapter mAdapter;
    public static PopupMenu popupMenu;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two, container, false);
    }
}