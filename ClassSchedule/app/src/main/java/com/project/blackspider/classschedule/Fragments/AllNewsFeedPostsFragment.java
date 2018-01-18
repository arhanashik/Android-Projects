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
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
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
import com.project.blackspider.classschedule.Adapters.FeedListAdapter;
import com.project.blackspider.classschedule.Models.FeedItem;
import com.project.blackspider.classschedule.Models.User;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.NewsFeed.NewsFeedPost;
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
public class AllNewsFeedPostsFragment extends Fragment implements View.OnTouchListener{
    private ArrayList<String> allInfo;
    private Context context;
    private Intent intent;
    private String message = "";
    private String success = "";
    private String tableName;

    private ArrayList<String> myPostsSl = new ArrayList<>();

    private View fragmentView;
    private static RelativeLayout rootView;
    private TextView textViewNoPost;
    private ListView listViewAllPosts;
    private static LinearLayout slider;

    private StringRequest stringRequest;
    private BroadcastReceiver broadcastReceiver;
    private PendingIntent pendingIntent;

    private FinalVariables finalVariables = new FinalVariables();
    private User user;
    private DBHelper dbHelper;
    private  static CustomAnimation customAnimation;
    private NewsFeedPost newsFeedPost;
    private FeedListAdapter feedListAdapter;
    private List<FeedItem> feedItems;

    private final AlphaAnimation mFadeOut = new AlphaAnimation(1.0f, 0.3f);
    private final AlphaAnimation mFadeIn = new AlphaAnimation(0.3f, 1.0f);
    private float startY=0.0f, endY=0.0f;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    public AllNewsFeedPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_news_feed, container, false);
        rootView = (RelativeLayout) fragmentView.findViewById(R.id.icon_front);
        textViewNoPost = (TextView) fragmentView.findViewById(R.id.textViewNoPost);
        listViewAllPosts = (ListView) fragmentView.findViewById(R.id.listViewAllPosts);
        slider = (LinearLayout) fragmentView.findViewById(R.id.slider);
        context = fragmentView.getContext();
        dbHelper = new DBHelper(context);

        customAnimation = new CustomAnimation(getContext());

        allInfo = new ArrayList<>();
        //allInfo = getArguments().getStringArrayList("allInfo");
        if(allInfo.size()<1) allInfo = dbHelper.getUserInfo();

        String session = allInfo.get(FinalVariables.SESSION_INDEX).replace("-","_");
        tableName = "table_news_feed_"+allInfo.get(FinalVariables.FACULTY_INDEX).toLowerCase()+"_"+session;

        slider.setOnTouchListener(this);
        if(slider.getVisibility()==View.VISIBLE) slider.setVisibility(View.GONE);

        feedItems = new ArrayList<>();
        feedListAdapter = new FeedListAdapter(getActivity(), feedItems, myPostsSl, slider);
        listViewAllPosts.setAdapter(feedListAdapter);

        setUp();
        viewAllPosts();

        return  fragmentView;
    }

    public static boolean isSliderOpen(){
        return (slider.getVisibility()==View.VISIBLE) ? true : false;
    }

    private void viewAllPosts() {
        textViewNoPost.setText("Loading...");

        stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.GET_ALL_NEWS_FEED_POSTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //pd.dismiss();
                if(response.isEmpty()){
                    //Toast.makeText(context, "No server response!", Toast.LENGTH_SHORT).show();
                    textViewNoPost.setText("No server response at table "+tableName);
                }
                else {
                    Log.d("Response news feed: ","Server Response: " + response);
                    textViewNoPost.setText("Loaded: "+response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener (){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //pd.dismiss();
                textViewNoPost.setText("Error, while loading posts");
                Log.d("Error: ","Volley Error: " + volleyError);
                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("table_name", tableName);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void handleJSON(JSONObject jsonObject){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                JSONArray arr = jsonObject.getJSONArray("all_feed_posts");

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject singleObject = arr.getJSONObject(i);
                    if(singleObject.getString("email").equals(allInfo.get(FinalVariables.EMAIL_INDEX))) {
                        myPostsSl.add(singleObject.getString("sl"));
                    }

                    FeedItem item = new FeedItem();
                    item.setId(i);
                    item.setSl(singleObject.getString("sl"));
                    item.setName(singleObject.getString("name"));
                    item.setEmail(singleObject.getString("email"));

                    // Image might be null sometimes
                    String image = singleObject.isNull("post_image") ? null : singleObject
                            .getString("post_image");
                    item.setImge(image);
                    item.setStatus(singleObject.getString("status"));
                    item.setProfilePic(singleObject.getString("image_path"));
                    String timeStamp = singleObject.isNull("date") ? null : singleObject
                            .getString("date");
                    item.setTimeStamp(timeStamp);

                    // url might be null sometimes
                    String feedUrl = singleObject.isNull("url") ? null : singleObject
                            .getString("url");
                    item.setUrl(feedUrl);

                    feedItems.add(item);
                }
                if(feedItems.size()>0){
                    textViewNoPost.setVisibility(View.GONE);
                    listViewAllPosts.setVisibility(View.VISIBLE);
                    setListViewSelection();
                    listViewAllPosts.setOnScrollListener(new AbsListView.OnScrollListener() {
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

                }else textViewNoPost.setText("No posts yet");
                feedListAdapter.setMyPostsSl(myPostsSl);
                feedListAdapter.notifyDataSetChanged();

            }else { //means error
                textViewNoPost.setText("Failed to retrieve posts");
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            textViewNoPost.setText("Error, while parsing posts");
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

    public static void hideSlideMenu(){
        if(slider.getVisibility() == View.VISIBLE){
            customAnimation.animDown(slider);
            rootView.setAlpha(1.0f);
        }
    }

    private void setUp(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg_for_me = intent.getStringExtra("message");
                String postSl = intent.getStringExtra("POST_SL");
                int pos = Integer.parseInt(postSl);
                sharedPreferences = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_FEED_POST_SL_INFO, Context.MODE_PRIVATE);
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString(finalVariables.SHARED_PREFERENCES_FEED_POST_SL_INFO, postSl);
                sharedPreferencesEditor.commit();
                //log our message value
                Log.i("Broadcast: ", msg_for_me);
                viewAllPosts();
            }
        };
        context.registerReceiver(broadcastReceiver, new IntentFilter("com.project.blackspider.classschedule.SCHEDULE_POST_INTENT"));
        pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.project.blackspider.classschedule.SCHEDULE_POST_INTENT"), 0);
    }

    private void setListViewSelection(){
        sharedPreferences = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_FEED_POST_SL_INFO, Context.MODE_PRIVATE);

        String postSl = sharedPreferences.getString(finalVariables.SHARED_PREFERENCES_FEED_POST_SL_INFO, "0");
        int pos = Integer.parseInt(postSl);
        listViewAllPosts.setSelection(pos);

        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(finalVariables.SHARED_PREFERENCES_FEED_POST_SL_INFO, "0");
        sharedPreferencesEditor.commit();
    }
}
