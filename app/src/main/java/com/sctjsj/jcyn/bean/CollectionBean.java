package com.sctjsj.jcyn.bean;

import java.io.Serializable;

/**
 * Created by Aaron on 2017/3/18.
 */

public class CollectionBean implements Serializable {
    private boolean isCheck;
    private String img;
    private String id;
    private String title;
    private String indroation;
    private String collectNum;

    public String getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(String collectNum) {
        this.collectNum = collectNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIndroation() {
        return indroation;
    }

    public void setIndroation(String indroation) {
        this.indroation = indroation;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "CollectionBean{" +
                "indroation='" + indroation + '\'' +
                ", collectNum='" + collectNum + '\'' +
                ", id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", isCheck=" + isCheck +
                ", title='" + title + '\'' +
                '}';
    }
}
