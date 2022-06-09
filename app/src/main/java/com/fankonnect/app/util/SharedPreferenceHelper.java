package com.fankonnect.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    private final static String PREF_FILE = "CLP_CONNECT_PREF";
    private final static String TOKEN = "TOKEN";
    private final static String LANDING_URL = "CLP_CONNECT_PREF";

    /**
     * Set a string shared preference
     *
     * @param token - Value for the token
     */
    public static void saveToken(Context context, String token) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    /**
     * Get a string shared preference
     *
     * @return value - String containing value of the shared preference if found.
     */
    public static String getToken(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getString(TOKEN, null);
    }

    /**
     * Set a string shared preference
     *
     * @param landingURL - Value for the token
     */
    public static void saveLandingUrl(Context context, String landingURL) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LANDING_URL, landingURL);
        editor.apply();
    }

    /**
     * Get a string shared preference
     *
     * @return value - String containing value of the shared preference if found.
     */
    public static String getLandingUrl(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREF_FILE, 0);
        return settings.getString(LANDING_URL, null);
    }
}