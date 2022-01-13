package com.alvarenstudio.pasardanawatcher.ui.saham;

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

import com.alvarenstudio.pasardanawatcher.HttpHandler;
import com.alvarenstudio.pasardanawatcher.MainActivity;
import com.alvarenstudio.pasardanawatcher.R;
import com.alvarenstudio.pasardanawatcher.adapter.MainCardSahamAdapter;
import com.alvarenstudio.pasardanawatcher.model.MainCardSaham;
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

        new doItJsonSaham().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saham, container, false);
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
        String json = sharedPreferences.getString("maincardsaham", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<List<MainCardSaham>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        mMainCardSaham = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (mMainCardSaham == null) {
            // if the array list is empty
            // creating a new array list.
            mMainCardSaham = new ArrayList<>();
        }
        else {
            if(mMainCardSaham.size() > 0){
                mainCardSahamAdapter = new MainCardSahamAdapter(getContext(), mMainCardSaham);
                recyclerView.setAdapter(mainCardSahamAdapter);
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
                saveDataShared("maincardsaham", mMainCardSaham);

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
}