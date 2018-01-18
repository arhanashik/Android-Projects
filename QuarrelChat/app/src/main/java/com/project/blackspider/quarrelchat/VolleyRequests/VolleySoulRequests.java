package com.project.blackspider.quarrelchat.VolleyRequests;

/**
 * Created by Mr blackSpider on 5/5/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.quarrelchat.Activities.WelcomeActivity;
import com.project.blackspider.quarrelchat.CustomView.CatLoadingView;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VolleySoulRequests {
    private static Context context;
    private static FragmentManager fragmentManager;
    private static ProgressDialog progressDialog;

    private static String fromSoulmate;
    private static String toSoulmate;
    private static String timestamp;
    private static String targetFcmId;

    private static RequestQueue requestQueue;
    private static DBHelper dbHelper;

    private static CatLoadingView catLoadingView;

    int sdk = android.os.Build.VERSION.SDK_INT;

    public VolleySoulRequests(Context context, FragmentManager fragmentManager){
        this.context = context;
        this.fragmentManager = fragmentManager;

        requestQueue = Volley.newRequestQueue(context);
        catLoadingView = new CatLoadingView();
        dbHelper = new DBHelper(context);
    }

    public void sendSoulRequest(String fromSoulmate, String toSoulmate, String timestamp, String targetFcmId){
        this.fromSoulmate = fromSoulmate;
        this.toSoulmate = toSoulmate;
        this.timestamp = timestamp;
        this.targetFcmId = targetFcmId;
        doVolleyRequest(FinalVariables.FLAG_SOUL_REQUEST_SEND);
    }

    public void acceptSoulRequest(String fromSoulmate, String toSoulmate, String timestamp, String targetFcmId){
        this.fromSoulmate = fromSoulmate;
        this.toSoulmate = toSoulmate;
        this.timestamp = timestamp;
        this.targetFcmId = targetFcmId;
        doVolleyRequest(FinalVariables.FLAG_SOUL_REQUEST_ACCEPT);
    }

    public void cancelSoulRequest(String fromSoulmate, String toSoulmate, String timestamp, String targetFcmId){
        this.fromSoulmate = fromSoulmate;
        this.toSoulmate = toSoulmate;
        this.timestamp = timestamp;
        this.targetFcmId = targetFcmId;
        doVolleyRequest(FinalVariables.FLAG_SOUL_REQUEST_CANCEL);
    }

    public void removeSoulmate(String fromSoulmate, String toSoulmate, String timestamp, String targetFcmId){
        this.fromSoulmate = fromSoulmate;
        this.toSoulmate = toSoulmate;
        this.timestamp = timestamp;
        this.targetFcmId = targetFcmId;
        doVolleyRequest(FinalVariables.FLAG_SOUL_REMOVE);
    }

    private void doVolleyRequest(final int flag){
        if (sdk < Build.VERSION_CODES.LOLLIPOP) {
            progressDialog  = new ProgressDialog(context);
            progressDialog.setMessage("PLEASE WAIT...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        else {
            catLoadingView.setText("PLEASE WAIT...");
            catLoadingView.show(fragmentManager, "");
            catLoadingView.setCancelable(false);
        }

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
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
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
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
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
    String message = "";
    private void handleJSON(JSONObject jsonObject, final int flag){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){
                success = FinalVariables.SUCCESS;
                message = jsonObject.getString("message");

                if(flag==FinalVariables.FLAG_SOUL_REQUEST_SEND
                        || flag==FinalVariables.FLAG_SOUL_REQUEST_ACCEPT
                        || flag==FinalVariables.FLAG_SOUL_REQUEST_CANCEL
                        || flag==FinalVariables.FLAG_SOUL_REMOVE){
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }

                if(flag==FinalVariables.FLAG_SOUL_REMOVE) {
                    dbHelper.dropSingleChatMessagesTable(fromSoulmate, toSoulmate);
                }

            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                Log.e("Failure ", message);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
