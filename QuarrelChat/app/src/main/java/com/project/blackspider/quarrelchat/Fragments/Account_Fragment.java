package com.project.blackspider.quarrelchat.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.project.blackspider.quarrelchat.Activities.MainActivity;
import com.project.blackspider.quarrelchat.Activities.WelcomeActivity;
import com.project.blackspider.quarrelchat.App.ImageLoaderController;
import com.project.blackspider.quarrelchat.CustomView.CatLoadingView;
import com.project.blackspider.quarrelchat.CustomView.CustomToast;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CircleTransform;
import com.project.blackspider.quarrelchat.Utils.CustomAnimation;
import com.project.blackspider.quarrelchat.Utils.CustomImageConverter;
import com.project.blackspider.quarrelchat.VolleyRequests.VolleyAccountSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Account_Fragment extends Fragment implements OnClickListener {
	private static View view, promptsView, dialogView;
	private static AppBarLayout appBarLayout;
	private static LinearLayout noAccountContainer;
	private static ImageView imgNoAccount, imgProfile;
	private static TextView tvNoAccount;
	private static SimpleLoveTextView tvImgSmall;
	private static CoordinatorLayout accountContainer;
    private static FloatingActionButton fabCamera;
    private static ImageView imgProfileSmall;
    private static TextView tvName, tvUserName, tvNumber, tvEmail, tvChangePassword,tvLogout;
    private static ImageView btnEditName, btnEditNumber, btnConfirm, btnClose, imageViewImageToUpload;
    private ImageButton imageButtonCamera,imageButtonGallery, imageButtonAvater;
    private SimpleLoveTextView tvProfileIconText, textViewCameraTitle, textViewGalleryTitle, textViewAvaterTitle,
            tvUpload, tvCancelUpload, tvCancelMethod, tvLogoutTitle, tvLogoutName, tvConfirmLogout, tvCancelLogout;
    private EditText editText1, editText2, editText3;
    private static CatLoadingView catLoadingView;

	private static FragmentManager fragmentManager;
	private static LayoutInflater layoutInflater;
	public static Bitmap bitmap;
    private static AlertDialog.Builder alertDialogBuilder;
    private static AlertDialog alertDialog;
    private static Dialog dialog;
    private static Context context;
    private static ProgressDialog progressDialog;

    private static SharedPreferences sharedPreferencesSession;
    private static SharedPreferences sharedPreferencesSignature;
    private static SharedPreferences.Editor sharedPreferencesEditor;

    private static String imgUrl;
    private static String signature;

	private static CustomAnimation mCustomAnimation;
	private static CustomImageConverter customImageConverter;
    private static VolleyAccountSettings volleyAccountSettings;

	private boolean isConnected = false;

    int sdk = android.os.Build.VERSION.SDK_INT;

	public Account_Fragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_account, container, false);
		initViews();
		return view;
	}

    @Override
    public Animation onCreateAnimation(final int transit, boolean enter, int nextAnim) {
        int anim;
        if(enter) anim = R.anim.right_enter;
        else anim = R.anim.left_out;
        Animation animation = AnimationUtils.loadAnimation(getContext(), anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                setListeners();
                setValues();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return animation;
    }

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();
        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
		noAccountContainer = (LinearLayout) view.findViewById(R.id.no_account_container);
		imgNoAccount = (ImageView) view.findViewById(R.id.img_no_account);
		tvNoAccount = (TextView) view.findViewById(R.id.tv_no_account);
		tvImgSmall = (SimpleLoveTextView) view.findViewById(R.id.icon_text);
        accountContainer = (CoordinatorLayout) view.findViewById(R.id.account_container);
        imgProfile = (ImageView) view.findViewById(R.id.img_profile);
        tvProfileIconText = (SimpleLoveTextView) view.findViewById(R.id.tv_img_profile);
        fabCamera = (FloatingActionButton) view.findViewById(R.id.fab_camera);
        imgProfileSmall = (ImageView) view.findViewById(R.id.iv_profile_img_small);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvUserName = (TextView) view.findViewById(R.id.tvUsername);
        tvNumber = (TextView) view.findViewById(R.id.tvNumber);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvChangePassword = (TextView) view.findViewById(R.id.tvChangePassword);
        tvLogout = (TextView) view.findViewById(R.id.tvLogout);
        btnEditName =(ImageView) view.findViewById(R.id.btnEditName);
        btnEditNumber =(ImageView) view.findViewById(R.id.btnEditNumber);
        catLoadingView = new CatLoadingView();

		mCustomAnimation = new CustomAnimation(getContext());
        customImageConverter = new CustomImageConverter();
        volleyAccountSettings = new VolleyAccountSettings(getActivity(), fragmentManager);

        context = getContext();

        sharedPreferencesSession = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);
        sharedPreferencesSignature = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_IMAGE_UPDATE_SIGNATURE, Context.MODE_PRIVATE);

        appBarLayout.setExpanded(false, true);
        appBarLayout.setMinimumHeight(150);

        imgUrl = MainActivity.myInfo.get(5);
        if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
        signature = sharedPreferencesSignature.getString(FinalVariables.IMAGE_SIGNATURE, "updated_at_"+System.currentTimeMillis());
        applyProfilePicture(imgUrl, signature);

	}

	// Set Listeners
	private void setListeners() {
		imgNoAccount.setOnClickListener(this);
        fabCamera.setOnClickListener(this);
        btnEditName.setOnClickListener(this);
        btnEditNumber.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
	}

	private void setValues(){
        if(!MainActivity.myInfo.isEmpty()){
            tvImgSmall.setText(MainActivity.myInfo.get(1).substring(0, 1));
            tvName.setText(MainActivity.myInfo.get(1));
            tvProfileIconText.setText(MainActivity.myInfo.get(1).substring(0, 1));
            tvUserName.setText(MainActivity.myInfo.get(0));
            tvEmail.setText(MainActivity.myInfo.get(2));
            tvNumber.setText(MainActivity.myInfo.get(3));
        }
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.img_account:
				//startActivity(new Intent(getContext(), ProfileActivity.class));
				break;

            case R.id.fab_camera:
                galleryPermissionDialog(FinalVariables.GALLERY_ACCESS_PERMISSION_REQUEST_CODE);
                break;

            case R.id.imageButtonCamera:
                alertDialog.dismiss();
                captureImage();
                break;

            case R.id.imageButtonGallery:
                alertDialog.dismiss();
                selectImageFromGallery();
                break;

            case R.id.imageButtonAvater:// select picture
                alertDialog.dismiss();
                bitmap = null;
                new CustomToast().Show_Toast(getActivity(), view,
                        "Coming soon...");
                break;

            case R.id.textViewCameraTitle:
                alertDialog.dismiss();
                captureImage();
                break;

            case R.id.textViewGalleryTitle:
                alertDialog.dismiss();
                selectImageFromGallery();
                break;

            case R.id.textViewAvaterTitle:// select picture
                alertDialog.dismiss();
                bitmap = null;
                new CustomToast().Show_Toast(getActivity(), view,
                        "Coming soon...");
                break;

            case R.id.tv_upload_image:
                mCustomAnimation.revealShow(dialogView, false, alertDialog);
                uploadImage();
                break;

            case R.id.tv_cancel_dialog:
                mCustomAnimation.revealShow(dialogView, false, alertDialog);
                break;

            case R.id.tv_cancel_upload:
                mCustomAnimation.revealShow(dialogView, false, alertDialog);
                break;

            case R.id.btnEditName:
                changeNamePhonePasswordDialog(FinalVariables.FLAG_CHANGE_NAME);
                break;

            case R.id.btnEditNumber:
                changeNamePhonePasswordDialog(FinalVariables.FLAG_CHANGE_PHONE);
                break;

            case R.id.tvChangePassword:
                changeNamePhonePasswordDialog(FinalVariables.FLAG_CHANGE_PASSWORD);
                break;

            case R.id.img_close:
                mCustomAnimation.revealShow(dialogView, false, alertDialog);
                break;

            case R.id.tvLogout:
                logOutDialog();
                break;

            case R.id.tv_confirm_logout:
                mCustomAnimation.revealShow(dialogView, false, alertDialog);
                volleyAccountSettings.logoutFromServer(MainActivity.myInfo.get(0), "null");
                break;

            case R.id.tv_cancel_logout:
                mCustomAnimation.revealShow(dialogView, false, alertDialog);
                new CustomToast().Show_Toast(getActivity(), view,
                        "Good decision :)");
                break;

			default:
				break;
		}
	}

	private void applyProfilePicture(final String url, final String signature) {
		if (!TextUtils.isEmpty(url)){
            Glide.with(getActivity())
                    .load(url)
                    .crossFade()
                    .signature(new StringSignature(signature))
                    .centerCrop()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            tvProfileIconText.setVisibility(View.GONE);
                            imgProfile.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(imgProfile);

            Glide.with(getActivity())
                    .load(url)
                    .thumbnail(0.4f)
                    .centerCrop()
                    .transform(new CircleTransform(getActivity()))
                    .crossFade()
                    .signature(new StringSignature(signature))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            tvImgSmall.setVisibility(View.INVISIBLE);
                            imgProfileSmall.setVisibility(View.VISIBLE);
                            appBarLayout.setExpanded(true, true);
                            return false;
                        }
                    })
                    .into(imgProfileSmall);
		}
	}

    void galleryPermissionDialog(int requestCode) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
            return;

        } else imageSelectionMethodAlertDialog();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        Map<String, Integer> perms = new HashMap<String, Integer>();
        // Initial
        perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        // Fill with results
        for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);
        // Check for READ_EXTERNAL_STORAGE

        boolean showRationale = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // All Permissions Granted
                imageSelectionMethodAlertDialog();

            } else {
                showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if (showRationale) {
                    Toast.makeText(getContext(), "Please enable the Read Storage permission to change image", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "Please Enable the Read Storage permission in permission", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, requestCode);

                    //proceed with logic by disabling the related features or quit the app.
                }


            }


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        if (requestCode == FinalVariables.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // successfully captured the image
                bitmap = (Bitmap) data.getExtras().get("data");
                bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                imagePreviewAndUpload(bitmap);

                //Toast.makeText(getApplicationContext(), "Image selected", Toast.LENGTH_SHORT).show();

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled Image selection
//                Toast.makeText(getContext(),
//                        "User cancelled image capture", Toast.LENGTH_SHORT)
//                        .show();

            } else {
                // failed to select image
                new CustomToast().Show_Toast(getActivity(), view,
                        "Something's wrong! Try again.");
            }

            //else if the result is gallery Image
        } else if (requestCode == FinalVariables.GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // successfully selected the image
                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                    imagePreviewAndUpload(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled Image selection
//                Toast.makeText(getContext(),
//                        "User cancelled image selection", Toast.LENGTH_SHORT)
//                        .show();

            } else {
                // failed to select image
                new CustomToast().Show_Toast(getActivity(), view,
                        "Something's wrong! Try again.");
            }

        } else if (requestCode == FinalVariables.AVATER_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // successfully selected the image
                String imageString = data.getStringExtra("imageString");
                bitmap = customImageConverter.convertStringIntoBitmap(imageString);
                bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                imagePreviewAndUpload(bitmap);

            } else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled Image selection
//                Toast.makeText(getContext(),
//                        "User cancelled avater", Toast.LENGTH_SHORT)
//                        .show();

            } else {
                // failed to select image
                new CustomToast().Show_Toast(getActivity(), view,
                        "Something's wrong! Try again.");
            }

        }
    }

    public void imageSelectionMethodAlertDialog(){
        // get layout_image_selection_method.xml view
        layoutInflater = LayoutInflater.from(getContext());
        promptsView = layoutInflater.inflate(R.layout.layout_image_selection_method, null);

        imageButtonCamera = (ImageButton) promptsView.findViewById(R.id.imageButtonCamera);
        imageButtonGallery = (ImageButton) promptsView.findViewById(R.id.imageButtonGallery);
        imageButtonAvater = (ImageButton) promptsView.findViewById(R.id.imageButtonAvater);
        textViewCameraTitle = (SimpleLoveTextView) promptsView.findViewById(R.id.textViewCameraTitle);
        textViewGalleryTitle = (SimpleLoveTextView) promptsView.findViewById(R.id.textViewGalleryTitle);
        textViewAvaterTitle = (SimpleLoveTextView) promptsView.findViewById(R.id.textViewAvaterTitle);
        tvCancelMethod = (SimpleLoveTextView) promptsView.findViewById(R.id.tv_cancel_dialog);

        dialogView = promptsView.findViewById(R.id.dialog);

        imageButtonCamera.setOnClickListener(this);
        imageButtonGallery.setOnClickListener(this);
        imageButtonAvater.setOnClickListener(this);
        textViewCameraTitle.setOnClickListener(this);
        textViewGalleryTitle.setOnClickListener(this);
        textViewAvaterTitle.setOnClickListener(this);
        tvCancelMethod.setOnClickListener(this);

        alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle);

        // set promptsView to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mCustomAnimation.revealShow(dialogView, true, alertDialog);
            }
        });
        // show it
        alertDialog.show();

    }

    public void captureImage() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // start the image capture Intent
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                FinalVariables.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture From"),
                FinalVariables.GALLERY_IMAGE_REQUEST_CODE);
    }

    public void imagePreviewAndUpload(final Bitmap bmp){
        // get layout_developer.xml view
        layoutInflater = LayoutInflater.from(getContext());
        promptsView = layoutInflater.inflate(R.layout.layout_image_preview, null);

        imageViewImageToUpload = (ImageView) promptsView.findViewById(R.id.imageViewImageToUpload);
        tvUpload = (SimpleLoveTextView) promptsView.findViewById(R.id.tv_upload_image);
        tvCancelUpload = (SimpleLoveTextView) promptsView.findViewById(R.id.tv_cancel_upload);

        dialogView = promptsView.findViewById(R.id.dialog);

        bitmap = bmp;
        imageViewImageToUpload.setImageBitmap(customImageConverter.getCircledBitmap(bmp));

        tvUpload.setOnClickListener(this);
        tvCancelUpload.setOnClickListener(this);
//
        alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle);

        // set layout_image_preview.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mCustomAnimation.revealShow(dialogView, true, alertDialog);
            }
        });
        // show it
        alertDialog.show();

    }

    String input1 = "", input2 = "", input3 = "";
    public void changeNamePhonePasswordDialog(final int flag){
        // get prompts_view_input_area.xml view
        layoutInflater = LayoutInflater.from(getContext());
        promptsView = layoutInflater.inflate(R.layout.prompts_view_input_area, null);

        editText1 = (EditText) promptsView.findViewById(R.id.input1);
        editText2 = (EditText) promptsView.findViewById(R.id.input2);
        editText3 = (EditText) promptsView.findViewById(R.id.input3);
        btnConfirm = (ImageView) promptsView.findViewById(R.id.img_confirm);
        btnClose = (ImageView) promptsView.findViewById(R.id.img_close);
        dialogView = promptsView.findViewById(R.id.dialog);

        if(flag==FinalVariables.FLAG_CHANGE_PASSWORD){
            editText1.setHint("Enter old password");
            editText2.setHint("Enter new password");
            editText3.setHint("Confirm new password");
        }else {
            editText2.setVisibility(View.GONE);
            editText3.setVisibility(View.GONE);
            if(flag==FinalVariables.FLAG_CHANGE_NAME) editText1.setHint("Enter new name");
            else if(flag==FinalVariables.FLAG_CHANGE_PHONE) editText1.setHint("Enter new phone number");
        }

        btnConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                input1 = editText1.getText().toString();
                if(!TextUtils.isEmpty(input1)) {
                    if(flag==FinalVariables.FLAG_CHANGE_NAME){
                        MainActivity.myInfo.set(FinalVariables.MY_NAME_INDEX, input1);
                        tvName.setText(input1);
                        mCustomAnimation.revealShow(dialogView, false, alertDialog);
                        volleyAccountSettings.changeName(MainActivity.myInfo.get(0), input1);
                    }
                    else if(flag==FinalVariables.FLAG_CHANGE_PHONE){
                        MainActivity.myInfo.set(FinalVariables.MY_PHONE_INDEX, input1);
                        tvNumber.setText(input1);
                        mCustomAnimation.revealShow(dialogView, false, alertDialog);
                        volleyAccountSettings.changePhone(MainActivity.myInfo.get(0), input1);
                    }
                    else if(flag==FinalVariables.FLAG_CHANGE_PASSWORD){
                        input2 = editText2.getText().toString();
                        input3 = editText3.getText().toString();
                        if(!(TextUtils.isEmpty(input2)||TextUtils.isEmpty(input3))){
                            if(input2.equals(input3)) {
                                mCustomAnimation.revealShow(dialogView, false, alertDialog);
                                volleyAccountSettings.changePassword(MainActivity.myInfo.get(0), input1, input2);
                            }
                            else new CustomToast().Show_Toast(getContext(), view, "Password miss match");
                        }
                    }
                }
            }
        });
        btnClose.setOnClickListener(this);

        alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle);

        // set layout_image_preview.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mCustomAnimation.revealShow(dialogView, true, alertDialog);
            }
        });
        // show it
        alertDialog.show();

    }

    public void logOutDialog(){
        // get layout_developer.xml view
        layoutInflater = LayoutInflater.from(getContext());
        promptsView = layoutInflater.inflate(R.layout.custom_logout_dialog, null);

        tvLogoutName = (SimpleLoveTextView) promptsView.findViewById(R.id.tv_logout_name);
        tvLogoutTitle = (SimpleLoveTextView) promptsView.findViewById(R.id.tv_logout_title);
        tvConfirmLogout = (SimpleLoveTextView) promptsView.findViewById(R.id.tv_confirm_logout);
        tvCancelLogout = (SimpleLoveTextView) promptsView.findViewById(R.id.tv_cancel_logout);

        dialogView = promptsView.findViewById(R.id.dialog);

        tvConfirmLogout.setOnClickListener(this);
        tvCancelLogout.setOnClickListener(this);
//
        alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialogStyle);

        // set layout_image_preview.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mCustomAnimation.revealShow(dialogView, true, alertDialog);
            }
        });
        // show it
        alertDialog.show();

    }

    private void uploadImage(){
        if (sdk < Build.VERSION_CODES.LOLLIPOP) {
            progressDialog  = new ProgressDialog(getContext());
            progressDialog.setMessage("UPLOADING...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        else {
            catLoadingView.setText("UPLOADING...");
            catLoadingView.show(fragmentManager, "");
            catLoadingView.setCancelable(false);
        }
        final String imageString = customImageConverter.convertBitmapIntoImageString(bitmap);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.IMAGE_UPDATE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
                Log.d("Response: ", "From server: " + response);

                if (response.isEmpty()){
                    new CustomToast().Show_Toast(getActivity(), view,
                            "Something's wrong! Try again.");
                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(jsonObject, FinalVariables.FLAG_UPDATE_IMAGE);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (sdk < Build.VERSION_CODES.LOLLIPOP) progressDialog.dismiss();
                else catLoadingView.dismiss();
                Log.d("Error: ",volleyError.toString());
                new CustomToast().Show_Toast(getActivity(), view,
                        "Something's wrong! Try again.");
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                String imageName = MainActivity.myInfo.get(0);
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                params.put("username", MainActivity.myInfo.get(0));
                params.put(FinalVariables.KEY_IMAGE, imageString);
                params.put(FinalVariables.KEY_IMAGE_NAME, imageName);

                //returning parameters
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

    String success, message;
    private void handleJSON(JSONObject jsonObject, int flag){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){
                success = FinalVariables.SUCCESS;
                message = jsonObject.getString("message");

                if(flag==FinalVariables.FLAG_UPDATE_IMAGE){
                    signature = "updated_at_"+System.currentTimeMillis();
                    sharedPreferencesEditor = sharedPreferencesSignature.edit();
                    sharedPreferencesEditor.putString(FinalVariables.SHARED_PREFERENCES_IMAGE_UPDATE_SIGNATURE, signature);
                    sharedPreferencesEditor.commit();
                    tvImgSmall.setVisibility(View.VISIBLE);
                    imgProfileSmall.setVisibility(View.INVISIBLE);
                    applyProfilePicture(imgUrl, signature);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }


            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                Log.e("Failure ", message);
                if(flag==FinalVariables.FLAG_UPDATE_IMAGE){
                    new CustomToast().Show_Toast(getActivity(), view,
                            message);
                }
                else {
                    new CustomToast().Show_Toast(getActivity(), view,
                            "Something's wrong! Try again.");
                }
            }

        }catch (JSONException e){
            Log.e("JSONException", ""+e.getMessage());
            new CustomToast().Show_Toast(getActivity(), view,
                    "Something's wrong! Try again.");
        }

    }
}
