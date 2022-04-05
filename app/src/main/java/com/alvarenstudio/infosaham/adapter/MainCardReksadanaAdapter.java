package com.alvarenstudio.infosaham.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.ReksadanaDetailActivity;
import com.alvarenstudio.infosaham.SahamDetailActivity;
import com.alvarenstudio.infosaham.model.MainCardReksadana;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainCardReksadanaAdapter extends RecyclerView.Adapter<MainCardReksadanaAdapter.viewHolder>{
    private Context mContext;
    private List<MainCardReksadana> mMainCardReksadanas;

    public MainCardReksadanaAdapter(Context mContext, List<MainCardReksadana> mMainCardReksadanas) {
        this.mContext = mContext;
        this.mMainCardReksadanas = mMainCardReksadanas;
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        public TextView tvName, tvType, tvCategory, tvCur, tvNav, tvAum, tv1Day, tvMtd, tv1Month, tvYtd, tv1Year;
        public CardView cardReksadana, cardAdsGoogle;
        public NativeExpressAdView nativeAdView;
        public VideoController adVideoController;

        public viewHolder(View itemView) {
            super(itemView);

            cardReksadana = itemView.findViewById(R.id.card_reksadana);
            cardAdsGoogle = itemView.findViewById(R.id.card_ads_google);
            nativeAdView = itemView.findViewById(R.id.nativeAdView);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCur = itemView.findViewById(R.id.tvCur);
            tvNav = itemView.findViewById(R.id.tvNav);
            tvAum = itemView.findViewById(R.id.tvAum);

            tv1Day = itemView.findViewById(R.id.tv1day);
            tvMtd = itemView.findViewById(R.id.tvMtd);
            tv1Month = itemView.findViewById(R.id.tv1month);
            tvYtd = itemView.findViewById(R.id.tvYtd);
            tv1Year = itemView.findViewById(R.id.tv1year);
        }
    }

    @NonNull
    @Override
    public MainCardReksadanaAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_card_reksadana, parent, false);
        return new MainCardReksadanaAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
        MainCardReksadana mainCardReksadana = mMainCardReksadanas.get(position);

        if(position == 0 && loadPreferencesInt("show_ads") == 1) {
            holder.nativeAdView.setVideoOptions(new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build());

            holder.adVideoController = holder.nativeAdView.getVideoController();
            holder.adVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });

            holder.nativeAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    holder.cardAdsGoogle.setVisibility(View.VISIBLE);
                }
            });

            holder.nativeAdView.loadAd(new AdRequest.Builder().build());
        }

        holder.cardReksadana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReksadanaDetailActivity.class);
                intent.putExtra("mainCardReksadana", (Serializable) mainCardReksadana);
                mContext.startActivity(intent);
            }
        });

        holder.tvName.setText(mainCardReksadana.getName());
        holder.tvType.setText(mainCardReksadana.getType());
        holder.tvCategory.setText(mainCardReksadana.getCategory());
        if(mainCardReksadana.getCur() == 0){
            holder.tvCur.setText("IDR");
        }
        else{
            holder.tvCur.setText("USD");
        }
        holder.tvNav.setText(currency2Format(mainCardReksadana.getNav()));
        holder.tvAum.setText(currencyFormat(mainCardReksadana.getAum()));

        holder.tv1Day.setText(pctFormat(mainCardReksadana.getOneday()));
        holder.tvMtd.setText(pctFormat(mainCardReksadana.getMtd()));
        holder.tv1Month.setText(pctFormat(mainCardReksadana.getOnemonth()));
        holder.tvYtd.setText(pctFormat(mainCardReksadana.getYtd()));
        holder.tv1Year.setText(pctFormat(mainCardReksadana.getOneyear()));
    }

    @Override
    public int getItemCount() {
        return mMainCardReksadanas.size();
    }

    public void setFilter(List<MainCardReksadana> filterList){
        mMainCardReksadanas = new ArrayList<>();
        mMainCardReksadanas.addAll(filterList);
        notifyDataSetChanged();
    }

    public String pctFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount * 100) + "%";
    }

    public String currencyFormat(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    public String currency2Format(Double amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.0000");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    private int loadPreferencesInt(String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext.getApplicationContext());
        int value = sharedPreferences.getInt(key, 0);

        return value;
    }
}
