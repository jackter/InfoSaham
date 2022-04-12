package com.alvarenstudio.infosaham.ui.saham;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alvarenstudio.infosaham.HttpHandler;
import com.alvarenstudio.infosaham.MainActivity;
import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.SharedPref;
import com.alvarenstudio.infosaham.adapter.MainCardSahamAdapter;
import com.alvarenstudio.infosaham.model.MChart;
import com.alvarenstudio.infosaham.model.MainCardSaham;
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
 * Use the {@link SahamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SahamFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types of parameters
    private RecyclerView recyclerView;
    private List<MainCardSaham> mMainCardSaham;
    private MainCardSahamAdapter mainCardSahamAdapter;
    private String TAG = SahamFragment.class.getSimpleName();
    private int sort = 0;
    private int sortType = 0;
    private SharedPref sharedPref;

    public SahamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SahamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SahamFragment newInstance(String param1, String param2) {
        SahamFragment fragment = new SahamFragment();
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

        ((MainActivity) getContext()).getFab(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogForm();
            }
        });

        ((MainActivity) getContext()).getMenuRefresh(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doItJsonSaham().execute();
            }
        });

        ((MainActivity) getContext()).getMenuSort(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFormSort();
            }
        });

        new doItJsonSaham().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_saham, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sharedPref = new SharedPref(this.getContext());
        mMainCardSaham = sharedPref.loadDataSharedMainCardSaham("maincardsaham");

        if(mMainCardSaham.size() > 0){
            mainCardSahamAdapter = new MainCardSahamAdapter(getContext(), mMainCardSaham);
            recyclerView.setAdapter(mainCardSahamAdapter);
        }
    }

    private class doItJsonSaham extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpHandler httpHandler = new HttpHandler();

                String jsonurl = "https://pasardana.id/api/StockSearchResult/GetAll";
                String jsonString = httpHandler.makeServiceCall(jsonurl);
                if (jsonString != null) {
                    try {
                        mMainCardSaham = new ArrayList<>();
                        MainCardSaham mainCardSaham;
                        JSONArray listSaham = new JSONArray(jsonString);

                        for(int i = 0; i < listSaham.length(); i++){
                            JSONObject saham = listSaham.getJSONObject(i);
                            mainCardSaham = new MainCardSaham();

                            mainCardSaham.setId(saham.getInt("Id"));
                            mainCardSaham.setName(saham.getString("Name"));
                            mainCardSaham.setCode(saham.getString("Code"));
                            mainCardSaham.setSectore(saham.getString("NewSectorName"));
                            mainCardSaham.setSubindustry(saham.getString("NewSubIndustryName"));

                            if(!saham.isNull("AdjustedClosingPrice")){
                                mainCardSaham.setLast(saham.getDouble("AdjustedClosingPrice"));
                            }
                            else{
                                mainCardSaham.setLast(0);
                            }

                            if(!saham.isNull("AdjustedOpenPrice")){
                                mainCardSaham.setPrev(saham.getDouble("AdjustedOpenPrice"));
                            }
                            else{
                                mainCardSaham.setPrev(0);
                            }

                            if(!saham.isNull("AdjustedOpenPrice")){
                                mainCardSaham.setOpen(saham.getDouble("AdjustedOpenPrice"));
                            }
                            else{
                                mainCardSaham.setOpen(0);
                            }

                            if(!saham.isNull("AdjustedHighPrice")){
                                mainCardSaham.setHigh(saham.getDouble("AdjustedHighPrice"));
                            }
                            else{
                                mainCardSaham.setHigh(0);
                            }

                            if(!saham.isNull("AdjustedLowPrice")){
                                mainCardSaham.setLow(saham.getDouble("AdjustedLowPrice"));
                            }
                            else{
                                mainCardSaham.setLow(0);
                            }

                            if(!saham.isNull("Frequency")){
                                mainCardSaham.setFreq(saham.getDouble("Frequency"));
                            }
                            else{
                                mainCardSaham.setFreq(0);
                            }

                            if(!saham.isNull("Per")){
                                mainCardSaham.setPer(saham.getDouble("Per"));
                            }
                            else{
                                mainCardSaham.setPer(0);
                            }

                            if(!saham.isNull("Pbr")){
                                mainCardSaham.setPbv(saham.getDouble("Pbr"));
                            }
                            else{
                                mainCardSaham.setPbv(0);
                            }

                            if(!saham.isNull("Volume")){
                                mainCardSaham.setVol(saham.getDouble("Volume"));
                            }
                            else{
                                mainCardSaham.setVol(0);
                            }

                            if(!saham.isNull("Value")){
                                mainCardSaham.setVal(saham.getDouble("Value"));
                            }
                            else{
                                mainCardSaham.setVal(0);
                            }


                            if(!saham.isNull("OneDay")){
                                mainCardSaham.setOneday(saham.getDouble("OneDay"));
                            }
                            else{
                                mainCardSaham.setOneday(0);
                            }

                            if(!saham.isNull("OneMonth")){
                                mainCardSaham.setOnemonth(saham.getDouble("OneMonth"));
                            }
                            else{
                                mainCardSaham.setOnemonth(0);
                            }

                            if(!saham.isNull("Ytd")){
                                mainCardSaham.setYtd(saham.getDouble("Ytd"));
                            }
                            else{
                                mainCardSaham.setYtd(0);
                            }

                            if(!saham.isNull("OneYear")){
                                mainCardSaham.setOneyear(saham.getDouble("OneYear"));
                            }
                            else{
                                mainCardSaham.setOneyear(0);
                            }

                            if(!saham.isNull("Capitalization")){
                                mainCardSaham.setCap(saham.getDouble("Capitalization"));
                            }
                            else{
                                mainCardSaham.setCap(0);
                            }

                            mMainCardSaham.add(mainCardSaham);
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

            if(mMainCardSaham != null){
                sharedPref.saveDataShared("maincardsaham", mMainCardSaham);

                if(mMainCardSaham.size() > 0){
                    mainCardSahamAdapter = new MainCardSahamAdapter(getContext(), mMainCardSaham);
                    recyclerView.setAdapter(mainCardSahamAdapter);
                }
            }
        }
    }

    private void DialogForm() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.find_saham_dialog, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle("Cari Saham");

        SearchView svName = dialogView.findViewById(R.id.svName);
        SearchView svCode = dialogView.findViewById(R.id.svCode);
        SearchView svSectore = dialogView.findViewById(R.id.svSectore);
        SearchView svSubIndustry = dialogView.findViewById(R.id.svSubIndustry);

        dialog.setPositiveButton("Cari", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mMainCardSaham.size() > 0){
                    List<MainCardSaham> mMarketFilter = new ArrayList<>();
                    for(MainCardSaham data : mMainCardSaham){
                        if((!svName.getQuery().toString().trim().isEmpty() && data.getName().toLowerCase().contains(svName.getQuery().toString().toLowerCase())) ||
                                (!svCode.getQuery().toString().trim().isEmpty() && data.getCode().toLowerCase().contains(svCode.getQuery().toString().toLowerCase())) ||
                                (!svSectore.getQuery().toString().trim().isEmpty() && data.getSectore().toLowerCase().contains(svSectore.getQuery().toString().toLowerCase())) ||
                                (!svSubIndustry.getQuery().toString().trim().isEmpty() && data.getSubindustry().toLowerCase().contains(svSubIndustry.getQuery().toString().toLowerCase()))){
                            mMarketFilter.add(data);
                        }
                    }
                    mainCardSahamAdapter.setFilter(mMarketFilter);
                }

                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainCardSahamAdapter.setFilter(mMainCardSaham);

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
                if (sort == 0) {
                    if(sortType == 0) {
                        return t1.getCode().compareTo(t2.getCode());
                    }
                    else {
                        return t2.getCode().compareTo(t1.getCode());
                    }
                }
                else if(sort == 1) {
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
                else if(sort == 4) {
                    if(sortType == 0) {
                        return Double.compare(t1.getOpen(), t2.getOpen());
                    }
                    else {
                        return Double.compare(t2.getOpen(), t1.getOpen());
                    }
                }
                else {
                    if(sortType == 0) {
                        return Double.compare(t1.getFreq(), t2.getFreq());
                    }
                    else {
                        return Double.compare(t2.getFreq(), t1.getFreq());
                    }
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.right_menu, menu) ;
    }

}