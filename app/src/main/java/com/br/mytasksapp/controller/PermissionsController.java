package com.br.mytasksapp.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.br.mytasksapp.MyTaskApplication;

public class PermissionsController {

    // Location permissions
    public static final int INITIAL_REQUEST = 0;
    public static final String[] LOCATION_PERMS = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

    // application context
    private Context ctx;

    /**
     * Construtor
     */
    public PermissionsController() {
        this.ctx = MyTaskApplication.getInstance();
    }

    /**
     * Check if can acess location
     * @return
     */
    public boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    /**
     * Check permission for location
     * @param perm
     * @return
     */
    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == ctx.checkSelfPermission(perm));
        }

        return true;
    }
}
