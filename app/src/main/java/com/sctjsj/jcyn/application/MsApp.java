package com.sctjsj.jcyn.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.sctjsj.jcyn.getui.DemoPushService;
import com.zxy.recovery.core.Recovery;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Aaron on 2017/1/22.
 */

public class MsApp extends Application{
    private List<Activity> mList = new LinkedList<Activity>();
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    public  List<String> searchlv = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 xUtils
        x.Ext.init(this);
//        //初始化个推
//       if(getNotication()){
//           PushManager.getInstance().initialize(getApplicationContext(), DemoPushService.class);
//       }
        //bug恢复页面
//        Recovery.getInstance()
//                .recoverInBackground(false)
//                .debug(true)
//                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
//                .init(this);
    }
    /**
     * 获取系统设置SPF
     * @return
     */
    public SharedPreferences getSPF(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        return spf;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    //存储是否接受推送
    public boolean getNotication(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        boolean b=spf.getBoolean("notic",true);
        return b;
    }
    public void saveNotication(boolean b){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        spf.edit().putBoolean("notic",b).commit();
    }

    //保存UserId到本地
    public void saveUserId(String userId){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        spf.edit().putString("user",userId).commit();
    }
    public String getUserId(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        String userId=spf.getString("user","none");
        return userId;
    }

    public void saveAccount(String userName,String passWord){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        spf.edit().putString("account",userName).commit();
        spf.edit().putString("password",passWord).commit();
    }

    public String getAccount(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        String userName=spf.getString("account","none");
        return userName;
    }

    public String getPassword(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        String password=spf.getString("password","none");
        return password;
    }


    public void saveLatLont(String lat,String lont){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        spf.edit().putString("lat",lat).commit();
        spf.edit().putString("lont",lont).commit();
    }

    public void saveCityName(String city){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        spf.edit().putString("city",city).commit();
    }

    public String getCityName(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        String city=spf.getString("city","none");
        return city;
    }


    public String getLat(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        String lat=spf.getString("lat","none");
        return lat;
    }
    public String getLont(){
        spf=getSharedPreferences("SYSTEM",MODE_PRIVATE);
        String lont=spf.getString("lont","none");
        return lont;
    }


    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    //移除所有activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public List<String> getSearchlv() {
        return searchlv;
    }
    public void remove(String s){
        searchlv.remove(s);
    }

    public void setSearchlv(List<String> searchlv) {
        this.searchlv = searchlv;
    }
}
