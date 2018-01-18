package com.project.blackspider.classschedule.Teacher;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.blackspider.classschedule.Activities.WelcomeActivity;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class TeacherSignUpActivity extends AppCompatActivity {
    private EditText editTextTeacherName;
    private EditText editTextTeacherDesignation;
    private EditText editTextTeacherDepartment;
    private Spinner facultySelectionSpinner;
    private EditText editTextTeacherPhone;
    private EditText editTextTeacherEmail;
    private EditText editTextTeacherPassword;
    private EditText editTextTeacherConfirmPassword;
    private Button buttonTeacherSignUp;

    private ArrayList<String> allFaculty;
    private ArrayAdapter arrayAdapter;
    private String teacherName;
    private String teacherDesignation;
    private String teacherDepartment;
    private String teacherFaculty;
    private String teacherPhone;
    private String teacherEmail;
    private String teacherPassword;
    private String teacherConfirmPassword;
    private String userDeviceRegID;
    private String userStatus;

    private FinalVariables finalVariables = new FinalVariables();
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;
    private ProgressDialog pd;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prepareData();

        editTextTeacherName = (EditText) findViewById(R.id.teacherNameEditText);
        editTextTeacherDesignation = (EditText) findViewById(R.id.teacherDesignationEditText);
        editTextTeacherDepartment = (EditText) findViewById(R.id.teacherDepartmentEditText);
        facultySelectionSpinner = (Spinner) findViewById(R.id.teacherFacultySelectionSpinner);
        editTextTeacherPhone = (EditText) findViewById(R.id.teacherPhoneNumberEditText);
        editTextTeacherEmail = (EditText) findViewById(R.id.teacherEmailEditText);
        editTextTeacherPassword = (EditText) findViewById(R.id.teacherPasswordEditText);
        editTextTeacherConfirmPassword = (EditText) findViewById(R.id.teacherConfirmPasswordEditText);
        buttonTeacherSignUp = (Button) findViewById(R.id.createTeacherIDButton);

        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(this);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(buttonTeacherSignUp);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allFaculty);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        facultySelectionSpinner.setAdapter(arrayAdapter);

        buttonTeacherSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        teacherName = editTextTeacherName.getText().toString().trim();
        teacherDesignation = editTextTeacherDesignation.getText().toString().trim();
        teacherDepartment = editTextTeacherDepartment.getText().toString().trim();
        if(facultySelectionSpinner.getSelectedItemId()>0) teacherFaculty = facultySelectionSpinner.getSelectedItem().toString().trim();
        else teacherFaculty = "";
        teacherPhone = editTextTeacherPhone.getText().toString().trim();
        teacherEmail = editTextTeacherEmail.getText().toString().toLowerCase().trim();
        teacherPassword = editTextTeacherPassword.getText().toString().trim();
        teacherConfirmPassword = editTextTeacherConfirmPassword.getText().toString().trim();

        if(teacherName.isEmpty() || teacherDesignation.isEmpty() || teacherDepartment.isEmpty() ||
                teacherFaculty.isEmpty() || teacherPhone.isEmpty() || teacherEmail.isEmpty() ||
                teacherPassword.isEmpty() || teacherConfirmPassword.isEmpty()){

            Toast.makeText(getApplicationContext(), "Please fill up all fields", Toast.LENGTH_SHORT).show();
        }else {
            if(!teacherPassword.equals(teacherConfirmPassword)){
                Toast.makeText(getApplicationContext(), "Password mis-match", Toast.LENGTH_SHORT).show();
            }else {
                checkEmailValidity();
            }
        }

    }

    private void prepareData(){
        allFaculty = new ArrayList<>();

        allFaculty.add(FinalVariables.FACULTY);
        allFaculty.add(FinalVariables.FACULTY_AG);
        allFaculty.add(FinalVariables.FACULTY_CSE);
        allFaculty.add(FinalVariables.FACULTY_BAM);
        allFaculty.add(FinalVariables.FACULTY_FISH);
        allFaculty.add(FinalVariables.FACULTY_DM);
        allFaculty.add(FinalVariables.FACULTY_NFS);
        allFaculty.add(FinalVariables.FACULTY_LM);
        allFaculty.add(FinalVariables.FACULTY_DVM);
        allFaculty.add(FinalVariables.FACULTY_AH);
        allFaculty.add(FinalVariables.FACULTY_NOT_LISTED);
    }

    private void checkEmailValidity(){
        pd = new ProgressDialog(this);
        pd.setMessage("Checking Email Validity...");
        pd.setCancelable(false);
        pd.show();

        doVolleyRequest(FinalVariables.TEACHER_EMAIL_VALIDATION_URL, FinalVariables.REQUEST_TYPE_EMAIL_VALIDITY_CHECK);

    }

    private void saveData(){
        userStatus = "Available";
        pd = new ProgressDialog(this);
        pd.setMessage("Signing up...");
        pd.setCancelable(false);
        pd.show();

        doVolleyRequest(FinalVariables.TEACHER_SIGN_UP_URL, FinalVariables.REQUEST_TYPE_SAVE_DATA);
    }

    private void doVolleyRequest(String url, final int code){
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
                        handleJSON(jsonObject, code);
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
                userDeviceRegID = FirebaseInstanceId.getInstance().getToken();
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                if(code == FinalVariables.REQUEST_TYPE_EMAIL_VALIDITY_CHECK){
                    params.put(FinalVariables.KEY_EMAIL, teacherEmail);

                }else if(code == FinalVariables.REQUEST_TYPE_SAVE_DATA){
                    params.put(FinalVariables.KEY_NAME, teacherName);
                    params.put(FinalVariables.KEY_DESIGNATION, teacherDesignation);
                    params.put(FinalVariables.KEY_DEPARTMENT, teacherDepartment);
                    params.put(FinalVariables.KEY_FACULTY, teacherFaculty);
                    params.put(FinalVariables.KEY_PHONE, teacherPhone);
                    params.put(FinalVariables.KEY_EMAIL, teacherEmail);
                    params.put(FinalVariables.KEY_PASSWORD, teacherPassword);
                    params.put(FinalVariables.KEY_FCM_DEVICE_REG_ID, userDeviceRegID);
                    params.put(FinalVariables.KEY_STATUS, userStatus);
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
    private ArrayList<String> allInfo;

    private void handleJSON(JSONObject jsonObject, final int code){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){

                if(code== FinalVariables.REQUEST_TYPE_EMAIL_VALIDITY_CHECK){
                    message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    saveData();

                }else if(code== FinalVariables.REQUEST_TYPE_SAVE_DATA){
                    success = FinalVariables.SUCCESS;
                    message = jsonObject.getString("message");

                    alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Confirmation Message")
                            .setMessage(message+"\n\n" +
                                    "Please, request any teacher of your faculty who already have an id to confirm your id.")
                            .setCancelable(false)
                            .setPositiveButton("Oky", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }


            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString("message");
                Log.e("Failure ", message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                message = jsonObject.getString("error");
                Log.e("Failure ", message);
            }

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
