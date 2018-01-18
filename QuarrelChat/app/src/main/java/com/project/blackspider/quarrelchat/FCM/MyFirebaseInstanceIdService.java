package com.project.blackspider.quarrelchat.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.project.blackspider.quarrelchat.Activities.WelcomeActivity;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.R;

import java.util.ArrayList;

/**
 * Created by Mr blackSpider on 12/22/2016.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";
    //private DBHelper dbHelper = new DBHelper(this);
    private FinalVariables finalVariables = new FinalVariables();
    private ArrayList<String> lastSignInInfo = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sharedPreferences = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_NEW_FCM_ID_INFO, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(FinalVariables.IS_NEW_FCM_ID_EXIST, true);
        sharedPreferencesEditor.putString(FinalVariables.SHARED_PREFERENCES_NEW_FCM_ID_INFO, refreshedToken);
        sharedPreferencesEditor.commit();
        sendNotification("Notification service Status: Connected");
        //sendRegistrationToServer(refreshedToken);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, FinalVariables.NOTIFICATION_ID /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Soulmate :: Token Update")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(FinalVariables.NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());
    }
}
