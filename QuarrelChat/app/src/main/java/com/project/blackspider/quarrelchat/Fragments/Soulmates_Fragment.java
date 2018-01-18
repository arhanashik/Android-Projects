package com.project.blackspider.quarrelchat.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.project.blackspider.quarrelchat.Activities.GreetingsActivity;
import com.project.blackspider.quarrelchat.Activities.MainActivity;
import com.project.blackspider.quarrelchat.Activities.ProfileActivity;
import com.project.blackspider.quarrelchat.Adapter.SoulmateAdapter;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Interface.SoulmateAdapterListener;
import com.project.blackspider.quarrelchat.Model.Soulmate;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CustomAnimation;
import com.project.blackspider.quarrelchat.Utils.GridSpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Soulmates_Fragment extends Fragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener, SoulmateAdapterListener {
	private static View view, promptView, dialogView;
	private static ImageView imgNoSoulmate, imgProfilePic, imgChat, imgCall, imgAccount, imgClose;
	private static SimpleLoveTextView tvNoSoulmate, tvName, tvProfilePic;
	private static LinearLayout noSoulmateContainer;
	private static CoordinatorLayout soulmateContainer;
	private static RecyclerView soulmatesRecyclerView;
	private static SwipeRefreshLayout swipeRefreshLayout;

	private static GridLayoutManager mGridLayoutManager;
	private static LinearLayoutManager mLinearLayoutManager;
	private static GridSpacingItemDecoration gridSpacingItemDecoration;

	private static FragmentManager fragmentManager;
	private static Dialog dialog;
	private static Intent intent;

	private static CustomAnimation mCustomAnimation;

	private static SharedPreferences sharedPreferencesLayoutType;
	private static SharedPreferences.Editor sharedPreferencesEditor;
	private static int layoutType = FinalVariables.TYPE_LINEAR_LAYOUT;

	private boolean isConnected = false;
	private boolean isRefresh = false;

	private List<Soulmate> soulmates = new ArrayList<>();
	private ArrayList<String> mySoulmate = new ArrayList<>();
	private static SoulmateAdapter mAdapter;

	public Soulmates_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_soulmates, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
		initViews();
	}

    @Override
    public Animation onCreateAnimation(final int transit, boolean enter, int nextAnim) {
        int anim;
        if(enter) anim = R.anim.left_enter;
        else anim = R.anim.right_out;
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
		getSoulmates();
		super.onResume();
	}

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();
		imgNoSoulmate = (ImageView) view.findViewById(R.id.img_no_soulmate);
		tvNoSoulmate = (SimpleLoveTextView) view.findViewById(R.id.tv_no_soulmate);
		noSoulmateContainer = (LinearLayout) view.findViewById(R.id.no_soulmate_container);
		soulmateContainer = (CoordinatorLayout) view.findViewById(R.id.soulmate_container);
		soulmatesRecyclerView = (RecyclerView) view.findViewById(R.id.soulmates_recycler_view);
		// Load ShakeAnimation
		mCustomAnimation = new CustomAnimation(getContext());

		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(this);

		sharedPreferencesLayoutType = getContext().getSharedPreferences(FinalVariables.SHARED_PREFERENCES_LAYOUT_TYPE,
				Context.MODE_PRIVATE);
		layoutType = sharedPreferencesLayoutType.getInt(FinalVariables.SHARED_PREFERENCES_LAYOUT_TYPE,
				FinalVariables.TYPE_LINEAR_LAYOUT);
		sharedPreferencesEditor = sharedPreferencesLayoutType.edit();

		mLinearLayoutManager = new LinearLayoutManager(getActivity());
		mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
		gridSpacingItemDecoration = new GridSpacingItemDecoration(2, 15, true);

		mAdapter = new SoulmateAdapter(getContext(), soulmates, MainActivity.myInfo.get(0), this);
		soulmatesRecyclerView.setItemAnimator(new DefaultItemAnimator());
		soulmatesRecyclerView.setAdapter(mAdapter);
	}

	// Set Listeners
	private void setListeners() {
		imgNoSoulmate.setOnClickListener(this);

		// show loader and fetch messages
		swipeRefreshLayout.post(
				new Runnable() {
					@Override
					public void run() {
                        getSoulmates();
					}
				}
		);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.img_no_soulmate:
				//startActivity(new Intent(getContext(), ProfileActivity.class));
				MainActivity.navigation.setSelectedItemId(MainActivity.navigationMenu.getItem(1).getItemId());
				break;

			case R.id.img_chat:
				intent = new Intent(getContext(), ChatActivity.class);
				intent.putStringArrayListExtra("me", MainActivity.myInfo);
				intent.putStringArrayListExtra("my_soulmate", mySoulmate);
				intent.putExtra("isFriend", true);
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
				intent.putExtra("isFriend", true);
				startActivity(intent);
				break;

			case R.id.img_close:
				mCustomAnimation.revealShow(dialogView, false, dialog);
				break;

			default:
				break;
		}
	}

	public static void setListView(){
		if(mAdapter.getLayoutType()!=FinalVariables.TYPE_LINEAR_LAYOUT){
			sharedPreferencesEditor.putInt(FinalVariables.SHARED_PREFERENCES_LAYOUT_TYPE, FinalVariables.TYPE_LINEAR_LAYOUT);
			sharedPreferencesEditor.commit();
			mAdapter.setLayoutType(FinalVariables.TYPE_LINEAR_LAYOUT);
			soulmatesRecyclerView.removeItemDecoration(gridSpacingItemDecoration);
			soulmatesRecyclerView.setLayoutManager(mLinearLayoutManager);
			mAdapter.notifyDataSetChanged();
		}
	}

	public static void setGridView(){
		if(mAdapter.getLayoutType()!=FinalVariables.TYPE_GRID_LAYOUT){
			sharedPreferencesEditor.putInt(FinalVariables.SHARED_PREFERENCES_LAYOUT_TYPE, FinalVariables.TYPE_GRID_LAYOUT);
			sharedPreferencesEditor.commit();
			mAdapter.setLayoutType(FinalVariables.TYPE_GRID_LAYOUT);
			soulmatesRecyclerView.removeItemDecoration(gridSpacingItemDecoration);
			soulmatesRecyclerView.setLayoutManager(mGridLayoutManager);
			soulmatesRecyclerView.addItemDecoration(gridSpacingItemDecoration);
			mAdapter.notifyDataSetChanged();
		}
	}

	public static void setExtendedListView(){
		if(mAdapter.getLayoutType()!=FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT){
			sharedPreferencesEditor.putInt(FinalVariables.SHARED_PREFERENCES_LAYOUT_TYPE, FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT);
			sharedPreferencesEditor.commit();
			mAdapter.setLayoutType(FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT);
			soulmatesRecyclerView.removeItemDecoration(gridSpacingItemDecoration);
			soulmatesRecyclerView.setLayoutManager(mLinearLayoutManager);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void getSoulmates() {
		swipeRefreshLayout.setRefreshing(true);
        soulmates.clear();
		imgNoSoulmate.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imgNoSoulmate.setEnabled(false);
		Glide.with(getActivity()).load(R.drawable.loading_blue).asGif().into(imgNoSoulmate);
		tvNoSoulmate.setText("Loading . . .");

		StringRequest stringRequest = new StringRequest(
				Request.Method.POST, FinalVariables.GET_SOULMATES_URL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String res = "";
				res  = response;
				Log.d("Response: ",response);
				swipeRefreshLayout.setRefreshing(false);
				imgNoSoulmate.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imgNoSoulmate.setImageResource(R.drawable.ic_warning_black_24dp);
				if(res.isEmpty()) {
					tvNoSoulmate.setText("Nothing found! Please refresh.");
				}
				else {
					try {
						JSONObject jsonObject = new JSONObject(response);
						handleJSON(jsonObject, FinalVariables.FLAG_GET_SOULMATES);
					} catch (JSONException e) {

					}
				}
			}
		}, new Response.ErrorListener (){

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Log.d("Error: ",volleyError.toString());
				swipeRefreshLayout.setRefreshing(false);
				imgNoSoulmate.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imgNoSoulmate.setImageResource(R.drawable.ic_warning_black_24dp);
				tvNoSoulmate.setText("Something's wrong! Please try again.");
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

	private void showDialog(Soulmate soulmate) {
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

		mySoulmate.clear();
		mySoulmate.add(soulmate.getUsername());
		mySoulmate.add(soulmate.getName());
		mySoulmate.add(soulmate.getEmail());
		mySoulmate.add(soulmate.getPhone());
		mySoulmate.add(soulmate.getImagePath());
		mySoulmate.add(soulmate.getFcmId());

		tvName.setText(soulmate.getName());
		tvProfilePic.setText(soulmate.getName().substring(0,1));
		String imgUrl = soulmate.getImagePath();
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

	private int getRandomMaterialColor(String typeColor) {
		int returnColor = Color.GRAY;
		int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getContext().getPackageName());

		if (arrayId != 0) {
			TypedArray colors = getResources().obtainTypedArray(arrayId);
			int index = (int) (Math.random() * colors.length());
			returnColor = colors.getColor(index, Color.GRAY);
			colors.recycle();
		}
		return returnColor;
	}

	private String message, success;

	private void handleJSON(JSONObject jsonObject, final int flag){

		try{
			String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

			if (what.equals(FinalVariables.SUCCESS)) { //means valid id
				message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
				success = FinalVariables.SUCCESS;

				if(flag==FinalVariables.FLAG_GET_SOULMATES){
					JSONArray arr = jsonObject.getJSONArray("soulmates");

					for (int i = 0; i < arr.length(); i++) {
						JSONObject singleObject = arr.getJSONObject(i);

						Soulmate soulmate = new Soulmate();
						soulmate.setSl(Integer.parseInt(singleObject.getString("sl")));
						soulmate.setUsername(singleObject.getString("username"));
						soulmate.setName(singleObject.getString("name"));
						soulmate.setEmail(singleObject.getString("email"));
						soulmate.setPhone(singleObject.getString("phone"));
						soulmate.setImagePath(singleObject.getString("image_path"));
						soulmate.setFcmId(singleObject.getString("fcm_id"));

						soulmates.add(soulmate);
					}
					swipeRefreshLayout.setRefreshing(false);
					mAdapter.notifyDataSetChanged();

					if(mAdapter.getItemCount()>0) {
						noSoulmateContainer.setVisibility(View.GONE);
						soulmateContainer.setVisibility(View.VISIBLE);
					}else {
						noSoulmateContainer.setVisibility(View.VISIBLE);
						soulmateContainer.setVisibility(View.GONE);
						imgNoSoulmate.setScaleType(ImageView.ScaleType.FIT_CENTER);
						imgNoSoulmate.setImageResource(R.drawable.ic_add_soulmate_black_24dp);
						imgNoSoulmate.setEnabled(true);
						tvNoSoulmate.setText("Add soulmates to start chatting.");
					}
					layoutType = sharedPreferencesLayoutType.getInt(FinalVariables.SHARED_PREFERENCES_LAYOUT_TYPE,
							FinalVariables.TYPE_LINEAR_LAYOUT);
					setListView();
					//if(layoutType==FinalVariables.TYPE_LINEAR_LAYOUT) setListView();
					if(layoutType==FinalVariables.TYPE_GRID_LAYOUT) setGridView();
					else if(layoutType==FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT)setExtendedListView();
				}else{
					Log.d("Response: ",message);
					//Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
				}

			}else { //means error
				message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
				success = FinalVariables.FAILURE;
				tvNoSoulmate.setText(message);
				Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
			}
			Log.d("Message: ",message);
			Log.d("Success: ",success);

		} catch (JSONException e) {
			e.printStackTrace();
			tvNoSoulmate.setText("Error: " + e.getMessage());
			//Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onProfilePicClicked(int position) {
		showDialog(soulmates.get(position));
	}

	@Override
	public void onSoulmateRowClicked(int position) {
		Soulmate soulmate = soulmates.get(position);
		soulmate.setRead(true);
		mySoulmate.clear();
        mySoulmate.add(soulmate.getUsername());
        mySoulmate.add(soulmate.getName());
        mySoulmate.add(soulmate.getEmail());
        mySoulmate.add(soulmate.getPhone());
        mySoulmate.add(soulmate.getImagePath());
        mySoulmate.add(soulmate.getFcmId());
		Intent intent = new Intent(getContext(), ChatActivity.class);
		intent.putStringArrayListExtra("me", MainActivity.myInfo);
		intent.putStringArrayListExtra("my_soulmate", mySoulmate);
		intent.putExtra("isFriend", true);
        startActivity(intent);
	}

	@Override
	public void onSoulmateRowLongClicked(int position) {
		//Toast.makeText(getContext(), "Do something", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSoulmateChatClicked(int position) {
		Soulmate soulmate = soulmates.get(position);
		soulmate.setRead(true);
		mySoulmate.clear();
		mySoulmate.add(soulmate.getUsername());
		mySoulmate.add(soulmate.getName());
		mySoulmate.add(soulmate.getEmail());
		mySoulmate.add(soulmate.getPhone());
		mySoulmate.add(soulmate.getImagePath());
		mySoulmate.add(soulmate.getFcmId());
		intent = new Intent(getContext(), ChatActivity.class);
		intent.putStringArrayListExtra("me", MainActivity.myInfo);
		intent.putStringArrayListExtra("my_soulmate", mySoulmate);
		intent.putExtra("isFriend", true);
		startActivity(intent);
	}

	@Override
	public void onSoulmateCallClicked(int position) {
		Toast.makeText(getContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSoulmateProfileClicked(int position) {
		Soulmate soulmate = soulmates.get(position);
		soulmate.setRead(true);
		mySoulmate.clear();
		mySoulmate.add(soulmate.getUsername());
		mySoulmate.add(soulmate.getName());
		mySoulmate.add(soulmate.getEmail());
		mySoulmate.add(soulmate.getPhone());
		mySoulmate.add(soulmate.getImagePath());
		mySoulmate.add(soulmate.getFcmId());
		intent = new Intent(getContext(), ProfileActivity.class);
		intent.putStringArrayListExtra("me", MainActivity.myInfo);
		intent.putStringArrayListExtra("my_soulmate", mySoulmate);
		intent.putExtra("isFriend", true);
		startActivity(intent);
	}


	@Override
	public void onRefresh() {
		getSoulmates();
	}
}
