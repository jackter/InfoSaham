package com.alvarenstudio.infosaham;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.alvarenstudio.infosaham.adapter.CatatanSahamAdapter;
import com.alvarenstudio.infosaham.adapter.EmitenAdapter;
import com.alvarenstudio.infosaham.model.CatatanSaham;
import com.alvarenstudio.infosaham.model.Emiten;
import com.alvarenstudio.infosaham.model.EmitenClosingPrice;
import com.alvarenstudio.infosaham.model.EmitenHPP;
import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CatatanSahamActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CatatanSahamAdapter catatanAdapter;
    private List<CatatanSaham> mCatatan;
    private FirebaseUser fBaseUser;
    private ViewPager vpEmiten;
    private ProgressBar ivLoading;
    private String TAG = CatatanSahamActivity.class.getSimpleName();
    private TextView tvSetoran, tvPenarikan, tvAsset, tvPotensi, tvPotensiPct;
    private List<MainCardSaham> mMainCardSaham;
    private SearchView searchView;
    private SharedPref sharedPref;
    private SwipeRefreshLayout refreshLayout;
    private List<EmitenHPP> mEmitenHPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catatan_saham);

        getSupportActionBar().setTitle("Catatan Saham");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatatanSahamActivity.this, AddCatatSahamActivity.class);
                startActivity(intent);
            }
        });

        refreshLayout = findViewById(R.id.swipe_refresh_catatan_saham);
        ivLoading = findViewById(R.id.ivLoading);
        recyclerView = findViewById(R.id.recycler_view);
        tvSetoran = findViewById(R.id.tvTotalSetoran);
        tvPenarikan = findViewById(R.id.tvTotalPenarikan);
        tvAsset = findViewById(R.id.tvTotalAsset);
        tvPotensi = findViewById(R.id.tvTotalPotensi);
        tvPotensiPct = findViewById(R.id.tvTotalPotensiPct);
        vpEmiten = findViewById(R.id.vpEmiten);

        fBaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        sharedPref = new SharedPref(getApplicationContext());

        mMainCardSaham = sharedPref.loadDataSharedMainCardSaham("maincardsaham");

        vpEmiten.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(refreshLayout != null && !refreshLayout.isRefreshing()) {
                    refreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
                }
            }
        });

        ivLoading.setVisibility(View.VISIBLE);
        catatanListFirestore();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        catatanListFirestore();
                        refreshLayout.setRefreshing(false);
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                    }
                }, 1000);
            }
        });
    }

    public void catatanListFirestore() {
        mCatatan = new ArrayList<>();
        mEmitenHPP = new ArrayList<EmitenHPP>();

        FirebaseFirestore
                .getInstance()
                .collection("catatan_saham")
                .whereEqualTo("userid", fBaseUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshots.isEmpty()) {
                            mCatatan.clear();
                            mEmitenHPP.clear();

                            tvSetoran.setText("IDR 0,00");
                            tvPenarikan.setText("IDR 0,00");
                            tvAsset.setText("IDR 0,00");
                            tvPotensi.setText("IDR 0,00");

                            catatanAdapter = new CatatanSahamAdapter(CatatanSahamActivity.this, mCatatan);
                            recyclerView.setAdapter(catatanAdapter);

                            return;
                        } else {
                            mCatatan.clear();
                            mEmitenHPP.clear();

                            ivLoading.setVisibility(View.GONE);

                            for (QueryDocumentSnapshot doc : documentSnapshots) {
                                CatatanSaham catatan = new CatatanSaham();
                                catatan.setId(doc.getId());
                                catatan.setUserid(doc.get("userid").toString());
                                catatan.setType(doc.get("type").toString());
                                catatan.setEmiten(doc.get("emiten").toString());
                                catatan.setJmlSaham((Long) doc.get("jmlSaham"));
                                catatan.setHargaSaham((Long) doc.get("hargaSaham"));
                                catatan.setNilaiSaham((Long) doc.get("nilaiSaham"));
                                catatan.setFeeTrx((Long) doc.get("feeTrx"));
                                catatan.setTglTrx((Long) doc.get("tglTrx"));

                                if(catatan.getType().equals("Deposit")) {
                                    catatan.setHirarki(0);
                                }
                                else if(catatan.getType().equals("Buy")) {
                                    catatan.setHirarki(1);
                                }
                                else if(catatan.getType().equals("Sell")) {
                                    catatan.setHirarki(2);
                                }
                                else if(catatan.getType().equals("Withdraw")) {
                                    catatan.setHirarki(3);
                                }
                                else{
                                    catatan.setHirarki(0);
                                }

                                mCatatan.add(catatan);
                            }

                            Collections.sort(mCatatan, new Comparator<CatatanSaham>() {
                                @Override
                                public int compare(CatatanSaham t1, CatatanSaham t2) {
                                    return t2.getHirarki() - t1.getHirarki();
                                }
                            });

                            Collections.sort(mCatatan, new Comparator<CatatanSaham>() {
                                @Override
                                public int compare(CatatanSaham t1, CatatanSaham t2) {
                                    return t1.getTglTrx().compareTo(t2.getTglTrx());
                                }
                            });

                            Long vSetoran = 0L;
                            Long vPenarikan = 0L;
                            Long vAsset = 0L;

                            ArrayList<String> arrStringEmiten = new ArrayList<>();

                            for(CatatanSaham data : mCatatan){
                                if((data.getType().equals("Buy") || data.getType().equals("Sell")) && !arrStringEmiten.contains(data.getEmiten())) {
                                    arrStringEmiten.add(data.getEmiten());
                                }

                                if(data.getType().equals("Deposit")) {
                                    vSetoran += data.getNilaiSaham();
                                }

                                if(data.getType().equals("Withdraw")) {
                                    vPenarikan += data.getNilaiSaham();
                                }

                                if(data.getType().equals("Buy") || data.getType().equals("Withdraw")) {
                                    vAsset -= (data.getNilaiSaham() + data.getFeeTrx());
                                }
                                else {
                                    vAsset += (data.getNilaiSaham() - data.getFeeTrx());
                                }
                            }

                            ArrayList<Emiten> arrEmiten = new ArrayList<>();
                            ArrayList<Emiten> arrShowEmiten = new ArrayList<>();
                            for(String emit:arrStringEmiten) {
                                Long openingStock = 0L;
                                Long closingStock = 0L;
                                Long openingPrice = 0L;
                                Long closingPrice = 0L;
                                Long lastPrice = 0L;

                                for(CatatanSaham data : mCatatan){
                                    if(data.getEmiten().equals(emit)) {
                                        openingStock = closingStock;
                                        openingPrice = closingPrice;
                                        Long curPrice = 0L;
                                        Long newPrice = 0L;

                                        if(data.getType().equals("Buy")) {
                                            curPrice = openingStock * openingPrice;
                                            newPrice = data.getJmlSaham() * data.getHargaSaham();
                                            closingStock = openingStock + data.getJmlSaham();
                                            if(closingStock != 0) {
                                                closingPrice = (curPrice + newPrice) / closingStock;
                                            }
                                        }
                                        else if(data.getType().equals("Sell")){
                                            closingStock = openingStock + (data.getJmlSaham() * -1);
                                            closingPrice = openingPrice;
                                        }
                                    }
                                }

                                for(MainCardSaham data : mMainCardSaham) {
                                    if(data.getCode().equals(emit)) {
                                        lastPrice = (long)data.getLast();
                                        break;
                                    }
                                }

                                for(EmitenClosingPrice detail: sharedPref.loadDataSharedEmitenSaham("emitenclosingprices")) {
                                    if(emit.equals(detail.getEmiten())) {
                                        lastPrice = detail.getClosingPrice();
                                        break;
                                    }
                                }

                                arrEmiten.add(new Emiten(emit, closingStock, closingPrice, lastPrice));
                            }

                            WebViewHandler webViewHandler;

                            for(Emiten emit:arrEmiten) {
                                vAsset += (emit.getQty() * emit.getLastPrice());

                                if(emit.getQty() > 0) {
                                    arrShowEmiten.add(emit);

                                    webViewHandler = new WebViewHandler(getApplicationContext());
                                    webViewHandler.getPrice(emit.getEmiten(), "https://idx.co.id/umbraco/Surface/ListedCompany/GetTradingInfoDaily?code=" + emit.getEmiten() + "&language=id-id");
                                }
                            }

                            for(Emiten emit:arrShowEmiten) {
                                for(EmitenClosingPrice data: sharedPref.loadDataSharedEmitenSaham("emitenclosingprices")) {
                                    if(emit.getEmiten().equals(data.getEmiten())) {
                                        emit.setLastPrice(data.getClosingPrice());
                                    }
                                }
                            }

                            if(arrShowEmiten.size() > 0) {
                                EmitenAdapter emitenAdapter = new EmitenAdapter(arrShowEmiten, getApplicationContext());
                                vpEmiten.setClipToPadding(false);
                                vpEmiten.setPadding(0, 0, 600, 0);
                                vpEmiten.setAdapter(emitenAdapter);
                                vpEmiten.setVisibility(View.VISIBLE);
                            }
                            else {
                                vpEmiten.setVisibility(View.GONE);
                            }

                            tvSetoran.setText(currencyFormat(vSetoran.toString(), "IDR"));
                            tvPenarikan.setText(currencyFormat(vPenarikan.toString(), "IDR"));
                            tvAsset.setText(currencyFormat(vAsset.toString(), "IDR"));
                            tvPotensi.setText(currencyFormat(String.valueOf(vAsset - (vSetoran - vPenarikan)), "IDR"));
                            tvPotensiPct.setText(currencyFormat(String.valueOf((float)(vAsset + vPenarikan - vSetoran) / (float) vSetoran * 100), "") + "%");

                            if(vAsset + vPenarikan - vSetoran < 0) {
                                tvPotensi.setTextColor(Color.RED);
                                tvPotensiPct.setTextColor(Color.RED);
                            }
                            else {
                                tvPotensi.setTextColor(Color.parseColor("#5abd96"));
                                tvPotensiPct.setTextColor(Color.parseColor("#5abd96"));
                            }

                            Collections.sort(mCatatan, new Comparator<CatatanSaham>() {
                                @Override
                                public int compare(CatatanSaham t1, CatatanSaham t2) {
                                    return t2.getTglTrx().compareTo(t1.getTglTrx());
                                }
                            });

                            catatanAdapter = new CatatanSahamAdapter(CatatanSahamActivity.this, mCatatan);
                            recyclerView.setAdapter(catatanAdapter);
                        }
                    }
                });
    }

    public String currencyFormat(String amount, String curr) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        if(curr.equals("")) {
            return formatter.format(Double.parseDouble(amount)).replace(".", "x").replace(",", ".").replace("x", ",");
        }
        else {
            return curr + " " + formatter.format(Double.parseDouble(amount)).replace(".", "x").replace(",", ".").replace("x", ",");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String nextText) {
                //Data akan berubah saat user menginputkan text/kata kunci pada SearchView
                if(mCatatan.size() > 0){
                    nextText = nextText.toLowerCase();
                    List<CatatanSaham> mCatatanFilter = new ArrayList<>();
                    for(CatatanSaham data : mCatatan){
                        String emiten = data.getEmiten().toLowerCase();
                        if(emiten.contains(nextText)){
                            mCatatanFilter.add(data);
                        }
                    }
                    catatanAdapter.setFilter(mCatatanFilter);
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
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