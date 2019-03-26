package com.br.mytasksapp.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.br.mytasksapp.BuildConfig;
import com.br.mytasksapp.R;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
            toolbarTitle.setText("Sobre");
        }

        TextView version = findViewById(R.id.version);

        String versionText = "Versão " + BuildConfig.VERSION_NAME;

        version.setText(versionText);
    }
}