package com.project.blackspider.quarrelchat.VolleyRequests;

/**
 * Created by Mr blackSpider on 5/5/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.FragmentManager;
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
import com.project.blackspider.quarrelchat.Model.ChatStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VolleyAccountSettings {
    private static Context context;
    private static Activity activity;
    private static FragmentManager fragmentManager;

    private static String username;
    private static String newName;
    private static String newPhone;
    private static String oldPassword;
    private static String newPassword;
    private static String fcmId;

    private static RequestQueue requestQueue;
    private static DBHelper dbHelper;
    private static ChatStatus chatStatus;

    private static CatLoadingView catLoadingView;
    private static ProgressDialog progressDialog;
    private static Intent intent;

    private static SharedPreferences sharedPreferencesSession;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    int sdk = android.os.Build.VERSION.SDK_INT;

    public VolleyAccountSettings(Activity activity, FragmentManager fragmentManager){
        this.activity = activity;
        this.fragmentManager = fragmentManager;

        this.context = activity.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        catLoadingView = new CatLoadingView();
        dbHelper = new DBHelper(context);
    }

    public void changeName(String username, String newName){
        this.username = username;
        this.newName = newName;
        doVolleyRequest(FinalVariables.FLAG_CHANGE_NAME);
    }

    public void changePhone(String username, String newPhone){
        this.username = username;
        this.newPhone = newPhone;
        doVolleyRequest(FinalVariables.FLAG_CHANGE_PHONE);
    }

    public void changePassword(String username, String oldPassword, String newPassword){
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        doVolleyRequest(FinalVariables.FLAG_CHANGE_PASSWORD);
    }

    public void logoutFromServer(String username, String fcmId){
        this.username = username;
        this.fcmId = fcmId;
        sharedPreferencesSession = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);

        doVolleyRequest(FinalVariables.FLAG_LOGOUT);
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

        if(flag==FinalVariables.FLAG_CHANGE_NAME) url = FinalVariables.CHANGE_NAME_URL;
        else if(flag==FinalVariables.FLAG_CHANGE_PHONE) url = FinalVariables.CHANGE_PHONE_URL;
        else if(flag==FinalVariables.FLAG_CHANGE_PASSWORD) url = FinalVariables.CHANGE_PASSWORD_URL;
        else if(flag==FinalVariables.FLAG_LOGOUT) url = FinalVariables.UPDATE_FCM_ID_URL;

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
                params.put("username", username);
                if (flag==FinalVariables.FLAG_CHANGE_NAME) params.put("name", newName);
                else if(flag==FinalVariables.FLAG_CHANGE_PHONE) params.put("phone", newPhone);
                else if(flag==FinalVariables.FLAG_CHANGE_PASSWORD){
                    params.put("old_password", oldPassword);
                    params.put("new_password", newPassword);
                }
                else if(flag==FinalVariables.FLAG_CHANGE_PHONE) params.put(FinalVariables.KEY_FCM_ID, fcmId);

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

                if(flag==FinalVariables.FLAG_LOGOUT){
                    sharedPreferencesEditor = sharedPreferencesSession.edit();
                    sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, false);
                    sharedPreferencesEditor.commit();
                    intent = new Intent(activity, WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                    msg = "Successfully logged out.";
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
}
