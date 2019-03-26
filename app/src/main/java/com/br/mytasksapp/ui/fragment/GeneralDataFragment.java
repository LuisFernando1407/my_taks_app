package com.br.mytasksapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.br.mytasksapp.model.User;
import com.br.mytasksapp.ui.activity.TermsActivity;
import com.br.mytasksapp.util.Mask;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.inmite.android.lib.validations.form.FormValidator;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;
import eu.inmite.android.lib.validations.form.callback.SimpleErrorPopupCallback;

public class GeneralDataFragment extends Fragment implements OnUserCompleted {

    @NotEmpty(messageId = R.string.name)
    private EditText name;

    private CheckBox terms;
    private Spinner sex;

    private EditText phone;

    private RadioGroup accountReason;

    private AppCompatButton salve;

    private Context context;

    private String[] itemsSex = {"Masculino", "Feminino"};

    private UserHttp userHttp;

    private TextView viewTerms;

    private View view;

    private RadioButton professional;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_general, container, false);

        terms = view.findViewById(R.id.terms);
        sex = view.findViewById(R.id.sex);

        phone = view.findViewById(R.id.phone);
        accountReason = view.findViewById(R.id.account_reason);

        salve = view.findViewById(R.id.salve);

        name = view.findViewById(R.id.name);

        professional = view.findViewById(R.id.professional);

        context = getContext();

        viewTerms = view.findViewById(R.id.viewTerms);

        viewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, TermsActivity.class));
            }
        });

        userHttp = new UserHttp(context, this);

        userHttp.getMyData();

        /* Init */
        terms.setTypeface(ResourcesCompat.getFont(Objects.requireNonNull(context), R.font.montserrat));

        Util.createSpinnerItems(context, sex, itemsSex, R.color.gray_strong_app, R.layout.item_spinner, false);

        phone.addTextChangedListener(Mask.insert(phone, Mask.MaskType.PHONE));

        salve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(terms.isChecked()){
                    validate(view);
                }else{
                    Toast.makeText(context, "Aceite os termos de uso para prosseguir", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    private void validate(View view){
        boolean isValid = FormValidator.validate(this, new SimpleErrorPopupCallback(context, true));
        RadioButton ocp = view.findViewById(accountReason.getCheckedRadioButtonId());

        if(isValid){
            JSONObject params = new JSONObject();

            try {
                params.put("name", name.getText().toString());
                params.put("sex", sex.getSelectedItem().toString());
                params.put("occupation", ocp.getText().toString());
                params.put("phone", phone.getText().toString());

                userHttp.update(params);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void userCompleted(JSONObject results) {
        RadioButton ocp = view.findViewById(accountReason.getCheckedRadioButtonId());

        try {
            JSONObject user = results.getJSONObject("user");

            Util.putPref("lastUser", user.toString());

            name.setText(user.getString("name"));
            name.clearFocus();

            if(!sex.getSelectedItem().toString().equalsIgnoreCase(user.getString("sex"))){
                sex.setSelection(1);
            }

            if(!ocp.getText().toString().equalsIgnoreCase(user.getString("occupation"))){
                professional.setChecked(true);
            }

            String phoneFinal = user.getString("phone") + 1; //+1 spacing

            phone.setText(phoneFinal);
            phone.clearFocus();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}