package com.project.blackspider.classschedule.FCM;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.blackspider.classschedule.Activities.WelcomeActivity;
import com.project.blackspider.classschedule.Models.Message;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Services.OverlayService;
import com.project.blackspider.classschedule.Utils.DateAndTime;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr blackSpider on 12/22/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private static final String SUBJECT_NOTIFICATION = "notification";
    private static final String SUBJECT_SCHEDULE_POST = "schedule_post";
    private static final String SUBJECT_FEED_POST = "feed_post";
    private static final String SUBJECT_SCHEDULE = "schedule";
    private static final String SUBJECT_CHAT = "chat";
    private static final String SUBJECT_BROADCAST = "broadcast";

    private String senderName = "";
    private String senderEmail = "";
    private ArrayList<String> lastSignInInfo = new ArrayList<>();

    private FinalVariables finalVariables = new FinalVariables();
    private DateAndTime dateAndTime = new DateAndTime();
    private DBHelper dbHelper = new DBHelper(this);

    private LayoutInflater mInflater;

    private PowerManager powerManager;
    private KeyguardManager keyguardManager;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if(remoteMessage.getData().get("subject").equals(SUBJECT_CHAT)) {
                senderName = remoteMessage.getData().get("sender_name");
                senderEmail = remoteMessage.getData().get("sender_email");
                Log.d(TAG, "Sender: " + senderName);
            }

            sendNotification(remoteMessage,""+remoteMessage.getData().get("subject"));
            //if(isScreenLocked()) {
                //createDialog("" + remoteMessage.getData().get("message"), "" + remoteMessage.getData().get("subject"));
                Intent intent = new Intent(this, OverlayService.class);
                startService(intent);
            //}
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage,"Notification");
            if(isScreenLocked()) {
                //createDialog(remoteMessage.getNotification().getBody(),"Notification");
            }
        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(RemoteMessage messageBody, String subject) {
        Intent intent1 = new Intent();
        if(subject.equals(SUBJECT_CHAT)){
            lastSignInInfo = dbHelper.getLastSignInInfo();
            Message message = new Message(messageBody.getData().get("message"), FinalVariables.RECEIVED_MESSAGE, "", "",
                    dateAndTime.currentDateAndTimeFormate4());
            String tableName = lastSignInInfo.get(FinalVariables.EMAIL_INDEX).replace(".","_").replace("@","_")+
                    "_"+ FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                    senderEmail.replace(".","_").replace("@","_");
            dbHelper.saveSingleChatMessage(tableName, message);
            intent1.setAction("com.project.blackspider.classschedule.CHAT_INTENT");
            intent1.putExtra("flag", "0");
            subject = senderName;

        }else if(subject.equals(SUBJECT_BROADCAST)){
            intent1.setAction("com.project.blackspider.classschedule.CHAT_INTENT");
            intent1.putExtra("flag", "0");
            subject = "Broadcast Message";

        } else if(subject.equals(SUBJECT_FEED_POST)){
            intent1.setAction("com.project.blackspider.classschedule.FEED_POST_INTENT");
            intent1.putExtra("POST_SL", messageBody.getData().get("sl"));
            subject = messageBody.getData().get("subtitle");

        }else if(subject.equals(SUBJECT_SCHEDULE_POST)){
            intent1.setAction("com.project.blackspider.classschedule.SCHEDULE_POST_INTENT");
            intent1.putExtra("POST_SL", messageBody.getData().get("sl"));
            subject = messageBody.getData().get("subtitle");

        }else if(subject.equals(SUBJECT_NOTIFICATION)){
            intent1.setAction("com.project.blackspider.classschedule.NOTIFICATION_INTENT");
            subject = "Notification";

        }else if(subject.isEmpty()){

        }
        //if(isAppInForground()){

        intent1.putExtra("message", messageBody.getData().get("message"));
        sendBroadcast(intent1);

        //}else {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("message", messageBody.getData().get("message"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri= Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.notification);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_ok)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_class_schedule2))
                .setContentTitle(""+subject)
                .setContentText(""+messageBody.getData().get("message"))
                .setTicker(messageBody.getData().get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        //}

    }

    private void createDialog(String message, String subject) {
        WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mInflater = LayoutInflater.from(getApplicationContext());
        View mView = mInflater.inflate(R.layout.layout_lock_screen_notification, null, true);

        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
/* | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON */,
                PixelFormat.RGBA_8888);

        mWindowManager.addView(mView, mLayoutParams);

    }

    private Boolean isAppInForground(){
        //String runningProcess = "com.project.blackspider.classschedule";
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList){
            if(appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                if(appProcessInfo.processName.equals(getPackageName())){
                    return true;
                }

            }

        }

        return false;
    }

    private Boolean isScreenLocked(){
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        Boolean awake;
        Boolean locked = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) awake = powerManager.isInteractive();
        else awake = powerManager.isScreenOn();

        if(!awake) locked = keyguardManager.inKeyguardRestrictedInputMode();

        return locked;
    }
}
