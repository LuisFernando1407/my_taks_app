package com.br.mytasksapp.api.http;

import android.content.Context;

import com.br.mytasksapp.util.Util;
import com.loopj.android.http.AsyncHttpClient;

public class AuthenticatedHttp {
    /**
     * Class attributes
     */
    protected AsyncHttpClient client;
    protected Context ctx;
    private static final int CONNECTION_TIMEOUT = 20*1000;

    protected AsyncHttpClient getAuthHeader(AsyncHttpClient client) {
        // add authenticate header
        client.addHeader("Authorization", Util.getApiToken());
        return client;
    }

    protected void setupClient() {
        // start async http client
        client = new AsyncHttpClient();
        client.setTimeout(AuthenticatedHttp.CONNECTION_TIMEOUT);
    }
}
