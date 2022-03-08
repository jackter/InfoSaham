package com.alvarenstudio.infosaham;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.alvarenstudio.infosaham.adapter.MainCardReksadanaAdapter;
import com.alvarenstudio.infosaham.adapter.MainCardSahamAdapter;
import com.alvarenstudio.infosaham.model.MainCardReksadana;
import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavReksadanaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<MainCardReksadana> mMainCardReksadanaUtama, mMainCardReksadana;
    private MainCardReksadanaAdapter mainCardReksadanaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_reksadana);

        recyclerView = findViewById(R.id.recyleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDataShared();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadDataShared() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("maincardreksadana", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<List<MainCardReksadana>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardReksadanaUtama = gson.fromJson(json, type);

        /****************************************************************************************************************/

        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for gson.
        gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        json = sharedPreferences.getString("maincardreksadanafav", null);

        // below line is to get the type of our array list.
        type = new TypeToken<List<MainCardReksadana>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardReksadana = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mMainCardReksadana == null) {
            // if the array list is empty
            // creating a new array list.
            mMainCardReksadana = new ArrayList<>();
        }
        else {
            if(mMainCardReksadana.size() > 0){
                mainCardReksadanaAdapter = new MainCardReksadanaAdapter(FavReksadanaActivity.this, mMainCardReksadana);
                recyclerView.setAdapter(mainCardReksadanaAdapter);

                for(int i = 0; i < mMainCardReksadana.size(); i++){
                    for(int j = 0; j < mMainCardReksadanaUtama.size(); j++){
                        if(mMainCardReksadana.get(i).getId() == mMainCardReksadanaUtama.get(j).getId()){
                            mMainCardReksadana.set(i, mMainCardReksadanaUtama.get(j));
                        }
                    }
                }
                mainCardReksadanaAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        this.loadDataShared();
        super.onResume();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}