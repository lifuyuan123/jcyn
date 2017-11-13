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

public class PanoramaActivity extends BaseAppcompatActivity {


    @Bind(R.id.basegreen_back_rl)
    RelativeLayout basegreenBackRl;
    @Bind(R.id.fg_video_video_lv)
    ListView fgVideoVideoLv;
    @Bind(R.id.fg_video_qrefresh)
    QRefreshLayout fgVideoQrefresh;
    @Bind(R.id.activity_panorama2)
    LinearLayout activityPanorama2;
    private List<VideoImg> imagelist=new ArrayList<>();
    private String videoid;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    fgVideoVideoLv.setAdapter(new MyImgAdapter(imagelist,PanoramaActivity.this));
                    break;
            }
        }
    };

    @Override
    public void setView() {
        setContentView(R.layout.activity_panorama2);
        ButterKnife.bind(this);
        videoid=getIntent().getStringExtra("videoid");
        getMsg(videoid);
        fgVideoQrefresh.setLoadMoreEnable(true);
        fgVideoQrefresh.setRefreshHandler(new RefreshHandler() {
            @Override
            public void onRefresh(QRefreshLayout refresh) {
                fgVideoQrefresh.refreshComplete();
                imagelist.clear();
                getMsg(videoid);
            }

            @Override
            public void onLoadMore(QRefreshLayout refresh) {
                fgVideoQrefresh.LoadMoreComplete();
            }
        });
        basegreenBackRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void reloadData() {
    getMsg(videoid);
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
                        JSONObject thumbnail = data.getJSONObject("thumbnail");
                        for (int i = 0; i < panorama.length(); i++) {
                            JSONObject pna = panorama.getJSONObject(i);
                            VideoImg v=new VideoImg();
                            v.setPictureImg(pna.getString("coverPhotoUrl"));
                            v.setPictureUrl(pna.getString("panoramaUrl"));
                            v.setTitle(pna.getString("title"));
                            v.setIntroduction(pna.getString("description"));
                            imagelist.add(v);
                            Log.i("ddda", pna.getString("panoramaUrl"));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyImgAdapter extends BaseAdapter {
        private List<VideoImg> list=new ArrayList<>();
        private Context c;
        private int type;

        public MyImgAdapter(List<VideoImg> list, Context c) {
            this.list = list;
            this.c = c;
        }

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
            if(view==null){
                holder=new MyViewHolder();
                view= LayoutInflater.from(c).inflate(R.layout.videoimgitem,viewGroup,false);
                holder.tv= (TextView) view.findViewById(R.id.text);
                holder.iv= (ImageView) view.findViewById(R.id.img);
                holder.text= (TextView) view.findViewById(R.id.image_text);
                view.setTag(holder);
            }else {
                holder=(MyViewHolder)view.getTag();
            }
            Picasso.with(c).load(list.get(i).getPictureImg()).into(holder.iv);
            holder.text.setText("720全景");
            holder.tv.setText(list.get(i).getTitle());
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(PanoramaActivity.this,PanoramaPlayActivity.class).putExtra("panoramaUrl",list.get(i).getPictureUrl()));
                }
            });
            return view;
        }
        class MyViewHolder {
            ImageView iv;
            TextView tv,text;
        }

    }
}
