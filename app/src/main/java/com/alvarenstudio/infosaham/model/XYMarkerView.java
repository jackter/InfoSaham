package com.alvarenstudio.infosaham.model;

import android.content.Context;
import android.widget.TextView;

import com.alvarenstudio.infosaham.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class XYMarkerView extends MarkerView {

    private TextView tvContent;
    private ArrayList<String> chartStringList;

    public XYMarkerView (Context context, int layoutResource, ArrayList<String> chartStringList) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.chartStringList = chartStringList;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(this.chartStringList.get(highlight.getXIndex()) + "\n" + currencyFormat(Math.round(e.getVal()))); // set the entry-value as the display text
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }

    public String currencyFormat(int amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(amount).replace(".", "x").replace(",", ".").replace("x", ",");
    }
}