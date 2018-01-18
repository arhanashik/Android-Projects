package com.project.blackspider.quarrelchat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.Interface.SoulmateAdapterListener;
import com.project.blackspider.quarrelchat.R;

/**
 * Created by Mr blackSpider on 8/25/2017.
 */

public class SoulmateGridLayoutViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
    public SimpleLoveTextView textViewItemName, textViewIcon, textViewItemMsgCount;
    public ImageView imageViewItemProfilePic, imageViewOption;
    public RelativeLayout container;
    public FrameLayout imgContainer;
    public View transParentViewUp, transParentViewDown;

    public SoulmateAdapterListener listener;

    public SoulmateGridLayoutViewHolder(View view) {
        super(view);
        textViewItemName = (SimpleLoveTextView) view.findViewById(R.id.tv_name);
        textViewIcon = (SimpleLoveTextView) view.findViewById(R.id.tv_icon);
        textViewItemMsgCount = (SimpleLoveTextView) view.findViewById(R.id.tv_msg_count);
        imageViewItemProfilePic = (ImageView) view.findViewById(R.id.img_profile_pic);
        imageViewOption = (ImageView) view.findViewById(R.id.img_option);
        container = (RelativeLayout) view.findViewById(R.id.container);
        imgContainer = (FrameLayout) view.findViewById(R.id.img_container);
        transParentViewUp = view.findViewById(R.id.transparent_view_up);
        transParentViewDown = view.findViewById(R.id.transparent_view_down);
        view.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        listener.onSoulmateRowLongClicked(getAdapterPosition());
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        return true;
    }
}
