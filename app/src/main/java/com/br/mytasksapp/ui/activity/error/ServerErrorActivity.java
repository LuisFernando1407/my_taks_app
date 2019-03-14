package com.br.mytasksapp.ui.activity.error;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.br.mytasksapp.R;

public class ServerErrorActivity extends AppCompatActivity {

    private TextView txtMessage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_error);

        txtMessage = findViewById(R.id.txtMessage);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            txtMessage.setText(bundle.getString("message"));
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
