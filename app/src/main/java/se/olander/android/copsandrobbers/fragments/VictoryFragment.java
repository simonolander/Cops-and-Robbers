package se.olander.android.copsandrobbers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import se.olander.android.copsandrobbers.R;
import se.olander.android.copsandrobbers.models.GameEngine;
import se.olander.android.copsandrobbers.models.Level;


public class VictoryFragment extends Fragment {

    private static final String TAG = VictoryFragment.class.getSimpleName();
    private static final String LEVEL_KEY = "LEVEL_KEY";
    private static final String NUMBER_OF_TURNS_KEY = "NUMBER_OF_TURNS_KEY";
    private static final String TOTAL_TIME_KEY = "TOTAL_TIME_KEY";

    private Level level;
    private int numberOfTurns;
    private long totalTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        level = (Level) getArguments().getSerializable(LEVEL_KEY);
        numberOfTurns = getArguments().getInt(NUMBER_OF_TURNS_KEY);
        totalTime = getArguments().getLong(TOTAL_TIME_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_victory, container, false);

        Button nextLevelButton = view.findViewById(R.id.nextLevel);
        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextLevel();
            }
        });
        if (LevelUtils.nextLevel(getContext(), level) == null) {
            nextLevelButton.setEnabled(false);
        }

        Button selectLevelButton = view.findViewById(R.id.select_levels);
        selectLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectLevel();
            }
        });

        Button replayButton = view.findViewById(R.id.replay);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replay();
            }
        });

        TextView numberOfTurnsTextView = view.findViewById(R.id.number_of_turns);
        numberOfTurnsTextView.setText(FormatUtils.formatNumber(numberOfTurns));

        TextView totalTimeTextView = view.findViewById(R.id.total_time);
        totalTimeTextView.setText(FormatUtils.formatHHmmssms(totalTime));

        return view;
    }

    private void goToNextLevel() {
        Level level = LevelUtils.nextLevel(getContext(), this.level);
        if (level != null) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, LevelFragment.newInstance(level))
                .commit();
        }
        else {
            goToSelectLevel();
        }
    }

    private void goToSelectLevel() {
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new SelectLevelFragment())
            .commit();
    }

    private void replay() {
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, LevelFragment.newInstance(level))
            .commit();
    }

    public static VictoryFragment newInstance(Level level, int numberOfTurns, long totalTime) {
        VictoryFragment fragment = new VictoryFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(LEVEL_KEY, level);
        arguments.putInt(NUMBER_OF_TURNS_KEY, numberOfTurns);
        arguments.putLong(TOTAL_TIME_KEY, totalTime);
        fragment.setArguments(arguments);
        return fragment;
    }
}
