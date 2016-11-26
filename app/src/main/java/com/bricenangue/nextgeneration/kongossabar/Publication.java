package com.bricenangue.nextgeneration.kongossabar;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bricenangue on 18/11/2016.
 */

public class Publication {
    private String firebaseUniqueid;
    private String creator;
    private long rating;
    private String location;
    private long time;
    private String media;
    private String containt;
    private HashMap<String,Comments> commentSet;
    private HashMap<String,String> rater;

    public Publication() {
    }


    public String getFirebaseUniqueid() {
        return firebaseUniqueid;
    }

    public void setFirebaseUniqueid(String firebaseUniqueid) {
        this.firebaseUniqueid = firebaseUniqueid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getContaint() {
        return containt;
    }

    public void setContaint(String containt) {
        this.containt = containt;
    }

    public void setCommentSet(HashMap<String, Comments> commentSet) {
        this.commentSet = commentSet;
    }

    public HashMap<String, Comments> getCommentSet() {
        return commentSet;
    }

    public HashMap<String, String> getRater() {
        return rater;
    }

    public void setRater(HashMap<String, String> rater) {
        this.rater = rater;
    }
}
