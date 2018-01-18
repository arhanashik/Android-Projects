package com.project.blackspider.classschedule.Teacher;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.Activities.SingleChatActivity;
import com.project.blackspider.classschedule.Activities.WelcomeActivity;
import com.project.blackspider.classschedule.Adapters.CustomListAdapterAllSchedulePosts;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FCM.SendFCMPushNotification;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.blackspider.classschedule.Utils.DateAndTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherMainActivity extends AppCompatActivity {
    private TextView textViewNoSession;
    private ListView listView;
    private Spinner sessionSelectionSpinner;
    private EditText editTextCourseCode;
    private EditText editTextCourseTitle;
    private EditText editTextCourseTeacher;
    private EditText editTextClassRoom;
    private EditText editTextDescription;
    private TimePicker timePickerClassTime;
    private EditText editTextBroadcastMessage;
    private TextView textViewNB;

    private ArrayList<String> teacherAllInfo;
    private List<String> allSession;
    private ArrayAdapter<String> adapter;

    private ArrayList<String> allPostmenName = new ArrayList<>();
    private ArrayList<String> allPostmenEmail = new ArrayList<>();
    private ArrayList<String> allPostsSl = new ArrayList<>();
    private ArrayList<String> allCourseCode = new ArrayList<>();
    private ArrayList<String> allCourseTitle = new ArrayList<>();
    private ArrayList<String> allCourseTeacher = new ArrayList<>();
    private ArrayList<String> allClassRoom = new ArrayList<>();
    private ArrayList<String> allClassTime = new ArrayList<>();
    private ArrayList<String> allDescription = new ArrayList<>();
    private ArrayList<String> allPostsDate = new ArrayList<>();
    private ArrayList<String> myPostsSl = new ArrayList<>();

    private String message;
    private String success;
    private String courseCode = "";
    private String courseTitle = "";
    private String courseTeacher = "";
    private String classRoom = "";
    private String classTime = "";
    private String description = "";
    private String name = "";
    private String email = "";

    private String broadcastMessage = "";

    private FinalVariables finalVariables;
    private Intent intent;
    private DBHelper dbHelper;
    private SendFCMPushNotification sendFCMPushNotification;
    private DateAndTime dateAndTime = new DateAndTime();
    private CustomListAdapterAllSchedulePosts customListAdapterAllSchedulePosts;

    private LayoutInflater li;
    private View promptsView;
    private ProgressDialog pd;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private BroadcastReceiver broadcastReceiver;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;

    private final int FLAG_VIEW_POSTS = 0;
    private final int FLAG_ADD_POST = 1;
    private final int FLAG_REVIEW_POST = 2;
    private final int FLAG_CONFIRMATION = 3;
    private final int FLAG_BROADCAST_MESSAGE = 4;

    // # milliseconds, desired time passed between two back presses.
    private static final int TIME_INTERVAL = 2500;
    private long mBackPressed;

    private String session = "";
    private String table = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prepareData();

        dbHelper = new DBHelper(this);
        finalVariables = new FinalVariables();
        teacherAllInfo = dbHelper.getTeacherInfo();
        sharedPreferences = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);
        sendFCMPushNotification = new SendFCMPushNotification(this);

        textViewNoSession = (TextView) findViewById(R.id.textViewNoSessionFound);
        listView = (ListView) findViewById(R.id.listViewTeacherMainActivity);
        sessionSelectionSpinner = (Spinner) findViewById(R.id.sessionSelectionSpinner1);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allSession);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        sessionSelectionSpinner.setAdapter(adapter);

        sessionSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Item :" + allSession.get(position), Toast.LENGTH_SHORT).show();
                if(position>0){
                    listView.setVisibility(View.GONE);
                    textViewNoSession.setVisibility(View.VISIBLE);
                    textViewNoSession.setText("Loading posts...");
                    session = allSession.get(position);
                    table = "table_schedule_"+teacherAllInfo.get(FinalVariables.TEACHER_FACULTY_INDEX)+"_"+session.replace("-","_");
                    table=table.toLowerCase();
                    doVolleyRequest(table, FLAG_VIEW_POSTS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void prepareData() {
        allSession = new ArrayList<>();
        allSession.add("Select Session");
        allSession.add("2012-13");
        allSession.add("2013-14");
        allSession.add("2014-15");
        allSession.add("2015-16");
        allSession.add("2016-17");
        allSession.add("2017-18");
        allSession.add("2018-19");
        allSession.add("2019-20");
        allSession.add("Not listed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_teacher_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_post:
                if(sessionSelectionSpinner.getSelectedItemId()>0) {
                    addSchedulePost();
                }else Toast.makeText(getApplicationContext(), "Please, select a session first", Toast.LENGTH_SHORT).show();

                break;

            case R.id.action_broadcast:
                if(sessionSelectionSpinner.getSelectedItemId()>0) {
                    name = teacherAllInfo.get(FinalVariables.TEACHER_NAME_INDEX);
                    email = teacherAllInfo.get(FinalVariables.TEACHER_EMAIL_INDEX);
                    session = sessionSelectionSpinner.getSelectedItem().toString();
                    table = "table_broadcast_messages_"+teacherAllInfo.get(FinalVariables.TEACHER_FACULTY_INDEX)+"_"+session.replace("-", "_");
                    table=table.toLowerCase();
                    //getUserInput(FLAG_BROADCAST_MESSAGE, table);
                    intent = new Intent(getApplicationContext(), TeacherChatActivity.class);
                    intent.putExtra("flag", FinalVariables.FLAG_GET_BROADCAST_MESSAGES);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("session", session);
                    intent.putExtra("tableName", table);
                    startActivity(intent);
                }else Toast.makeText(getApplicationContext(), "Please, select a session first", Toast.LENGTH_SHORT).show();

                break;

            case R.id.action_refresh_main_menu:
                if(sessionSelectionSpinner.getSelectedItemId()>0) {
                    session = sessionSelectionSpinner.getSelectedItem().toString();
                    table = "table_schedule_"+teacherAllInfo.get(FinalVariables.TEACHER_FACULTY_INDEX)+"_"+session.replace("-", "_");
                    table=table.toLowerCase();
                    //getUserInput(FLAG_CONFIRMATION, table);
                    doVolleyRequest(table, FLAG_VIEW_POSTS);
                }else Toast.makeText(getApplicationContext(), "Please, select a session first", Toast.LENGTH_SHORT).show();


                break;

            case R.id.action_settings:


                break;

            case R.id.action_sign_out:
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Sign Out")
                        .setMessage("You may not receive any notifications before signing in again.\n\nStill want to sign out?")
                        .setPositiveButton("Yes, Do It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferencesEditor = sharedPreferences.edit();
                                sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, false);
                                sharedPreferencesEditor.commit();
                                dbHelper.deleteUserInfo();
                                intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Exit Without Sign Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(TeacherMainActivity.this, WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("EXIT", true);
                                startActivity(intent);
                                finish();
                            }
                        });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSchedulePost() {
        session = sessionSelectionSpinner.getSelectedItem().toString();
        table = teacherAllInfo.get(FinalVariables.TEACHER_FACULTY_INDEX)+"_"+session.replace("-","_");
        table=table.toLowerCase();
        //Toast.makeText(getApplicationContext(), "Publish in "+tableName, Toast.LENGTH_SHORT).show();
        //getUserInput(FLAG_ADD_POST, table);
        getUserInput(FLAG_CONFIRMATION, table);

    }

    protected void getUserInput(final int flag, final String tableName){
        alertDialogBuilder = new AlertDialog.Builder(this);

        // get layout_developer.xml view
        li = LayoutInflater.from(this);
        if(flag == FLAG_ADD_POST){
            promptsView = li.inflate(R.layout.layout_schedule_post_input, null);
            editTextCourseCode = (EditText) promptsView.findViewById(R.id.editTextCourseCode);
            editTextCourseTitle = (EditText) promptsView.findViewById(R.id.editTextCourseTitle);
            editTextCourseTeacher = (EditText) promptsView.findViewById(R.id.editTextCourseTeacher);
            editTextClassRoom = (EditText) promptsView.findViewById(R.id.editTextClassRoom);
            editTextDescription = (EditText) promptsView.findViewById(R.id.editTextDescription);
            timePickerClassTime = (TimePicker) promptsView.findViewById(R.id.timePickerClassTime);

            editTextCourseCode.setText(teacherAllInfo.get(FinalVariables.TEACHER_DEPARTMENT_INDEX)+"-");
            if(!courseCode.isEmpty()) editTextCourseCode.setText(courseCode);
            if(!courseTitle.isEmpty()) editTextCourseTitle.setText(courseTitle);
            editTextCourseTeacher.setText(teacherAllInfo.get(FinalVariables.TEACHER_NAME_INDEX));
            if(!classRoom.isEmpty()) editTextClassRoom.setText(classRoom);
            if(!description.isEmpty()) editTextDescription.setText(description);
            timePickerClassTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int hr; int min;
                    String AM_PM = "AM";
                    if(Build.VERSION.SDK_INT<23){
                        hr= timePickerClassTime.getCurrentHour();
                        min = timePickerClassTime.getCurrentMinute();
                    }else {
                        hr = timePickerClassTime.getHour();
                        min = timePickerClassTime.getMinute();
                    }

                    //if(timePickerClassTime.is24HourView()) hr = hr-12;
                    if(hourOfDay>11){
                        AM_PM = "PM";
                        hr = hr-12;
                    }
                    classTime = hr+":"+min+" "+AM_PM;
                }
            });


            alertDialogBuilder.setView(promptsView)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }else if(flag == FLAG_REVIEW_POST){
            String message = "Course Code: "+courseCode+"\n"
                    +"Course Title: "+courseTitle+"\n"
                    +"Course Teacher: "+courseTeacher+"\n"
                    +"Class Room: "+classRoom+"\n"
                    +"Class Time: "+classTime+"\n"
                    +"Description: "+description;
            alertDialogBuilder.setTitle("Review")
                    .setMessage(message)
                    .setNegativeButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getUserInput(FLAG_ADD_POST, tableName);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }else if(flag == FLAG_CONFIRMATION){
            String message = "Are you sure you want to post for session: "+tableName
                    +"?\n\nN.B. You can change the session on dropdown menu.";
            alertDialogBuilder.setTitle("Selected Session: "+tableName)
                    .setMessage(message)
                    .setNeutralButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }else if(flag == FLAG_BROADCAST_MESSAGE){
            promptsView = li.inflate(R.layout.layout_single_post_input, null);
            editTextBroadcastMessage = (EditText) promptsView.findViewById(R.id.editTextPost);
            textViewNB = (TextView) promptsView.findViewById(R.id.textViewNB);
            editTextBroadcastMessage.setHint("Type a message");
            if(!broadcastMessage.isEmpty()) editTextBroadcastMessage.setText(broadcastMessage);
            textViewNB.setText("N. B. Message size can't be more then 150 words.");
            alertDialogBuilder.setView(promptsView)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag == FLAG_CONFIRMATION){
                            getUserInput(FLAG_ADD_POST, "table_schedule_"+tableName);

                        }else if(flag == FLAG_ADD_POST) {
                            courseCode = editTextCourseCode.getText().toString();
                            courseTitle = editTextCourseTitle.getText().toString();
                            courseTeacher = editTextCourseTeacher.getText().toString();
                            classRoom = editTextClassRoom.getText().toString();
                            description = editTextDescription.getText().toString();

                            if(courseTitle.isEmpty() || classTime.isEmpty()){
                                Toast.makeText(getApplicationContext(), "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_ADD_POST, tableName);
                            }else if(description.length()>250){
                                Toast.makeText(getApplicationContext(), "Too Long Description(Max length 250 word)",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_ADD_POST, tableName);
                            }else getUserInput(FLAG_REVIEW_POST, tableName);

                        }else if(flag == FLAG_REVIEW_POST){
                            if(courseTitle.isEmpty() || classTime.isEmpty()){
                                Toast.makeText(getApplicationContext(), "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_ADD_POST, tableName);
                            }else if(description.length()>250){
                                Toast.makeText(getApplicationContext(), "Too Long Description(Max length 250 word)",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_ADD_POST, tableName);
                            }else doVolleyRequest(tableName, FLAG_ADD_POST);

                        }else if(flag == FLAG_BROADCAST_MESSAGE){
                            broadcastMessage = editTextBroadcastMessage.getText().toString();
                            if(broadcastMessage.isEmpty() || broadcastMessage.length()>150){
                                Toast.makeText(getApplicationContext(), "Invalid Message Length",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_BROADCAST_MESSAGE, tableName);

                            }else doVolleyRequest(tableName, FLAG_BROADCAST_MESSAGE);

                        }


                    }
                });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    private void doVolleyRequest(final String table, final int flag) {
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();

        String url="";
        if(flag == FLAG_VIEW_POSTS) url = FinalVariables.GET_All_SCHEDULE_POSTS_URL;
        else if(flag == FLAG_ADD_POST) url = FinalVariables.ADD_SCHEDULE_POST_URL;
        else if(flag == FLAG_BROADCAST_MESSAGE) url = FinalVariables.SEND_BROADCAST_MESSAGE_URL;

        stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                if(response.isEmpty()) Toast.makeText(getApplicationContext(), "No server response", Toast.LENGTH_SHORT).show();
                else {
                    Log.d("Response: ","Server Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, flag, table);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener (){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                listView.setVisibility(View.GONE);
                textViewNoSession.setVisibility(View.VISIBLE);
                textViewNoSession.setText("Error, while loading posts");
                Log.d("Error: ","Volley Error: " + volleyError);
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                if(flag == FLAG_VIEW_POSTS){
                    params.put("table", table);

                }else if(flag == FLAG_ADD_POST){
                    params.put(FinalVariables.KEY_NAME, teacherAllInfo.get(FinalVariables.TEACHER_NAME_INDEX));
                    params.put(FinalVariables.KEY_EMAIL, teacherAllInfo.get(FinalVariables.TEACHER_EMAIL_INDEX));
                    params.put("course_code", courseCode);
                    params.put("course_title", courseTitle);
                    params.put("course_teacher", courseTeacher);
                    params.put("class_room", classRoom);
                    params.put("class_time", classTime);
                    params.put("description", description);
                    params.put("date", Long.toString(dateAndTime.currentTimeInMillis()));
                    params.put("table", table);
                    params.put("faculty", teacherAllInfo.get(FinalVariables.TEACHER_FACULTY_INDEX));
                    params.put("table", session);

                }else if(flag == FLAG_BROADCAST_MESSAGE){
                    params.put("name", teacherAllInfo.get(FinalVariables.TEACHER_NAME_INDEX));
                    params.put("email", teacherAllInfo.get(FinalVariables.TEACHER_EMAIL_INDEX));
                    params.put("message", broadcastMessage);
                    params.put("date", Long.toString(dateAndTime.currentTimeInMillis()));
                    params.put("table", table);
                }

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void handleJSON(JSONObject jsonObject, final int flag, String value){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                if(flag == FLAG_VIEW_POSTS){
                    JSONArray arr = jsonObject.getJSONArray("all_schedule_posts");
                    allPostmenName = new ArrayList<>();
                    allPostmenEmail = new ArrayList<>();
                    allPostsSl = new ArrayList<>();
                    allCourseCode = new ArrayList<>();
                    allCourseTitle = new ArrayList<>();
                    allCourseTeacher = new ArrayList<>();
                    allClassRoom = new ArrayList<>();
                    allClassTime = new ArrayList<>();
                    allDescription = new ArrayList<>();
                    allPostsDate = new ArrayList<>();
                    myPostsSl = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject singleObject = arr.getJSONObject(i);
                        if(singleObject.getString("email").equals(teacherAllInfo.get(FinalVariables.TEACHER_EMAIL_INDEX))) {
                            myPostsSl.add(singleObject.getString("sl"));
                        }
                        allPostmenName.add(singleObject.getString("name"));
                        allPostmenEmail.add(singleObject.getString("email"));
                        allPostsSl.add(singleObject.getString("sl"));
                        allCourseCode.add(singleObject.getString("course_code"));
                        allCourseTitle.add(singleObject.getString("course_title"));
                        allCourseTeacher.add(singleObject.getString("course_teacher"));
                        allClassRoom.add(singleObject.getString("class_room"));
                        allClassTime.add(singleObject.getString("class_time"));
                        allDescription.add(singleObject.getString("description"));
                        allPostsDate.add(singleObject.getString("date"));
                    }
                    if(allPostmenName.size()>0){
                        customListAdapterAllSchedulePosts = new CustomListAdapterAllSchedulePosts(
                                this, allPostmenName, allPostmenEmail, allPostsSl,
                                allCourseCode, allCourseTitle, allCourseTeacher, allClassRoom, allClassTime, allDescription,
                                allPostsDate, myPostsSl, value, FinalVariables.FLAG_TEACHER, session);
                        textViewNoSession.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(customListAdapterAllSchedulePosts);

                    }else textViewNoSession.setText("No posts yet");

                }else if(flag == FLAG_ADD_POST){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    message = jsonObject.getString("data");
                    Toast.makeText(getApplicationContext(), jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                }else if(flag == FLAG_BROADCAST_MESSAGE){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                }

            }else { //means error
                listView.setVisibility(View.GONE);
                textViewNoSession.setVisibility(View.VISIBLE);
                textViewNoSession.setText("Failed to retrieve posts");
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            listView.setVisibility(View.GONE);
            textViewNoSession.setVisibility(View.VISIBLE);
            textViewNoSession.setText("Error, while parsing posts");
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            //super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
            return;
        }
        else {
            Toast.makeText(getApplicationContext(), "Tap again to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

}
