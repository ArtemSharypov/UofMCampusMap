package com.artem.uofmcampusmap.buildings.human_ecology;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.artem.uofmcampusmap.R;

/**
 * Created by Artem on 2017-09-14.
 */

public class Human_Ecology_Floor1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_human_eco_floor1, container, false);

        return view;
    }
}
