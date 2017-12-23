package se.olander.android.copsandrobbers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import se.olander.android.copsandrobbers.R;
import se.olander.android.copsandrobbers.models.GameEngine;
import se.olander.android.copsandrobbers.models.Level;
import se.olander.android.copsandrobbers.views.GraphView;


public class LevelFragment extends Fragment implements GameEngine.OnGameEventHandler {

    private static final String LEVEL_KEY = "LEVEL_KEY";

    private Level level;
    private GraphView graphView;
    private GameEngine gameEngine;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        level = (Level) getArguments().getSerializable(LEVEL_KEY);
        gameEngine = new GameEngine(level);
        gameEngine.setOnGameEventHandler(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_level, container, false);
        graphView = view.findViewById(R.id.graph);
        graphView.setGraph(gameEngine.getGraph());
        graphView.setOnNodeClickListener(gameEngine);

        Button relayout = view.findViewById(R.id.relayout);
        relayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                graphView.relayout();
            }
        });


        return view;
    }

    private void createDialog() {

    }

    private void moveCop(int from, int to) {
    }

    @Override
    public void victory() {
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, VictoryFragment.newInstance(level, gameEngine.getNumberOfTurns(), gameEngine.getTotalTime()))
            .commit();
    }

    public static LevelFragment newInstance(Level level) {
        LevelFragment fragment = new LevelFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(LevelFragment.LEVEL_KEY, level);
        fragment.setArguments(arguments);
        return fragment;
    }
}
