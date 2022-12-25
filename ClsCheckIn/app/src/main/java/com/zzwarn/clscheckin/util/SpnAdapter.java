package com.zzwarn.clscheckin.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zzwarn.clscheckin.R;

public class SpnAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] stringArray;


    public SpnAdapter(Context txt, String[] strArr){
        super(txt, android.R.layout.simple_spinner_dropdown_item, strArr);
        context = txt;
        stringArray = strArr;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(stringArray[position]);
        tv.setTextSize(15f);
        tv.setTextColor(context.getResources().getColor(R.color.black));
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        tv.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
        return convertView;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
        }
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(stringArray[position]);
        tv.setTextSize(15f);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        return convertView;
    }


}
