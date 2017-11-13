package com.sctjsj.jcyn.bean;

import java.io.Serializable;

/**
 * Created by Aaron on 2017/3/17.
 */

public class UserClollectionBean implements Serializable {
    private boolean isChecked;
    private String img;
    private String id;
    private String title;
    private String indroation;
    private String collageNum;

    public String getCollageNum() {
        return collageNum;
    }

    public void setCollageNum(String collageNum) {
        this.collageNum = collageNum;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
