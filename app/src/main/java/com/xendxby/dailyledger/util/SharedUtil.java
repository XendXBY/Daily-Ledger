package com.xendxby.dailyledger.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedUtil {

    private static SharedUtil mUtil;

    private SharedPreferences preferences;

    public static SharedUtil getInstance(Context ctx) {
        if (mUtil == null) {
            mUtil = new SharedUtil();
            mUtil.preferences = ctx.getSharedPreferences("ledger_state", Context.MODE_PRIVATE);
        }
        return mUtil;
    }

    public void writeInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    public int readInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public void writeBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean readBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
}
