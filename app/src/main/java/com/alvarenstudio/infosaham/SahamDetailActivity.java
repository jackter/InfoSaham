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

import com.alvarenstudio.infosaham.model.CalenderSaham;
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
    private TableLayout tableDividenLayout, tableCalenderLayout;
    private LinearLayoutCompat linLayChart, linLayChartLive;
    private LinearLayout linLayDiv;
    private ImageButton btnArrowDiv, btnArrowCalender, btnArrowChartLive, btnArrowChart, btnRefresh;
    private boolean arrowDiv = true, arrowCalender = false;
    private WebView webView1, webView2;
    private boolean flipChart = false, flipChartLive = false;
    private TextView tvName, tvSectore, tvSubIndustry, tvLast, tvPrev, tvOpen, tvFreq, tvHigh, tvLow, tvPER, tvPBV, tv1Day, tv1Month, tvYtd, tv1Year, tvVol, tvVal, tvCap, tvCLHi, tvCLLo, tvCHi, tvCLo;
    private ArrayList<String> liveChartStringList = new ArrayList<>();
    private ArrayList<String> chartStringList = new ArrayList<>();
    private ArrayList<CalenderSaham> arrCalender = new ArrayList<>();
    private MainCardSaham mainCardSaham;
    private List<MainCardSaham> mMainCardSahamFav;
    private RadioButton rb1Month, rb1Year, rb3Year, rb5Year;
    private int rbChart = 4;
    private SharedPref sharedPref;
    private ScrollView svDividen, svCalender;
    private long CLHi, CLLo, CHi, CLo;
    private Formatter formatter = new Formatter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saham_detail);

        tvName = findViewById(R.id.tvName);
        tvSectore = findViewById(R.id.tvSectore);
        tvSubIndustry = findViewById(R.id.tvSubIndustry);
        tvLast = findViewById(R.id.tvLast);
        tvPrev = findViewById(R.id.tvPrev);
        tvOpen = findViewById(R.id.tvOpen);
        tvFreq = findViewById(R.id.tvFreq);
        tvHigh = findViewById(R.id.tvHigh);
        tvLow = findViewById(R.id.tvLow);
        tvPER = findViewById(R.id.tvPER);
        tvPBV = findViewById(R.id.tvPBV);
        tvVol = findViewById(R.id.tvVol);
        tvVal = findViewById(R.id.tvVal);
        tvVol = findViewById(R.id.tvVol);
        tvVal = findViewById(R.id.tvVal);
        tvCap = findViewById(R.id.tvCap);
        tv1Day = findViewById(R.id.tv1day);
        tv1Month = findViewById(R.id.tv1month);
        tvYtd = findViewById(R.id.tvYtd);
        tv1Year = findViewById(R.id.tv1year);
        tableDividenLayout = findViewById(R.id.tableLayoutDividen);
        tableCalenderLayout = findViewById(R.id.tableLayoutCalender);
        mLineChartLive = findViewById(R.id.chartLive);
        mLineChart = findViewById(R.id.chart);
        chartProgBar = findViewById(R.id.ivLoading);
        chartProgBarLive = findViewById(R.id.ivLoadingLive);
        linLayChart = findViewById(R.id.linLayChart);
        linLayChartLive = findViewById(R.id.linLayLiveChart);
        linLayDiv = findViewById(R.id.linLayDiv);
        btnArrowDiv = findViewById(R.id.btnArrowDiv);
        btnArrowCalender = findViewById(R.id.btnArrowCalender);
        btnArrowChartLive = findViewById(R.id.btnArrowChartLive);
        btnArrowChart = findViewById(R.id.btnArrowChart);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvCLHi = findViewById(R.id.tvCLHi);
        tvCLLo = findViewById(R.id.tvCLLo);
        tvCHi = findViewById(R.id.tvCHi);
        tvCLo = findViewById(R.id.tvCLo);
        webView1 = new WebView(getApplicationContext());
        webView2 = new WebView(getApplicationContext());
        svDividen = findViewById(R.id.scrollViewDividen);
        svCalender = findViewById(R.id.scrollViewCalender);

        Intent mIntent = getIntent();
        mainCardSaham = (MainCardSaham) mIntent.getSerializableExtra("mainCardSaham");
        id = mainCardSaham.getId();
        code = mainCardSaham.getCode();
        fav = mainCardSaham.isFav();
        tvName.setText(mainCardSaham.getName());
        tvSectore.setText(mainCardSaham.getSectore());
        tvSubIndustry.setText(mainCardSaham.getSubindustry());

        tvLast.setText(formatter.currencyFormat(mainCardSaham.getLast()));
        tvPrev.setText(formatter.currencyFormat(mainCardSaham.getPrev()));
        tvOpen.setText(formatter.currencyFormat(mainCardSaham.getOpen()));
        tvFreq.setText(formatter.currencyFormat(mainCardSaham.getFreq()));
        tvHigh.setText(formatter.currencyFormat(mainCardSaham.getHigh()));
        tvLow.setText(formatter.currencyFormat(mainCardSaham.getLow()));
        tvPER.setText(formatter.decimalFormat(mainCardSaham.getPer()));
        tvPBV.setText(formatter.decimalFormat(mainCardSaham.getPbv()));
        tvVol.setText(formatter.currencyFormat(mainCardSaham.getVol()));
        tvVal.setText(formatter.currencyFormat(mainCardSaham.getVal()));
        tvCap.setText(formatter.currencyFormat(mainCardSaham.getCap()));

        tv1Day.setText(formatter.pctFormat(mainCardSaham.getOneday()));
        tv1Month.setText(formatter.pctFormat(mainCardSaham.getOnemonth()));
        tvYtd.setText(formatter.pctFormat(mainCardSaham.getYtd()));
        tv1Year.setText(formatter.pctFormat(mainCardSaham.getOneyear()));

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

                    svDividen.setVisibility(View.GONE);
                }
                else{
                    arrowDiv = true;

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowDiv.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24) );
                    } else {
                        btnArrowDiv.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24));
                    }

                    svDividen.setVisibility(View.VISIBLE);
                }
            }
        });

        btnArrowCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrowCalender) {
                    arrowCalender = false;

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowCalender.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24) );
                    } else {
                        btnArrowCalender.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }

                    svCalender.setVisibility(View.GONE);
                }
                else{
                    arrowCalender = true;

                    if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        btnArrowCalender.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24) );
                    } else {
                        btnArrowCalender.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_keyboard_arrow_up_24));
                    }

                    svCalender.setVisibility(View.VISIBLE);
                }
            }
        });

        rowDividend = new ArrayList<LVDividend>();

        this.calender();

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

    public void calender() {
        String header = "TANGGAL,PERIHAL,LOKASI";
        this.createColumns(header, tableCalenderLayout);

        arrCalender = sharedPref.loadDataSharedCalenderSaham("calendersaham");

        boolean check = false;
        for(CalenderSaham data:arrCalender) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            String[] arrHeader = header.split(",");

            if(data.getEmiten().equals(code)) {
                check = true;
                for(int i = 0; i < arrHeader.length; i++) {
                    TextView tv = new TextView(this);
                    tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setTextSize(10);

                    if(arrHeader[i].equals("TANGGAL")) {
                        tv.setPadding(5, 5, 5, 5);
                        tv.setText(data.getDate());
                    }
                    else if(arrHeader[i].equals("PERIHAL")) {
                        tv.setPadding(5, 5, 5, 5);
                        tv.setText(data.getDesc());
                        tv.setWidth(400);
                        tv.setGravity(Gravity.LEFT);
                    }
                    else if(arrHeader[i].equals("LOKASI")) {
                        tv.setPadding(5, 5, 5, 5);
                        if(data.getLocation().equals("")) {
                            data.setLocation("-");
                        }
                        tv.setText(data.getLocation());
                        tv.setWidth(400);
                        tv.setGravity(Gravity.LEFT);
                    }

                    tableRow.addView(tv);
                }

                tableCalenderLayout.addView(tableRow, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.FILL_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
            }
        }

        if(!check) {
            tableCalenderLayout.setVisibility(View.GONE);
        }
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

                        tvCLHi.setText("High - " + String.valueOf(formatter.currencyFormat((double) CLHi)));
                        tvCLLo.setText("Low - " + String.valueOf(formatter.currencyFormat((double) CLLo)));
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

                        tvCHi.setText("High - " + String.valueOf(formatter.currencyFormat((double) CHi)));
                        tvCLo.setText("Low - " + String.valueOf(formatter.currencyFormat((double) CLo)));
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
                    fillData(rowDividend, tableDividenLayout);
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

    private void createColumns(String header, TableLayout tabLayout) {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tableRow.setBackgroundColor(Color.parseColor("#1da1f2"));

        String[] arrHeader = header.split(",");

        for(int i = 0; i < arrHeader.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(arrHeader[i]);
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tv.setPadding(20, 30, 20, 30);
            tv.setTextSize(12);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tableRow.addView(tv);
        }

        tabLayout.addView(tableRow, new TableLayout.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
    }

    private void fillData(List<LVDividend> dividens, TableLayout tabLayout) {
        String header = "DESKRIPSI,DIVIDEN,TAHUN,RECORD,DISTRIBUTE";
        this.createColumns(header, tabLayout);

        for (LVDividend dividen : dividens) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            String[] arrHeader = header.split(",");

            for(int i = 0; i < arrHeader.length; i++) {
                TextView tv = new TextView(this);
                tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                tv.setTextSize(10);

                if(arrHeader[i].equals("DESKRIPSI")) {
                    tv.setPadding(5, 5, 5, 5);
                    tv.setText(dividen.getDesc());
                }
                else if(arrHeader[i].equals("DIVIDEN")) {
                    tv.setPadding(5, 5, 5, 5);
                    tv.setText(String.format("%.2f", Double.parseDouble(dividen.getDividend())));
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                else if(arrHeader[i].equals("TAHUN")) {
                    tv.setPadding(5, 5, 5, 5);
                    tv.setText(dividen.getYear());
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                else if(arrHeader[i].equals("RECORD")) {
                    tv.setPadding(10, 5, 10, 5);
                    tv.setText(formatDate(dividen.getRecorddate()));
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                else if(arrHeader[i].equals("DISTRIBUTE")) {
                    tv.setPadding(10, 5, 5, 5);
                    tv.setText(formatDate(dividen.getDistributedate()));
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }

                tableRow.addView(tv);
            }

            tabLayout.addView(tableRow, new TableLayout.LayoutParams(
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