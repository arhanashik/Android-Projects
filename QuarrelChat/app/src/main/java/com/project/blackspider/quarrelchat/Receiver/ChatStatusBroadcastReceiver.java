package com.project.blackspider.quarrelchat.Receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mr blackSpider on 12/26/2016.
 */

public class ChatStatusBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Got it: "+intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();
    }
}
