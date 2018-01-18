package com.project.blackspider.quarrelchat.FCM;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Mr blackSpider on 12/22/2016.
 */

public class SendFCMPushNotification {
    private Context context;

    private String senderEmail = "";
    private ArrayList<String> senderInfo;

    private String receiverEmail;
    private String receiverRegistrationToken;
    private String receiversTableName;
    private String success = "0";
    private String message = "No message";
    private String date = "";
    private ArrayList<String> allReceiversRegistrationToken;

    private FinalVariables finalVariables = new FinalVariables();
//    private DBHelper dbHelper;
//    private DateAndTime dateAndTime = new DateAndTime();

    private ProgressDialog progressDialog;

    public SendFCMPushNotification(Context context){
        this.context = context;
        //dbHelper = new DBHelper(context);
    }

    public void singleChat(String receiverEmail, String receiverRegistrationToken, String message){
        this.receiverEmail = receiverEmail;
        this.receiverRegistrationToken = receiverRegistrationToken;
        this.message = message;

        senderInfo = new ArrayList<>();
        //senderInfo = dbHelper.getUserInfo();
        //senderEmail = senderInfo.get(FinalVariables.EMAIL_INDEX);
        //date = dateAndTime.currentDateAndTimeFormate4();
        sendSingleChatMessage();
    }

    public void groupChat(ArrayList<String> allReceiverRegistrationToken, String message){
        this.allReceiversRegistrationToken = allReceiverRegistrationToken;
        this.message = message;
    }

    public void broadcastMessage(ArrayList<String> allReceiverRegistrationToken, String message){
        this.allReceiversRegistrationToken = allReceiverRegistrationToken;
        this.message = message;
        sendBroadcastMessage();
    }

    public void newPostNotificatin(String receiversTableName){
        this.receiversTableName = receiversTableName;
    }

    private void sendSingleChatMessage(){
        //progressDialog = new ProgressDialog(context);
        //progressDialog.setCancelable(false);
        //progressDialog.setMessage("Sending...");
        //progressDialog.show();

        String url = FinalVariables.SEND_PUSH_NOTIFICATION_URL;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", "From server: " + response);
                //progressDialog.dismiss();
                if (response.isEmpty())
                    Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
                else {
                    //Toast.makeText(context, "Response: " + response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, FinalVariables.FLAG_SINGLE_CHAT);
                    } catch (JSONException e) {

                    }
                    //showAlert("Response: "+response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Error: ","Volley Error: "+volleyError);
                //progressDialog.dismiss();
                Toast.makeText(context, "Volley Error: " + volleyError, Toast.LENGTH_LONG).show();
                //showAlert("Volley Error: "+volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("id", receiverRegistrationToken);
                params.put("sender_email", senderEmail);
                params.put("message", message);
                params.put("receiver_email", receiverEmail);
                params.put("date", date);

                //returning parameters
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        queue.add(request);
    }

    private void sendBroadcastMessage(){
        String url = FinalVariables.SEND_PUSH_NOTIFICATION_URL;
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response: ", "From server: " + response);

                if (response.isEmpty())
                    Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
                else {
                    //Toast.makeText(context, "Response: " + response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, FinalVariables.FLAG_SEND_BROADCAST_MESSAGE);
                    } catch (JSONException e) {

                    }
                    showAlert("Response: "+response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Error: ","Volley Error: "+volleyError);
                //Toast.makeText(context, "Volley Error: " + volleyError, Toast.LENGTH_LONG).show();
                showAlert("Volley Error: "+volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("id", allReceiversRegistrationToken +"");
                params.put("message", message);

                //returning parameters
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        queue.add(request);
    }

    private void handleJSON(JSONObject jsonObject, int flag){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);
            String report = "";
            Intent intent1 = new Intent();
            String tableName = senderEmail.replace(".","_").replace("@","_")+
                    "_"+ FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                    receiverEmail.replace(".","_").replace("@","_");

            if(what.equals(FinalVariables.SUCCESS)){

                if(flag == FinalVariables.FLAG_SINGLE_CHAT){
                    report = "Sent";
//                    Soulmate msg = new Soulmate(message, FinalVariables.SENT_MESSAGE, report,
//                            "--", date);
//                    dbHelper.saveSingleChatMessage(tableName, msg);
                }

            }else {

                if(flag == FinalVariables.FLAG_SINGLE_CHAT){
                    report = "Failed";
//                    Soulmate msg = new Soulmate(message, FinalVariables.SENT_MESSAGE, report,
//                            "--", date);
//                    dbHelper.saveSingleChatMessage(tableName, msg);
                }

            }
            intent1.setAction("com.project.blackspider.quarrelchat.CHAT_INTENT");
            intent1.putExtra("message", message);
            intent1.putExtra("report", report);
            intent1.putExtra("flag", "1");
            context.sendBroadcast(intent1);

            success = what;
            message = jsonObject.getString("message");
            Log.e("Success ", success);
            Log.e("Soulmate ", message);
            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            Toast.makeText(context, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
