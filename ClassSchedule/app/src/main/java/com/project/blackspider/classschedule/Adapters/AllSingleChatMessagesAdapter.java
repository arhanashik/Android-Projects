package com.project.blackspider.classschedule.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.project.blackspider.classschedule.CustomAlertDialogs.CustomAlertDialogBox;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by Mr blackSpider on 12/23/2016.
 */

public class AllSingleChatMessagesAdapter extends ArrayAdapter<String> implements View.OnLongClickListener{
    private Activity context;
    private ArrayList<String> allMessagesSl = new ArrayList<>();
    private ArrayList<String> allMessages = new ArrayList<>();
    private ArrayList<String> allMessageType = new ArrayList<>();
    private ArrayList<String> allDeliveryReports = new ArrayList<>();
    private ArrayList<String> allSeenReports = new ArrayList<>();
    private ArrayList<String> allMessagesDate = new ArrayList<>();

    private View rowView;
    private EmojiconTextView textViewMessage;
    private TextView textViewMessageDeliveryReport;
    private TextView textViewMessageDate;

    private LayoutInflater inflater;

    private ImageLoader imageLoader;
    private Bitmap bitmap;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private ProgressDialog pd;

    private FinalVariables finalVariables;
    private CustomImageConverter customImageConverter;
    private CustomAnimation customAnimation;
    private DBHelper dbHelper;
    private CustomAlertDialogBox customAlertDialogBox;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    private SparseBooleanArray selectedItems;
    private AllSingleChatMessagesAdapterListener listener;

    private static final int FLAG_NEWS_FEED_POST_UPDATE = 0;
    private static final int FLAG_NEWS_FEED_POST_DELETE = 1;
    private static final int FLAG_SCHEDULE_POST_UPDATE = 2;
    private static final int FLAG_SCHEDULE_POST_DELETE = 3;

    private String userInput;

    public AllSingleChatMessagesAdapter(Activity context, ArrayList<String> allMessagesSl, ArrayList<String> allMessages,
                                        ArrayList<String> allMessageType, ArrayList<String> allDeliveryReports,
                                        ArrayList<String> allSeenReports, ArrayList<String> allMessagesDate,
                                        AllSingleChatMessagesAdapterListener listener) {
        super(context, R.layout.layout_sent_message, allMessages);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.allMessagesSl = allMessagesSl;
        this.allMessages = allMessages;
        this.allMessageType = allMessageType;
        this.allDeliveryReports = allDeliveryReports;
        this.allSeenReports = allSeenReports;
        this.allMessagesDate = allMessagesDate;
        this.listener = listener;

//        dbHelper = new DBHelper(context);
//        finalVariables = new FinalVariables();
//        customImageConverter = new CustomImageConverter();
//        customAnimation = new CustomAnimation(context);
//        customAlertDialogBox = new CustomAlertDialogBox(context);
//        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(context);
//        pd = new ProgressDialog(context);
//
//        requestQueue = Volley.newRequestQueue(context);

        //postmanAllInfo = dbHelper.getUserInfo();
        selectedItems = new SparseBooleanArray();
    }

    public View getView(int position, View view, ViewGroup parent) {
        inflater=context.getLayoutInflater();

        if(allMessageType.get(position).equals(FinalVariables.SENT_MESSAGE)){
            rowView=inflater.inflate(R.layout.layout_sent_message, null,true);

            textViewMessage = (EmojiconTextView) rowView.findViewById(R.id.textViewSingleChatSentMessage);
            textViewMessageDeliveryReport = (TextView) rowView.findViewById(R.id.textViewSingleChatSentMessageDeliveryReport);
            textViewMessageDate = (TextView) rowView.findViewById(R.id.textViewSingleChatSentMessageDate);

            textViewMessage.setText(allMessages.get(position));
            textViewMessageDeliveryReport.setText(allDeliveryReports.get(position));
            textViewMessageDate.setText(allMessagesDate.get(position));

        }else {

            rowView=inflater.inflate(R.layout.layout_received_message, null,true);

            textViewMessage = (EmojiconTextView) rowView.findViewById(R.id.textViewSingleChatReceivedMessage);
            textViewMessageDate = (TextView) rowView.findViewById(R.id.textViewSingleChatReceivedMessageDate);

            textViewMessage.setText(allMessages.get(position));
            textViewMessageDate.setText(allMessagesDate.get(position));
        }

        rowView.setTag(position+"");
        rowView.setOnLongClickListener(this);
        // apply click events
        applyClickEvents(rowView, position);
        rowView.setActivated(selectedItems.get(position, false));
        return rowView;

    }

    @Override
    public boolean onLongClick(View v) {
        int pos = Integer.parseInt(v.getTag().toString());
        listener.onRowLongClicked(pos);
        rowView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        return true;
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

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;

        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);

            Toast.makeText(context, "Unselected: " + allMessages.get(pos), Toast.LENGTH_SHORT).show();
        } else {
            selectedItems.put(pos, true);
            Toast.makeText(context, "Selected: " + allMessages.get(pos), Toast.LENGTH_SHORT).show();
        }
        notifyDataSetChanged();
    }

    public void clearSelections() {
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

    public interface AllSingleChatMessagesAdapterListener {
        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);
    }
}
