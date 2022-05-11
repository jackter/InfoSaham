package com.alvarenstudio.infosaham.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.SahamDetailActivity;
import com.alvarenstudio.infosaham.model.MainCardSaham;
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

public class MainCardSahamAdapter extends RecyclerView.Adapter<MainCardSahamAdapter.viewHolder>{
    private Context mContext;
    private List<MainCardSaham> mMainCardSahams;

    public MainCardSahamAdapter(Context mContext, List<MainCardSaham> mMainCardSahams) {
        this.mContext = mContext;
        this.mMainCardSahams = mMainCardSahams;
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        public TextView tvName, tvSectore, tvSubIndustry, tvLast, tvPrev, tvOpen, tvFreq, tvHigh, tvLow, tvPER, tvPBV, tvVol, tvVal, tv1Day, tv1Month, tvYtd, tv1Year, tvCap;
        public CardView cardSaham, cardAdsGoogle;
        public LinearLayout linLayoutMC;
        public NativeExpressAdView nativeAdView;
        public VideoController adVideoController;

        public viewHolder(View itemView) {
            super(itemView);

            linLayoutMC = itemView.findViewById(R.id.linLayoutMC);
            cardSaham = itemView.findViewById(R.id.card_saham);
            cardAdsGoogle = itemView.findViewById(R.id.card_ads_google);
            nativeAdView = itemView.findViewById(R.id.nativeAdView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSectore = itemView.findViewById(R.id.tvSectore);
            tvSubIndustry = itemView.findViewById(R.id.tvSubIndustry);
            tvLast = itemView.findViewById(R.id.tvLast);
            tvPrev = itemView.findViewById(R.id.tvPrev);
            tvOpen = itemView.findViewById(R.id.tvOpen);
            tvFreq = itemView.findViewById(R.id.tvFreq);
            tvHigh = itemView.findViewById(R.id.tvHigh);
            tvLow = itemView.findViewById(R.id.tvLow);
            tvPER = itemView.findViewById(R.id.tvPER);
            tvPBV = itemView.findViewById(R.id.tvPBV);
            tvVol = itemView.findViewById(R.id.tvVol);
            tvVal = itemView.findViewById(R.id.tvVal);
            tv1Day = itemView.findViewById(R.id.tv1day);
            tv1Month = itemView.findViewById(R.id.tv1month);
            tvYtd = itemView.findViewById(R.id.tvYtd);
            tv1Year = itemView.findViewById(R.id.tv1year);
            tvCap = itemView.findViewById(R.id.tvCap);
        }
    }

    @NonNull
    @Override
    public MainCardSahamAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_card_saham, parent, false);
        return new MainCardSahamAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {
        MainCardSaham mainCardSaham = mMainCardSahams.get(position);

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

        holder.cardSaham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SahamDetailActivity.class);
                intent.putExtra("mainCardSaham", (Serializable) mainCardSaham);
                mContext.startActivity(intent);
            }
        });

        holder.tvName.setText(mainCardSaham.getName() + " (" + mainCardSaham.getCode() + ")");
        holder.tvSectore.setText(mainCardSaham.getSectore());
        holder.tvSubIndustry.setText(mainCardSaham.getSubindustry());
        holder.tvLast.setText(currencyFormat(mainCardSaham.getLast()));
        holder.tvPrev.setText(currencyFormat(mainCardSaham.getPrev()));
        holder.tvOpen.setText(currencyFormat(mainCardSaham.getOpen()));
        holder.tvFreq.setText(currencyFormat(mainCardSaham.getFreq()));
        holder.tvHigh.setText(currencyFormat(mainCardSaham.getHigh()));
        holder.tvLow.setText(currencyFormat(mainCardSaham.getLow()));
        holder.tvPER.setText(decimalFormat(mainCardSaham.getPer()));
        holder.tvPBV.setText(decimalFormat(mainCardSaham.getPbv()));
        holder.tvVol.setText(currencyFormat(mainCardSaham.getVol()));
        holder.tvVal.setText(currencyFormat(mainCardSaham.getVal()));
        holder.tvCap.setText(currencyFormat(mainCardSaham.getCap()));

        holder.tv1Day.setText(pctFormat(mainCardSaham.getOneday()));
        holder.tv1Month.setText(pctFormat(mainCardSaham.getOnemonth()));
        holder.tvYtd.setText(pctFormat(mainCardSaham.getYtd()));
        holder.tv1Year.setText(pctFormat(mainCardSaham.getOneyear()));

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {(int) Long.parseLong("4294944559"), (int) Long.parseLong("4294918508")});
        gd.setCornerRadius(0f);

        holder.linLayoutMC.setBackgroundDrawable(gd);
    }

    @Override
    public int getItemCount() {
        return mMainCardSahams.size();
    }

    public void setFilter(List<MainCardSaham> filterList){
        mMainCardSahams = new ArrayList<>();
        mMainCardSahams.addAll(filterList);
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

    public String decimalFormat(Double amount) {
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
