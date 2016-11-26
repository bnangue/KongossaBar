package com.bricenangue.nextgeneration.kongossabar;

import android.graphics.Bitmap;

/**
 * Created by bricenangue on 18/11/2016.
 */
public class Comments {
    private String firebaseUniqueid;
    private String location;
    private long time;
    private Bitmap media;
    private String containt;
    private long rating;
    private String creator;

    public Comments() {
    }

    public String getFirebaseUniqueid() {
        return firebaseUniqueid;
    }

    public void setFirebaseUniqueid(String firebaseUniqueid) {
        this.firebaseUniqueid = firebaseUniqueid;
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

    public Bitmap getMedia() {
        return media;
    }

    public void setMedia(Bitmap media) {
        this.media = media;
    }

    public String getContaint() {
        return containt;
    }

    public void setContaint(String containt) {
        this.containt = containt;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
