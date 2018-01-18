package com.project.blackspider.classschedule.DataBase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

import com.project.blackspider.classschedule.Models.Message;
import com.project.blackspider.classschedule.Models.Teacher;
import com.project.blackspider.classschedule.Models.User;
import com.project.blackspider.classschedule.FinalClasses.FinalVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr blackSpider on 12/22/2016.
 */

public class DBHelper extends SQLiteOpenHelper{
    private Context context;
    private FinalVariables finalVariables = new FinalVariables();

    private static final String DATABASE_NAME = "class_schedule.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FinalVariables.CREATE_TABLE_USER_DATA);
        db.execSQL(FinalVariables.CREATE_TABLE_LAST_SIGN_IN_INFO);
        db.execSQL(FinalVariables.CREATE_TABLE_TEACHER_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_USER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_TEACHER_DATA);
        // Creating tables again
        onCreate(db);
    }

    // Checking table has data or not
    public int getRowCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = -1;
        Cursor cursor  = db.rawQuery("SELECT * FROM " + tableName, null);

        count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    // Checking table has the given data or not
    public boolean isInserted(String tableName, String index, String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = -1;
        Cursor cursor  = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + index + "=\"" + value.trim()+"\";", null);

        count = cursor.getCount();
        cursor.close();
        db.close();

        if(count>0) return true;

        return false;
    }

    public void createChatListTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE_CHAT_LIST = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + finalVariables.KEY_SL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + finalVariables.KEY_NAME + " TEXT, "
                + finalVariables.KEY_ID + " TEXT, "
                + finalVariables.KEY_REG + " TEXT, "
                + finalVariables.KEY_FACULTY + " TEXT, "
                + finalVariables.KEY_SESSION + " TEXT, "
                + finalVariables.KEY_PHONE + " TEXT, "
                + finalVariables.KEY_EMAIL + " TEXT, "
                + finalVariables.KEY_IMAGE_PATH + " TEXT, "
                + finalVariables.KEY_OFFLINE_IMAGE_PATH + " TEXT, "
                + finalVariables.KEY_FCM_DEVICE_REG_ID + " TEXT, "
                + finalVariables.KEY_STATUS + " TEXT, "
                + finalVariables.KEY_LAST_MESSAGE + " TEXT, "
                + finalVariables.KEY_LAST_CHAT_DATE + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_CHAT_LIST);
        db.close();
    }

    public long saveChatList(User user, String tableName){
        //int curr_row = getRowCount(FinalVariables.TABLE_CHAT_LIST);
        boolean isInserted = isInserted(tableName, finalVariables.KEY_EMAIL, user.getEmail());

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_NAME, user.getName());
        values.put(FinalVariables.KEY_ID, user.getID());
        values.put(FinalVariables.KEY_REG, user.getReg());
        values.put(FinalVariables.KEY_FACULTY, user.getFaculty());
        values.put(FinalVariables.KEY_SESSION, user.getSession());
        values.put(FinalVariables.KEY_PHONE, user.getPhone());
        values.put(FinalVariables.KEY_EMAIL, user.getEmail());
        values.put(FinalVariables.KEY_IMAGE_PATH, user.getImage_path());
        values.put(FinalVariables.KEY_FCM_DEVICE_REG_ID, user.getFcmDeviceRegID());
        values.put(FinalVariables.KEY_STATUS, user.getStatus());

        if(!isInserted){
            // Inserting Row
            values.put(FinalVariables.KEY_OFFLINE_IMAGE_PATH, "");
            values.put(FinalVariables.KEY_LAST_MESSAGE, "");
            values.put(FinalVariables.KEY_LAST_CHAT_DATE, "0");
            res = db.insert(tableName, null, values);
        }else{
            res = db.update(tableName, values, FinalVariables.KEY_EMAIL + " = ?",
                    new String[]{user.getEmail()});
        }

        // Closing database connection
        db.close();

        Log.d("Chat list saved: ",user.getName());
        return res;
    }

    public int updateLastChatTimeAndMessage(String tableName, String email, String message, long time){
        int updated = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_LAST_MESSAGE, message);
        values.put(FinalVariables.KEY_LAST_CHAT_DATE, time+"");

        updated = db.update(tableName, values, FinalVariables.KEY_EMAIL + " = ?",
                new String[]{email});

        return updated;
    }

    public int updateOfflineImagePath(String tableName,String email, String offlineImagePath){
        int updated = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_OFFLINE_IMAGE_PATH, offlineImagePath);

        updated = db.update(tableName, values, FinalVariables.KEY_EMAIL + " = ?",
                new String[]{email});

        return updated;
    }

    // Getting chat list
    public ArrayList<User> getChatList(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<User> chatList = new ArrayList<>();
        User user;

        String query = "SELECT * FROM " + tableName + ";";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                do {
                    user = new User();
                    user.setName(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_NAME)));
                    user.setID(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_ID)));
                    user.setReg(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_REG)));
                    user.setFaculty(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FACULTY)));
                    user.setSession(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SESSION)));
                    user.setPhone(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PHONE)));
                    user.setEmail(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_EMAIL)));
                    user.setImage_path(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_IMAGE_PATH)));
                    user.setOffline_image_path(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_OFFLINE_IMAGE_PATH)));
                    user.setFcmDeviceRegID(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FCM_DEVICE_REG_ID)));
                    user.setStatus(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_STATUS)));
                    user.setLastMessage(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_LAST_MESSAGE)));
                    user.setDate(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_LAST_CHAT_DATE)));

                    Log.d("Chat list retrieving: ",user.getName());
                    chatList.add(user);

                }while (cursor.moveToNext());

            }
        }

        // return chat list
        cursor.close();
        db.close();
        return chatList;
    }

    public void createSingleChatMessagesTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        String CREATE_TABLE_SINGLE_CHAT_MESSAGE = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + FinalVariables.KEY_SL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FinalVariables.KEY_MESSAGE + " TEXT, "
                + FinalVariables.KEY_SENT + " TEXT, "
                + FinalVariables.KEY_DELIVERED + " TEXT, "
                + FinalVariables.KEY_SEEN + " TEXT, "
                + FinalVariables.KEY_DATE + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_SINGLE_CHAT_MESSAGE);
        db.close();
    }

    public boolean saveSingleChatMessage(String tableName, Message message){
        createSingleChatMessagesTable(tableName);
        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_MESSAGE, message.getMessage());
        values.put(FinalVariables.KEY_SENT, message.getSent());
        values.put(FinalVariables.KEY_DELIVERED, message.getDelivered());
        values.put(FinalVariables.KEY_SEEN, message.getSeen());
        values.put(FinalVariables.KEY_DATE, message.getDate());

        // Inserting Row
        res = db.insert(tableName, null, values);

        // Closing database connection
        db.close();

        if(res>0) {
            Log.i("Inserting message", "inserted in table: "+tableName);
            return true;
        }
        else {
            Log.i("Inserting message", "insertion failed in table: "+tableName);
            return false;
        }
    }

    // Getting single chat messages
    public List<Message> getSingleChatAllMessages(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Message> allMessages = new ArrayList<>();
        Message message;

        String query = "SELECT * FROM " + tableName + ";";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                do {
                    message = new Message();
                    message.setSl(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SL)));
                    message.setMessage(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_MESSAGE)));
                    message.setSent(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SENT)));
                    message.setDelivered(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_DELIVERED)));
                    message.setSeen(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SEEN)));
                    message.setDate(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_DATE)));

                    allMessages.add(message);
                }while (cursor.moveToNext());

            }
        }

        // return master_password
        cursor.close();
        db.close();
        return allMessages;
    }

    public boolean deleteSingleChatMessage(String tableName, String sl){
        SQLiteDatabase db = this.getWritableDatabase();

        int deleted = db.delete(tableName, FinalVariables.KEY_SL + " = ?",
                new String[] { sl });

        // Closing database connection
        db.close();

        return deleted > 0;
    }

    public void deleteAllSingleChatMessages(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        // Creating tables again
        onCreate(db);
        db.close();
    }

    public void dropSingleChatMessagesTable(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        db.close();
    }

    public long saveImagePath(String imagePath){
        int curr_row = getRowCount(FinalVariables.TABLE_USER_DATA);

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_IMAGE_PATH, imagePath);

        if(curr_row<1){
            // Inserting Row
            res = db.insert(FinalVariables.TABLE_USER_DATA, null, values);
        }else if(curr_row>0){
            res = db.update(FinalVariables.TABLE_USER_DATA, values, FinalVariables.KEY_SL + " = ?",
                    new String[]{"1"});
        }

        // Closing database connection
        db.close();

        return res;
    }


    // Getting master_password
    public String getImagePath() {
        SQLiteDatabase db = this.getReadableDatabase();
        String image_path = new String();
        Cursor cursor = db.rawQuery(FinalVariables.QUERY_GET_IMAGE_PATH, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                image_path = cursor.getString(cursor
                        .getColumnIndex(FinalVariables.KEY_IMAGE_PATH));

            }
        }

        // return master_password
        cursor.close();
        db.close();
        return image_path;
    }

    public long saveUserInfo(User user){
        int curr_row = getRowCount(FinalVariables.TABLE_USER_DATA);

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_NAME, user.getName());
        values.put(FinalVariables.KEY_ID, user.getID());
        values.put(FinalVariables.KEY_REG, user.getReg());
        values.put(FinalVariables.KEY_FACULTY, user.getFaculty());
        values.put(FinalVariables.KEY_SESSION, user.getSession());
        values.put(FinalVariables.KEY_PHONE, user.getPhone());
        values.put(FinalVariables.KEY_EMAIL, user.getEmail());
        values.put(FinalVariables.KEY_PASSWORD, user.getPassword());
        values.put(FinalVariables.KEY_IMAGE_PATH, user.getImage_path());
        values.put(FinalVariables.KEY_FCM_DEVICE_REG_ID, user.getFcmDeviceRegID());
        values.put(FinalVariables.KEY_STATUS, user.getStatus());

        if(curr_row<1){
            // Inserting Row
            res = db.insert(FinalVariables.TABLE_USER_DATA, null, values);
        }else if(curr_row>0){
            res = db.update(FinalVariables.TABLE_USER_DATA, values, FinalVariables.KEY_SL + " = ?",
                    new String[]{"1"});
        }

        // Closing database connection
        db.close();

        return res;
    }

    public long saveTeacherInfo(Teacher teacher){
        int curr_row = getRowCount(FinalVariables.TABLE_TEACHER_DATA);

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_NAME, teacher.getName());
        values.put(FinalVariables.KEY_DESIGNATION, teacher.getDesignation());
        values.put(FinalVariables.KEY_DEPARTMENT, teacher.getDepartment());
        values.put(FinalVariables.KEY_FACULTY, teacher.getFaculty());
        values.put(FinalVariables.KEY_PHONE, teacher.getPhone());
        values.put(FinalVariables.KEY_EMAIL, teacher.getEmail());
        values.put(FinalVariables.KEY_PASSWORD, teacher.getPassword());
        values.put(FinalVariables.KEY_FCM_DEVICE_REG_ID, teacher.getFcmDeviceRegID());
        values.put(FinalVariables.KEY_STATUS, teacher.getStatus());

        if(curr_row<1){
            // Inserting Row
            res = db.insert(FinalVariables.TABLE_TEACHER_DATA, null, values);
        }else if(curr_row>0){
            res = db.update(FinalVariables.TABLE_TEACHER_DATA, values, FinalVariables.KEY_SL + " = ?",
                    new String[]{"1"});
        }

        // Closing database connection
        db.close();

        return res;
    }

    public long saveLastSignInInfo(User user){
        int curr_row = getRowCount(FinalVariables.TABLE_LAST_SIGN_IN_INFO);

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_NAME, user.getName());
        values.put(FinalVariables.KEY_ID, user.getID());
        values.put(FinalVariables.KEY_REG, user.getReg());
        values.put(FinalVariables.KEY_FACULTY, user.getFaculty());
        values.put(FinalVariables.KEY_SESSION, user.getSession());
        values.put(FinalVariables.KEY_PHONE, user.getPhone());
        values.put(FinalVariables.KEY_EMAIL, user.getEmail());
        values.put(FinalVariables.KEY_PASSWORD, user.getPassword());
        values.put(FinalVariables.KEY_IMAGE_PATH, user.getImage_path());
        values.put(FinalVariables.KEY_FCM_DEVICE_REG_ID, user.getFcmDeviceRegID());
        values.put(FinalVariables.KEY_STATUS, user.getStatus());

        if(curr_row<1){
            // Inserting Row
            res = db.insert(FinalVariables.TABLE_LAST_SIGN_IN_INFO, null, values);
        }else if(curr_row>0){
            res = db.update(FinalVariables.TABLE_LAST_SIGN_IN_INFO, values, FinalVariables.KEY_SL + " = ?",
                    new String[]{"1"});
        }

        // Closing database connection
        db.close();

        return res;
    }

    // Getting user_info
    public ArrayList<String> getUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> allInfo = new ArrayList<>();
        User user;

        Cursor cursor = db.rawQuery(FinalVariables.QUERY_GET_ALL_INFO, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                user = new User();
                user.setName(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_NAME)));
                user.setID(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_ID)));
                user.setReg(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_REG)));
                user.setFaculty(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FACULTY)));
                user.setSession(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SESSION)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PHONE)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PASSWORD)));
                user.setImage_path(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_IMAGE_PATH)));
                user.setFcmDeviceRegID(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FCM_DEVICE_REG_ID)));
                user.setStatus(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_STATUS)));

                allInfo.add(user.getName());
                allInfo.add(user.getID());
                allInfo.add(user.getReg());
                allInfo.add(user.getFaculty());
                allInfo.add(user.getSession());
                allInfo.add(user.getPhone());
                allInfo.add(user.getEmail());
                allInfo.add(user.getPassword());
                allInfo.add(user.getImage_path());
                allInfo.add(user.getFcmDeviceRegID());
                allInfo.add(user.getStatus());

            }
        }

        // return master_password
        cursor.close();
        db.close();
        return allInfo;
    }

    // Getting teacher_info
    public ArrayList<String> getTeacherInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> teacherAllInfo = new ArrayList<>();
        Teacher teacher;

        Cursor cursor = db.rawQuery(FinalVariables.QUERY_GET_TEACHER_ALL_INFO, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                teacher = new Teacher();
                teacher.setName(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_NAME)));
                teacher.setDesignation(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_DESIGNATION)));
                teacher.setDepartment(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_DEPARTMENT)));
                teacher.setFaculty(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FACULTY)));
                teacher.setPhone(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PHONE)));
                teacher.setEmail(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_EMAIL)));
                teacher.setPassword(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PASSWORD)));
                teacher.setFcmDeviceRegID(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FCM_DEVICE_REG_ID)));
                teacher.setStatus(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_STATUS)));

                teacherAllInfo.add(teacher.getName());
                teacherAllInfo.add(teacher.getDesignation());
                teacherAllInfo.add(teacher.getDepartment());
                teacherAllInfo.add(teacher.getFaculty());
                teacherAllInfo.add(teacher.getPhone());
                teacherAllInfo.add(teacher.getEmail());
                teacherAllInfo.add(teacher.getPassword());
                teacherAllInfo.add(teacher.getFcmDeviceRegID());
                teacherAllInfo.add(teacher.getStatus());

            }
        }

        // return master_password
        cursor.close();
        db.close();
        return teacherAllInfo;
    }

    // Getting master_password
    public ArrayList<String> getLastSignInInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> allInfo = new ArrayList<>();
        User user;

        Cursor cursor = db.rawQuery(FinalVariables.QUERY_GET_LAST_SIGN_IN_INFO, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                user = new User();
                user.setName(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_NAME)));
                user.setID(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_ID)));
                user.setReg(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_REG)));
                user.setFaculty(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FACULTY)));
                user.setSession(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SESSION)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PHONE)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PASSWORD)));
                user.setImage_path(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_IMAGE_PATH)));
                user.setFcmDeviceRegID(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FCM_DEVICE_REG_ID)));
                user.setStatus(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_STATUS)));

                allInfo.add(user.getName());
                allInfo.add(user.getID());
                allInfo.add(user.getReg());
                allInfo.add(user.getFaculty());
                allInfo.add(user.getSession());
                allInfo.add(user.getPhone());
                allInfo.add(user.getEmail());
                allInfo.add(user.getPassword());
                allInfo.add(user.getImage_path());
                allInfo.add(user.getFcmDeviceRegID());
                allInfo.add(user.getStatus());

            }
        }

        // return master_password
        cursor.close();
        db.close();
        return allInfo;
    }

    public void deleteUserInfo(){
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_USER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_TEACHER_DATA);
        // Creating tables again
        onCreate(db);
        db.close();
    }

    public void deleteLastSignInInfo(){
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_LAST_SIGN_IN_INFO);
        // Creating tables again
        onCreate(db);
        db.close();
    }
}
