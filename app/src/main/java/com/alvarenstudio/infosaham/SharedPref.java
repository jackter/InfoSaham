package com.alvarenstudio.infosaham;

import android.content.Context;

import com.alvarenstudio.infosaham.model.Emiten;
import com.alvarenstudio.infosaham.model.EmitenClosingPrice;
import com.alvarenstudio.infosaham.model.MainCardReksadana;
import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPref {
    private Context mContext;

    public SharedPref() {
    }

    public SharedPref(Context mContext) {
        this.mContext = mContext;
    }

    public void saveDataShared(String nama, Object card) {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        android.content.SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(card);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString(nama, json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();
    }

    public List<MainCardSaham> loadDataSharedMainCardSaham(String key) {
        List<MainCardSaham> mMainCardSaham;

        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        android.content.SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString(key, null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<List<MainCardSaham>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardSaham = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mMainCardSaham == null) {
            // if the array list is empty
            // creating a new array list.
            mMainCardSaham = new ArrayList<>();
        }

        return mMainCardSaham;
    }

    public List<MainCardReksadana> loadDataSharedMainCardReksadana(String key) {
        List<MainCardReksadana> mMainCardReksadana;

        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        android.content.SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString(key, null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<List<MainCardReksadana>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardReksadana = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mMainCardReksadana == null) {
            // if the array list is empty
            // creating a new array list.
            mMainCardReksadana = new ArrayList<>();
        }

        return mMainCardReksadana;
    }

    public ArrayList<EmitenClosingPrice> loadDataSharedEmitenSaham(String key) {
        ArrayList<EmitenClosingPrice> mEmitenClosingPrice;

        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        android.content.SharedPreferences sharedPreferences = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString(key, null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<EmitenClosingPrice>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mEmitenClosingPrice = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mEmitenClosingPrice == null) {
            // if the array list is empty
            // creating a new array list.
            mEmitenClosingPrice = new ArrayList<>();
        }

        return mEmitenClosingPrice;
    }
}
