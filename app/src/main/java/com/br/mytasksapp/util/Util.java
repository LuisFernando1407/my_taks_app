package com.br.mytasksapp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.mytasksapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Util {
    public static void alert(final Context context, String title, String message, final Class redirect){
        final AlertDialog alertDialog =  new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Html.fromHtml(message))
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(redirect != null){
                            context.startActivity(new Intent(context, redirect));
                        }
                    }
                }).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.gray_app));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });

        alertDialog.show();
    }


    public static void applyFontToMenuItem(Context context, MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "montserrat_regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new FontsOverride("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public static void createSpinnerItems(final Context context, Spinner spinner, String[] items, final int colorSelected, int layout, final boolean isTitle) {
        final java.util.List<String> options = new ArrayList<>(Arrays.asList(items));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, layout, options) {
            @Override
            public boolean isEnabled(int position) {
                return !isTitle || position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                if(position == 0) {
                    tv.setTextColor(context.getResources().getColor(colorSelected));
                } else {
                    tv.setTextColor(context.getResources().getColor(colorSelected));
                }
                return  view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    public static String limitString(String value, int limit, String format) {
        if (value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit-1) + format;
    }

}