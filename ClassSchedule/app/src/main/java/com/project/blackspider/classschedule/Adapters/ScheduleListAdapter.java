package com.project.blackspider.classschedule.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.App.AppController;
import com.project.blackspider.classschedule.CustomAlertDialogs.CustomAlertDialogBox;
import com.project.blackspider.classschedule.CustomView.FeedImageView;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Models.FeedItem;
import com.project.blackspider.classschedule.Models.ScheduleItem;
import com.project.blackspider.classschedule.NewsFeed.NewsFeedPost;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.Utils.LruBitmapCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by Mr blackSpider on 7/29/2017.
 */

public class ScheduleListAdapter extends BaseAdapter implements View.OnClickListener{
    private Activity activity;
    private LayoutInflater inflater;
    private List<ScheduleItem> scheduleItems;
    private ArrayList<String> myPostsSl = new ArrayList<>();
    private ArrayList<String> postmanAllInfo = new ArrayList<>();

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    private EmojIconActions emojIconActions;
    private EmojiconEditText editTextComment;
    private ImageView imageViewEmojIconKeyboard;
    private ImageView imgViewSaveCommentBtn;
    private LinearLayout linearLayoutAllComments;
    private LinearLayout slider;
    private ImageView imageViewPostOptions;
    private LinearLayout editPost;
    private LinearLayout deletePost;
    private LinearLayout hidePost;
    private View convertView;
    private View customView;
    private EditText editTextCourseCode;
    private EditText editTextCourseTitle;
    private EditText editTextCourseTeacher;
    private EditText editTextClassRoom;
    private EditText editTextDescription;
    private TimePicker timePickerClassTime;

    private CustomImageConverter customImageConverter;
    private CustomAnimation customAnimation;
    private DBHelper dbHelper;
    private FinalVariables finalVariables;
    private CustomAlertDialogBox customAlertDialogBox;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;
    private NewsFeedPost newsFeedPost;

    //private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private Bitmap bitmap;
    private StringRequest stringRequest;
    private ProgressDialog pd;private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;

    private static final int FLAG_SCHEDULE_POST_UPDATE = 0;
    private static final int FLAG_SCHEDULE_POST_DELETE = 1;
    private static final int FLAG_SCHEDULE_POST_REVIEW = 2;

    int position = -1;
    private int flag;

    private String userInput;
    private String tableName;
    private String courseCode = "";
    private String courseTitle = "";
    private String courseTeacher = "";
    private String classRoom = "";
    private String classTime = "";
    private String description = "";
    private String session = "";

    public ScheduleListAdapter(Activity activity, List<ScheduleItem> scheduleItems, ArrayList<String> myPostsSl,
                               String tableName,int flag, String session, LinearLayout slider) {
        this.activity = activity;
        this.scheduleItems = scheduleItems;
        this.myPostsSl = myPostsSl;
        this.tableName = tableName;
        this.flag = flag;
        this.session = session;
        this.slider = slider;

        dbHelper = new DBHelper(activity);
        finalVariables = new FinalVariables();
        customImageConverter = new CustomImageConverter();
        customAnimation = new CustomAnimation(activity);
        customAlertDialogBox = new CustomAlertDialogBox(activity);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(activity);

        dbHelper = new DBHelper(activity);
        finalVariables = new FinalVariables();
        customImageConverter = new CustomImageConverter();
        customAnimation = new CustomAnimation(activity);
        customAlertDialogBox = new CustomAlertDialogBox(activity);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(activity);
        pd = new ProgressDialog(activity);

        requestQueue = Volley.newRequestQueue(activity);

        if (flag == FinalVariables.FLAG_STUDENT) postmanAllInfo = dbHelper.getUserInfo();
        else if (flag == FinalVariables.FLAG_TEACHER) postmanAllInfo = dbHelper.getTeacherInfo();
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public int getCount() {
        return scheduleItems.size();
    }

    @Override
    public Object getItem(int location) {
        return scheduleItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMyPostsSl(ArrayList<String> myPostsSl) {
        this.myPostsSl = myPostsSl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        this.convertView = convertView;

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);


        imageViewPostOptions = (ImageView) convertView.findViewById(R.id.imageViewPostOptions);

        editTextComment = (EmojiconEditText) convertView.findViewById(R.id.editTextComment);
        imageViewEmojIconKeyboard = (ImageView) convertView.findViewById(R.id.imageViewEmojIconKeyboard);
        imgViewSaveCommentBtn = (ImageView) convertView.findViewById(R.id.imageViewSaveComment);
        linearLayoutAllComments = (LinearLayout) convertView.findViewById(R.id.feed_root_container);

        emojIconActions = new EmojIconActions(activity, linearLayoutAllComments, editTextComment, imageViewEmojIconKeyboard);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.icon_keyboard3, R.drawable.icon_emo_btn3);

        ScheduleItem item = scheduleItems.get(position);

        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimestamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        String status = "Course Code: "+item.getCourseCode()+"\n"
                +"Course Title: "+item.getCourseTitle()+"\n\n"
                +"Course Teacher: "+item.getCourseTeacher()+"\n\n"
                +"Class Room: "+item.getClassRoom()+"\n"
                +"Class Time: "+item.getClassTime()+"\n\n"
                +"Description: "+item.getDescription()+"\n";

        // Chcek for empty status message
        if (!TextUtils.isEmpty(status)) {
            statusMsg.setText(status);
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfileImage(), imageLoader);

        // Feed image
        if (item.getPostImage() != null) {
            feedImageView.setImageUrl(item.getPostImage(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        imageViewPostOptions.setTag(position+"");
        imageViewPostOptions.setOnClickListener(this);

        return convertView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        position = Integer.parseInt(v.getTag().toString());
        final ScheduleItem item = scheduleItems.get(position);
        Log.e("Post sl: ",""+item.getSl());
        String session = postmanAllInfo.get(FinalVariables.SESSION_INDEX).replace("-","_");
        tableName = "table_schedule_"+postmanAllInfo.get(FinalVariables.FACULTY_INDEX).toLowerCase()+"_"+session;

        editPost = (LinearLayout) slider.findViewById(R.id.edit);
        deletePost = (LinearLayout) slider.findViewById(R.id.delete);
        hidePost = (LinearLayout) slider.findViewById(R.id.hide);
        editPost.setVisibility(View.GONE);
        deletePost.setVisibility(View.GONE);
        editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Edit Button: ",""+v.getId());
                //Toast.makeText(context, "Post SL "+allPostsSl.get(position), Toast.LENGTH_SHORT).show();
                courseCode = item.getCourseCode();
                courseTitle = item.getCourseTitle();
                courseTeacher  = item.getCourseTeacher();
                classRoom = item.getClassRoom();
                classTime = item.getClassTime();
                description = item.getDescription();
                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                customAnimation.animDown(slider);
                convertView.setAlpha(1.0f);
            }
        });
        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Delete Button: ",""+v.getId());
                //Toast.makeText(context, "Post SL "+allPostsSl.get(position), Toast.LENGTH_SHORT).show();
                getNewInput(FLAG_SCHEDULE_POST_DELETE, tableName, position);
                customAnimation.animDown(slider);
                convertView.setAlpha(1.0f);
            }
        });
        hidePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAnimation.animDown(slider);
                convertView.setAlpha(1.0f);
                Toast.makeText(activity, "Pos: "+position, Toast.LENGTH_SHORT).show();
            }
        });

        switch (v.getId()){
            case R.id.imageViewPostOptions:
                if(slider.getVisibility()==View.GONE) {
                    //convertView.setAlpha(0.2f);
                    customAnimation.animUp(slider);
                    for (int i = 0; i<myPostsSl.size(); i++){
                        if(item.getSl()==myPostsSl.get(i)){
                            editPost.setVisibility(View.VISIBLE);
                            deletePost.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }else {
                    //convertView.setAlpha(1.0f);
                    customAnimation.animDown(slider);
                }
                break;

            default:
                break;
        }
    }

    protected void getNewInput(final int flag, final String tableName, final int position){
        alertDialogBuilder = new AlertDialog.Builder(activity);
        String positiveButtonTitle = "Yes";
        // get layout_developer.xml view
        if (flag == FLAG_SCHEDULE_POST_UPDATE){
            customView = inflater.inflate(R.layout.layout_schedule_post_input, null);
            editTextCourseCode = (EditText) customView.findViewById(R.id.editTextCourseCode);
            editTextCourseTitle = (EditText) customView.findViewById(R.id.editTextCourseTitle);
            editTextCourseTeacher = (EditText) customView.findViewById(R.id.editTextCourseTeacher);
            editTextClassRoom = (EditText) customView.findViewById(R.id.editTextClassRoom);
            editTextDescription = (EditText) customView.findViewById(R.id.editTextDescription);
            timePickerClassTime = (TimePicker) customView.findViewById(R.id.timePickerClassTime);

            if(!courseCode.isEmpty()) editTextCourseCode.setText(courseCode);
            if(!courseTitle.isEmpty()) editTextCourseTitle.setText(courseTitle);
            if(!courseTeacher.isEmpty()) editTextCourseTeacher.setText(courseTeacher);
            if(!classRoom.isEmpty()) editTextClassRoom.setText(classRoom);
            if(!description.isEmpty()) editTextDescription.setText(description);
            timePickerClassTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    int hr; int min;
                    String AM_PM = "AM";
                    if(Build.VERSION.SDK_INT<23){
                        hr= timePickerClassTime.getCurrentHour();
                        min = timePickerClassTime.getCurrentMinute();
                    }else {
                        hr = timePickerClassTime.getHour();
                        min = timePickerClassTime.getMinute();
                    }

                    //if(timePickerClassTime.is24HourView()) hr = hr-12;
                    if(hourOfDay>11){
                        AM_PM = "PM";
                        hr = hr-12;
                    }
                    classTime = hr+":"+min+" "+AM_PM;
                }
            });

            alertDialogBuilder.setView(customView);
            positiveButtonTitle = "Update";

        }else if(flag == FLAG_SCHEDULE_POST_REVIEW){
            String message = "Course Code: "+courseCode+"\n"
                    +"Course Title: "+courseTitle+"\n"
                    +"Course Teacher: "+courseTeacher+"\n"
                    +"Class Room: "+classRoom+"\n"
                    +"Class Time: "+classTime+"\n"
                    +"Description: "+description;
            alertDialogBuilder.setTitle("Review")
                    .setMessage(message)
                    .setNegativeButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                        }
                    });

            positiveButtonTitle = "Update";

        }else if(flag == FLAG_SCHEDULE_POST_DELETE){
            alertDialogBuilder.setTitle("Are you sure?");
            positiveButtonTitle = "Yes, Delete";

        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag == FLAG_SCHEDULE_POST_UPDATE){
                            courseCode = editTextCourseCode.getText().toString();
                            courseTitle = editTextCourseTitle.getText().toString();
                            courseTeacher = editTextCourseTeacher.getText().toString();
                            classRoom = editTextClassRoom.getText().toString();
                            description = editTextDescription.getText().toString();
                            if(courseTitle.isEmpty() || classTime.isEmpty()){
                                Toast.makeText(activity, "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                            }else if(description.length()>250){
                                Toast.makeText(activity, "Too Long Description(Max length 250 word)",
                                        Toast.LENGTH_SHORT).show();
                                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                            }else getNewInput(FLAG_SCHEDULE_POST_REVIEW, tableName, position);

                        }else if(flag == FLAG_SCHEDULE_POST_REVIEW){
                            if(courseTitle.isEmpty() || classTime.isEmpty()){
                                Toast.makeText(activity, "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                            }else if(description.length()>250){
                                Toast.makeText(activity, "Too Long Description(Max length 250 word)",
                                        Toast.LENGTH_SHORT).show();
                                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                            }else doVolleyRequest(position, FinalVariables.UPDATE_SCHEDULE_POST_URL, FLAG_SCHEDULE_POST_UPDATE, tableName);

                        }else if(flag == FLAG_SCHEDULE_POST_DELETE) {
                            doVolleyRequest(position, FinalVariables.DELETE_SCHEDULE_POST_URL, FLAG_SCHEDULE_POST_DELETE, tableName);

                        }
                    }
                });
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }

    private void doVolleyRequest(final int position, String url, final int flag, final String tableName){
        pd = new ProgressDialog(activity);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();

        stringRequest = new StringRequest(
                Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("Response: ", "From server: " + response);

                if (response.isEmpty())
                    Toast.makeText(activity, "No server response", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        handleJSON(position, jsonObject, flag);
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.d("Error: ",volleyError.toString());
                Toast.makeText(activity, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                ScheduleItem item = scheduleItems.get(position);
                //Adding parameters
                params.put("sl", item.getSl());

                if(flag == FLAG_SCHEDULE_POST_UPDATE){
                    params.put("course_code", courseCode);
                    params.put("course_title", courseTitle);
                    params.put("course_teacher", courseTeacher);
                    params.put("class_room", classRoom);
                    params.put("class_time", classTime);
                    params.put("description", description);
                    params.put("table", tableName);
                    if (flag == FinalVariables.FLAG_STUDENT){
                        params.put("faculty", postmanAllInfo.get(FinalVariables.FACULTY_INDEX));

                    }else if (flag == FinalVariables.FLAG_TEACHER){
                        params.put("faculty", postmanAllInfo.get(FinalVariables.TEACHER_FACULTY_INDEX));
                    }
                    params.put("session", session);

                }else if(flag == FLAG_SCHEDULE_POST_DELETE){
                    params.put("table", tableName);

                }

                //returning parameters
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    String message = "";
    String success = "";

    private void handleJSON(final int position, JSONObject jsonObject, final int flag){

        try{
            String what = jsonObject.getString(FinalVariables.KEY_SUCCESS);

            if (what.equals(FinalVariables.SUCCESS)) { //means valid id
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.SUCCESS;
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                if(flag== FLAG_SCHEDULE_POST_UPDATE){
                    ScheduleItem item = new ScheduleItem();

                    item.setCourseCode(courseCode);
                    item.setCourseTitle(courseTitle);
                    item.setCourseTeacher(courseTeacher);
                    item.setClassRoom(classRoom);
                    item.setClassTime(classTime);
                    item.setDescription(description);
                    scheduleItems.add(position, item);
                    notifyDataSetChanged();
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    message = jsonObject.getString("data");
                    //Toast.makeText(activity, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                }else if(flag== FLAG_SCHEDULE_POST_DELETE){
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    scheduleItems.remove(position);
                    notifyDataSetChanged();
                }

            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                message = jsonObject.getString("data");
                Toast.makeText(activity, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                Log.d("MySQL error: ", message);
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}