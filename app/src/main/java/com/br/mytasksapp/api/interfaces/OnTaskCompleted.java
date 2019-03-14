package com.br.mytasksapp.api.interfaces;

import org.json.JSONObject;

public interface OnTaskCompleted {
    void taskCompleted(JSONObject results);
}