package com.project.blackspider.quarrelchat.Activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.blackspider.quarrelchat.Adapter.MessageAdapter;
import com.project.blackspider.quarrelchat.Adapter.WallpaperAdapter;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Interface.MessageAdapterListener;
import com.project.blackspider.quarrelchat.Interface.WallpaperAdapterListener;
import com.project.blackspider.quarrelchat.Model.ChatStatus;
import com.project.blackspider.quarrelchat.Model.Message;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CustomAnimation;
import com.project.blackspider.quarrelchat.VolleyRequests.VolleySoulMessaging;
import com.project.blackspider.quarrelchat.VolleyRequests.VolleySoulRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends AppCompatActivity implements MessageAdapterListener, WallpaperAdapterListener, View.OnClickListener{
    private static ImageView imgNoChat, emojiIcon, sendMessage, imgTyping, imgClose, imgRecord, imgPlay, imgSendRecord;
    private static RecyclerView messageRecyclerView, wallpaperRecyclerView;
    private static LinearLayoutManager mLayoutManager;
    private static EmojiconEditText emojiconEditText;
    private static TextView chatName, chatStatus;
    private static Toolbar toolbar;
    private static RelativeLayout container;
    private static Chronometer chronometer;

    private static View promptView, dialogView;

    private static EmojIconActions emojIconActions;
    private static InputMethodManager imm;
    private static Dialog dialog;

    private List<Message> messages = new ArrayList<>();
    public static ArrayList<String> me = new ArrayList<>();
    public static ArrayList<String> mySoulmate = new ArrayList<>();
    public static boolean isFriend = false;
    public static boolean isAvailable = false;
    public static boolean isOnline = false;
    public static boolean isTyping = false;
    public static boolean isTextMsg = false;
    public static boolean isRecording = false;
    public static boolean isPlaying = false;
    public static int icSend = R.drawable.ic_send_black_24dp;
    public static int icMic = R.drawable.ic_mic_black_24dp;
    public static int wallpaperId;
    private static Drawable drawable;

    private static String userMsg;
    private static String myImgUrl;
    private static String soulmateImgUrl;
    private static String tableName;

    private static MessageAdapter mAdapter;
    private static WallpaperAdapter wAdapter;
    private static DBHelper dbHelper;
    private static VolleySoulRequests volleySoulRequests;
    private static VolleySoulMessaging volleySoulMessaging;
    private static CustomAnimation mCustomAnimation;

    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private BroadcastReceiver chatMessageBroadcastReceiver;
    private BroadcastReceiver chatReportBroadcastReceiver;
    private BroadcastReceiver chatStatusBroadcastReceiver;
    IntentFilter chatMessageIntentFilter;
    IntentFilter chatReportIntentFilter;
    IntentFilter chatStatusIntentFilter;
    private Vibrator vibrator;
    private Intent intent;

    private SharedPreferences sharedPreferencesMyStatus;
    private SharedPreferences sharedPreferencesChatStatus;
    private SharedPreferences sharedPreferencesWallpaper;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private static String audioFilePath = null;
    private static File audioFile;
    private static MediaRecorder mediaRecorder ;
    private static Random random ;
    private static String randAudioFileName = "ABCDEFGHIJKLMNOP";
    private static MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        setListener();
        if(isFriend) loadMessage();
        else Toast.makeText(this, "Your messages won't be saved!", Toast.LENGTH_SHORT).show();

    }

    private void init(){
        me = getIntent().getStringArrayListExtra("me");
        mySoulmate = getIntent().getStringArrayListExtra("my_soulmate");
        isFriend = getIntent().getBooleanExtra("isFriend", false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setTitle(mySoulmate.get(1))
        dbHelper = new DBHelper(this);

        container = (RelativeLayout) findViewById(R.id.messaging_container);
        imgNoChat = (ImageView) findViewById(R.id.img_no_chat);
        messageRecyclerView =(RecyclerView) findViewById(R.id.messaging_recycler_view);
        emojiIcon = (ImageView) findViewById(R.id.imageViewEmojIconKeyboard);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.editTextMessage);
        sendMessage = (ImageView) findViewById(R.id.imageViewSendMessage);
        imgTyping = (ImageView) findViewById(R.id.img_typing);

        chatName = (TextView) toolbar.findViewById(R.id.chat_name);
        chatStatus = (TextView) toolbar.findViewById(R.id.chat_status);
        chatName.setText(mySoulmate.get(1));

        sharedPreferencesMyStatus = getSharedPreferences(me.get(0)+"_chat_with_"+mySoulmate.get(0), Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferencesMyStatus.edit();
        sharedPreferencesEditor.putBoolean(FinalVariables.IS_CHATTING, true);
        sharedPreferencesEditor.commit();

        sharedPreferencesChatStatus = getSharedPreferences(mySoulmate.get(0), Context.MODE_PRIVATE);
        isAvailable = sharedPreferencesChatStatus.getBoolean(FinalVariables.IS_AVAILABLE, false);
        isOnline = sharedPreferencesChatStatus.getBoolean(FinalVariables.IS_ONLINE, false);
        isTyping = sharedPreferencesChatStatus.getBoolean(FinalVariables.IS_TYPING, false);
        if(!isAvailable) chatStatus.setVisibility(View.GONE);
        else {
            chatStatus.setVisibility(View.VISIBLE);
            chatStatus.setText("Available");
            if(isOnline) chatStatus.setText("Online");
            if(isTyping) {
                chatStatus.setText("Typing...");
                imgTyping.setVisibility(View.VISIBLE);
                Glide.with(ChatActivity.this).load(R.drawable.typing).asGif().into(imgTyping);
            }else imgTyping.setVisibility(View.INVISIBLE);
        }

        sharedPreferencesWallpaper = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_WALLPAPER, Context.MODE_PRIVATE);
        wallpaperId = sharedPreferencesWallpaper.getInt(FinalVariables.SHARED_PREFERENCES_WALLPAPER, R.drawable.default_wallpaper);
        drawable = ContextCompat.getDrawable(this, R.color.background_white);
        if(wallpaperId == 0 || wallpaperId == -1 || wallpaperId == R.drawable.default_wallpaper) container.setBackground(drawable);
        else container.setBackgroundResource(wallpaperId);

        emojIconActions = new EmojIconActions(this, messageRecyclerView, emojiconEditText, emojiIcon);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.ic_keyboard_open_black_24dp, R.drawable.ic_emoji_open_black_24dp);

        imm = (InputMethodManager)getSystemService(ChatActivity.INPUT_METHOD_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        volleySoulRequests = new VolleySoulRequests(this, getSupportFragmentManager());
        volleySoulMessaging = new VolleySoulMessaging(this, getSupportFragmentManager());

        messageRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(mLayoutManager);
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());

        myImgUrl = me.get(5);
        if(!myImgUrl.contains("http://")) myImgUrl = "http://"+ myImgUrl;

        soulmateImgUrl = mySoulmate.get(4);
        if(!soulmateImgUrl.contains("http://")) soulmateImgUrl = "http://"+ soulmateImgUrl;

        tableName = me.get(0).replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                mySoulmate.get(0).replace(".","_").replace("@","_");

        setOnline(true);

        mCustomAnimation = new CustomAnimation(this);
        random = new Random();
    }

    boolean sequence = false;
    private void setListener(){
        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence)) {
                    if(!sequence) {
                        setTyping(true);
                        sendMessage.setImageResource(icSend);
                        isTextMsg = true;
                    }
                    sequence = true;
                }else {
                    sequence = !sequence;
                    setTyping(false);
                    sendMessage.setImageResource(icMic);
                    isTextMsg = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        emojiconEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojIconActions.closeEmojIcon();
            }
        });

        sendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageViewSendMessage:
                userMsg = emojiconEditText.getText().toString();
                if(isTextMsg){
                    if(!TextUtils.isEmpty(userMsg)){
                        int sl = 1;
                        if (messages.size()>0) sl = messages.get(messages.size()-1).getSl()+1;
                        long timestamp = System.currentTimeMillis();

                        Message message = new Message();
                        message.setIndex(messages.size());
                        message.setSl(sl);
                        message.setFrom(me.get(0));
                        message.setTo(mySoulmate.get(0));
                        message.setMyName(me.get(1));
                        message.setMessage(userMsg);
                        message.setTimestamp(String.valueOf(timestamp));
                        message.setImagePath(myImgUrl);
                        message.setMyFcmId(me.get(6));
                        message.setTargetFcmId(mySoulmate.get(5));
                        message.setReport(FinalVariables.MSG_SENDING);
                        message.setType(FinalVariables.INTERNAL_TEXT_MSG);
                        if(messages.size()==0) message.setSame(false);
                        else {
                            if (messages.get(messages.size() - 1).getType() == FinalVariables.INTERNAL_TEXT_MSG)
                                message.setSame(true);
                            else message.setSame(false);
                        }
                        messages.add(message);
                        mAdapter.notifyDataSetChanged();
                        if(imgNoChat.getVisibility()==View.VISIBLE) imgNoChat.setVisibility(View.GONE);
                        if(messageRecyclerView.getBottom()>0)
                            messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
                        else
                            messageRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                        emojiconEditText.setText(null);

                        dbHelper.saveSingleChatMessage(tableName, message);
                        volleySoulMessaging.sendFCMMessage(message, isFriend, isOnline);
                    }
                }else {
                    showRecordDialog();
                }

                break;

            case R.id.img_close:
                audioFile = new File(audioFilePath);
                if (audioFile.exists()) {
                    MediaRecorderReady();
                    audioFile.delete();
                }
                mCustomAnimation.revealShow(dialogView, false, dialog);
                break;

            case R.id.img_record:
                if(!isRecording){
                    isRecording = true;
                    imgRecord.setImageResource(R.drawable.ic_stop_red_24dp);
                    if(checkPermission()) {
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        MediaRecorderReady();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        requestPermission();
                    }
                }else {
                    isRecording = false;
                    chronometer.stop();
                    imgRecord.setImageResource(R.drawable.ic_mic_black_24dp);
                    mediaRecorder.stop();

                }
                break;

            case R.id.img_media_control:
                audioFile = new File(audioFilePath);
                if (!isRecording && audioFile.exists()) {
                    if(!isPlaying ){
                        isPlaying = true;
                        imgPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(audioFilePath);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        mediaPlayer.start();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isPlaying = false;
                                imgPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                                chronometer.stop();
                                mediaPlayer.stop();
                                mediaPlayer.release();
                            }
                        });

                    }else {
                        isPlaying = false;
                        imgPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                        if(mediaPlayer != null){
                            chronometer.stop();
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                    }
                }
                break;

            case R.id.img_send:
                break;

            default:
                break;
        }

    }

    private void loadMessage(){
        messages.clear();
        messages = dbHelper.getSingleChatAllMessages(me.get(0), mySoulmate.get(0));
        mAdapter = new MessageAdapter(this, this, messages, me.get(FinalVariables.MY_IMAGE_PATH_INDEX), mySoulmate.get(4));
        messageRecyclerView.setAdapter(mAdapter);

        if(mAdapter.getItemCount()>0) {
            imgNoChat.setVisibility(View.GONE);
            messageRecyclerView.setVisibility(View.VISIBLE);
            messageRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        if(isFriend){Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_call_black_24dp);
            DrawableCompat.setTint(drawable, Color.WHITE);
            menu.getItem(0).setIcon(drawable);
            menu.getItem(0).setTitle("Call");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                if(isFriend) Toast.makeText(getApplicationContext(),  "Coming soon...", Toast.LENGTH_SHORT).show();
                else {
                    volleySoulRequests.sendSoulRequest(me.get(0),
                            mySoulmate.get(0),
                            String.valueOf(System.currentTimeMillis()),
                            mySoulmate.get(5));
                }
                break;

            case R.id.action_view_profile:
                intent = new Intent(this, ProfileActivity.class);
                intent.putStringArrayListExtra("me", me);
                intent.putStringArrayListExtra("my_soulmate", mySoulmate);
                intent.putExtra("isFriend", isFriend);
                startActivity(intent);
                break;

            case R.id.action_relation:
                Toast.makeText(getApplicationContext(),  "Coming soon...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_msg_block:
                Toast.makeText(getApplicationContext(),  "Coming soon...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_clear_chat:
                String msg = "Are you sure you want to delete all conversations?\n"
                    +"There's no way to undo.";
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setMessage(msg)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dbHelper.deleteAllSingleChatMessages(me.get(0), mySoulmate.get(0));
                                messages.clear();
                                mAdapter.notifyDataSetChanged();
                                imgNoChat.setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                mDialog = mBuilder.create();
                if(messages.size()>0)mDialog.show();
                break;

            case R.id.action_change_wallpaper:
                showWallpaperDialog();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public void onMessageLongClicked(final int position) {
        final Message message = messages.get(position);
        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Options")
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(dbHelper.deleteSingleChatMessage(me.get(0), mySoulmate.get(0), message.getSl())){
                            messages.remove(message.getIndex());
                            mAdapter.notifyDataSetChanged();
                            if(messages.size()==0) imgNoChat.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        copyToClipboard(message.getMessage());
                    }
                });
        mDialog = mBuilder.create();
        mDialog.show();
    }

    @Override
    public void onWallpaperClicked(int position) {
        wAdapter.toggleSelection(position);
        int selectedWallpaper = (int) wAdapter.getItemId(position);
        drawable = ContextCompat.getDrawable(this, R.color.background_white);
        if(selectedWallpaper == 0){
            container.setBackground(drawable);
        }
        else if(selectedWallpaper == -1){
            selectedWallpaper = wallpaperId;
            if(selectedWallpaper == 0 || selectedWallpaper == -1 || selectedWallpaper == R.drawable.default_wallpaper) container.setBackground(drawable);
            else container.setBackgroundResource(selectedWallpaper);
        }
        else {
            container.setBackgroundResource(selectedWallpaper);
        }
        sharedPreferencesEditor = sharedPreferencesWallpaper.edit();
        sharedPreferencesEditor.putInt(FinalVariables.SHARED_PREFERENCES_WALLPAPER, selectedWallpaper);
        sharedPreferencesEditor.commit();
    }

    public void copyToClipboard(String copyText) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copyText);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText(copyText, copyText);
            clipboard.setPrimaryClip(clip);
        }
        Toast toast = Toast.makeText(getApplicationContext(),
                "Text copied to clipboard", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void sendFCMMessage(final Message message, boolean isFriend) {
        final String Legacy_SERVER_KEY = FinalVariables.Legacy_SERVER_KEY;
        String msg = message.getMessage();
        int msgSl = message.getSl();
        final String from = message.getFrom();
        String name = message.getMyName();
        final String to = message.getTo();
        String timestamp = message.getTimestamp();
        String senderFcmId = FirebaseInstanceId.getInstance().getToken();//message.getMyFcmId();
        String imagePath = message.getImagePath();
        if(!imagePath.contains("http://")) imagePath = "http://"+imagePath;
        String subject = FinalVariables.SUBJECT_CHAT;
        String token = message.getTargetFcmId();
        String type = String.valueOf(message.getType());
        String isFrnd = String.valueOf(isFriend);

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("msg_sl", msgSl);
            objData.put("body", msg);
            objData.put("subject", subject);
            objData.put("from_soulmate", from);
            objData.put("name", name);
            objData.put("to_soulmate", to);
            objData.put("timestamp", timestamp);
            objData.put("image_path", imagePath);
            objData.put("sender_fcm_id", senderFcmId);
            objData.put("type", type);
            objData.put("is_friend", isFrnd);
            objData.put(FinalVariables.IS_ONLINE, isOnline);
            objData.put("priority", "high");

//            dataobjData = new JSONObject();

            obj.put("to", token);

            obj.put("notification", null);
            obj.put("data", objData);
            Log.e("FCM FORMAT:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, FinalVariables.FCM_PUSH_URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int report = FinalVariables.MSG_FAILED;
                        Log.e("FCM Response:>", response.toString());
                        try {
                            if(response.getString("success").equals("1")) {
                                if(isOnline) report = FinalVariables.MSG_SEEN;
                                else report = FinalVariables.MSG_SENT;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        message.setReport(report);
                        messages.set(message.getIndex(), message);
                        mAdapter.notifyDataSetChanged();
                        boolean updated=dbHelper.updateSingleChatMessageReport(from, to, report, message.getSl());
                        Log.e("Report updated:>", report + "");
                        if(messageRecyclerView.getBottom()>0)
                            messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
                        else
                            messageRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.setReport(FinalVariables.MSG_FAILED);
                        messages.set(message.getIndex(), message);
                        mAdapter.notifyDataSetChanged();
                        boolean updated=dbHelper.updateSingleChatMessageReport(from, to, FinalVariables.MSG_FAILED, message.getSl());
                        Log.e("Report updated:>", FinalVariables.MSG_FAILED+"");
                        if(messageRecyclerView.getBottom()>0)
                            messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
                        else
                            messageRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                        Log.e("FCM Error:>", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return checkParams(params);
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    private Map<String, String> checkParams(Map<String, String> map){
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
            if(pairs.getValue()==null){
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }

    private void showWallpaperDialog() {
        promptView = View.inflate(this ,R.layout.custom_walpaper_dialog,null);
        dialog = new Dialog(this, R.style.CustomAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(promptView);

        dialogView = promptView.findViewById(R.id.dialog);
        wallpaperRecyclerView = (RecyclerView) dialog.findViewById(R.id.wallpaper_recycler_view);

        wAdapter = new WallpaperAdapter(this, this, null);
        wallpaperRecyclerView.setHasFixedSize(true);
        wallpaperRecyclerView.setItemAnimator(new DefaultItemAnimator());
        wallpaperRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        wallpaperRecyclerView.setAdapter(wAdapter);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mCustomAnimation.revealShow(dialogView, true, dialog);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    mCustomAnimation.revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onResume() {
        setOnline(true);

        chatMessageIntentFilter = new IntentFilter("com.project.blackspider.quarrelchat.CHAT_INTENT");
        chatReportIntentFilter = new IntentFilter("com.project.blackspider.quarrelchat.REPORT_INTENT");
        chatStatusIntentFilter = new IntentFilter("com.project.blackspider.quarrelchat.STATUS_INTENT");

        chatMessageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long[] pattern = {0, 100, 1000};
                //extract our message from intent
                String msg_for_me = intent.getStringExtra("message");
                String msgFrom = intent.getStringExtra("senderUsername");
                String timestamp = intent.getStringExtra("timestamp");
                boolean isFriend = intent.getBooleanExtra("isFriend", false);
                int intentFlag = intent.getIntExtra("flag", FinalVariables.MSG_RECEIVED);

                Message message = new Message();

                //log our message value
                Log.i("Broadcast: ", msg_for_me);

                if(intentFlag==FinalVariables.MSG_RECEIVED){
                    vibrator.vibrate(pattern, -1);
                    int msgSl = 1;
                    boolean isSame = false;
                    if(messages.size()>0){
                        msgSl=messages.get(messages.size()-1).getSl()+1;
                        if (messages.get(messages.size()-1).getType() == FinalVariables.EXTERNAL_TEXT_MSG)
                            isSame = true;
                    }

                    message.setIndex(messages.size());
                    message.setSl(msgSl);
                    message.setFrom(msgFrom);
                    message.setTo(me.get(0));
                    message.setMessage(msg_for_me);
                    message.setTimestamp(timestamp);
                    message.setImagePath(soulmateImgUrl);
                    message.setReport(FinalVariables.MSG_RECEIVED);
                    message.setType(FinalVariables.EXTERNAL_TEXT_MSG);
                    message.setSame(isSame);

                    if(isFriend){

                    }
                }

                if(message!=null) messages.add(message);
                mAdapter.notifyDataSetChanged();
                if(imgNoChat.getVisibility()==View.VISIBLE) imgNoChat.setVisibility(View.GONE);
                if(messageRecyclerView.getBottom()>0)
                    messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
                else
                    messageRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
            }
        };

        chatReportBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String reportFrom = intent.getStringExtra("senderUsername");
                String timestamp = intent.getStringExtra("timestamp");
                //boolean isFriend = intent.getBooleanExtra("isFriend", false);
                int msgSl = intent.getIntExtra("msg_sl", 0);
                int msgReport = intent.getIntExtra("msg_report", 2);

                if(reportFrom.equals(mySoulmate.get(0)) && msgSl>0){
                    for (int i = 0; i<messages.size(); i++){
                        Message message = messages.get(i);
                        if(message.getSl()==msgSl){
                            if(msgReport>message.getReport()){
                                message.setReport(msgReport);
                                messages.set(i, message);
                                mAdapter.notifyDataSetChanged();
                                if(messageRecyclerView.getBottom()>0)
                                    messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
                                else messageRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                            }
                            break;
                        }
                    }
                }
            }
        };

        chatStatusBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String statusFrom = intent.getStringExtra("senderUsername");
                String timestamp = intent.getStringExtra("timestamp");
                //boolean isFriend = intent.getBooleanExtra("isFriend", false);
                isAvailable = intent.getBooleanExtra(FinalVariables.IS_AVAILABLE, false);
                isOnline = intent.getBooleanExtra(FinalVariables.IS_ONLINE, false);
                isTyping = intent.getBooleanExtra(FinalVariables.IS_TYPING, false);

                if(statusFrom.equals(mySoulmate.get(0))){
                    if(!isAvailable) chatStatus.setVisibility(View.GONE);
                    else {
                        chatStatus.setVisibility(View.VISIBLE);
                        chatStatus.setText("Available");
                        if(isOnline) {
                            chatStatus.setText("Online");
                            for (int i = 0; i<messages.size(); i++){
                                Message message = messages.get(i);
                                if(message.getReport()==FinalVariables.MSG_SENT || message.getReport()==FinalVariables.MSG_DELIVERED ){
                                    message.setReport(FinalVariables.MSG_SEEN);
                                    messages.set(i, message);
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            if(messageRecyclerView.getBottom()>0)
                                messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
                            else messageRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                        }
                        if(isTyping) {
                            chatStatus.setText("Typing...");
                            imgTyping.setVisibility(View.VISIBLE);
                            Glide.with(ChatActivity.this).load(R.drawable.typing).asGif().into(imgTyping);
                        }else imgTyping.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
        //registering our receiver
        this.registerReceiver(chatMessageBroadcastReceiver, chatMessageIntentFilter);
        this.registerReceiver(chatReportBroadcastReceiver, chatReportIntentFilter);
        this.registerReceiver(chatStatusBroadcastReceiver, chatStatusIntentFilter);

        sharedPreferencesEditor = sharedPreferencesMyStatus.edit();
        sharedPreferencesEditor.putBoolean(FinalVariables.IS_CHATTING,  true);
        sharedPreferencesEditor.commit();
        super.onResume();
    }

    @Override
    protected void onPause() {
        setOnline(false);
        this.unregisterReceiver(chatMessageBroadcastReceiver);
        this.unregisterReceiver(chatReportBroadcastReceiver);
        this.unregisterReceiver(chatStatusBroadcastReceiver);

        sharedPreferencesEditor = sharedPreferencesMyStatus.edit();
        sharedPreferencesEditor.putBoolean(FinalVariables.IS_CHATTING,  false);
        sharedPreferencesEditor.commit();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        setOnline(false);

        sharedPreferencesEditor = sharedPreferencesMyStatus.edit();
        sharedPreferencesEditor.putBoolean(FinalVariables.IS_ONLINE,  false);
        sharedPreferencesEditor.commit();
        super.onDestroy();
    }

    private void setOnline(boolean isOnline){
        ChatStatus chatStatus = new ChatStatus();
        chatStatus.setFrom(me.get(0));
        chatStatus.setTo(mySoulmate.get(0));
        chatStatus.setMyName(me.get(1));
        chatStatus.setTimestamp(String.valueOf(System.currentTimeMillis()));
        chatStatus.setMyFcmId(me.get(FinalVariables.MY_FCM_ID_INDEX));
        chatStatus.setTargetFcmId(mySoulmate.get(5));
        chatStatus.setFriend(isFriend);
        chatStatus.setAvailable(true);
        chatStatus.setOnline(isOnline);
        chatStatus.setTyping(false);
        volleySoulMessaging.sendChatStatus(chatStatus);
    }

    private void setTyping(boolean isTyping){
        ChatStatus chatStatus = new ChatStatus();
        chatStatus.setFrom(me.get(0));
        chatStatus.setTo(mySoulmate.get(0));
        chatStatus.setMyName(me.get(1));
        chatStatus.setTimestamp(String.valueOf(System.currentTimeMillis()));
        chatStatus.setMyFcmId(me.get(FinalVariables.MY_FCM_ID_INDEX));
        chatStatus.setTargetFcmId(mySoulmate.get(5));
        chatStatus.setFriend(isFriend);
        chatStatus.setAvailable(true);
        chatStatus.setOnline(true);
        chatStatus.setTyping(isTyping);
        volleySoulMessaging.sendChatStatus(chatStatus);
    }

    private void showRecordDialog(){
        promptView = View.inflate(this ,R.layout.prompts_view_record_area, null);
        dialog = new Dialog(this, R.style.CustomAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(promptView);

        dialogView = promptView.findViewById(R.id.dialog);
        chronometer = (Chronometer) promptView.findViewById(R.id.chronometer);
        imgClose = (ImageView) promptView.findViewById(R.id.img_close);
        imgRecord = (ImageView) promptView.findViewById(R.id.img_record);
        imgPlay = (ImageView) promptView.findViewById(R.id.img_media_control);
        imgSendRecord = (ImageView) promptView.findViewById(R.id.img_send);

        imgClose.setOnClickListener(this);
        imgRecord.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgSendRecord.setOnClickListener(this);

        audioFilePath =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        CreateRandomAudioFileName(5) +"_"+me.get(0)+ "_recording.3gp";

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {

            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mCustomAnimation.revealShow(dialogView, true, dialog);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    audioFile = new File(audioFilePath);
                    if (audioFile.exists()) {
                        MediaRecorderReady();
                        audioFile.delete();
                    }
                    mCustomAnimation.revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, FinalVariables.MEDIA_ACCESS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FinalVariables.MEDIA_ACCESS_PERMISSION_REQUEST_CODE:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(ChatActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ChatActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        requestPermission();
                    }
                }
                break;
        }
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(randAudioFileName.
                    charAt(random.nextInt(randAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString().toLowerCase();
    }
}
