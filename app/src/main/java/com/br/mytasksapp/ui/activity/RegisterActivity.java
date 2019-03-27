package com.br.mytasksapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnUserCompleted;
import com.br.mytasksapp.api.rest.UserHttp;
import com.br.mytasksapp.util.Mask;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.MinLength;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.annotations.RegExp;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

import static eu.inmite.android.lib.validations.form.annotations.RegExp.EMAIL;

public class RegisterActivity extends AppCompatActivity implements OnUserCompleted {

    @NotEmpty(messageId = R.string.name)
    private EditText name;

    private Spinner sex;

    private RadioGroup accountReason;

    private EditText phone;

    @RegExp(value = EMAIL, messageId = R.string.email)
    private EditText email;

    @NotEmpty(messageId = R.string.pass)
    @MinLength(value = 6, messageId = R.string.pass_min)
    private EditText pass;

    private Context context;

    private TextView viewTerms;

    private CheckBox terms;

    private AppCompatButton register;

    private UserHttp userHttp;

    private String[] itemsSex = {"Masculino", "Feminino"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewTerms = findViewById(R.id.viewTerms);
        terms = findViewById(R.id.terms);
        register = findViewById(R.id.register);

        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);
        accountReason = findViewById(R.id.account_reason);
        phone = findViewById(R.id.phone);

        phone.addTextChangedListener(Mask.insert(phone, Mask.MaskType.PHONE));

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);

        this.context = this;

        Util.createSpinnerItems(context, sex, itemsSex, R.color.gray_strong_app, R.layout.item_spinner, false);

        userHttp = new UserHttp(context, this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(terms.isChecked()){
                    validate();
                }else{
                    Toast.makeText(context, "Aceite os termos de uso para prosseguir", Toast.LENGTH_LONG).show();
                }
            }
        });

        viewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TermsActivity.class));
            }
        });

    }

    private void validate() {
        boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(context, true));
        RadioButton ocp = findViewById(accountReason.getCheckedRadioButtonId());

        if(isValid){
            JSONObject params = new JSONObject();

            try {
                params.put("name", name.getText().toString());
                params.put("sex", sex.getSelectedItem().toString());
                params.put("occupation", ocp.getText().toString());
                params.put("phone", phone.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", pass.getText().toString());

                userHttp.register(params);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void userCompleted(JSONObject results) {
        try {
            JSONObject user = results.getJSONObject("user");

            /* Set user */
            Util.setApiToken(results.getString("token"));
            Util.putPref("lastUser", user.toString());

            /* View user */
            alertWelcome(user.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alertWelcome(String name){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialog_welcome, null);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        final AppCompatButton skip = v.findViewById(R.id.ok);

        TextView message = v.findViewById(R.id.message);

        String txtMss = "Parabéns " + name + "!  Seu cadastro no My Tasks foi realizado com sucesso. <br><br> Clique em <b>entrar</b> e comece a usar o app.";
        message.setText(Html.fromHtml(txtMss));

        alertDialogBuilder.setView(v);
        final android.app.AlertDialog d = alertDialogBuilder.create();
        d.setCancelable(false);
        d.show();
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}