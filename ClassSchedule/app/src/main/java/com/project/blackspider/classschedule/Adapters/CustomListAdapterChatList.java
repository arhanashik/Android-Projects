package com.project.blackspider.classschedule.Adapters;

import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Utils.LruBitmapCache;
import com.project.blackspider.classschedule.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Mr blackSpider on 12/23/2016.
 */

public class CustomListAdapterChatList extends ArrayAdapter<String> {
    private Activity context;
    private ArrayList<String> allUserNames;
    private ArrayList<String> allUserImagePaths;
    private ArrayList<String> allUserOfflineImagePaths;
    private ArrayList<String> fcmDeviceRegIDs;
    private ArrayList<String> allUserLastMessage;
    private ArrayList<String> allUserEmails;
    private String chatListTableName;

    private ContextWrapper contextWrapper;
    private File offlineFileDir;

    private View rowView;
    private ImageView imageViewItemImage;
    private TextView textViewItemName;
    private TextView textViewItemStatus;
    private RelativeLayout relativeLayoutSelected;

    private CustomImageConverter customImageConverter = new CustomImageConverter();
    private Bitmap bitmap;
    private CustomAnimation customAnimation;
    private FinalVariables finalVariables = new FinalVariables();
    private DBHelper dbHelper;

    private ImageLoader imageLoader;
    private RequestQueue mRequestQueue;

    public CustomListAdapterChatList(Activity context, ArrayList<String> allUserNames,
                                     ArrayList<String> allUserImagePaths, ArrayList<String> allUserOfflineImagePaths,
                                     ArrayList<String> fcmDeviceRegIDs, ArrayList<String> allUserLastMessage,
                                     ArrayList<String> allUserEmails, String chatListTableName) {
        super(context, R.layout.layout_chat_list_item, allUserNames);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.allUserNames = allUserNames;
        this.allUserImagePaths = allUserImagePaths;
        this.allUserOfflineImagePaths = allUserOfflineImagePaths;
        this.fcmDeviceRegIDs = fcmDeviceRegIDs;
        this.allUserLastMessage = allUserLastMessage;
        this.allUserEmails = allUserEmails;
        this.chatListTableName = chatListTableName;

        customAnimation = new CustomAnimation(context);
        dbHelper = new DBHelper(context);
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        rowView=inflater.inflate(R.layout.layout_chat_list_item, null,true);

        imageViewItemImage = (ImageView) rowView.findViewById(R.id.imageViewItemProfilePic);
        textViewItemName = (TextView) rowView.findViewById(R.id.textViewItemName);
        textViewItemStatus = (TextView) rowView.findViewById(R.id.textViewItemStatus);
        relativeLayoutSelected = (RelativeLayout) rowView.findViewById(R.id.icon_selected);
        relativeLayoutSelected.setVisibility(View.INVISIBLE);

        setItemImage(imageViewItemImage, allUserImagePaths.get(position),
                allUserOfflineImagePaths.get(position), allUserEmails.get(position));
        textViewItemName.setText(allUserNames.get(position));
        textViewItemStatus.setText(allUserLastMessage.get(position));
        return rowView;

    }

    private void setItemImage(final ImageView imgView, final String url, final String offlineUrl, final String email){
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_loading1);
        bitmap = customImageConverter.getResizedBitmap(bitmap, 20);
        bitmap = customImageConverter.getCircledBitmap(bitmap);
        imgView.setImageBitmap(bitmap);
        customAnimation.roll(imgView);

        contextWrapper = new ContextWrapper(context.getApplicationContext());
        offlineFileDir = contextWrapper.getDir("stu_images", Context.MODE_PRIVATE);
        File offlineImage = new File(offlineUrl);
        final String imageName = email.replace(".","_").replace("@","_")+".png";
        if(!offlineFileDir.exists()) offlineFileDir.mkdir();
        final File destImgPath = new File(offlineFileDir, imageName);

        if(offlineUrl.isEmpty() || !offlineImage.exists()){
            mRequestQueue = Volley.newRequestQueue(context);
            imageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    if(isImmediate){
                        //Toast.makeText(context, "Loading image", Toast.LENGTH_SHORT).show();
                    }else {
                        //Toast.makeText(context, "Image loaded", Toast.LENGTH_SHORT).show();
                        bitmap = response.getBitmap();
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(destImgPath);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            Log.e("User image saving: ", destImgPath.getPath());
                        }catch (Exception e){
                            Log.e("User image saving: ", e.getMessage(), e);
                        }
                        dbHelper.updateOfflineImagePath(chatListTableName, email, destImgPath.getPath());
                        bitmap = customImageConverter.getResizedBitmap(bitmap, 80);
                        bitmap = customImageConverter.getCircledBitmap(bitmap);
                        imgView.clearAnimation();
                        imgView.setImageBitmap(bitmap);
                        //bitmap.recycle();
                        bitmap =null;
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(FinalVariables.TAG, "Volley Error: "+error.getMessage());
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_error);
                    bitmap = customImageConverter.getResizedBitmap(bitmap, 80);
                    bitmap = customImageConverter.getCircledBitmap(bitmap);
                    imgView.clearAnimation();
                    imgView.setImageBitmap(bitmap);
                    //bitmap.recycle();
                    bitmap =null;
                }
            });
        }else {
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(destImgPath));
                bitmap = customImageConverter.getResizedBitmap(bitmap, 80);
                bitmap = customImageConverter.getCircledBitmap(bitmap);
                imgView.clearAnimation();
                imgView.setImageBitmap(bitmap);
                //bitmap.recycle();
                bitmap =null;
                Log.e("User image loading: ", destImgPath.getAbsolutePath());
            }catch (FileNotFoundException e){
                Log.e("User image loading: ", e.getMessage(), e);
            }

        }

    }
}
