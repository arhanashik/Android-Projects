package com.project.blackspider.quarrelchat.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;

import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Model.Message;
import com.project.blackspider.quarrelchat.Model.Soulmate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr blackSpider on 19/08/2017.
 */

public class DBHelper extends SQLiteOpenHelper{
    private Context context;
    private FinalVariables finalVariables = new FinalVariables();

    public DBHelper(Context context){
        super(context, FinalVariables.DATABASE_NAME, null, FinalVariables.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FinalVariables.CREATE_TABLE_USER_DATA);
        db.execSQL(FinalVariables.CREATE_TABLE_LAST_SIGN_IN_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_USER_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_LAST_SIGN_IN_INFO);
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
        //if select by email(containes special character '@')
        //Cursor cursor  = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + index + "=\"" + value.trim()+"\";", null);
        //if not select by email
        Cursor cursor  = db.rawQuery("SELECT * FROM " + tableName + " WHERE " + index + "=" + value.trim()+";", null);

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
                + finalVariables.KEY_USERNAME + " TEXT, "
                + finalVariables.KEY_NAME + " TEXT, "
                + finalVariables.KEY_EMAIL + " TEXT, "
                + finalVariables.KEY_PHONE + " TEXT, "
                + finalVariables.KEY_IMAGE_PATH + " TEXT, "
                + finalVariables.KEY_FCM_ID + " TEXT, "
                + finalVariables.KEY_LAST_MESSAGE + " TEXT, "
                + finalVariables.KEY_LAST_CHAT_TIMESTAMP + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_CHAT_LIST);
        db.close();
    }

    public long saveChatList(Soulmate soulmate, String tableName){
        //int curr_row = getRowCount(FinalVariables.TABLE_CHAT_LIST);
        createChatListTable(tableName);
        boolean isInserted = isInserted(tableName, finalVariables.KEY_USERNAME, soulmate.getUsername());

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_USERNAME, soulmate.getUsername());
        values.put(FinalVariables.KEY_NAME, soulmate.getName());
        values.put(FinalVariables.KEY_EMAIL, soulmate.getEmail());
        values.put(FinalVariables.KEY_PHONE, soulmate.getPhone());
        values.put(FinalVariables.KEY_IMAGE_PATH, soulmate.getImagePath());
        values.put(FinalVariables.KEY_FCM_ID, soulmate.getFcmId());

        if(!isInserted){
            // Inserting Row
            values.put(FinalVariables.KEY_LAST_MESSAGE, "");
            values.put(FinalVariables.KEY_LAST_CHAT_TIMESTAMP, "0");
            res = db.insert(tableName, null, values);
        }else{
            res = db.update(tableName, values, FinalVariables.KEY_USERNAME + " = ?",
                    new String[]{soulmate.getUsername()});
        }

        // Closing database connection
        db.close();

        Log.d("Chat list saved: ", soulmate.getUsername());
        return res;
    }

    public int updateLastChatTimeAndMessage(String tableName, String username, String message, long timestamp){
        int updated = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_LAST_MESSAGE, message);
        values.put(FinalVariables.KEY_LAST_CHAT_TIMESTAMP, String.valueOf(timestamp));

        updated = db.update(tableName, values, FinalVariables.KEY_USERNAME + " = ?",
                new String[]{username});

        db.close();
        return updated;
    }

    // Getting chat list
    public ArrayList<Soulmate> getChatList(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Soulmate> chatList = new ArrayList<>();
        Soulmate soulmate;

        String query = "SELECT * FROM " + tableName + ";";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                do {
                    soulmate = new Soulmate();
                    soulmate.setUsername(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_USERNAME)));
                    soulmate.setName(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_NAME)));
                    soulmate.setEmail(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_EMAIL)));
                    soulmate.setPhone(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PHONE)));
                    soulmate.setImagePath(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_IMAGE_PATH)));
                    soulmate.setFcmId(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FCM_ID)));
                    soulmate.setLastMessage(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_LAST_MESSAGE)));
                    soulmate.setTimestamp(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_LAST_CHAT_TIMESTAMP)));

                    Log.d("Chat list retrieving: ", soulmate.getName());
                    chatList.add(soulmate);

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
                + FinalVariables.KEY_REPORT + " TEXT, "
                + FinalVariables.KEY_TIMESTAMP + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_SINGLE_CHAT_MESSAGE);
        db.close();
    }

    public boolean saveSingleChatMessage(String tableName, Message message){
        createSingleChatMessagesTable(tableName);
        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_MESSAGE, message.getMessage());
        values.put(FinalVariables.KEY_REPORT, message.getReport());
        values.put(FinalVariables.KEY_TIMESTAMP, message.getTimestamp());

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
    public List<Message> getSingleChatAllMessages(String my_username, String soulmate_username) {
        String tableName = my_username.replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                soulmate_username.replace(".","_").replace("@","_");
        createSingleChatMessagesTable(tableName);
        SQLiteDatabase db = this.getReadableDatabase();
        List<Message> allMessages = new ArrayList<>();
        Message message;

        String query = "SELECT * FROM " + tableName + ";";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                do {
                    int report = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_REPORT)));
                    message = new Message();
                    message.setIndex(allMessages.size());
                    message.setSl(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SL))));
                    if(report==FinalVariables.MSG_RECEIVED){
                        message.setFrom(soulmate_username);
                        message.setTo(my_username);
                        message.setType(FinalVariables.EXTERNAL_TEXT_MSG);
                    }
                    else{
                        message.setFrom(my_username);
                        message.setTo(soulmate_username);
                        message.setType(FinalVariables.INTERNAL_TEXT_MSG);
                    }
                    message.setMessage(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_MESSAGE)));
                    message.setTimestamp(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_TIMESTAMP)));
                    message.setReport(report);
                    if(allMessages.size()==0) message.setSame(false);
                    else {
                        if(allMessages.get(allMessages.size()-1).getType()==message.getType()) message.setSame(true);
                        else message.setSame(false);
                    }

                    allMessages.add(message);
                    Log.i("Loading message", message.getMessage()+":"+message.getReport());
                }while (cursor.moveToNext());

            }
        }

        // return master_password
        cursor.close();
        db.close();
        return allMessages;
    }

    public Message getLastMessage(String my_username, String soulmate_username){
        String tableName = my_username.replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                soulmate_username.replace(".","_").replace("@","_");
        createSingleChatMessagesTable(tableName);
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + tableName + " ORDER BY " + FinalVariables.KEY_SL +" DESC LIMIT 1;";
        Cursor cursor = db.rawQuery(query, null);

        Message message = new Message();
        message.setMessage("Start chatting now!");
        message.setSame(true); //here isSame is used to determine first time chat

        if (cursor != null){
            if(cursor.moveToFirst()){
                do {
                    int report = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_REPORT)));
                    message.setIndex(0);
                    message.setSl(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_SL))));
                    if(report==FinalVariables.MSG_RECEIVED){
                        message.setFrom(soulmate_username);
                        message.setTo(my_username);
                        message.setType(FinalVariables.EXTERNAL_TEXT_MSG);
                    }
                    else{
                        message.setFrom(my_username);
                        message.setTo(soulmate_username);
                        message.setType(FinalVariables.INTERNAL_TEXT_MSG);
                    }
                    message.setMessage(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_MESSAGE)));
                    message.setTimestamp(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_TIMESTAMP)));
                    message.setReport(report);
                    message.setSame(false);
                    Log.i("Last message", message.getMessage()+":"+message.getReport());
                }while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();
        return message;
    }

    public Message getMessageCount(String my_username, String soulmate_username){
        String tableName = my_username.replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                soulmate_username.replace(".","_").replace("@","_");
        createSingleChatMessagesTable(tableName);
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+FinalVariables.KEY_REPORT + " FROM " + tableName + ";";
        Cursor cursor = db.rawQuery(query, null);

        Message message = new Message();
        int totalSentMsg=0, totalReceivedMsg=0, totalMsgCount=0;

        if (cursor != null){
            if(cursor.moveToFirst()){
                do {
                    int report = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_REPORT)));
                    if (report==FinalVariables.MSG_RECEIVED) totalReceivedMsg++;
                    else totalSentMsg++;

                    totalMsgCount++;
                }while (cursor.moveToNext());
            }
        }

        message.setTotalSentMsg(totalSentMsg);
        message.setTotalReceivedMsg(totalReceivedMsg);
        message.setTotalMsgCount(totalMsgCount);

        cursor.close();
        db.close();
        return message;
    }

    public boolean updateSingleChatMessageReport(String my_username, String soulmate_username, int report, int sl){
        String tableName = my_username.replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                soulmate_username.replace(".","_").replace("@","_");
        int updated = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_REPORT, report);

        if(sl==FinalVariables.FLAG_UPDATE_MSG_REPORT_ALL_DELIVERED) {
            //means update all row
            updated = db.update(tableName, values, FinalVariables.KEY_REPORT + " = ?",
                    new String[]{String.valueOf(FinalVariables.MSG_SENT)});

        }else if(sl==FinalVariables.FLAG_UPDATE_MSG_REPORT_ALL_SEEN) {
            //means update all row
            updated = db.update(tableName, values, FinalVariables.KEY_REPORT + " = ? OR " + FinalVariables.KEY_REPORT + " = ?",
                    new String[]{String.valueOf(FinalVariables.MSG_SENT), String.valueOf(FinalVariables.MSG_DELIVERED)});

        } else {
            if(report==FinalVariables.MSG_DELIVERED){
                String query = "SELECT * FROM " + tableName + " WHERE "+FinalVariables.KEY_SL+"="+sl+";";
                Cursor cursor = db.rawQuery(query, null);
                int oldReport = FinalVariables.MSG_SEEN;
                if (cursor != null){
                    if(cursor.moveToFirst()){
                        do {
                            oldReport = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_REPORT)));
                        }while (cursor.moveToNext());
                    }
                }
                if(oldReport==FinalVariables.MSG_SENT) {
                    updated = db.update(tableName, values, FinalVariables.KEY_SL + "=?", new String[]{String.valueOf(sl)});
                }

            }else {
                updated = db.update(tableName, values, FinalVariables.KEY_SL + " = ?",
                        new String[]{String.valueOf(sl)});
            }
        }

        db.close();
        if (updated>0) return true;

        return false;
    }

    public boolean deleteSingleChatMessage(String my_username, String soulmate_username, int sl){
        String tableName = my_username.replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                soulmate_username.replace(".","_").replace("@","_");
        SQLiteDatabase db = this.getWritableDatabase();

        int deleted = db.delete(tableName, FinalVariables.KEY_SL + " = ?",
                new String[] { String.valueOf(sl) });

        // Closing database connection
        db.close();

        return deleted > 0;
    }

    public void deleteAllSingleChatMessages(String my_username, String soulmate_username){
        String tableName = my_username.replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                soulmate_username.replace(".","_").replace("@","_");
        createSingleChatMessagesTable(tableName);
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        // Creating tables again
        onCreate(db);
        db.close();
    }

    public void dropSingleChatMessagesTable(String my_username, String soulmate_username){
        String tableName = my_username.replace(".","_").replace("@","_")+
                FinalVariables.TABLE_SINGLE_CHAT_MESSAGES +
                soulmate_username.replace(".","_").replace("@","_");
        createChatListTable(tableName);
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

    public long saveUserInfo(Soulmate soulmate){
        int curr_row = getRowCount(FinalVariables.TABLE_USER_DATA);

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_USERNAME, soulmate.getUsername());
        values.put(FinalVariables.KEY_NAME, soulmate.getName());
        values.put(FinalVariables.KEY_EMAIL, soulmate.getEmail());
        values.put(FinalVariables.KEY_PHONE, soulmate.getPhone());
        values.put(FinalVariables.KEY_PASSWORD, soulmate.getPassword());
        values.put(FinalVariables.KEY_IMAGE_PATH, soulmate.getImagePath());
        values.put(FinalVariables.KEY_FCM_ID, soulmate.getFcmId());

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

    public long saveLastSigninInfo(Soulmate soulmate){
        int curr_row = getRowCount(FinalVariables.TABLE_LAST_SIGN_IN_INFO);

        SQLiteDatabase db = this.getWritableDatabase();

        long res = 0;
        ContentValues values = new ContentValues();
        values.put(FinalVariables.KEY_USERNAME, soulmate.getUsername());
        values.put(FinalVariables.KEY_NAME, soulmate.getName());
        values.put(FinalVariables.KEY_EMAIL, soulmate.getEmail());
        values.put(FinalVariables.KEY_PHONE, soulmate.getPhone());
        values.put(FinalVariables.KEY_PASSWORD, soulmate.getPassword());
        values.put(FinalVariables.KEY_IMAGE_PATH, soulmate.getImagePath());
        values.put(FinalVariables.KEY_FCM_ID, soulmate.getFcmId());

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
    public Soulmate getUserInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Soulmate soulmate = new Soulmate();

        Cursor cursor = db.rawQuery(FinalVariables.QUERY_GET_ALL_INFO, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                soulmate.setUsername(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_USERNAME)));
                soulmate.setName(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_NAME)));
                soulmate.setEmail(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_EMAIL)));
                soulmate.setPhone(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PHONE)));
                soulmate.setPassword(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PASSWORD)));
                soulmate.setImagePath(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_IMAGE_PATH)));
                soulmate.setFcmId(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FCM_ID)));

            }
        }

        cursor.close();
        db.close();
        return soulmate;
    }

    // Getting last signin info
    public Soulmate getLastSigninInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Soulmate soulmate = new Soulmate();

        Cursor cursor = db.rawQuery(FinalVariables.QUERY_GET_LAST_SIGN_IN_INFO, null);

        if (cursor != null){
            if(cursor.moveToFirst()){
                soulmate.setUsername(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_USERNAME)));
                soulmate.setName(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_NAME)));
                soulmate.setEmail(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_EMAIL)));
                soulmate.setPhone(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PHONE)));
                soulmate.setPassword(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_PASSWORD)));
                soulmate.setImagePath(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_IMAGE_PATH)));
                soulmate.setFcmId(cursor.getString(cursor.getColumnIndex(FinalVariables.KEY_FCM_ID)));

            }
        }

        cursor.close();
        db.close();
        return soulmate;
    }

    public void deleteUserInfo(){
        SQLiteDatabase db = this.getWritableDatabase();

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FinalVariables.TABLE_USER_DATA);
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
