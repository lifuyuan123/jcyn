package com.sctjsj.jcyn.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.activity.VideoPlayActivity;
import com.sctjsj.jcyn.bean.MyFootview;
import com.sctjsj.jcyn.bean.UrltitleBean;
import com.sctjsj.jcyn.bean.VideoBean;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.ListViewUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import q.rorbin.qrefreshlayout.FooterView;
import q.rorbin.qrefreshlayout.QLoadView;
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

/**
 * Created by Aaron on 2017/2/6.
 */

public class VideoFg extends Fragment {
    private List<VideoBean> topList = new ArrayList<>();
    private List<VideoBean> videoList = new ArrayList<>();
//    @Bind(R.id.basegreen_back_rl)RelativeLayout backRl;
//    @Bind(R.id.basegreen_title_bg)RelativeLayout bgRl;
//    @Bind(R.id.basegreen_second_title_tv)TextView titleTv;
//    @Bind(R.id.basegreen_second_title_iv)ImageView imageView;
    @Bind(R.id.fg_video_video_lv)ListView listView;
//    @Bind(R.id.basegreen_second_rl)RelativeLayout relativeLayout;
    @Bind(R.id.fg_video_qrefresh)QRefreshLayout qRefreshLayout;


    @Bind(R.id.fg_video_1_iv)ImageView imageView1;
    @Bind(R.id.fg_video_1_tv)TextView  title1Tv;
    @Bind(R.id.fg_video_1_title_tv)TextView second1Tv;

    @Bind(R.id.fg_video_2_iv)ImageView imageView2;
    @Bind(R.id.fg_video_2_tv)TextView  title2Tv;
    @Bind(R.id.fg_video_2_title_tv)TextView second2Tv;

    @Bind(R.id.fg_video_3_iv)ImageView imageView3;
    @Bind(R.id.fg_video_3_tv)TextView  title3Tv;
    @Bind(R.id.fg_video_3_title_tv)TextView second3Tv;

    @Bind(R.id.fg_video_4_iv)ImageView imageView4;
    @Bind(R.id.fg_video_4_tv)TextView  title4Tv;
    @Bind(R.id.fg_video_4_title_tv)TextView second4Tv;

    private int num = 1;
    private MyAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    qRefreshLayout.refreshComplete();//下拉刷新
                    qRefreshLayout.LoadMoreComplete();//上拉加载更多
                    listView.setAdapter(new MyAdapter(videoList));
                    ListViewUtil.setListViewHeightBasedOnChildren(listView);
                    listView.setOnItemClickListener(onListListen);
                    listView.setFocusable(false);//失去焦点 防止回到此页面时不是上次离开的位置
                    break;
                case 1:
                    setVideoView(topList);
                    break;

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_video,null);
        ButterKnife.bind(this,view);
//        relativeLayout.setVisibility(View.GONE);
//        backRl.setVisibility(View.GONE);
//        titleTv.setVisibility(View.GONE);
//        imageView.setVisibility(View.VISIBLE);
//        imageView.setImageResource(R.mipmap.icon_fg_home_noction);
        getVideoMsg();
        getListMsg(num);
        qRefreshLayout.setLoadMoreEnable(true);
        qRefreshLayout.setRefreshHandler(new RefreshHandler() {
            //下啦刷新
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                getVideoMsg();
            }
            //加载更多
            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                num = num+1;
                getListMsg(num);

            }
        });


        return view;
    }
    @OnClick({R.id.fg_video_1,
            R.id.fg_video_2,
            R.id.fg_video_3,
            R.id.fg_video_4})
    public void videoFgOclic(View view){
        Intent intent = null;

        if(topList.size()!=0){
            switch (view.getId()){
                case R.id.fg_video_1:
                    intent = new Intent(getActivity(),VideoPlayActivity.class);
                    if(topList.get(0).getPlayUrl()!=null){
                        intent.putExtra("playUrl",topList.get(0).getPlayUrl());
                        intent.putExtra("introduction",topList.get(0).getIntroduction());
                        startActivity(intent);
                    }

                    break;
                case R.id.fg_video_2:
                    intent = new Intent(getActivity(),VideoPlayActivity.class);
                    if(topList.get(1).getPlayUrl()!=null){
                        intent.putExtra("playUrl",topList.get(1).getPlayUrl());
                        intent.putExtra("introduction",topList.get(1).getIntroduction());
                        startActivity(intent);
                    }

                    break;
                case R.id.fg_video_3:
                    intent = new Intent(getActivity(),VideoPlayActivity.class);
                    if(topList.get(2).getPlayUrl()!=null){
                        intent.putExtra("playUrl",topList.get(2).getPlayUrl());
                        intent.putExtra("introduction",topList.get(2).getIntroduction());
                        startActivity(intent);
                    }

                    break;
                case R.id.fg_video_4:
                    intent = new Intent(getActivity(),VideoPlayActivity.class);
                    if(topList.get(3).getPlayUrl()!=null){
                        intent.putExtra("playUrl",topList.get(3).getPlayUrl());
                        intent.putExtra("introduction",topList.get(3).getIntroduction());
                        startActivity(intent);
                    }

                    break;
            }
        }else {
            Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
        }

    }
    //上面四条数据
    public void getVideoMsg(){
       RequestParams params = new RequestParams(BnUrl.FgVideoTopUrl);
        x.http().get(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("video",result.toString());
                if(result!=null){
                    try {
                        JSONArray alist = result.getJSONArray("alist");
                        for(int i=0 ; i<alist.length() ; i++){
                            JSONObject jb = alist.getJSONObject(i);
                            VideoBean vb = new VideoBean();
                            vb.setTitle(jb.getString("title"));
                            vb.setScondTitle(jb.getString("subheading"));
                            vb.setImg(jb.getString("coverPhotoUrl"));
                            vb.setPlayUrl(jb.getString("url"));
                            vb.setIntroduction(jb.getString("introduction"));
                            topList.add(vb);
                        }
                        Message m = new Message();
                        m.what = 1;
                        handler.sendMessage(m);
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
              qRefreshLayout.refreshComplete();
            }
        });
    }
    //获取列表数据
    public void getListMsg(final int num){
        RequestParams params = new RequestParams(BnUrl.FgVideoListUrl);
        params.addBodyParameter("ctype","video");
        params.addBodyParameter("size","3");
        params.addBodyParameter("pageIndex",num+"");
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("video",result.toString());
                if(result!=null){
                    try {
                        JSONArray resultList = result.getJSONArray("resultList");
                            for(int i=0;i<resultList.length();i++){
                                JSONObject jb = resultList.getJSONObject(i);
                                VideoBean vb = new VideoBean();
                                vb.setImg(jb.getString("coverPhotoUrl"));
                                vb.setPlayUrl(jb.getString("url"));
                                vb.setTitle(jb.getString("title"));
                                vb.setScondTitle(jb.getString("subheading"));
                                vb.setIntroduction(jb.getString("introduction"));
                                Log.e("video",vb.toString());
                                videoList.add(vb);
                            }
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
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
                qRefreshLayout.LoadMoreComplete();
                adapter.notifyDataSetChanged();
            }
        });
    }

     public AdapterView.OnItemClickListener onListListen = new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
             intent.putExtra("playUrl",videoList.get(position).getPlayUrl());
             intent.putExtra("introduction",videoList.get(position).getIntroduction());
             startActivity(intent);
         }
     };
    /**
     * 设置布局
     */
    private void setVideoView(List<VideoBean> list){
        Picasso.with(getActivity()).load(list.get(0).getImg()).resize(150,150).into(imageView1);
        title1Tv.setText(list.get(0).getTitle());
        second1Tv.setText(list.get(0).getScondTitle());

        Picasso.with(getActivity()).load(list.get(1).getImg()).resize(150,150).into(imageView2);
        title2Tv.setText(list.get(1).getTitle());
        second2Tv.setText(list.get(1).getScondTitle());

        Picasso.with(getActivity()).load(list.get(2).getImg()).resize(150,150).into(imageView3);
        title3Tv.setText(list.get(2).getTitle());
        second3Tv.setText(list.get(2).getScondTitle());

        Picasso.with(getActivity()).load(list.get(3).getImg()).resize(150,150).into(imageView4);
        title4Tv.setText(list.get(3).getTitle());
        second4Tv.setText(list.get(3).getScondTitle());
    }
    private class MyAdapter extends BaseAdapter {
        List<VideoBean> list;

        public MyAdapter(List<VideoBean> list) {
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
            ViewHolder viewHolder = null;
            if(view == null){
                viewHolder = new ViewHolder();
                view=LayoutInflater.from(getActivity()).inflate(R.layout.fg_video_video_item,null);
                viewHolder.titleTV = (TextView) view.findViewById(R.id.fg_video_itme_title_tv);
                viewHolder.detailsTv = (TextView) view.findViewById(R.id.fg_video_item_detaisl_tv);
                viewHolder.img = (ImageView) view.findViewById(R.id.fg_video_item_iv);
                view.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) view.getTag();
            }
            viewHolder.detailsTv.setText(list.get(position).getIntroduction());
            viewHolder.titleTV.setText(list.get(position).getTitle());
            if(!TextUtils.isEmpty(list.get(position).getImg())){
                Picasso.with(getActivity()).load(list.get(position).getImg()).resize(150,150).into(viewHolder.img);
            }

            return view;
        }
        class ViewHolder{
            TextView titleTV,detailsTv;
            ImageView img;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        num = 1;
        videoList.clear();
    }
}
