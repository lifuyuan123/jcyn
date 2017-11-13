package com.sctjsj.jcyn.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.activity.CityListActivity;
import com.sctjsj.jcyn.activity.DetailsActivity;
import com.sctjsj.jcyn.activity.ListActivity;
import com.sctjsj.jcyn.activity.QueryActivity;
import com.sctjsj.jcyn.activity.UserDataActivity;
import com.sctjsj.jcyn.adapter.CityAdapter;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.AnyEventType;
import com.sctjsj.jcyn.bean.ChangeCityName;
import com.sctjsj.jcyn.bean.CityNameBean;
import com.sctjsj.jcyn.bean.HomeFgPopBean;
import com.sctjsj.jcyn.bean.HomeFgTypeBean;
import com.sctjsj.jcyn.bean.HomeFgVideoBean;
import com.sctjsj.jcyn.bean.Location;
import com.sctjsj.jcyn.bean.UrltitleBean;
import com.sctjsj.jcyn.bean.UserImgChang;
import com.sctjsj.jcyn.bean.UserImgChange;
import com.sctjsj.jcyn.getui.DemoIntentService;
import com.sctjsj.jcyn.getui.DemoPushService;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.HorizontalListView;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.SessionUtil;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/2/6.
 */

public class HomeFg extends Fragment {
    private PopupWindow popupWindow;
    //banner图片集合
    List<String> bannerlist=new ArrayList<>();
    //type图片集合
    List<HomeFgTypeBean> typeList=new ArrayList();
    //推荐视频集合
    List<HomeFgVideoBean> videoList=new ArrayList();
    //poplv的集合
    List<HomeFgPopBean> popList = new ArrayList<>();
    //获取typeId
    String typeId=null;
    ListView popLv;
    private MsApp msApp;

    private UserImgChange userImgChange;
    @Bind(R.id.fg_home_hlv)HorizontalListView hlv;
    @Bind(R.id.home_fg_banner)Banner banner;
//    @Bind(R.id.basegreen_back_rl)LinearLayout ctiyRl;
//    @Bind(R.id.fg_home_cityname_tv)TextView cityName;

    //推荐视频
    @Bind(R.id.fg_home_recommend_1_tv)TextView like1Num;
    @Bind(R.id.fg_home_video1_iv)ImageView image1View;
    @Bind(R.id.fg_home_video1_title_tv)TextView titel1;
    @Bind(R.id.fg_home_video1_scondtitle_tv)TextView scondTitle1;

    @Bind(R.id.fg_home_recommend_2_tv)TextView like2Num;
    @Bind(R.id.fg_home_video2_iv)ImageView image2View;
    @Bind(R.id.fg_home_video2_title_tv)TextView titel2;
    @Bind(R.id.fg_home_video2_scondtitle_tv)TextView scondTitle2;

    @Bind(R.id.fg_home_recommend_3_tv)TextView like3Num;
    @Bind(R.id.fg_home_video3_iv)ImageView image3View;
    @Bind(R.id.fg_home_video3_title_tv)TextView titel3;
    @Bind(R.id.fg_home_video3_scondtitle_tv)TextView scondTitle3;

    @Bind(R.id.fg_home_recommend_4_tv)TextView like4Num;
    @Bind(R.id.fg_home_video4_iv)ImageView image4View;
    @Bind(R.id.fg_home_video4_title_tv)TextView titel4;
    @Bind(R.id.fg_home_video4_scondtitle_tv)TextView scondTitle4;

    @Bind(R.id.fg_home_recommend_5_tv)TextView like5Num;
    @Bind(R.id.fg_home_video5_iv)ImageView image5View;
    @Bind(R.id.fg_home_video5_title_tv)TextView titel5;
    @Bind(R.id.fg_home_video5_scondtitle_tv)TextView scondTitle5;

    @Bind(R.id.fg_home_recommend_6_tv)TextView like6Num;
    @Bind(R.id.fg_home_video6_iv)ImageView image6View;
    @Bind(R.id.fg_home_video6_title_tv)TextView titel6;
    @Bind(R.id.fg_home_video6_scondtitle_tv)TextView scondTitle6;

//    private CityListActivity.GetCityName getCityName;
    private CityListActivity cityListActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fg_home,null);
        ButterKnife.bind(this,view);
        getMsg();
        timeTask();

        msApp = (MsApp) getActivity().getApplication();
        EventBus.getDefault().register(this);

//        bannerlist.add(R.mipmap.banner_1);
//        bannerlist.add(R.mipmap.banner_2);
//        bannerlist.add(R.mipmap.banner_1);
//        bannerlist.add(R.mipmap.banner_2);
//        EventBus.getDefault().register(getActivity());
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setDelayTime(3000);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
//        ctiyRl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getActivity(), CityListActivity.class);
//                startActivity(intent);
//                getActivity().finish();
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    /**
     * 监听
     */
    @OnClick({R.id.fg_home_video1_ll,
            R.id.fg_home_video2_ll,
            R.id.fg_home_video3_ll,
            R.id.fg_home_video4_ll,
            R.id.fg_home_video5_ll,
            R.id.fg_home_video6_ll
//            , R.id.fg_home_query
    })
    public void homeFgClick(View view){
        Intent intent = null;
        if(videoList.size()!=0){
            switch (view.getId()){
                case R.id.fg_home_video1_ll:
                    if(videoList.get(0).getId()!=null){
                        intent=new Intent(getActivity(), DetailsActivity.class);
                        String videoId = videoList.get(0).getId();
                        intent.putExtra("videoId",videoId);
                        startActivity(intent);
                        Log.e("msg","ll1");
                    }
                    break;
                case R.id.fg_home_video2_ll:
                    if(videoList.get(1).getId()!=null){
                        intent=new Intent(getActivity(), DetailsActivity.class);
                        String videoId = videoList.get(1).getId();
                        intent.putExtra("videoId",videoId);
                        startActivity(intent);
                        Log.e("msg","ll2");
                    }
                    break;
                case R.id.fg_home_video3_ll:
                    if(videoList.get(2).getId()!=null){
                        intent=new Intent(getActivity(), DetailsActivity.class);
                        String videoId = videoList.get(2).getId();
                        intent.putExtra("videoId",videoId);
                        startActivity(intent);
                        Log.e("msg","ll3");
                    }
                    break;
                case R.id.fg_home_video4_ll:
                    if(videoList.get(3).getId()!=null){
                        intent=new Intent(getActivity(), DetailsActivity.class);
                        String videoId = videoList.get(3).getId();
                        intent.putExtra("videoId",videoId);
                        startActivity(intent);
                        Log.e("msg","ll4");
                    }
                    break;
                case R.id.fg_home_video5_ll:
                    if(videoList.get(4).getId()!=null){
                        intent=new Intent(getActivity(), DetailsActivity.class);
                        String videoId = videoList.get(4).getId();
                        intent.putExtra("videoId",videoId);
                        startActivity(intent);
                        Log.e("msg","ll4");
                    }
                    break;
                case R.id.fg_home_video6_ll:
                    if(videoList.get(5).getId()!=null){
                        intent=new Intent(getActivity(), DetailsActivity.class);
                        String videoId = videoList.get(5).getId();
                        intent.putExtra("videoId",videoId);
                        startActivity(intent);
                        Log.e("msg","ll4");
                    }
                    break;
//            case R.id.fg_home_query:
//                intent = new Intent(getActivity(), QueryActivity.class);
//                startActivity(intent);
//                break;
            }
        }else {
            Toast.makeText(msApp, "暂无数据", Toast.LENGTH_SHORT).show();
        }

    }



    /**
     * 网络请求
     */
    public void getMsg(){
        //请求头部栏
        LinUtil.newInstance().getMsg(BnUrl.homeFgTypeUrl, null,getContext(), false ,new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {
                Log.e("home",objects.toString());
               if(objects!=null){
                   try {
                       JSONArray clist=objects.getJSONArray("clist");
                       for(int i=0;i<clist.length();i++){
                           JSONObject jb=clist.getJSONObject(i);
                           HomeFgTypeBean hftb=new HomeFgTypeBean();
                           hftb.setId(jb.getString("id"));
                           hftb.setTitle(jb.getString("title"));
                           typeList.add(hftb);
                       }
                       Log.e("home",typeList.toString());
                       hlv.setAdapter(new MyAdapter());
                       hlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                           @Override
                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                               if (popupWindow != null&&popupWindow.isShowing()) {
                                   popupWindow.dismiss();
                               } else {
                                   initPopWindow();
                                   popList.clear();
                                   HomeFgPopBean hfpb=new HomeFgPopBean();
                                   hfpb.setTitle("全部");
                                   hfpb.setId("-1");
                                   popList.add(0,hfpb);
                                   popupWindow.showAtLocation(getActivity().getLayoutInflater().inflate(R.layout.activity_index,null),Gravity.RIGHT,0,0);
                                   typeId=typeList.get(position).getId();
                                   getTypePopMsg(typeId);
                               }
                           }
                       });
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
            }
        });
        //banner图请求
        LinUtil.newInstance().getMsg(BnUrl.homeFgBannerUrl, null,getContext(),false , new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {
                if(objects!=null){
                    try {
                        JSONArray homelist = objects.getJSONArray("homelist");
                        for(int i=0;i<homelist.length();i++){
                            JSONObject jb=homelist.getJSONObject(i);
                            JSONObject accessory = jb.getJSONObject("accessory");
                            String url = accessory.getString("url");
                            bannerlist.add(url);
                            Log.e("home",bannerlist.toString());
                        }
                        banner.setImages(bannerlist);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //推荐视频
        LinUtil.newInstance().getMsg(BnUrl.homeFgVideoUrl, null,getContext() ,false ,new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {

                if(objects!=null){
                    try {
                        JSONArray alist = objects.getJSONArray("alist");
                        for(int i=0;i<alist.length();i++){
                            JSONObject jb = alist.getJSONObject(i);
                            JSONObject thumbnail = jb.getJSONObject("thumbnail");
                            HomeFgVideoBean hfvb = new HomeFgVideoBean();
                            hfvb.setUrl(thumbnail.getString("url"));
                            hfvb.setId(jb.getString("id"));
                            hfvb.setTitle(jb.getString("title"));
                            hfvb.setScondTitle(jb.getString("keyword"));
                            hfvb.setCollectNumber(jb.getString("collectNumber"));
                            videoList.add(hfvb);
                        }
                           setVideoView(videoList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
//        LinUtil.newInstance().getMsg();
    }
    //设置弹窗
    public void initPopWindow(){
        View itemView = getActivity().getLayoutInflater().inflate(R.layout.popwindow_item,null);
        popupWindow = new PopupWindow(itemView,500,WindowManager.LayoutParams.MATCH_PARENT,true);
        popLv = (ListView) itemView.findViewById(R.id.popwindow_item_lv);
        popupWindow.setAnimationStyle(R.style.fg_home_popwin_anim_style);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(getDrawable());
    }




    /**
     * 顶部筛选适配器
     */
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return typeList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder vh=null;
            if(view==null){
                vh=new ViewHolder();
                view=LayoutInflater.from(getActivity()).inflate(R.layout.fg_home_gv_item,null);
                vh.textview = (TextView) view.findViewById(R.id.text_item);
                view.setTag(vh);
            }else{
                vh= (ViewHolder) view.getTag();
            }
            vh.textview.setText(typeList.get(position).getTitle());
            return view;
        }
        private class ViewHolder{
            TextView textview;
        }
    }

    /**
     * popitem  listview适配器
     */
    private class PopAdapter extends BaseAdapter{

        List<HomeFgPopBean> list;

        public PopAdapter(List<HomeFgPopBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder vh=null;
            if(view==null){
                vh= new ViewHolder();
                view=LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_item_lv_item,null);
                vh.textview = (TextView) view.findViewById(R.id.popwindow_item_lv_item_tv);
                view.setTag(vh);
            }else{
                vh= (ViewHolder) view.getTag();
            }
            vh.textview.setText(list.get(position).getTitle());
            return view;
        }
        private class ViewHolder{
            TextView textview;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }
    //隐藏popitem
    private Drawable getDrawable(){
        ShapeDrawable bgdrawable =new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(getActivity().getResources().getColor(android.R.color.transparent));
        return   bgdrawable;
    }
    //推荐视频设置
    private void setVideoView(List<HomeFgVideoBean> list){
        Picasso.with(getActivity()).load(list.get(0).getUrl()).placeholder(R.mipmap.icon_default_img).into(image1View);
        titel1.setText(list.get(0).getTitle());
        scondTitle1.setText(list.get(0).getScondTitle());
        like1Num.setText(list.get(0).getCollectNumber());

        Picasso.with(getActivity()).load(list.get(1).getUrl()).placeholder(R.mipmap.icon_default_img).into(image2View);
        titel2.setText(list.get(1).getTitle());
        scondTitle2.setText(list.get(1).getScondTitle());
        like2Num.setText(list.get(1).getCollectNumber());

        Picasso.with(getActivity()).load(list.get(2).getUrl()).placeholder(R.mipmap.icon_default_img).into(image3View);
        titel3.setText(list.get(2).getTitle());
        scondTitle3.setText(list.get(2).getScondTitle());
        like3Num.setText(list.get(2).getCollectNumber());

        Picasso.with(getActivity()).load(list.get(3).getUrl()).placeholder(R.mipmap.icon_default_img).into(image4View);
        titel4.setText(list.get(3).getTitle());
        scondTitle4.setText(list.get(3).getScondTitle());
        like4Num.setText(list.get(3).getCollectNumber());

        Picasso.with(getActivity()).load(list.get(4).getUrl()).placeholder(R.mipmap.icon_default_img).into(image5View);
        titel5.setText(list.get(4).getTitle());
        scondTitle5.setText(list.get(4).getScondTitle());
        like5Num.setText(list.get(4).getCollectNumber());

        Picasso.with(getActivity()).load(list.get(5).getUrl()).placeholder(R.mipmap.icon_default_img).into(image6View);
        titel6.setText(list.get(5).getTitle());
        scondTitle6.setText(list.get(5).getScondTitle());
        like6Num.setText(list.get(5).getCollectNumber());
    }

    //type的分类请求
    private void getTypePopMsg(final String typeId){
        final List<UrltitleBean> list = new ArrayList<>();
        String cond = "{parent:{id:"+typeId+"}}";
        list.add(new UrltitleBean("ctype","column"));
        list.add(new UrltitleBean("cond",cond));
        LinUtil.newInstance()
                .getMsg(BnUrl.homeFgPopUrl, list, getContext(),false ,new LinUtil.OnNetRequestListner() {
                    @Override
                    public void onSuccess(JSONObject objects) {
                        try {
                            JSONArray resultList = objects.getJSONArray("resultList");
                            for(int i=0;i<resultList.length();i++){
                               JSONObject jb = resultList.getJSONObject(i);
                                HomeFgPopBean hfpb=new HomeFgPopBean();
                                hfpb.setId(jb.getString("id"));
                                hfpb.setTitle(jb.getString("title"));
                                popList.add(hfpb);
                            }
                            popLv.setAdapter(new PopAdapter(popList));
                            popLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    //根据二级分类跳转列表页
                                    Intent intent = new Intent(getActivity(), ListActivity.class);
                                    intent.putExtra("typeId",typeId);
                                    intent.putExtra("popId",popList.get(position).getId());
                                    Log.e("find",typeId+"  ,"+popList.get(position).getId() );
                                    startActivity(intent);
                                    popupWindow.dismiss();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //布局销毁时清除数据
    @Override
    public void onDestroyView() {
        videoList.clear();
        bannerlist.clear();
        typeList.clear();
        EventBus.getDefault().unregister(getActivity());
        msApp.getSPF().edit().putString("cityName",null).commit();
        if(msApp.getSPF().contains("cityName")){
            msApp.getSPF().edit().remove("cityName");
        }
        super.onDestroyView();

    }

    //    @Subscribe

    @Subscribe
        public void onEventMainThread(AnyEventType anyEventType){
        CityNameBean cityNameBean = new CityNameBean();
        cityNameBean = anyEventType.getCityName();
        Log.e("hff",msApp.getSPF().getString("cityName","none"));
        if(!msApp.getSPF().getString("cityName","none").equals("none") && !msApp.getSPF().getString("cityName","none").equals("")){
//            cityName.setText(msApp.getSPF().getString("cityName","none"));
               msApp.saveCityName(cityNameBean.getName());

        }else {
//            cityName.setText(cityNameBean.getName());
        }
        msApp.saveLatLont(cityNameBean.getLat(),cityNameBean.getLont());
    }

    /**
     * 定时任务
     */
    public void timeTask(){
        Timer timer=new Timer(true);
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                getMssg();
            }
        };
        timer.schedule(timerTask,0,1000*10*60);
    }

    public void getMssg(){
        RequestParams params=new RequestParams(BnUrl.userLogin);
        params.setUseCookie(false);
        params.setHeader("cookie", SessionUtil.getCookie());
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {

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
}
