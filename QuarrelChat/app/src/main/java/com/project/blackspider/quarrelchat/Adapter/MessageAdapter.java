package com.project.blackspider.quarrelchat.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Interface.MessageAdapterListener;
import com.project.blackspider.quarrelchat.Model.Message;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CircleTransform;
import com.project.blackspider.quarrelchat.ViewHolder.ExternalTextMessageViewHolder;
import com.project.blackspider.quarrelchat.ViewHolder.InternalTextMessageViewHolder;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter {

    private List<Message> messages;
    private String myImg;
    private String soulmateImg;
    private Context mContext;

    private ImageView imgProfile;
    private TextView tvIcon;

    public static MessageAdapterListener mListener;

    public MessageAdapter(Context context, MessageAdapterListener listener, List<Message> messages, String myImg, String soulmateImg) {
        this.mContext = context;
        this.mListener = listener;
        this.messages = messages;
        this.myImg = myImg;
        this.soulmateImg = soulmateImg;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case FinalVariables.INTERNAL_TEXT_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user_text, parent, false);
                return new InternalTextMessageViewHolder(view);
            case FinalVariables.EXTERNAL_TEXT_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_external_text, parent, false);
                return new ExternalTextMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    private Message getMessageByPosition(int position) {
        if (messages != null && !messages.isEmpty()) {
            if (position >= 0 && position < messages.size()) {
                Message messageItem = messages.get(position);
                if (messageItem != null) {
                    return messageItem;
                }
            }
        }

        return null;
    }

    private int getPrevViewType(int position) {
        int viewType = -1;
        if (messages != null && !messages.isEmpty()) {
            if (position > 0 && position < messages.size()) {
                viewType = messages.get(position-1).getType();
            }
        }

        return viewType;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {
        if (holder == null) {
            return;
        }
        Message message = getMessageByPosition(listPosition);
        if (message != null) {
            // Converting timestamp into x ago format
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    Long.parseLong(message.getTimestamp()),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

            switch (message.getType()) {
                case FinalVariables.INTERNAL_TEXT_MSG:
                    InternalTextMessageViewHolder ivh = (InternalTextMessageViewHolder) holder;
                    ivh.internalIconTxt.setText(message.getFrom().substring(0, 1));
                    ivh.internalMsg.setText(message.getMessage());
                    ivh.internalReport.setImageDrawable(applyReport(message.getReport()));
                    ivh.internalTimestamp.setText(timeAgo);
                    if(message.isSame()){
                        ivh.internalCarrot.setVisibility(View.INVISIBLE);
                        ivh.internalImageContainer.setVisibility(View.INVISIBLE);
                    }else {
                        ivh.internalCarrot.setVisibility(View.VISIBLE);
                        ivh.internalImageContainer.setVisibility(View.VISIBLE);
                    }
                    if((listPosition+1)<messages.size()){
                        if(messages.get(listPosition+1).isSame()){
                            ivh.internalTimestamp.setVisibility(View.GONE);
                            ivh.internalReport.setVisibility(View.GONE);
                        }else {
                            ivh.internalTimestamp.setVisibility(View.VISIBLE);
                            ivh.internalReport.setVisibility(View.VISIBLE);
                        }
                    }else {
                        ivh.internalTimestamp.setVisibility(View.VISIBLE);
                        ivh.internalReport.setVisibility(View.VISIBLE);
                    }

                    ivh.internalMsg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mListener.onMessageLongClicked(listPosition);
                            return true;
                        }
                    });

                    break;

                case FinalVariables.EXTERNAL_TEXT_MSG:
                    ExternalTextMessageViewHolder evh = (ExternalTextMessageViewHolder) holder;
                    evh.externalIconTxt.setText(message.getFrom().substring(0, 1));
                    evh.externalMsg.setText(message.getMessage());
                    evh.externalTimestamp.setText(timeAgo);
                    if(message.isSame()){
                        evh.externalCarrot.setVisibility(View.INVISIBLE);
                        evh.externalImageContainer.setVisibility(View.INVISIBLE);
                    }else {
                        evh.externalCarrot.setVisibility(View.VISIBLE);
                        evh.externalImageContainer.setVisibility(View.VISIBLE);
                    }
                    if((listPosition+1)<messages.size()){
                        if(messages.get(listPosition+1).isSame()){
                            evh.externalTimestamp.setVisibility(View.GONE);
                        }else {
                            evh.externalTimestamp.setVisibility(View.VISIBLE);
                        }
                    }else {
                        evh.externalTimestamp.setVisibility(View.VISIBLE);
                    }

                    evh.externalMsg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mListener.onMessageLongClicked(listPosition);
                            return false;
                        }
                    });

                    break;
            }

            applyProfilePicture(holder, message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    String imgUrl;
    private void applyProfilePicture(final RecyclerView.ViewHolder holder, Message message) {
        if (message != null) {
            switch (message.getType()) {
                case FinalVariables.INTERNAL_TEXT_MSG:
                    InternalTextMessageViewHolder ivh = (InternalTextMessageViewHolder) holder;
                    imgProfile = ivh.internalProfilePic;
                    tvIcon = ivh.internalIconTxt;
                    imgUrl = myImg;

                    break;

                case FinalVariables.EXTERNAL_TEXT_MSG:
                    ExternalTextMessageViewHolder evh = (ExternalTextMessageViewHolder) holder;
                    imgProfile = evh.externalProfilePic;
                    tvIcon = evh.externalIconTxt;
                    imgUrl = soulmateImg;

                    break;
            }
            if (!TextUtils.isEmpty(imgUrl)) {
                if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
                Glide.with(mContext).load(imgUrl)
                        .thumbnail(0.4f)
                        .crossFade()
                        .transform(new CircleTransform(mContext))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .signature(new StringSignature(String.valueOf(message.getType())))
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                Log.d("Msg adapter", "Error"+e.getMessage());
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                tvIcon.setVisibility(View.GONE);
                                imgProfile.setVisibility(View.VISIBLE);
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

    private Drawable applyReport(int report){
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_sending_black_24dp);
        int res = R.drawable.ic_sending_black_24dp;
        switch (report){
            case FinalVariables.MSG_SENDING:
                drawable =  ContextCompat.getDrawable(mContext, R.drawable.ic_sending_black_24dp);
                res = R.drawable.ic_sending_black_24dp;
                break;

            case FinalVariables.MSG_SENT:
                drawable =  ContextCompat.getDrawable(mContext, R.drawable.ic_sent_black_24dp);
                res = R.drawable.ic_sent_black_24dp;
                break;

            case FinalVariables.MSG_DELIVERED:
                drawable =  ContextCompat.getDrawable(mContext, R.drawable.ic_delivered_black_24dp);
                res = R.drawable.ic_delivered_black_24dp;
                break;

            case FinalVariables.MSG_SEEN:
                drawable  = ContextCompat.getDrawable(mContext, R.drawable.ic_seen_bg_24dp);
                //DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.background_color));
                res = R.drawable.ic_seen_bg_24dp;
                break;

            case FinalVariables.MSG_FAILED:
                drawable =  ContextCompat.getDrawable(mContext, R.drawable.ic_failed_black_24dp);
                res = R.drawable.ic_failed_black_24dp;
                break;
        }

        return drawable;
        //return res;
    }

}