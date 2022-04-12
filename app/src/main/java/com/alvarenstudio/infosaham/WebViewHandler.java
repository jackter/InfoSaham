package com.alvarenstudio.infosaham;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.alvarenstudio.infosaham.model.EmitenClosingPrice;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        Date parsedDate = dateFormat.parse(jsonObject.getString("DTCreate").split("T")[0] + " " + jsonObject.getString("DTCreate").split("T")[1]);

                        if(emitenClosingPrices.size() > 0) {
                            boolean find = false;
                            for(EmitenClosingPrice data: emitenClosingPrices) {
                                if(data.getEmiten().equals(emiten) && jsonObject.getLong("ClosingPrice") > 0 && parsedDate.getTime() > data.getDate()) {
                                    find = true;
                                    data.setClosingPrice(jsonObject.getLong("ClosingPrice"));
                                    data.setDate(parsedDate.getTime());
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
    }

    public void getPrice(String emiten, String URL) {
        webView.loadUrl(URL);
        this.emiten = emiten;
    }
}