package com.br.mytasksapp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnTaskCompleted;
import com.br.mytasksapp.api.rest.TaskHttp;
import com.br.mytasksapp.api.rest.UserHttp;
import com.br.mytasksapp.model.User;
import com.br.mytasksapp.ui.adapter.NoResultAdapter;
import com.br.mytasksapp.ui.adapter.SettingsAdapter;
import com.br.mytasksapp.model.Setting;
import com.br.mytasksapp.util.Util;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements OnTaskCompleted {

    private RecyclerView recyclerSettings;
    private AutoCompleteTextView searchComplete;
    private AppCompatButton delete;

    private Context context;

    private int k = 0;

    private String[] itemsSettings = {
            "Notificações"
    };

    private User user;
    private UserHttp userHttp;

    private TaskHttp taskHttp;


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

        userHttp = new UserHttp(context);

        taskHttp = new TaskHttp(context, this);
        taskHttp.getFCMTokenStatus();

        /* User logged */
        user = new Gson().fromJson(Util.getPref("lastUser", null), User.class);

        /* Actions */
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /* Delete account click */
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDeleteAccount(user.getName());
            }
        });

    }

    private void alertDeleteAccount(String name){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialog_delete_account, null);
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

        final AppCompatButton ok = v.findViewById(R.id.ok);

        TextView title = v.findViewById(R.id.title);
        AppCompatButton skip = v.findViewById(R.id.skip);

        String txtTitle = "Mas por quê " +  Util.limitString(name, 14, "...") + "?";
        title.setText(txtTitle);

        alertDialogBuilder.setView(v);
        final android.app.AlertDialog d = alertDialogBuilder.create();
        d.setCancelable(false);
        d.show();
        Objects.requireNonNull(d.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                Toast.makeText(context, "Aêêê... ficamos felizes de ter você aqui! Obrigado pela confiança", Toast.LENGTH_LONG).show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userHttp.deleteAccount();
            }
        });
    }

    @Override
    public void taskCompleted(JSONObject results) {
        try {
            JSONArray fcm = results.getJSONArray("fcm");
            ArrayList<Setting> settings = new ArrayList<>();

            if(fcm.length() > 0){
                for(int i = 0; i < fcm.length(); i++){
                    JSONObject object = fcm.getJSONObject(i);
                    settings.add(new Setting(itemsSettings[i], object.getBoolean("is_accepted")));
                }

                final SettingsAdapter settingsAdapter = new SettingsAdapter(context, settings);

                /* Recycler layout */
                RecyclerView.LayoutManager layout = new LinearLayoutManager(context) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };

                recyclerSettings.setAdapter(settingsAdapter);
                recyclerSettings.setLayoutManager(layout);

                /* Adapters */
                ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(
                        context,
                        android.R.layout.select_dialog_item,
                        itemsSettings
                );

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

            }else{
                searchComplete.setVisibility(View.GONE);
                NoResultAdapter noResultAdapter = new NoResultAdapter(context, "Houve uma falha ao buscar suas configurações! Por favor tente novamente mais tarde", R.drawable.noresult, 100);
                recyclerSettings.setAdapter(noResultAdapter);

                RecyclerView.LayoutManager layout = new LinearLayoutManager(context);
                recyclerSettings.setLayoutManager(layout);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}