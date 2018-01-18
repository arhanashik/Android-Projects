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

public class ExternalTextMessageViewHolder extends RecyclerView.ViewHolder{
    public FrameLayout externalImageContainer;
    public SimpleLoveTextView externalIconTxt, externalMsg, externalTimestamp;
    public ImageView externalProfilePic, externalCarrot;

    public ExternalTextMessageViewHolder(View itemView) {
        super(itemView);

        this.externalImageContainer = (FrameLayout) itemView.findViewById(R.id.fl_external_img_container);
        this.externalMsg = (SimpleLoveTextView) itemView.findViewById(R.id.tv_soulmate_msg);
        this.externalIconTxt = (SimpleLoveTextView) itemView.findViewById(R.id.tv_soulmate_img);
        this.externalTimestamp = (SimpleLoveTextView) itemView.findViewById(R.id.tv_soulmate_msg_timestamp);
        this.externalProfilePic = (ImageView) itemView.findViewById(R.id.imgV_soulmate_img);
        this.externalCarrot = (ImageView) itemView.findViewById(R.id.imgV_soulmate_img_carrot);
    }
}
