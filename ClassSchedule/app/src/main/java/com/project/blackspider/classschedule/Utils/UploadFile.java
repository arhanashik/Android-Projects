package com.project.blackspider.classschedule.Utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Activities.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadFile {
	private ProgressDialog progressDialog;
	private Context context;
	private Bitmap bitmap;
	private String encodedImage = "";
	private String fileName = "";

	private FinalVariables finalVariables = new FinalVariables();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

	public UploadFile(Context context, Bitmap bitmap, String fileName){
		this.context = context;
		this.bitmap = bitmap;
        this.fileName = fileName;

		encodedImage = convertBitmapIntoImageString(bitmap);
        sharedPreferences = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);
	}

	private String convertBitmapIntoImageString(Bitmap bmp){
        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return imageString;
	}

	public void upload(){
		progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FinalVariables.IMAGE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
						Log.d(FinalVariables.TAG, "Volley Response: " + response);
                        //Disimissing the progress dialog
                        progressDialog.dismiss();
                        //Showing toast message of the response
                        if(response.isEmpty()) Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
                        else {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                handleJSON(jsonObject);
//                            } catch (JSONException e) {
//
//                            }


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
						Log.d(FinalVariables.TAG, "Volley Error: " + volleyError);
                        //Dismissing the progress dialog
                        progressDialog.dismiss();

                        //Showing toast
                        Toast.makeText(context, "Error: " + volleyError.getMessage(), Toast.LENGTH_LONG).show();
						//showAlert("Volley Error"+volleyError);
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put(FinalVariables.KEY_IMAGE, encodedImage);
                params.put(FinalVariables.KEY_IMAGE_NAME, fileName);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
	}

    private String message = "";
    private String success = "";
    private String image_path = "";

    private void handleJSON(JSONObject jsonObject){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) {
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = jsonObject.getString(FinalVariables.KEY_SUCCESS);
                image_path = jsonObject.getString(FinalVariables.KEY_IMAGE_PATH);

                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putBoolean(FinalVariables.IS_IMAGE_UPLOADED, true);
                sharedPreferencesEditor.putString(FinalVariables.KEY_IMAGE_PATH, image_path);
                sharedPreferencesEditor.commit();
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                Toast.makeText(context, "Signing up successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);

            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = jsonObject.getString(FinalVariables.KEY_SUCCESS);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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