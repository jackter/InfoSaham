package com.alvarenstudio.infosaham;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.alvarenstudio.infosaham.adapter.MainCardReksadanaAdapter;
import com.alvarenstudio.infosaham.adapter.MainCardSahamAdapter;
import com.alvarenstudio.infosaham.model.MainCardReksadana;
import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavReksadanaActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<MainCardReksadana> mMainCardReksadanaUtama, mMainCardReksadana;
    private MainCardReksadanaAdapter mainCardReksadanaAdapter;
    private int sort = 0;
    private int sortType = 0;

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

    private void DialogFormSort() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sort_dialog, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle("Sort Reksadana");

        RadioButton rbAsc = dialogView.findViewById(R.id.radBatAsc);
        RadioButton rbDesc = dialogView.findViewById(R.id.radBatDesc);
        RadioButton rbCode = dialogView.findViewById(R.id.radBatCode);
        RadioButton rb1Day = dialogView.findViewById(R.id.radBat1Day);
        RadioButton rb1Month = dialogView.findViewById(R.id.radBat1Month);
        RadioButton rb1Year = dialogView.findViewById(R.id.radBat1Year);

        rbCode.setText("Name");

        if(sortType == 0) {
            rbAsc.setChecked(true);
            rbDesc.setChecked(false);
        }
        else{
            rbAsc.setChecked(false);
            rbDesc.setChecked(true);
        }

        if(sort == 0) {
            rbCode.setChecked(true);
        }
        else if(sort == 1) {
            rb1Day.setChecked(true);
        }
        else if(sort == 2) {
            rb1Month.setChecked(true);
        }
        else {
            rb1Year.setChecked(true);
        }

        rbAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortType = 0;
                rbAsc.setChecked(true);
            }
        });

        rbDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortType = 1;
                rbDesc.setChecked(true);
            }
        });

        rbCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 0;
                rbCode.setChecked(true);
            }
        });

        rb1Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 1;
                rb1Day.setChecked(true);
            }
        });

        rb1Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 2;
                rb1Month.setChecked(true);
            }
        });

        rb1Year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 3;
                rb1Year.setChecked(true);
            }
        });

        dialog.setPositiveButton("Sort", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mMainCardReksadana.size() > 0){
                    sortMainCard();
                    mainCardReksadanaAdapter.setFilter(mMainCardReksadana);
                }

                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sort = 0;
                sortType = 0;

                sortMainCard();
                mainCardReksadanaAdapter.setFilter(mMainCardReksadana);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void sortMainCard() {
        Collections.sort(mMainCardReksadana, new Comparator<MainCardReksadana>() {
            @Override
            public int compare(MainCardReksadana t1, MainCardReksadana t2) {
                if(sort == 1) {
                    if(sortType == 0) {
                        return Double.compare(t1.getOneday(), t2.getOneday());
                    }
                    else {
                        return Double.compare(t2.getOneday(), t1.getOneday());
                    }
                }
                else if(sort == 2) {
                    if(sortType == 0) {
                        return Double.compare(t1.getOnemonth(), t2.getOnemonth());
                    }
                    else {
                        return Double.compare(t2.getOnemonth(), t1.getOnemonth());
                    }
                }
                else if(sort == 3) {
                    if(sortType == 0) {
                        return Double.compare(t1.getOneyear(), t2.getOneyear());
                    }
                    else {
                        return Double.compare(t2.getOneyear(), t1.getOneyear());
                    }
                }
                else {
                    if(sortType == 0) {
                        return t1.getName().compareTo(t2.getName());
                    }
                    else {
                        return t2.getName().compareTo(t1.getName());
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.sort:
                DialogFormSort();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}