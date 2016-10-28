package com.heytz.iflytek.listener;

import org.apache.cordova.CallbackContext;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Alben on 16/10/28.
 */
public class HeytzListener {

    // 听写后的回调
    protected CallbackContext callback = null;

    // 听写结果
    protected HashMap<String, String> results = new LinkedHashMap<String, String>();

    public CallbackContext getCallback() {
        return this.callback;
    }

    public void setCallback(CallbackContext callback) {
        this.callback = callback;
    }

    protected boolean hasCallbackContext() {
        return callback != null;
    }
}
