package com.alvarenstudio.pasardanawatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarenstudio.pasardanawatcher.model.LVDividend;
import com.alvarenstudio.pasardanawatcher.model.MChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SahamDetailActivity extends AppCompatActivity {
    private String TAG = SahamDetailActivity.class.getSimpleName();
    private int id;
    private String code;
    private List<LVDividend> rowDividend;
    private List<MChart> chartList;
    private LineChart mLineChart;
    private ArrayList<Entry> x;
    private ArrayList<String> y;
    private ProgressBar chartProgBar, chartProgBarLive;
    private TableLayout tableLayout;
    private LinearLayout linLayChart, linLayChartLive;
    private ImageButton imgBtnRefresh;
    private Button btnShowChart, btnShowLiveChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saham_detail);

        Intent mIntent = getIntent();
        id = mIntent.getIntExtra("id", 0);
        code = mIntent.getStringExtra("code");

        tableLayout = findViewById(R.id.tableLayoutDividen);
        mLineChart = findViewById(R.id.chart);
        chartProgBar = findViewById(R.id.ivLoading);
        chartProgBarLive = findViewById(R.id.ivLoadingLive);
        linLayChart = findViewById(R.id.linLayChart);
        linLayChartLive = findViewById(R.id.linLayLiveChart);
        btnShowChart = findViewById(R.id.btnShowChart);
        btnShowLiveChart = findViewById(R.id.btnShowLiveChart);
        imgBtnRefresh = findViewById(R.id.imgBtnRefresh);

        rowDividend = new ArrayList<LVDividend>();

        new doItDiv().execute();

        mLineChart.setDescription("Chart : " + code);
        mLineChart.setNoDataText("");
        mLineChart.setDrawGridBackground(false);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.getXAxis().setTextSize(12f);
        mLineChart.getAxisLeft().setTextSize(12f);

        btnShowChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doItChart().execute();
                btnShowChart.setVisibility(View.GONE);
                linLayChart.setVisibility(View.VISIBLE);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class doItDiv extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpHandler httpHandler = new HttpHandler();
                String jsondividendurl = "https://pasardana.id/api/StockData/GetStockDividendActions?Id=" + id;
                String jsondividendString = httpHandler.makeServiceCall(jsondividendurl);

                if (jsondividendString != null) {
                    try {
                        JSONArray listDividend = new JSONArray(jsondividendString);
                        LVDividend dividend;

                        for(int i = 0; i < listDividend.length(); i++){
                            JSONObject jsondividend = listDividend.getJSONObject(i);
                            dividend = new LVDividend(jsondividend.getString("Type"), jsondividend.getString("ProceedInstrument"), jsondividend.getString("Year"), jsondividend.getString("RecordDate"), jsondividend.getString("DistributionDate"));

                            rowDividend.add(dividend);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Gagal mengambil data json dari server.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

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

            if(rowDividend != null){
                if(rowDividend.size() > 0){
                    fillData(rowDividend);
                }
            }
        }
    }

    private class doItChart extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpHandler httpHandler = new HttpHandler();

//                String jsoncharturl = "https://pasardana.id/api/StockData/GetStockDailyClosingPrice?code=" + code + "&username=anonymous";
                String jsoncharturl = "https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=750";

                String jsonchartString = httpHandler.makeServiceCall(jsoncharturl);

                if (jsonchartString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonchartString);
                        JSONArray listChart = jsonObject.getJSONArray("replies");
//                        JSONArray listChart = new JSONArray(jsonchartString);

                        chartList = new ArrayList<>();
                        List<String> DateList = new ArrayList<String>();

                        for(int i = 0; i < listChart.length(); i++){
                            JSONObject jsonchart = listChart.getJSONObject(i);

                            if(!DateList.contains(jsonchart.getString("Date"))){
                                chartList.add(new MChart(jsonchart.getLong("Close"), jsonchart.getString("Date")));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Gagal mengambil data json dari server.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

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
                    linLayChartLive.setVisibility(View.INVISIBLE);

                    int i = 0;
                    x = new ArrayList<Entry>();
                    y = new ArrayList<String>();

                    for (MChart chart : chartList) {
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
                        LineData data = new LineData(y, set1);
                        mLineChart.clear();
                        mLineChart.setData(data);
                        mLineChart.invalidate();
                    }
                }
            }
        }
    }

    public String formatDate(String date){
        SimpleDateFormat dateFormatSrc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        SimpleDateFormat dateFormatDst = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormatSrc.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormatSrc.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateFormatDst.format(parsedDate);
    }

    private void createColumns() {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setBackgroundColor(Color.parseColor("#99cccc"));

        // Desc Column
        TextView textViewDesc = new TextView(this);
        textViewDesc.setText("Deskripsi");
        textViewDesc.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDesc.setPadding(30, 30, 30, 30);
        textViewDesc.setTextSize(14);
        textViewDesc.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewDesc);

        // Dividen Column
        TextView textViewDividen = new TextView(this);
        textViewDividen.setText("Dividen");
        textViewDividen.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDividen.setPadding(30, 30, 30, 30);
        textViewDividen.setTextSize(14);
        textViewDividen.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewDividen);

        // Tahun Column
        TextView textViewTahun = new TextView(this);
        textViewTahun.setText("Tahun");
        textViewTahun.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewTahun.setPadding(30, 30, 30, 30);
        textViewTahun.setTextSize(14);
        textViewTahun.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewTahun);

        // Record Column
        TextView textViewRecord = new TextView(this);
        textViewRecord.setText("Record");
        textViewRecord.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewRecord.setPadding(30, 30, 30, 30);
        textViewRecord.setTextSize(14);
        textViewRecord.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewRecord);

        // Distribute Column
        TextView textViewDistribute = new TextView(this);
        textViewDistribute.setText("Distribute");
        textViewDistribute.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDistribute.setPadding(30, 30, 30, 30);
        textViewDistribute.setTextSize(14);
        textViewDistribute.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewDistribute);

        tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    private void fillData(List<LVDividend> dividens) {
        this.createColumns();

        for (LVDividend dividen : dividens) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // Desc Column
            TextView textViewDesc = new TextView(this);
            textViewDesc.setText(dividen.getDesc());
            textViewDesc.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewDesc.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewDesc);

            // Dividen Column
            TextView textViewDividen = new TextView(this);
            textViewDividen.setText(String.format("%.2f", Double.parseDouble(dividen.getDividend())));
            textViewDividen.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewDividen.setPadding(5, 5, 5, 0);
            textViewDividen.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(textViewDividen);

            // Tahun Column
            TextView textViewTahun = new TextView(this);
            textViewTahun.setText(dividen.getYear());
            textViewTahun.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewTahun.setPadding(5, 5, 5, 0);
            textViewTahun.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(textViewTahun);

            // Record Column
            TextView textViewRecord = new TextView(this);
            textViewRecord.setText(formatDate(dividen.getRecorddate()));
            textViewRecord.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewRecord.setPadding(10, 5, 10, 0);
            tableRow.addView(textViewRecord);

            // Distribute Column
            TextView textViewDistribute = new TextView(this);
            textViewDistribute.setText(formatDate(dividen.getDistributedate()));
            textViewDistribute.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewDistribute.setPadding(10, 5, 5, 0);
            tableRow.addView(textViewDistribute);

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
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