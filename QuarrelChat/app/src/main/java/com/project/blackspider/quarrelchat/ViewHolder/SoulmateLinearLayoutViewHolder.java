package com.project.blackspider.quarrelchat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.Interface.SoulmateAdapterListener;
import com.project.blackspider.quarrelchat.R;

/**
 * Created by Mr blackSpider on 8/25/2017.
 */

public class SoulmateLinearLayoutViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
    public SimpleLoveTextView iconText, textViewItemName, textViewItemTimestamp, textViewItemLastMsg;
    public ImageView imageViewItemType;
    public ImageView imageViewItemProfilePic;
    public LinearLayout container;

    public SoulmateAdapterListener listener;

    public SoulmateLinearLayoutViewHolder(View view) {
        super(view);
        iconText = (SimpleLoveTextView) view.findViewById(R.id.icon_text);
        textViewItemName = (SimpleLoveTextView) view.findViewById(R.id.textViewItemName);
        textViewItemLastMsg = (SimpleLoveTextView) view.findViewById(R.id.textViewItemLastMsg);
        textViewItemTimestamp = (SimpleLoveTextView) view.findViewById(R.id.textViewItemTimestamp);
        imageViewItemType = (ImageView) view.findViewById(R.id.imageViewItemType);
        imageViewItemProfilePic = (ImageView) view.findViewById(R.id.imageViewItemProfilePic);
        container = (LinearLayout) view.findViewById(R.id.container);
        view.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        listener.onSoulmateRowLongClicked(getAdapterPosition());
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        return true;
    }
}
