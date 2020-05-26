package ru.alfapoint.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private SharedPreferences sharedPreferences;
    private static UserPreferences userPreferences;


    public static UserPreferences getInstance(Context context) {
        if (userPreferences == null) {
            userPreferences = new UserPreferences(context);
        }
        return userPreferences;
    }

    private UserPreferences(Context context){
        sharedPreferences = context.getSharedPreferences("sharedSettings", Context.MODE_PRIVATE);
    }

    public void setFistrRun(String vote){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("storeUserVote", vote);
        editor.commit();
    }
    public String getFirstRun(){
        return sharedPreferences.getString("storeUserVote", null);
    }

}
