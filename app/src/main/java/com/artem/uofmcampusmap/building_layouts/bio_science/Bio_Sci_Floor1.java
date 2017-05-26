package com.artem.uofmcampusmap.building_layouts.bio_science;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artem.uofmcampusmap.R;

/**
 * Created by Artem on 2017-05-23.
 */

public class Bio_Sci_Floor1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bio_sci_floor1, container, false);

        return view;
    }
}
