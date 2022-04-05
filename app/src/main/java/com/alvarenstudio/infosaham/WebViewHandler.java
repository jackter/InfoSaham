package com.alvarenstudio.infosaham;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alvarenstudio.infosaham.model.Emiten;
import com.alvarenstudio.infosaham.model.EmitenClosingPrice;
import com.alvarenstudio.infosaham.model.MChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class WebViewHandler {
    private static final String TAG = WebViewHandler.class.getSimpleName();
    private WebView webView;
    private WebViewClient webClient;
    private SharedPref sharedPref;
    private ArrayList<EmitenClosingPrice> emitenClosingPrices;
    private String emiten;

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
        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cmsg)
            {
                if (cmsg.message() != null) {
                    try {
                        emitenClosingPrices = sharedPref.loadDataSharedEmitenSaham("emitenclosingprices");

                        JSONObject jsonObject = new JSONObject(cmsg.message());

                        if(emitenClosingPrices.size() > 0) {
                            boolean find = false;
                            for(EmitenClosingPrice data: emitenClosingPrices) {
                                if(data.getEmiten().equals(emiten) && jsonObject.getLong("ClosingPrice") > 0) {
                                    find = true;
                                    data.setClosingPrice(jsonObject.getLong("ClosingPrice"));
                                }
                            }

                            if(!find && jsonObject.getLong("ClosingPrice") > 0) {
                                emitenClosingPrices.add(new EmitenClosingPrice(emiten, jsonObject.getLong("ClosingPrice"), 0L));
                            }
                        }
                        else {
                            if(jsonObject.getLong("ClosingPrice") > 0) {
                                emitenClosingPrices.add(new EmitenClosingPrice(emiten, jsonObject.getLong("ClosingPrice"), 0L));
                            }
                        }

                        sharedPref.saveDataShared("emitenclosingprices", emitenClosingPrices);

                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                }

                return true;
            }
        });
    }

    public void getPrice(String emiten, String URL) {
        webView.loadUrl(URL);
        this.emiten = emiten;
    }
}