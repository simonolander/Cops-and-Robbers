package se.olander.android.copsandrobbers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.olander.android.copsandrobbers.R;


public class LevelFragment extends Fragment {

    public static final String LEVEL_RES_ID_KEY = "level";

    private int levelResId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        levelResId = getArguments().getInt(LEVEL_RES_ID_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(levelResId, container, false);
        return view;
    }
}
