package com.bricenangue.nextgeneration.kongossabar;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by bricenangue on 19/11/2016.
 */

public class Myapplication  extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing firebase
        Firebase.setAndroidContext(getApplicationContext());

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built =builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

}
