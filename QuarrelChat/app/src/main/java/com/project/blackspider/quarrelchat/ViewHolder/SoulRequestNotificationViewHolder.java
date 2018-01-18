package com.project.blackspider.quarrelchat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.R;

/**
 * Created by Mr blackSpider on 8/26/2017.
 */

public class SoulRequestNotificationViewHolder extends RecyclerView.ViewHolder{

    public SimpleLoveTextView notificationName, notificationIconTxt, notification, notificationTimestamp,
            notificationAccept, notificationCancel, notificationDone;
    public ImageView notificationProfilePic;

    public SoulRequestNotificationViewHolder(View itemView) {
        super(itemView);

        this.notificationName = (SimpleLoveTextView) itemView.findViewById(R.id.textViewItemName);
        this.notification = (SimpleLoveTextView) itemView.findViewById(R.id.textViewNotification);
        this.notificationIconTxt = (SimpleLoveTextView) itemView.findViewById(R.id.icon_text);
        this.notificationTimestamp = (SimpleLoveTextView) itemView.findViewById(R.id.textViewItemTimestamp);
        this.notificationAccept = (SimpleLoveTextView) itemView.findViewById(R.id.btnAccept);
        this.notificationDone = (SimpleLoveTextView) itemView.findViewById(R.id.txtDone);
        this.notificationCancel = (SimpleLoveTextView) itemView.findViewById(R.id.btnCancel);
        this.notificationProfilePic = (ImageView) itemView.findViewById(R.id.imageViewItemProfilePic);
    }
}
