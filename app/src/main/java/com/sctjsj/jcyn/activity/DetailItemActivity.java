package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.adapter.DetailsCommentAdapter;
import com.sctjsj.jcyn.bean.CommentBean;
import com.sctjsj.jcyn.util.BnUrl;
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

/**
 * Created by Aaron on 2017/3/27.
 */

public class DetailItemActivity extends BaseAppcompatActivity {
    @Bind(R.id.basegreen_second_rl)RelativeLayout relativeLayout;
    @Bind(R.id.basegreen_back_rl)RelativeLayout backRl;
    @Bind(R.id.basegreen_title_tv)TextView titleTv;
    @Bind(R.id.load_more_tv)TextView lodaTv;


    @Bind(R.id.activity_deail_item_iv)ImageView imageView;
    @Bind(R.id.activity_deail_item_title_tv)TextView title1Tv;
    @Bind(R.id.activity_deail_item_title)TextView locationTv;
    @Bind(R.id.activity_deail_item_phone)TextView telTv;
    @Bind(R.id.activity_deail_item_deail_tv)TextView deaiTv;

    @Bind(R.id.activity_deatils_item_comment_lv)ListView listView;

    private List<CommentBean> commentBeanList = new ArrayList<>();
    private int size = 1;
    private String itrmId;
    private boolean ismore=false;
    private DetailsCommentAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Log.i("da",commentBeanList.toString());
                    if(ismore){
                        lodaTv.setText("没有更多数据...");
                    }
                    adapter=new DetailsCommentAdapter(DetailItemActivity.this,commentBeanList);
                    listView.setAdapter(adapter);
                    ListViewUtil.setListViewHeightBasedOnChildren(listView);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void setView() {
        setContentView(R.layout.activity_deail_item);
        ButterKnife.bind(this);
        relativeLayout.setVisibility(View.GONE);
        titleTv.setText("简介页");

        if(getIntent()!=null){
            itrmId = (String) getIntent().getSerializableExtra("itemId");
            deaiTv.setText((String) getIntent().getSerializableExtra("synopsis"));
            getMsg(itrmId);
            getComment(itrmId,size+"");
        }

       lodaTv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
//               commentBeanList.clear();
//               getComments(itrmId,++size+3+"");

               size = size+1;
               getComment(itrmId,size+"");
           }
       });

        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void reloadData() {

    }

    //页面详情以及播放地址
    private void getMsg(String id){
        // http://118.123.22.190:8088/vr-zone/singleSearch$ajax.htm?
        // ctype=article&id=1&jf=thumbnail|videos|panorama&size=999
        Log.i("dda",id);

        RequestParams parms = new RequestParams(BnUrl.deailsActivityUrl);
        parms.addBodyParameter("ctype","article");
        parms.addBodyParameter("id",id);
        parms.addBodyParameter("jf","thumbnail|videos|panorama");
        parms.addBodyParameter("size","999");
        x.http().post(parms, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.i("dda",result.toString());
                if(result!=null){
                    try {
                        JSONObject data = result.getJSONObject("data");
                        JSONObject thumbnail = data.getJSONObject("thumbnail");
                        Picasso.with(DetailItemActivity.this).load(thumbnail.getString("url")).into(imageView);
                        title1Tv.setText(data.getString("title"));
                        locationTv.setText(data.getString("address"));
                        telTv.setText(data.getString("tel"));
//                        deaiTv.setText(data.getString("describes"));
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


    //品论
    private void getComments(String id ,String size){
        //ctype=article&cond={id:1}
        // &jf=comments|creator|headPortraitUrl|accessory
        // &size=3
        Log.e("abab",id);
        String detailId="{article:{id:"+id+"}}";
        RequestParams params = new RequestParams(BnUrl.deailsCommentUrl);
        params.addBodyParameter("ctype","comment");
        params.addBodyParameter("cond",detailId);
        params.addBodyParameter("jf","creator|headPortraitUrl|accessory");
        params.addBodyParameter("size",size);
//        String detailId="{article:{id:"+id+"}}";
//        RequestParams params = new RequestParams(BnUrl.deailsCommentUrl);
//        params.addBodyParameter("ctype","comment");
//        params.addBodyParameter("orderby","commentTime desc");
//        params.addBodyParameter("cond",detailId);
//        params.addBodyParameter("jf","creator|headPortraitUrl|accessory");
//        params.addBodyParameter("size","3");
//        params.addBodyParameter("pageIndex",size);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                if(result!=null){
                    try {
                        JSONArray resultList = result.getJSONArray("resultList");
                        Log.i("adaaaaa",resultList.toString());
                        if(resultList.toString().equals("[]")|| TextUtils.isEmpty(resultList.toString())){
                            ismore=true;
                        }
//                        JSONObject jsonObject = resultList.getJSONObject(0);
//                        JSONArray comments = jsonObject.getJSONArray("comments");
//                        for(int i = 0; i<comments.length() ; i++){
//                            JSONObject object = comments.getJSONObject(i);
//                            CommentBean cb = new CommentBean();
//                            cb.setCommentContent(object.getString("content"));
//                            cb.setCommentTime(object.getString("commentTime"));
//                            JSONObject accessory = object.getJSONObject("accessory");
//                            cb.setCommentImg(accessory.getString("url"));
//                            JSONObject creator = object.getJSONObject("creator");
//                            cb.setUserName(creator.getString("realName"));
//                            JSONObject headPortraitUrl = creator.getJSONObject("headPortraitUrl");
//                            cb.setUserImg(headPortraitUrl.getString("url"));
                        for(int i=0;i<resultList.length();i++){
                            JSONObject object = resultList.getJSONObject(i);
                            CommentBean cb = new CommentBean();
                            cb.setCommentContent(object.getString("content"));
                            cb.setCommentTime(object.getString("commentTime"));
                            JSONObject accessory = object.getJSONObject("accessory");
                            cb.setCommentImg(accessory.getString("url"));
                            JSONObject creator = object.getJSONObject("creator");
                            cb.setUserName(creator.getString("realName"));
                            JSONObject headPortraitUrl = creator.getJSONObject("headPortraitUrl");
                            cb.setUserImg(headPortraitUrl.getString("url"));
                            commentBeanList.add(cb);
                        }
//                            Log.i("ca",cb.toString());
//                            commentBeanList.add(cb);

                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ismore=true;
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


    //查询评论
    private void getComment(String id , final String size){
        lodaTv.setVisibility(View.GONE);
        String detailId="{article:{id:"+id+"}}";
        RequestParams params = new RequestParams(BnUrl.deailsCommentUrl);
        params.addBodyParameter("ctype","comment");
        params.addBodyParameter("orderby","commentTime desc");
        params.addBodyParameter("cond",detailId);
        params.addBodyParameter("jf","creator|headPortraitUrl|accessory");
        params.addBodyParameter("size","5");
        params.addBodyParameter("pageIndex",size);
        Log.e("pageIndex",size);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("aabcc",result.toString());
                if(result!=null){
                    try {

                        JSONArray resultList = result.getJSONArray("resultList");
                        if(resultList.toString().equals("[]")|| TextUtils.isEmpty(resultList.toString())){
                            Log.e("aabcc","null");
                            ismore=true;
                        }
                        Log.e("aabcc",""+resultList.toString());
                        for(int i=0;i<resultList.length();i++){
                            JSONObject object = resultList.getJSONObject(i);
                            CommentBean cb = new CommentBean();
                            cb.setCommentContent(object.getString("content"));
                            cb.setCommentTime(object.getString("commentTime"));
                            String accessory = object.getString("accessory");
                            String url = "vvv";
                            if(accessory!=null && !accessory.equals("null")){
                                url=object.getJSONObject("accessory").getString("url");
                            }
                            cb.setCommentImg(url);

                            JSONObject creator = object.getJSONObject("creator");
                            cb.setUserName(creator.getString("realName"));
                            JSONObject headPortraitUrl = creator.getJSONObject("headPortraitUrl");
                            cb.setUserImg(headPortraitUrl.getString("url"));
                            Log.e("aabcc",cb.toString());
//                                if((cb.getCommentImg().equals("vvv")&&cb.getCommentContent().equals(""))||
//                                        (cb.getCommentImg().equals("vvv")&&cb.getCommentContent()==null)){
//                                    ismore=true;
//                                }
                            commentBeanList.add(cb);
                        }
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    ismore=true;
                    Log.e("aabcc","meiyou");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("aabcc","onError"+ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("aabcc","onCancelled");
            }

            @Override
            public void onFinished() {
                if(commentBeanList.size()<1){
                    lodaTv.setVisibility(View.GONE);
                }else {
                    lodaTv.setVisibility(View.VISIBLE);
                }
//                detailsCommentAdapter.notifyDataSetChanged();
            }
        });
    }

}
