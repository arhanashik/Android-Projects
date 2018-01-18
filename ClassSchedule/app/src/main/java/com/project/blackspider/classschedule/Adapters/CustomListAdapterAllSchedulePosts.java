package com.project.blackspider.classschedule.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.CustomAlertDialogs.CustomAlertDialogBox;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Utils.LruBitmapCache;
import com.project.blackspider.classschedule.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by Mr blackSpider on 12/23/2016.
 */

public class CustomListAdapterAllSchedulePosts extends ArrayAdapter<String> implements View.OnClickListener{
    private Activity context;
    private ArrayList<String> allPostmenName = new ArrayList<>();
    private ArrayList<String> allPostmenEmail = new ArrayList<>();
    private ArrayList<String> allPostsSl = new ArrayList<>();
    private ArrayList<String> allCourseCode = new ArrayList<>();
    private ArrayList<String> allCourseTitle = new ArrayList<>();
    private ArrayList<String> allCourseTeacher = new ArrayList<>();
    private ArrayList<String> allClassRoom = new ArrayList<>();
    private ArrayList<String> allClassTime = new ArrayList<>();
    private ArrayList<String> allDescription = new ArrayList<>();
    private ArrayList<String> allPostsDate = new ArrayList<>();
    private ArrayList<String> myPostsSl = new ArrayList<>();
    private ArrayList<String> postmanAllInfo = new ArrayList<>();
    private String tableName;
    private int flag;

    private View rowView;
    private LinearLayout linearLayoutSinglePost;
    private ImageView imageViewPostmanImage;
    private TextView textViewPostmanName;
    private TextView textViewPostDate;
    private TextView textViewPostMessage;
    private Button buttonEditPost;
    private Button buttonDeletePost;
    private EditText editTextCourseCode;
    private EditText editTextCourseTitle;
    private EditText editTextCourseTeacher;
    private EditText editTextClassRoom;
    private EditText editTextDescription;
    private TimePicker timePickerClassTime;

    private EmojIconActions emojIconActions;
    private EmojiconEditText editTextComment;
    private ImageView imageViewEmojIconKeyboard;
    private ImageView imgViewSaveCommentBtn;
    private LinearLayout linearLayoutAllComments;

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

    private static final int FLAG_SCHEDULE_POST_UPDATE = 0;
    private static final int FLAG_SCHEDULE_POST_DELETE = 1;
    private static final int FLAG_SCHEDULE_POST_REVIEW = 2;

    private String userInput;
    private String courseCode = "";
    private String courseTitle = "";
    private String courseTeacher = "";
    private String classRoom = "";
    private String classTime = "";
    private String description = "";
    private String session = "";

    public CustomListAdapterAllSchedulePosts(Activity context, ArrayList<String> allPostmenName,
                                             ArrayList<String> allPostmenEmail, ArrayList<String> allPostsSl,
                                             ArrayList<String> allCourseCode, ArrayList<String> allCourseTitle,
                                             ArrayList<String> allCourseTeacher, ArrayList<String> allClassRoom,
                                             ArrayList<String> allClassTime, ArrayList<String> allDescription,
                                             ArrayList<String> allPostsDate, ArrayList<String> myPostsSl,
                                             String tableName,int flag, String session) {
        super(context, R.layout.layout_single_post_view, allPostmenName);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.allPostmenName = allPostmenName;
        this.allPostmenEmail = allPostmenEmail;
        this.allPostsSl = allPostsSl;
        this.allCourseCode = allCourseCode;
        this.allCourseTitle = allCourseTitle;
        this.allCourseTeacher = allCourseTeacher;
        this.allClassRoom = allClassRoom;
        this.allClassTime = allClassTime;
        this.allDescription = allDescription;
        this.allPostsDate = allPostsDate;
        this.myPostsSl = myPostsSl;
        this.tableName = tableName;
        this.flag = flag;
        this.session = session;

        dbHelper = new DBHelper(context);
        finalVariables = new FinalVariables();
        customImageConverter = new CustomImageConverter();
        customAnimation = new CustomAnimation(context);
        customAlertDialogBox = new CustomAlertDialogBox(context);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(context);
        pd = new ProgressDialog(context);

        requestQueue = Volley.newRequestQueue(context);

        if (flag == FinalVariables.FLAG_STUDENT) postmanAllInfo = dbHelper.getUserInfo();
        else if (flag == FinalVariables.FLAG_TEACHER) postmanAllInfo = dbHelper.getTeacherInfo();
    }

    public View getView(int position, View view, ViewGroup parent) {
        inflater=context.getLayoutInflater();
        rowView=inflater.inflate(R.layout.layout_single_post_view, null,true);

        int backgrounds[]={R.drawable.rounded_corner,R.drawable.rounded_corner1};

        linearLayoutSinglePost = (LinearLayout) rowView.findViewById(R.id.linearLayoutSinglePost);
        imageViewPostmanImage = (ImageView) rowView.findViewById(R.id.imageViewPostmanImage);
        textViewPostmanName = (TextView) rowView.findViewById(R.id.textViewPostmanName);
        textViewPostDate = (TextView) rowView.findViewById(R.id.textViewPostDate);
        textViewPostMessage = (TextView) rowView.findViewById(R.id.textViewPostMessage);
        buttonEditPost = (Button) rowView.findViewById(R.id.buttonEditPost);
        buttonDeletePost = (Button) rowView.findViewById(R.id.buttonDeletePost);

        linearLayoutSinglePost.setBackgroundResource(backgrounds[position%2]);
        linearLayoutSinglePost.setPadding(20,20,12,20);

        editTextComment = (EmojiconEditText) rowView.findViewById(R.id.editTextComment);
        imageViewEmojIconKeyboard = (ImageView) rowView.findViewById(R.id.imageViewEmojIconKeyboard);
        imgViewSaveCommentBtn = (ImageView) rowView.findViewById(R.id.imageViewSaveComment);
        linearLayoutAllComments = (LinearLayout) rowView.findViewById(R.id.linearLayoutSinglePost);

        emojIconActions = new EmojIconActions(context, linearLayoutAllComments, editTextComment, imageViewEmojIconKeyboard);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setIconsIds(R.drawable.icon_keyboard3, R.drawable.icon_emo_btn3);

        imageViewPostmanImage.setVisibility(View.GONE);
        //setPostmanImage(imageViewPostmanImage, allCourseCode.get(position));
        textViewPostmanName.setText(allPostmenName.get(position));CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(allPostsDate.get(position)),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        textViewPostDate.setText(timeAgo);

        String message = "Course Code: "+allCourseCode.get(position)+"\n"
                +"Course Title: "+allCourseTitle.get(position)+"\n\n"
                +"Course Teacher: "+allCourseTeacher.get(position)+"\n\n"
                +"Class Room: "+allClassRoom.get(position)+"\n"
                +"Class Time: "+allClassTime.get(position)+"\n\n"
                +"Description: "+allDescription.get(position)+"\n";
        textViewPostMessage.setText(message);

        buttonEditPost.setTag(position+"");
        buttonDeletePost.setTag(position+"");
        buttonEditPost.setOnClickListener(this);
        buttonDeletePost.setOnClickListener(this);

        for (int i = 0; i<myPostsSl.size(); i++){
            if(allPostsSl.get(position)==myPostsSl.get(i)){
                buttonEditPost.setVisibility(View.VISIBLE);
                buttonDeletePost.setVisibility(View.VISIBLE);
                //Toast.makeText(context, "You have a post!", Toast.LENGTH_SHORT).show();
                //myPostsSl.remove(i);
                //break;
            }
        }

        return rowView;

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        Log.e("Post sl: ",""+allPostsSl.get(position));

        tableName=tableName.toLowerCase();

        switch (v.getId()){
            case R.id.buttonEditPost:
                Log.e("Edit Button: ",""+v.getId());
                //Toast.makeText(context, "Post SL "+allPostsSl.get(position), Toast.LENGTH_SHORT).show();
                courseCode = allCourseCode.get(position);
                courseTitle = allCourseTitle.get(position);
                courseTeacher  = allCourseTeacher.get(position);
                classRoom = allClassRoom.get(position);
                classTime = allClassTime.get(position);
                description = allDescription.get(position);
                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                break;

            case R.id.buttonDeletePost:
                Log.e("Delete Button: ",""+v.getId());
                //Toast.makeText(context, "Post SL "+allPostsSl.get(position), Toast.LENGTH_SHORT).show();
                getNewInput(FLAG_SCHEDULE_POST_DELETE, tableName, position);
                break;

            default:
                break;
        }
    }

    protected void getNewInput(final int flag, final String tableName, final int position){
        alertDialogBuilder = new AlertDialog.Builder(context);
        String positiveButtonTitle = "Yes";
        // get layout_developer.xml view
        if (flag == FLAG_SCHEDULE_POST_UPDATE){
            rowView = inflater.inflate(R.layout.layout_schedule_post_input, null);
            editTextCourseCode = (EditText) rowView.findViewById(R.id.editTextCourseCode);
            editTextCourseTitle = (EditText) rowView.findViewById(R.id.editTextCourseTitle);
            editTextCourseTeacher = (EditText) rowView.findViewById(R.id.editTextCourseTeacher);
            editTextClassRoom = (EditText) rowView.findViewById(R.id.editTextClassRoom);
            editTextDescription = (EditText) rowView.findViewById(R.id.editTextDescription);
            timePickerClassTime = (TimePicker) rowView.findViewById(R.id.timePickerClassTime);

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

            alertDialogBuilder.setView(rowView);
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
                                Toast.makeText(context, "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                            }else if(description.length()>250){
                                Toast.makeText(context, "Too Long Description(Max length 250 word)",
                                        Toast.LENGTH_SHORT).show();
                                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                            }else getNewInput(FLAG_SCHEDULE_POST_REVIEW, tableName, position);

                        }else if(flag == FLAG_SCHEDULE_POST_REVIEW){
                            if(courseTitle.isEmpty() || classTime.isEmpty()){
                                Toast.makeText(context, "Course title and Class time can't be empty",
                                        Toast.LENGTH_SHORT).show();
                                getNewInput(FLAG_SCHEDULE_POST_UPDATE, tableName, position);
                            }else if(description.length()>250){
                                Toast.makeText(context, "Too Long Description(Max length 250 word)",
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
        pd = new ProgressDialog(context);
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
                    Toast.makeText(context, "No server response", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                //Creating parameters
                Map<String,String> params = new Hashtable<>();

                //Adding parameters
                params.put("sl", allPostsSl.get(position));

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
                    allCourseCode.add(position, courseCode);
                    allCourseTitle.add(position, courseTitle);
                    allCourseTeacher.add(position, courseTeacher);
                    allClassRoom.add(position, classRoom);
                    allClassTime.add(position, classTime);
                    allDescription.add(position, description);
                    notifyDataSetChanged();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    message = jsonObject.getString("data");
                    Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                }else if(flag== FLAG_SCHEDULE_POST_DELETE){
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    allPostmenName.remove(position);
                    allPostmenEmail.remove(position);
                    allPostsSl.remove(position);
                    allCourseCode.remove(position);
                    allCourseTitle.remove(position);
                    allCourseTeacher.remove(position);
                    allClassRoom.remove(position);
                    allClassTime.remove(position);
                    allPostsDate.remove(position);
                    allDescription.remove(position);
                    notifyDataSetChanged();
                }

            }else { //means error
                message = jsonObject.getString(FinalVariables.KEY_MESSAGE);
                success = FinalVariables.FAILURE;
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                message = jsonObject.getString("data");
                Toast.makeText(context, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
            }
            Log.d("Message: ",message);
            Log.d("Success: ",success);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
