package com.alvarenstudio.infosaham;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alvarenstudio.infosaham.model.MainCardSaham;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msa.dateedittext.DateEditText;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddCatatSahamActivity extends AppCompatActivity {

    private List<MainCardSaham> mMainCardSaham;
    private List arrayEmiten = new ArrayList<>();
    private EditText etJml, etHarga, etFee, etTotal;
    private TextView tvJml, tvHarga, tvFee, tvEmiten;
    private Button btnSimpan;
    private DateEditText etTglTrx;
    private Spinner spinType, spinEmiten;
    private DatePickerDialog.OnDateSetListener dateTrx;
    private Calendar myCalendar;
    private Long vTglTrx;
    private String TAG = AddCatatSahamActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_catat_saham);

        tvEmiten = findViewById(R.id.tvEmiten);
        tvJml = findViewById(R.id.tvJml);
        tvHarga = findViewById(R.id.tvHarga);
        tvFee = findViewById(R.id.tvFee);
        etJml = findViewById(R.id.etJml);
        etHarga = findViewById(R.id.etHarga);
        etFee = findViewById(R.id.etFee);
        etTotal = findViewById(R.id.etTotal);
        etTglTrx = findViewById(R.id.etTglTrx);
        spinType = findViewById(R.id.spinType);
        spinEmiten = findViewById(R.id.spinEmiten);
        btnSimpan = findViewById(R.id.btnSimpan);

        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etJml.setText("");
                etHarga.setText("");
                etTotal.setText("");

                if(spinType.getSelectedItem().toString().equals("Deposit") || spinType.getSelectedItem().toString().equals("Withdraw")) {
                    tvJml.setVisibility(View.GONE);
                    tvHarga.setVisibility(View.GONE);
                    tvFee.setVisibility(View.GONE);
                    tvEmiten.setVisibility(View.GONE);
                    etJml.setVisibility(View.GONE);
                    etHarga.setVisibility(View.GONE);
                    etFee.setVisibility(View.GONE);
                    spinEmiten.setVisibility(View.GONE);
                    etTotal.setEnabled(true);
                }
                else {
                    if(spinType.getSelectedItem().toString().equals("Buy") || spinType.getSelectedItem().toString().equals("Sell")) {
                        tvFee.setVisibility(View.VISIBLE);
                        etFee.setVisibility(View.VISIBLE);
                    }

                    tvJml.setVisibility(View.VISIBLE);
                    tvHarga.setVisibility(View.VISIBLE);
                    tvEmiten.setVisibility(View.VISIBLE);
                    etJml.setVisibility(View.VISIBLE);
                    etHarga.setVisibility(View.VISIBLE);
                    spinEmiten.setVisibility(View.VISIBLE);
                    etTotal.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etJml.addTextChangedListener(new NumberTextWatcher(etJml));
        etHarga.addTextChangedListener(new NumberTextWatcher(etHarga));
        etFee.addTextChangedListener(new NumberTextWatcher(etFee));
        etTotal.addTextChangedListener(new NumberTotalTextWatcher(etTotal));

        loadDataShared();
        tglTrx();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSimpan.setEnabled(false);
                if(!spinType.getSelectedItem().toString().equals("Deposit") && !spinType.getSelectedItem().toString().equals("Withdraw")) {
                    if (etJml.getText().toString().trim().isEmpty() || etJml.getText().toString().equals("0")) {
                        etJml.setError("Masukkan jumlah saham yang valid");
                        etJml.requestFocus();
                        return;
                    }

                    if (etHarga.getText().toString().trim().isEmpty() || etHarga.getText().toString().equals("0")) {
                        etHarga.setError("Masukkan harga saham yang valid");
                        etHarga.requestFocus();
                        return;
                    }

                    if(spinType.getSelectedItem().toString().equals("Buy") || spinType.getSelectedItem().toString().equals("Sell")) {
                        if (etFee.getText().toString().trim().isEmpty()) {
                            etFee.setError("Masukkan jumlah saham yang valid");
                            etFee.requestFocus();
                            return;
                        }
                    }
                }
                else{
                    if (etTotal.getText().toString().trim().isEmpty() || etTotal.getText().toString().equals("0")) {
                        etTotal.setError("Masukkan nilai yang valid");
                        etTotal.requestFocus();
                        return;
                    }
                }

                if(vTglTrx == null){
                    Toast.makeText(getApplicationContext(), "Masukkan tanggal transaksi yang valid",  Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseUser fbaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert fbaseUser != null;
                String userID = fbaseUser.getUid();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userid", userID);
                hashMap.put("type", spinType.getSelectedItem().toString());

                if(spinType.getSelectedItem().toString().equals("Deposit") || spinType.getSelectedItem().toString().equals("Withdraw")){
                    hashMap.put("emiten", "RDN");
                }
                else{
                    hashMap.put("emiten", spinEmiten.getSelectedItem().toString());
                }

                if(etJml.getText().toString().replace(",", "").equals("")) {
                    hashMap.put("jmlSaham", 0);
                }
                else {
                    hashMap.put("jmlSaham", Long.parseLong(etJml.getText().toString().replace(",", "")));
                }

                if(etHarga.getText().toString().replace(",", "").equals("")) {
                    hashMap.put("hargaSaham", 0);
                }
                else {
                    hashMap.put("hargaSaham", Long.parseLong(etHarga.getText().toString().replace(",", "")));
                }

                if(etFee.getText().toString().replace(",", "").equals("")) {
                    hashMap.put("feeTrx", 0);
                }
                else {
                    hashMap.put("feeTrx", Long.parseLong(etFee.getText().toString().replace(",", "")));
                }

                hashMap.put("nilaiSaham", Long.parseLong(etTotal.getText().toString().replace(",", "")));
                hashMap.put("tglTrx", vTglTrx);

                FirebaseFirestore
                        .getInstance()
                        .collection("catatan_saham")
                        .add(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                finish();
                                btnSimpan.setEnabled(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                btnSimpan.setEnabled(true);
                            }
                        });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class NumberTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public NumberTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                EditText editText = editTextWeakReference.get();
                if (editText == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                editText.removeTextChangedListener(this);

                Long longval;
                if (s.contains(",")) {
                    s = s.replaceAll(",", "");
                }
                longval = Long.parseLong(s);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longval);

                //setting text after format to EditText
                editText.setText(formattedString);
                editText.setSelection(editText.getText().length());
                editText.addTextChangedListener(this);

                if(Integer.parseInt(etJml.getText().toString().replace(",", "")) > 0 && Integer.parseInt(etHarga.getText().toString().replace(",", "")) > 0) {
                    etTotal.setText(String.valueOf(Integer.parseInt(etJml.getText().toString().replace(",", "")) * Integer.parseInt(etHarga.getText().toString().replace(",", ""))));
                }
            }
            catch (Exception e){

            }
        }
    }

    public class NumberTotalTextWatcher implements TextWatcher {
        private final WeakReference<EditText> editTextWeakReference;

        public NumberTotalTextWatcher(EditText editText) {
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                EditText editText = editTextWeakReference.get();
                if (editText == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                editText.removeTextChangedListener(this);

                Long longval;
                if (s.contains(",")) {
                    s = s.replaceAll(",", "");
                }
                longval = Long.parseLong(s);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longval);

                //setting text after format to EditText
                editText.setText(formattedString);
                editText.setSelection(editText.getText().length());
                editText.addTextChangedListener(this);
            }
            catch (Exception e){

            }
        }
    }

    private void tglTrx() {
        etTglTrx.listen();
        myCalendar = Calendar.getInstance();
        dateTrx = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabelTgl();
            }
        };
        etTglTrx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddCatatSahamActivity.this, dateTrx, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabelTgl() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etTglTrx.setText(sdf.format(myCalendar.getTime()));

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parsedDateF = dateFormat.parse(etTglTrx.getText().toString() + " 09:00:00");
            vTglTrx = parsedDateF.getTime();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataShared() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);

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
                for(MainCardSaham data: mMainCardSaham) {
                    arrayEmiten.add(data.getCode());
                }
            }
        }

        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, arrayEmiten);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinEmiten.setAdapter(adapter);
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