package com.project.blackspider.quarrelchat.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.project.blackspider.quarrelchat.App.ImageLoaderController;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Interface.NotificationAdapterListener;
import com.project.blackspider.quarrelchat.Model.Notification;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CircleTransform;
import com.project.blackspider.quarrelchat.ViewHolder.SoulRequestNotificationViewHolder;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter {

    private List<Notification> notifications;
    private Context mContext;

    private ImageView imgProfile;
    private TextView tvIcon;

    public static NotificationAdapterListener mListener;

    public NotificationAdapter(List<Notification> notifications, Context context, NotificationAdapterListener listener) {
        this.notifications = notifications;
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case FinalVariables.SOUL_REQUEST_NOTIFICATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
                return new SoulRequestNotificationViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return notifications.get(position).getType();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        Notification notification = notifications.get(listPosition);
        if (notification != null) {
            // Converting timestamp into x ago format
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(notification.getTimestamp()),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

            switch (notification.getType()) {
                case FinalVariables.SOUL_REQUEST_NOTIFICATION:
                    SoulRequestNotificationViewHolder svh = (SoulRequestNotificationViewHolder) holder;

//                    if(notification.isFromMe()) {
//                        svh.notificationName.setText(notification.getMyName());
//                        svh.notificationIconTxt.setText(notification.getMyName().substring(0, 1));
//                    }else {
//                        svh.notificationName.setText(notification.getSoulmateName());
//                        svh.notificationIconTxt.setText(notification.getSoulmateName().substring(0, 1));
//                    }
                    svh.notificationName.setText(notification.getSoulmateName());
                    svh.notificationIconTxt.setText(notification.getSoulmateName().substring(0, 1));

                    if(notification.getNotification().equals(FinalVariables.SOUL_REQUEST_SENT)){
                        if(notification.isFromMe()){
                            svh.notification.setText("Soul request sent to "+notification.getTo());
                            svh.notificationAccept.setVisibility(View.INVISIBLE);
                            svh.notificationCancel.setVisibility(View.INVISIBLE);

                        }else {
                            svh.notification.setText(notification.getFrom()+" sent you Soul request.");
                            svh.notificationAccept.setVisibility(View.VISIBLE);
                            svh.notificationCancel.setVisibility(View.VISIBLE);

                        }
                    }

                    if(notification.getNotification().equals(FinalVariables.SOUL_REQUEST_ACCEPTED)){
                        svh.notification.setText("Soul request accepted.");
                        svh.notificationAccept.setVisibility(View.INVISIBLE);
                        svh.notificationCancel.setVisibility(View.INVISIBLE);
                    }
                    else if(notification.getNotification().equals(FinalVariables.SOUL_REQUEST_CANCELED)){
                        svh.notification.setText("Soul request canceled.");
                        svh.notificationAccept.setVisibility(View.INVISIBLE);
                        svh.notificationCancel.setVisibility(View.INVISIBLE);
                    }
                    svh.notificationTimestamp.setText(timeAgo);

                    svh.notificationIconTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onImageIconClicked(listPosition);
                        }
                    });

                    svh.notificationProfilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onImageIconClicked(listPosition);
                        }
                    });

                    svh.notificationAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onSoulRequestAcceptListener(listPosition);
                        }
                    });

                    svh.notificationCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.onSoulRequestCancelListener(listPosition);
                        }
                    });

                    break;
            }

            applyProfilePicture(holder, notification);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void applyProfilePicture(final RecyclerView.ViewHolder holder, Notification notification) {
        if (notification != null) {
            String imgUrl = notification.getImagePath();
            switch (notification.getType()) {
                case FinalVariables.SOUL_REQUEST_NOTIFICATION:
                    SoulRequestNotificationViewHolder svh = (SoulRequestNotificationViewHolder) holder;
                    imgProfile = svh.notificationProfilePic;
                    tvIcon = svh.notificationIconTxt;

                    break;
            }
            if (!TextUtils.isEmpty(imgUrl)) {
                if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
                Glide.with(mContext).load(imgUrl)
                        .thumbnail(0.4f)
                        .crossFade()
                        .transform(new CircleTransform(mContext))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Log.d("Msg adapter", e.toString());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                imgProfile.setVisibility(View.VISIBLE);
                                tvIcon.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imgProfile);
            } else {
                imgProfile.setVisibility(View.GONE);
                tvIcon.setVisibility(View.VISIBLE);
            }
        }
    }

}