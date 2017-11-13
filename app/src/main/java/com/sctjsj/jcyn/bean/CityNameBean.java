package com.sctjsj.jcyn.bean;

/**
 * Created by Aaron on 2017/3/20.
 */

public class CityNameBean {
    private String name;
    private String lat;
    private String lont;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLont() {
        return lont;
    }

    public void setLont(String lont) {
        this.lont = lont;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CityNameBean{" +
                "lat='" + lat + '\'' +
                ", name='" + name + '\'' +
                ", lont='" + lont + '\'' +
                '}';
    }
}
