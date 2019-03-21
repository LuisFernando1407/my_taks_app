package com.br.mytasksapp.api.rest;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.br.mytasksapp.Constants;
import com.br.mytasksapp.api.BaseJsonHandler;
import com.br.mytasksapp.api.http.AuthenticatedHttp;
import com.br.mytasksapp.api.interfaces.OnTaskCompleted;
import com.br.mytasksapp.ui.activity.HomeActivity;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TaskHttp extends AuthenticatedHttp {
    private Context context;
    private OnTaskCompleted listener;

    public TaskHttp(Context context, OnTaskCompleted listener){
        this.context = context;
        this.listener = listener;

        setupClient();
    }

    public void getMyTasks() {
        client.get(Constants.API.TASKS, new BaseJsonHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.taskCompleted(response);
            }
        });

    }

    public void setFCMToken(boolean isAccepted){
        JSONObject params = new JSONObject();
        StringEntity entity = null;

        try {
            params.put("fcm_token", Util.getApiFCMToken());
            params.put("is_accepted", isAccepted);

            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, Constants.API.TASKS + "/notification", entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Util.putPref("first_access", "no");
            }
        });
    }
}