package com.alvarenstudio.infosaham.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alvarenstudio.infosaham.CatatanSahamActivity;
import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.model.CatatanSaham;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CatatanSahamAdapter extends RecyclerView.Adapter<CatatanSahamAdapter.viewHolder>{
    private Context mContext;
    private List<CatatanSaham> mCatatanSaham;
    private String TAG = CatatanSahamAdapter.class.getSimpleName();

    public CatatanSahamAdapter(Context mContext, List<CatatanSaham> mCatatanSaham) {
        this.mContext = mContext;
        this.mCatatanSaham = mCatatanSaham;
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public TextView tvType, tvEmiten, tvKuantitas, tvQty, tvHargaLbr, tvHarga, tvFeeTrx, tvFee, tvTotal, tvTglTrx;
        public CardView cardCatatanSaham;

        public viewHolder(View itemView) {
            super(itemView);

            cardCatatanSaham = itemView.findViewById(R.id.cardCatatanSaham);
            tvType = itemView.findViewById(R.id.tvType);
            tvEmiten = itemView.findViewById(R.id.tvEmiten);
            tvKuantitas = itemView.findViewById(R.id.tvKuantitas);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvHargaLbr = itemView.findViewById(R.id.tvHargaLbr);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvFeeTrx = itemView.findViewById(R.id.tvFeeTrx);
            tvFee = itemView.findViewById(R.id.tvFee);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvTglTrx = itemView.findViewById(R.id.tvTglTrx);
        }

    }

    @NonNull
    @Override
    public CatatanSahamAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.catatan_saham_item, parent, false);
        return new CatatanSahamAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        CatatanSaham catatan = mCatatanSaham.get(position);

        holder.tvType.setText("Type : " + catatan.getType());
        holder.tvEmiten.setText(catatan.getEmiten());
        holder.tvQty.setText(numberFormat(String.valueOf(catatan.getJmlSaham())));
        holder.tvHarga.setText(currencyFormat(String.valueOf(catatan.getHargaSaham()), "IDR"));
        holder.tvFee.setText(currencyFormat(String.valueOf(catatan.getFeeTrx()), "IDR"));
        holder.tvTotal.setText(currencyFormat(String.valueOf(catatan.getNilaiSaham()), "IDR"));
        holder.tvTglTrx.setText(timestampToDT(catatan.getTglTrx()));

        if(catatan.getType().equals("Deposit") || catatan.getType().equals("Withdraw")) {
            holder.tvKuantitas.setVisibility(View.GONE);
            holder.tvQty.setVisibility(View.GONE);
            holder.tvHarga.setVisibility(View.GONE);
            holder.tvHargaLbr.setVisibility(View.GONE);
            holder.tvFee.setVisibility(View.GONE);
            holder.tvFeeTrx.setVisibility(View.GONE);
        }
        else if(catatan.getType().equals("Dividen")) {
            holder.tvFee.setVisibility(View.GONE);
            holder.tvFeeTrx.setVisibility(View.GONE);
        }
        else {
            holder.tvKuantitas.setVisibility(View.VISIBLE);
            holder.tvQty.setVisibility(View.VISIBLE);
            holder.tvHarga.setVisibility(View.VISIBLE);
            holder.tvHargaLbr.setVisibility(View.VISIBLE);
            holder.tvFee.setVisibility(View.VISIBLE);
            holder.tvFeeTrx.setVisibility(View.VISIBLE);
        }

        holder.cardCatatanSaham.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder((CatatanSahamActivity) mContext);
                alert.setTitle("Info Saham");
                alert.setMessage("Apakah anda yakin ingin menghapus catatan ini?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        removeCatatanFirestore(catatan.getId());
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
                return false;
            }
        });
    }

    private String loadPreferencesString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        String value = sharedPreferences.getString(key, "");

        return value;
    }

    private void removeCatatanFirestore(String rowid){
        FirebaseFirestore
                .getInstance()
                .collection("catatan_saham")
                .document(rowid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private String timestampToDT(Long timestamp) {
        long yourmilliseconds = timestamp;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Date resultdate = new Date(yourmilliseconds);

        return sdf.format(resultdate).toString();
    }

    public String currencyFormat(String amount, String curr) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return curr + " " + formatter.format(Double.parseDouble(amount)).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    public String numberFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("#,###,###,###");
        return formatter.format(Double.parseDouble(amount)).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    @Override
    public int getItemCount() {
        return mCatatanSaham.size();
    }

    public void setFilter(List<CatatanSaham> filterList){
        mCatatanSaham = new ArrayList<>();
        mCatatanSaham.addAll(filterList);
        notifyDataSetChanged();
    }
}
