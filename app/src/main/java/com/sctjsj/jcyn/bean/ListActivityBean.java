package com.sctjsj.jcyn.bean;

import java.io.Serializable;

/**
 * Created by Aaron on 2017/3/17.
 */

public class ListActivityBean implements Serializable {
    private String id;
    private String img;
    private String title;
    private String indroation;
    private String collection;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ListActivityBean{" +
                "collection='" + collection + '\'' +
                ", id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", title='" + title + '\'' +
                ", indroation='" + indroation + '\'' +
                '}';
    }
}
