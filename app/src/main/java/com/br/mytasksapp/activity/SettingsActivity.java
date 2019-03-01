package com.br.mytasksapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.br.mytasksapp.R;
import com.br.mytasksapp.adapter.SettingsAdapter;
import com.br.mytasksapp.model.Setting;
import com.br.mytasksapp.util.Util;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerSettings;
    private AutoCompleteTextView searchComplete;
    private AppCompatButton delete;

    private Context context;

    private int k = 0;

    private String[] itemsSettings = {
            "Notificações",
            "Som notification",
            "Perfil visível",
            "Sincronização AUTO"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /* Sets */
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerSettings = findViewById(R.id.recycler_settings);
        searchComplete = findViewById(R.id.search_complete);
        delete = findViewById(R.id.delete);

        /* Application context */
        this.context = this;

        /* Actions */
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /* Adapters */
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.select_dialog_item,
                itemsSettings
        );

        final SettingsAdapter settingsAdapter = new SettingsAdapter(context, getSettings());

        /* Recycler layout */
        RecyclerView.LayoutManager layout = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        recyclerSettings.setAdapter(settingsAdapter);
        recyclerSettings.setLayoutManager(layout);

        /* Auto Complete */
        searchComplete.setThreshold(1);
        searchComplete.setAdapter(searchAdapter);
        searchComplete.setTextColor(getResources().getColor(R.color.gray_strong_app));

        /* Case click */
        searchComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                settingsAdapter.getFilter().filter(item);
            }
        });

        /* Case digit */
        searchComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Search every three characters */
                if(s.length() == 3){
                    settingsAdapter.getFilter().filter(s);
                }else{
                    if(s.length() > 3){
                        if(k == 2){
                            settingsAdapter.getFilter().filter(s);
                            k = 0;
                        }else{
                            k += 1;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* Clear */
                if(s.length() == 0){
                    settingsAdapter.getFilter().filter(s);
                }
            }
        });

        /* Delete account click */
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.alert(context, "Atenção!", "Deseja realmente excluir sua conta?", HomeActivity.class);
            }
        });

    }

    private ArrayList<Setting> getSettings(){
        ArrayList<Setting> settings = new ArrayList<>();

        for(String itemsSetting : itemsSettings) {
            settings.add(new Setting(itemsSetting));
        }

        return settings;
    }
}