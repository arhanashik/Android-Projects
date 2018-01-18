package com.project.blackspider.classschedule.NewsFeed;

/**
 * Created by Mr blackSpider on 5/5/2017.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.CustomAlertDialogs.CustomAlertDialogBox;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class NewsFeedPost {
    private static final int FLAG_NEWS_FEED_POST_UPDATE = 0;
    private static final int FLAG_NEWS_FEED_POST_DELETE = 1;
    private static final int FLAG_SCHEDULE_POST_UPDATE = 2;
    private static final int FLAG_SCHEDULE_POST_DELETE = 3;
    int flag = -1;
    int position = -1;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private ProgressDialog pd;

    private FinalVariables finalVariables;
    private CustomImageConverter customImageConverter;
    private CustomAnimation customAnimation;
    private DBHelper dbHelper;
    private CustomAlertDialogBox customAlertDialogBox;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;

    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    private LayoutInflater inflater;
    private View rowView;
    private EditText editTextPost;

    private String userInput;
    private String tableName;
    private String postSl;
    private String post;

    private ArrayList<String> postmanAllInfo = new ArrayList<>();

    Context activity;
    public NewsFeedPost(Activity activity, String tableName, String postSl, String post){
        this.activity = activity;
        this.tableName = tableName;
        this.postSl = postSl;
        this.post = post;

        dbHelper = new DBHelper(activity);
        finalVariables = new FinalVariables();
        customImageConverter = new CustomImageConverter();
        customAnimation = new CustomAnimation(activity);
        customAlertDialogBox = new CustomAlertDialogBox(activity);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(activity);
        pd = new ProgressDialog(activity);

        requestQueue = Volley.newRequestQueue(activity);

        postmanAllInfo = dbHelper.getUserInfo();
    }

    public void getNewInput(final int flag){
        String positiveButtonTitle = "Yes";
        // get layout_developer.xml view
        if (flag == FLAG_NEWS_FEED_POST_UPDATE){
            alertDialogBuilder = new AlertDialog.Builder(activity);
            inflater = LayoutInflater.from(activity);
            rowView = inflater.inflate(R.layout.layout_single_post_input, null);
            editTextPost = (EditText) rowView.findViewById(R.id.editTextPost);
            editTextPost.setText(post);
            alertDialogBuilder.setView(rowView);
            positiveButtonTitle = "Update";

        }else if(flag == FLAG_NEWS_FEED_POST_DELETE){
            alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setTitle("Are you sure?");
            positiveButtonTitle = "Yes, Delete";

        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag == FLAG_NEWS_FEED_POST_UPDATE){
                            userInput = editTextPost.getText().toString();
                            if(userInput.isEmpty() || userInput.length()>500){
                                Toast.makeText(activity, "Invalid Message Length", Toast.LENGTH_SHORT).show();
                                post = userInput;
                                getNewInput(FLAG_NEWS_FEED_POST_UPDATE);

                            }else {
                                doVolleyRequest(position, FinalVariables.UPDATE_NEWS_FEED_POST_URL, FLAG_NEWS_FEED_POST_UPDATE, tableName);
                            }

                        }else if(flag == FLAG_NEWS_FEED_POST_DELETE) {
                            doVolleyRequest(position, FinalVariables.DELETE_NEWS_FEED_POST_URL, FLAG_NEWS_FEED_POST_DELETE, tableName);

                        }
                    }
                });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    private void doVolleyRequest(final int position, String url, final int flag, final String tableName){
        pd = new ProgressDialog(activity);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();

        stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("Response: ", "From server: " + response);

                if (response.isEmpty())
                    Toast.makeText(activity, "No server response", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, flag);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.d("Error: ",volleyError.toString());
                Toast.makeText(activity, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("sl", postSl+"");

                if(flag == FLAG_NEWS_FEED_POST_UPDATE){
                    params.put("post", userInput);
                    params.put("table", tableName);
                    params.put("faculty", postmanAllInfo.get(FinalVariables.FACULTY_INDEX));
                    params.put("session", postmanAllInfo.get(FinalVariables.SESSION_INDEX));

                }else if(flag == FLAG_NEWS_FEED_POST_DELETE){
                    params.put("table", tableName);

                }

                //returning parameters
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    String message = "";
    String success = "";

    private void handleJSON(JSONObject jsonObject, final int flag){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                if(flag== FLAG_NEWS_FEED_POST_UPDATE){
                    //allPosts.add(position, userInput);
                    //notifyDataSetChanged();
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    message = jsonObject.getString("data");
                    Toast.makeText(activity, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                }else if(flag== FLAG_NEWS_FEED_POST_DELETE){
                    Intent intent1 = new Intent();
                    intent1.setAction("com.project.blackspider.classschedule.POST_INTENT");
                    intent1.putExtra("POST_SL", "4");
                    String subject = "Post";
                    intent1.putExtra("message", post);
                    activity.getApplicationContext().sendBroadcast(intent1);
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }

            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                message = jsonObject.getString("data");
                Toast.makeText(activity, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
