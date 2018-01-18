package com.project.blackspider.classschedule.Teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.Manifest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.blackspider.classschedule.Adapters.SingleChatListMessageAdapter;
import com.project.blackspider.classschedule.Models.Message;
import com.project.blackspider.classschedule.Adapters.AllSingleChatMessagesAdapter;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.Adapters.CustomListAdapterAllBroadcastMessages;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.FCM.SendFCMPushNotification;
import com.project.blackspider.classschedule.Utils.DateAndTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class TeacherChatActivity extends AppCompatActivity implements View.OnClickListener,
        AllSingleChatMessagesAdapter.AllSingleChatMessagesAdapterListener{
    private ArrayList<String> receiverInfo;
    private ArrayList<String> senderInfo;
    private List<Message> allMessages;
    private ArrayList<String> allSingleChatMessagesSl = new ArrayList<>();
    private ArrayList<String> allSingleChatMessages  = new ArrayList<>();
    private ArrayList<String> allSingleChatMessagesType  = new ArrayList<>();
    private ArrayList<String> allSingleChatMessagesDeliveryReport  = new ArrayList<>();
    private ArrayList<String> allSingleChatMessagesSeenReport  = new ArrayList<>();
    private ArrayList<String> allSingleChatMessagesDate  = new ArrayList<>();
    List<Integer> selectedItemPositions;

    private ArrayList<String> groupChatInfo;
    private ArrayList<String> groupChatNames;
    private ArrayList<String> groupChatFacultys;
    private ArrayList<String> groupChatSessions;
    private ArrayList<String> groupChatPhones;
    private ArrayList<String> groupChatEmails;
    private ArrayList<String> groupChatDeviceRegIDs;

    private ArrayList<String> allBroadcastMessageSenders;
    private ArrayList<String> allBroadcastMessageSendersEmail;
    private ArrayList<String> allBroadcastMessages;
    private ArrayList<String> allBroadrcastMessageDates;

    private Bitmap bitmap;
    private String userGivenMessage;
    private String activityTitle;
    private int flag;
    private final int RECEIVER_NAME_INDEX = 0;
    private final int RECEIVER_PHONE_INDEX = 1;
    private final int RECEIVER_EMAIL_INDEX = 2;
    private final int RECEIVER_IMAGE_PATH_INDEX = 3;
    private final int RECEIVER_FCM_DEVICE_REG_ID_INDEX = 4;
    private final int RECEIVER_STATUS_INDEX = 5;

    private FinalVariables finalVariables = new FinalVariables();
    private CustomImageConverter customImageConverter = new CustomImageConverter();
    private CustomAnimation customAnimation;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;
    private AllSingleChatMessagesAdapter allSingleChatMessagesAdapter;
    private SingleChatListMessageAdapter singleChatListMessageAdapter;
    private CustomListAdapterAllBroadcastMessages customListAdapterAllBroadcastMessages;
    private SendFCMPushNotification sendFCMPushNotification;
    private DBHelper dbHelper;
    private DateAndTime dateAndTime;
    private BroadcastReceiver broadcastReceiver;
    private ProgressDialog progressDialog;
    private Vibrator vibrator;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private EmojiconEditText editTextMessage;
    private ImageView imageViewEmojIconKeyboard;
    private ImageView imgViewSendMsgBtn;
    private LinearLayout linearLayoutAllMessage;
    private ListView listViewAllMessages;
    private RecyclerView recyclerViewAllMsg;

    private EmojIconActions emojIconActions;

    private ImageLoader imageLoader;
    private RequestQueue mRequestQueue;
    private StringRequest stringRequest;

    private String sentMessage = "";
    private String sentMessageDate = "";
    private String receivedMessage = "";
    private String receivedMessageDate = "";
    private String senderName = "";
    private String senderEmail = "";
    private String senderSession = "";
    private String tableName = "";

    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;

    private ContextWrapper contextWrapper;
    private File offlineFileDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        imageViewEmojIconKeyboard = (ImageView) findViewById(R.id.imageViewEmojIconKeyboard);
        imgViewSendMsgBtn = (ImageView) findViewById(R.id.imageViewSendMessage);
        linearLayoutAllMessage = (LinearLayout) findViewById(R.id.linearLayout1);
        listViewAllMessages = (ListView) findViewById(R.id.list_view_all_messages);

        InputMethodManager imm = (InputMethodManager)getSystemService
                (TeacherChatActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);

        sendFCMPushNotification = new SendFCMPushNotification(this);
        customAnimation = new CustomAnimation(this);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(this);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(imgViewSendMsgBtn);
        imgViewSendMsgBtn.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        dateAndTime = new DateAndTime();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        receiverInfo = new ArrayList<>();
        flag = getIntent().getIntExtra("flag", 0);
        senderName = getIntent().getStringExtra("name");
        senderEmail = getIntent().getStringExtra("email");
        senderSession = getIntent().getStringExtra("session");
        tableName = getIntent().getStringExtra("tableName");
        senderInfo = new ArrayList<>();
        //senderInfo = dbHelper.getUserInfo();

        emojIconActions = new EmojIconActions(this, linearLayoutAllMessage, editTextMessage, imageViewEmojIconKeyboard);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.icon_keyboard3, R.drawable.icon_emo_btn3);

        if(flag == FinalVariables.FLAG_SINGLE_CHAT){
            receiverInfo = getIntent().getStringArrayListExtra("receiverInfo");
            activityTitle = "\t"+receiverInfo.get(RECEIVER_NAME_INDEX);

            contextWrapper = new ContextWrapper(getApplicationContext());
            offlineFileDir = contextWrapper.getDir("stu_images", Context.MODE_PRIVATE);
            final String imageName = receiverInfo.get(RECEIVER_EMAIL_INDEX).replace(".","_").replace("@","_")+".png";
            if(!offlineFileDir.exists()) offlineFileDir.mkdir();
            final File destImgPath = new File(offlineFileDir, imageName);

            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(destImgPath));
                bitmap = customImageConverter.getResizedBitmap(bitmap, 80);
                bitmap = customImageConverter.getCircledBitmap(bitmap);
                getSupportActionBar().setLogo(new BitmapDrawable(getResources(), bitmap));
                //bitmap.recycle();
                bitmap =null;
                Log.e("User image loading: ", destImgPath.getAbsolutePath());
            }catch (FileNotFoundException e){
                Log.e("User image loading: ", e.getMessage(), e);
            }

            allSingleChatMessagesAdapter = new AllSingleChatMessagesAdapter(this, allSingleChatMessagesSl,
                    allSingleChatMessages, allSingleChatMessagesType, allSingleChatMessagesDeliveryReport,
                    allSingleChatMessagesSeenReport, allSingleChatMessagesDate, this);
            listViewAllMessages.setAdapter(allSingleChatMessagesAdapter);
            listViewAllMessages.setSelection(allSingleChatMessagesAdapter.getCount()-1);
            prepareSingleChatData();

            actionModeCallback = new ActionModeCallback();
            // show loader and fetch messages

        }else if(flag == FinalVariables.FLAG_GROUP_CHAT){
            groupChatInfo = new ArrayList<>();
            groupChatNames = new ArrayList<>();
            groupChatFacultys = new ArrayList<>();
            groupChatSessions = new ArrayList<>();
            groupChatPhones = new ArrayList<>();
            groupChatEmails = new ArrayList<>();
            groupChatDeviceRegIDs = new ArrayList<>();
            activityTitle = "Group Chat";


        }else if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES){
            activityTitle = "Broadcast Messages";

            getAndSetAllMessages(FinalVariables.FLAG_GET_BROADCAST_MESSAGES);
        }else {

        }
        setTitle(activityTitle);//set the title of this activity

    }

    private void prepareSingleChatData() {
        String tableName = senderInfo.get(FinalVariables.EMAIL_INDEX).replace(".","_").replace("@","_")+
                "_"+ FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +receiverInfo.get(RECEIVER_EMAIL_INDEX)
                .replace(".","_").replace("@","_");
        dbHelper.createSingleChatMessagesTable(tableName);
        int rowCount = dbHelper.getRowCount(tableName);
        if(rowCount>0){
            allMessages = dbHelper.getSingleChatAllMessages(tableName);
            allSingleChatMessagesSl.clear();
            allSingleChatMessages.clear();
            allSingleChatMessagesType.clear();
            allSingleChatMessagesDeliveryReport.clear();
            allSingleChatMessagesSeenReport.clear();
            allSingleChatMessagesDate.clear();
            for(Message message: allMessages){
                allSingleChatMessagesSl.add(message.getSl());
                allSingleChatMessages.add(message.getMessage());
                allSingleChatMessagesType.add(message.getSent());
                allSingleChatMessagesDeliveryReport.add(message.getDelivered());
                allSingleChatMessagesSeenReport.add(message.getSeen());
                allSingleChatMessagesDate.add(message.getDate());
            }
            allSingleChatMessagesAdapter.notifyDataSetChanged();
            listViewAllMessages.setSelection(allSingleChatMessagesAdapter.getCount()-1);
        }
        else {
            dbHelper.createSingleChatMessagesTable(tableName);
            Toast.makeText(getApplicationContext(),"Send your first message!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v== imgViewSendMsgBtn){
            userGivenMessage = editTextMessage.getText().toString();
            if(userGivenMessage.length()>150){
                Toast.makeText(getApplicationContext(), "Too long message!", Toast.LENGTH_SHORT).show();

            }else {
                editTextMessage.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService
                        (TeacherChatActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);
                if(!userGivenMessage.isEmpty()){
                    String notification = userGivenMessage;

                    if(flag == FinalVariables.FLAG_SINGLE_CHAT){
                        sendFCMPushNotification.singleChat(receiverInfo.get(RECEIVER_EMAIL_INDEX),
                                receiverInfo.get(RECEIVER_FCM_DEVICE_REG_ID_INDEX), notification);
                        dbHelper.updateLastChatTimeAndMessage(receiverInfo.get(receiverInfo.size()-1),
                                receiverInfo.get(RECEIVER_EMAIL_INDEX), notification, dateAndTime.currentTimeInMillis());
                        int msgSl = 1;
                        if(allSingleChatMessagesSl.size()>0)
                            msgSl=Integer.parseInt(allSingleChatMessagesSl.get(allSingleChatMessagesSl.size()-1))+1;
                        allSingleChatMessagesSl.add(msgSl+"");
                        allSingleChatMessages.add(notification);
                        allSingleChatMessagesType.add(FinalVariables.SENT_MESSAGE);
                        allSingleChatMessagesDeliveryReport.add("Sending");
                        allSingleChatMessagesSeenReport.add("--");
                        allSingleChatMessagesDate.add(dateAndTime.currentDateAndTimeFormate4());
                        allSingleChatMessagesAdapter.notifyDataSetChanged();
                        listViewAllMessages.setSelection(allSingleChatMessagesAdapter.getCount()-1);

                    }else if(flag == FinalVariables.FLAG_GROUP_CHAT){
                        // [START subscribe_topics]
                        FirebaseMessaging.getInstance().subscribeToTopic("group_A");
                        // [END subscribe_topics]

                        // Log and toast
                        Log.d(FinalVariables.TAG, "Subscription Done!");
                        //Toast.makeText(getApplicationContext(), "Subscription Done!", Toast.LENGTH_SHORT).show();
                        sendFCMPushNotification.groupChat(groupChatDeviceRegIDs, notification);

                    }else if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES){
                        getAndSetAllMessages(FinalVariables.FLAG_SEND_BROADCAST_MESSAGE);

                    }else {

                    }

                }else {
                    if(flag == FinalVariables.FLAG_GROUP_CHAT){
                        // [START subscribe_topics]
                        FirebaseMessaging.getInstance().subscribeToTopic("group_A");
                        // [END subscribe_topics]

                        // Log and toast
                        Log.d(FinalVariables.TAG, "Subscription Done!");
                        Toast.makeText(getApplicationContext(), "Too short message!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }

    private void getAndSetAllMessages(final int flag){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        String url = "";
        if(flag == FinalVariables.FLAG_SINGLE_CHAT) url = FinalVariables.GET_SINGLE_CHAT_MESSAGES_URL;
        else if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES) url = FinalVariables.GET_BROADCAST_MESSAGES_URL;
        else if(flag == FinalVariables.FLAG_SEND_BROADCAST_MESSAGE) url = FinalVariables.SEND_BROADCAST_MESSAGE_URL;

        stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if(response.isEmpty()) Toast.makeText(getApplicationContext(), "No server response", Toast.LENGTH_SHORT).show();
                else {
                    Log.d("Response: ","Server Response: " + response);
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
                progressDialog.dismiss();
                if(flag == FinalVariables.FLAG_SINGLE_CHAT){
                }
                Log.d("Error: ","Volley Error: " + volleyError);
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                if(flag == FinalVariables.FLAG_SINGLE_CHAT){
                    params.put("f2_email", receiverInfo.get(RECEIVER_EMAIL_INDEX)); //who sent me messages
                    params.put("f1_email", senderInfo.get(FinalVariables.EMAIL_INDEX)); //me
                }else if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES){
                    params.put("table", tableName);
                }else if(flag == FinalVariables.FLAG_SEND_BROADCAST_MESSAGE){
                    params.put("name", senderName);
                    params.put("email", senderEmail);
                    params.put("message", userGivenMessage);
                    params.put("date", Long.toString(dateAndTime.currentTimeInMillis()));
                    params.put("table", tableName);
                }

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    String success = "";
    String message = "";

    private void handleJSON(JSONObject jsonObject, int flag){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString("message");
                success = jsonObject.getString("success");
                if(flag == FinalVariables.FLAG_SINGLE_CHAT){
                    //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    sentMessage = jsonObject.getString("sent_message");
                    sentMessageDate = jsonObject.getString("sent_date");
                    receivedMessage = jsonObject.getString("received_message");
                    receivedMessageDate = jsonObject.getString("received_date");
                    if(sentMessage.isEmpty()) sentMessage = "--";
                    if(sentMessageDate.isEmpty()) sentMessageDate = "-- ---, 00:00";
                    if(receivedMessage.isEmpty()) receivedMessage = "--";
                    if(receivedMessageDate.isEmpty()) receivedMessageDate = "-- ---, 00:00";

                }else if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES){
                    allBroadcastMessageSenders = new ArrayList<>();
                    allBroadcastMessageSendersEmail = new ArrayList<>();
                    allBroadcastMessages = new ArrayList<>();
                    allBroadrcastMessageDates = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("all_broadcast_messages");
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        allBroadcastMessageSenders.add(jsonObject1.getString("name"));
                        allBroadcastMessageSendersEmail.add(jsonObject1.getString("email"));
                        allBroadcastMessages.add(jsonObject1.getString("message"));
                        allBroadrcastMessageDates.add(jsonObject1.getString("date"));
                    }
                    if(allBroadcastMessageSenders.size()<=0){
                        Toast.makeText(getApplicationContext(), "No message found", Toast.LENGTH_SHORT).show();
                    }else {
                        customListAdapterAllBroadcastMessages = new CustomListAdapterAllBroadcastMessages(this,
                                allBroadcastMessageSenders, allBroadcastMessageSendersEmail, allBroadcastMessages,
                                allBroadrcastMessageDates);
                        listViewAllMessages.setAdapter(customListAdapterAllBroadcastMessages);
                        listViewAllMessages.setSelection(listViewAllMessages.getAdapter().getCount()-1);
                        progressDialog.dismiss();
                    }

                }else if(flag == FinalVariables.FLAG_SEND_BROADCAST_MESSAGE){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    getAndSetAllMessages(FinalVariables.FLAG_GET_BROADCAST_MESSAGES);
                    progressDialog.dismiss();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_single_chat, menu);

        if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES) menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_call:
                makeCall();
                break;

            case R.id.action_refresh_main_menu:
                //if(flag == FinalVariables.FLAG_SINGLE_CHAT) getAndSetAllMessages(FinalVariables.FLAG_SINGLE_CHAT);
                //else if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES) getAndSetAllMessages(FinalVariables.FLAG_GET_BROADCAST_MESSAGES);
                String title = item.getTitle().toString();
                if(title.equals("Block")){
                    item.setTitle("Unblock");
                    Toast.makeText(getApplicationContext(),"You blocked "+activityTitle, Toast.LENGTH_SHORT).show();
                }else {
                    item.setTitle("Block");
                    Toast.makeText(getApplicationContext(),"You unblocked "+activityTitle, Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (allSingleChatMessagesAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            String message = allSingleChatMessages.get(position);
            //allSingleChatMessagesAdapter.notifyDataSetChanged();

            Toast.makeText(getApplicationContext(), "Read: " + message, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        allSingleChatMessagesAdapter.toggleSelection(position);
        int count = allSingleChatMessagesAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void makeCall(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Call "+receiverInfo.get(RECEIVER_NAME_INDEX)+"?")
                .setCancelable(true)
                .setPositiveButton("Voice Call (Using mobile balance)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+receiverInfo.get(RECEIVER_PHONE_INDEX)));
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        startActivity(callIntent);

                    }
                })
                /*.setNegativeButton("Audio Call (Using internet)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNeutralButton("Video Call (Using internet)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Under Construction", Toast.LENGTH_SHORT).show();
                        //Intent callIntent = new Intent("com.android.phone.videocall");
                        //callIntent.putExtra("videocall", true);
                        //callIntent.setData(Uri.parse("tel:" + receiverInfo.get(RECEIVER_PHONE_INDEX)));
                        //startActivity(callIntent);

                    }
                })*/;
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("com.project.blackspider.classschedule.CHAT_INTENT");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long[] pattern = {0, 100, 1000};
                //extract our message from intent
                String msg_for_me = intent.getStringExtra("message");
                String intentFlag = intent.getStringExtra("flag");
                //Toast.makeText(getApplicationContext(), "Broadcast Received: "+msg_for_me, Toast.LENGTH_SHORT).show();
                //log our message value
                Log.i("Broadcast: ", msg_for_me);
                if(flag == FinalVariables.FLAG_SINGLE_CHAT) {
                    dbHelper.updateLastChatTimeAndMessage(receiverInfo.get(receiverInfo.size()-1),
                            receiverInfo.get(RECEIVER_EMAIL_INDEX), msg_for_me,dateAndTime.currentTimeInMillis());

                    if(intentFlag.equals("0")){
                        vibrator.vibrate(pattern, -1);
                        int msgSl = 1;
                        if(allSingleChatMessagesSl.size()>0)
                            msgSl=Integer.parseInt(allSingleChatMessagesSl.get(allSingleChatMessagesSl.size()-1))+1;
                        allSingleChatMessagesSl.add(msgSl+"");
                        allSingleChatMessages.add(msg_for_me);
                        allSingleChatMessagesType.add(FinalVariables.RECEIVED_MESSAGE);
                        allSingleChatMessagesDeliveryReport.add("");
                        allSingleChatMessagesSeenReport.add("");
                    }else if(intentFlag.equals("1")){
                        String report = intent.getStringExtra("report");
                        int sl = 1;
                        if(allSingleChatMessagesType.size()>0)
                            sl=allSingleChatMessagesType.size()-1;
                        allSingleChatMessagesType.add(sl,FinalVariables.SENT_MESSAGE);
                        if(allSingleChatMessagesDeliveryReport.size()>0)
                            sl=allSingleChatMessagesDeliveryReport.size()-1;
                        allSingleChatMessagesDeliveryReport.add(sl,report);
                        if(allSingleChatMessagesSeenReport.size()>0)
                            sl=allSingleChatMessagesSeenReport.size()-1;
                        allSingleChatMessagesSeenReport.add(sl,"--");
                    }
                    int sl = 1;
                    if(allSingleChatMessagesDate.size()>0)
                        sl=allSingleChatMessagesDate.size()-1;
                    allSingleChatMessagesDate.add(sl,dateAndTime.currentDateAndTimeFormate4());
                    allSingleChatMessagesAdapter.notifyDataSetChanged();
                    listViewAllMessages.setSelection(allSingleChatMessagesAdapter.getCount()-1);

                }else if(flag == FinalVariables.FLAG_GET_BROADCAST_MESSAGES){
                    getAndSetAllMessages(FinalVariables.FLAG_GET_BROADCAST_MESSAGES);
                }


            }
        };
        //registering our receiver
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            allSingleChatMessagesAdapter.clearSelections();
            actionMode = null;
        }
    }

    int deletedItem =0;
    String str = "";
    private void deleteMessages() {
        final String tableName = senderInfo.get(FinalVariables.EMAIL_INDEX).replace(".","_").replace("@","_")+
                "_"+ FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +receiverInfo.get(RECEIVER_EMAIL_INDEX)
                .replace(".","_").replace("@","_");
        selectedItemPositions = allSingleChatMessagesAdapter.getSelectedItems();
        int totalItem = selectedItemPositions.size();
        deletedItem=0; str = "";

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete "+totalItem+" messages?\n"+str)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(), "Delete: "+selectedItemPositions.toString(), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < selectedItemPositions.size(); i++) {
                            boolean deleted = false;
                            int sl = selectedItemPositions.get(i);
                            deleted=dbHelper.deleteSingleChatMessage(tableName, allSingleChatMessagesSl.get(sl));
                            if(deleted){
                                allSingleChatMessagesSl.remove(sl);
                                allSingleChatMessages.remove(sl);
                                allSingleChatMessagesType.remove(sl);
                                allSingleChatMessagesDeliveryReport.remove(sl);
                                allSingleChatMessagesSeenReport.remove(sl);
                                allSingleChatMessagesDate.remove(sl);
                                deletedItem++;
                            }
                        }
                        if(deletedItem>0) {
                            allSingleChatMessagesAdapter.notifyDataSetChanged();
                            listViewAllMessages.setSelection(allSingleChatMessagesAdapter.getCount()-1);
                            Toast.makeText(getApplicationContext(), deletedItem+" item deleted", Toast.LENGTH_SHORT).show();
                        }else Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}
