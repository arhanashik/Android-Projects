package com.project.blackspider.classschedule.Fragments;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.Adapters.ScheduleListAdapter;
import com.project.blackspider.classschedule.Models.ScheduleItem;
import com.project.blackspider.classschedule.Models.User;
import com.project.blackspider.classschedule.Adapters.CustomListAdapterAllSchedulePosts;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.DateAndTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllSchedulePostsFragment extends Fragment implements View.OnTouchListener{
    private ArrayList<String> allInfo;
    private Context context;
    private Intent intent;

    private String message = "";
    private String success = "";
    private String tableName = "";

    private List<ScheduleItem> scheduleItems;
    private ArrayList<String> myPostsSl = new ArrayList<>();

    private View fragmentView;
    private static RelativeLayout rootView;
    private TextView textViewNoSchedulePost;
    private ListView listViewAllSchedulePosts;
    private static LinearLayout slider;

    private StringRequest stringRequest;
    private BroadcastReceiver broadcastReceiver;
    private PendingIntent pendingIntent;

    private FinalVariables finalVariables = new FinalVariables();
    private User user;
    private CustomListAdapterAllSchedulePosts customListAdapterAllSchedulePosts;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private ScheduleListAdapter scheduleListAdapter;
    private  static CustomAnimation customAnimation;

    private float startY=0.0f, endY=0.0f;

    public AllSchedulePostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_all_schedule_posts, container, false);
        rootView = (RelativeLayout) fragmentView.findViewById(R.id.icon_front);
        textViewNoSchedulePost = (TextView) fragmentView.findViewById(R.id.textViewNoSchedulePost);
        listViewAllSchedulePosts = (ListView) fragmentView.findViewById(R.id.listViewAllSchedulePosts);
        slider = (LinearLayout) fragmentView.findViewById(R.id.slider);
        context = fragmentView.getContext();
        dbHelper = new DBHelper(context);
        customAnimation = new CustomAnimation(getContext());
        allInfo = new ArrayList<>();
        //allInfo = getArguments().getStringArrayList("allInfo");
        if(allInfo.size()<1) allInfo = dbHelper.getUserInfo();

        if(slider.getVisibility()==View.VISIBLE) slider.setVisibility(View.GONE);
        slider.setOnTouchListener(this);

        scheduleItems = new ArrayList<>();
        String session = allInfo.get(FinalVariables.SESSION_INDEX).replace("-","_");
        tableName = "table_schedule_"+allInfo.get(FinalVariables.FACULTY_INDEX).toLowerCase()+"_"+session;
        scheduleListAdapter = new ScheduleListAdapter(this.getActivity(), scheduleItems, myPostsSl,
                tableName, FinalVariables.FLAG_STUDENT, session, slider);
        listViewAllSchedulePosts.setAdapter(scheduleListAdapter);

        setUp();
        viewAllPosts();

        return  fragmentView;
    }

    public static boolean isSliderOpen(){
        return (slider.getVisibility()==View.VISIBLE) ? true : false;
    }

    public static void hideSlideMenu(){
        if(slider.getVisibility() == View.VISIBLE){
            customAnimation.animDown(slider);
            rootView.setAlpha(1.0f);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                endY = event.getY();
                if(endY>startY){
                    hideSlideMenu();
                }
                break;
        }

        return false;
    }

    private void viewAllPosts() {
        textViewNoSchedulePost.setText("Loading...");

        stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.GET_All_SCHEDULE_POSTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //pd.dismiss();
                if(response.isEmpty()){
                    //Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
                    textViewNoSchedulePost.setText("No server response!");
                }
                else {
                    Log.d("Response: ","Server Response: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, tableName);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener (){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //pd.dismiss();
                textViewNoSchedulePost.setText("Error, while loading posts");
                Log.d("Error: ","Volley Error: " + volleyError);
                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("table", tableName);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void handleJSON(JSONObject jsonObject, String value){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                JSONArray arr = jsonObject.getJSONArray("all_schedule_posts");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject singleObject = arr.getJSONObject(i);
                    if(singleObject.getString("email").equals(allInfo.get(FinalVariables.EMAIL_INDEX))) {
                        myPostsSl.add(singleObject.getString("sl"));
                    }
                    ScheduleItem item = new ScheduleItem();
                    item.setName(singleObject.getString("name"));
                    item.setEmail(singleObject.getString("email"));
                    item.setSl(singleObject.getString("sl"));
                    item.setCourseCode(singleObject.getString("course_code"));
                    item.setCourseTitle(singleObject.getString("course_title"));
                    item.setCourseTeacher(singleObject.getString("course_teacher"));
                    item.setClassRoom(singleObject.getString("class_room"));
                    item.setClassTime(singleObject.getString("class_time"));
                    item.setDescription(singleObject.getString("description"));
                    String timeStamp = singleObject.isNull("date") ? null : singleObject
                            .getString("date");
                    item.setTimestamp(timeStamp);
                    // url might be null sometimes
                    String feedUrl = singleObject.isNull("url") ? null : singleObject
                            .getString("url");
                    item.setUrl(feedUrl);
                    String profileImage = singleObject.isNull("image_path") ? null : singleObject
                            .getString("image_path");
                    item.setProfileImage(profileImage);
                    String image = singleObject.isNull("post_image") ? null : singleObject
                            .getString("post_image");
                    item.setPostImage(image);
                    scheduleItems.add(item);
                }
                if(scheduleItems.size()>0){
                    textViewNoSchedulePost.setVisibility(View.GONE);
                    listViewAllSchedulePosts.setVisibility(View.VISIBLE);
                    setListViewSelection();
                    listViewAllSchedulePosts.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            if(slider.getVisibility() == View.VISIBLE){
                                customAnimation.animDown(slider);
                                rootView.setAlpha(1.0f);
                            }
                        }
                    });

                }else textViewNoSchedulePost.setText("No posts yet");

                scheduleListAdapter.setMyPostsSl(myPostsSl);
                scheduleListAdapter.notifyDataSetChanged();

            }else { //means error
                textViewNoSchedulePost.setText("Failed to retrieve posts");
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            textViewNoSchedulePost.setText("Error, while parsing posts");
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        //setUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unregisterReceiver(broadcastReceiver);
    }

    private void setUp(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg_for_me = intent.getStringExtra("message");
                String postSl = intent.getStringExtra("POST_SL");
                //Toast.makeText(context,"Sl: "+pos+" Msg: "+msg_for_me, Toast.LENGTH_LONG).show();
                sharedPreferences = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_POST_SL_INFO, Context.MODE_PRIVATE);
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString(finalVariables.SHARED_PREFERENCES_POST_SL_INFO, postSl);
                sharedPreferencesEditor.commit();
                viewAllPosts();
            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter("com.project.blackspider.classschedule.SCHEDULE_POST_INTENT"));
        pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.project.blackspider.classschedule.SCHEDULE_POST_INTENT"), 0);
    }

    private void setListViewSelection(){
        sharedPreferences = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_POST_SL_INFO, Context.MODE_PRIVATE);

        String postSl = sharedPreferences.getString(finalVariables.SHARED_PREFERENCES_POST_SL_INFO, "0");
        int pos = Integer.parseInt(postSl);
        listViewAllSchedulePosts.setSelection(pos);

        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(finalVariables.SHARED_PREFERENCES_POST_SL_INFO, "0");
        sharedPreferencesEditor.commit();
    }

}
