package com.project.blackspider.classschedule.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.Models.User;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Utils.LruBitmapCache;
import com.project.blackspider.classschedule.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class ProfileSettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imageViewProfileImage;
    private TextView textViewUserName;
    private TextView textViewUserID;
    private TextView textViewUserReg;
    private TextView textViewUserFaculty;
    private TextView textViewUserSession;
    private TextView textViewUserPhone;
    private TextView textViewUserEmail;
    private TextView textViewUserStatus;
    private TextView textViewChangeNameOption;
    private TextView textViewChangePasswordOption;
    private TextView textViewChangePhoneNumberOption;
    private TextView textViewChangeProfileImageOption;
    private TextView textViewChangeStatusOption;
    private TextView textViewInputCollectorTitle;
    private EditText editText1InputCollector;
    private EditText editText2InputCollector;
    private EditText editText3InputCollector;
    private Button buttonDeleteAccount;

    private DBHelper dbHelper;
    private CustomAnimation customAnimation;
    private CustomImageConverter customImageConverter;
    private FinalVariables finalVariables;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private User user;

    private ArrayList<String> allInfo;
    private String alertDialogTitle;

    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private RequestQueue mRequestQueue;

    private LayoutInflater li;
    private View promptsView;

    private ProgressDialog pd;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private final int FLAG_CHANGE_NAME = 0;
    private final int FLAG_CHANGE_PASSWORD = 1;
    private final int FLAG_CHANGE_PHONE_NUMBER = 2;
    private final int FLAG_CHANGE_STATUS = 3;
    private final int FLAG_DELETE_ACCOUNT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        dbHelper = new DBHelper(this);
        allInfo = dbHelper.getUserInfo();
        customAnimation = new CustomAnimation(this);
        customImageConverter = new CustomImageConverter();
        finalVariables = new FinalVariables();
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(this);

        imageViewProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserID = (TextView) findViewById(R.id.textViewUserID);
        textViewUserReg = (TextView) findViewById(R.id.textViewUserReg);
        textViewUserFaculty = (TextView) findViewById(R.id.textViewUserFaculty);
        textViewUserSession = (TextView) findViewById(R.id.textViewUserSession);
        textViewUserPhone = (TextView) findViewById(R.id.textViewUserPhone);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserStatus = (TextView) findViewById(R.id.textViewUserStatus);
        textViewChangeNameOption = (TextView) findViewById(R.id.textViewChangeNameOption);
        textViewChangePhoneNumberOption = (TextView) findViewById(R.id.textViewChangePhoneNumberOption);
        textViewChangePasswordOption = (TextView) findViewById(R.id.textViewChangePasswordOption);
        textViewChangeProfileImageOption = (TextView) findViewById(R.id.textViewChangeProfileImageOption);
        textViewChangeStatusOption = (TextView) findViewById(R.id.textViewChangeStatusOption);
        buttonDeleteAccount = (Button) findViewById(R.id.buttonDeleteAccount);

        setUserImage(imageViewProfileImage);

        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewChangeNameOption);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewChangePasswordOption);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewChangePhoneNumberOption);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewChangeProfileImageOption);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewChangeStatusOption);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(buttonDeleteAccount);

        textViewChangeNameOption.setOnClickListener(this);
        textViewChangePasswordOption.setOnClickListener(this);
        textViewChangePhoneNumberOption.setOnClickListener(this);
        textViewChangeProfileImageOption.setOnClickListener(this);
        textViewChangeStatusOption.setOnClickListener(this);
        buttonDeleteAccount.setOnClickListener(this);

        setData();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v == textViewChangeNameOption){
            alertDialogTitle = "Change name";
            getUserInput(alertDialogTitle, FLAG_CHANGE_NAME);

        }else if(v == textViewChangePasswordOption){
            alertDialogTitle = "Change password";
            getUserInput(alertDialogTitle, FLAG_CHANGE_PASSWORD);

        }else if(v == textViewChangePhoneNumberOption){
            alertDialogTitle = "Change phone number";
            getUserInput(alertDialogTitle, FLAG_CHANGE_PHONE_NUMBER);

        }else if(v == textViewChangeProfileImageOption){
            alertDialogTitle = "Change profile image";
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(alertDialogTitle)
                    .setMessage("To change your profile image please do following steps:\n\n" +
                            "1. Go back to settings menu.\n" +
                            "2. Tap on your profile image.\n" +
                            "3. And you will find the option.")
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }else if(v == textViewChangeStatusOption){
            alertDialogTitle = "Change status";
            getUserInput(alertDialogTitle, FLAG_CHANGE_STATUS);

        }else if(v == buttonDeleteAccount){
            deleteMyAccount();
        }

    }

    private void setUserImage(final ImageView imgView){
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_loading1);
        bitmap = customImageConverter.getCircledBitmap(bitmap);
        imgView.setImageBitmap(bitmap);
        customAnimation.roll(imgView);

        mRequestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(this.mRequestQueue,
                new LruBitmapCache());
        imageLoader.get(allInfo.get(FinalVariables.IMAGE_PATH_INDEX), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                if(isImmediate){
                    //Toast.makeText(getApplicationContext(), "Loading image", Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(getApplicationContext(), "Image loaded", Toast.LENGTH_SHORT).show();
                    //bitmap = customImageConverter.getResizedBitmap(response.getBitmap(), 400);
                    imgView.clearAnimation();
                    imgView.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(FinalVariables.TAG, "Volley Error: "+error.getMessage());
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_error);
                //bitmap = customImageConverter.getResizedBitmap(bitmap, 100);
                imgView.clearAnimation();
                imgView.setImageBitmap(bitmap);
            }
        });
    }

    protected void getUserInput(String title, final int flag){
        // get layout_developer.xml view
        li = LayoutInflater.from(this);
        promptsView = li.inflate(R.layout.layout_user_input_collector, null);

        textViewInputCollectorTitle = (TextView) promptsView.findViewById(R.id.textViewInputCollectorTitle);
        editText1InputCollector = (EditText) promptsView.findViewById(R.id.editText1InputCollector);
        editText2InputCollector = (EditText) promptsView.findViewById(R.id.editText2InputCollector);
        editText3InputCollector = (EditText) promptsView.findViewById(R.id.editText3InputCollector);

        textViewInputCollectorTitle.setText(title);

        if(flag == FLAG_CHANGE_NAME){
            editText1InputCollector.setHint("Enter new name");
            editText2InputCollector.setVisibility(View.GONE);
            editText3InputCollector.setVisibility(View.GONE);

        }else if(flag == FLAG_CHANGE_PASSWORD){
            editText1InputCollector.setHint("Old password");
            editText1InputCollector.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            editText2InputCollector.setHint("New password(Max 8digit)");
            editText2InputCollector.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            editText3InputCollector.setHint("Re-enter new password");
            editText3InputCollector.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        }else if(flag == FLAG_CHANGE_PHONE_NUMBER){
            editText1InputCollector.setHint("New phone number");
            editText1InputCollector.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editText2InputCollector.setVisibility(View.GONE);
            editText3InputCollector.setVisibility(View.GONE);

        }else if(flag == FLAG_CHANGE_STATUS){
            editText1InputCollector.setHint("Enter new status");
            editText2InputCollector.setVisibility(View.GONE);
            editText3InputCollector.setVisibility(View.GONE);

        }
        alertDialogBuilder = new AlertDialog.Builder(this);
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyTransparentTheme);

        // set layout_calculator.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag == FLAG_CHANGE_NAME){
                            changeName(editText1InputCollector.getText().toString());

                        }else if(flag == FLAG_CHANGE_PASSWORD){
                            String oldPass = editText1InputCollector.getText().toString();
                            String newPass = editText2InputCollector.getText().toString();
                            String confirmPass = editText3InputCollector.getText().toString();
                            changePassword(oldPass, newPass, confirmPass);

                        }else if(flag == FLAG_CHANGE_PHONE_NUMBER){
                            changePhoneNumber(editText1InputCollector.getText().toString());

                        }else if(flag == FLAG_CHANGE_STATUS){
                            changeStatus(editText1InputCollector.getText().toString());
                        }
                    }
                });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    private void changeName(String newName){
        if(newName.isEmpty()){
            Toast.makeText(getApplicationContext(), "Nothing to change!", Toast.LENGTH_SHORT).show();
            getUserInput("Change Name", FLAG_CHANGE_NAME);

        }else {
            doVolleyRequest(newName, FinalVariables.CHANGE_NAME_URL, FLAG_CHANGE_NAME);
        }

    }

    private void changePassword(String oldPass, String newPass, String confirmPass){
        if(!oldPass.equals(allInfo.get(FinalVariables.PASSWORD_INDEX))){
            Toast.makeText(getApplicationContext(), "Wrong old password", Toast.LENGTH_SHORT).show();
            getUserInput("Change Password", FLAG_CHANGE_PASSWORD);

        }else {
            if(newPass.length()<1 || newPass.length()>8){
                Toast.makeText(getApplicationContext(), "Not a valid password", Toast.LENGTH_SHORT).show();
                getUserInput("Change Password", FLAG_CHANGE_PASSWORD);

            }else {
                if(!newPass.equals(confirmPass)){
                    Toast.makeText(getApplicationContext(), "Password mis match", Toast.LENGTH_SHORT).show();
                    getUserInput("Change Password", FLAG_CHANGE_PASSWORD);

                }else {
                    doVolleyRequest(newPass, FinalVariables.CHANGE_PASSWORD_URL, FLAG_CHANGE_PASSWORD);
                }

            }
        }

    }

    private void changePhoneNumber(String newPhoneNumber){
        if(newPhoneNumber.length()<1 && newPhoneNumber.length()>11){
            Toast.makeText(getApplicationContext(), "Invalid phone number!", Toast.LENGTH_SHORT).show();
            getUserInput("Change phone number", FLAG_CHANGE_PHONE_NUMBER);
        }else {
            doVolleyRequest(newPhoneNumber, FinalVariables.CHANGE_PHONE_NUMBER_URL, FLAG_CHANGE_PHONE_NUMBER);
        }

    }

    private void changeStatus(String newStatus){
        if(newStatus.isEmpty()){
            Toast.makeText(getApplicationContext(), "Nothing to change!", Toast.LENGTH_SHORT).show();
            getUserInput("Change Status", FLAG_CHANGE_STATUS);

        }else if(newStatus.length()>100){
            Toast.makeText(getApplicationContext(), "Too long!(Max 100 word)", Toast.LENGTH_SHORT).show();
            getUserInput("Change Status", FLAG_CHANGE_STATUS);

        } else {
            doVolleyRequest(newStatus, FinalVariables.CHANGE_STATUS_URL, FLAG_CHANGE_STATUS);
        }

    }

    private void deleteMyAccount(){
        alertDialogTitle = "Delete Account";
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(alertDialogTitle)
                .setMessage("You will no more receive any notification of class or chat. " +
                        "There is no way to undo it.\n\n" +
                        "Are you sure? ")
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Good decision :)", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteMyAccountFinalDecision();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteMyAccountFinalDecision(){
        alertDialogTitle = "Delete Account";
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(alertDialogTitle)
                .setMessage("Your friends will miss you.\n\n" +
                        "Still want to delete? ")
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Good decision :)", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doVolleyRequest("", FinalVariables.DELETE_ACCOUNT_URL, FLAG_DELETE_ACCOUNT);
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void doVolleyRequest(final String value, String url, final int flag){
        pd = new ProgressDialog(this);
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
                    Toast.makeText(getApplicationContext(), "No server response", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put(FinalVariables.KEY_EMAIL, allInfo.get(FinalVariables.EMAIL_INDEX));
                params.put("table", "registration_requests");

                if(flag == FLAG_CHANGE_NAME){
                    params.put(FinalVariables.KEY_NAME, value);

                }else if(flag == FLAG_CHANGE_PASSWORD){
                    params.put(FinalVariables.KEY_PASSWORD, value);

                }else if(flag == FLAG_CHANGE_PHONE_NUMBER){
                    params.put(FinalVariables.KEY_PHONE, value);

                }else if(flag == FLAG_CHANGE_STATUS){
                    params.put(FinalVariables.KEY_STATUS, value);

                }else if(flag == FLAG_DELETE_ACCOUNT){
                    String imageName = "img_"+allInfo.get(FinalVariables.ID_INDEX)+"_"+allInfo.get(FinalVariables.SESSION_INDEX)+".jpeg";
                    params.put("image_name", imageName);

                }

                //returning parameters
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String message = "";
    private String success = "";

    private void handleJSON(JSONObject jsonObject, final int flag){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){

                if(flag==FLAG_CHANGE_NAME){
                    message = jsonObject.getString("message");
                    String newName = jsonObject.getString(FinalVariables.KEY_NAME);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    user = new User(newName,
                            allInfo.get(FinalVariables.ID_INDEX),
                            allInfo.get(FinalVariables.REG_INDEX),
                            allInfo.get(FinalVariables.FACULTY_INDEX),
                            allInfo.get(FinalVariables.SESSION_INDEX),
                            allInfo.get(FinalVariables.PHONE_INDEX),
                            allInfo.get(FinalVariables.EMAIL_INDEX),
                            allInfo.get(FinalVariables.PASSWORD_INDEX),
                            allInfo.get(FinalVariables.FCM_DEVICE_REG_ID_INDEX),
                            allInfo.get(FinalVariables.IMAGE_PATH_INDEX),
                            allInfo.get(FinalVariables.STATUS_INDEX) );
                    dbHelper.saveUserInfo(user);
                    allInfo = dbHelper.getUserInfo();
                    setData();

                }else if(flag==FLAG_CHANGE_PASSWORD){
                    message = jsonObject.getString("message");
                    String newPass = jsonObject.getString(FinalVariables.KEY_PASSWORD);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    user = new User(allInfo.get(FinalVariables.NAME_INDEX),
                            allInfo.get(FinalVariables.ID_INDEX),
                            allInfo.get(FinalVariables.REG_INDEX),
                            allInfo.get(FinalVariables.FACULTY_INDEX),
                            allInfo.get(FinalVariables.SESSION_INDEX),
                            allInfo.get(FinalVariables.PHONE_INDEX),
                            allInfo.get(FinalVariables.EMAIL_INDEX),
                            newPass,
                            allInfo.get(FinalVariables.FCM_DEVICE_REG_ID_INDEX),
                            allInfo.get(FinalVariables.IMAGE_PATH_INDEX),
                            allInfo.get(FinalVariables.STATUS_INDEX) );
                    dbHelper.saveUserInfo(user);
                    allInfo = dbHelper.getUserInfo();
                    setData();

                }else if(flag==FLAG_CHANGE_PHONE_NUMBER){
                    message = jsonObject.getString("message");
                    String newPhone = jsonObject.getString(FinalVariables.KEY_PHONE);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    user = new User(allInfo.get(FinalVariables.NAME_INDEX),
                            allInfo.get(FinalVariables.ID_INDEX),
                            allInfo.get(FinalVariables.REG_INDEX),
                            allInfo.get(FinalVariables.FACULTY_INDEX),
                            allInfo.get(FinalVariables.SESSION_INDEX),
                            newPhone,
                            allInfo.get(FinalVariables.EMAIL_INDEX),
                            allInfo.get(FinalVariables.PASSWORD_INDEX),
                            allInfo.get(FinalVariables.FCM_DEVICE_REG_ID_INDEX),
                            allInfo.get(FinalVariables.IMAGE_PATH_INDEX),
                            allInfo.get(FinalVariables.STATUS_INDEX) );
                    dbHelper.saveUserInfo(user);
                    allInfo = dbHelper.getUserInfo();
                    setData();

                }else if(flag==FLAG_CHANGE_STATUS){
                    message = jsonObject.getString("message");
                    String newStatus = jsonObject.getString(FinalVariables.KEY_STATUS);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    user = new User(allInfo.get(FinalVariables.NAME_INDEX),
                            allInfo.get(FinalVariables.ID_INDEX),
                            allInfo.get(FinalVariables.REG_INDEX),
                            allInfo.get(FinalVariables.FACULTY_INDEX),
                            allInfo.get(FinalVariables.SESSION_INDEX),
                            allInfo.get(FinalVariables.PHONE_INDEX),
                            allInfo.get(FinalVariables.EMAIL_INDEX),
                            allInfo.get(FinalVariables.PASSWORD_INDEX),
                            allInfo.get(FinalVariables.FCM_DEVICE_REG_ID_INDEX),
                            allInfo.get(FinalVariables.IMAGE_PATH_INDEX),
                            newStatus );
                    dbHelper.saveUserInfo(user);
                    allInfo = dbHelper.getUserInfo();
                    setData();

                }else if(flag == FLAG_DELETE_ACCOUNT){
                    message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    sharedPreferences = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);
                    sharedPreferencesEditor = sharedPreferences.edit();
                    sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, false);
                    sharedPreferencesEditor.commit();
                    dbHelper.deleteUserInfo();
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }


            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                Log.e("Failure ", message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void setData(){
        textViewUserName.setText("Full Name: "+allInfo.get(FinalVariables.NAME_INDEX));
        textViewUserID.setText("ID no: "+allInfo.get(FinalVariables.ID_INDEX));
        textViewUserReg.setText("Registration no: "+allInfo.get(FinalVariables.REG_INDEX));
        textViewUserFaculty.setText("Faculty: "+allInfo.get(FinalVariables.FACULTY_INDEX));
        textViewUserSession.setText("Session: "+allInfo.get(FinalVariables.SESSION_INDEX));
        textViewUserPhone.setText("Phone no: "+allInfo.get(FinalVariables.PHONE_INDEX));
        textViewUserEmail.setText("Email: "+allInfo.get(FinalVariables.EMAIL_INDEX));
        textViewUserStatus.setText("Status: "+allInfo.get(FinalVariables.STATUS_INDEX));
    }

}
