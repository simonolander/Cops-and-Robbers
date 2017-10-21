package se.olander.android.copsandrobbers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.olander.android.copsandrobbers.R;


public class SelectLevelFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_level, container, false);
        view.findViewById(R.id.button_level1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LevelFragment fragment = new LevelFragment();
                Bundle arguments = new Bundle();
                arguments.putInt(LevelFragment.LEVEL_RES_ID_KEY, R.layout.fragment_level_test);
                fragment.setArguments(arguments);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
