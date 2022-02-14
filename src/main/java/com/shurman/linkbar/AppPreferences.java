package com.shurman.linkbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferences {
    private static final int MAX_ICONS_COUNT = 5;
    private static final String KEY_ICONS = "icons";
    private static final String DEFAULT_STRING = "NULL\nNULL\nNULL\n";
    private static final String NULL_PACKAGE = "NULL";
    private static final char DELIMITER = '\n';

    public static String[] getBarPackagesNamesArray(Context context) {
        String[] result = new String[MAX_ICONS_COUNT];
        String str = PreferenceManager.getDefaultSharedPreferences(context)
                                        .getString(KEY_ICONS, DEFAULT_STRING);
        int index = 0, start = 0, delim;
        while (index < MAX_ICONS_COUNT) {
            delim = str.indexOf(DELIMITER, start);
            if (-1 == delim) break;
            result[index] = str.substring(start,delim);
            start = delim + 1;
            index++;
        }
        for (; index < MAX_ICONS_COUNT; index++) {
            result[index] = NULL_PACKAGE;
        }
        return result;
    }

    public static void saveBarPackagesNamesArray(Context context, String[] pkgs) {
        StringBuilder sb = new StringBuilder();
        for (String line : pkgs) {
            sb.append(line).append(DELIMITER);
        }
        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_ICONS, sb.toString());
        editor.apply();
    }
}
