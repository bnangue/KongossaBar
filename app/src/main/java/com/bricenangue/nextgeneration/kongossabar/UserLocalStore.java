package com.bricenangue.nextgeneration.kongossabar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bricenangue on 19/11/2016.
 */

public class UserLocalStore {
    Context context;
    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDataBase;

    public UserLocalStore(Context context){
        this.context=context;
        userLocalDataBase=context.getSharedPreferences(SP_NAME,0);
    }
    public void storeUserLocation(String location){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putString("location",location);

        spEditor.apply();
    }

    public String getUserLocation(){
        return userLocalDataBase.getString("location", "");
    }

    public void storeUserKarma(long karma){
        SharedPreferences.Editor spEditor=userLocalDataBase.edit();
        spEditor.putLong("karma",karma);

        spEditor.apply();
    }
    public Long getUserKarma(){
        return userLocalDataBase.getLong("karma", 0);
    }

    public void reducekarma(){
        storeUserKarma(getUserKarma() - 4);
    }

    public void addkarma(){
        storeUserKarma(getUserKarma() + 8);
    }

    public void reducekarmaoncomment(){
        storeUserKarma(getUserKarma() - 3);
    }

    public void addkarmaoncomment(){
        storeUserKarma(getUserKarma() + 5);
    }
}
