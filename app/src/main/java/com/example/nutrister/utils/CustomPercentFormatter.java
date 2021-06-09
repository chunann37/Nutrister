package com.example.nutrister.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class CustomPercentFormatter extends ValueFormatter {

    private DecimalFormat mFormat;

    public CustomPercentFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {

        if (value < 0.5) return "";
        else return mFormat.format(value) + " %";
    }
}


