package com.sctjsj.jcyn.bean;

/**
 * Created by Aaron on 2017/3/21.
 */

public class MapBean {
    private String id;
    private String lat;
    private String lnt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLnt() {
        return lnt;
    }

    public void setLnt(String lnt) {
        this.lnt = lnt;
    }

    @Override
    public String toString() {
        return "MapBean{" +
                "id='" + id + '\'' +
                ", lat='" + lat + '\'' +
                ", lnt='" + lnt + '\'' +
                '}';
    }
}
