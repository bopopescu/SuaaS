package com.example.santoshkumaramisagadda.summerizer;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size()>0)
        {
            String key=remoteMessage.getData().get("key");

            Intent intent= new Intent("com.example.santoshkumaramisagadda.summerizer_FCMMESSAGE");

            intent.putExtra("key",key);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(intent);

        }
    }
}
