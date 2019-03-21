package com.br.mytasksapp.fcm;

import com.br.mytasksapp.MyTaskApplication;
import com.br.mytasksapp.api.rest.TaskHttp;
import com.br.mytasksapp.util.Util;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        TaskHttp taskHttp = new TaskHttp(MyTaskApplication.getInstance(), null);

        String token = FirebaseInstanceId.getInstance().getToken();

        Util.setApiFCMToken(token);

        if(Util.containsPref("first_access") && Util.getPref("first_access", null).equals("no")){
            taskHttp.setFCMToken(Util.containsPref("is_accepted") ? Util.containsPref("is_accepted") : true);
        }
    }
}