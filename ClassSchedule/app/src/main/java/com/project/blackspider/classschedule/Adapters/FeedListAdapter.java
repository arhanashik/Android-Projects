package com.project.blackspider.classschedule.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Mr blackSpider on 7/29/2017.
 */


import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.project.blackspider.classschedule.App.AppController;
import com.project.blackspider.classschedule.CustomAlertDialogs.CustomAlertDialogBox;
import com.project.blackspider.classschedule.CustomView.FeedImageView;
import com.project.blackspider.classschedule.DataBase.DBHelper;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;
import com.project.blackspider.classschedule.Models.FeedItem;
import com.project.blackspider.classschedule.NewsFeed.NewsFeedPost;
import com.project.blackspider.classschedule.R;
import com.project.blackspider.classschedule.Utils.CustomAnimation;
import com.project.blackspider.classschedule.Utils.CustomButtonPressedStateChanger;
import com.project.blackspider.classschedule.Utils.CustomImageConverter;
import com.project.blackspider.classschedule.Utils.LruBitmapCache;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class FeedListAdapter extends BaseAdapter implements View.OnClickListener{
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
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

    private CustomImageConverter customImageConverter;
    private CustomAnimation customAnimation;
    private DBHelper dbHelper;
    private FinalVariables finalVariables;
    private CustomAlertDialogBox customAlertDialogBox;
    private CustomButtonPressedStateChanger customButtonPressedStateChanger;
    private NewsFeedPost newsFeedPost;

    //private ImageLoader imageLoader;
    private RequestQueue requestQueue;

    private static final int FLAG_NEWS_FEED_POST_UPDATE = 0;
    private static final int FLAG_NEWS_FEED_POST_DELETE = 1;

    int position = -1;

    private String userInput;
    private String tableName;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems, ArrayList<String> myPostsSl, LinearLayout slider) {
        this.activity = activity;
        this.feedItems = feedItems;
        this.myPostsSl = myPostsSl;
        this.slider = slider;

        dbHelper = new DBHelper(activity);
        finalVariables = new FinalVariables();
        customImageConverter = new CustomImageConverter();
        customAnimation = new CustomAnimation(activity);
        customAlertDialogBox = new CustomAlertDialogBox(activity);
        customButtonPressedStateChanger = new CustomButtonPressedStateChanger(activity);

        requestQueue = Volley.newRequestQueue(activity);

        postmanAllInfo = dbHelper.getUserInfo();
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
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

        FeedItem item = feedItems.get(position);

        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
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
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
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
        final FeedItem item = feedItems.get(position);
        Log.e("Post sl: ",""+item.getSl());
        String session = postmanAllInfo.get(FinalVariables.SESSION_INDEX).replace("-","_");
        tableName = "table_news_feed_"+postmanAllInfo.get(FinalVariables.FACULTY_INDEX).toLowerCase()+"_"+session;

        editPost = (LinearLayout) slider.findViewById(R.id.edit);
        deletePost = (LinearLayout) slider.findViewById(R.id.delete);
        hidePost = (LinearLayout) slider.findViewById(R.id.hide);
        editPost.setVisibility(View.GONE);
        deletePost.setVisibility(View.GONE);
        editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsFeedPost = new NewsFeedPost(activity, tableName, item.getSl(), item.getStatus());
                newsFeedPost.getNewInput(FLAG_NEWS_FEED_POST_UPDATE);
                customAnimation.animDown(slider);
                convertView.setAlpha(1.0f);
            }
        });
        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsFeedPost = new NewsFeedPost(activity, tableName, item.getSl(), item.getStatus());
                newsFeedPost.getNewInput(FLAG_NEWS_FEED_POST_DELETE);
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
                    convertView.setAlpha(0.2f);
                    customAnimation.animUp(slider);
                    for (int i = 0; i<myPostsSl.size(); i++){
                        if(item.getSl()==myPostsSl.get(i)){
                            editPost.setVisibility(View.VISIBLE);
                            deletePost.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }else {
                    convertView.setAlpha(1.0f);
                    customAnimation.animDown(slider);
                }
                break;

            default:
                break;
        }
    }

    private void setPostmanImage(final ImageView imgView, final String url){
        Bitmap bmap;
        bmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_loading1);
        //bitmap = customImageConverter.getCircledBitmap(bitmap);
        imgView.setImageBitmap(bmap);
        customAnimation.roll(imgView);

        imageLoader = new ImageLoader(this.requestQueue,
                new LruBitmapCache());
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                if(isImmediate){
                    //Toast.makeText(getApplicationContext(), "Loading image", Toast.LENGTH_SHORT).show();
                }else {
                    Bitmap bm;
                    //Toast.makeText(getApplicationContext(), "Image loaded", Toast.LENGTH_SHORT).show();
                    bm = customImageConverter.getResizedBitmap(response.getBitmap(), 80);
                    //bitmap = customImageConverter.getCircledBitmap(bitmap);
                    imgView.clearAnimation();
                    imgView.setImageBitmap(bm);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(FinalVariables.TAG, "Volley Error: "+error.getMessage());
                Bitmap bm;
                bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icon_error);
                bm = customImageConverter.getResizedBitmap(bm, 80);
                //bitmap = customImageConverter.getCircledBitmap(bitmap);
                imgView.clearAnimation();
                imgView.setImageBitmap(bm);
            }
        });
    }

}