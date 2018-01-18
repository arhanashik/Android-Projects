package com.project.blackspider.quarrelchat.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.project.blackspider.quarrelchat.Activities.ChatActivity;
import com.project.blackspider.quarrelchat.Activities.MainActivity;
import com.project.blackspider.quarrelchat.Activities.ProfileActivity;
import com.project.blackspider.quarrelchat.Adapter.NotificationAdapter;
import com.project.blackspider.quarrelchat.CustomView.CatLoadingView;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Interface.NotificationAdapterListener;
import com.project.blackspider.quarrelchat.Model.Notification;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CustomAnimation;
import com.project.blackspider.quarrelchat.VolleyRequests.VolleySoulRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Notifications_Fragment extends Fragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener, NotificationAdapterListener {
	private static View view, promptView, dialogView;
    private static ImageView imgNoNotification, imgProfilePic, imgChat, imgCall, imgAccount, imgClose;
    private static SimpleLoveTextView tvNoNotification, tvName, tvProfilePic;
    private static LinearLayout noNotificationContainer;
    private static CoordinatorLayout notificationContainer;
    private static RecyclerView notificationRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static CatLoadingView catLoadingView;

    private static List<Notification> notifications = new ArrayList<>();
    private static ArrayList<String> mySoulmate = new ArrayList<>();

	private static FragmentManager fragmentManager;
    private static ProgressDialog progressDialog;
    private static Intent intent;
    private static Dialog dialog;

	private static CustomAnimation mCustomAnimation;
    private static NotificationAdapter mAdapter;
    private static VolleySoulRequests volleySoulRequests;

    private Calendar calendar;

	private boolean isConnected = false;

    int sdk = android.os.Build.VERSION.SDK_INT;

	public Notifications_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_notifications, container, false);
		initViews();
		//setListeners();
		return view;
	}

    @Override
    public Animation onCreateAnimation(final int transit, boolean enter, int nextAnim) {
        int anim;
        if(enter) {
            if(MainActivity.nextTransitionDirection==MainActivity.left) anim = R.anim.left_enter;
            else anim = R.anim.right_enter;
        }
        else {
            if(MainActivity.nextTransitionDirection==MainActivity.left) anim = R.anim.right_out;
            else anim = R.anim.left_out;
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                setListeners();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return animation;
    }

    @Override
    public void onResume() {
        getNotifications();
        super.onResume();
    }

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();
        imgNoNotification = (ImageView) view.findViewById(R.id.img_no_notification);
        tvNoNotification = (SimpleLoveTextView) view.findViewById(R.id.tv_no_notification);
        noNotificationContainer = (LinearLayout) view.findViewById(R.id.no_notification_container);
        notificationContainer = (CoordinatorLayout) view.findViewById(R.id.notification_container);
        notificationRecyclerView = (RecyclerView) view.findViewById(R.id.notification_recycler_view);
        catLoadingView = new CatLoadingView();

		// Load ShakeAnimation
		mCustomAnimation = new CustomAnimation(getContext());
        volleySoulRequests = new VolleySoulRequests(getContext(), fragmentManager);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.notification_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

//        Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_notifications_black_24dp);
//        DrawableCompat.setTint(mDrawable, getContext().getResources().getColor(R.color.background_gray));
//        imgNoNotification.setImageDrawable(mDrawable);

        mAdapter = new NotificationAdapter(notifications, getContext(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(false);
        notificationRecyclerView.setLayoutManager(mLayoutManager);
        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //soulmatesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        notificationRecyclerView.setAdapter(mAdapter);
        calendar = Calendar.getInstance();
	}

	private void setCompDraw(EditText v, int img){
		Drawable mDrawable = ContextCompat.getDrawable(getContext(), img);
		int size = (int) Math.round(v.getLineHeight() * 0.9);
		mDrawable.setBounds(0, 0, size, size);
		v.setCompoundDrawables(mDrawable, null, null, null);
	}

	// Set Listeners
	private void setListeners() {
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getNotifications();
                    }
                }
        );
	}

	boolean b = true;

	@Override
	public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_no_notification:
                break;

            case R.id.img_chat:
                intent = new Intent(getContext(), ChatActivity.class);
                intent.putStringArrayListExtra("me", MainActivity.myInfo);
                intent.putStringArrayListExtra("my_soulmate", mySoulmate);
                startActivity(intent);
                break;

            case R.id.img_call:
                mCustomAnimation.revealShow(dialogView, false, dialog);
                Toast.makeText(getContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_account:
                intent = new Intent(getContext(), ProfileActivity.class);
                intent.putStringArrayListExtra("me", MainActivity.myInfo);
                intent.putStringArrayListExtra("my_soulmate", mySoulmate);
                startActivity(intent);
                break;

            case R.id.img_close:
                mCustomAnimation.revealShow(dialogView, false, dialog);
                break;

            default:
                break;
        }
	}

    private void getNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        notifications.clear();
        imgNoNotification.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Glide.with(getActivity()).load(R.drawable.loading_blue).asGif().into(imgNoNotification);
        tvNoNotification.setText("Loading . . .");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.GET_NOTIFICATIONS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = "";
                res  = response;
                Log.d("Response: ",response);
                swipeRefreshLayout.setRefreshing(false);
                imgNoNotification.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imgNoNotification.setImageResource(R.drawable.ic_warning_black_24dp);
                if(res.isEmpty()) {
                    tvNoNotification.setText("Nothing found! Something's wrong.");
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, FinalVariables.FLAG_GET_NOTIFICATIONS);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener (){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("Error: ",volleyError.toString());
                imgNoNotification.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imgNoNotification.setImageResource(R.drawable.ic_warning_black_24dp);
                tvNoNotification.setText("Something's wrong! Try again.");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", MainActivity.myInfo.get(0));

                return checkParams(params);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getSoulmateInfo(final Notification notification) {
        if (sdk < Build.VERSION_CODES.LOLLIPOP) {
            progressDialog  = new ProgressDialog(getContext());
            progressDialog.setMessage("PLEASE WAIT...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        else {
            catLoadingView.setText("PLEASE WAIT...");
            catLoadingView.show(fragmentManager, "");
            catLoadingView.setCancelable(false);
        }

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.GET_SOULMATE_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = "";
                res  = response;
                Log.d("Response: ",response);
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
                if(res.isEmpty()) {
                    Toast.makeText(getContext(), "Something's wrong! Try again.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, FinalVariables.FLAG_GET_SOULMATE_INFO);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener (){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Error: ",volleyError.toString());
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
                Toast.makeText(getContext(), "Something's wrong! Try again.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String username = notification.getFrom();
                if(username.equals(MainActivity.myInfo.get(0))) username = notification.getTo();
                params.put("username", username);

                return checkParams(params);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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

    private void showDialog() {
        promptView = View.inflate(getActivity() ,R.layout.custom_profile_dialog,null);
        dialog = new Dialog(getContext(), R.style.CustomAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(promptView);

        tvName = (SimpleLoveTextView) dialog.findViewById(R.id.tv_name);
        imgProfilePic = (ImageView) dialog.findViewById(R.id.img_profile_pic);
        tvProfilePic = (SimpleLoveTextView) dialog.findViewById(R.id.tv_profile_pic);
        imgChat = (ImageView) dialog.findViewById(R.id.img_chat);
        imgCall = (ImageView) dialog.findViewById(R.id.img_call);
        imgAccount = (ImageView) dialog.findViewById(R.id.img_account);
        imgClose = (ImageView)dialog.findViewById(R.id.img_close);
        dialogView = promptView.findViewById(R.id.dialog);

        imgChat.setOnClickListener(this);
        imgCall.setOnClickListener(this);
        imgAccount.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        tvName.setText(mySoulmate.get(1));
        tvProfilePic.setText(mySoulmate.get(1).substring(0,1));
        String imgUrl = mySoulmate.get(4);
        if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
        Glide.with(getActivity())
                .load(imgUrl)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        tvProfilePic.setVisibility(View.GONE);
                        imgProfilePic.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .centerCrop()
                .into(imgProfilePic);

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

    private String message, success;

    private void handleJSON(JSONObject jsonObject, final int flag){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;

                if(flag==FinalVariables.FLAG_GET_NOTIFICATIONS){
                    JSONArray arr = jsonObject.getJSONArray("notifications");

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject singleObject = arr.getJSONObject(i);

                        Notification notification = new Notification();
                        notification.setSl(Integer.parseInt(singleObject.getString("sl")));
                        notification.setFrom(singleObject.getString("from_soulmate"));
                        notification.setTo(singleObject.getString("to_soulmate"));
                        notification.setSoulmateName(singleObject.getString("name"));
                        notification.setMyName(MainActivity.myInfo.get(1));
                        notification.setNotification(singleObject.getString("status"));
                        notification.setTimestamp(singleObject.getString("timestamp"));
                        notification.setType(FinalVariables.SOUL_REQUEST_NOTIFICATION);
                        notification.setImagePath(singleObject.getString("image_path"));
                        if(singleObject.getString("from_soulmate").equals(MainActivity.myInfo.get(0))) {
                            notification.setFromMe(true);
                        }
                        else {
                            notification.setFromMe(false);
                        }

                        notifications.add(notification);
                    }
                    mAdapter.notifyDataSetChanged();

                    if(mAdapter.getItemCount()>0) {
                        noNotificationContainer.setVisibility(View.GONE);
                        notificationContainer.setVisibility(View.VISIBLE);
                    }else {
                        noNotificationContainer.setVisibility(View.VISIBLE);
                        notificationContainer.setVisibility(View.GONE);
                        imgNoNotification.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imgNoNotification.setImageResource(R.drawable.ic_notifications_black_24dp);
                        tvNoNotification.setText("Your notifications will apear here.");
                    }

                }else if(flag==FinalVariables.FLAG_SOUL_REQUEST_CANCEL){
                    Log.d("SQL ERROR: ",jsonObject.getString("error"));
                    //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    getNotifications();

                }else if(flag==FinalVariables.FLAG_GET_SOULMATE_INFO){
                    mySoulmate.clear();
                    mySoulmate.add(jsonObject.getString("username"));
                    mySoulmate.add(jsonObject.getString("name"));
                    mySoulmate.add(jsonObject.getString("email"));
                    mySoulmate.add(jsonObject.getString("phone"));
                    mySoulmate.add(jsonObject.getString("image_path"));
                    mySoulmate.add(jsonObject.getString("fcm_id"));
                    showDialog();

                }

            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                tvNoNotification.setText(message);
                //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            tvNoNotification.setText("Error: " + e.getMessage());
            //Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onImageIconClicked(int position) {
        getSoulmateInfo(notifications.get(position));
    }

    @Override
    public void onSoulRequestAcceptListener(int position) {
        Notification notification = notifications.get(position);
        volleySoulRequests.acceptSoulRequest(notification.getFrom(),
                notification.getTo(),
                String.valueOf(System.currentTimeMillis()),
                MainActivity.myInfo.get(FinalVariables.MY_FCM_ID_INDEX));
    }

    @Override
    public void onSoulRequestCancelListener(int position) {
        Notification notification = notifications.get(position);
        volleySoulRequests.cancelSoulRequest(notification.getFrom(),
                notification.getTo(),
                String.valueOf(System.currentTimeMillis()),
                MainActivity.myInfo.get(FinalVariables.MY_FCM_ID_INDEX));

    }


    @Override
    public void onRefresh() {
        getNotifications();
    }
}
