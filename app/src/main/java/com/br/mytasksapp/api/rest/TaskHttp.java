package com.br.mytasksapp.api.rest;

import android.content.Context;

import com.br.mytasksapp.Constants;
import com.br.mytasksapp.api.BaseJsonHandler;
import com.br.mytasksapp.api.http.AuthenticatedHttp;
import com.br.mytasksapp.api.interfaces.OnTaskCompleted;
import com.br.mytasksapp.util.Util;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TaskHttp extends AuthenticatedHttp {
    private Context context;
    private OnTaskCompleted listener;

    public TaskHttp(Context context, OnTaskCompleted listener){
        this.context = context;
        this.listener = listener;

        setupClient();
    }

    public void getMyTasks() {
        client.addHeader("Authorization", Constants.API.TYPE_REQUEST + Util.getApiToken());
        client.get(Constants.API.USER + "s", new BaseJsonHandler(context) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.taskCompleted(response);
            }
        });

    }
}