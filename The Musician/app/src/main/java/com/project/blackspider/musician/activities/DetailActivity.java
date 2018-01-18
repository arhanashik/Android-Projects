package com.project.blackspider.musician.activities;

import android.app.ActivityOptions;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.music.MusicCoverView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blackspider.musician.database.StorageUtil;
import com.project.blackspider.musician.adapters.TransitionAdapter;
import com.project.blackspider.musician.R;
import com.project.blackspider.musician.utils.CircleTransform;
import com.project.blackspider.musician.variables.FinalVariables;
import com.project.blackspider.musician.view.RhombusDrawable;
import com.project.blackspider.musician.view.ScrollTextView;

public class DetailActivity extends PlayerActivity implements View.OnClickListener{
    public static MusicCoverView mCoverView;
    public static ScrollTextView titleTxt;
    private ImageView btnAddToPlaylist, btnRepeat, btnShuffle, btnPrev, btnRewind, btnForward,
            btnNext, btnFavourite, btnHeadset, btnShare;
    public static FloatingActionButton fab;
    public static Context mContext;

    boolean isNeedToFinish = false;
    public static boolean isOpen = false;
    public boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        isOpen = true;
        MainActivity.isOpen = false;
        mContext = this;

        mCoverView = (MusicCoverView) findViewById(R.id.cover);
        titleTxt = (ScrollTextView) findViewById(R.id.title_txt);
        btnHeadset = (ImageView) findViewById(R.id.headset);
        btnShare = (ImageView) findViewById(R.id.share);
        btnAddToPlaylist = (ImageView) findViewById(R.id.add_to_playlist);
        btnPrev = (ImageView) findViewById(R.id.previous);
        btnNext = (ImageView) findViewById(R.id.next);
        btnFavourite = (ImageView) findViewById(R.id.favourite);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        btnRepeat = (ImageView) findViewById(R.id.repeat);
        btnShuffle = (ImageView) findViewById(R.id.shuffle);

        if(mProgressChangerView!=null) mProgressChangerView.setVisibility(View.GONE);
        if(mProgressView!=null) mProgressView.setVisibility(View.GONE);

        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                mCoverView.stop();
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                if(isNeedToFinish) {
                    titleTxt.setVisibility(View.GONE);
                    if(mProgressChangerView!=null) mProgressChangerView.setVisibility(View.GONE);
                    if(mProgressView!=null) mProgressView.setVisibility(View.GONE);
                    supportFinishAfterTransition();
                }
            }
        });

        toggleFabIcon();

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                //mCoverView.start();
                init();
                setListener();
                if(mProgressChangerView!=null) mProgressChangerView.setVisibility(View.VISIBLE);
                if(mProgressView!=null) mProgressView.setVisibility(View.VISIBLE);
                //playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");
            }
        });
    }

    public static void init() {
        if(mPlayer != null) {
            titleTxt.setText(mPlayer.getCurrentAudio().getTitle());
            titleTxt.startScroll();
            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, mPlayer.getCurrentAudio().getAlbumId());
            Glide.with(mContext)
                    .load(uri)
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.default_cover)
                    .into(mCoverView);
        }
    }

    private void setListener() {
        btnHeadset.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnAddToPlaylist.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFavourite.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        btnShuffle.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        isNeedToFinish = true;
        isOpen = false;
        MainActivity.isOpen = true;
        //if(mPlayer != null) mPlayer.pauseMedia();

        if(mCoverView.isRunning()) {
            mCoverView.stop();
        }
        else {
            titleTxt.setVisibility(View.GONE);
            if(mProgressChangerView!=null) mProgressChangerView.setVisibility(View.GONE);
            if(mProgressView!=null) mProgressView.setVisibility(View.GONE);
            supportFinishAfterTransition();
        }
    }

    @Override
    protected void onDestroy() {
        isOpen = false;
        super.onDestroy();
    }

    public void onFabClick(View view) {
        isOpen = true;
        if(mPlayer != null) {
            if(mPlayer.isPlaying()) {
                mCoverView.stop();
                mPlayer.pauseMedia();
                fab.setImageResource(R.drawable.ic_play_vector);
            }
            else{
                mCoverView.start();
                mPlayer.resumeMedia();
                fab.setImageResource(R.drawable.ic_pause_vector);
            }
        }
    }

    public void shareClick(View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                btnShare, getString(R.string.share_transition_name));
        Intent intent = new Intent(this, SharingActivity.class);
        startActivity(intent, options.toBundle());
    }

    public void toggleFabIcon(){
        if(mPlayer!=null){
            if(mPlayer.isPlaying()) {
                fab.setImageResource(R.drawable.ic_pause_vector);
                mCoverView.start();
            }
            else{
                fab.setImageResource(R.drawable.ic_play_vector);
                mCoverView.stop();
            }
        }
    }

    @Override
    public void onClick(View v) {
        isOpen = true;

        switch (v.getId()){
            case R.id.headset:
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
                break;

            case R.id.share:
                shareClick(v);
                break;

            case R.id.add_to_playlist:
                Toast.makeText(getApplicationContext(), "ID: "+mPlayer.getCurrentAudio().getId(), Toast.LENGTH_SHORT).show();
                break;

            case R.id.previous:
                if(mPlayer != null) {
                    mPlayer.skipToPrevious();
                    fab.setImageResource(R.drawable.ic_pause_vector);
                    init();
                }
                break;

            case R.id.next:
                if(mPlayer != null) {
                    mPlayer.skipToNext();
                    fab.setImageResource(R.drawable.ic_pause_vector);
                    init();
                }
                break;

            case R.id.repeat:
                new StorageUtil(getApplicationContext()).storeAudioPlayStyle(FinalVariables.REPEAT_ALL);
                Toast.makeText(getApplicationContext(), "Repeat all", Toast.LENGTH_SHORT).show();
                break;

            case R.id.shuffle:
                new StorageUtil(getApplicationContext()).storeAudioPlayStyle(FinalVariables.SHUFFLE);
                Toast.makeText(getApplicationContext(), "Shuffle", Toast.LENGTH_SHORT).show();
                break;

            case R.id.favourite:
                toggleFavourite();
                break;

            default:
                break;
        }
    }

    public void toggleFavourite(){
        if(!isFavourite) {
            isFavourite = true;
            btnFavourite.setImageResource(R.drawable.ic_favourite_full_24dp);
            Toast.makeText(getApplicationContext(), "Marked as favourite", Toast.LENGTH_SHORT).show();
        }
        else {
            isFavourite = false;
            btnFavourite.setImageResource(R.drawable.ic_favourite_gap_24dp);
            Toast.makeText(getApplicationContext(), "Removed from favourite", Toast.LENGTH_SHORT).show();
        }
    }
}
