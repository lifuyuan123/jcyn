package com.sctjsj.jcyn.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.AnyEventType;
import com.sctjsj.jcyn.bean.ChangeCityName;
import com.sctjsj.jcyn.bean.CityNameBean;
import com.sctjsj.jcyn.fragment.AboutMeFg;
import com.sctjsj.jcyn.fragment.HomeFg;
import com.sctjsj.jcyn.fragment.MapFg;
import com.sctjsj.jcyn.fragment.VideoFg;
import com.sctjsj.jcyn.getui.DemoPushService;


import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/2/6.
 */

public class IndexActivity extends AppCompatActivity{
    private boolean isExited = false;//标志是否已经退出
    private FragmentManager fgManager;
    private HomeFg homeFg;
    private Handler mHandler;
    private MapFg mapFg;
    private VideoFg videoFg;
    private AboutMeFg meFg;
    private MsApp myApp;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;

    public AMapLocationClientOption mLocationClientOption = null;

    private Double longT;//所在地经度
    private Double lanT;//所在地纬度

    private CityNameBean cityNameBean=null;


    //文字
    @Bind(R.id.home_index_home_tv)
    TextView homeTv;
    @Bind(R.id.home_index_paymoney_tv)
    TextView payTv;
    @Bind(R.id.home_index_feedback_tv)
    TextView feedTv;
    @Bind(R.id.home_index_aboutme_tv)
    TextView meTv;
    //图片
    @Bind(R.id.home_index_home_iv)
    ImageView homeIv;
    @Bind(R.id.home_index_paymoney_iv)
    ImageView payIv;
    @Bind(R.id.home_index_feedback_iv)
    ImageView feedIv;
    @Bind(R.id.home_index_aboutme_iv)
    ImageView meIv;

    @Bind(R.id.home_index_home_ll)
    LinearLayout homeLl;
    @Bind(R.id.home_index_paymoney_ll)
    LinearLayout payLl;
    @Bind(R.id.home_index_feedback_ll)
    LinearLayout feedLl;
    @Bind(R.id.home_index_aboutme_ll)
    LinearLayout meLl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);
        myApp = (MsApp) getApplication();
        myApp.addActivity(this);
        /**
         * 判断定位权限
         */
        if(Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);//自定义的code
            }else {
                initLocation();
            }
        }else {
            initLocation();
        }
        fgManager = getSupportFragmentManager();
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));

        //进入默认首页
        int colorGreen = Color.parseColor("#00c7be");
        hideAllFgIfNotNull();
        setNavbarNotSelected();
//        homeLl.setBackgroundColor(colorRed);
        homeTv.setTextColor(colorGreen);
        homeIv.setImageResource(R.mipmap.icon_activity_index_home_chooce);
        if (homeFg == null) {
            homeFg = new HomeFg();
        }

        fgManager.beginTransaction().add(R.id.activity_index_fg_content, homeFg).
                show(homeFg).commit();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        isExited = false;
                        break;
                    case 1:
                        Log.e("indx", lanT + "," + longT);
                        if(cityNameBean!=null) {
                            EventBus.getDefault().post(new AnyEventType(cityNameBean));
                            myApp.saveCityName(cityNameBean.getName());
                            myApp.saveLatLont(cityNameBean.getLat(),cityNameBean.getLont());
                        }else {
                            Toast.makeText(IndexActivity.this,"定位获取失败",Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };


    }

    @OnClick({R.id.home_index_home_ll, R.id.home_index_paymoney_ll, R.id.home_index_feedback_ll, R.id.home_index_aboutme_ll})
    public void changFg(View view) {
        int colorWhite = Color.parseColor("#FFFFFF");
        int colorRed = Color.parseColor("#FF0600");
        int colorGreen = Color.parseColor("#00c7be");
        switch (view.getId()) {
            case R.id.home_index_home_ll:
                hideAllFgIfNotNull();
                setNavbarNotSelected();
//                homeLl.setBackgroundColor(colorRed);
                homeIv.setImageResource(R.mipmap.icon_activity_index_home_chooce);
                homeTv.setTextColor(colorGreen);
                if (homeFg == null) {
                    homeFg = new HomeFg();
                    fgManager.beginTransaction().add(R.id.activity_index_fg_content, homeFg).
                            show(homeFg).commit();
                } else {
                    fgManager.beginTransaction().show(homeFg).commit();
                }
                break;
            case R.id.home_index_paymoney_ll:
                hideAllFgIfNotNull();
                setNavbarNotSelected();
//                payLl.setBackgroundColor(colorRed);
                payTv.setTextColor(colorGreen);
                payIv.setImageResource(R.mipmap.icon_activity_index_loction_chooce);
                if (mapFg == null) {
                    mapFg = new MapFg();
                    fgManager.beginTransaction().add(R.id.activity_index_fg_content, mapFg).
                            show(mapFg).commit();
                } else {
                    fgManager.beginTransaction().show(mapFg).commit();
                }
                break;
            case R.id.home_index_feedback_ll:
                hideAllFgIfNotNull();
                setNavbarNotSelected();
//                feedLl.setBackgroundColor(colorRed);
                feedTv.setTextColor(colorGreen);
                feedIv.setImageResource(R.mipmap.icon_activity_index_video_chooce);
                if (videoFg == null) {
                    videoFg = new VideoFg();
                    fgManager.beginTransaction().add(R.id.activity_index_fg_content, videoFg).
                            show(videoFg).commit();
                } else {
                    fgManager.beginTransaction().show(videoFg).commit();
                }
                break;
            case R.id.home_index_aboutme_ll:
                hideAllFgIfNotNull();
                setNavbarNotSelected();
//                meLl.setBackgroundColor(colorRed);
                meTv.setTextColor(colorGreen);
                meIv.setImageResource(R.mipmap.icon_activity_index_me_chooce);
                if (meFg == null) {
                    meFg = new AboutMeFg();
                    fgManager.beginTransaction().add(R.id.activity_index_fg_content, meFg).
                            show(meFg).commit();
                } else {
                    fgManager.beginTransaction().show(meFg).commit();
                }
                break;
        }
    }

    /**
     * 清除导航栏所有状态
     */
    private void setNavbarNotSelected() {
        //灰色字体
        int colorGray = Color.parseColor("#80000000");
        int colorBack = Color.parseColor("#FFFFFF");
        homeTv.setTextColor(colorGray);
        payTv.setTextColor(colorGray);
        feedTv.setTextColor(colorGray);
        meTv.setTextColor(colorGray);

        homeIv.setImageResource(R.mipmap.icon_activity_index_home);
        payIv.setImageResource(R.mipmap.icon_location);
        feedIv.setImageResource(R.mipmap.icon_activity_index_video);
        meIv.setImageResource(R.mipmap.icon_activity_index_me);

//        homeLl.setBackgroundColor(colorBack);
//        payLl.setBackgroundColor(colorBack);
//        feedLl.setBackgroundColor(colorBack);
//        meLl.setBackgroundColor(colorBack);

    }

    /**
     * 隐藏所有fg
     */
    private void hideAllFgIfNotNull() {
        FragmentTransaction transaction = fgManager.beginTransaction();
        if (homeFg != null) {
            transaction.hide(homeFg);
        }
        if (mapFg != null) {
            transaction.hide(mapFg);
        }
        if (videoFg != null) {
            transaction.hide(videoFg);
            videoFg.onDestroy();
        }
        if (meFg != null) {
            transaction.hide(meFg);
        }
        //提交
        transaction.commit();
    }

    /**
     * 重写返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //物理返回键值
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //连续点击两次返回键退出程序
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出程序
     */
    private void exit() {
        if (!isExited) {
            isExited = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            Message msg = new Message();
            msg.what = 0;
            mHandler.sendMessageDelayed(msg, 2000);

        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 高德定位
     */
    private void initLocation() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
        }
        //初始化定位监听器
        if (mLocationListener == null) {
            mLocationListener = new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                        //获取定位错误码
                        int errorCode = aMapLocation.getErrorCode();
                        Log.e("indx",errorCode+"");
                        //定位成功
                        if (0 == errorCode) {
                            /**
                             * 解析地址
                             */
                            //当前POI
                            String poi = aMapLocation.getPoiName();
                            String cityName = aMapLocation.getCity();
                            cityNameBean = new CityNameBean();
                            Log.e("indx",cityName);
                            //当前经纬度
                            longT = aMapLocation.getLongitude();
                            lanT = aMapLocation.getLatitude();
                            cityNameBean.setLont(longT+"");
                            cityNameBean.setLat(lanT+"");
                            cityNameBean.setName(cityName);
                            Message msg = new Message();
                            msg.what = 1;
                            mLocationClient.stopLocation();
                            mHandler.sendMessage(msg);
                        } else {
                            //定位失败
                            mLocationClient.stopLocation();
                        }

                    } else {
                        //定位失败
                        mLocationClient.stopLocation();
                    }
                }
            };
        }

        //定位客户端设置定位监听器
        mLocationClient.setLocationListener(mLocationListener);
        /**
         * 配置定位参数
         */
        if (mLocationClientOption == null) {
            mLocationClientOption = new AMapLocationClientOption();
        }
        //设置定位模式：高精度定位模式
        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息
        mLocationClientOption.setNeedAddress(true);
        //设置是否指定位一次
        mLocationClientOption.setOnceLocation(false);
        //如果设置了只定位一次，将获取3s内经度最高的一次定位结果
        if (mLocationClientOption.isOnceLocation()) {
            mLocationClientOption.setOnceLocationLatest(true);
        }
        //设置是否强制刷新wifi
        mLocationClientOption.setWifiActiveScan(true);
        //设置是否允许模拟位置
        mLocationClientOption.setMockEnable(false);
        //设置定位时间间隔5min
        mLocationClientOption.setInterval(1000 * 60 * 5);
        mLocationClient.setLocationOption(mLocationClientOption);
        //开启定位
        mLocationClient.startLocation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            initLocation();
        }
    }
}