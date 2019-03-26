package com.br.mytasksapp.api.rest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.br.mytasksapp.Constants;
import com.br.mytasksapp.api.BaseJsonHandler;
import com.br.mytasksapp.api.http.AuthenticatedHttp;
import com.br.mytasksapp.api.interfaces.OnUserCompleted;
import com.br.mytasksapp.ui.activity.HomeActivity;
import com.br.mytasksapp.ui.activity.LoginActivity;
import com.br.mytasksapp.ui.activity.SettingsActivity;
import com.br.mytasksapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UserHttp extends AuthenticatedHttp {

    private OnUserCompleted listener;

    public UserHttp(Context ctx) {
        this.ctx = ctx;
        // setup http
        setupClient();
    }

    public UserHttp(Context ctx, OnUserCompleted listener){
        this.ctx = ctx;
        this.listener = listener;
        // setup http
        setupClient();
    }

    public void register(JSONObject params){
        StringEntity entity = null;

        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(ctx, Constants.API.REGISTER, entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(ctx) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Util.putPref("first_access", "yes");
                listener.userCompleted(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

    public void getMyData(){
        client.get(Constants.API.USER, new BaseJsonHandler(ctx) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.userCompleted(response);
            }
        });
    }

    public void update(JSONObject params){
        StringEntity entity = null;

        try {
            entity = new StringEntity(params.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.put(ctx, Constants.API.USER, entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(ctx) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(ctx, "Usuário atualizado com sucesso", Toast.LENGTH_LONG).show();
                listener.userCompleted(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

    public void forgot(String email){
        JSONObject params = new JSONObject();
        StringEntity entity = null;

        try {
            params.put("email", email);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(ctx, Constants.API.FORGOT_PASS, entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(ctx) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.userCompleted(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

    public void refresh(String uid, String new_pass){
        JSONObject params = new JSONObject();
        StringEntity entity = null;

        try {
            params.put("password", new_pass);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(ctx, Constants.API.REFRESH_PASS + uid, entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(ctx) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.userCompleted(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

    public void alterPassword(String oldPass, String newPass){
        JSONObject params = new JSONObject();
        StringEntity entity = null;

        try {
            params.put("old_password", oldPass);
            params.put("new_password", newPass);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(ctx, Constants.API.USER + "/refresh_password", entity, Constants.API.CONTENT_TYPE, new BaseJsonHandler(ctx) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.userCompleted(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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

    public void deleteAccount() {

        client.delete(ctx, Constants.API.USER, new BaseJsonHandler(ctx) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Util.setApiToken(null);
                Util.removePref("lastUser");
                ctx.startActivity(new Intent(ctx, LoginActivity.class));
                ((SettingsActivity) ctx).finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 400) {
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