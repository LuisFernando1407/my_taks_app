package com.br.mytasksapp.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnUserCompleted;
import com.br.mytasksapp.api.rest.UserHttp;

import org.json.JSONObject;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class AlterPassActivity extends AppCompatActivity implements OnUserCompleted {

    private Context context;

    @NotEmpty(messageId = R.string.old_pass)
    private EditText oldPass;

    @MinLength(value = 6, messageId = R.string.new_pass_min)
    private EditText newPass;

    private UserHttp userHttp;

    private AppCompatButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_pass);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.context = this;

        userHttp = new UserHttp(context, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        oldPass = findViewById(R.id.old_pass);
        newPass = findViewById(R.id.new_pass);

        send = findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate(){
        boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(context, true));
        if(isValid){
            userHttp.alterPassword(oldPass.getText().toString(), newPass.getText().toString());
        }
    }

    @Override
    public void userCompleted(JSONObject results) {
        if(results.has("success")){
            Toast.makeText(context, "Senha alterada com sucesso", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }
}