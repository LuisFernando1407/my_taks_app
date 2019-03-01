package com.br.mytasksapp.fragment;

import android.content.Context;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.util.Mask;
import com.br.mytasksapp.util.Util;

import java.util.Objects;

public class GeneralDataFragment extends Fragment {

    private EditText name;

    private CheckBox terms;
    private Spinner sex;

    private EditText phone;
    private RadioGroup accountReason;

    private AppCompatButton salve;

    private Context context;

    private String[] itemsSex = {"Masculino", "Feminino"};

    private String account = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general, container, false);

        terms = view.findViewById(R.id.terms);
        sex = view.findViewById(R.id.sex);

        phone = view.findViewById(R.id.phone);
        accountReason = view.findViewById(R.id.account_reason);

        salve = view.findViewById(R.id.salve);

        name = view.findViewById(R.id.name);

        context = getContext();

        /* Init */
        terms.setTypeface(ResourcesCompat.getFont(Objects.requireNonNull(context), R.font.montserrat));

        Util.createSpinnerItems(context, sex, itemsSex, R.color.gray_strong_app, R.layout.item_spinner, false);

        phone.addTextChangedListener(Mask.insert(phone, Mask.MaskType.PHONE));


        /* Listener */

        accountReason.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.student:
                        account = "Estudante";
                        break;

                    case R.id.professional:
                        account = "Profissional";
                        break;

                    default:
                        account = null;
                        break;
                }
            }
        });

        salve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(account != null){
                    String termsTxt = terms.isChecked() ? "aceito": "não aceito";

                    String message = "Nome: " + name.getText().toString() + "<br> Sexo: " + sex.getSelectedItem().toString() +
                            "<br> Área de atuação: " + account + "<br> Telefone: " + phone.getText().toString() +
                            "<br> Termos de uso: " + termsTxt;

                    Util.alert(context, "Confirma os dados?", message, null);

                }else{
                    Toast.makeText(context, "Selecione um tipo de área de atuação", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
}