package com.example.spacecolonypioneers.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonProvider {
    private static Gson gson;

    private GsonProvider() {
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        }
        return gson;
    }
}
