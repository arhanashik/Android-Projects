package com.project.blackspider.musician.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.blackspider.musician.model.Audio;
import com.project.blackspider.musician.R;

/**
 * Created by Mr blackSpider on 9/4/2017.
 */

public class AudioItemViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final ImageView mCoverView, mOptions, mIcPlaying;
    public final TextView mTitleView;
    public final TextView mArtistView;
    public final TextView mDurationView;
    //public MusicItem mItem;
    public Audio mItem;

    public AudioItemViewHolder(View view) {
        super(view);
        //mView = view.findViewById(R.id.body);
        mView = view;
        mCoverView = (ImageView) view.findViewById(R.id.cover);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mArtistView = (TextView) view.findViewById(R.id.artist);
        mDurationView = (TextView) view.findViewById(R.id.duration);
        mOptions = (ImageView) view.findViewById(R.id.options);
        mIcPlaying = (ImageView) view.findViewById(R.id.playing_icon);
    }
}
