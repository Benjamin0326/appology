package com.appology.mannercash.mannercash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jeong on 2015-06-25.
 */
public class PointSearchFragment extends Fragment {

    public PointSearchFragment newInstance() {
        PointSearchFragment fragment = new PointSearchFragment();
        return fragment;
    }

    public PointSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_point_search, container, false);

        return view;
    }
}
