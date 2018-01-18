package com.project.blackspider.classschedule.FinalClasses;

import com.project.blackspider.classschedule.Activities.MainActivity;

/**
 * Created by Mr blackSpider on 12/15/2016.
 */

public final class FinalVariables {
    public static final String DATABASE_NAME = "class_schedule.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_USER_DATA = "user_data";
    public static final String TABLE_TEACHER_DATA = "teacher_data";
    public static final String TABLE_LAST_SIGN_IN_INFO = "last_sign_in_info";
    public static final String TABLE_SINGLE_CHAT_MESSAGES = "chat_with_";
    public static final String TABLE_CHAT_LIST = "_chat_list";

    public static final String KEY_SL = "_SL";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_REG = "reg";
    public static final String KEY_FACULTY = "faculty";
    public static final String KEY_SESSION = "session";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_OFFLINE_IMAGE_PATH = "offline_image_path";
    public static final String KEY_FCM_DEVICE_REG_ID = "fcm_device_reg_id";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_IMAGE_NAME = "image_name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_DESIGNATION = "designation";
    public static final String KEY_DEPARTMENT = "department";
    public static final String KEY_SENT = "sent";
    public static final String KEY_DELIVERED = "delivered";
    public static final String KEY_SEEN = "seen";
    public static final String KEY_DATE = "date";
    public static final String KEY_LAST_MESSAGE = "last_message";
    public static final String KEY_LAST_CHAT_DATE = "last_chat_date";

    public static final String CREATE_TABLE_USER_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_DATA + " ("
            + KEY_SL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_ID + " TEXT, "
            + KEY_REG + " TEXT, "
            + KEY_FACULTY + " TEXT, "
            + KEY_SESSION + " TEXT, "
            + KEY_PHONE + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_PASSWORD + " TEXT, "
            + KEY_IMAGE_PATH + " TEXT, "
            + KEY_FCM_DEVICE_REG_ID + " TEXT, "
            + KEY_STATUS + " TEXT" + ")";

    public static final String CREATE_TABLE_TEACHER_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_TEACHER_DATA + " ("
            + KEY_SL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_DESIGNATION + " TEXT, "
            + KEY_DEPARTMENT + " TEXT, "
            + KEY_FACULTY + " TEXT, "
            + KEY_PHONE + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_PASSWORD + " TEXT, "
            + KEY_FCM_DEVICE_REG_ID + " TEXT, "
            + KEY_STATUS + " TEXT" + ")";

    public static final String CREATE_TABLE_LAST_SIGN_IN_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_LAST_SIGN_IN_INFO + " ("
            + KEY_SL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_ID + " TEXT, "
            + KEY_REG + " TEXT, "
            + KEY_FACULTY + " TEXT, "
            + KEY_SESSION + " TEXT, "
            + KEY_PHONE + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_PASSWORD + " TEXT, "
            + KEY_IMAGE_PATH + " TEXT, "
            + KEY_FCM_DEVICE_REG_ID + " TEXT, "
            + KEY_STATUS + " TEXT" + ")";

    public static final String QUERY_GET_IMAGE_PATH = "SELECT " + KEY_IMAGE_PATH + " FROM " + TABLE_USER_DATA +
            " WHERE " + KEY_SL + "=1" + ";";

    public static final String QUERY_GET_ALL_INFO = "SELECT * FROM " + TABLE_USER_DATA +
            " WHERE " + KEY_SL + "=1" + ";";

    public static final String QUERY_GET_TEACHER_ALL_INFO = "SELECT * FROM " + TABLE_TEACHER_DATA +
            " WHERE " + KEY_SL + "=1" + ";";

    public static final String QUERY_GET_LAST_SIGN_IN_INFO = "SELECT * FROM " + TABLE_LAST_SIGN_IN_INFO +
            " WHERE " + KEY_SL + "=1" + ";";

    public static final String TAB_ONE = "News feed";
    public static final String TAB_TWO = "Schedules";
    public static final String TAB_THREE = "Chat";

    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";

    public static final String SENT_MESSAGE = "sent";
    public static final String RECEIVED_MESSAGE = "received";

    public static final String DELIVERY_REPORT_SENT = "sent";
    public static final String DELIVERY_REPORT_FAILED = "failed";


    public static final String SIGN_IN_URL = "http://www.businessclubpstu.com/json_request/class_schedule/check_sign_in_validity.php";
    public static final String TEACHER_SIGN_IN_URL = "http://www.businessclubpstu.com/json_request/class_schedule/check_teacher_sign_in_validity.php";
    public static final String SIGN_UP_URL = "http://www.businessclubpstu.com/json_request/class_schedule/add_student.php";
    public static final String TEACHER_SIGN_UP_URL = "http://www.businessclubpstu.com/json_request/class_schedule/add_teacher.php";
    public static final String EMAIL_VALIDATION_URL = "http://www.businessclubpstu.com/json_request/class_schedule/check_email_validity.php";
    public static final String TEACHER_EMAIL_VALIDATION_URL = "http://www.businessclubpstu.com/json_request/class_schedule/check_teacher_email_validity.php";
    public static final String SEND_PUSH_NOTIFICATION_URL = "http://www.businessclubpstu.com/json_request/class_schedule/send_fcm_push_notification.php";
    public static final String SEND_BROADCAST_MESSAGE_URL = "http://www.businessclubpstu.com/json_request/class_schedule/send_broadcast_message.php";
    public static final String GET_ALL_NEWS_FEED_POSTS_URL = "http://www.businessclubpstu.com/json_request/class_schedule/get_all_news_feed_posts.php";
    public static final String GET_All_SCHEDULE_POSTS_URL = "http://www.businessclubpstu.com/json_request/class_schedule/get_all_schedule_posts.php";
    public static final String GET_CHAT_LIST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/get_chat_list.php";
    public static final String IMAGE_UPLOAD_URL = "http://www.businessclubpstu.com/json_request/class_schedule/upload_stu_image.php";
    public static final String IMAGE_CHANGE_URL = "http://www.businessclubpstu.com/json_request/class_schedule/change_stu_image.php";
    public static final String IMAGE_PATH = "http://www.businessclubpstu.com/json_request/class_schedule/stu_images/";
    public static final String CHANGE_NAME_URL = "http://www.businessclubpstu.com/json_request/class_schedule/change_name.php";
    public static final String CHANGE_PASSWORD_URL = "http://www.businessclubpstu.com/json_request/class_schedule/change_password.php";
    public static final String CHANGE_PHONE_NUMBER_URL = "http://www.businessclubpstu.com/json_request/class_schedule/change_phone_number.php";
    public static final String CHANGE_STATUS_URL = "http://www.businessclubpstu.com/json_request/class_schedule/change_status.php";
    public static final String DELETE_ACCOUNT_URL = "http://www.businessclubpstu.com/json_request/class_schedule/delete_account.php";
    public static final String UPDATE_FCM_DEVICE_REG_ID_URL = "http://www.businessclubpstu.com/json_request/class_schedule/update_fcm_device_reg_id.php";
    public static final String ADD_NEWS_FEED_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/add_news_feed_post.php";
    public static final String ADD_SCHEDULE_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/add_schedule_post.php";
    public static final String UPDATE_NEWS_FEED_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/update_news_feed_post.php";
    public static final String UPDATE_SCHEDULE_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/update_schedule_post.php";
    public static final String DELETE_NEWS_FEED_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/delete_news_feed_post.php";
    public static final String DELETE_SCHEDULE_POST_URL = "http://www.businessclubpstu.com/json_request/class_schedule/delete_schedule_post.php";
    public static final String GET_SINGLE_CHAT_MESSAGES_URL = "http://www.businessclubpstu.com/json_request/class_schedule/get_single_chat_messages.php";
    public static final String GET_BROADCAST_MESSAGES_URL = "http://www.businessclubpstu.com/json_request/class_schedule/get_broadcast_messages.php";
    public static final String OFFLINE_IMAGE_PATH = "android.resource://com.project.blackspider.classschedule/drawable/";

    public static final String FACULTY = "Faculty";
    public static final String FACULTY_AG = "AG";
    public static final String FACULTY_CSE = "CSE";
    public static final String FACULTY_BAM = "BAM";
    public static final String FACULTY_FISH = "FISH";
    public static final String FACULTY_DM = "DM";
    public static final String FACULTY_NFS = "NFS";
    public static final String FACULTY_LM = "LM";
    public static final String FACULTY_DVM = "DVM";
    public static final String FACULTY_AH = "AH";
    public static final String FACULTY_NOT_LISTED = "Not Listed";

    public static final String SESSION = "Session";

    // LogCat tag
    public static final String TAG = MainActivity.class.getSimpleName();


    // Camera activity request codes
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int GALLERY_IMAGE_REQUEST_CODE = 200;
    public static final int AVATER_IMAGE_REQUEST_CODE = 300;
    public static final int GALLERY_ACCESS_PERMISSION_REQUEST_CODE = 400;

    public static final int REQUEST_TYPE_SAVE_DATA = 1;
    public static final int REQUEST_TYPE_EMAIL_VALIDITY_CHECK = 2;

    public static final Integer NAME_INDEX = 0;
    public static final Integer ID_INDEX = 1;
    public static final Integer REG_INDEX = 2;
    public static final Integer FACULTY_INDEX = 3;
    public static final Integer SESSION_INDEX = 4;
    public static final Integer PHONE_INDEX = 5;
    public static final Integer EMAIL_INDEX = 6;
    public static final Integer PASSWORD_INDEX = 7;
    public static final Integer IMAGE_PATH_INDEX = 8;
    public static final Integer FCM_DEVICE_REG_ID_INDEX = 9;
    public static final Integer STATUS_INDEX = 10;

    public static final int TEACHER_NAME_INDEX = 0;
    public static final int TEACHER_DESIGNATION_INDEX = 1;
    public static final int TEACHER_DEPARTMENT_INDEX = 2;
    public static final int TEACHER_FACULTY_INDEX = 3;
    public static final int TEACHER_PHONE_INDEX = 4;
    public static final int TEACHER_EMAIL_INDEX = 5;
    public static final int TEACHER_PASSWORD_INDEX = 6;
    public static final int TEACHER_DEVICE_ID_INDEX = 7;
    public static final int TEACHER_STATUS_INDEX = 8;

    public static final String IS_SESSION_EXIST = "is_session_exist";
    public static final String IS_NEW_FCM_ID_EXIST = "is_new_fcm_id_exist";
    public static final String IS_FEED_POST_SL_EXIST = "is_feed_post_sl_exist";
    public static final String IS_STUDENT = "is_student";
    public static final String IS_IMAGE_SELECTED = "is_image_selected";
    public static final String IS_IMAGE_UPLOADED = "is_image_uploaded";

    public static final String SHARED_PREFERENCES_SIGN_IN_INFO = "sign_in_info";
    public static final String SHARED_PREFERENCES_IMAGE_SELECTION_INFO = "image_selection_info";
    public static final String SHARED_PREFERENCES_POST_SL_INFO = "post_sl_info";
    public static final String SHARED_PREFERENCES_FEED_POST_SL_INFO = "feed_post_sl_info";
    public static final String SHARED_PREFERENCES_NEW_FCM_ID_INFO = "new_fcm_id_info";

    public static final String API_ACCESS_KEY = "AIzaSyBgHbr3fbCi-tRy_EeW4Wwb11bLGnxW66A";

    public static final int FLAG_SINGLE_CHAT = 0;
    public static final int FLAG_GROUP_CHAT = 1;
    public static final int FLAG_GET_BROADCAST_MESSAGES = 2;
    public static final int FLAG_SEND_BROADCAST_MESSAGE = 3;
    public static final int FLAG_STUDENT = 0;
    public static final int FLAG_TEACHER = 1;


    public static final int FLAG_PREVIEW_AND_UPLOAD_PHOTO = 0;
    public static final int FLAG_PREVIEW_PHOTO = 1;

    public FinalVariables(){

    }
}
