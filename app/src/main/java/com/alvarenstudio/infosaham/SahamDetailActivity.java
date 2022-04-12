package com.alvarenstudio.infosaham;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarenstudio.infosaham.model.LVDividend;
import com.alvarenstudio.infosaham.model.MChart;
import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.alvarenstudio.infosaham.model.XYMarkerView;
import com.github.mikephil.charting.charts.LineChart;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SahamDetailActivity extends AppCompatActivity {
    private String TAG = SahamDetailActivity.class.getSimpleName();
    private int id;
    private String code;
    private boolean fav;
    private List<LVDividend> rowDividend;
    private List<MChart> chartList, chartLiveList;
    private LineChart mLineChart, mLineChartLive;
    private ArrayList<Entry> x;
    private ArrayList<String> y;
    private ProgressBar chartProgBar, chartProgBarLive;
    private TableLayout tableLayout;
    private LinearLayoutCompat linLayChart, linLayChartLive;
    private LinearLayout linLayDiv;
    private ImageButton btnArrowDiv, btnArrowChartLive, btnArrowChart, btnRefresh;
    private boolean arrowDiv = true;
    private WebView webView1, webView2;
    private boolean flipChart = false, flipChartLive = false;
    private TextView tvName, tvSectore, tvSubIndustry, tvVol, tvVal, tvCap, tvCLHi, tvCLLo, tvCHi, tvCLo;
    private ArrayList<String> liveChartStringList = new ArrayList<>();
    private ArrayList<String> chartStringList = new ArrayList<>();
    private MainCardSaham mainCardSaham;
    private List<MainCardSaham> mMainCardSahamFav;
    private RadioButton rb1Month, rb1Year, rb3Year, rb5Year;
    private int rbChart = 4;
    private SharedPref sharedPref;
    private ScrollView scrollView;
    private long CLHi, CLLo, CHi, CLo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saham_detail);

        tvName = findViewById(R.id.tvName);
        tvSectore = findViewById(R.id.tvSectore);
        tvSubIndustry = findViewById(R.id.tvSubIndustry);
        tvVol = findViewById(R.id.tvVol);
        tvVal = findViewById(R.id.tvVal);
        tvCap = findViewById(R.id.tvCap);
        tableLayout = findViewById(R.id.tableLayoutDividen);
        mLineChartLive = findViewById(R.id.chartLive);
        mLineChart = findViewById(R.id.chart);
        chartProgBar = findViewById(R.id.ivLoading);
        chartProgBarLive = findViewById(R.id.ivLoadingLive);
        linLayChart = findViewById(R.id.linLayChart);
        linLayChartLive = findViewById(R.id.linLayLiveChart);
        linLayDiv = findViewById(R.id.linLayDiv);
        btnArrowDiv = findViewById(R.id.btnArrowDiv);
        btnArrowChartLive = findViewById(R.id.btnArrowChartLive);
        btnArrowChart = findViewById(R.id.btnArrowChart);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvCLHi = findViewById(R.id.tvCLHi);
        tvCLLo = findViewById(R.id.tvCLLo);
        tvCHi = findViewById(R.id.tvCHi);
        tvCLo = findViewById(R.id.tvCLo);
        webView1 = new WebView(getApplicationContext());
        webView2 = new WebView(getApplicationContext());
        scrollView = findViewById(R.id.scrollViewDividend);

        Intent mIntent = getIntent();
        mainCardSaham = (MainCardSaham) mIntent.getSerializableExtra("mainCardSaham");
        id = mainCardSaham.getId();
        code = mainCardSaham.getCode();
        fav = mainCardSaham.isFav();
        tvName.setText(mainCardSaham.getName());
        tvSectore.setText(mainCardSaham.getSectore());
        tvSubIndustry.setText(mainCardSaham.getSubindustry());
        tvVol.setText(currencyFormat(mainCardSaham.getVol()));
        tvVal.setText(currencyFormat(mainCardSaham.getVal()));
        tvCap.setText(currencyFormat(mainCardSaham.getCap()));

        sharedPref = new SharedPref(getApplicationContext());
        mMainCardSahamFav = sharedPref.loadDataSharedMainCardSaham("maincardsahamfav");

        for(MainCardSaham data: mMainCardSahamFav) {
            if(data.getCode().equals(mainCardSaham.getCode())){
                mainCardSaham.setFav(true);
                fav = true;
            }
        }

        btnArrowDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrowDiv) {
                    arrowDiv = false;

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowDiv.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24) );
                    } else {
                        btnArrowDiv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }

                    scrollView.setVisibility(View.GONE);
                }
                else{
                    arrowDiv = true;

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowDiv.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24) );
                    } else {
                        btnArrowDiv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24));
                    }

                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        });

        rowDividend = new ArrayList<LVDividend>();

        new doItDiv().execute();

        this.webViewPg1();
        this.webViewPg2();
        this.chart1Period();
        this.chart3Years();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveChartStringList.clear();
                webView1.loadUrl("https://idx.co.id/umbraco/Surface/Helper/GetStockChart?indexCode=" + code);
            }
        });

        btnArrowChartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flipChartLive) {
                    flipChartLive = true;
                    if(chartLiveList == null) {
                        liveChartStringList.clear();
                        webView1.loadUrl("https://idx.co.id/umbraco/Surface/Helper/GetStockChart?indexCode=" + code);
                    }

                    linLayChartLive.setVisibility(View.VISIBLE);

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowChartLive.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24) );
                    } else {
                        btnArrowChartLive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24));
                    }
                }
                else{
                    linLayChartLive.setVisibility(View.GONE);
                    flipChartLive = false;

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowChartLive.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24) );
                    } else {
                        btnArrowChartLive.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                }
            }
        });

        btnArrowChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flipChart) {
                    flipChart = true;
                    if(chartList == null) {
                        if(rbChart == 1) {
                            webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=250");
                        }
                        else if (rbChart == 2) {
                            webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=750");
                        }
                        else if (rbChart == 3) {
                            webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=1250");
                        }
                        else {
                            webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=21");
                        }
                    }

                    linLayChart.setVisibility(View.VISIBLE);

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowChart.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24) );
                    } else {
                        btnArrowChart.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24));
                    }
                }
                else{
                    linLayChart.setVisibility(View.GONE);
                    flipChart = false;

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowChart.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24) );
                    } else {
                        btnArrowChart.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }
                }
            }
        });

        rb1Month = findViewById(R.id.radBat1Month);
        rb1Year = findViewById(R.id.radBat1Year);
        rb3Year = findViewById(R.id.radBat3Year);
        rb5Year = findViewById(R.id.radBat5Year);

        rb1Month.setChecked(true);
        rb1Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbChart = 4;
                rb1Month.setChecked(true);
                chartList = null;
                chartStringList.clear();
                webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=21");
            }
        });

        rb1Year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbChart = 1;
                rb1Year.setChecked(true);
                chartList = null;
                chartStringList.clear();
                webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=250");
            }
        });

        rb3Year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbChart = 2;
                rb3Year.setChecked(true);
                chartList = null;
                chartStringList.clear();
                webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=750");
            }
        });

        rb5Year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbChart = 3;
                rb5Year.setChecked(true);
                chartList = null;
                chartStringList.clear();
                webView2.loadUrl("https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoSS?code=" + code + "&length=1250");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public String currencyFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    private void webViewPg1() {
        WebViewClient webClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(flipChartLive){
                    chartProgBarLive.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:console.log(document.body.getElementsByTagName('pre')[0].innerHTML);");
            }
        };


        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.setWebViewClient(webClient);
        webView1.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cmsg)
            {
                if (cmsg.message() != null) {
                    try {
                        if(flipChartLive) {
                            JSONObject jsonObject = new JSONObject(cmsg.message());
                            JSONArray listChart = jsonObject.getJSONArray("ChartData");

                            chartLiveList = new ArrayList<>();
                            List<String> DateList = new ArrayList<String>();

                            for(int i = 0; i < listChart.length(); i++){
                                JSONObject jsonchart = listChart.getJSONObject(i);

                                if(!DateList.contains(jsonchart.getLong("Date")) && jsonchart.getLong("Close") != 0){
                                    chartLiveList.add(new MChart(jsonchart.getLong("Close"), String.valueOf(jsonchart.getLong("Date"))));
                                    DateList.add(String.valueOf(jsonchart.getLong("Date")));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                }

                if(chartLiveList != null && flipChartLive){
                    if(chartLiveList.size() > 0){
                        chartProgBarLive.setVisibility(View.INVISIBLE);

                        Collections.sort(chartLiveList, new Comparator<MChart>() {
                            @Override
                            public int compare(MChart t1, MChart t2) {
                                return t1.getDate().compareTo(t2.getDate());
                            }
                        });

                        int i = 0;
                        x = new ArrayList<Entry>();
                        y = new ArrayList<String>();

                        CLHi = 0;
                        CLLo = 100000;

                        for (MChart chart : chartLiveList) {
                            liveChartStringList.add(timestampToDT(Long.valueOf(chart.getDate())));
                            x.add(new Entry(chart.getPrice(), i));
                            y.add(timestampToDT(Long.valueOf(chart.getDate())));

                            if(chart.getPrice() > CLHi) {
                                CLHi = chart.getPrice();
                            }

                            if(chart.getPrice() < CLLo) {
                                CLLo = chart.getPrice();
                            }

                            i++;
                        }

                        tvCLHi.setText("High - " + String.valueOf(currencyFormat((double) CLHi)));
                        tvCLLo.setText("Low - " + String.valueOf(currencyFormat((double) CLLo)));
                        tvCLHi.setVisibility(View.VISIBLE);
                        tvCLLo.setVisibility(View.VISIBLE);

                        if(i > 1){
                            LineDataSet set1 = new LineDataSet(x, "");
                            set1.setDrawCircleHole(false);
                            set1.setDrawCircles(false);
                            set1.setLineWidth(1.5f);
                            set1.setCircleRadius(4f);
                            set1.setValueTextColor(Color.TRANSPARENT);
                            set1.setDrawFilled(true);
                            LineData data = new LineData(y, set1);
                            mLineChartLive.clear();
                            mLineChartLive.setData(data);
                            mLineChartLive.invalidate();
                        }
                    }
                }

                return true;
            }
        });
    }

    private void webViewPg2() {
        WebViewClient webClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(flipChart){
                    chartProgBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:console.log(document.body.getElementsByTagName('pre')[0].innerHTML);");
            }
        };


        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.setWebViewClient(webClient);
        webView2.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cmsg)
            {
                if (cmsg.message() != null) {
                    try {
                        if(flipChart) {
                            JSONObject jsonObject = new JSONObject(cmsg.message());
                            JSONArray listChart = jsonObject.getJSONArray("replies");

                            chartList = new ArrayList<>();
                            List<String> DateList = new ArrayList<String>();

                            for(int i = 0; i < listChart.length(); i++){
                                JSONObject jsonchart = listChart.getJSONObject(i);

                                if(!DateList.contains(jsonchart.getString("Date"))){
                                    chartList.add(new MChart(jsonchart.getLong("Close"), jsonchart.getString("Date")));
                                    DateList.add(jsonchart.getString("Date"));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                }

                if(chartList != null && flipChart){
                    if(chartList.size() > 0){
                        chartProgBar.setVisibility(View.INVISIBLE);

                        Collections.sort(chartList, new Comparator<MChart>() {
                            @Override
                            public int compare(MChart t1, MChart t2) {
                                return t1.getDate().compareTo(t2.getDate());
                            }
                        });

                        int i = 0;
                        x = new ArrayList<Entry>();
                        y = new ArrayList<String>();

                        CHi = 0;
                        CLo = 100000;

                        for (MChart chart : chartList) {
                            chartStringList.add(chart.getDate().split("T")[0]);
                            x.add(new Entry(chart.getPrice(), i));
                            y.add(chart.getDate().split("T")[0]);

                            if(chart.getPrice() > CHi) {
                                CHi = chart.getPrice();
                            }

                            if(chart.getPrice() < CLo) {
                                CLo = chart.getPrice();
                            }

                            i++;
                        }

                        tvCHi.setText("High - " + String.valueOf(currencyFormat((double) CHi)));
                        tvCLo.setText("Low - " + String.valueOf(currencyFormat((double) CLo)));
                        tvCHi.setVisibility(View.VISIBLE);
                        tvCLo.setVisibility(View.VISIBLE);

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

                return true;
            }
        });
    }

    private void chart1Period() {
        mLineChartLive.setDescription("Live Chart : " + code);
        mLineChartLive.setNoDataText("");
        mLineChartLive.setDrawGridBackground(false);
        mLineChartLive.setTouchEnabled(true);
        mLineChartLive.setDragEnabled(true);
        mLineChartLive.setScaleEnabled(true);
        mLineChartLive.setPinchZoom(true);
        mLineChartLive.getXAxis().setTextSize(12f);
        mLineChartLive.getAxisLeft().setTextSize(12f);
        XYMarkerView mv = new XYMarkerView(getApplicationContext(), R.layout.xy_marker, liveChartStringList);
        mLineChartLive.setMarkerView(mv);
    }

    private void chart3Years() {
        mLineChart.setDescription("Chart : " + code);
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
    }

    private String timestampToDT(Long timestamp) {
        long yourmilliseconds = timestamp - (7 * 3600 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date(yourmilliseconds);

        return sdf.format(resultdate).toString();
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

            if(rowDividend != null){
                if(rowDividend.size() > 0){
                    fillData(rowDividend);
                    linLayDiv.setVisibility(View.VISIBLE);
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
        tableRow.setBackgroundColor(Color.parseColor("#1da1f2"));

        // Desc Column
        TextView textViewDesc = new TextView(this);
        textViewDesc.setText("DESKRIPSI");
        textViewDesc.setTextColor(Color.WHITE);
        textViewDesc.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDesc.setPadding(20, 30, 20, 30);
        textViewDesc.setTextSize(12);
        textViewDesc.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewDesc);

        // Dividen Column
        TextView textViewDividen = new TextView(this);
        textViewDividen.setText("DIVIDEN");
        textViewDividen.setTextColor(Color.WHITE);
        textViewDividen.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDividen.setPadding(20, 30, 20, 30);
        textViewDividen.setTextSize(12);
        textViewDividen.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewDividen);

        // Tahun Column
        TextView textViewTahun = new TextView(this);
        textViewTahun.setText("TAHUN");
        textViewTahun.setTextColor(Color.WHITE);
        textViewTahun.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewTahun.setPadding(20, 30, 20, 30);
        textViewTahun.setTextSize(12);
        textViewTahun.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewTahun);

        // Record Column
        TextView textViewRecord = new TextView(this);
        textViewRecord.setText("RECORD");
        textViewRecord.setTextColor(Color.WHITE);
        textViewRecord.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewRecord.setPadding(20, 30, 20, 30);
        textViewRecord.setTextSize(12);
        textViewRecord.setGravity(Gravity.CENTER_HORIZONTAL);
        tableRow.addView(textViewRecord);

        // Distribute Column
        TextView textViewDistribute = new TextView(this);
        textViewDistribute.setText("DISTRIBUTE");
        textViewDistribute.setTextColor(Color.WHITE);
        textViewDistribute.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textViewDistribute.setPadding(20, 30, 20, 30);
        textViewDistribute.setTextSize(12);
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
            textViewDesc.setPadding(5, 5, 5, 5);
            textViewDesc.setTextSize(10);
            tableRow.addView(textViewDesc);

            // Dividen Column
            TextView textViewDividen = new TextView(this);
            textViewDividen.setText(String.format("%.2f", Double.parseDouble(dividen.getDividend())));
            textViewDividen.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewDividen.setPadding(5, 5, 5, 5);
            textViewDividen.setTextSize(12);
            textViewDividen.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(textViewDividen);

            // Tahun Column
            TextView textViewTahun = new TextView(this);
            textViewTahun.setText(dividen.getYear());
            textViewTahun.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewTahun.setPadding(5, 5, 5, 5);
            textViewTahun.setTextSize(12);
            textViewTahun.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(textViewTahun);

            // Record Column
            TextView textViewRecord = new TextView(this);
            textViewRecord.setText(formatDate(dividen.getRecorddate()));
            textViewRecord.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewRecord.setPadding(10, 5, 10, 5);
            textViewRecord.setTextSize(12);
            textViewRecord.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(textViewRecord);

            // Distribute Column
            TextView textViewDistribute = new TextView(this);
            textViewDistribute.setText(formatDate(dividen.getDistributedate()));
            textViewDistribute.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewDistribute.setPadding(10, 5, 5, 5);
            textViewDistribute.setTextSize(12);
            textViewDistribute.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(textViewDistribute);

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
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
                    Toast.makeText(getApplicationContext(),"Saham dihapus dari favorite", Toast.LENGTH_LONG).show();
                    invalidateOptionsMenu();
                }
                else{
                    fav = true;
                    updateSahamFav(fav);
                    Toast.makeText(getApplicationContext(),"Saham ditambah ke favorite", Toast.LENGTH_LONG).show();
                    invalidateOptionsMenu();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSahamFav(boolean type) {
        mMainCardSahamFav = sharedPref.loadDataSharedMainCardSaham("maincardsahamfav");

        if(type) {
            mMainCardSahamFav.add(mainCardSaham);
        }
        else {
            List<MainCardSaham> mMainCardSahamFavTemp = new ArrayList<>();

            for(MainCardSaham data : mMainCardSahamFav){
                if(!data.getCode().equals(mainCardSaham.getCode())) {
                    mMainCardSahamFavTemp.add(data);
                }
            }

            mMainCardSahamFav = mMainCardSahamFavTemp;
        }

        saveDataShared("maincardsahamfav", mMainCardSahamFav);
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