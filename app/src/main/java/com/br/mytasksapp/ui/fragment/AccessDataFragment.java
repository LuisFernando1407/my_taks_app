package com.br.mytasksapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.br.mytasksapp.R;
import com.br.mytasksapp.api.interfaces.OnUserCompleted;
import com.br.mytasksapp.api.rest.UserHttp;
import com.br.mytasksapp.ui.activity.AlterPassActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AccessDataFragment extends Fragment implements OnUserCompleted {

    private EditText email;
    private TextView alterPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_access, container, false);

            email = view.findViewById(R.id.email);

            UserHttp userHttp = new UserHttp(getContext(), this);
            userHttp.getMyData();

            alterPass = view.findViewById(R.id.alterPass);
            alterPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), AlterPassActivity.class));
                }
            });

            return view;
    }

    @Override
    public void userCompleted(JSONObject results) {
        try {
            JSONObject user = results.getJSONObject("user");

            email.setText(user.getString("email"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
