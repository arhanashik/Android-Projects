package com.project.blackspider.classschedule.App;

import com.project.blackspider.classschedule.Receiver.ConnectivityReceiver;

/**
 * Created by Mr blackSpider on 7/29/2017.
 */

public class ConnectivityController extends AppController {

    private static ConnectivityController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized ConnectivityController getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}