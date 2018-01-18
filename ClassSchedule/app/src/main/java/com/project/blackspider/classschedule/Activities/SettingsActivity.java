package com.project.blackspider.classschedule.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.CustomAlertDialogs.CustomAlertDialogBox;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Utils.LruBitmapCache;
import com.project.blackspider.classschedule.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imageViewUserImage;
    private TextView textViewUserName;
    private TextView textViewUserStatus;
    private ImageView imageViewImageToUpload;
    private Button buttonChangeImage;
    private Button buttonUploadImage;
    private Button buttonCancelUpload;
    private TextView textViewProfileSettings;
    private TextView textViewChatSettings;
    private TextView textViewPrivacySettings;
    private TextView textViewAdminPanel;
    private TextView textViewFeedbackOption;
    private TextView textViewShare;
    private TextView textViewAboutAndHelpOption;

    private ArrayList<String> allInfo;

    private FinalVariables finalVariables;
    private CustomImageConverter customImageConverter;
    private CustomAnimation customAnimation;
    private DBHelper dbHelper;
    private CustomAlertDialogBox customAlertDialogBox;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;

    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private RequestQueue mRequestQueue;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private LayoutInflater layoutInflater;
    private View view;
    private ProgressDialog progressDialog;

    private String imageString;

    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this);

        allInfo = new ArrayList<>();
        //allInfo = getIntent().getStringArrayListExtra("allInfo");
        allInfo = dbHelper.getUserInfo();
        imageViewUserImage = (ImageView) findViewById(R.id.imageViewUserImage);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserStatus = (TextView) findViewById(R.id.textViewUserStatus);
        textViewProfileSettings = (TextView) findViewById(R.id.textViewProfileSettings);
        textViewChatSettings = (TextView) findViewById(R.id.textViewChatSettings);
        textViewPrivacySettings = (TextView) findViewById(R.id.textViewPrivacySettings);
        textViewAdminPanel = (TextView) findViewById(R.id.textViewAdminPanel);
        textViewFeedbackOption = (TextView) findViewById(R.id.textViewFeedbackOption);
        textViewShare = (TextView) findViewById(R.id.textViewShare);
        textViewAboutAndHelpOption = (TextView) findViewById(R.id.textViewAboutAndHelpOption);
        finalVariables = new FinalVariables();
        customImageConverter = new CustomImageConverter();
        customAnimation = new CustomAnimation(this);
        customAlertDialogBox = new CustomAlertDialogBox(this);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(this);
        progressDialog = new ProgressDialog(this);

        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(imageViewUserImage);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewProfileSettings);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewChatSettings);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewPrivacySettings);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewAdminPanel);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewFeedbackOption);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewShare);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(textViewAboutAndHelpOption);

        setUserImage(imageViewUserImage);
        textViewUserName.setText(allInfo.get(FinalVariables.NAME_INDEX));
        textViewUserStatus.setText(allInfo.get(FinalVariables.STATUS_INDEX));

        textViewProfileSettings.setOnClickListener(this);
        textViewChatSettings.setOnClickListener(this);
        textViewPrivacySettings.setOnClickListener(this);
        textViewAdminPanel.setOnClickListener(this);
        textViewFeedbackOption.setOnClickListener(this);
        textViewShare.setOnClickListener(this);
        textViewAboutAndHelpOption.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==imageViewUserImage){
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Do you want to change image?")
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            galleryPermissionDialog(FinalVariables.GALLERY_ACCESS_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNeutralButton("No, It's fine", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }else if(v == textViewProfileSettings){
            Intent intent = new Intent(getApplicationContext(), ProfileSettingsActivity.class);
            startActivity(intent);


        }else if(v == textViewChatSettings){


        }else if(v == textViewPrivacySettings){


        }else if(v == textViewAdminPanel){


        }else if(v == textViewFeedbackOption){


        }else if(v == textViewShare){
            shareApp();

        }else if(v == textViewAboutAndHelpOption){


        }
        else if(v == buttonCancelUpload){
            alertDialog.dismiss();

        }else if(v == buttonChangeImage){
            customAlertDialogBox.imageSelectionMethodAlertDialog();

        }else if(v == buttonUploadImage){
            buttonCancelUpload.setEnabled(false);
            buttonChangeImage.setEnabled(false);
            buttonUploadImage.setEnabled(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            uploadImage();

        }
    }

    void galleryPermissionDialog(int requestCode) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
            return;

        }else customAlertDialogBox.imageSelectionMethodAlertDialog();
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
                customAlertDialogBox.imageSelectionMethodAlertDialog();

            } else {
                showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if (showRationale) {
                    Toast.makeText(this, "Please enable the Read Storage permission to change image", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Please Enable the Read Storage permission in permission", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, requestCode);

                    //proceed with logic by disabling the related features or quit the app.
                }


            }


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setUserImage(final ImageView imgView){
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_loading1);
        bitmap = customImageConverter.getCircledBitmap(bitmap);
        imgView.setImageBitmap(bitmap);
        customAnimation.roll(imgView);

        mRequestQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(this.mRequestQueue,
                new LruBitmapCache());
        imageLoader.get(allInfo.get(FinalVariables.IMAGE_PATH_INDEX), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                if(isImmediate){
                    //Toast.makeText(getApplicationContext(), "Loading image", Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(getApplicationContext(), "Image loaded", Toast.LENGTH_SHORT).show();
                    bitmap = customImageConverter.getResizedBitmap(response.getBitmap(), 200);
                    bitmap = customImageConverter.getCircledBitmap(bitmap);
                    imgView.clearAnimation();
                    imgView.setImageBitmap(bitmap);
                    imageViewUserImage.setOnClickListener(SettingsActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(FinalVariables.TAG, "Volley Error: "+error.getMessage());
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_error);
                bitmap = customImageConverter.getResizedBitmap(bitmap, 100);
                bitmap = customImageConverter.getCircledBitmap(bitmap);
                imgView.clearAnimation();
                imgView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == FinalVariables.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                bitmap = (Bitmap) data.getExtras().get("data");
                bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                imagePreviewAndUpload(bitmap);

                //Toast.makeText(getApplicationContext(), "Image selected", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image selection
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to select image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

            //else if the result is gallery Image
        } else if (requestCode == FinalVariables.GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully selected the image
                Uri filePath = data.getData();
                try {
                    //Getting the Bitmap from Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                    imagePreviewAndUpload(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image selection
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to select image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == FinalVariables.AVATER_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully selected the image
                String imageString = data.getStringExtra("imageString");
                bitmap = customImageConverter.convertStringIntoBitmap(imageString);
                bitmap = customImageConverter.getResizedBitmap(bitmap, 350);
                imagePreviewAndUpload(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image selection
                Toast.makeText(getApplicationContext(),
                        "User cancelled avater", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to select image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to select avater", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    protected void imagePreviewAndUpload(Bitmap bmp){
        // get layout_developer.xml view
        layoutInflater = LayoutInflater.from(this);
        view = layoutInflater.inflate(R.layout.layout_image_preview, null);

        imageViewImageToUpload = (ImageView) view.findViewById(R.id.imageViewImageToUpload);
        buttonChangeImage = (Button) view.findViewById(R.id.buttonChangeImage);
        buttonUploadImage = (Button) view.findViewById(R.id.buttonUploadImage);
        buttonCancelUpload = (Button) view.findViewById(R.id.buttonCancelUpload);
        buttonUploadImage.setText("Upload");

        bitmap = bmp;
        imageViewImageToUpload.setImageBitmap(customImageConverter.getCircledBitmap(bmp));

        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(buttonChangeImage);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(buttonUploadImage);
        customButtonPressedStateChanger.buttonPressStateWithOnTouchMethod(buttonCancelUpload);

        buttonChangeImage.setOnClickListener(this);
        buttonUploadImage.setOnClickListener(this);
        buttonCancelUpload.setOnClickListener(this);

        alertDialogBuilder = new AlertDialog.Builder(this);
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyTransparentTheme);

        // set layout_calculator.xml to alertdialog builder
        alertDialogBuilder.setView(view);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false);
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    private void uploadImage(){
        imageString = customImageConverter.convertBitmapIntoImageString(bitmap);

        stringRequest = new StringRequest(
                Request.Method.POST, FinalVariables.IMAGE_CHANGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("Response: ", "From server: " + response);

                if (response.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No server response", Toast.LENGTH_SHORT).show();
                    buttonUploadImage.setEnabled(true);
                    buttonChangeImage.setEnabled(true);
                    buttonCancelUpload.setEnabled(true);
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
                progressDialog.dismiss();
                Log.d("Error: ",volleyError.toString());
                buttonUploadImage.setEnabled(true);
                buttonChangeImage.setEnabled(true);
                buttonCancelUpload.setEnabled(true);
                Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                String imageName = "img_" + allInfo.get(FinalVariables.ID_INDEX)
                        +"_"+allInfo.get(FinalVariables.SESSION_INDEX)+".jpeg";
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                params.put(FinalVariables.KEY_EMAIL, allInfo.get(FinalVariables.EMAIL_INDEX));
                params.put(FinalVariables.KEY_IMAGE, imageString);
                params.put(FinalVariables.KEY_IMAGE_NAME, imageName);

                //returning parameters
                return params;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    String success;
    String message;
    private void handleJSON(JSONObject jsonObject){
        try {
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if(what.equals(FinalVariables.SUCCESS)){
                success = FinalVariables.SUCCESS;
                message = jsonObject.getString("message");

                alertDialog.dismiss();
                setUserImage(imageViewUserImage);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


            }else {
                success = FinalVariables.FAILURE;
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                buttonUploadImage.setEnabled(true);
                buttonChangeImage.setEnabled(true);
                buttonCancelUpload.setEnabled(true);
                Log.e("Failure ", message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e){
            buttonUploadImage.setEnabled(true);
            buttonChangeImage.setEnabled(true);
            buttonCancelUpload.setEnabled(true);
            Log.e("JSONException", ""+e.getMessage());
            Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void shareApp(){
        ApplicationInfo info = getApplicationContext().getApplicationInfo();
        String path = info.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        startActivity(intent.createChooser(intent,"Share app via "));
    }

}
