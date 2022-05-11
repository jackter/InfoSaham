package com.alvarenstudio.infosaham;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alvarenstudio.infosaham.model.CalenderSaham;
import com.alvarenstudio.infosaham.model.CatatanSaham;
import com.alvarenstudio.infosaham.model.Emiten;
import com.alvarenstudio.infosaham.model.EmitenClosingPrice;
import com.alvarenstudio.infosaham.model.MChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WebViewHandler {
    private static final String TAG = WebViewHandler.class.getSimpleName();
    private WebView webView;
    private WebViewClient webClient;
    private SharedPref sharedPref;
    private ArrayList<EmitenClosingPrice> emitenClosingPrices;

    public WebViewHandler(Context mContext) {
        this.webView = new WebView(mContext);
        this.sharedPref = new SharedPref(mContext);

        webClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webClient);
    }

    public void getPrice(String emiten, String URL) {
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cmsg)
            {
                if (cmsg.message() != null) {
                    try {
                        emitenClosingPrices = sharedPref.loadDataSharedEmitenSaham("emitenclosingprices");

                        JSONObject jsonObject = new JSONObject(cmsg.message());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        if(!jsonObject.isNull("DTCreate")) {
                            Date parsedDate = dateFormat.parse(jsonObject.getString("DTCreate").split("T")[0] + " " + jsonObject.getString("DTCreate").split("T")[1]);
                            if(emitenClosingPrices.size() > 0) {
                                ArrayList<EmitenClosingPrice> emitenClosingPricesTemp = new ArrayList<>();
                                ArrayList<String> arrStringEmiten = new ArrayList<>();

                                for(EmitenClosingPrice item: emitenClosingPrices) {
                                    if(!arrStringEmiten.contains(item.getEmiten())) {
                                        arrStringEmiten.add(item.getEmiten());
                                        emitenClosingPricesTemp.add(item);
                                    }
                                }

                                emitenClosingPrices = emitenClosingPricesTemp;

                                boolean find = false;
                                for(EmitenClosingPrice data: emitenClosingPrices) {
                                    if(data.getEmiten().equals(emiten) && jsonObject.getLong("ClosingPrice") > 0) {
                                        find = true;
                                        if(parsedDate.getTime() > data.getDate()) {
                                            data.setClosingPrice(jsonObject.getLong("ClosingPrice"));
                                            data.setDate(parsedDate.getTime());
                                        }
                                    }
                                }

                                if(!find && jsonObject.getLong("ClosingPrice") > 0) {
                                    emitenClosingPrices.add(new EmitenClosingPrice(emiten, jsonObject.getLong("ClosingPrice"), parsedDate.getTime()));
                                }
                            }
                            else {
                                if(jsonObject.getLong("ClosingPrice") > 0) {
                                    emitenClosingPrices.add(new EmitenClosingPrice(emiten, jsonObject.getLong("ClosingPrice"), parsedDate.getTime()));
                                }
                            }
                        }

                        sharedPref.saveDataShared("emitenclosingprices", emitenClosingPrices);

                    } catch (JSONException | ParseException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                }

                return true;
            }
        });

        webView.loadUrl(URL);
    }

    public void getCalender() {
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cmsg)
            {
                if (cmsg.message() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(cmsg.message());
                        JSONArray calenderList = jsonObject.getJSONArray("Results");
                        ArrayList<CalenderSaham> arrCalender = new ArrayList<>();

                        for(int i = 0; i < calenderList.length(); i++){
                            JSONObject calender = calenderList.getJSONObject(i);
                            arrCalender.add(new CalenderSaham(calender.getString("title"), calender.getString("start").split("T")[0], calender.getString("description"), calender.getString("location")));
                        }

                        Collections.sort(arrCalender, new Comparator<CalenderSaham>() {
                            @Override
                            public int compare(CalenderSaham t1, CalenderSaham t2) {
                                return t1.getDate().compareTo(t2.getDate());
                            }
                        });

                        sharedPref.saveDataShared("calendersaham", arrCalender);
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                }

                return true;
            }
        });

        webView.loadUrl("https://idx.co.id/umbraco/Surface/Home/GetCalendar");
    }
}