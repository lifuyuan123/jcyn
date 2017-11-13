package com.sctjsj.jcyn.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.bean.DetailsMsgBean;
import com.sctjsj.jcyn.bean.VideoImg;
import com.sctjsj.jcyn.util.BnUrl;
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
import q.rorbin.qrefreshlayout.QRefreshLayout;
import q.rorbin.qrefreshlayout.RefreshHandler;

public class VRvideoActivity extends BaseAppcompatActivity implements View.OnClickListener {
    @Bind(R.id.fg_video_video_lv)
    ListView fgVideoVideoLv;
    @Bind(R.id.fg_video_qrefresh)
    QRefreshLayout fgVideoQrefresh;
    @Bind(R.id.basegreen_back_rl)
    RelativeLayout basegreenBackRl;
    @Bind(R.id.basegreen_second_rl)
    RelativeLayout basegreenSecondRl;
//    @Bind(R.id.activity_deaile_video_iv)
//    ImageView VideoIv;
    private String videoid;
    private DetailsMsgBean detailsMsgBean = new DetailsMsgBean();
    private List<VideoImg> videolist=new ArrayList<>();
    private List<VideoImg> imagelist=new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
//                    Picasso.with(VRvideoActivity.this).load(detailsMsgBean.getVideoImg()).into(VideoIv);
                    fgVideoVideoLv.setAdapter(new MyVideoImgAdapter(videolist,VRvideoActivity.this));
                    break;
            }
        }
    };

    @Override
    public void setView() {
        setContentView(R.layout.activity_vrvideo);
        ButterKnife.bind(this);
        basegreenSecondRl.setVisibility(View.GONE);
        basegreenBackRl.setOnClickListener(this);
        Intent intent = getIntent();
        videoid = intent.getExtras().getString("videoid");
        getMsg(videoid);
        fgVideoQrefresh.setLoadMoreEnable(true);
        fgVideoQrefresh.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                fgVideoQrefresh.refreshComplete();
                videolist.clear();
                getMsg(videoid);
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
               fgVideoQrefresh.LoadMoreComplete();
            }
        });
    }

    @Override
    public void reloadData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.basegreen_back_rl:
                finish();
                break;
        }
    }


    //页面详情以及播放地址
    private void getMsg(String id) {
        // http://118.123.22.190:8088/vr-zone/singleSearch$ajax.htm?
        // ctype=article&id=1&jf=thumbnail|videos|panorama&size=999
        Log.i("dda", id);

        RequestParams parms = new RequestParams(BnUrl.deailsActivityUrl);
        parms.addBodyParameter("ctype", "article");
        parms.addBodyParameter("id", id);
        parms.addBodyParameter("jf", "thumbnail|videos|panorama");
        parms.addBodyParameter("size", "999");
        x.http().post(parms, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.i("dda", result.toString());
                if (result != null) {
                    try {
                        JSONObject data = result.getJSONObject("data");
                        JSONArray panorama = data.getJSONArray("panorama");
                        JSONArray videos = data.getJSONArray("videos");
                        for (int i = 0; i <videos.length() ; i++) {
                            JSONObject vd = videos.getJSONObject(i);
                            VideoImg v=new VideoImg();
                            v.setVideoImg(vd.getString("coverPhotoUrl"));
                            v.setVideoUrl(vd.getString("url"));
                            v.setTitle(vd.getString("title"));
                            v.setIntroduction(vd.getString("introduction"));
                            videolist.add(v);
                        }
                        Message message = new Message();
                        message.what = 1;
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

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    class MyVideoImgAdapter extends BaseAdapter{
        private List<VideoImg> list=new ArrayList<>();
        private Context c;
        private int type;

        public MyVideoImgAdapter(List<VideoImg> list, Context c) {
            this.list = list;
            this.c = c;
        }

//        @Override
//        public int getItemViewType(int position) {
//            if(position==list.size()){
//                type=1;
//            }else {
//                type=0;
//            }
//            return type;
//        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            MyViewHolder holder=null;
            textholder textholder=null;
            if(view==null){
//                if(type==0){
                holder=new MyViewHolder();
                view= LayoutInflater.from(c).inflate(R.layout.videoimgitem,viewGroup,false);
                holder.tv= (TextView) view.findViewById(R.id.text);
                holder.iv= (ImageView) view.findViewById(R.id.img);
                holder.text= (TextView) view.findViewById(R.id.image_text);
                view.setTag(holder);
//                }else if (type==1){
//                    textholder=new textholder();
//                    view=LayoutInflater.from(c).inflate(R.layout.acitivity_search_item,viewGroup,false);
//                    textholder.textView= (TextView) view.findViewById(R.id.activity_search_item_tv);
//                    view.setTag(textholder);
//                }

            }else {
//                if(type==0){
                    holder=(MyViewHolder)view.getTag();
//                }else if (type==1){
//                    textholder=(textholder)view.getTag();
//                }

            }
//            if(type==0){
                Picasso.with(c).load(list.get(i).getVideoImg()).into(holder.iv);
                holder.text.setText("VR视频");
                holder.tv.setText(list.get(i).getTitle());
                holder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(VRvideoActivity.this,VideoPlayActivity.class)
                                .putExtra("playUrl",list.get(i).getVideoUrl())
                        .putExtra("introduction",list.get(i).getIntroduction()));
                    }
                });
//            }else if (type==1){
//                textholder.textView.setText("已经全部加载");
//            }

            return view;
        }
        class MyViewHolder {
            ImageView iv;
            TextView tv,text;
        }
        class textholder{
            TextView textView;
        }

    }

}
