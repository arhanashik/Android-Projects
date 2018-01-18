package com.project.blackspider.musician.variables;

/**
 * Created by Mr blackSpider on 9/4/2017.
 */

public class FinalVariables {
    public static final String PACKAGE_NAME = "com.project.blackspider.musician";

    public static final String STORAGE = PACKAGE_NAME+".STORAGE";
    public static final String INFO = PACKAGE_NAME+".INFO";
    public static final String PLAY_LIST_TABLE = PACKAGE_NAME+".PLAY_LIST_TABLE";
    public static final String PLAY_LIST = PACKAGE_NAME+".PLAY_LIST";
    public static final String Broadcast_PLAY_NEW_AUDIO = PACKAGE_NAME+".PlayNewAudio";

    public static final String ACTION_PLAY = PACKAGE_NAME+".ACTION_PLAY";
    public static final String ACTION_PAUSE = PACKAGE_NAME+".ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = PACKAGE_NAME+".ACTION_PREVIOUS";
    public static final String ACTION_NEXT = PACKAGE_NAME+".ACTION_NEXT";
    public static final String ACTION_STOP = PACKAGE_NAME+".ACTION_STOP";

    //AudioPlayer notification ID
    public static final int NOTIFICATION_ID = 101;

    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int NEXT = 2;
    public static final int PREV = 3;

    public static final int REPEAT_ALL = 0;
    public static final int SHUFFLE = 1;

    public static final int STORAGE_ACCESS_PERMISSION_REQUEST_CODE = 111;
}
