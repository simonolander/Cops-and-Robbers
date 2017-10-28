package se.olander.android.copsandrobbers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.olander.android.copsandrobbers.R;
import se.olander.android.copsandrobbers.models.Level;
import se.olander.android.copsandrobbers.views.GraphLayout;


public class LevelFragment extends Fragment {

    public static final String LEVEL_KEY = "level";

    private Level level;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        level = (Level) getArguments().getSerializable(LEVEL_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_level, container, false);
        GraphLayout graphView = view.findViewById(R.id.graph);
        graphView.setLevel(level);
        return view;
    }
}
