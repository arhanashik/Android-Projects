package com.project.blackspider.quarrelchat.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Interface.SoulmateAdapterListener;
import com.project.blackspider.quarrelchat.Model.Message;
import com.project.blackspider.quarrelchat.Model.Soulmate;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Utils.CircleTransform;
import com.project.blackspider.quarrelchat.Utils.FlipAnimator;
import com.project.blackspider.quarrelchat.ViewHolder.SoulmateExtendedLinearLayoutLeftViewHolder;
import com.project.blackspider.quarrelchat.ViewHolder.SoulmateExtendedLinearLayoutRightViewHolder;
import com.project.blackspider.quarrelchat.ViewHolder.SoulmateGridLayoutViewHolder;
import com.project.blackspider.quarrelchat.ViewHolder.SoulmateLinearLayoutViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SoulmateAdapter extends RecyclerView.Adapter  {
    private Context mContext;
    private List<Soulmate> soulmates;
    private String myUsername;
    private SoulmateAdapterListener listener;
    private int layoutType;
    private SparseBooleanArray selectedItems;
    private DBHelper dbHelper;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public SoulmateAdapter(Context mContext, List<Soulmate> soulmates, String myUsername, SoulmateAdapterListener listener) {
        this.mContext = mContext;
        this.soulmates = soulmates;
        this.myUsername = myUsername;
        this.listener = listener;

        dbHelper = new DBHelper(mContext);
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    @Override
    public int getItemViewType(int position) {
        if(getLayoutType()==FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT){
            if(position%2==0) return FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT_LEFT;
            else return FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT_RIGHT;
        }
        else return getLayoutType();
    }

    @Override
    public long getItemId(int position) {
        return soulmates.get(position).getSl();
    }

    @Override
    public int getItemCount() {
        return soulmates.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case FinalVariables.TYPE_GRID_LAYOUT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_soulmate_gridview, parent, false);
                return new SoulmateGridLayoutViewHolder(view);
            case FinalVariables.TYPE_LINEAR_LAYOUT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_soulmate_listview, parent, false);
                return new SoulmateLinearLayoutViewHolder(view);
            case FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT_LEFT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_soulmate_extended_listview_left, parent, false);
                return new SoulmateExtendedLinearLayoutLeftViewHolder(view);
            case FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT_RIGHT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_soulmate_extended_listview_right, parent, false);
                return new SoulmateExtendedLinearLayoutRightViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Soulmate soulmate = soulmates.get(position);
        String lastMessage = "Start chatting now!";
        int msgSent = 0, msgReceived = 0, totalMsgCount=0;
        String msgCount = "";
        CharSequence timeAgo = "";
        Message message = new Message();
        message = dbHelper.getMessageCount(myUsername, soulmate.getUsername());
        if(message!=null){
            msgSent = message.getTotalSentMsg();
            msgReceived = message.getTotalReceivedMsg();
            totalMsgCount = message.getTotalMsgCount();
            msgCount = "Message Count\nSent: "+msgSent+"\nReceived: "+msgReceived;
        }
        message = dbHelper.getLastMessage(myUsername, soulmate.getUsername());
        if(message!=null){
            lastMessage = message.getMessage();
            if (lastMessage.length()>35) lastMessage = lastMessage.substring(0, 34)+" ...";
            if (!TextUtils.isEmpty(message.getTimestamp())){
                // Converting timestamp into x ago format
                timeAgo = DateUtils.getRelativeTimeSpanString(
                        Long.parseLong(message.getTimestamp()),
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
            }
        }

        // display profile image
        applyProfilePicture(holder, position);

        switch (getLayoutType()){
            case FinalVariables.TYPE_GRID_LAYOUT:
                final SoulmateGridLayoutViewHolder glvh = (SoulmateGridLayoutViewHolder) holder;
                glvh.textViewItemName.setText(soulmate.getName());
                glvh.textViewIcon.setText(soulmate.getName().substring(0,1));
                glvh.textViewItemMsgCount.setText(msgCount);
                glvh.imgContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleGridViewForground(glvh);
                    }
                });
                glvh.imageViewOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //toggleGridViewForground(glvh);
                        listener.onProfilePicClicked(position);
                    }
                });
                break;

            case FinalVariables.TYPE_LINEAR_LAYOUT:
                SoulmateLinearLayoutViewHolder llvh = (SoulmateLinearLayoutViewHolder) holder;
                // displaying text view data
                llvh.textViewItemName.setText(soulmate.getName());
                llvh.textViewItemLastMsg.setText(lastMessage);
                if(message.isSame())llvh.textViewItemLastMsg.setTextColor(mContext.getResources().getColor(R.color.background_gray));
                llvh.textViewItemTimestamp.setText(timeAgo);
                llvh.imageViewItemType.setImageResource(R.drawable.ic_friend_black_24dp);

                // displaying the first letter of From in icon text
                llvh.iconText.setText(soulmate.getName().substring(0, 1));

                // change the row state to activated
                llvh.itemView.setActivated(selectedItems.get(position, false));

                // change the font style depending on message read status
                //applyReadStatus(holder, soulmate);

                // handle icon animation
                //applyIconAnimation(holder, position);

                // apply click events
                applyClickEvents(llvh, position);
                break;

            case FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT:
                if(position%2==0){
                    SoulmateExtendedLinearLayoutLeftViewHolder selllvh = (SoulmateExtendedLinearLayoutLeftViewHolder) holder;
                    selllvh.textViewItemName.setText(soulmate.getName());
                    msgCount = "Message sent: "+msgSent+"\nMessage received: "+msgReceived;
                    selllvh.textViewItemMsgCount.setText(msgCount);
                    lastMessage = "Last message: "+lastMessage;
                    selllvh.textViewItemLastMsg.setText(lastMessage);
                    timeAgo = "Last chat on: "+timeAgo;
                    selllvh.textViewItemTimestamp.setText(timeAgo);
                }
                else {
                    SoulmateExtendedLinearLayoutRightViewHolder sellrvh = (SoulmateExtendedLinearLayoutRightViewHolder) holder;
                    sellrvh.textViewItemName.setText(soulmate.getName());
                    msgCount = "Message sent: "+msgSent+"\nMessage received: "+msgReceived;
                    sellrvh.textViewItemMsgCount.setText(msgCount);
                    lastMessage = "Last message: "+lastMessage;
                    sellrvh.textViewItemLastMsg.setText(lastMessage);
                    timeAgo = "Last chat on: "+timeAgo;
                    sellrvh.textViewItemTimestamp.setText(timeAgo);
                }

                applyClickEvents(holder, position);

                break;

            default:
                break;
        }
    }

    private void toggleGridViewForground(SoulmateGridLayoutViewHolder glvh){
        if(glvh.imageViewOption.getVisibility()==View.VISIBLE){
            glvh.imageViewOption.setVisibility(View.GONE);
            glvh.textViewItemName.setVisibility(View.GONE);
            glvh.textViewItemMsgCount.setVisibility(View.GONE);
            glvh.imgContainer.setForeground(ContextCompat.getDrawable(mContext, R.color.transparent_100));
            glvh.transParentViewUp.setVisibility(View.GONE);
            glvh.transParentViewDown.setVisibility(View.GONE);
        }else {
            glvh.imageViewOption.setVisibility(View.VISIBLE);
            glvh.textViewItemName.setVisibility(View.VISIBLE);
            glvh.textViewItemMsgCount.setVisibility(View.VISIBLE);
            glvh.imgContainer.setForeground(ContextCompat.getDrawable(mContext, R.color.transparent_50));
            glvh.transParentViewUp.setVisibility(View.VISIBLE);
            glvh.transParentViewDown.setVisibility(View.VISIBLE);
        }
    }

    private void applyClickEvents(RecyclerView.ViewHolder holder, final int position) {
        switch (getLayoutType()){
            case FinalVariables.TYPE_LINEAR_LAYOUT:
                SoulmateLinearLayoutViewHolder llvh = (SoulmateLinearLayoutViewHolder) holder;
                llvh.iconText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onProfilePicClicked(position);
                    }
                });

                llvh.imageViewItemProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onProfilePicClicked(position);
                    }
                });

                llvh.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onSoulmateRowClicked(position);
                    }
                });
                break;

            case FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT:
                if(position%2==0){
                    SoulmateExtendedLinearLayoutLeftViewHolder sellvh = (SoulmateExtendedLinearLayoutLeftViewHolder) holder;

                    sellvh.imageViewChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onSoulmateChatClicked(position);
                        }
                    });

                    sellvh.imageViewCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onSoulmateCallClicked(position);
                        }
                    });

                    sellvh.imageViewProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onSoulmateProfileClicked(position);
                        }
                    });
                }
                else {
                    SoulmateExtendedLinearLayoutRightViewHolder sellrvh = (SoulmateExtendedLinearLayoutRightViewHolder) holder;

                    sellrvh.imageViewChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onSoulmateChatClicked(position);
                        }
                    });

                    sellrvh.imageViewCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onSoulmateCallClicked(position);
                        }
                    });

                    sellrvh.imageViewProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onSoulmateProfileClicked(position);
                        }
                    });
                }
                break;
        }
    }

    ImageView imgView;
    SimpleLoveTextView tvIcon;
    private void applyProfilePicture(final RecyclerView.ViewHolder holder, int position) {
        String imgUrl = soulmates.get(position).getImagePath();
        switch (getLayoutType()){
            case FinalVariables.TYPE_GRID_LAYOUT:
                final SoulmateGridLayoutViewHolder glvh = (SoulmateGridLayoutViewHolder) holder;
                if (!TextUtils.isEmpty(imgUrl)) {
                    if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
                    Glide.with(mContext).load(imgUrl)
                            .thumbnail(0.4f)
                            .crossFade()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    Log.d("Soulmates adapter", e.toString());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    glvh.textViewIcon.setVisibility(View.INVISIBLE);
                                    glvh.imageViewItemProfilePic.setVisibility(View.VISIBLE);
                                    Drawable drawable = ContextCompat.getDrawable(mContext, R.color.text_navy_transparent_50);
                                    glvh.imgContainer.setForeground(drawable);
                                    return false;
                                }
                            })
                            .into(glvh.imageViewItemProfilePic);
                } else {
                    //
                }

                break;

            case FinalVariables.TYPE_LINEAR_LAYOUT:
                final SoulmateLinearLayoutViewHolder llvh = (SoulmateLinearLayoutViewHolder) holder;
                if (!TextUtils.isEmpty(imgUrl)) {
                    if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
                    Glide.with(mContext).load(imgUrl)
                            .thumbnail(0.4f)
                            .crossFade()
                            .transform(new CircleTransform(mContext))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    llvh.imageViewItemProfilePic.setVisibility(View.INVISIBLE);
                                    llvh.iconText.setVisibility(View.VISIBLE);
                                    Log.d("Soulmates adapter", e.toString());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    llvh.iconText.setVisibility(View.INVISIBLE);
                                    llvh.imageViewItemProfilePic.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .into(llvh.imageViewItemProfilePic);
                } else {
                    llvh.imageViewItemProfilePic.setVisibility(View.INVISIBLE);
                    llvh.iconText.setVisibility(View.VISIBLE);
                }
                break;

            case FinalVariables.TYPE_EXTENDED_LINEAR_LAYOUT:
                if(position%2==0) {
                    SoulmateExtendedLinearLayoutLeftViewHolder selllvh = (SoulmateExtendedLinearLayoutLeftViewHolder) holder;
                    imgView = selllvh.imageViewItemProfilePic;
                }
                else {
                    SoulmateExtendedLinearLayoutRightViewHolder sellrvh = (SoulmateExtendedLinearLayoutRightViewHolder) holder;
                    imgView = sellrvh.imageViewItemProfilePic;
                }
                if (!TextUtils.isEmpty(imgUrl)) {
                    if(!imgUrl.contains("http://")) imgUrl = "http://"+imgUrl;
                    Glide.with(mContext).load(imgUrl)
                            .thumbnail(0.4f)
                            .crossFade()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    Log.d("Soulmates adapter", e.toString());
                                    imgView.setImageResource(R.drawable.ic_signle_bird);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(imgView);
                } else {

                }

                break;
        }

    }

    private void applyIconAnimation(SoulmateLinearLayoutViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconText.setVisibility(View.GONE);
            resetIconYAxis(holder.imageViewItemProfilePic);
            holder.imageViewItemProfilePic.setVisibility(View.VISIBLE);
            holder.imageViewItemProfilePic.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.imageViewItemProfilePic, holder.iconText, true);
                resetCurrentIndex();
            }
        } else {
            holder.imageViewItemProfilePic.setVisibility(View.GONE);
            resetIconYAxis(holder.iconText);
            holder.iconText.setVisibility(View.VISIBLE);
            holder.iconText.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.imageViewItemProfilePic, holder.iconText, false);
                resetCurrentIndex();
            }
        }
    }


    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void applyReadStatus(SoulmateLinearLayoutViewHolder holder, Soulmate soulmate) {
        if (soulmate.isRead()) {
            holder.textViewItemName.setTypeface(null, Typeface.NORMAL);
            holder.textViewItemLastMsg.setTypeface(null, Typeface.NORMAL);
            holder.textViewItemTimestamp.setTypeface(null, Typeface.NORMAL);
            holder.textViewItemName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            holder.textViewItemLastMsg.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            holder.textViewItemTimestamp.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        } else {
            holder.textViewItemName.setTypeface(null, Typeface.BOLD);
            holder.textViewItemLastMsg.setTypeface(null, Typeface.BOLD);
            holder.textViewItemTimestamp.setTypeface(null, Typeface.BOLD);
            holder.textViewItemName.setTextColor(ContextCompat.getColor(mContext, R.color.background_color));
            holder.textViewItemLastMsg.setTextColor(ContextCompat.getColor(mContext, R.color.background_color));
            holder.textViewItemTimestamp.setTextColor(ContextCompat.getColor(mContext, R.color.background_color));
        }
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        soulmates.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }
}