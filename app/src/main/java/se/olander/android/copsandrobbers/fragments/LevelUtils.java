package se.olander.android.copsandrobbers.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import se.olander.android.copsandrobbers.models.Level;

public abstract class LevelUtils {

    @NonNull
    public static List<Level> getLevels(@NonNull Context context) {
        ArrayList<Level> levels = new ArrayList<>();
        try {
            AssetManager assets = context.getAssets();
            String[] fileNames = assets.list("levels");
            for (String fileName : fileNames) {
                try {
                    InputStream in = assets.open("levels/" + fileName);
                    InputStreamReader reader = new InputStreamReader(in);
                    Gson gson = new Gson();
                    Level level = gson.fromJson(reader, Level.class);
                    levels.add(level);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return levels;
    }

    @Nullable
    public static Level nextLevel(@NonNull Context context, @NonNull Level currentLevel) {
        boolean pickNext = false;
        for (Level level : getLevels(context)) {
            if (pickNext) {
                return level;
            }
            if (currentLevel.equals(level)) {
                pickNext = true;
            }
        }
        return null;
    }
}
