package com.alvarenstudio.infosaham.ui.reksadana;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alvarenstudio.infosaham.HttpHandler;
import com.alvarenstudio.infosaham.MainActivity;
import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.SharedPref;
import com.alvarenstudio.infosaham.adapter.MainCardReksadanaAdapter;
import com.alvarenstudio.infosaham.adapter.MainCardSahamAdapter;
import com.alvarenstudio.infosaham.model.MainCardReksadana;
import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.alvarenstudio.infosaham.ui.saham.SahamFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReksadanaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReksadanaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types of parameters
    private RecyclerView recyclerView;
    private List<MainCardReksadana> mMainCardReksadana;
    private MainCardReksadanaAdapter mainCardReksadanaAdapter;
    private String TAG = ReksadanaFragment.class.getSimpleName();
    private int sort = 0;
    private int sortType = 0;
    private SharedPref sharedPref;

    public ReksadanaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReksadanaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReksadanaFragment newInstance(String param1, String param2) {
        ReksadanaFragment fragment = new ReksadanaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        ((MainActivity) getContext()).getFab(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogForm();
            }
        });

        ((MainActivity) getContext()).getMenuRefresh(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doItJsonReksadana().execute();
            }
        });

        ((MainActivity) getContext()).getMenuSort(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFormSort();
            }
        });

        new doItJsonReksadana().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reksadana, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sharedPref = new SharedPref(this.getContext());
        mMainCardReksadana = sharedPref.loadDataSharedMainCardReksadana("maincardreksadana");

        if(mMainCardReksadana.size() > 0){
            mainCardReksadanaAdapter = new MainCardReksadanaAdapter(getContext(), mMainCardReksadana);
            recyclerView.setAdapter(mainCardReksadanaAdapter);
        }
    }

    private class doItJsonReksadana extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpHandler httpHandler = new HttpHandler();

                String jsonurl = "https://pasardana.id/api/FundSearchResult/GetAll?sortField=Name&sortOrder=ASC";
                String jsonString = httpHandler.makeServiceCall(jsonurl);

                if (jsonString != null) {
                    try {
                        mMainCardReksadana = new ArrayList<>();
                        MainCardReksadana mainCardReksadana;
                        JSONArray listReksadana = new JSONArray(jsonString);

                        for(int i = 0; i < listReksadana.length(); i++){
                            JSONObject reksadana = listReksadana.getJSONObject(i);
                            mainCardReksadana = new MainCardReksadana();

                            mainCardReksadana.setId(reksadana.getInt("Id"));
                            mainCardReksadana.setName(reksadana.getString("Name"));
                            mainCardReksadana.setType(reksadana.getString("ConservativeCategory"));
                            mainCardReksadana.setCategory(reksadana.getString("Type"));
                            mainCardReksadana.setCur(reksadana.getInt("Currency"));

                            if(!reksadana.isNull("NetAssetValue")){
                                mainCardReksadana.setNav(reksadana.getDouble("NetAssetValue"));
                            }
                            else{
                                mainCardReksadana.setNav(0);
                            }

                            if(!reksadana.isNull("AssetUnderManagement")){
                                mainCardReksadana.setAum(reksadana.getDouble("AssetUnderManagement"));
                            }
                            else{
                                mainCardReksadana.setAum(0);
                            }

                            if(!reksadana.isNull("DailyReturn")){
                                mainCardReksadana.setOneday(reksadana.getDouble("DailyReturn"));
                            }
                            else{
                                mainCardReksadana.setOneday(0);
                            }

                            if(!reksadana.isNull("MtdReturn")){
                                mainCardReksadana.setMtd(reksadana.getDouble("MtdReturn"));
                            }
                            else{
                                mainCardReksadana.setMtd(0);
                            }

                            if(!reksadana.isNull("MonthlyReturn")){
                                mainCardReksadana.setOnemonth(reksadana.getDouble("MonthlyReturn"));
                            }
                            else{
                                mainCardReksadana.setOnemonth(0);
                            }

                            if(!reksadana.isNull("YtdReturn")){
                                mainCardReksadana.setYtd(reksadana.getDouble("YtdReturn"));
                            }
                            else{
                                mainCardReksadana.setYtd(0);
                            }

                            if(!reksadana.isNull("YearlyReturn")){
                                mainCardReksadana.setOneyear(reksadana.getDouble("YearlyReturn"));
                            }
                            else{
                                mainCardReksadana.setOneyear(0);
                            }

                            mMainCardReksadana.add(mainCardReksadana);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Gagal mengambil data json dari server.");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
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

            if(mMainCardReksadana != null){
                for(MainCardReksadana dataReksadana : mMainCardReksadana){
                    if(dataReksadana.getName().toLowerCase().contains("index") || dataReksadana.getName().toLowerCase().contains("etf")){
                        dataReksadana.setCategory("ETF, Index");
                    }
                    else if(dataReksadana.getName().toLowerCase().contains("syariah")){
                        dataReksadana.setCategory("Sharia");
                    }
                    else{
                        dataReksadana.setCategory("Konvensional");
                    }
                }

                sharedPref.saveDataShared("maincardreksadana", mMainCardReksadana);

                if(mMainCardReksadana.size() > 0){
                    mainCardReksadanaAdapter = new MainCardReksadanaAdapter(getContext(), mMainCardReksadana);
                    recyclerView.setAdapter(mainCardReksadanaAdapter);
                }
            }
        }
    }

    private void DialogForm() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.find_reksadana_dialog, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle("Cari Reksadana");

        SearchView svName = dialogView.findViewById(R.id.svName);
        SearchView svType = dialogView.findViewById(R.id.svType);
        SearchView svCategory = dialogView.findViewById(R.id.svCategory);
        SearchView svCurrency = dialogView.findViewById(R.id.svCurrency);

        dialog.setPositiveButton("Cari", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mMainCardReksadana.size() > 0){
                    List<MainCardReksadana> mMarketFilter = new ArrayList<>();
                    for(MainCardReksadana data : mMainCardReksadana){
                        String cur = "IDR";

                        if(data.getCur() == 1){
                            cur = "USD";
                        }

                        if((!svName.getQuery().toString().trim().isEmpty() && data.getName().toLowerCase().contains(svName.getQuery().toString().toLowerCase())) ||
                                (!svType.getQuery().toString().trim().isEmpty() && data.getType().toLowerCase().contains(svType.getQuery().toString().toLowerCase())) ||
                                (!svCategory.getQuery().toString().trim().isEmpty() && data.getCategory().toLowerCase().contains(svCategory.getQuery().toString().toLowerCase())) ||
                                (!svCurrency.getQuery().toString().trim().isEmpty() && cur.toLowerCase().contains(svCurrency.getQuery().toString().toLowerCase()))){
                            mMarketFilter.add(data);
                        }
                    }
                    mainCardReksadanaAdapter.setFilter(mMarketFilter);
                }

                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainCardReksadanaAdapter.setFilter(mMainCardReksadana);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void DialogFormSort() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
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
        dialogView.findViewById(R.id.radBatOpen).setVisibility(View.GONE);
        dialogView.findViewById(R.id.radBatFreq).setVisibility(View.GONE);

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
}