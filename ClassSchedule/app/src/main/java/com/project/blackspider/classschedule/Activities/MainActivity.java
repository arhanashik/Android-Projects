package com.project.blackspider.classschedule.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.App.ConnectivityController;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Fragments.AllNewsFeedPostsFragment;
import com.project.blackspider.classschedule.Fragments.AllSchedulePostsFragment;
import com.project.blackspider.classschedule.Receiver.ConnectivityReceiver;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.ViewPagers.MainActivityViewPagerAdapter;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.FCM.SendFCMPushNotification;
import com.project.blackspider.classschedule.Utils.DateAndTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Menu menu;

    private EditText editTextPost;
    private EditText editTextCourseCode;
    private EditText editTextCourseTitle;
    private EditText editTextCourseTeacher;
    private EditText editTextClassRoom;
    private EditText editTextDescription;

    private TextView noticeBoardTextView;

    private TimePicker timePickerClassTime;

    private FinalVariables finalVariables;
    private MainActivityViewPagerAdapter mainActivityViewPagerAdapter;
    private Intent intent;
    private DBHelper dbHelper;
    private SendFCMPushNotification sendFCMPushNotification;
    private DateAndTime dateAndTime = new DateAndTime();
    private CustomAnimation customAnimation;

    private LayoutInflater li;
    private View promptsView;
    private ProgressDialog pd;
    private Handler mHandler = new Handler();

    private ArrayList<String> allInfo;
    private String courseCode = "";
    private String courseTitle = "";
    private String courseTeacher = "";
    private String classRoom = "";
    private String classTime = "";
    private String description = "";

    private boolean isConnected = false;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private BroadcastReceiver broadcastReceiver;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private final int FLAG_NEWS_FEED_POST = 0;
    private final int FLAG_SCHEDULE_POST = 1;
    private final int FLAG_REVIEW_SCHEDULE_POST = 2;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;

    // # milliseconds, desired time passed between two back presses.
    private static final int TIME_INTERVAL = 2500;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //allInfo = getIntent().getStringArrayListExtra("allInfo");
        noticeBoardTextView = (TextView) findViewById(R.id.noticeBoardTextView);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        dbHelper = new DBHelper(this);
        finalVariables = new FinalVariables();
        allInfo = dbHelper.getUserInfo();
        customAnimation = new CustomAnimation(this);
        sharedPreferences = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);
        sendFCMPushNotification = new SendFCMPushNotification(this);
        noticeBoardTextView.setText("Notice board - 0 new Notice");
        setUpTabConfig();

        checkConnection();

        noticeBoardTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.noticeBoardTextView:
                intent = new Intent(MainActivity.this, NoticeBoardActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_post:
                addPost();

                break;

            case R.id.action_group:
                intent = new Intent(getApplicationContext(), SingleChatActivity.class);
                intent.putExtra("flag", FinalVariables.FLAG_GROUP_CHAT);
                startActivity(intent);

                break;

            case R.id.action_broadcast:
                intent = new Intent(getApplicationContext(), SingleChatActivity.class);
                intent.putExtra("flag", FinalVariables.FLAG_GET_BROADCAST_MESSAGES);
                startActivity(intent);

                break;

            case R.id.action_refresh_main_menu:
                if(viewPager.getCurrentItem()==0){
                    viewPager.setCurrentItem(0);

                }else if(viewPager.getCurrentItem()==1){
                    viewPager.setCurrentItem(1);

                }else if(viewPager.getCurrentItem()==2){
                    viewPager.setCurrentItem(2);

                }

                break;

            case R.id.action_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putStringArrayListExtra("allInfo", allInfo);
                startActivity(intent);

                break;

            case R.id.action_sign_out:
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Sign Out")
                        .setMessage("You may not receive any messages before signing in again.\n\nStill want to sign out?")
                        .setPositiveButton("Yes, Do It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPreferencesEditor = sharedPreferences.edit();
                                sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, false);
                                sharedPreferencesEditor.commit();
                                dbHelper.deleteUserInfo();
                                intent = new Intent(MainActivity.this, WelcomeActivity.class);
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
                                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
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

    private void setUpTabConfig(){
        tabLayout.addTab(tabLayout.newTab().setText(FinalVariables.TAB_ONE));
        tabLayout.addTab(tabLayout.newTab().setText(FinalVariables.TAB_TWO));
        tabLayout.addTab(tabLayout.newTab().setText(FinalVariables.TAB_THREE));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mainActivityViewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), allInfo);
        viewPager.setAdapter(mainActivityViewPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getText().equals(FinalVariables.TAB_ONE)){
                    if(noticeBoardTextView.getVisibility()==View.GONE){
                        noticeBoardTextView.setVisibility(View.VISIBLE);
                        customAnimation.slideDown(noticeBoardTextView);
                    }
                    menu.getItem(0).setVisible(true);
                    menu.getItem(2).setVisible(false);
                    menu.getItem(3).setVisible(false);
                    if(AllSchedulePostsFragment.isSliderOpen())
                        AllSchedulePostsFragment.hideSlideMenu();

                }else if(tab.getText().equals(FinalVariables.TAB_TWO)){
                    if(noticeBoardTextView.getVisibility()==View.GONE){
                        noticeBoardTextView.setVisibility(View.VISIBLE);
                        customAnimation.slideDown(noticeBoardTextView);
                    }
                    menu.getItem(0).setVisible(true);
                    menu.getItem(2).setVisible(false);
                    menu.getItem(3).setVisible(false);
                    if(AllNewsFeedPostsFragment.isSliderOpen())
                        AllNewsFeedPostsFragment.hideSlideMenu();

                }else if(tab.getText().equals(FinalVariables.TAB_THREE)){
                    customAnimation.slideUp(noticeBoardTextView);
                    noticeBoardTextView.setVisibility(View.GONE);
                    menu.getItem(0).setVisible(false);
                    menu.getItem(2).setVisible(true);
                    menu.getItem(3).setVisible(true);

                    if(AllNewsFeedPostsFragment.isSliderOpen())
                        AllNewsFeedPostsFragment.hideSlideMenu();
                    if(AllSchedulePostsFragment.isSliderOpen())
                        AllSchedulePostsFragment.hideSlideMenu();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void addPost(){
        if(viewPager.getCurrentItem() == 0){
            addNewsFeedPost();

        }if(viewPager.getCurrentItem() == 1){
            //Toast.makeText(getApplicationContext(), "Publish in schedule", Toast.LENGTH_SHORT).show();
            addSchedulePost();

        }

    }

    private void addNewsFeedPost() {
        String session = allInfo.get(FinalVariables.SESSION_INDEX).replace("-","_");
        String tableName = "table_news_feed_"+allInfo.get(FinalVariables.FACULTY_INDEX)+"_"+session;
        tableName=tableName.toLowerCase();
        //Toast.makeText(getApplicationContext(), "Publish in "+tableName, Toast.LENGTH_SHORT).show();
        getUserInput(FLAG_NEWS_FEED_POST, tableName);

    }


    private void addSchedulePost() {
        String session = allInfo.get(FinalVariables.SESSION_INDEX).replace("-","_");
        String tableName = "table_schedule_"+allInfo.get(FinalVariables.FACULTY_INDEX)+"_"+session;
        tableName=tableName.toLowerCase();
        //Toast.makeText(getApplicationContext(), "Publish in "+tableName, Toast.LENGTH_SHORT).show();
        getUserInput(FLAG_SCHEDULE_POST, tableName);

    }

    protected void getUserInput(final int flag, final String tableName){
        alertDialogBuilder = new AlertDialog.Builder(this);

        // get layout_developer.xml view
        li = LayoutInflater.from(this);
        if (flag == FLAG_NEWS_FEED_POST){
            promptsView = li.inflate(R.layout.layout_single_post_input, null);
            editTextPost = (EditText) promptsView.findViewById(R.id.editTextPost);

            alertDialogBuilder.setView(promptsView)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

        }else if(flag == FLAG_SCHEDULE_POST){
            promptsView = li.inflate(R.layout.layout_schedule_post_input, null);
            editTextCourseCode = (EditText) promptsView.findViewById(R.id.editTextCourseCode);
            editTextCourseTitle = (EditText) promptsView.findViewById(R.id.editTextCourseTitle);
            editTextCourseTeacher = (EditText) promptsView.findViewById(R.id.editTextCourseTeacher);
            editTextClassRoom = (EditText) promptsView.findViewById(R.id.editTextClassRoom);
            editTextDescription = (EditText) promptsView.findViewById(R.id.editTextDescription);
            timePickerClassTime = (TimePicker) promptsView.findViewById(R.id.timePickerClassTime);

            if(!courseCode.isEmpty()) editTextCourseCode.setText(courseCode);
            if(!courseTitle.isEmpty()) editTextCourseTitle.setText(courseTitle);
            if(!courseTeacher.isEmpty()) editTextCourseTeacher.setText(courseTeacher);
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

        }else if(flag == FLAG_REVIEW_SCHEDULE_POST){
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
                            getUserInput(FLAG_SCHEDULE_POST, tableName);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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
                        if(flag == FLAG_NEWS_FEED_POST){
                            String postMessage = editTextPost.getText().toString();
                            if(postMessage.isEmpty() || postMessage.length()>500){
                                Toast.makeText(getApplicationContext(), "Invalid Message length", Toast.LENGTH_SHORT).show();
                            }else {
                                doVolleyRequest(postMessage, FinalVariables.ADD_NEWS_FEED_POST_URL, FLAG_NEWS_FEED_POST, tableName);
                            }

                        }else if(flag == FLAG_SCHEDULE_POST) {
                            courseCode = editTextCourseCode.getText().toString();
                            courseTitle = editTextCourseTitle.getText().toString();
                            courseTeacher = editTextCourseTeacher.getText().toString();
                            classRoom = editTextClassRoom.getText().toString();
                            description = editTextDescription.getText().toString();

                            if(courseTitle.isEmpty() || classTime.isEmpty()){
                                Toast.makeText(getApplicationContext(), "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_SCHEDULE_POST, tableName);
                            }else if(description.length()>250){
                                Toast.makeText(getApplicationContext(), "Too Long Description(Max length 250 word)",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_SCHEDULE_POST, tableName);
                            }else getUserInput(FLAG_REVIEW_SCHEDULE_POST, tableName);

                        }else if(flag == FLAG_REVIEW_SCHEDULE_POST){
                            if(courseTitle.isEmpty() || classTime.isEmpty()){
                                Toast.makeText(getApplicationContext(), "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_SCHEDULE_POST, tableName);
                            }else if(description.length()>250){
                                Toast.makeText(getApplicationContext(), "Too Long Description(Max length 250 word)",
                                        Toast.LENGTH_SHORT).show();
                                getUserInput(FLAG_SCHEDULE_POST, tableName);
                            }else doVolleyRequest("", FinalVariables.ADD_SCHEDULE_POST_URL, FLAG_SCHEDULE_POST, tableName);

                        }
                    }
                });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    private void doVolleyRequest(final String value, String url, final int flag, final String tableName){
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
                params.put(FinalVariables.KEY_NAME, allInfo.get(FinalVariables.NAME_INDEX));
                params.put(FinalVariables.KEY_EMAIL, allInfo.get(FinalVariables.EMAIL_INDEX));

                if(flag == FLAG_NEWS_FEED_POST){
                    params.put(FinalVariables.KEY_IMAGE_PATH, allInfo.get(FinalVariables.IMAGE_PATH_INDEX));
                    params.put("date", Long.toString(dateAndTime.currentTimeInMillis()));
                    params.put("post", value);
                    params.put("table", tableName);

                }else if(flag == FLAG_SCHEDULE_POST){
                    params.put("course_code", courseCode);
                    params.put("course_title", courseTitle);
                    params.put("course_teacher", courseTeacher);
                    params.put("class_room", classRoom);
                    params.put("class_time", classTime);
                    params.put("description", description);
                    params.put("date", Long.toString(dateAndTime.currentTimeInMillis()));
                    params.put("table", tableName);

                }
                params.put("faculty", allInfo.get(FinalVariables.FACULTY_INDEX));
                params.put("session", allInfo.get(FinalVariables.SESSION_INDEX));

                //returning parameters
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
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
                if(flag==FLAG_NEWS_FEED_POST){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    message = jsonObject.getString("data");
                    Toast.makeText(getApplicationContext(), jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                }else if(flag==FLAG_SCHEDULE_POST){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    message = jsonObject.getString("data");
                    Toast.makeText(getApplicationContext(), jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                }

            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                message = jsonObject.getString("data");
                Toast.makeText(getApplicationContext(), jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityController.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        this.isConnected = isConnected;
    }

    // Method to manually check connection status
    private void checkConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                isConnected = ConnectivityReceiver.isConnected();
                                if (isConnected){
                                    noticeBoardTextView.setText("Connected");
                                    noticeBoardTextView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    noticeBoardTextView.setTextColor(Color.WHITE);
                                    //noticeBoardTextView.setVisibility(View.GONE);
                                    noticeBoardTextView.setText("Notice board - 0 new Notice");
                                }else{
                                    noticeBoardTextView.setVisibility(View.VISIBLE);
                                    noticeBoardTextView.setText("Connection not available");
                                    noticeBoardTextView.setBackgroundColor(Color.RED);
                                    noticeBoardTextView.setTextColor(Color.WHITE);
                                }
                            }
                        });
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if(AllNewsFeedPostsFragment.isSliderOpen()){
            AllNewsFeedPostsFragment.hideSlideMenu();


        }else if(AllSchedulePostsFragment.isSliderOpen()){

            AllSchedulePostsFragment.hideSlideMenu();

        } else {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
            {
                //super.onBackPressed();
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
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


}
