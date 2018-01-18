package com.project.blackspider.musician.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.ContentUris;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blackspider.musician.R;
import com.project.blackspider.musician.interfaces.AudioAdapterListener;
import com.project.blackspider.musician.model.Audio;
import com.project.blackspider.musician.utils.CircleTransform;
import com.project.blackspider.musician.view.AudioItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter implements SectionIndexer{
    private Context mContext;
    private Activity mActivity;
    private final List<Audio> mValues;
    private AudioAdapterListener mListener;

    private int selectedAudioId = -1;
    private ArrayList<Integer> mSectionPositions;

    public AudioAdapter(Activity activity, List<Audio> items, AudioAdapterListener listener) {
        mActivity = activity;
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_audio, parent, false);
        return new AudioItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final AudioItemViewHolder aivh = (AudioItemViewHolder) holder;
        aivh.mItem = mValues.get(position);
        applyCover(aivh.mItem.getAlbumId(), aivh.mCoverView);
        aivh.mTitleView.setText(aivh.mItem.getTitle());
        aivh.mArtistView.setText(aivh.mItem.getArtist());
        aivh.mDurationView.setText(DateUtils.formatElapsedTime(aivh.mItem.getDuration()/1000));

        aivh.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAudioClicked(v, position);
            }
        });

        aivh.mOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAudioOptionClicked(v, position);
            }
        });

        if(selectedAudioId == aivh.mItem.getId()) {
            aivh.mDurationView.setVisibility(View.GONE);
            aivh.mIcPlaying.setVisibility(View.VISIBLE);
            aivh.mIcPlaying.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Glide.with(mActivity).load(R.drawable.playing).asGif().into(aivh.mIcPlaying);

        }else {
            aivh.mDurationView.setVisibility(View.VISIBLE);
            aivh.mIcPlaying.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = mValues.size(); i < size; i++) {
            Character ch = mValues.get(i).getTitle().charAt(0);
            if(!Character.isLetter(ch)) ch = '#';
            String section = String.valueOf(ch).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    public int getSelectedAudioId() {
        return selectedAudioId;
    }

    public void setSelectedAudioId(int selectedAudioId) {
        this.selectedAudioId = selectedAudioId;
    }

    public void applyCover(int albumId, ImageView imageView){
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);

        Glide.with(mActivity)
                .load(uri)
                .crossFade()
                .transform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.skipMemoryCache(true)
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }

}
