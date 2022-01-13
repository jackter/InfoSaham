package com.alvarenstudio.pasardanawatcher.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alvarenstudio.pasardanawatcher.MainActivity;
import com.alvarenstudio.pasardanawatcher.R;
import com.alvarenstudio.pasardanawatcher.SahamDetailActivity;
import com.alvarenstudio.pasardanawatcher.model.MainCardSaham;

import org.jetbrains.annotations.NotNull;

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
        public TextView tvName, tvSectore, tvSubIndustry, tvLast, tvPrev, tvOpen, tvHigh, tvLow, tvPER, tvPBV, tvVol, tvVal, tv1Day, tv1Month, tvYtd, tv1Year, tvCap;
        public CardView cardSaham;

        public viewHolder(View itemView) {
            super(itemView);

            cardSaham = itemView.findViewById(R.id.card_saham);
            tvName = itemView.findViewById(R.id.tvName);
            tvSectore = itemView.findViewById(R.id.tvSectore);
            tvSubIndustry = itemView.findViewById(R.id.tvSubIndustry);
            tvLast = itemView.findViewById(R.id.tvLast);
            tvPrev = itemView.findViewById(R.id.tvPrev);
            tvOpen = itemView.findViewById(R.id.tvOpen);
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

        holder.cardSaham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SahamDetailActivity.class);
                intent.putExtra("id", mainCardSaham.getId());
                intent.putExtra("code", mainCardSaham.getCode());
                mContext.startActivity(intent);
            }
        });

        holder.tvName.setText(mainCardSaham.getName() + " (" + mainCardSaham.getCode() + ")");
        holder.tvSectore.setText(mainCardSaham.getSectore());
        holder.tvSubIndustry.setText(mainCardSaham.getSubindustry());
        holder.tvLast.setText(currencyFormat(mainCardSaham.getLast()));
        holder.tvPrev.setText(currencyFormat(mainCardSaham.getPrev()));
        holder.tvOpen.setText(currencyFormat(mainCardSaham.getOpen()));
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
}
