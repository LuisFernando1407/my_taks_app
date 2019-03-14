package com.br.mytasksapp.api.rest;

import android.content.Context;
import android.content.Intent;

import com.br.mytasksapp.Constants;
import com.br.mytasksapp.api.BaseJsonHandler;
import com.br.mytasksapp.api.http.AuthenticatedHttp;
import com.br.mytasksapp.ui.activity.HomeActivity;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UserHttp extends AuthenticatedHttp {

    public UserHttp(Context ctx) {
        this.ctx = ctx;
        // setup http
        setupClient();
    }

    public void login(String email, String password){

        JSONObject params = new JSONObject();
        StringEntity entity = null;

        try {
            params.put("email", email);
            params.put("password", password);

            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(ctx, Constants.API.LOGIN, entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(ctx) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Util.putPref("lastUser", response.getJSONObject("user").toString());
                    Util.setApiToken(response.getString("token"));
                    ctx.startActivity(new Intent(ctx, HomeActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(statusCode == 400){
                    try {
                        Util.alert(ctx, "Atenção!", errorResponse.getString("error"), null, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}