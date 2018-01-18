package com.project.blackspider.classschedule.Activities;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.internal.Utility;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.blackspider.classschedule.App.ConnectivityController;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FCM.Update_FCM_ID;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Models.Teacher;
import com.project.blackspider.classschedule.Models.User;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Receiver.ConnectivityReceiver;
import com.project.blackspider.classschedule.Teacher.TeacherMainActivity;
import com.project.blackspider.classschedule.Teacher.TeacherSignUpActivity;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener{
    private TextView textView1, textView2, textViewForgetPassword, textViewSignup, textViewLoginType;
    private EditText editTextEmail, editTextPassword;
    private Button buttonSignin;
    private ImageButton imgStudentOrTeacherSelection;

    private TextView textViewInputCollectorTitle;
    private EditText editText1InputCollector, editText2InputCollector, editText3InputCollector;

    private String userGivenEmail = "", userGivenPassword = "";
    private ArrayList<String> allInfo;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private boolean isConnected = false;

    private CustomAnimation customAnimation;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;
    private FinalVariables finalVariables;
    private DBHelper dbHelper;
    private User user;
    private Teacher teacher;

    private LayoutInflater li;
    private View promptsView;

    private ProgressDialog pd;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private final int FLAG_TEACHER = 0;
    private final int FLAG_STUDENT = 1;
    private Drawable STUDENT_ICON;
    private Drawable TEACHER_ICON;
    private int loginType = FLAG_STUDENT;

    private final String SWITCH_TEXT_STUDENT = "Student";
    private final String SWITCH_TEXT_TEACHER = "Teacher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        finalVariables = new FinalVariables();
        dbHelper = new DBHelper(this);
        customAnimation = new CustomAnimation(this);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(this);

        //galleryPermissionDialog(FinalVariables.GALLERY_ACCESS_PERMISSION_REQUEST_CODE);

        textView1 = (TextView) findViewById(R.id.textViewWelcomeNote1);
        textView2 = (TextView) findViewById(R.id.textViewWelcomeNote2);
        textViewLoginType = (TextView) findViewById(R.id.textViewLoginType);
        textViewForgetPassword = (TextView) findViewById(R.id.textViewForgetPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignin = (Button) findViewById(R.id.buttonSignIn);
        textViewSignup = (TextView) findViewById(R.id.textViewSignUp);
        imgStudentOrTeacherSelection = (ImageButton) findViewById(R.id.stuOrTeacSelection);

        customAnimation.crossFade(textView1, textView2);

        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewSignup);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewForgetPassword);

        STUDENT_ICON = ResourcesCompat.getDrawable(getResources(), R.drawable.stu_profile_icon, getTheme());
        TEACHER_ICON = ResourcesCompat.getDrawable(getResources(), R.drawable.teacher_profile_icon, getTheme());

        imgStudentOrTeacherSelection.setBackground(STUDENT_ICON);
        loginType = FLAG_STUDENT;

        buttonSignin.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
        textViewForgetPassword.setOnClickListener(this);
        imgStudentOrTeacherSelection.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);

        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            //Toast.makeText(this, "Message:" + getIntent().getExtras().get("message"), Toast.LENGTH_SHORT ).show();
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(FinalVariables.TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        if(getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }else{
            checkSessionValidity();
        }
    }

    private void checkSessionValidity(){
        if(sharedPreferences.getBoolean(FinalVariables.IS_SESSION_EXIST, false)){

            if(sharedPreferences.getBoolean(FinalVariables.IS_STUDENT, false)){
                allInfo = new ArrayList<>();
                allInfo = dbHelper.getUserInfo();

                Toast.makeText(getApplicationContext(), "Welcome back " + allInfo.get(0), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putStringArrayListExtra("allInfo",allInfo);
                startActivity(intent);
            }else{
                allInfo = new ArrayList<>();
                allInfo = dbHelper.getTeacherInfo();

                Toast.makeText(getApplicationContext(), "Welcome back " + allInfo.get(0), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, TeacherMainActivity.class);
                intent.putStringArrayListExtra("teacherAllInfo",allInfo);
                startActivity(intent);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stuOrTeacSelection:
                if(imgStudentOrTeacherSelection.getBackground().equals(STUDENT_ICON)) {
                    //imgStudentOrTeacherSelection.setBackground(TEACHER_ICON);
                    setRevealAnimation(imgStudentOrTeacherSelection, TEACHER_ICON);
                    textViewLoginType.setText("Sign in as a teacher");
                    loginType = FLAG_TEACHER;

                }else {
                    //imgStudentOrTeacherSelection.setBackground(STUDENT_ICON);
                    setRevealAnimation(imgStudentOrTeacherSelection, STUDENT_ICON);
                    textViewLoginType.setText("Sign in as a student");
                    loginType = FLAG_STUDENT;
                }
                break;

            case R.id.buttonSignIn:
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected) {
                    userGivenEmail = editTextEmail.getText()
                            .toString()
                            .trim()
                            .toLowerCase();
                    userGivenPassword = editTextPassword.getText()
                            .toString()
                            .trim();
                    if (userGivenEmail.isEmpty() || userGivenPassword.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please, enter email & password", Toast.LENGTH_SHORT).show();

                    }else {
                        checkSignInValidity(loginType);
                    }
                }else {
                    Snackbar.make(getCurrentFocus(), "Connection not available", Snackbar.LENGTH_SHORT).show();
                }

                break;

            case R.id.textViewSignUp:
                signUp();

                break;

            case R.id.textViewForgetPassword:
                resetPassword();

                break;

            default:
                break;
        }

    }

    void galleryPermissionDialog(int requestCode) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
            return;

        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
            Map<String, Integer> perms = new HashMap<String, Integer>();
            // Initial
            perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            // Fill with results
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            // Check for READ_EXTERNAL_STORAGE

            boolean showRationale = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted

                } else {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (showRationale) {

                    } else {
                        Toast.makeText(this, "Please Enable the Read Storage permission in permission", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, requestCode);

                        //proceed with logic by disabling the related features or quit the app.
                    }


                }


            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void signUp(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Sign Up")
                .setPositiveButton("As a student", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("As a teacher", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WelcomeActivity.this, TeacherSignUpActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void resetPassword() {
        // get layout_developer.xml view
        li = LayoutInflater.from(this);
        promptsView = li.inflate(R.layout.layout_user_input_collector, null);

        textViewInputCollectorTitle = (TextView) promptsView.findViewById(R.id.textViewInputCollectorTitle);
        editText1InputCollector = (EditText) promptsView.findViewById(R.id.editText1InputCollector);
        editText2InputCollector = (EditText) promptsView.findViewById(R.id.editText2InputCollector);
        editText3InputCollector = (EditText) promptsView.findViewById(R.id.editText3InputCollector);

        textViewInputCollectorTitle.setText("Reset password");


        editText1InputCollector.setHint("Enter your email address");
        editText1InputCollector.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        editText2InputCollector.setHint("Enter your phone number");
        editText2InputCollector.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);

        editText3InputCollector.setHint("Enter the code you received");
        editText3InputCollector.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);

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
                .setPositiveButton("Send Code", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPass = editText1InputCollector.getText().toString();
                        String newPass = editText2InputCollector.getText().toString();
                        String confirmPass = editText3InputCollector.getText().toString();
                    }
                });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void checkSignInValidity(final int flag){
        pd = new ProgressDialog(this);
        pd.setMessage("Signing in...");
        pd.setCancelable(false);
        pd.show();

        String url = "";
        if (flag == FLAG_STUDENT) url = FinalVariables.SIGN_IN_URL;
        else if(flag == FLAG_TEACHER) url = FinalVariables.TEACHER_SIGN_IN_URL;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                if(TextUtils.isEmpty(response)) Toast.makeText(getApplicationContext(), "No server response", Toast.LENGTH_SHORT).show();
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
                pd.dismiss();
                Log.d("Error: ",volleyError.toString());
                Toast.makeText(getApplicationContext(), "Ops: "+volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(new FinalVariables().KEY_EMAIL, userGivenEmail);
                params.put(new FinalVariables().KEY_PASSWORD, userGivenPassword);
                params.put(new FinalVariables().KEY_FCM_DEVICE_REG_ID, FirebaseInstanceId.getInstance().getToken());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private String message = "";
    private String success = "";

    private void handleJSON(JSONObject jsonObject, int flag){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;
                if(sharedPreferences.getBoolean(FinalVariables.IS_NEW_FCM_ID_EXIST, false)){
                    String new_id = sharedPreferences.getString(FinalVariables.SHARED_PREFERENCES_NEW_FCM_ID_INFO, "null");
                    new Update_FCM_ID(this, jsonObject.getString(FinalVariables.KEY_EMAIL))
                            .update(new_id, FinalVariables.UPDATE_FCM_DEVICE_REG_ID_URL);
                    sharedPreferencesEditor = sharedPreferences.edit();
                    sharedPreferencesEditor.putBoolean(FinalVariables.IS_NEW_FCM_ID_EXIST, false);
                    sharedPreferencesEditor.putString(FinalVariables.SHARED_PREFERENCES_NEW_FCM_ID_INFO, "null");
                    sharedPreferencesEditor.commit();

                }

                if(flag == FLAG_STUDENT){
                    user = new User(jsonObject.getString(FinalVariables.KEY_NAME),
                            jsonObject.getString(FinalVariables.KEY_ID),
                            jsonObject.getString(FinalVariables.KEY_REG),
                            jsonObject.getString(FinalVariables.KEY_FACULTY),
                            jsonObject.getString(FinalVariables.KEY_SESSION),
                            jsonObject.getString(FinalVariables.KEY_PHONE),
                            jsonObject.getString(FinalVariables.KEY_EMAIL),
                            jsonObject.getString(FinalVariables.KEY_PASSWORD),
                            jsonObject.getString(FinalVariables.KEY_IMAGE_PATH),
                            jsonObject.getString(FinalVariables.KEY_FCM_DEVICE_REG_ID),
                            jsonObject.getString(FinalVariables.KEY_STATUS));

                    dbHelper.saveUserInfo(user);
                    dbHelper.saveLastSignInInfo(user);
                    sharedPreferencesEditor = sharedPreferences.edit();
                    sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, true);
                    sharedPreferencesEditor.putBoolean(FinalVariables.IS_STUDENT, true);
                    sharedPreferencesEditor.commit();

                    allInfo = new ArrayList<>();
                    allInfo.add(user.getName());
                    allInfo.add(user.getID());
                    allInfo.add(user.getReg());
                    allInfo.add(user.getFaculty());
                    allInfo.add(user.getSession());
                    allInfo.add(user.getPhone());
                    allInfo.add(user.getEmail());
                    allInfo.add(user.getPassword());
                    allInfo.add(user.getImage_path());
                    allInfo.add(user.getFcmDeviceRegID());
                    allInfo.add(user.getStatus());
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putStringArrayListExtra("allInfo",allInfo);
                    startActivity(intent);

                }else if(flag == FLAG_TEACHER){
                    teacher = new Teacher(jsonObject.getString(FinalVariables.KEY_NAME),
                            jsonObject.getString(FinalVariables.KEY_DESIGNATION),
                            jsonObject.getString(FinalVariables.KEY_DEPARTMENT),
                            jsonObject.getString(FinalVariables.KEY_FACULTY),
                            jsonObject.getString(FinalVariables.KEY_PHONE),
                            jsonObject.getString(FinalVariables.KEY_EMAIL),
                            jsonObject.getString(FinalVariables.KEY_PASSWORD),
                            jsonObject.getString(FinalVariables.KEY_FCM_DEVICE_REG_ID),
                            jsonObject.getString(FinalVariables.KEY_STATUS));

                    user = new User(jsonObject.getString(FinalVariables.KEY_NAME),
                            jsonObject.getString(FinalVariables.KEY_DESIGNATION),
                            jsonObject.getString(FinalVariables.KEY_DEPARTMENT),
                            jsonObject.getString(FinalVariables.KEY_FACULTY),
                            "null",
                            jsonObject.getString(FinalVariables.KEY_PHONE),
                            jsonObject.getString(FinalVariables.KEY_EMAIL),
                            jsonObject.getString(FinalVariables.KEY_PASSWORD),
                            "null",
                            jsonObject.getString(FinalVariables.KEY_FCM_DEVICE_REG_ID),
                            jsonObject.getString(FinalVariables.KEY_STATUS));

                    dbHelper.saveTeacherInfo((teacher));
                    dbHelper.saveLastSignInInfo(user);
                    sharedPreferencesEditor = sharedPreferences.edit();
                    sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, true);
                    sharedPreferencesEditor.putBoolean(FinalVariables.IS_STUDENT, false);
                    sharedPreferencesEditor.commit();

                    allInfo = new ArrayList<>();
                    allInfo.add(teacher.getName());
                    allInfo.add(teacher.getDesignation());
                    allInfo.add(teacher.getDepartment());
                    allInfo.add(teacher.getFaculty());
                    allInfo.add(teacher.getPhone());
                    allInfo.add(teacher.getEmail());
                    allInfo.add(teacher.getPassword());
                    allInfo.add(teacher.getFcmDeviceRegID());
                    allInfo.add(teacher.getStatus());
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, TeacherMainActivity.class);
                    intent.putStringArrayListExtra("teacherAllInfo",allInfo);
                    startActivity(intent);
                }

            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setRevealAnimation(final View view, final Drawable drawable){
        final int cX = view.getWidth()/2;
        final int cY = view.getHeight()/2;
        final int sR = (int) Math.hypot(view.getWidth(), view.getHeight())/2;
        final int eR = 0;
        if(android.os.Build.VERSION.SDK_INT>= android.os.Build.VERSION_CODES.LOLLIPOP){
            Animator animator = ViewAnimationUtils
                    .createCircularReveal(view, cX, cY, sR, eR);
            animator.setDuration(250);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) { }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setBackground(drawable);
                    if(android.os.Build.VERSION.SDK_INT>= android.os.Build.VERSION_CODES.LOLLIPOP){
                        ViewAnimationUtils.createCircularReveal(view, cX, cY, eR, sR)
                                .setDuration(250).start();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) { }

                @Override
                public void onAnimationRepeat(Animator animator) { }
            });
            animator.start();

        }else view.setBackground(drawable);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectivityController.getInstance().setConnectivityListener(this);
    }
}
