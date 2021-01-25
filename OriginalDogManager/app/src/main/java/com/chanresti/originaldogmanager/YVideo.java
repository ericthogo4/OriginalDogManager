package com.chanresti.originaldogmanager;

/**
 * Created by Mwangi on 14/03/2018.
 */
// a class defining the You Tube video model
 public class YVideo {
    private int image;
    private String video;
    private String tag;
    public YVideo(int image, String video, String tag) {
        this.image = image;
        this.video = video;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
