package com.project.blackspider.quarrelchat.Utils;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mr blackSpider on 12/16/2016.
 */

public class ImageSelection{
    private Context context;
    private Activity activity;
    private Intent intent;

    private Uri fileUri = null; // file url to store image
    private Bitmap bitmap;

    private static FinalVariables finalVariables = new FinalVariables();

    public ImageSelection(Activity activity){
        this.activity = activity;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    /**
     * Launching camera app to capture image
     */
    public void captureImage() {
        intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // start the image capture Intent
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                FinalVariables.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void selectImageFromGallery() {
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture From"),
                FinalVariables.GALLERY_IMAGE_REQUEST_CODE);
    }


    /**
     * returning image
     */
    private File createImageFile() throws IOException{
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        String imagePath = "file:" + image.getAbsolutePath();
        intent.putExtra("imagePath", imagePath);

        return image;
    }

}
