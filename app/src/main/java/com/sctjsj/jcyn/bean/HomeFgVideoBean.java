package com.sctjsj.jcyn.bean;

import java.io.Serializable;

/**
 * Created by Aaron on 2017/2/24.
 * 首页视频类
 */

public class HomeFgVideoBean implements Serializable {
    private String url;
    private String id;
    private String title;
    private String scondTitle;
    private String collectNumber;


    public String getCollectNumber() {
        return collectNumber;
    }

    public void setCollectNumber(String collectNumber) {
        this.collectNumber = collectNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "HomeFgVideoBean{" +
                "collectNumber='" + collectNumber + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", scondTitle='" + scondTitle + '\'' +
                '}';
    }
}
