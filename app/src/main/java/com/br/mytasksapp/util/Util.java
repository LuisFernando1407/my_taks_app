package com.br.mytasksapp.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.br.mytasksapp.MyTaskApplication;
import com.br.mytasksapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static void alert(final Context context, String title, String message, final Class redirect, boolean isButton){
        final AlertDialog alertDialog;

        if(isButton) {
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setCancelable(false)
                    .setMessage(Html.fromHtml(message))
                    .setNegativeButton("Não", null)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (redirect != null) {
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
        }else{
            alertDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(Html.fromHtml(message))
                    .create();
        }

        alertDialog.show();
    }

    /*
      Pode ser usado para fazer solicitações assíncronas de GET, POST, PUT e DELETE HTTP.
      As solicitações podem ser feitas com parâmetros adicionais
      passando uma instância RequestParams e as respostas podem ser manipuladas passando uma
      instância ResponseHandlerInterface anonimamente substituída.
   */
    public static AsyncHttpClient createAsyncHttpClient()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setResponseTimeout(60000);
        client.setConnectTimeout(60000);
        return client;
    }

    /*
        Processa solicitações http no modo síncrono, para que seu encadeamento
        de chamadas seja bloqueado em cada solicitação.
    */
    public static SyncHttpClient createSyncHttpClient(){
        SyncHttpClient client = new SyncHttpClient();
        client.setResponseTimeout(60000);
        client.setConnectTimeout(60000);
        return  client;
    }


    public static Boolean containsPref(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyTaskApplication.getInstance());
        return prefs.contains(key);
    }

    /**
     * validate your email address format. Ex-akhi@mani.com
     */
    public static boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
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

    private static SharedPreferences getSessionPreferences() {
        Context ctx = MyTaskApplication.getInstance();
        return ctx.getSharedPreferences("SESSION_PREFERENCES", ctx.MODE_PRIVATE);
    }

    public static void setApiToken(String token) {
        SharedPreferences mPreferences = getSessionPreferences();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("API_TOKEN", token);
        editor.apply();
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateFormat(String date, String initDateFormat, String endDateFormat){
        try {
            Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
            return formatter.format(initDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Erro ao obter data";
        }
    }

    public static String getApiToken() {
        return getSessionPreferences().getString("API_TOKEN", null);
    }

    public static void setApiFCMToken(String token) {
        SharedPreferences mPreferences = getSessionPreferences();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("API_FCM_TOKEN", token);
        editor.apply();
    }

    public static String getApiFCMToken() {
        return getSessionPreferences().getString("API_FCM_TOKEN", null);
    }

    public static Dialog loadingDialog(final Context ctx) {
        Dialog loading = new Dialog(ctx);
        loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loading.setContentView(R.layout.dialog_loading);
        Objects.requireNonNull(loading.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(false);
        return loading;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MyTaskApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void putPref(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyTaskApplication.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static String getPref(String key, String defValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyTaskApplication.getInstance());
        return preferences.getString(key, defValue);
    }

    public static void removePref(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyTaskApplication.getInstance());
        SharedPreferences.Editor ed = prefs.edit();
        ed.remove(key);
        ed.apply();
    }
}