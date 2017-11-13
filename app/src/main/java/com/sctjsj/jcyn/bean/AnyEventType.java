package com.sctjsj.jcyn.bean;

/**
 * Created by Aaron on 2017/3/20.
 */

public class AnyEventType {
    private CityNameBean cityNameBean;
    public AnyEventType(CityNameBean cityNameBean){
        this.cityNameBean = cityNameBean;
    }
    public CityNameBean getCityName(){
        return cityNameBean;
    }
}
