package com.example.shourav.watchover;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Musa on 9/14/2018.
 */

public class UserPreferences {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String SORT_ORDER = "sortOrder";

    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("user",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void setSortOrder(String order)
    {
        editor.putString(SORT_ORDER,order);
        editor.commit();
    }

    public String getSortOrder()
    {
        return sharedPreferences.getString(SORT_ORDER,"NAME");
    }
}
