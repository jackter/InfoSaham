package com.alvarenstudio.infosaham.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.alvarenstudio.infosaham.R;
import com.alvarenstudio.infosaham.SahamDetailActivity;
import com.alvarenstudio.infosaham.SharedPref;
import com.alvarenstudio.infosaham.model.Emiten;
import com.alvarenstudio.infosaham.model.MainCardSaham;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class EmitenAdapter extends PagerAdapter {
    private List<Emiten> model;
    private LayoutInflater layoutInflater;
    private Context context;

    public EmitenAdapter(List<Emiten> model, Context context) {
        this.model = model;
        this.context = context;
    }

    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_emiten, container, false);

        TextView tvCode, tvBal, tvAvgPrice, tvLastPrice, tvPotensi, tvPotensiPct;
        CardView cardEmiten;

        cardEmiten = view.findViewById(R.id.cardEmiten);
        tvCode = view.findViewById(R.id.tvEmiten);
        tvBal = view.findViewById(R.id.tvBal);
        tvAvgPrice = view.findViewById(R.id.tvAvgPrice);
        tvLastPrice = view.findViewById(R.id.tvLastPrice);
        tvPotensi = view.findViewById(R.id.tvPotensi);
        tvPotensiPct = view.findViewById(R.id.tvPotensiPct);

        tvCode.setText(model.get(position).getEmiten());
        tvBal.setText(currencyFormat(model.get(position).getQty()));
        tvAvgPrice.setText(currencyFormat(model.get(position).getPrice()));
        tvLastPrice.setText(currencyFormat(model.get(position).getLastPrice()));
        long potensi = (model.get(position).getLastPrice() * model.get(position).getQty()) - (model.get(position).getPrice() * model.get(position).getQty());
        tvPotensi.setText(currencyFormat(potensi));
        float potensiPct = (float)potensi / (float)(model.get(position).getLastPrice() * model.get(position).getQty()) * (float)100;
        tvPotensiPct.setText(String.format("%.2f", potensiPct) + "%");

        if(potensi < 0) {
            tvPotensi.setTextColor(Color.RED);
            tvPotensiPct.setTextColor(Color.RED);

            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvPotensi.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.shape3c) );
            } else {
                tvPotensi.setBackground(ContextCompat.getDrawable(context, R.drawable.shape3c));
            }
        }
        else {
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                tvPotensi.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.shape1c) );
            } else {
                tvPotensi.setBackground(ContextCompat.getDrawable(context, R.drawable.shape1c));
            }
        }

        cardEmiten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SahamDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                SharedPref sharedPref = new SharedPref(context);
                List<MainCardSaham> mMainCardSaham = sharedPref.loadDataSharedMainCardSaham("maincardsaham");
                MainCardSaham mainCardSaham = new MainCardSaham();

                for(MainCardSaham data:mMainCardSaham) {
                    if(data.getCode().equals(model.get(position).getEmiten())) {
                        mainCardSaham = data;
                    }
                }

                intent.putExtra("mainCardSaham", (Serializable) mainCardSaham);
                context.startActivity(intent);
            }
        });

        container.addView(view, 0);
        return view;
    }

    public String currencyFormat(Long amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
