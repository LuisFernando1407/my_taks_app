package com.br.mytasksapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.rest.UserHttp;
import com.br.mytasksapp.util.Util;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private AppCompatButton enter;
    private AppCompatButton register;
    private TextView forgot_pass;

    private UserHttp userHttp;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);

        enter = findViewById(R.id.enter);
        register = findViewById(R.id.register);
        forgot_pass = findViewById(R.id.forgot_pass);

        this.context = this;

        userHttp = new UserHttp(context);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.isNetworkAvailable()){
                    String emailText = email.getText().toString();
                    String passText = password.getText().toString();

                    if(Util.emailValidator(emailText)){
                        if(!passText.isEmpty()){
                            userHttp.login(emailText, passText);
                        }else{
                            password.setError("O campo senha é obrigatório");
                        }
                    }else{
                        email.setError("E-mail inválido");
                    }

                }else{
                    Toast.makeText(context, "Sem acesso a internet", Toast.LENGTH_LONG).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegisterActivity.class));
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ForgotPassActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {}
}