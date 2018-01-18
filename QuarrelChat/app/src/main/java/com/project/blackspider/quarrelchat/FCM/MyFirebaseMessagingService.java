package com.project.blackspider.quarrelchat.FCM;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.blackspider.quarrelchat.Activities.GreetingsActivity;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Model.ChatReport;
import com.project.blackspider.quarrelchat.Model.Message;
import com.project.blackspider.quarrelchat.Model.Soulmate;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.VolleyRequests.VolleySoulMessaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by Mr blackSpider on 12/22/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    private static int notificationID;
    private static String subject;

    private String senderName = "";
    private String senderUsername = "";
    private String senderFcmId = "";
    private String receiverUsername = "";
    private int msgSl = 0;
    private int msgReport = 0;
    private int unseenMsgCount = 0;
    private String timestamp = "";
    private boolean isFriend = false;
    private boolean isAvailable = false;
    private boolean isOnline = false;
    private boolean isTyping = false;
    private boolean isChatting = false;

    private DBHelper dbHelper = new DBHelper(this);
    private Soulmate soulmate;
    private Message message;

    private LayoutInflater mInflater;
    Intent intent1;

    private PowerManager powerManager;
    private KeyguardManager keyguardManager;

    private SharedPreferences sharedPreferencesMyStatus;
    private SharedPreferences.Editor sharedPreferencesMyStatusEditor;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Soulmate data payload: " + remoteMessage.getData());
            subject = remoteMessage.getData().get("subject");
            senderName = remoteMessage.getData().get("name");
            senderUsername = remoteMessage.getData().get("from_soulmate");
            senderFcmId = remoteMessage.getData().get("sender_fcm_id");
            receiverUsername = remoteMessage.getData().get("to_soulmate");
            timestamp = remoteMessage.getData().get("timestamp");
            isFriend = Boolean.valueOf(remoteMessage.getData().get("is_friend"));

            if(subject.equals(FinalVariables.SUBJECT_CHAT)) {
                msgSl = Integer.parseInt(remoteMessage.getData().get("msg_sl"));
                subject = FinalVariables.SUBJECT_CHAT;
                Log.d(TAG, "Sender: " + senderName+", Msg sl: " + msgSl);
                ChatReport chatReport = new ChatReport();
                chatReport.setFrom(receiverUsername);
                chatReport.setTo(senderUsername);
                chatReport.setTimestamp(String.valueOf(System.currentTimeMillis()));
                chatReport.setMyFcmId(FirebaseInstanceId.getInstance().getToken());
                chatReport.setTargetFcmId(senderFcmId);
                chatReport.setFriend(isFriend);
                chatReport.setSl(msgSl);
                chatReport.setReport(FinalVariables.MSG_DELIVERED);
                new VolleySoulMessaging(this).sendChatReport(chatReport);
                notificationID = FinalVariables.CHAT_NOTIFICATION_ID;
                showNotification(remoteMessage,""+subject);

            } else if(subject.equals(FinalVariables.SUBJECT_REPORT)){
                msgSl = Integer.parseInt(remoteMessage.getData().get("msg_sl"));
                msgReport = Integer.parseInt(remoteMessage.getData().get("msg_report"));
                Log.d(TAG, "Sender: " + senderName+", Receiver: "+receiverUsername);
                Log.d(TAG, "Msg sl: "+msgSl+", report: "+msgReport);
                if(isFriend && msgSl > 0) dbHelper.updateSingleChatMessageReport(receiverUsername, senderUsername,
                        FinalVariables.MSG_DELIVERED, msgSl);

                intent1 = new Intent();
                intent1.setAction(FinalVariables.ACTION_REPORT_INTENT);
                intent1.putExtra("isFriend", isFriend);
                intent1.putExtra("senderUsername", senderUsername);
                intent1.putExtra("senderFcmId", senderFcmId);
                intent1.putExtra("timestamp", timestamp);
                intent1.putExtra("msg_sl", msgSl);
                intent1.putExtra("msg_report", msgReport);
                sendBroadcast(intent1);


            } else if(subject.equals(FinalVariables.SUBJECT_STATUS)){
                subject = FinalVariables.SUBJECT_STATUS;
                isAvailable = Boolean.valueOf(remoteMessage.getData().get(FinalVariables.IS_AVAILABLE));
                isOnline = Boolean.valueOf(remoteMessage.getData().get(FinalVariables.IS_ONLINE));
                isTyping = Boolean.valueOf(remoteMessage.getData().get(FinalVariables.IS_TYPING));
                sharedPreferences = getSharedPreferences(senderUsername, Context.MODE_PRIVATE);
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putBoolean(FinalVariables.IS_AVAILABLE, isAvailable);
                sharedPreferencesEditor.putBoolean(FinalVariables.IS_ONLINE, isOnline);
                sharedPreferencesEditor.putBoolean(FinalVariables.IS_TYPING, isTyping);
                sharedPreferencesEditor.commit();

                if(isOnline) dbHelper.updateSingleChatMessageReport(receiverUsername, senderUsername,
                        FinalVariables.MSG_SEEN, FinalVariables.FLAG_UPDATE_MSG_REPORT_ALL_SEEN);

                intent1 = new Intent();
                intent1.setAction(FinalVariables.ACTION_STATUS_INTENT);
                intent1.putExtra("isFriend", isFriend);
                intent1.putExtra("senderUsername", senderUsername);
                intent1.putExtra("senderFcmId", senderFcmId);
                intent1.putExtra("timestamp", timestamp);
                intent1.putExtra(FinalVariables.IS_AVAILABLE, isAvailable);
                intent1.putExtra(FinalVariables.IS_ONLINE, isOnline);
                intent1.putExtra(FinalVariables.IS_TYPING, isTyping);
                sendBroadcast(intent1);
            }
       }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Soulmate Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage, "Notification");
            notificationID = FinalVariables.NOTIFICATION_ID;
        }

    }

    private void showNotification(RemoteMessage messageBody, String subject) {
        intent1 = new Intent();
        if(subject.equals(FinalVariables.SUBJECT_CHAT)){
            intent1.setAction(FinalVariables.ACTION_CHAT_INTENT);
            intent1.putExtra("isFriend", isFriend);
            intent1.putExtra("senderUsername", senderUsername);
            intent1.putExtra("senderFcmId", senderFcmId);
            intent1.putExtra("timestamp", timestamp);
            if(isFriend){
                soulmate = new Soulmate();
                soulmate = dbHelper.getLastSigninInfo();
                message = new Message();
                message.setMessage(messageBody.getData().get("body"));
                message.setReport(FinalVariables.MSG_RECEIVED);
                message.setTimestamp(String.valueOf(System.currentTimeMillis()));
                String tableName = receiverUsername.replace(".","_").replace("@","_")+
                        FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                        senderUsername.replace(".","_").replace("@","_");
                dbHelper.saveSingleChatMessage(tableName, message);
                intent1.putExtra("tableName", tableName);
            }
            intent1.putExtra("flag", FinalVariables.MSG_RECEIVED);

            sharedPreferencesMyStatus = getSharedPreferences(receiverUsername+"_chat_with_"+senderUsername, Context.MODE_PRIVATE);
            isChatting = sharedPreferencesMyStatus.getBoolean(FinalVariables.IS_CHATTING, false);
            unseenMsgCount = sharedPreferencesMyStatus.getInt(FinalVariables.UNSEEN_MSG, 0)+1;
            sharedPreferencesMyStatusEditor = sharedPreferencesMyStatus.edit();
            if(!isChatting) {
                sharedPreferencesMyStatusEditor.putInt(FinalVariables.UNSEEN_MSG, unseenMsgCount);
                buildNotification(messageBody.getData().get("body"), senderName, unseenMsgCount);
            }else {
                sharedPreferencesMyStatusEditor.putInt(FinalVariables.UNSEEN_MSG, 0);
            }
            sharedPreferencesMyStatusEditor.commit();

        }else if(subject.equals(FinalVariables.SUBJECT_NOTIFICATION)){
            intent1.setAction(FinalVariables.ACTION_NOTIFICATION_INTENT);
            subject = "Notification";
            buildNotification(messageBody.getNotification().getBody(), subject, 0);

        }else if(subject.isEmpty()){

        }
        intent1.putExtra("message", messageBody.getData().get("body"));
        sendBroadcast(intent1);
    }

    private void buildNotification(String msg, String subject, int msgCount){
        Intent intent = new Intent(this, GreetingsActivity.class);
        intent.putExtra("message", msg);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri= Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.notification);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(""+subject)
                .setContentText(""+msg)
                .setTicker(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                //.setNumber(7)
                .setContentIntent(pendingIntent);

        if(msgCount>0) notificationBuilder.setNumber(msgCount);

        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID /* ID of notification */, notification);
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
