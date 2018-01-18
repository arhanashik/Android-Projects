package com.project.blackspider.quarrelchat.FCM;

/**
 * Created by Mr blackSpider on 5/5/2017.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.quarrelchat.Activities.WelcomeActivity;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class UpdateFcmId {
    private Context context;
    private String username;

    public UpdateFcmId(Context context, String username){
        this.context = context;
        this.username = username;
    }

    public void update(String id, String url){
        doVolleyRequest(id, url);
    }

    private void doVolleyRequest(final String value, String url){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", "From server: " + response);

                if (response.isEmpty()){
                    //Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
                    //sendNotification(message);
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Error: ",volleyError.toString());
                //Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                //sendNotification(message);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put(FinalVariables.KEY_USERNAME, username);
                params.put(FinalVariables.KEY_FCM_ID, value);

                //returning parameters
                return checkParams(params);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
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
    private void handleJSON(JSONObject jsonObject){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){
                success = FinalVariables.SUCCESS;
                message = jsonObject.getString("message");

                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                //sendNotification(message);

            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                Log.e("Failure ", message);
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                //sendNotification(message);
            }

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            //Toast.makeText(context, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            //sendNotification(message);
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
