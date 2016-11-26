package com.bricenangue.nextgeneration.kongossabar;

import java.util.ArrayList;

/**
 * Created by bricenangue on 18/11/2016.
 */

public class People {
    private String firebaseuniqueId;
    private String location;
    private long karma;


    public People() {
    }

    public String getFirebaseuniqueId() {
        return this.firebaseuniqueId;
    }

    public void setFirebaseuniqueId(String firebaseuniqueId) {
        this.firebaseuniqueId = firebaseuniqueId;
    }

    public long getKarma() {
        return this.karma;
    }

    public void setKarma(long karma) {
        this.karma = karma;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
