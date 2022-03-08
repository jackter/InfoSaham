package com.alvarenstudio.infosaham;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarenstudio.infosaham.model.MChart;
import com.alvarenstudio.infosaham.model.MainCardReksadana;
import com.alvarenstudio.infosaham.model.XYMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReksadanaDetailActivity extends AppCompatActivity {
    private String TAG = ReksadanaDetailActivity.class.getSimpleName();
    public TextView tvName, tvType, tvCategory, tvCur, tvNav, tvAum, tv1Day, tvMtd, tv1Month, tvYtd, tv1Year;
    MainCardReksadana mainCardReksadana;
    private List<MChart> chartList;
    private LineChart mLineChart;
    private ArrayList<String> chartStringList = new ArrayList<>();
    private ArrayList<Entry> x;
    private ArrayList<String> y;
    private ProgressBar chartProgBar;
    private LinearLayout linLayChart;
    private Button btnShowChart;
    private boolean flipChart = false;
    private boolean fav;
    private List<MainCardReksadana> mMainCardReksadanaFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reksadana_detail);

        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvCategory = findViewById(R.id.tvCategory);
        tvCur = findViewById(R.id.tvCur);
        tvNav = findViewById(R.id.tvNav);
        tvAum = findViewById(R.id.tvAum);

        tv1Day = findViewById(R.id.tv1day);
        tvMtd = findViewById(R.id.tvMtd);
        tv1Month = findViewById(R.id.tv1month);
        tvYtd = findViewById(R.id.tvYtd);
        tv1Year = findViewById(R.id.tv1year);

        linLayChart = findViewById(R.id.linLayChart);
        mLineChart = findViewById(R.id.chart);
        btnShowChart = findViewById(R.id.btnShowChart);
        chartProgBar = findViewById(R.id.ivLoading);

        Intent mIntent = getIntent();
        mainCardReksadana = (MainCardReksadana) mIntent.getSerializableExtra("mainCardReksadana");
        tvName.setText(mainCardReksadana.getName());
        tvType.setText(mainCardReksadana.getType());
        tvCategory.setText(mainCardReksadana.getCategory());
        if(mainCardReksadana.getCur() == 0){
            tvCur.setText("IDR");
        }
        else{
            tvCur.setText("USD");
        }
        tvNav.setText(currency2Format(mainCardReksadana.getNav()));
        tvAum.setText(currencyFormat(mainCardReksadana.getAum()));

        tv1Day.setText(pctFormat(mainCardReksadana.getOneday()));
        tvMtd.setText(pctFormat(mainCardReksadana.getMtd()));
        tv1Month.setText(pctFormat(mainCardReksadana.getOnemonth()));
        tvYtd.setText(pctFormat(mainCardReksadana.getYtd()));
        tv1Year.setText(pctFormat(mainCardReksadana.getOneyear()));

        btnShowChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flipChart) {
                    flipChart = true;
                    if(chartList == null) {
                        new doItChart().execute();
                    }

                    btnShowChart.setText("HIDE CHART");
                    linLayChart.setVisibility(View.VISIBLE);
                }
                else{
                    btnShowChart.setText("SHOW CHART");
                    linLayChart.setVisibility(View.GONE);
                    flipChart = false;
                }
            }
        });

        this.chart1Period();

        loadDataShared();
        for(MainCardReksadana data: mMainCardReksadanaFav) {
            if(data.getId() == mainCardReksadana.getId()){
                mainCardReksadana.setFav(true);
                fav = true;
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void chart1Period() {
        mLineChart.setDescription("");
        mLineChart.setNoDataText("");
        mLineChart.setDrawGridBackground(false);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.getXAxis().setTextSize(12f);
        mLineChart.getAxisLeft().setTextSize(12f);
        XYMarkerView mv = new XYMarkerView(getApplicationContext(), R.layout.xy_marker, chartStringList);
        mLineChart.setMarkerView(mv);

//        YAxis leftYAxis = mLineChart.getAxisLeft();
//        leftYAxis.setEnabled(false);
    }

    private class doItChart extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            chartProgBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpHandler httpHandler = new HttpHandler();

                String jsoncharturl = "https://pasardana.id/api/FundService/GetSnapshot?fundId=" + mainCardReksadana.getId() + "&username=anonymous";
                String jsonchartString = httpHandler.makeServiceCall(jsoncharturl);

                if (jsonchartString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonchartString);
                        JSONArray listChart = jsonObject.getJSONArray("NetAssetValues");

                        chartList = new ArrayList<>();
                        List<String> DateList = new ArrayList<String>();

                        for(int i = 0; i < listChart.length(); i++){
                            JSONObject jsonchart = listChart.getJSONObject(i);

                            if(!DateList.contains(jsonchart.getString("Date"))){
                                chartList.add(new MChart(jsonchart.getLong("Value"), jsonchart.getString("Date")));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(chartList != null){
                if(chartList.size() > 0){
                    System.out.println("jumlah chart : " + chartList.size());
                    chartProgBar.setVisibility(View.INVISIBLE);
                    int i = 0;
                    x = new ArrayList<Entry>();
                    y = new ArrayList<String>();

                    for (MChart chart : chartList) {
                        chartStringList.add(chart.getDate().split("T")[0]);
                        x.add(new Entry(chart.getPrice(), i));
                        y.add(chart.getDate().split("T")[0]);
                        i++;
                    }

                    if(i > 1){
                        LineDataSet set1 = new LineDataSet(x, "");
                        set1.setDrawCircleHole(false);
                        set1.setDrawCircles(false);
                        set1.setLineWidth(1.5f);
                        set1.setCircleRadius(4f);
                        set1.setValueTextColor(Color.TRANSPARENT);
                        set1.setDrawFilled(true);
                        LineData data = new LineData(y, set1);
                        mLineChart.clear();
                        mLineChart.setData(data);
                        mLineChart.invalidate();
                    }
                }
            }
        }
    }

    public String pctFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount * 100) + "%";
    }

    public String currencyFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    public String currency2Format(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.0000");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(fav){
            getMenuInflater().inflate(R.menu.right_menu_detail_fav, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.right_menu_detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.favorite:
                if(fav){
                    fav = false;
                    updateSahamFav(fav);
                    Toast.makeText(getApplicationContext(),"Reksadana dihapus dari favorite", Toast.LENGTH_LONG).show();
                    invalidateOptionsMenu();
                }
                else{
                    fav = true;
                    updateSahamFav(fav);
                    Toast.makeText(getApplicationContext(),"Reksadana ditambah ke favorite", Toast.LENGTH_LONG).show();
                    invalidateOptionsMenu();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSahamFav(boolean type) {
        loadDataShared();

        if(type) {
            mMainCardReksadanaFav.add(mainCardReksadana);
        }
        else {
            for(int i = 0; i < mMainCardReksadanaFav.size(); i++){
                if(mMainCardReksadanaFav.get(i).getId() == mainCardReksadana.getId()){
                    mMainCardReksadanaFav.remove(i);
                }
            }
        }

        saveDataShared("maincardreksadanafav", mMainCardReksadanaFav);
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
        String json = sharedPreferences.getString("maincardreksadanafav", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<List<MainCardReksadana>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardReksadanaFav = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mMainCardReksadanaFav == null) {
            // if the array list is empty
            // creating a new array list.
            mMainCardReksadanaFav = new ArrayList<>();
        }
    }

    private void saveDataShared(String nama, Object card) {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

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
}