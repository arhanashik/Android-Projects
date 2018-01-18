package com.project.blackspider.quarrelchat.VolleyRequests;

/**
 * Created by Mr blackSpider on 5/5/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.blackspider.quarrelchat.Activities.WelcomeActivity;
import com.project.blackspider.quarrelchat.CustomView.CatLoadingView;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Model.ChatReport;
import com.project.blackspider.quarrelchat.Model.ChatStatus;
import com.project.blackspider.quarrelchat.Model.Message;
import com.project.blackspider.quarrelchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VolleySoulMessaging {
    private static Context context;
    private static FragmentManager fragmentManager;
    private static Intent intent;

    private static String fromSoulmate;
    private static String toSoulmate;
//    private static String targetMessage;
    private static String timestamp;
    private static String targetFcmId;
//    public static boolean isFriend = false;
//    public static boolean isAvailable = false;
//    public static boolean isOnline = false;
//    public static boolean isTyping = false;

    private static RequestQueue requestQueue;
    private static DBHelper dbHelper;
    private static Message message;
    private static ChatStatus chatStatus;

    private static CatLoadingView catLoadingView;

    public VolleySoulMessaging(Context context){
        this.context = context;

        requestQueue = Volley.newRequestQueue(context);
        dbHelper = new DBHelper(context);
    }

    public VolleySoulMessaging(Context context, FragmentManager fragmentManager){
        this.context = context;
        this.fragmentManager = fragmentManager;

        requestQueue = Volley.newRequestQueue(context);
        catLoadingView = new CatLoadingView();
        dbHelper = new DBHelper(context);
    }


    public void sendFCMMessage(final Message message, final boolean isFriend, final boolean isOnline) {
        final String Legacy_SERVER_KEY = FinalVariables.Legacy_SERVER_KEY;
        String msg = message.getMessage();
        int msgSl = message.getSl();
        final String from = message.getFrom();
        String name = message.getMyName();
        final String to = message.getTo();
        String timestamp = message.getTimestamp();
        String senderFcmId = FirebaseInstanceId.getInstance().getToken();//message.getMyFcmId();
        String imagePath = message.getImagePath();
        if(!imagePath.contains("http://")) imagePath = "http://"+imagePath;
        String subject = FinalVariables.SUBJECT_CHAT;
        String token = message.getTargetFcmId();
        String type = String.valueOf(message.getType());
        String isFrnd = String.valueOf(isFriend);

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        intent = new Intent();
        intent.setAction(FinalVariables.ACTION_REPORT_INTENT);
        intent.putExtra("isFriend", isFriend);
        intent.putExtra("senderUsername", to);
        intent.putExtra("senderFcmId", senderFcmId);
        intent.putExtra("timestamp", timestamp);
        intent.putExtra("msg_sl", msgSl);

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("msg_sl", msgSl);
            objData.put("body", msg);
            objData.put("subject", subject);
            objData.put("from_soulmate", from);
            objData.put("name", name);
            objData.put("to_soulmate", to);
            objData.put("timestamp", timestamp);
            objData.put("image_path", imagePath);
            objData.put("sender_fcm_id", senderFcmId);
            objData.put("type", type);
            objData.put("is_friend", isFrnd);
            objData.put(FinalVariables.IS_ONLINE, isOnline);
            objData.put("priority", "high");

//            dataobjData = new JSONObject();

            obj.put("to", token);

            obj.put("notification", null);
            obj.put("data", objData);
            Log.e("FCM FORMAT:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FinalVariables.FCM_PUSH_URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int report = FinalVariables.MSG_FAILED;
                        boolean updated = false;
                        Log.e("FCM Response:>", response.toString());
                        try {
                            if(response.getString("success").equals("1")) {
                                if(isOnline) report = FinalVariables.MSG_SEEN;
                                else report = FinalVariables.MSG_SENT;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        message.setReport(report);
                        if(isFriend) updated=dbHelper.updateSingleChatMessageReport(from, to, report, message.getSl());
                        Log.e("Report updated:>", report + "");
                        intent.putExtra("msg_report", report);
                        context.sendBroadcast(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        boolean updated = false;
                        message.setReport(FinalVariables.MSG_FAILED);
                        if(isFriend) updated=dbHelper.updateSingleChatMessageReport(from, to, FinalVariables.MSG_FAILED, message.getSl());
                        Log.e("Report updated:>", FinalVariables.MSG_FAILED+"");
                        Log.e("FCM Error:>", error + "");
                        intent.putExtra("msg_report", FinalVariables.MSG_FAILED);
                        context.sendBroadcast(intent);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return checkParams(params);
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void sendChatReport(final ChatReport chatReport) {
        final String Legacy_SERVER_KEY = FinalVariables.Legacy_SERVER_KEY;
        final String from = chatReport.getFrom();
        //String name = chatReport.getMyName();
        final String to = chatReport.getTo();
        String timestamp = chatReport.getTimestamp();
        String senderFcmId = chatReport.getMyFcmId();
        String subject = FinalVariables.SUBJECT_REPORT;
        String token = chatReport.getTargetFcmId();
        String isFrnd = String.valueOf(chatReport.isFriend());
        String msgSl = String.valueOf(chatReport.getSl());
        String msgReport = String.valueOf(chatReport.getReport());

        JSONObject obj = null;
        JSONObject objData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("subject", subject);
            objData.put("from_soulmate", from);
            objData.put("name", from);
            objData.put("to_soulmate", to);
            objData.put("timestamp", timestamp);
            objData.put("sender_fcm_id", senderFcmId);
            objData.put("is_friend", isFrnd);
            objData.put("msg_sl", msgSl);
            objData.put("msg_report", msgReport);
            objData.put("priority", "high");

            obj.put("to", token);

            obj.put("notification", null);
            obj.put("data", objData);
            Log.e("CHAT REPORT FORMAT:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FinalVariables.FCM_PUSH_URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("CHAT REPORT Response:>", response.toString());
                        try {
                            if(response.getString("success").equals("1")) {
                                //
                            }else {
                                //
                            }
                            Log.e("Chat REPORT success:>", response.getString("success"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Chat REPORT success:>", "0");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Chat REPORT Error:>", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return checkParams(params);
            }
        };

        int socketTimeout = 1000 * 10;// 10 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    public void sendChatStatus(final ChatStatus chatStatus) {
        final String Legacy_SERVER_KEY = FinalVariables.Legacy_SERVER_KEY;
        final String from = chatStatus.getFrom();
        String name = chatStatus.getMyName();
        final String to = chatStatus.getTo();
        String timestamp = chatStatus.getTimestamp();
        String senderFcmId = chatStatus.getMyFcmId();
        String subject = FinalVariables.SUBJECT_STATUS;
        String token = chatStatus.getTargetFcmId();
        String isFrnd = String.valueOf(chatStatus.isFriend());
        String isAvailable = String.valueOf(chatStatus.isAvailable());
        String isOnline = String.valueOf(chatStatus.isOnline());
        String isTyping = String.valueOf(chatStatus.isTyping());

        JSONObject obj = null;
        JSONObject objData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("subject", subject);
            objData.put("from_soulmate", from);
            objData.put("name", name);
            objData.put("to_soulmate", to);
            objData.put("timestamp", timestamp);
            objData.put("sender_fcm_id", senderFcmId);
            objData.put("is_friend", isFrnd);
            objData.put(FinalVariables.IS_AVAILABLE, isAvailable);
            objData.put(FinalVariables.IS_ONLINE, isOnline);
            objData.put(FinalVariables.IS_TYPING, isTyping);
            objData.put("priority", "high");

            obj.put("to", token);

            obj.put("notification", null);
            obj.put("data", objData);
            Log.e("CHAT STATUS FORMAT:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FinalVariables.FCM_PUSH_URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("CHAT STATUS Response:>", response.toString());
                        try {
                            if(response.getString("success").equals("1")) {
                                //
                            }else {
                                //
                            }
                            Log.e("Chat Status sent:>", response.getString("success"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Chat Status sent:>", "0");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Chat Status Error:>", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return checkParams(params);
            }
        };

        int socketTimeout = 1000 * 10;// 10 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    private void doVolleyRequest(final int flag){
        catLoadingView.setText("PLEASE WAIT...");
        catLoadingView.show(fragmentManager, "");
        catLoadingView.setCancelable(false);

        String url = FinalVariables.SEND_SOUL_REQUEST_URL;

        if(flag==FinalVariables.FLAG_SOUL_REQUEST_ACCEPT) url = FinalVariables.ACCEPT_SOUL_REQUEST_URL;
        else if(flag==FinalVariables.FLAG_SOUL_REQUEST_CANCEL) url = FinalVariables.CANCEL_SOUL_REQUEST_URL;
        else if(flag==FinalVariables.FLAG_SOUL_REMOVE) url = FinalVariables.REMOVE_SOULMATE_URL;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = "";
                res  = response;
                Log.d("Response: ",response);
                catLoadingView.dismiss();
                if(res.isEmpty()) {
                    Toast.makeText(context, "Failed! Something's wrong.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, flag);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener (){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                catLoadingView.dismiss();
                Log.d("Error: ",volleyError.toString());
                Toast.makeText(context, "Failed! Something's wrong.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                if (flag==FinalVariables.FLAG_SOUL_REQUEST_SEND
                        || flag==FinalVariables.FLAG_SOUL_REQUEST_ACCEPT
                        || flag==FinalVariables.FLAG_SOUL_REQUEST_CANCEL
                        || flag==FinalVariables.FLAG_SOUL_REMOVE){
                    params.put("from_soulmate", fromSoulmate);
                    params.put("to_soulmate", toSoulmate);
                    params.put("timestamp", timestamp);
                    params.put("target_fcm_id", targetFcmId);
                }

                return checkParams(params);
            }
        };

        requestQueue.add(stringRequest);
    }

    private Map<String, String> checkParams(Map<String, String> map){
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
            if(pairs.getValue()==null){
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }

    String success = "";
    String msg = "";
    private void handleJSON(JSONObject jsonObject, final int flag){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){
                success = FinalVariables.SUCCESS;
                msg = jsonObject.getString("message");

                if(flag==FinalVariables.FLAG_SOUL_REQUEST_SEND
                        || flag==FinalVariables.FLAG_SOUL_REQUEST_ACCEPT
                        || flag==FinalVariables.FLAG_SOUL_REQUEST_CANCEL
                        || flag==FinalVariables.FLAG_SOUL_REMOVE){
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

                if(flag==FinalVariables.FLAG_SOUL_REMOVE) {
                    dbHelper.dropSingleChatMessagesTable(fromSoulmate, toSoulmate);
                }

            }else {
                success = FinalVariables.FAILURE;
                msg = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                Log.e("Failure ", msg);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            Toast.makeText(context, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, FinalVariables.NOTIFICATION_ID /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_warning_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Soulmate :: Token Update")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(FinalVariables.NOTIFICATION_ID /* ID of notification */, notificationBuilder.build());
    }
}
