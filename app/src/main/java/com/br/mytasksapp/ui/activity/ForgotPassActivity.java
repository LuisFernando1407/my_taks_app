package com.br.mytasksapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnUserCompleted;
import com.br.mytasksapp.api.rest.UserHttp;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class ForgotPassActivity extends AppCompatActivity implements OnUserCompleted {
    private AppCompatButton send;

    private EditText email;

    private UserHttp userHttp;

    private Context context;

    @MinLength(value = 6, messageId = R.string.pass_min)
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        send = findViewById(R.id.send);
        email = findViewById(R.id.email);

        this.context = this;

        userHttp = new UserHttp(context, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.emailValidator(email.getText().toString())){
                    userHttp.forgot(email.getText().toString());
                }else{
                    email.setError(getResources().getString(R.string.email));
                }
            }
        });

    }

    @Override
    public void userCompleted(JSONObject results) {
        if(results.has("user")){
            try {
                JSONObject user = results.getJSONObject("user");
                alertRefresh(user.getString("_id"), user.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(context, "Senha alterada com sucesso", Toast.LENGTH_LONG).show();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }
    }

    private void alertRefresh(final String uid, String name){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialog_refresh, null);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        final AppCompatButton skip = v.findViewById(R.id.ok);

        TextView title = v.findViewById(R.id.title);

        pass = v.findViewById(R.id.pass);

        String txtTitle = "Olá, " + Util.limitString(name, 14, "...");
        title.setText(txtTitle);

        alertDialogBuilder.setView(v);
        final android.app.AlertDialog d = alertDialogBuilder.create();
        d.setCancelable(false);
        d.show();
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(uid, d);

            }
        });
    }

    private void validate(String uid, android.app.AlertDialog d){
        boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(context, true));
        if(isValid){
            d.dismiss();
            userHttp.refresh(uid, pass.getText().toString());
        }
    }
}