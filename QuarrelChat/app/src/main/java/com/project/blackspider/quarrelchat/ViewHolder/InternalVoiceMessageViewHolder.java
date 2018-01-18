package com.project.blackspider.quarrelchat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.R;

/**
 * Created by Mr blackSpider on 8/26/2017.
 */

public class InternalVoiceMessageViewHolder extends RecyclerView.ViewHolder{
    public FrameLayout internalImageContainer;
    public SimpleLoveTextView internalIconTxt, internalMediaTime, internalMediaTotalTime, internalTimestamp;
    public ImageView internalProfilePic, internalCarrot, internalReport;

    public InternalVoiceMessageViewHolder(View itemView) {
        super(itemView);

        this.internalImageContainer = (FrameLayout) itemView.findViewById(R.id.fl_user_img_container);
        this.internalMediaTime = (SimpleLoveTextView) itemView.findViewById(R.id.tv_media_time);
        this.internalMediaTotalTime = (SimpleLoveTextView) itemView.findViewById(R.id.tv_media_time_total);
        this.internalIconTxt = (SimpleLoveTextView) itemView.findViewById(R.id.tv_user_img);
        this.internalTimestamp = (SimpleLoveTextView) itemView.findViewById(R.id.tv_user_msg_timestamp);
        this.internalProfilePic = (ImageView) itemView.findViewById(R.id.imgV_user_img);
        this.internalCarrot = (ImageView) itemView.findViewById(R.id.imgV_user_img_carrot);
        this.internalReport = (ImageView) itemView.findViewById(R.id.img_user_msg_report);
    }
}
