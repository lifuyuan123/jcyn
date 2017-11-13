package com.sctjsj.jcyn.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.vr.ndk.base.Constants;
import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.activity.CityListActivity;
import com.sctjsj.jcyn.activity.DetailsActivity;
import com.sctjsj.jcyn.activity.QueryActivity;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.AnyEventType;
import com.sctjsj.jcyn.bean.CityNameBean;
import com.sctjsj.jcyn.bean.MapBean;
import com.sctjsj.jcyn.maputil.ClusterClickListener;
import com.sctjsj.jcyn.maputil.ClusterItem;
import com.sctjsj.jcyn.maputil.ClusterOverlay;
import com.sctjsj.jcyn.maputil.ClusterRender;
import com.sctjsj.jcyn.maputil.RegionItem;
import com.sctjsj.jcyn.util.BnUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/2/6.
 */

public class MapFg extends Fragment implements AMap.OnMapLoadedListener, View.OnClickListener {
    @Bind(R.id.fg_map_mv)
    MapView mMapView;
    //    @Bind(R.id.basegreen_back_rl)RelativeLayout relativeLayout;
//    @Bind(R.id.basegreen_second_rl)RelativeLayout relativeLayout1;
    List<MapBean> list = new ArrayList<>();

    String url = "http://demo.sctjsj.com:80/upload/wxggtp/735b384b-b077-4880-97cd-be8d31d78fc3.jpg";
    @Bind(R.id.fg_home_cityname_tv)
    TextView fgHomeCitynameTv;
    @Bind(R.id.basegreen_back_rl)
    LinearLayout basegreenBackRl;
    @Bind(R.id.basegreen_title_tv)
    TextView basegreenTitleTv;
    @Bind(R.id.fg_home_query)
    ImageView fgHomeQuery;
    @Bind(R.id.basegreen_title_bg)
    RelativeLayout basegreenTitleBg;


    private AMap mAMap;
    private int clusterRadius = 100;
    private MsApp msApp;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;

    public AMapLocationClientOption mLocationClientOption = null;

    private Double longT;//所在地经度
    private Double lanT;//所在地纬度

    private CityNameBean cityNameBean=null;

    //坐标点集合
    private Map<Integer, Drawable> mBackDrawAbles = new HashMap<Integer, Drawable>();

    private ClusterOverlay mClusterOverlay;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_map, null);
        ButterKnife.bind(this, view);
//        EventBus.getDefault().register(this);
        mMapView.onCreate(savedInstanceState);
        msApp = (MsApp) getActivity().getApplication();
        fgHomeQuery.setOnClickListener(this);
        basegreenBackRl.setOnClickListener(this);
        fgHomeCitynameTv.setText(msApp.getCityName());

//        AMapOptions aOptions = new AMapOptions();
//        aOptions.zoomGesturesEnabled(false);// 禁止通过手势缩放地图
//        aOptions.scrollGesturesEnabled(false);// 禁止通过手势移动地图
//        aOptions.tiltGesturesEnabled(false);// 禁止通过手势倾斜地图
//        aOptions.camera(LUJIAZUI);
//        initLocation();
        init();

//        new Location().setLoactionLatLont(new LoactionLatLont() {
//            @Override
//            public void getLatLont(String lat, String lont) {
//                Toast.makeText(getActivity(),lat+","+lont,Toast.LENGTH_LONG).show();
//            }
//        });


//        relativeLayout.setVisibility(View.GONE);
//        relativeLayout1.setVisibility(View.GONE);
        Log.e("abc", msApp.getLat() + ",,,," + msApp.getLont());

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    //添加地图数据坐标点
    private void init() {
        if (mAMap == null) {
            // 初始化地图
            mAMap = mMapView.getMap();
            mAMap.setOnMapLoadedListener(this);

            //定位图标和定位设置
            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            mAMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//            mAMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
            mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//            String lat = msApp.getLat();
//            String lont = msApp.getLont();






//            //点击可以动态添加点
//            mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    double lat = Math.random() + 39.474923;
//                    double lon = Math.random() + 116.027116;
//
//                    LatLng latLng1 = new LatLng(lat, lon, false);
//                    RegionItem regionItem = new RegionItem(latLng1,
//                            "test");
//                    mClusterOverlay.addClusterItem(regionItem);
//
//                }
//            });
        }
    }

//    @Subscribe
//    public void onEventMainThread(AnyEventType anyEventType){
//        Log.e("map","fff");
//        CityNameBean cityNameBean = new CityNameBean();
//        lat=cityNameBean.getLat();
//        lont=cityNameBean.getLont();
//        cityNameBean = anyEventType.getCityName();
//        Toast.makeText(getActivity(),cityNameBean.toString(),Toast.LENGTH_LONG).show();
//        Log.e("hf",cityNameBean.toString());
//    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        mClusterOverlay.onDestroy();
        if (mMapView!=null){
            mMapView.onDestroy();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onMapLoaded() {
        String lat = msApp.getLat();
        String lont = msApp.getLont();
        getMapMsg(lat, lont);

        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lont));
        CameraPosition LUJIAZUI = new CameraPosition.Builder()
                .target(latLng).zoom(18).bearing(0).tilt(30).build();

        AMapOptions aOptions = new AMapOptions();
        aOptions.camera(LUJIAZUI);


//        //添加测试数据
//        new Thread() {
//            public void run() {
//
//                List<ClusterItem> items = new ArrayList<ClusterItem>();
//
//                //随机10000个点
//                for (int i = 0; i < 100; i++) {
//
//                    double lat = Math.random() + 39.474923;
//                    double lon = Math.random() + 116.027116;
//
//                    LatLng latLng = new LatLng(lat, lon, false);
//                    RegionItem regionItem = new RegionItem(latLng,
//                            "test" + i);
//                    items.add(regionItem);
////                    addMarker(latLng,"测试点"+i);
//
//
//                }
//                mClusterOverlay = new ClusterOverlay(mAMap, items,
//                        dp2px(msApp.getApplicationContext(), clusterRadius),
//                        msApp.getApplicationContext());
//
//                mClusterOverlay.setClusterRenderer(new text());
//                mClusterOverlay.setOnClusterClickListener(new text());
//
//            }
//
//        }
//
//                .start();


    }


    public void getMapMsg(String lat, String lng) {
        RequestParams params = new RequestParams(BnUrl.mapUrl);
        params.addBodyParameter("lat", lat);
        params.addBodyParameter("lng", lng);
        params.addBodyParameter("raidus", "20");
        params.addBodyParameter("c_id", "1");
        Log.e("msg", lat + "," + lng);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("msg", result.toString());
                if (result != null) {
                    try {
                        List<ClusterItem> items = new ArrayList<ClusterItem>();
                        JSONArray alist = result.getJSONArray("alist");
                        for (int i = 0; i < alist.length(); i++) {
                            JSONObject object = alist.getJSONObject(i);
                            String lat = object.getString("lat");
                            String lon = object.getString("lng");
                            String id = object.getString("id");
                            LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon), false);
                            RegionItem regionItem = new RegionItem(latLng, id);
                            items.add(regionItem);
                            MapBean mapBean = new MapBean();
                            mapBean.setId(id);
                            mapBean.setLat(lat);
                            mapBean.setLnt(lon);
                            list.add(mapBean);
                        }
                        mClusterOverlay = new ClusterOverlay(mAMap, items,
                                dp2px(msApp.getApplicationContext(), clusterRadius),
                                msApp.getApplicationContext());

                        mClusterOverlay.setClusterRenderer(new text());
                        mClusterOverlay.setOnClusterClickListener(new text());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


//    /**
//     * 点击监听
//     * @param marker
//     *            点击的聚合点
//     * @param clusterItems
//     */
//    @Override
//    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
//
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (ClusterItem clusterItem : clusterItems) {
//            builder.include(clusterItem.getPosition());
//            Log.e("msg",clusterItem.getPosition()+"");
//        }
//        LatLngBounds latLngBounds = builder.build();
//        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0)
//        );
//    }

    /**
     * 添加定位位置view
     * lin
     *
     * @param position 传入的定位点
     * @param title    描述
     */
    private void addMarker(LatLng position, String title) {
        if (position != null) {
            //初始化marker内容
            MarkerOptions markerOptions = new MarkerOptions();
            //这里很简单就加了一个TextView，根据需求可以加载复杂的View
            View view = View.inflate(getActivity(), R.layout.custom_view, null);
            TextView textView = ((TextView) view.findViewById(R.id.title));
            textView.setText(title);
            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(view);
            markerOptions.position(position).icon(markerIcon);
            mAMap.addMarker(markerOptions);
        }
    }

//    /**
//     *
//     * @param clusterNum
//     * @return
//     * 通过判断当前圆圈内的数数字设置颜色
//     * Color.argb(透明度,。。。三原色号)
//     */
//    @Override
//    public Drawable getDrawAble(int clusterNum) {
//        int radius = dp2px(msApp.getApplicationContext(), 80);
//        if (clusterNum == 1) {
//            Drawable bitmapDrawable = mBackDrawAbles.get(1);
//            if (bitmapDrawable == null) {
//                try {
//                    bitmapDrawable = new BitmapDrawable(Picasso.with(getActivity()).load(url).get());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                        getApplication().getResources().getDrawable(R.drawable.custom_info_bubble);
////                        getApplication().getResources().getDrawable();
////                                            R.drawable.icon_openmap_mark);
//                mBackDrawAbles.put(1, bitmapDrawable);
//            }
//
//            return bitmapDrawable;
//        } else if (clusterNum < 5) {
//
//            Drawable bitmapDrawable = mBackDrawAbles.get(2);
//            if (bitmapDrawable == null) {
//                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
//                        Color.argb(159, 210, 154, 6)));
//                mBackDrawAbles.put(2, bitmapDrawable);
//            }
//
//            return bitmapDrawable;
//        } else if (clusterNum < 10) {
//            Drawable bitmapDrawable = mBackDrawAbles.get(3);
//            if (bitmapDrawable == null) {
//                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
//                        Color.argb(199, 217, 114, 0)));
//                mBackDrawAbles.put(3, bitmapDrawable);
//            }
//
//            return bitmapDrawable;
//        } else {
//            Drawable bitmapDrawable = mBackDrawAbles.get(4);
//            if (bitmapDrawable == null) {
//                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
//                        Color.argb(235, 215, 66, 2)));
//                mBackDrawAbles.put(4, bitmapDrawable);
//            }
//
//            return bitmapDrawable;
//        }
//    }

    /**
     * 动态画圆
     *
     * @param radius
     * @param color
     * @return
     */

    private Bitmap drawCircle(int radius, int color) {

        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, radius * 2, radius * 2);
        paint.setColor(color);
        canvas.drawArc(rectF, 0, 360, true, paint);
        return bitmap;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fg_home_query:
                startActivityForResult(new Intent(getActivity(), QueryActivity.class),100);
                break;
            case R.id.basegreen_back_rl:
                startActivityForResult(new Intent(getActivity(), CityListActivity.class),100);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    class text implements ClusterRender, ClusterClickListener {
        /**
         * @param clusterNum
         * @return 通过判断当前圆圈内的数数字设置颜色
         * Color.argb(透明度,。。。三原色号)
         */
        @Override
        public Drawable getDrawAble(int clusterNum) {
            int radius = dp2px(msApp.getApplicationContext(), 80);
            if (clusterNum == 1) {
                Drawable bitmapDrawable = mBackDrawAbles.get(1);
                if (bitmapDrawable == null) {
                    try {
                        bitmapDrawable = new BitmapDrawable(Picasso.with(getActivity()).load(R.mipmap.icon_home_map).get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                        getApplication().getResources().getDrawable(R.drawable.custom_info_bubble);
//                        getApplication().getResources().getDrawable();
//                                            R.drawable.icon_openmap_mark);
                    mBackDrawAbles.put(1, bitmapDrawable);
                }

                return bitmapDrawable;
            } else if (clusterNum < 5) {

                Drawable bitmapDrawable = mBackDrawAbles.get(2);
                if (bitmapDrawable == null) {
                    bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                            Color.argb(159, 210, 154, 6)));
                    mBackDrawAbles.put(2, bitmapDrawable);
                }

                return bitmapDrawable;
            } else if (clusterNum < 10) {
                Drawable bitmapDrawable = mBackDrawAbles.get(3);
                if (bitmapDrawable == null) {
                    bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                            Color.argb(199, 217, 114, 0)));
                    mBackDrawAbles.put(3, bitmapDrawable);
                }

                return bitmapDrawable;
            } else {
                Drawable bitmapDrawable = mBackDrawAbles.get(4);
                if (bitmapDrawable == null) {
                    bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                            Color.argb(235, 215, 66, 2)));
                    mBackDrawAbles.put(4, bitmapDrawable);
                }

                return bitmapDrawable;
            }
        }


        /**
         * 点击监听
         *
         * @param marker       点击的聚合点
         * @param clusterItems
         */
        @Override
        public void onClick(Marker marker, final List<ClusterItem> clusterItems) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ClusterItem clusterItem : clusterItems) {
                builder.include(clusterItem.getPosition());
//              Log.e("msg",clusterItem.getPosition()+"");
//              Toast.makeText(getActivity(),clusterItem.getPosition()+"",Toast.LENGTH_LONG).show();

            }
            if (clusterItems.size() == 1) {
                Log.e("msg", clusterItems.toString());
                Log.e("msg", clusterItems.get(0).getTitle());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("videoId", clusterItems.get(0).getTitle());
                startActivity(intent);
            }
//          LatLngBounds latLngBounds = builder.build();
//          mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0)
//          );
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode==101){

                fgHomeCitynameTv.setText(data.getExtras().getString("city"));
                //可以移动倒指定地点
//                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(com.sctjsj.jcyn.util.Constants.BEIJING,10));
                String url1 ="http://restapi.amap.com/v3/geocode/geo?address="+data.getExtras().getString("city")+"&output=JSON&key=9454cbfc67b4a9b6483449e939f128be";
                Log.e("111111url",url1);
                OkHttpClient httpClient=new OkHttpClient();
                Request request=new Request.Builder()
                        .url(url1)
                        .build();
                httpClient.newCall(request).enqueue(new com.squareup.okhttp.Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("1111111onFailure",e.toString());

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
//                        Log.e("1111111ononResponse",response.body().string());
                            try {
                                JSONObject jsonObject=new JSONObject(response.body().string());
                                String status=jsonObject.getString("status");
                                if(status.equals("1")){
                                    JSONArray jsonArray=jsonObject.getJSONArray("geocodes");
                                    JSONObject jsonObject1=jsonArray.getJSONObject(0);
                                    String location=jsonObject1.getString("location");
                                    Log.e("11111111location",location);
                                    String[] split = location.split(",");
                                    double x=Double.valueOf(split[0]);
                                    double y=Double.valueOf(split[1]);
                                    Log.e("11111111locations",x+"    "+y);
                                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(y,x),10));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                });

            }
            else if(resultCode==103){
                String lan= data.getExtras().getString("lan");
                String lang=data.getExtras().getString("lang");
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(lan),Double.valueOf(lang)),16));
            }
        }

    }


//    /**
//     * 高德定位
//     */
//    private void initLocation() {
//        if (mLocationClient == null) {
//            mLocationClient = new AMapLocationClient(getActivity());
//        }
//        //初始化定位监听器
//        if (mLocationListener == null) {
//            mLocationListener = new AMapLocationListener() {
//                @Override
//                public void onLocationChanged(AMapLocation aMapLocation) {
//                    if (aMapLocation != null) {
//                        //获取定位错误码
//                        int errorCode = aMapLocation.getErrorCode();
//                        Log.e("indx",errorCode+"");
//                        //定位成功
//                        if (0 == errorCode) {
//                            /**
//                             * 解析地址
//                             */
//                            //当前POI
//                            String poi = aMapLocation.getPoiName();
//                            String cityName = aMapLocation.getCity();
//                            cityNameBean = new CityNameBean();
//                            Log.e("indx",cityName);
//                            //当前经纬度
//                            longT = aMapLocation.getLongitude();
//                            lanT = aMapLocation.getLatitude();
//                            cityNameBean.setLont(longT+"");
//                            cityNameBean.setLat(lanT+"");
//                            cityNameBean.setName(cityName);
//                            //设置城市名和移动倒中心
//                            fgHomeCitynameTv.setText(cityNameBean.getName());
//                            LatLng latLng = new LatLng(lanT,longT);
//                            mAMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
//                            msApp.saveLatLont(lanT+"",longT+"");
//                            mLocationClient.stopLocation();
//                        } else {
//                            //定位失败
//                            mLocationClient.stopLocation();
//                        }
//
//                    } else {
//                        //定位失败
//                        mLocationClient.stopLocation();
//                    }
//                }
//            };
//        }
//
//        //定位客户端设置定位监听器
//        mLocationClient.setLocationListener(mLocationListener);
//        /**
//         * 配置定位参数
//         */
//        if (mLocationClientOption == null) {
//            mLocationClientOption = new AMapLocationClientOption();
//        }
//        //设置定位模式：高精度定位模式
//        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //设置是否返回地址信息
//        mLocationClientOption.setNeedAddress(true);
//        //设置是否指定位一次
//        mLocationClientOption.setOnceLocation(false);
//        //如果设置了只定位一次，将获取3s内经度最高的一次定位结果
//        if (mLocationClientOption.isOnceLocation()) {
//            mLocationClientOption.setOnceLocationLatest(true);
//        }
//        //设置是否强制刷新wifi
//        mLocationClientOption.setWifiActiveScan(true);
//        //设置是否允许模拟位置
//        mLocationClientOption.setMockEnable(false);
//        //设置定位时间间隔5min
//        mLocationClientOption.setInterval(1000 * 60 * 5);
//        mLocationClient.setLocationOption(mLocationClientOption);
//        //开启定位
//        mLocationClient.startLocation();
//
//    }
}
