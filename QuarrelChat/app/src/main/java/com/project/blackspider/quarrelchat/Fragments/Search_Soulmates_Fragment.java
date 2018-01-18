package com.project.blackspider.quarrelchat.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.project.blackspider.quarrelchat.CustomView.CatLoadingView;
import com.project.blackspider.quarrelchat.CustomView.CustomToast;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Receiver.ConnectivityReceiver;
import com.project.blackspider.quarrelchat.Utils.CircleTransform;
import com.project.blackspider.quarrelchat.Utils.CustomAnimation;
import com.project.blackspider.quarrelchat.VolleyRequests.VolleySoulRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Search_Soulmates_Fragment extends Fragment implements OnClickListener {
	private static View view;
	private static ImageView imgSearch, imgItemProfilePic;
	private static SimpleLoveTextView iconText, textViewItemName;
    private static ImageView btnSendSoulRequest, btnChat, btnBlock, btnInfo;
	private static TextView tvSearch;
	private static CoordinatorLayout resContainer;
	private static EditText editTextSearchKey;
	private static ImageView btnSearch;
	private static LinearLayout searchContainer;
    private static CatLoadingView catLoadingView;

	private static FragmentManager fragmentManager;
    private static ProgressDialog progressDialog;
    private static Intent intent;

    private ArrayList<String> mySoulmate = new ArrayList<>();
    private static boolean isFriend = false;

    int sdk = android.os.Build.VERSION.SDK_INT;

	private static CustomAnimation mCustomAnimation;
    private static VolleySoulRequests volleySoulRequests;

	private boolean isConnected = false;

	public Search_Soulmates_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_search_soulmates, container, false);
		initViews();
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

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();
		editTextSearchKey = (EditText) view.findViewById(R.id.search_key);
		btnSearch = (ImageView) view.findViewById(R.id.btn_search);
		searchContainer = (LinearLayout) view.findViewById(R.id.search_soulmate_container);
		resContainer = (CoordinatorLayout) view.findViewById(R.id.res_container);
		imgSearch =(ImageView) view.findViewById(R.id.img_search_soulmate);
		tvSearch =(TextView) view.findViewById(R.id.tv_search_soulmate);
        catLoadingView = new CatLoadingView();

        imgItemProfilePic =(ImageView) view.findViewById(R.id.imageViewItemProfilePic);
        iconText = (SimpleLoveTextView) view.findViewById(R.id.icon_text);
        textViewItemName = (SimpleLoveTextView) view.findViewById(R.id.textViewItemName);
        btnSendSoulRequest = (ImageView) view.findViewById(R.id.btnSendSoulRequest);
        btnChat = (ImageView) view.findViewById(R.id.btnChat);
        btnBlock = (ImageView) view.findViewById(R.id.btnBlock);
        btnInfo = (ImageView) view.findViewById(R.id.btnInfo);

//        Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_search_soulmate_black_24dp);
//        DrawableCompat.setTint(mDrawable, getContext().getResources().getColor(R.color.background_gray));
//        imgSearch.setImageDrawable(mDrawable);

		// Load ShakeAnimation
		mCustomAnimation = new CustomAnimation(getContext());
        volleySoulRequests = new VolleySoulRequests(getContext(), fragmentManager);
	}

	// Set Listeners
	private void setListeners() {
		btnSearch.setOnClickListener(this);
        btnSendSoulRequest.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnBlock.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search:
				isConnected = ConnectivityReceiver.isConnected();
				if(isConnected) search();
				else new CustomToast().Show_Toast(getActivity(), view,
						"Connection not available.");
				break;

            case R.id.btnSendSoulRequest:
                if(!isFriend) volleySoulRequests.sendSoulRequest(MainActivity.myInfo.get(0),
                        mySoulmate.get(0),
                        String.valueOf(System.currentTimeMillis()),
                        mySoulmate.get(5));
                else new CustomToast().Show_Toast(getActivity(), view,
                        "You are already friend.");
                break;

            case R.id.btnChat:
                intent = new Intent(getContext(), ChatActivity.class);
                intent.putStringArrayListExtra("me", MainActivity.myInfo);
                intent.putStringArrayListExtra("my_soulmate", mySoulmate);
                intent.putExtra("isFriend", isFriend);
                startActivity(intent);
                break;

            case R.id.btnBlock:
				new CustomToast().Show_Toast(getActivity(), view,
                    "Coming soon...");
                break;

            case R.id.btnInfo:
                intent = new Intent(getContext(), ProfileActivity.class);
                intent.putStringArrayListExtra("me", MainActivity.myInfo);
                intent.putStringArrayListExtra("my_soulmate", mySoulmate);
                intent.putExtra("isFriend", isFriend);
                startActivity(intent);
                break;

			default:
				break;
		}

	}

	// search for soulmate
	private void search() {
		// Get email id and password
		final String getSearchKey = editTextSearchKey.getText().toString();

		// Check for both field is empty or not
		if (getSearchKey.equals("") || getSearchKey.length() == 0) {
			mCustomAnimation.shakeAnim(searchContainer);
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter search key.");

		}
		// Else do login and do your stuff
		else
        {

            if (sdk < Build.VERSION_CODES.LOLLIPOP) {
                progressDialog  = new ProgressDialog(getContext());
                progressDialog.setMessage("SEARCHING...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
            else {
                catLoadingView.setText("SEARCHING...");
                catLoadingView.show(fragmentManager, "");
                catLoadingView.setCancelable(false);
            }

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST, FinalVariables.SEARCH_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String res = "";
                    res  = response;
                    Log.d("Response: ",response);
                    if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                    else catLoadingView.dismiss();
                    if(res.isEmpty()) {
                        //Toast.makeText(getContext(), "Nothing found", Toast.LENGTH_SHORT).show();
                        tvSearch.setText("Nothing found! Search with correct key.");
                    }
                    else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            handleJSON(jsonObject, FinalVariables.FLAG_SOUL_SEARCH);
                        } catch (JSONException e) {

                        }
                    }
                }
            }, new Response.ErrorListener (){

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                    else catLoadingView.dismiss();
                    Log.d("Error: ",volleyError.toString());
                    //Toast.makeText(getContext(), "Something's wrong! Try again.", Toast.LENGTH_SHORT).show();
                    tvSearch.setText("Something's wrong! Try again.");
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("my_username", MainActivity.myInfo.get(0));
                    params.put("username", getSearchKey);
                    params.put("email", getSearchKey);
                    params.put("phone", getSearchKey);

                    return checkParams(params);
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }


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

	private String message, success;

    private void handleJSON(JSONObject jsonObject, final int flag){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;

                if(flag==FinalVariables.FLAG_SOUL_SEARCH){
                    searchContainer.setVisibility(View.GONE);
                    resContainer.setVisibility(View.VISIBLE);
                    mySoulmate.clear();
                    mySoulmate.add(jsonObject.getString("username"));
                    mySoulmate.add(jsonObject.getString("name"));
                    mySoulmate.add(jsonObject.getString("email"));
                    mySoulmate.add(jsonObject.getString("phone"));
                    mySoulmate.add(jsonObject.getString("image_path"));
                    mySoulmate.add(jsonObject.getString("fcm_id"));
                    if(mySoulmate.get(0).equals(MainActivity.myInfo.get(0))){
                        //means its me!
                        btnSendSoulRequest.setVisibility(View.GONE);
                        btnChat.setVisibility(View.GONE);
                        btnBlock.setVisibility(View.GONE);
                        btnInfo.setVisibility(View.GONE);
                    }else {
                        btnSendSoulRequest.setVisibility(View.VISIBLE);
                        btnChat.setVisibility(View.VISIBLE);
                        btnBlock.setVisibility(View.VISIBLE);
                        btnInfo.setVisibility(View.VISIBLE);
                    }
                    isFriend = jsonObject.getBoolean("is_friend");
                    textViewItemName.setText(mySoulmate.get(1));
                    String imgUrl = mySoulmate.get(4);
                    if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
                    Glide.with(getActivity())
                            .load(imgUrl)
                            .centerCrop()
                            .crossFade()
                            .transform(new CircleTransform(getActivity()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    iconText.setVisibility(View.INVISIBLE);
                                    imgItemProfilePic.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .into(imgItemProfilePic);

                }

                //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();


            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                tvSearch.setText(message);
                //Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            tvSearch.setText(e.getMessage());
            //Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
