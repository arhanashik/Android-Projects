package com.project.blackspider.classschedule.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.Activities.SingleChatActivity;
import com.project.blackspider.classschedule.Models.User;
import com.project.blackspider.classschedule.Adapters.CustomListAdapterChatList;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllChatsFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{
    private ArrayList<String> allInfo;
    private Context context;
    private Intent intent;
    private String message = "";
    private String success = "";
    private String tableName = "";
    private ArrayList<User> allChatList;
    private ArrayList<String> allUserNames;
    private ArrayList<String> allUserFaculties;
    private ArrayList<String> allUserSessions;
    private ArrayList<String> allUserPhones;
    private ArrayList<String> allUserEmails;
    private ArrayList<String> allUserImagePaths;
    private ArrayList<String> allUserOfflineImagePaths;
    private ArrayList<String> fcmDeviceRegIDs;
    private ArrayList<String> allUserStatuses;
    private ArrayList<String> allUserLastMessage;
    private ArrayList<Long> allUserLastChatDate;

    private View fragmentView;
    private TextView textViewNoChat;
    private ListView listViewChatList;
    private RelativeLayout relativeLayout;

    private StringRequest stringRequest;

    private FinalVariables finalVariables = new FinalVariables();
    private User user;
    private CustomListAdapterChatList chatListCustomListAdapter;
    private DBHelper dbHelper;

    private final AlphaAnimation mFadeOut = new AlphaAnimation(1.0f, 0.0f);
    private final AlphaAnimation mFadeIn = new AlphaAnimation(0.0f, 1.0f);

    public AllChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_all_chats, container, false);
        textViewNoChat = (TextView) fragmentView.findViewById(R.id.textViewNoChat);
        listViewChatList = (ListView) fragmentView.findViewById(R.id.listViewChatList);
        context = fragmentView.getContext();
        dbHelper = new DBHelper(context);

        allInfo = new ArrayList<>();
        //allInfo = getArguments().getStringArrayList("allInfo");
        if(allInfo.size()<1) allInfo = dbHelper.getUserInfo();

        tableName = allInfo.get(finalVariables.EMAIL_INDEX).replace(".","_").replace("@","_")+finalVariables.TABLE_CHAT_LIST;
        dbHelper.createChatListTable(tableName);

        setChatList();

        return fragmentView;
    }

    private void setChatList(){
//        final ProgressDialog pd = new ProgressDialog(context);
//        pd.setMessage("Loading...");
//        pd.show();
        textViewNoChat.setText("Loading...");

        refreshChatListFromServer();
//        if(dbHelper.getRowCount(tableName)>0){
//            allChatList = new ArrayList<>();
//            allChatList = dbHelper.getChatList(tableName);
//            allUserNames=new ArrayList<>();
//            allUserFaculties =new ArrayList<>();
//            allUserSessions=new ArrayList<>();
//            allUserPhones=new ArrayList<>();
//            allUserEmails=new ArrayList<>();
//            allUserImagePaths=new ArrayList<>();
//            allUserOfflineImagePaths=new ArrayList<>();
//            fcmDeviceRegIDs=new ArrayList<>();
//            allUserStatuses =new ArrayList<>();
//            allUserLastMessage =new ArrayList<>();
//            allUserLastChatDate =new ArrayList<>();
//
//            for (User user: allChatList){
//                allUserNames.add(user.getName());
//                allUserFaculties.add(user.getFaculty());
//                allUserSessions.add(user.getSession());
//                allUserPhones.add(user.getPhone());
//                allUserEmails.add(user.getEmail());
//                allUserImagePaths.add(user.getImage_path());
//                allUserOfflineImagePaths.add(user.getOffline_image_path());
//                fcmDeviceRegIDs.add(user.getFcmDeviceRegID());
//                allUserStatuses.add(user.getStatus());
//                allUserLastMessage.add(user.getLastMessage());
//                allUserLastChatDate.add(Long.parseLong(user.getDate()));
//            }
//
//            //Collections.sort(allUserLastChatDate, Collections.<Long>reverseOrder());
//
//            //shortingChatListByLastChatDate();
//            //Toast.makeText(context, "Image path: "+allUserOfflineImagePaths.get(0), Toast.LENGTH_LONG).show();
//
//            if(allUserNames.size()>0){
//                chatListCustomListAdapter = new CustomListAdapterChatList(
//                        this.getActivity(), allUserNames, allUserImagePaths,
//                        allUserOfflineImagePaths, fcmDeviceRegIDs, allUserLastMessage,
//                        allUserEmails, tableName);
//                textViewNoChat.setVisibility(View.GONE);
//                listViewChatList.setVisibility(View.VISIBLE);
//                listViewChatList.setAdapter(chatListCustomListAdapter);
//                listViewChatList.setOnItemClickListener(this);
//                listViewChatList.setOnItemLongClickListener(this);
//            }
//        }else {
//            refreshChatListFromServer();
//        }
    }

    private void shortingChatListByLastChatDate(){
        int i,j;
        Long temp;
        for(i = 0; i<allUserLastChatDate.size(); i++){
            for(j = i+1; j<allUserLastChatDate.size(); j++){
                if(allUserLastChatDate.get(i)>allUserLastChatDate.get(j)){
                    temp = allUserLastChatDate.get(i);
                    allUserLastChatDate.add(i, allUserLastChatDate.get(j));
                    allUserLastChatDate.add(j, temp);

                    swapItem(i, j, allUserNames);
                    swapItem(i, j, allUserFaculties);
                    swapItem(i, j, allUserSessions);
                    swapItem(i, j, allUserPhones);
                    swapItem(i, j, allUserEmails);
                    swapItem(i, j, allUserImagePaths);
                    swapItem(i, j, fcmDeviceRegIDs);
                    swapItem(i, j, allUserStatuses);
                    swapItem(i, j, allUserLastMessage);
                }
            }

        }
    }

    private void swapItem(int prev_pos, int new_pos, ArrayList<String> arr){
        String temp;
        temp = arr.get(prev_pos);
        arr.add(prev_pos, arr.get(new_pos));
        arr.add(new_pos, temp);
    }

    private void refreshChatListFromServer(){
        stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.GET_CHAT_LIST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //pd.dismiss();
                if(response.isEmpty()) Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
                else {
                    Log.d("Response: ","Server Response: " + response);
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
                textViewNoChat.setText("Error, while loading chat list");
                Log.d("Error: ","Volley Error: " + volleyError);
                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(new FinalVariables().KEY_FACULTY, allInfo.get(FinalVariables.FACULTY_INDEX));
                params.put(new FinalVariables().KEY_SESSION, allInfo.get(FinalVariables.SESSION_INDEX));

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

                JSONArray arr = jsonObject.getJSONArray("all_users");
                allUserNames=new ArrayList<>();
                allUserFaculties =new ArrayList<>();
                allUserSessions=new ArrayList<>();
                allUserPhones=new ArrayList<>();
                allUserEmails=new ArrayList<>();
                allUserImagePaths=new ArrayList<>();
                allUserOfflineImagePaths=new ArrayList<>();
                fcmDeviceRegIDs=new ArrayList<>();
                allUserStatuses =new ArrayList<>();
                allUserLastMessage =new ArrayList<>();

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject singleObject = arr.getJSONObject(i);
                    if(singleObject.getString("reg").equals(allInfo.get(FinalVariables.REG_INDEX))){
                        continue;
                    }
                    user = new User();
                    user.setName(singleObject.getString("name"));
                    user.setID(singleObject.getString("id"));
                    user.setReg(singleObject.getString("reg"));
                    user.setFaculty(singleObject.getString("faculty"));
                    user.setSession(singleObject.getString("session"));
                    user.setPhone(singleObject.getString("phone"));
                    user.setEmail(singleObject.getString("email"));
                    user.setImage_path(singleObject.getString("image_path"));
                    user.setOffline_image_path("");
                    user.setFcmDeviceRegID(singleObject.getString("fcmDeviceRegID"));
                    user.setStatus(singleObject.getString("status"));
                    dbHelper.saveChatList(user, tableName);

                    allUserNames.add(singleObject.getString("name"));
                    allUserFaculties.add(singleObject.getString("faculty"));
                    allUserSessions.add(singleObject.getString("session"));
                    allUserPhones.add(singleObject.getString("phone"));
                    allUserEmails.add(singleObject.getString("email"));
                    allUserImagePaths.add(singleObject.getString("image_path"));
                    allUserOfflineImagePaths.add("");
                    fcmDeviceRegIDs.add(singleObject.getString("fcmDeviceRegID"));
                    allUserStatuses.add(singleObject.getString("status"));
                    allUserLastMessage.add("");
                }
                if(allUserNames.size()>0){
                    if(textViewNoChat.getVisibility()!=View.GONE){
                        chatListCustomListAdapter = new CustomListAdapterChatList(
                                this.getActivity(), allUserNames, allUserImagePaths,
                                allUserOfflineImagePaths, fcmDeviceRegIDs, allUserLastMessage,
                                allUserEmails, tableName);
                        textViewNoChat.setVisibility(View.GONE);
                        listViewChatList.setVisibility(View.VISIBLE);
                        listViewChatList.setAdapter(chatListCustomListAdapter);
                        listViewChatList.setOnItemClickListener(this);
                        listViewChatList.setOnItemLongClickListener(this);
                    }
                }

            }else { //means error
                textViewNoChat.setText("Failed to retrieve chat list");
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            textViewNoChat.setText("Error, while parsing chat list");
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(context, "Token: " + fcmDeviceRegIDs.get(position), Toast.LENGTH_SHORT).show();
        allInfo = new ArrayList<>();
        allInfo.add(allUserNames.get(position));
        allInfo.add(allUserPhones.get(position));
        allInfo.add(allUserEmails.get(position));
        allInfo.add(allUserImagePaths.get(position));
        allInfo.add(fcmDeviceRegIDs.get(position));
        allInfo.add(allUserStatuses.get(position));
        allInfo.add(allUserOfflineImagePaths.get(position));
        allInfo.add(tableName);

        intent = new Intent(context, SingleChatActivity.class);
        intent.putExtra("flag", FinalVariables.FLAG_SINGLE_CHAT);
        intent.putExtra("receiverInfo", allInfo);
        startActivity(intent);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        relativeLayout = (RelativeLayout) view.findViewById(R.id.icon_selected);
        if(relativeLayout.getVisibility()==View.INVISIBLE){
            relativeLayout.setVisibility(View.VISIBLE);
            relativeLayout.clearAnimation();
            Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_pro);
            relativeLayout.startAnimation(fadeIn);
        }
        else{
            Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_pro);
            relativeLayout.clearAnimation();
            relativeLayout.startAnimation(fadeOut);
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        //Toast.makeText(context, "Token: " + fcmDeviceRegIDs.get(position), Toast.LENGTH_SHORT).show();

        return true;
    }
}
