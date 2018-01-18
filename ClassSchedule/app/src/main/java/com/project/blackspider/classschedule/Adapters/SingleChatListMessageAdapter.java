package com.project.blackspider.classschedule.Adapters;

import android.content.*;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by Mr blackSpider on 5/10/2017.
 */

public class SingleChatListMessageAdapter extends RecyclerView.Adapter<SingleChatListMessageAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> allMessagesSl = new ArrayList<>();
    private ArrayList<String> allMessages = new ArrayList<>();
    private ArrayList<String> allMessageType = new ArrayList<>();
    private ArrayList<String> allDeliveryReports = new ArrayList<>();
    private ArrayList<String> allSeenReports = new ArrayList<>();
    private ArrayList<String> allMessagesDate = new ArrayList<>();

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    private SparseBooleanArray selectedItems;

    private SingleChatListMessageAdapterListener listener;

    private LayoutInflater inflater;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        LinearLayout sentMessageContainer, receivedMessageContainer;
        public EmojiconTextView textViewSentMessage, textViewReceivedMessage;
        public TextView textViewSentMessageDeliveryReport;
        public TextView textViewSentMessageDate, textViewReceivedMessageDate;

        public MyViewHolder(View view) {
            super(view);

            sentMessageContainer = (LinearLayout) view.findViewById(R.id.sentMessageContainer);
            receivedMessageContainer = (LinearLayout) view.findViewById(R.id.receivedMessageContainer);
            textViewSentMessage = (EmojiconTextView) view.findViewById(R.id.textViewSingleChatSentMessage);
            textViewReceivedMessage = (EmojiconTextView) view.findViewById(R.id.textViewSingleChatReceivedMessage);
            textViewSentMessageDeliveryReport = (TextView) view.findViewById(R.id.textViewSingleChatSentMessageDeliveryReport);
            textViewSentMessageDate = (TextView) view.findViewById(R.id.textViewSingleChatSentMessageDate);
            textViewReceivedMessageDate = (TextView) view.findViewById(R.id.textViewSingleChatReceivedMessageDate);

            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public SingleChatListMessageAdapter(Context context, ArrayList<String> allMessagesSl, ArrayList<String> allMessages,
                                        ArrayList<String> allMessageType, ArrayList<String> allDeliveryReports,
                                        ArrayList<String> allSeenReports, ArrayList<String> allMessagesDate,
                                        SingleChatListMessageAdapterListener listener){
        this.context=context;
        this.allMessagesSl = allMessagesSl;
        this.allMessages = allMessages;
        this.allMessageType = allMessageType;
        this.allDeliveryReports = allDeliveryReports;
        this.allSeenReports = allSeenReports;
        this.allMessagesDate = allMessagesDate;
        this.listener = listener;

        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_sent_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(allMessageType.get(position).equals(FinalVariables.SENT_MESSAGE)){
            //holder.receivedMessageContainer.setVisibility(View.GONE);

            holder.textViewSentMessage.setText(allMessages.get(position));
            holder.textViewSentMessageDeliveryReport.setText(allDeliveryReports.get(position));
            holder.textViewSentMessageDate.setText(allMessagesDate.get(position));

            // apply click events
            applyClickEvents(holder.sentMessageContainer, position);

        }else {
            //holder.sentMessageContainer.setVisibility(View.GONE);

            holder.textViewReceivedMessage.setText(allMessages.get(position));
            holder.textViewReceivedMessageDate.setText(allMessagesDate.get(position));

            // apply click events
            applyClickEvents(holder.receivedMessageContainer, position);
        }

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));
    }

    private void applyClickEvents(View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
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

    @Override
    public long getItemId(int position) {
        return Long.parseLong(allMessagesSl.get(position));
    }

    @Override
    public int getItemCount() {
        return allMessagesSl.size();
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
        allMessagesSl.remove(position);
        allMessages.remove(position);
        allDeliveryReports.remove(position);
        allMessageType.remove(position);
        allSeenReports.remove(position);
        allMessagesDate.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface SingleChatListMessageAdapterListener {
        //void onIconClicked(int position);

        //void onIconImportantClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }
}
