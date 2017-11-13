package com.sctjsj.jcyn.bean;

import java.io.Serializable;

/**
 * Created by Aaron on 2017/3/6.
 */

public class VideoBean implements Serializable {
    private String img;
    private String title;
    private String scondTitle;
    private String playUrl;
    private String introduction;


    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getScondTitle() {
        return scondTitle;
    }

    public void setScondTitle(String scondTitle) {
        this.scondTitle = scondTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "VideoBean{" +
                "img='" + img + '\'' +
                ", title='" + title + '\'' +
                ", scondTitle='" + scondTitle + '\'' +
                ", playUrl='" + playUrl + '\'' +
                '}';
    }
}
