package com.project.blackspider.quarrelchat.Fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.blackspider.quarrelchat.Activities.MainActivity;
import com.project.blackspider.quarrelchat.CustomView.CatLoadingView;
import com.project.blackspider.quarrelchat.CustomView.CustomToast;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FCM.UpdateFcmId;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Model.Soulmate;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Receiver.ConnectivityReceiver;
import com.project.blackspider.quarrelchat.Utils.CustomAnimation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login_Fragment extends Fragment implements OnClickListener {
	private static View view;

	private static EditText emailid, password;
	private static Button loginButton;
	private static TextView forgotPassword, signUp;
	private static CheckBox show_hide_password;
	private static LinearLayout loginLayout;
    private static CatLoadingView catLoadingView;

	private static FragmentManager fragmentManager;
    private static ProgressDialog progressDialog;

	private static TextWatcher textWatcher;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

	private static DBHelper dbHelper;
    private static CustomAnimation mCustomAnimation;

	private boolean isConnected = false;

    int sdk = android.os.Build.VERSION.SDK_INT;

    private static ArrayList<String> myInfo = new ArrayList<>();
    private static Soulmate soulmate;

    private final int left=0, right=1;

	public Login_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_login, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();
        dbHelper = new DBHelper(getContext());

		emailid = (EditText) view.findViewById(R.id.login_emailid);
		password = (EditText) view.findViewById(R.id.login_password);
		loginButton = (Button) view.findViewById(R.id.loginBtn);
		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
		signUp = (TextView) view.findViewById(R.id.createAccount);
		show_hide_password = (CheckBox) view
				.findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);
        catLoadingView = new CatLoadingView();

		// Load ShakeAnimation
		mCustomAnimation = new CustomAnimation(getContext());
        sharedPreferences = getActivity().getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);

        setCompDraw(emailid, R.drawable.user, left);
        setCompDraw(password, R.drawable.password, left);

        //checkSessionValidity();
	}

	private void setCompDraw(EditText v, int img, int side){
        Drawable mDrawable = ContextCompat.getDrawable(getContext(), img);
        int size = (int) Math.round(v.getLineHeight() * 0.9);
        mDrawable.setBounds(0, 0, size, size);
        if(side==left) v.setCompoundDrawables(mDrawable, null, null, null);
        if(side==right) v.setCompoundDrawables(null, null, mDrawable, null);
    }

	private void setCompDrawErr(EditText v, int imgLeft, int imgRight){
		Drawable mDrawableLeft = ContextCompat.getDrawable(getContext(), imgLeft);
		Drawable mDrawableRight = ContextCompat.getDrawable(getContext(), imgRight);
		int size = (int) Math.round(v.getLineHeight() * 0.9);
		mDrawableLeft.setBounds(0, 0, size, size);
		mDrawableRight.setBounds(0, 0, size, size);
		DrawableCompat.setTint(mDrawableRight, ContextCompat.getColor(getContext(), R.color.colorAccent));
		v.setCompoundDrawables(mDrawableLeft, null, mDrawableRight, null);
	}

    private void checkSessionValidity(){
        if(sharedPreferences.getBoolean(FinalVariables.IS_SESSION_EXIST, false)){
            soulmate = new Soulmate();
            soulmate = dbHelper.getUserInfo();

            UpdateFcmId ufi = new UpdateFcmId(getContext(), soulmate.getUsername());
            ufi.update(FirebaseInstanceId.getInstance().getToken(), FinalVariables.UPDATE_FCM_ID_URL);

            myInfo.clear();
            myInfo.add(soulmate.getUsername());
            myInfo.add(soulmate.getName());
            myInfo.add(soulmate.getEmail());
            myInfo.add(soulmate.getPhone());
            myInfo.add(soulmate.getPassword());
            myInfo.add(soulmate.getImagePath());
            myInfo.add(soulmate.getFcmId());
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putStringArrayListExtra("my_info", myInfo);
            new CustomToast().Show_Toast(getContext(), view, "Welcome back "+soulmate.getName());
            startActivity(intent);

        }
    }

	// Set Listeners
	private void setListeners() {
		loginButton.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);
		signUp.setOnClickListener(this);

		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {

							show_hide_password.setText(R.string.hide_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod
									.getInstance());// hide password

						}

					}
				});

		textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if(isValidUsername(charSequence.toString()) || TextUtils.isEmpty(charSequence))
					setCompDraw(emailid, R.drawable.user, left);
				else {
                    if (sdk < Build.VERSION_CODES.LOLLIPOP) {
                        //
                    }
                    else setCompDrawErr(emailid, R.drawable.user, R.drawable.ic_warning_black_24dp);
                }
			}

			@Override
			public void afterTextChanged(Editable editable) {
				//tvSearch.setText("Show search result for "+editable);
			}
		};
		emailid.addTextChangedListener(textWatcher);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			isConnected = ConnectivityReceiver.isConnected();
			if(isConnected) checkValidation();
			else new CustomToast().Show_Toast(getActivity(), view,
					"Connection not available.");
			break;

		case R.id.forgot_password:

			// Replace forgot password fragment with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.left_enter, R.anim.zoom_out)
					.replace(R.id.frameContainer,
							new ForgotPassword_Fragment(),
							FinalVariables.ForgotPassword_Fragment).commit();
			break;
		case R.id.createAccount:

			// Replace signup frgament with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.bottom_enter, R.anim.zoom_out)
					.replace(R.id.frameContainer, new SignUp_Fragment(),
							FinalVariables.SignUp_Fragment).commit();
			break;
		}

	}

	// Check Validation before login
	private void checkValidation() {
		// Get email id and password
		final String getEmailId = emailid.getText().toString();
		final String getPassword = password.getText().toString();

		// Check for both field is empty or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0) {
			mCustomAnimation.shakeAnim(loginLayout);
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter both credentials.");

		}
		// Check if email id is valid or not
		else if (!isValidUsername(getEmailId))
        {
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Username is Invalid.");
            //setCompDraw(emailid, R.drawable.error, right);
        }
		// Else do login and do your stuff
		else
        {
			checkOnServer(getEmailId, getPassword);
        }

	}

	public static boolean isValidEmail(String email){
		// Check patter for email id
		Pattern p = Pattern.compile(FinalVariables.EmailRegEx);
		Matcher m = p.matcher(email);
		return m.find();
	}

    public static boolean isValidUsername(String username){
        // Check patter for email id
        Pattern p = Pattern.compile(FinalVariables.UsernameRegEx);
        Matcher m = p.matcher(username);
        return m.matches();
    }

    public void checkOnServer(final String username, final String password){
        if (sdk < Build.VERSION_CODES.LOLLIPOP) {
            progressDialog  = new ProgressDialog(getContext());
            progressDialog.setMessage("Signing in...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        else {
            catLoadingView.setText("SIGNING IN...");
            catLoadingView.show(fragmentManager, "");
            catLoadingView.setCancelable(false);
        }


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.LOG_IN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loginButton.setActivated(true);
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
                if (response.isEmpty()) {
                    new CustomToast().Show_Toast(getContext(), view, "Nothing found! Try again.");
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                loginButton.setActivated(true);
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
                Log.d("Error: ",volleyError.toString());
                new CustomToast().Show_Toast(getContext(), view, "Something's wrong! Try again.");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", username);
                params.put("password", password);
                params.put("fcm_id", FirebaseInstanceId.getInstance().getToken());

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

    private String message, success;

    private void handleJSON(JSONObject jsonObject){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;

                soulmate = new Soulmate();
                soulmate.setUsername(jsonObject.getString("username"));
                soulmate.setName(jsonObject.getString("name"));
                soulmate.setEmail(jsonObject.getString("email"));
                soulmate.setPhone(jsonObject.getString("phone"));
                soulmate.setPassword(jsonObject.getString("password"));
                soulmate.setImagePath(jsonObject.getString("image_path"));
                soulmate.setFcmId(jsonObject.getString("fcm_id"));
                dbHelper.saveUserInfo(soulmate);
                dbHelper.saveLastSigninInfo(soulmate);

                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, true);
                sharedPreferencesEditor.commit();

                myInfo.clear();
                myInfo.add(soulmate.getUsername());
                myInfo.add(soulmate.getName());
                myInfo.add(soulmate.getEmail());
                myInfo.add(soulmate.getPhone());
                myInfo.add(soulmate.getPassword());
                myInfo.add(soulmate.getImagePath());
                myInfo.add(soulmate.getFcmId());
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putStringArrayListExtra("my_info", myInfo);
                new CustomToast().Show_Toast(getContext(), view, message);
                startActivity(intent);


            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                new CustomToast().Show_Toast(getContext(), view, message);
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
			new CustomToast().Show_Toast(getContext(), view, "Something's wrong! Try again.");
        }
    }
}
