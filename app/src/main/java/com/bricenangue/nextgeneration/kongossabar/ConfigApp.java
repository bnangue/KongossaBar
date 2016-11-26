package com.bricenangue.nextgeneration.kongossabar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by bricenangue on 19/11/2016.
 */

public class ConfigApp {

    private Context context;
    static final String FIREBASE_APP_URL_REGION_POST_COMMENTS = "commentSet";
    static final String FIREBASE_APP_URL_REGION_POST_RATING = "rating";
    static final String FIREBASE_APP_URL_USERS = "Users";
    static  final String FIREBASE_APP_URL_USERS_KARMA= "karma";

    public ConfigApp (Context context){
        this.context=context;
    }
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if ( "WIFI".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if ("MOBILE".equals(ni.getTypeName()))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


}
