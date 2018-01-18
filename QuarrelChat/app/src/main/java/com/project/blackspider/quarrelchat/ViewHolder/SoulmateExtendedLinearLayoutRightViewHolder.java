package com.project.blackspider.quarrelchat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.R;

/**
 * Created by Mr blackSpider on 8/25/2017.
 */

public class SoulmateExtendedLinearLayoutRightViewHolder extends RecyclerView.ViewHolder {
    public SimpleLoveTextView textViewItemName, textViewItemStatus,
            textViewItemMsgCount, textViewItemLastMsg, textViewItemTimestamp;
    public ImageView imageViewItemProfilePic, imageViewChat, imageViewCall, imageViewProfile;
    public RelativeLayout container, infoContainer;
    public FrameLayout imgContainer;
    public LinearLayout btnContainer;

    public SoulmateExtendedLinearLayoutRightViewHolder(View view) {
        super(view);
        textViewItemName = (SimpleLoveTextView) view.findViewById(R.id.tv_name);
        textViewItemStatus = (SimpleLoveTextView) view.findViewById(R.id.tv_status);
        textViewItemMsgCount = (SimpleLoveTextView) view.findViewById(R.id.tv_msg_count);
        textViewItemLastMsg = (SimpleLoveTextView) view.findViewById(R.id.tv_last_msg);
        textViewItemTimestamp = (SimpleLoveTextView) view.findViewById(R.id.tv_timestamp);
        imageViewItemProfilePic = (ImageView) view.findViewById(R.id.img_profile_pic);
        imageViewChat = (ImageView) view.findViewById(R.id.img_chat);
        imageViewCall = (ImageView) view.findViewById(R.id.img_call);
        imageViewProfile = (ImageView) view.findViewById(R.id.img_account);
        container = (RelativeLayout) view.findViewById(R.id.container);
        imgContainer = (FrameLayout) view.findViewById(R.id.img_container);
        infoContainer = (RelativeLayout) view.findViewById(R.id.info_container);
        btnContainer = (LinearLayout) view.findViewById(R.id.btn_container);
    }
}
