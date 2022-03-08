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
import android.widget.Toast;

import com.alvarenstudio.infosaham.HttpHandler;
import com.alvarenstudio.infosaham.MainActivity;
import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.adapter.MainCardReksadanaAdapter;
import com.alvarenstudio.infosaham.model.MainCardReksadana;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

        this.loadDataShared();
    }

    private void loadDataShared() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("maincardreksadana", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<List<MainCardReksadana>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardReksadana = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mMainCardReksadana == null) {
            // if the array list is empty
            // creating a new array list.
            mMainCardReksadana = new ArrayList<>();
        }
        else {
            if(mMainCardReksadana.size() > 0){
                mainCardReksadanaAdapter = new MainCardReksadanaAdapter(getContext(), mMainCardReksadana);
                recyclerView.setAdapter(mainCardReksadanaAdapter);
            }
        }
    }

    private void saveDataShared(String nama, Object card) {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

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
                for(MainCardReksadana data : mMainCardReksadana){
                    if(data.getName().toLowerCase().contains("index") || data.getName().toLowerCase().contains("etf")){
                        data.setCategory("ETF, Index");
                    }
                    else if(data.getName().toLowerCase().contains("syariah")){
                        data.setCategory("Sharia");
                    }
                    else{
                        data.setCategory("Konvensional");
                    }
                }

                saveDataShared("maincardreksadana", mMainCardReksadana);

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
}