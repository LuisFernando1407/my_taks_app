package com.br.mytasksapp.api;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.br.mytasksapp.api.rest.UserHttp;
import com.br.mytasksapp.ui.activity.LoginActivity;
import com.br.mytasksapp.ui.activity.error.ServerErrorActivity;
import com.br.mytasksapp.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class BaseJsonHandler extends JsonHttpResponseHandler {
    private Context ctx;
    private Dialog loadingDialog;
    private UserHttp userHttp;
    private boolean isAnimation = true;

    protected BaseJsonHandler(Context ctx) {
        this.ctx = ctx;
        userHttp = new UserHttp(ctx);
        loadingDialog = Util.loadingDialog(ctx);
    }

    protected BaseJsonHandler(Context ctx, boolean isAnimation) {
        this.ctx = ctx;
        this.isAnimation = isAnimation;
        userHttp = new UserHttp(ctx);
        loadingDialog = Util.loadingDialog(ctx);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if(isAnimation) {
                loadingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        whenConnectTimeOut(throwable);
        alertStatusCode(statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        whenConnectTimeOut(throwable);
        alertStatusCode(statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        whenConnectTimeOut(throwable);
        alertStatusCode(statusCode);
    }

    @Override
    public void onFinish() {
        try {
            if(isAnimation) {
                loadingDialog.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertStatusCode(int statusCode) {
        if(statusCode == 500) {
            Util.alert(ctx, "Falha", "Um erro interno ocorreu no servidor, por favor tente mais tarde.", null, false);
        }else if(statusCode == 404) {
            Util.alert(ctx, "Falha", "Servidor indisponível, por favor tente mais tarde.", null, false);
        }else if(statusCode == 401){
            /* TODO: Refresh token */
            Toast.makeText(ctx, "Sua seção expirou! Faça novamente o login", Toast.LENGTH_LONG).show();
            Util.setApiToken(null);
            Util.removePref("lastUser");
            Util.removePref("first_access");
            ctx.startActivity(new Intent(ctx, LoginActivity.class));
        }
    }

    private void whenConnectTimeOut(Throwable throwable) {
        if(throwable instanceof SocketTimeoutException
                || throwable instanceof ConnectTimeoutException) {
            Intent intent = new Intent(ctx, ServerErrorActivity.class);
            intent.putExtra("message", "Tempo para conexão esgostado ou servidor indisponível, por favor tente mais tarde.");
            ctx.startActivity(intent);
        }

        if(throwable instanceof ConnectException){
            Intent intent = new Intent(ctx, ServerErrorActivity.class);
            intent.putExtra("message", "Verifique seu acesso a internet ou tente novamente mais tarde.");
            ctx.startActivity(intent);
        }
    }
}