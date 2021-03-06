package se.olander.android.copsandrobbers.fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.olander.android.copsandrobbers.R;
import se.olander.android.copsandrobbers.models.Level;

class LevelListAdapter extends ArrayAdapter<Level> {
    public LevelListAdapter(@NonNull Context context) {
        super(context, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_level, parent, false);
        }

        TextView title = view.findViewById(R.id.title);
        title.setText(getItem(position).getTitle());

        return view;
    }
}
