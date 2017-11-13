package com.sctjsj.jcyn.bean;

/**
 * Created by Aaron on 2017/2/23.
 */

public class UrltitleBean {
    private String key;
    private String value;

    public UrltitleBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "UrltitleBean{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
