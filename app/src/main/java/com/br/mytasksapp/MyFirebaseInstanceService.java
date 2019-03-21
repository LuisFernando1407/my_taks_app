package com.br.mytasksapp;

import com.br.mytasksapp.api.rest.TaskHttp;
import com.br.mytasksapp.model.Task;
import com.br.mytasksapp.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceService extends FirebaseInstanceIdService {
    private TaskHttp taskHttp;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        taskHttp = new TaskHttp(MyTaskApplication.getInstance(), null);

        String token = FirebaseInstanceId.getInstance().getToken();

        Util.setApiFCMToken(token);

        if(Util.containsPref("first_access") && Util.getPref("first_access", null).equals("no")){
            taskHttp.setFCMToken(Util.containsPref("is_accepted") ? Util.containsPref("is_accepted") : true);
        }
    }
}