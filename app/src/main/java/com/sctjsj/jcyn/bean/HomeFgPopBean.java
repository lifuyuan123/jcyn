package com.sctjsj.jcyn.bean;

import java.io.Serializable;

/**
 * Created by Aaron on 2017/2/24.
 */

public class HomeFgPopBean implements Serializable {
    private String id;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "HomeFgPopBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
