package com.project.blackspider.quarrelchat.CustomView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CustomImageConverter;
import com.project.blackspider.quarrelchat.Utils.ImageSelection;
import com.project.blackspider.quarrelchat.Utils.UploadFile;

/**
 * Created by Mr blackSpider on 12/16/2016.
 */

public class CustomAlertDialogBox extends AlertDialog implements View.OnClickListener{
    private Activity context;

    private LayoutInflater li;
    private AlertDialog alertDialog;
    private Intent intent;
    private Bitmap bitmap = null;
    private RoundedBitmapDrawable roundedBitmapDrawable;

    private View promptsView;
    private ImageButton imageButtonCamera,imageButtonGallery, imageButtonAvater;
    private SimpleLoveTextView textViewCameraTitle,textViewGalleryTitle,textViewAvaterTitle;
    private ImageView imageViewImageToUpload;
    private Button buttonUploadImage;
    private Button buttonChangeImage;
    private Button buttonCancelUpload;

    private ImageSelection imageSelection;
    private UploadFile uploadFile;
    private CustomImageConverter customImageConverter = new CustomImageConverter();
    private FinalVariables finalVariables = new FinalVariables();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    public CustomAlertDialogBox(Activity context) {
        super(context);
        this.context = context;

        sharedPreferences = context.getSharedPreferences(FinalVariables.SHARED_PREFERENCES_IMAGE_SELECTION_INFO, Context.MODE_PRIVATE);
    }

    public void imageSelectionMethodAlertDialog(){
        // get layout_developer.xml view
        li = LayoutInflater.from(context);
        promptsView = li.inflate(R.layout.layout_image_selection_method, null);

        imageButtonCamera = (ImageButton) promptsView.findViewById(R.id.imageButtonCamera);
        imageButtonGallery = (ImageButton) promptsView.findViewById(R.id.imageButtonGallery);
        imageButtonAvater = (ImageButton) promptsView.findViewById(R.id.imageButtonAvater);
        textViewCameraTitle = (SimpleLoveTextView) promptsView.findViewById(R.id.textViewCameraTitle);
        textViewGalleryTitle = (SimpleLoveTextView) promptsView.findViewById(R.id.textViewGalleryTitle);
        textViewAvaterTitle = (SimpleLoveTextView) promptsView.findViewById(R.id.textViewAvaterTitle);

        imageButtonCamera.setOnClickListener(this);
        imageButtonGallery.setOnClickListener(this);
        imageButtonAvater.setOnClickListener(this);
        textViewCameraTitle.setOnClickListener(this);
        textViewGalleryTitle.setOnClickListener(this);
        textViewGalleryTitle.setOnClickListener(this);
        textViewAvaterTitle.setOnClickListener(this);

        Builder alertDialogBuilder = new Builder(context);
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyTransparentTheme);

        // set layout_calculator.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(true)
                .setNegativeButton("Cancel",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(bitmap==null){
                                    dialog.dismiss();
                                }
                                else {
                                    imagePreview(bitmap);
                                }
                            }
                        });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    public void imagePreview(Bitmap bmp){
        // get layout_developer.xml view
        li = LayoutInflater.from(context);
        promptsView = li.inflate(R.layout.layout_image_preview, null);

        imageViewImageToUpload = (ImageView) promptsView.findViewById(R.id.imageViewImageToUpload);
        buttonUploadImage = (Button) promptsView.findViewById(R.id.tv_upload_image);
        buttonCancelUpload = (Button) promptsView.findViewById(R.id.tv_cancel_upload);
        buttonUploadImage.setVisibility(View.GONE);
        buttonCancelUpload.setText("Keep it");

        bitmap = bmp;
        imageViewImageToUpload.setImageBitmap(customImageConverter.getCircledBitmap(bmp));

        buttonChangeImage.setOnClickListener(this);
        buttonUploadImage.setOnClickListener(this);
        buttonCancelUpload.setOnClickListener(this);

        Builder alertDialogBuilder = new Builder(context);
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyTransparentTheme);

        // set layout_calculator.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false);
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    @Override
    public void onClick(View v) {
        imageSelection = new ImageSelection(context);
        if(v == imageButtonCamera || v == textViewCameraTitle){
            // capture picture
            alertDialog.dismiss();
            imageSelection.captureImage();

        }else if(v == imageButtonGallery || v == textViewGalleryTitle){
            // select picture
            alertDialog.dismiss();
            imageSelection.selectImageFromGallery();

        }else if(v == imageButtonAvater || v == textViewAvaterTitle){
            // select picture
            alertDialog.dismiss();
            bitmap = null;
            //selectAvater();
            Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();

        }else if(v == buttonChangeImage){
            alertDialog.dismiss();
            imageSelectionMethodAlertDialog();

        }else if(v == buttonUploadImage){
            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(FinalVariables.IS_IMAGE_SELECTED, true);
            sharedPreferencesEditor.commit();
            if(sharedPreferences.getBoolean(FinalVariables.IS_IMAGE_SELECTED, false)){
                Toast.makeText(context, "Image selected", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

        }else if(v == buttonCancelUpload){
            alertDialog.dismiss();
            //bitmap = null;
            sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(FinalVariables.IS_IMAGE_SELECTED, true);
            sharedPreferencesEditor.commit();
            if(sharedPreferences.getBoolean(FinalVariables.IS_IMAGE_SELECTED, false)){
                Toast.makeText(context, "Image selected", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

        }
    }

    public Bitmap getBitmapFromResource(int image_id){
        Bitmap bmp = null;
        bmp = BitmapFactory.decodeResource(context.getResources(), image_id);

        return bmp;
    }
}
