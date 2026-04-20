package com.example.spacecolonypioneers.manager;

import android.content.Context;

import com.example.spacecolonypioneers.model.GameState;
import com.example.spacecolonypioneers.util.GsonProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class StorageManager {
    private static final String SAVE_FILE = "game_save.json";

    public static void saveGame(Context context, GameState state) {
        if (context == null || state == null) return;
        try {
            state.updatePersistentFields();
            String json = GsonProvider.getGson().toJson(state);
            FileOutputStream fos = context.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static GameState loadGame(Context context) {
        if (context == null) return null;
        try {
            FileInputStream fis = context.openFileInput(SAVE_FILE);
            InputStreamReader isr = new InputStreamReader(fis);
            GameState state = GsonProvider.getGson().fromJson(isr, GameState.class);
            isr.close();
            if (state != null) {
                state.restoreTransientFields();
                if (state.getStatistics() != null && state.getCrewList() != null) {
                    state.getStatistics().initializeCrewStats(state.getCrewList());
                }
            }
            return state;
        } catch (Exception e) {
            return null;
        }
    }
}
