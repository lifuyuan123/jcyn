package com.sctjsj.jcyn.bean;

/**
 * Created by Aaron on 2017/3/21.
 */

public class Accessory {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Accessory{" +
                "id='" + id + '\'' +
                '}';
    }
}
