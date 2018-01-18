package com.project.blackspider.classschedule.FCM;

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
import com.project.blackspider.classschedule.Activities.WelcomeActivity;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public class Update_FCM_ID {
    private Context context;
    private String email;

    public Update_FCM_ID(Context context, String email){
        this.context = context;
        this.email = email;
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

                if (response.isEmpty())
                    //Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
                    sendNotification(message);
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
                sendNotification(message);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put(FinalVariables.KEY_EMAIL, email);
                params.put(FinalVariables.KEY_FCM_DEVICE_REG_ID, value);

                //returning parameters
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
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
                sendNotification(message);

            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                Log.e("Failure ", message);
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                sendNotification(message);
            }

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            //Toast.makeText(context, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            sendNotification(message);
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
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_class_schedule2))
                .setContentTitle("Class Schedule :: Token Update")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }
}
