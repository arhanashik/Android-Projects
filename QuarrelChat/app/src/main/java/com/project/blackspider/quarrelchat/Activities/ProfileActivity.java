package com.project.blackspider.quarrelchat.Activities;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.VolleyRequests.VolleySoulRequests;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private static Toolbar toolbar;
    private static ActionBar actionBar;
    private static ImageView imgProfile;
    private static FloatingActionButton fabChat;
    private static TextView tvName, tvUserName, tvNumber, tvEmail;
    private static SimpleLoveTextView tvProfileIconText;

    public static ArrayList<String> me = new ArrayList<>();
    public static ArrayList<String> mySoulmate = new ArrayList<>();
    public static boolean isFriend = false;

    private static Intent intent;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;

    private static String myImgUrl;
    private static String soulmateImgUrl;

    private static VolleySoulRequests volleySoulRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ProfileActivity.this, ChatActivity.class);
                intent.putStringArrayListExtra("me", MainActivity.myInfo);
                intent.putStringArrayListExtra("my_soulmate", mySoulmate);
                intent.putExtra("isFriend", isFriend);
                startActivity(intent);
            }
        });
        applyProfilePicture(soulmateImgUrl, mySoulmate.get(0));
    }

    private void init(){
        me = getIntent().getStringArrayListExtra("me");
        mySoulmate = getIntent().getStringArrayListExtra("my_soulmate");
        isFriend = getIntent().getBooleanExtra("isFriend", false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        imgProfile = (ImageView) findViewById(R.id.img_profile);
        tvProfileIconText = (SimpleLoveTextView) findViewById(R.id.tv_img_profile);
        fabChat = (FloatingActionButton) findViewById(R.id.fab_chat);
        tvName = (TextView) findViewById(R.id.tvName);
        tvUserName = (TextView) findViewById(R.id.tvUsername);
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        tvEmail = (TextView) findViewById(R.id.tvEmail);

        volleySoulRequests = new VolleySoulRequests(this, getSupportFragmentManager());

        actionBar.setTitle(mySoulmate.get(1));
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvName.setText(mySoulmate.get(1));
        tvProfileIconText.setText(mySoulmate.get(1).substring(0,1));
        tvUserName.setText(mySoulmate.get(0));
        tvEmail.setText(mySoulmate.get(2));
        tvNumber.setText(mySoulmate.get(3));

        myImgUrl = me.get(5);
        if(!myImgUrl.contains("http://")) myImgUrl = "http://"+ myImgUrl;

        soulmateImgUrl = mySoulmate.get(4);
        if(!soulmateImgUrl.contains("http://")) soulmateImgUrl = "http://"+ soulmateImgUrl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        if(isFriend){
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_call_black_24dp);
            DrawableCompat.setTint(drawable, Color.WHITE);
            menu.getItem(0).setIcon(drawable);
            menu.getItem(0).setTitle("Call");
        }else {
            menu.getItem(2).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                if(isFriend) Toast.makeText(getApplicationContext(),  "Coming soon...", Toast.LENGTH_SHORT).show();
                else {
                    volleySoulRequests.sendSoulRequest(me.get(0),
                            mySoulmate.get(0),
                            String.valueOf(System.currentTimeMillis()),
                            mySoulmate.get(5));
                }
                break;

            case R.id.action_relation:
                Toast.makeText(getApplicationContext(),  "Coming soon...", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_remove:
                String msg = "Are you sure you want to remove "+mySoulmate.get(1)+"?\n"
                        +"All conversions will be deleted.";
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setMessage(msg)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                volleySoulRequests.removeSoulmate(me.get(0),
                                        mySoulmate.get(0),
                                        String.valueOf(System.currentTimeMillis()),
                                        mySoulmate.get(5));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                mDialog = mBuilder.create();
                mDialog.show();
                break;

            case R.id.action_block:
                Toast.makeText(getApplicationContext(),  "Coming soon..", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private void applyProfilePicture(final String url, final String signature) {
        if (!TextUtils.isEmpty(url)){
            Glide.with(this)
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
        }
    }
}
