package com.reactlibrary;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class SendSmsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;


    public SendSmsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    private ReactApplicationContext getContext() {
        return getReactApplicationContext();
    }

    @Override
    public String getName() {
        return "SendSms";
    }

    @ReactMethod
    public void send(String phone_to, String message, Callback callback) {
        ReactApplicationContext context = getContext();

        boolean checkPermissions = Utils.hasSendSMSPermissions(context);
        if (!checkPermissions)
        {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone_to, null, message, null, null);
        }

        callback.invoke("Sent SMS to: " + phone_to);
    }
}
