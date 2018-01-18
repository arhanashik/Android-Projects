package com.project.blackspider.quarrelchat.CustomView;

import com.project.blackspider.quarrelchat.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

	// Custom Toast Method
	public void Show_Toast(Context context, View view, String error) {

		// Layout Inflater for inflating custom view
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// inflate the layout over view
		View layout = inflater.inflate(R.layout.custom_toast,
				(ViewGroup) view.findViewById(R.id.toast_root));

		// Get TextView id and set error
		TextView text = (TextView) layout.findViewById(R.id.toast_error);
		text.setText(error);

		Toast toast = new Toast(context);// Get Toast Context
		toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);// Set
																		// Toast
																		// gravity
																		// and
																		// Fill
																		// Horizoontal

		toast.setDuration(Toast.LENGTH_SHORT);// Set Duration
		toast.setView(layout); // Set Custom View over toast

		toast.show();// Finally show toast
	}

}
