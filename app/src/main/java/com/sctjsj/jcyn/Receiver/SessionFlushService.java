package com.sctjsj.jcyn.Receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by Chris-Jason on 2016/10/28.
 */

/**
 * 刷新session定时任务
 */
public class SessionFlushService extends Service {
    private String TAG="SessionFlushService";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,TAG+"开启");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,TAG+"销毁");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }








}
