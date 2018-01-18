package com.project.blackspider.quarrelchat.Fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import com.project.blackspider.quarrelchat.Activities.WelcomeActivity;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Model.Soulmate;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Receiver.ConnectivityReceiver;

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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp_Fragment extends Fragment implements OnClickListener {
	private static View view, usernameDivider;
	private static EditText fullName, emailId, mobileNumber, username,
			password, confirmPassword;
	private static TextView login;
	private static Button signUpButton, fbSignUpButton;
	private static CheckBox terms_conditions;
	private static CatLoadingView catLoadingView;

	private static TextWatcher textWatcher;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPreferencesEditor;

	private static FragmentManager fragmentManager;
	private static ProgressDialog progressDialog;

	private static DBHelper dbHelper;

	private boolean isConnected = false;

	int sdk = android.os.Build.VERSION.SDK_INT;

	private static String strUsername;
	private static String strName;
	private static String strEmail;
	private static String strPhone;
	private static String strPassword;
	private static String strConfirmPassword;
	private static String strFcmId;
	private static String strImgName;

	public SignUp_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_signup, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize all views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();
		dbHelper = new DBHelper(getContext());

		fullName = (EditText) view.findViewById(R.id.fullName);
		emailId = (EditText) view.findViewById(R.id.userEmailId);
		mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
		username = (EditText) view.findViewById(R.id.userName);
		password = (EditText) view.findViewById(R.id.password);
		confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		fbSignUpButton = (Button) view.findViewById(R.id.fbSignUpBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
		catLoadingView = new CatLoadingView();

		usernameDivider = (View) view.findViewById(R.id.username_divider);
		sharedPreferences = getActivity().getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);

		setCompDraw(fullName, R.drawable.user);
		setCompDraw(username, R.drawable.user);
		setCompDraw(emailId, R.drawable.email);
		setCompDraw(mobileNumber, R.drawable.phone);
		setCompDraw(password, R.drawable.password);
		setCompDraw(confirmPassword, R.drawable.confirm_password);

	}

	private void setCompDraw(EditText v, int img){
		Drawable mDrawable = ContextCompat.getDrawable(getContext(), img);
		int size = (int) Math.round(v.getLineHeight() * 0.9);
		mDrawable.setBounds(0, 0, size, size);
		v.setCompoundDrawables(mDrawable, null, null, null);
	}

	private void setCompDrawErr(EditText v, int imgLeft, int imgRight){
		Drawable mDrawableLeft = ContextCompat.getDrawable(getContext(), imgLeft);
		Drawable mDrawableRight = ContextCompat.getDrawable(getContext(), imgRight);
		int size = (int) Math.round(v.getLineHeight() * 0.9);
		mDrawableLeft.setBounds(0, 0, size, size);
		mDrawableRight.setBounds(0, 0, size, size);
		DrawableCompat.setTint(mDrawableRight, ContextCompat.getColor(getContext(), R.color.red));
		v.setCompoundDrawables(mDrawableLeft, null, mDrawableRight, null);
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		fbSignUpButton.setOnClickListener(this);
		login.setOnClickListener(this);



		textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(Login_Fragment.isValidEmail(charSequence.toString()) || TextUtils.isEmpty(charSequence))
                    setCompDraw(emailId, R.drawable.email);
				else {
					if (sdk < Build.VERSION_CODES.LOLLIPOP) {
						//
					}
					else setCompDrawErr(emailId, R.drawable.user, R.drawable.ic_warning_black_24dp);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
				//tvSearch.setText("Show search result for "+editable);
			}
		};
		emailId.addTextChangedListener(textWatcher);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.signUpBtn:
                isConnected = ConnectivityReceiver.isConnected();
                if(isConnected) checkValidation();
                else new CustomToast().Show_Toast(getActivity(), view,
                        "Connection not available.");
                // Call checkValidation method
                //checkValidation();
                break;

            case R.id.fbSignUpBtn:

                break;

            case R.id.already_user:

                // Replace login fragment
                new WelcomeActivity().replaceLoginFragment();
                break;
		}

	}

	// Check Validation Method
	private void checkValidation() {
		// Get all edittext texts
		strUsername = username.getText().toString();
		strName = fullName.getText().toString();
		strEmail = emailId.getText().toString();
		strPhone = mobileNumber.getText().toString();
		strPassword = password.getText().toString();
		strConfirmPassword = confirmPassword.getText().toString();
		strFcmId = FirebaseInstanceId.getInstance().getId();
		strImgName = strUsername.replace(".", "_").replace("@", "_");

		// Check if all strings are null or not
		if (strUsername.equals("") || strUsername.length() == 0
				|| strName.equals("") || strName.length() == 0
				|| strEmail.equals("") || strEmail.length() == 0
				|| strPhone.equals("") || strPhone.length() == 0
				|| strPassword.equals("") || strPassword.length() == 0
				|| strConfirmPassword.equals("") || strConfirmPassword.length() == 0)

			new CustomToast().Show_Toast(getActivity(), view,
					"All fields are required.");

            // Check if email id valid or not
        else if (!Login_Fragment.isValidUsername(strUsername))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your username is Invalid.");

		// Check if email id valid or not
		else if (!Login_Fragment.isValidEmail(strEmail))
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");

		// Check if both password should be equal
		else if (!strConfirmPassword.equals(strPassword))
			new CustomToast().Show_Toast(getActivity(), view,
					"Both password doesn't match.");

		// Make sure user should check Terms and Conditions checkbox
		else if (!terms_conditions.isChecked())
			new CustomToast().Show_Toast(getActivity(), view,
					"Please select Terms and Conditions.");

		// Else do signup or do your stuff
		else{
			//at first check username validity. then email check --> phone check --> signup
			checkInServer(FinalVariables.FLAG_USERNAME_CHECK);
		}

	}

	private void checkInServer(final int flag){
		String url = FinalVariables.SIGN_UP_URL;
		String msg = "Signing up...";
		if(flag==FinalVariables.FLAG_USERNAME_CHECK){
			url = FinalVariables.USERNAME_VALIDITY_URL;
			msg = "Checking username validity...";
		}
		else if(flag==FinalVariables.FLAG_EMAIL_CHECK) {
			url = FinalVariables.EMAIL_VALIDITY_URL;
			msg = "Checking email validity...";
		}
		else if(flag==FinalVariables.FLAG_PHONE_CHECK) {
			url = FinalVariables.PHONE_VALIDITY_URL;
			msg = "Checking phone no validity...";
		}

		if (sdk < Build.VERSION_CODES.LOLLIPOP) {
			progressDialog  = new ProgressDialog(getContext());
			progressDialog.setMessage(msg);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		else {
			catLoadingView.setText(msg);
			catLoadingView.show(fragmentManager, "");
			catLoadingView.setCancelable(false);
		}

		StringRequest stringRequest = new StringRequest(
				Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				signUpButton.setActivated(true);
				if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
				else catLoadingView.dismiss();
				if (response.isEmpty()) {
					new CustomToast().Show_Toast(getContext(), view, "Nothing found! Try again.");
				}
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
				if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
				else catLoadingView.dismiss();
				signUpButton.setActivated(true);
				Log.d("Error: ",volleyError.toString());
				new CustomToast().Show_Toast(getContext(), view, "Ops: "+volleyError.getMessage());
			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				if((flag==FinalVariables.FLAG_USERNAME_CHECK) || (flag==FinalVariables.FLAG_SIGNUP))
					params.put("username", strUsername);
				if((flag==FinalVariables.FLAG_EMAIL_CHECK) || (flag==FinalVariables.FLAG_SIGNUP))
					params.put("email", strEmail);
				if((flag==FinalVariables.FLAG_PHONE_CHECK) || (flag==FinalVariables.FLAG_SIGNUP))
					params.put("phone", strPhone);
				if(flag==FinalVariables.FLAG_SIGNUP){
                    params.put("name", strName);
                    params.put("password", strPassword);
                    params.put("fcm_id", FirebaseInstanceId.getInstance().getToken());
                    params.put("image_name", strImgName);
                }

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

	private void handleJSON(JSONObject jsonObject, int flag){

		try{
			String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

			if (what.equals(FinalVariables.SUCCESS)) { //means valid id
				message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
				success = FinalVariables.SUCCESS;
				new CustomToast().Show_Toast(getContext(), view, message);

				if(flag==FinalVariables.FLAG_USERNAME_CHECK) checkInServer(FinalVariables.FLAG_EMAIL_CHECK);
				else if(flag==FinalVariables.FLAG_EMAIL_CHECK) checkInServer(FinalVariables.FLAG_PHONE_CHECK);
				else if(flag==FinalVariables.FLAG_PHONE_CHECK) checkInServer(FinalVariables.FLAG_SIGNUP);
				else if(flag==FinalVariables.FLAG_SIGNUP){
					Soulmate soulmate = new Soulmate();
					soulmate.setUsername(strUsername);
					soulmate.setName(strName);
					soulmate.setEmail(strEmail);
					soulmate.setPhone(strPhone);
					soulmate.setPassword(strPassword);
					soulmate.setImagePath(FinalVariables.ONLINE_IMAGE_PATH+strImgName+".jpeg");
					soulmate.setFcmId(strFcmId);
					dbHelper.saveUserInfo(soulmate);
					dbHelper.saveLastSigninInfo(soulmate);

					sharedPreferencesEditor = sharedPreferences.edit();
					sharedPreferencesEditor.putBoolean(FinalVariables.IS_SESSION_EXIST, true);
					sharedPreferencesEditor.commit();

					ArrayList<String> myInfo = new ArrayList<>();
					//me.add(jsonObject.getString("sl"));
					myInfo.add(soulmate.getUsername());
					myInfo.add(soulmate.getName());
					myInfo.add(soulmate.getEmail());
					myInfo.add(soulmate.getPhone());
					myInfo.add(soulmate.getPassword());
					myInfo.add(soulmate.getImagePath());
					myInfo.add(soulmate.getFcmId());
					Intent intent = new Intent(getContext(), MainActivity.class);
					intent.putStringArrayListExtra("my_info", myInfo);
					startActivity(intent);
				}


			}else { //means error
				message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
				success = FinalVariables.FAILURE;
				new CustomToast().Show_Toast(getContext(), view, message);
                //Log.d("SQL error: ",jsonObject.getString("error"));
			}
			Log.d("Message: ",message);
			Log.d("Success: ",success);

		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
