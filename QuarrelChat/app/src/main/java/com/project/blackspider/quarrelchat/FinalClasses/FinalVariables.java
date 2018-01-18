package com.project.blackspider.quarrelchat.FinalClasses;


import com.project.blackspider.quarrelchat.Activities.WelcomeActivity;

/**
 * Created by Mr blackSpider on 12/15/2016.
 */

public final class FinalVariables {
    public static final String DATABASE_NAME = "soulmate.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_USER_DATA = "user_data";
    public static final String TABLE_LAST_SIGN_IN_INFO = "last_sign_in_info";
    public static final String TABLE_SINGLE_CHAT_MESSAGES = "_with_";
    public static final String TABLE_CHAT_LIST = "_chat_list";

    public static final String KEY_SL = "_SL";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_FCM_ID = "fcm_id";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_NAME = "image_name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_REPORT = "report";
    public static final String KEY_SENT = "sent";
    public static final String KEY_DELIVERED = "delivered";
    public static final String KEY_SEEN = "seen";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_LAST_MESSAGE = "last_message";
    public static final String KEY_LAST_CHAT_TIMESTAMP = "last_chat_timestamp";

    public static final String CREATE_TABLE_USER_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_DATA + " ("
            + KEY_SL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_USERNAME + " TEXT, "
            + KEY_NAME + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_PHONE + " TEXT, "
            + KEY_PASSWORD + " TEXT, "
            + KEY_IMAGE_PATH + " TEXT, "
            + KEY_FCM_ID + " TEXT" + ")";

    public static final String CREATE_TABLE_LAST_SIGN_IN_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_LAST_SIGN_IN_INFO + " ("
            + KEY_SL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_USERNAME + " TEXT, "
            + KEY_NAME + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_PHONE + " TEXT, "
            + KEY_PASSWORD + " TEXT, "
            + KEY_IMAGE_PATH + " TEXT, "
            + KEY_FCM_ID + " TEXT" + ")";

    public static final String QUERY_GET_IMAGE_PATH = "SELECT " + KEY_IMAGE_PATH + " FROM " + TABLE_USER_DATA +
            " WHERE " + KEY_SL + "=1" + ";";

    public static final String QUERY_GET_ALL_INFO = "SELECT * FROM " + TABLE_USER_DATA +
            " WHERE " + KEY_SL + "=1" + ";";

    public static final String QUERY_GET_LAST_SIGN_IN_INFO = "SELECT * FROM " + TABLE_LAST_SIGN_IN_INFO +
            " WHERE " + KEY_SL + "=1" + ";";

    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";

    public static final String LOG_IN_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_check_login_validity.php";
    public static final String USERNAME_VALIDITY_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_check_username_validity.php";
    public static final String EMAIL_VALIDITY_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_check_email_validity.php";
    public static final String PHONE_VALIDITY_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_check_phone_validity.php";
    public static final String SIGN_UP_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_soul_signup.php";
    public static final String SEARCH_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_soul_search.php";
    public static final String SEND_SOUL_REQUEST_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_soul_request_send.php";
    public static final String ACCEPT_SOUL_REQUEST_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_soul_request_accept.php";
    public static final String CANCEL_SOUL_REQUEST_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_soul_request_cancel.php";
    public static final String REMOVE_SOULMATE_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_soul_remove.php";
    public static final String GET_SOULMATES_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_get_friend_list.php";
    public static final String GET_NOTIFICATIONS_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_get_notification.php";
    public static final String SEND_PUSH_NOTIFICATION_URL = "http://www.businessclubpstu.com/json_request/class_schedule/send_fcm_push_notification.php";
    public static final String GET_SOULMATE_INFO = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_get_account_info.php";
    public static final String SEND_CHAT_MESSAGE = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_message_send.php";
    public static final String IMAGE_UPLOAD_URL = "http://www.businessclubpstu.com/json_request/class_schedule/upload_stu_image.php";
    public static final String IMAGE_UPDATE_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_update_image.php";
    public static final String ONLINE_IMAGE_PATH = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_images/";
    public static final String CHANGE_NAME_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_update_name.php";
    public static final String CHANGE_PASSWORD_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_update_password.php";
    public static final String CHANGE_PHONE_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_update_phone.php";
    public static final String CHANGE_STATUS_URL = "http://www.businessclubpstu.com/json_request/class_schedule/change_status.php";
    public static final String DELETE_ACCOUNT_URL = "http://www.businessclubpstu.com/json_request/class_schedule/delete_account.php";
    public static final String UPDATE_FCM_ID_URL = "http://www.businessclubpstu.com/json_request/soulmate/soulmate_update_fcm_id.php";
    public static final String ADD_NEWS_FEED_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/add_news_feed_post.php";
    public static final String ADD_SCHEDULE_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/add_schedule_post.php";
    public static final String UPDATE_NEWS_FEED_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/update_news_feed_post.php";
    public static final String UPDATE_SCHEDULE_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/update_schedule_post.php";
    public static final String DELETE_NEWS_FEED_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/delete_news_feed_post.php";
    public static final String DELETE_SCHEDULE_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/delete_schedule_post.php";
    public static final String GET_SINGLE_CHAT_MESSAGES_URL = "http://www.businessclubpstu.com/json_request/class_schedule/get_single_chat_messages.php";
    public static final String GET_BROADCAST_MESSAGES_URL = "http://www.businessclubpstu.com/json_request/class_schedule/get_broadcast_messages.php";
    public static final String OFFLINE_IMAGE_PATH = "android.resource://com.project.blackspider.quarrelchat/drawable/";


    public static final String SESSION = "Session";

    // LogCat tag
    public static final String TAG = WelcomeActivity.class.getSimpleName();


    // Camera activity request codes
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int GALLERY_IMAGE_REQUEST_CODE = 200;
    public static final int AVATER_IMAGE_REQUEST_CODE = 300;
    public static final int GALLERY_ACCESS_PERMISSION_REQUEST_CODE = 400;
    public static final int MEDIA_ACCESS_PERMISSION_REQUEST_CODE = 500;

    public static final int REQUEST_TYPE_SAVE_DATA = 1;
    public static final int REQUEST_TYPE_EMAIL_VALIDITY_CHECK = 2;

    public static final Integer MY_USERNAME_INDEX = 0;
    public static final Integer MY_NAME_INDEX = 1;
    public static final Integer MY_EMAIL_INDEX = 2;
    public static final Integer MY_PHONE_INDEX = 3;
    public static final Integer MY_PASSWORD_INDEX = 4;
    public static final Integer MY_IMAGE_PATH_INDEX = 5;
    public static final Integer MY_FCM_ID_INDEX = 6;
    public static final Integer MY_STATUS_INDEX = 7;

    public static final String IS_SESSION_EXIST = "soulmate_is_session_exist";
    public static final String IS_NEW_FCM_ID_EXIST = "soulmate_is_new_fcm_id_exist";
    public static final String IS_IMAGE_SELECTED = "soulmate_is_image_selected";
    public static final String IS_IMAGE_UPLOADED = "soulmate_is_image_uploaded";
    public static final String IS_AVAILABLE = "soulmate_is_available";
    public static final String IS_ONLINE = "soulmate_is_online";
    public static final String IS_TYPING = "soulmate_is_typing";
    public static final String IS_CHATTING = "soulmate_is_chatting";
    public static final String UNSEEN_MSG = "soulmate_unseen_msg";

    public static final String SHARED_PREFERENCES_SIGN_IN_INFO = "soulmate_sign_in_info";
    public static final String SHARED_PREFERENCES_IMAGE_SELECTION_INFO = "soulmate_image_selection_info";
    public static final String SHARED_PREFERENCES_LAYOUT_TYPE = "soulmate_layout_type";
    public static final String SHARED_PREFERENCES_WALLPAPER = "soulmate_wallpaper";
    public static final String SHARED_PREFERENCES_IMAGE_UPDATE_SIGNATURE= "soulmate_img_update_signature";
    public static final String IMAGE_SIGNATURE= "soulmate_img_signature";
    public static final String SHARED_PREFERENCES_NEW_FCM_ID_INFO = "soulmate_new_fcm_id_info";

    public static final String Legacy_SERVER_KEY = "AIzaSyAR1tt2QN9gmILRobdDsV8qzZfth3FJZgw";
    public static final String FCM_PUSH_URL = "https://fcm.googleapis.com/fcm/send";

    public static final int FLAG_SINGLE_CHAT = 0;
    public static final int FLAG_GROUP_CHAT = 1;
    public static final int FLAG_GET_BROADCAST_MESSAGES = 2;
    public static final int FLAG_SEND_BROADCAST_MESSAGE = 3;

    public static final String SUBJECT_NOTIFICATION = "notification";
    public static final String SUBJECT_CHAT = "chat";
    public static final String SUBJECT_STATUS = "status";
    public static final String SUBJECT_REPORT = "report";

    public static final int NOTIFICATION_ID = 110011;
    public static final int CHAT_NOTIFICATION_ID = 110022;
    public static final int STATUS_NOTIFICATION_ID = 110033;

    public static final int TYPE_GRID_LAYOUT = 0;
    public static final int TYPE_LINEAR_LAYOUT = 1;
    public static final int TYPE_EXTENDED_LINEAR_LAYOUT = 2;
    public static final int TYPE_EXTENDED_LINEAR_LAYOUT_LEFT = 3;
    public static final int TYPE_EXTENDED_LINEAR_LAYOUT_RIGHT = 4;
    //Email Validation pattern
    public static final String EmailRegEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    public static final String UsernameRegEx = "\\p{L}+(?:[\\W_]\\p{L}+)*";

    public static final int FLAG_USERNAME_CHECK = 0;
    public static final int FLAG_EMAIL_CHECK = 1;
    public static final int FLAG_PHONE_CHECK = 2;
    public static final int FLAG_SIGNUP = 3;


    public static final int FLAG_SOUL_SEARCH = 0;
    public static final int FLAG_SOUL_REQUEST_SEND = 1;
    public static final int FLAG_SOUL_REQUEST_RECEIVED = 2;
    public static final int FLAG_SOUL_REQUEST_ACCEPT = 3;
    public static final int FLAG_SOUL_REQUEST_CANCEL = 4;
    public static final int FLAG_GET_NOTIFICATIONS = 5;
    public static final int FLAG_GET_SOULMATES = 6;
    public static final int FLAG_GET_SOULMATE_INFO = 7;
    public static final int FLAG_SEND_CHAT_MESSAGE = 8;
    public static final int FLAG_UPDATE_IMAGE = 9;
    public static final int FLAG_UPDATE_FCM_ID = 10;
    public static final int FLAG_LOGOUT = 11;
    public static final int FLAG_SOUL_REMOVE = 12;
    public static final int FLAG_CHANGE_NAME = 13;
    public static final int FLAG_CHANGE_PHONE = 14;
    public static final int FLAG_CHANGE_PASSWORD = 15;
    public static final int FLAG_UPDATE_MSG_REPORT_ALL_DELIVERED = -1;
    public static final int FLAG_UPDATE_MSG_REPORT_ALL_SEEN = -2;


    public static final String SOUL_REQUEST_FROM_ME = "REQUEST_FROM_ME";
    public static final String SOUL_REQUEST_TO_ME = "REQUEST_TO_ME";
    public static final String SOUL_REQUEST_SENT = "REQUEST_SENT";
    public static final String SOUL_REQUEST_RECEIVED = "REQUEST_RECEIVED";
    public static final String SOUL_REQUEST_ACCEPTED = "REQUEST_ACCEPTED";
    public static final String SOUL_REQUEST_CANCELED = "REQUEST_CANCELED";

    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";
    public static final String Soulmates_Fragment = "Soulmates_Fragment";
    public static final String Search_Soulmates_Fragment = "Search_Soulmates_Fragment";
    public static final String Notification_Fragment = "Notification_Fragment";
    public static final String Account_Fragment = "Account_Fragment";

    public static final int INTERNAL_TEXT_MSG=0;
    public static final int EXTERNAL_TEXT_MSG=1;
    public static final int INTERNAL_IMAGE_MSG=2;
    public static final int EXTERNAL_IMAGE_MSG=3;
    public static final int INTERNAL_VOICE_MSG=4;
    public static final int EXTERNAL_VOICE_MSG=5;

    public static final int MSG_SENDING=0;
    public static final int MSG_SENT=1;
    public static final int MSG_DELIVERED=2;
    public static final int MSG_RECEIVED=3;
    public static final int MSG_SEEN=4;
    public static final int MSG_FAILED=5;

    public static final String ACTION_CHAT_INTENT = "com.project.blackspider.quarrelchat.CHAT_INTENT";
    public static final String ACTION_REPORT_INTENT = "com.project.blackspider.quarrelchat.REPORT_INTENT";
    public static final String ACTION_STATUS_INTENT = "com.project.blackspider.quarrelchat.STATUS_INTENT";
    public static final String ACTION_NOTIFICATION_INTENT = "com.project.blackspider.quarrelchat.NOTIFICATION_INTENT";

    public static final int SOUL_REQUEST_NOTIFICATION=0;

    public FinalVariables(){

    }
}
