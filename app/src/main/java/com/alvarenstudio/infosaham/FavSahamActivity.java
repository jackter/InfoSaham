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
import android.widget.Toast;

import com.alvarenstudio.infosaham.adapter.MainCardSahamAdapter;
import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavSahamActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<MainCardSaham> mMainCardSahamUtama, mMainCardSaham;
    private MainCardSahamAdapter mainCardSahamAdapter;
    private int sort = 0;
    private int sortType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_saham);

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
        String json = sharedPreferences.getString("maincardsaham", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<List<MainCardSaham>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardSahamUtama = gson.fromJson(json, type);

        /****************************************************************************************************************/

        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for gson.
        gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        json = sharedPreferences.getString("maincardsahamfav", null);

        // below line is to get the type of our array list.
        type = new TypeToken<List<MainCardSaham>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardSaham = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mMainCardSaham == null) {
            // if the array list is empty
            // creating a new array list.
            mMainCardSaham = new ArrayList<>();
        }
        else {
            if(mMainCardSaham.size() > 0){
                mainCardSahamAdapter = new MainCardSahamAdapter(FavSahamActivity.this, mMainCardSaham);
                recyclerView.setAdapter(mainCardSahamAdapter);

                for(int i = 0; i < mMainCardSaham.size(); i++){
                    for(int j = 0; j < mMainCardSahamUtama.size(); j++){
                        if(mMainCardSaham.get(i).getCode().equals(mMainCardSahamUtama.get(j).getCode())){
                            mMainCardSaham.set(i, mMainCardSahamUtama.get(j));
                        }
                    }
                }
                mainCardSahamAdapter.notifyDataSetChanged();
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
        dialog.setTitle("Sort Saham");

        RadioButton rbAsc = dialogView.findViewById(R.id.radBatAsc);
        RadioButton rbDesc = dialogView.findViewById(R.id.radBatDesc);
        RadioButton rbCode = dialogView.findViewById(R.id.radBatCode);
        RadioButton rb1Day = dialogView.findViewById(R.id.radBat1Day);
        RadioButton rb1Month = dialogView.findViewById(R.id.radBat1Month);
        RadioButton rb1Year = dialogView.findViewById(R.id.radBat1Year);
        RadioButton rbOpen = dialogView.findViewById(R.id.radBatOpen);
        RadioButton rbFreq = dialogView.findViewById(R.id.radBatFreq);

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
        else if(sort == 3){
            rb1Year.setChecked(true);
        }
        else if(sort == 4){
            rbOpen.setChecked(true);
        }
        else if(sort == 5){
            rbFreq.setChecked(true);
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
                rb1Day.setChecked(false);
                rb1Month.setChecked(false);
                rb1Year.setChecked(false);
                rbOpen.setChecked(false);
                rbFreq.setChecked(false);
            }
        });

        rb1Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 1;

                rbCode.setChecked(false);
                rb1Day.setChecked(true);
                rb1Month.setChecked(false);
                rb1Year.setChecked(false);
                rbOpen.setChecked(false);
                rbFreq.setChecked(false);
            }
        });

        rb1Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 2;

                rbCode.setChecked(false);
                rb1Day.setChecked(false);
                rb1Month.setChecked(true);
                rb1Year.setChecked(false);
                rbOpen.setChecked(false);
                rbFreq.setChecked(false);
            }
        });

        rb1Year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 3;

                rbCode.setChecked(false);
                rb1Day.setChecked(false);
                rb1Month.setChecked(false);
                rb1Year.setChecked(true);
                rbOpen.setChecked(false);
                rbFreq.setChecked(false);
            }
        });

        rbOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 4;

                rbCode.setChecked(false);
                rb1Day.setChecked(false);
                rb1Month.setChecked(false);
                rb1Year.setChecked(false);
                rbOpen.setChecked(true);
                rbFreq.setChecked(false);
            }
        });

        rbFreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort = 5;

                rbCode.setChecked(false);
                rb1Day.setChecked(false);
                rb1Month.setChecked(false);
                rb1Year.setChecked(false);
                rbOpen.setChecked(false);
                rbFreq.setChecked(true);
            }
        });

        dialog.setPositiveButton("Sort", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mMainCardSaham.size() > 0){
                    sortMainCard();
                    mainCardSahamAdapter.setFilter(mMainCardSaham);
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
                mainCardSahamAdapter.setFilter(mMainCardSaham);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void sortMainCard() {
        Collections.sort(mMainCardSaham, new Comparator<MainCardSaham>() {
            @Override
            public int compare(MainCardSaham t1, MainCardSaham t2) {
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
                        return t1.getCode().compareTo(t2.getCode());
                    }
                    else {
                        return t2.getCode().compareTo(t1.getCode());
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