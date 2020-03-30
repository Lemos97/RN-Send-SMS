package com.reactlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    public void send(final String phone_to, String message, final Callback callback) {
        ReactApplicationContext context = getContext();

        boolean checkPermissions = Utils.hasSendSMSPermissions(context);
        if (!checkPermissions)
        {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,new Intent(DELIVERED), 0);

            BroadcastReceiver sendedBroadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    switch(getResultCode())
                    {
                        case Activity.RESULT_OK:
                            callback.invoke("SMS sent to: " + phone_to);
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            callback.invoke("SMS failed to be sent : " + phone_to + " the Error being: " + SmsManager.RESULT_ERROR_GENERIC_FAILURE);
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            callback.invoke("SMS failed to be sent : " + phone_to + " the Error being: " + SmsManager.RESULT_ERROR_NO_SERVICE);
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            callback.invoke("SMS failed to be sent : " + phone_to + " the Error being: " + SmsManager.RESULT_ERROR_NULL_PDU);
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            callback.invoke("SMS failed to be sent : " + phone_to + " the Error being: " + SmsManager.RESULT_ERROR_RADIO_OFF);
                            break;
                    }
                }
            };

            BroadcastReceiver deliveredBroadcastReceiver = new BroadcastReceiver()
            {

                @Override
                public void onReceive(Context context, Intent intent)
                {
                    switch(getResultCode())
                    {
                        case Activity.RESULT_OK:
                            callback.invoke("SMS delivered to: " + phone_to);

                            break;
                        case Activity.RESULT_CANCELED:
                            callback.invoke("SMS was not delivered to: " + phone_to + " the Error being :" + Activity.RESULT_CANCELED);
                            break;
                    }
                }
            };

            // ---when the SMS has been sent---
            context.registerReceiver(sendedBroadcastReceiver, new IntentFilter(SENT));

            // ---when the SMS has been delivered---
            context.registerReceiver( deliveredBroadcastReceiver, new IntentFilter(DELIVERED));

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone_to, null, message, sentPI, deliveredPI);

            context.unregisterReceiver(sendedBroadcastReceiver);
            context.unregisterReceiver(deliveredBroadcastReceiver);
        }
    }
}
