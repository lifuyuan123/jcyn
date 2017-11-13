package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.adapter.DetailsCommentAdapter;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.Accessory;
import com.sctjsj.jcyn.bean.Article;
import com.sctjsj.jcyn.bean.CommentBean;
import com.sctjsj.jcyn.bean.Comments;
import com.sctjsj.jcyn.bean.Creator;
import com.sctjsj.jcyn.bean.DetailsMsgBean;
import com.sctjsj.jcyn.bean.ReCommentBean;
import com.sctjsj.jcyn.bean.UrltitleBean;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.ListViewUtil;
import com.sctjsj.jcyn.util.PopupDialog;
import com.sctjsj.jcyn.util.SessionUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/2/27.
 * 详情页面
 */

public class DetailsActivity extends BaseAppcompatActivity implements TakePhoto.TakeResultListener,InvokeListener {
    List<CommentBean> commentBeanList = new ArrayList<>();

    List<ReCommentBean> reCommentBeanList = new ArrayList<>();
    @Bind(R.id.activity_deails_et)EditText editText;
    //绑定布局
    @Bind(R.id.basegreen_second_title_tv)TextView smallTileTv;
    @Bind(R.id.basegreen_second_title_iv)ImageView smallTitleIv;
    @Bind(R.id.basegreen_title_tv)TextView titleTv;
    @Bind(R.id.activity_deatils_comment_lv)ListView commentLv;
    //封面
    @Bind(R.id.activity_deails_cover_iv)ImageView cover;
    //视频
    @Bind(R.id.activity_deaile_video_iv)ImageView videoImg;
    //全景图
    @Bind(R.id.activity_deaile_picture_iv)ImageView pictureImg;
    //简介
    @Bind(R.id.activity_deails_describes_tv)TextView describesTv;
    //地址
    @Bind(R.id.activity_deails_adress_tv)TextView adressTv;


    @Bind(R.id.activity_deails_recomment_1)LinearLayout linearLayout1;
    @Bind(R.id.activity_deails_recomment_2)LinearLayout linearLayout2;

    /**
     * 推荐
     */
    @Bind(R.id.activity_deails_1_recomment_iv)ImageView reImg1;
    @Bind(R.id.activity_deails_1_recomment_title)TextView reTirle1;
    @Bind(R.id.activity_deails_1_recomment_indroation)TextView reIndroation1;
    @Bind(R.id.activity_deails_1_recomment_collect)TextView reCollect1;

    @Bind(R.id.activity_deails_recomment_2_iv)ImageView reImg2;
    @Bind(R.id.activity_deails_recomment_2_title)TextView reTirle2;
    @Bind(R.id.activity_deails_recomment_2_indroation)TextView reIndroation2;
    @Bind(R.id.activity_deails_recomment_2_collect)TextView reCollect2;

    @Bind(R.id.load_more_tv) TextView textView;

    private DetailsMsgBean detailsMsgBean = new DetailsMsgBean();
    private int size = 1;
    private MsApp msApp;
    String path = null;
    String acyId ;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private DetailsCommentAdapter detailsCommentAdapter;
    private boolean islogin=false;
    private boolean ismore=false;
    private String synopsis;



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Log.i("daaaaaa",commentBeanList.toString());
                    if(ismore){
                        textView.setText("没有更多数据...");
                    }
                    detailsCommentAdapter=new DetailsCommentAdapter(DetailsActivity.this,commentBeanList);
                    commentLv.setAdapter(detailsCommentAdapter);
                    detailsCommentAdapter.notifyDataSetChanged();
                    ListViewUtil.setListViewHeightBasedOnChildren(commentLv);
                    break;
                case 1:
                    Log.i("dda",detailsMsgBean.toString());
                     setDetaulView();
                    break;
                case 2:
                     setReCommentVIew(reCommentBeanList);
                    linearLayout1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailsActivity.this,DetailsActivity.class);
                            intent.putExtra("videoId",reCommentBeanList.get(0).getId());
                            startActivity(intent);
                            finish();
                        }
                    });
                    linearLayout2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailsActivity.this,DetailsActivity.class);
                            intent.putExtra("videoId",reCommentBeanList.get(1).getId());
                            startActivity(intent);
                            finish();
                        }
                    });
                    break;
            }
        }
    };



    //跳转页面传递的请求数据
    String videoId=null;
    @Override
    public void setView() {
        setContentView(R.layout.activity_deails);
        ButterKnife.bind(this);
        //设置title信息
        isLogin();
        smallTileTv.setVisibility(View.GONE);
//        smallTitleIv.setImageResource(R.mipmap.icon_location_white);
        //收藏图标
        smallTitleIv.setImageResource(R.mipmap.collection_white);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        titleTv.setText("详情页面");
        msApp = (MsApp) getApplication();
        Intent intent=getIntent();
        if(intent!=null){
            videoId = (String) intent.getSerializableExtra("videoId");
            Log.e("msg",videoId);
            getMsg(videoId);
            getComments(videoId,size+"");
        }

        getReComments();


    }

    @OnClick({R.id.basegreen_back_rl,
            R.id.basegreen_second_rl,
            R.id.activity_deails_panorama_rl,
            R.id.activitu_deails_video_rl,
            R.id.basegreen_second_title_iv,
            R.id.activity_deails_upload_img,
            R.id.activity_deails_comment_bt,
            R.id.load_more_tv,
            R.id.activity_deails_describes_ll})
    public void deatilClick(View view){
        switch (view.getId()){
            case R.id.basegreen_back_rl:
                finish();
                break;
            case R.id.basegreen_second_rl:
                break;
            case R.id.activity_deails_panorama_rl:
                  if(detailsMsgBean.getPictureUrl()!=null){
                      Intent intent = new Intent(this,PanoramaActivity.class);
                      intent.putExtra("videoid",videoId);
//                      intent.putExtra("panoramaUrl",detailsMsgBean.getPictureUrl());
                      Log.e("abc",detailsMsgBean.getPictureUrl());
                      startActivity(intent);
                  }else {
                      Toast.makeText(this,"暂没有全景图片可供观赏",Toast.LENGTH_LONG).show();
                  }
            break;
            case R.id.activitu_deails_video_rl:
                if(detailsMsgBean.getVideoUrl()!=null){
                    Intent intent = new Intent(this,VRvideoActivity.class);
                    intent.putExtra("videoid",videoId);
//                    intent.putExtra("playUrl",detailsMsgBean.getVideoUrl());
                    startActivity(intent);
                }else {
                    Toast.makeText(this,"暂没有全景视频可供观赏",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.basegreen_second_title_iv:
                if(videoId!=null){
                   isCollage(videoId);
                }
                break;
            //上传图片
            case R.id.activity_deails_upload_img:
                createPop();
                break;
            //发表品论
            case R.id.activity_deails_comment_bt:
                if(islogin){
                    if(editText.getText().length()>0){
                        userUpComment(editText.getText().toString(),videoId,acyId);
                    }else {
                        Toast.makeText(DetailsActivity.this, "您还没输入评论内容", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(DetailsActivity.this,"您还没有登录，登录后可发表评论。", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.load_more_tv:
                size = size+1;
                getComments(videoId,size+"");
                break;
            case R.id.activity_deails_describes_ll:
                Intent intent = new Intent(this,DetailItemActivity.class);
                if(videoId!=null){
                    intent.putExtra("itemId",videoId);
                    intent.putExtra("synopsis",synopsis);
                    startActivity(intent);
                }
                break;
        }

    }

    /**
     * 上传用户资料
     */
    public void upUserImg(String path){
        RequestParams params = new RequestParams(BnUrl.upLoadImgUrl);
        params.setHeader("cookie", SessionUtil.getCookie());
        Log.e("cuma",SessionUtil.getCookie());
        params.setMultipart(true);
        params.addBodyParameter("file",new File(path));
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("img",result.toString());
                if(result!=null){
                    try {
                        boolean resultt=result.getBoolean("result");
                        if(resultt){
                            String resultData = result.getString("resultData");
                            JSONArray jsonArray = new JSONArray(resultData);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            acyId = jsonObject.getString("acyId");
                            Log.e("data",acyId);
                        }
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

    /**
     * 用户发表品论
     */
    public void userUpComment(String msg,String deailsId,String imgId){
        RequestParams params = new RequestParams(BnUrl.userUpContentUrl);
        params.setHeader("cookie",SessionUtil.getCookie());
        params.addBodyParameter("userid",msApp.getUserId());
        params.addBodyParameter("articleid",deailsId);
        params.addBodyParameter("content",msg);
        if(imgId!=null){
            params.addBodyParameter("accessoryid",imgId);
        }else{
            params.addBodyParameter("accessoryid","-1");
        }
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject objects) {
                Log.e("data",objects.toString());
                commentBeanList.clear();
                if(objects!=null){
                    try {
                        boolean result = objects.getBoolean("result");
                        if(result){
                            getComments(videoId,size+"");
                            Toast.makeText(DetailsActivity.this,"评论成功",Toast.LENGTH_LONG).show();
                            editText.setText("");
                        }
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

    //查询评论
    private void getComments(String id , final String size){
        textView.setVisibility(View.GONE);
        String detailId="{article:{id:"+id+"}}";
        RequestParams params = new RequestParams(BnUrl.deailsCommentUrl);
        params.addBodyParameter("ctype","comment");
        params.addBodyParameter("orderby","commentTime desc");
        params.addBodyParameter("cond",detailId);
        params.addBodyParameter("jf","creator|headPortraitUrl|accessory");
        params.addBodyParameter("size","3");
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
                    textView.setVisibility(View.GONE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }
//                detailsCommentAdapter.notifyDataSetChanged();
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
                        detailsMsgBean.setAdress(data.getString("address"));
                        detailsMsgBean.setDescribes(data.getString("describes"));
                        detailsMsgBean.setLat(data.getString("lat"));
                        detailsMsgBean.setLng(data.getString("lng"));
                        JSONArray panorama = data.getJSONArray("panorama");
                        JSONObject pna = panorama.getJSONObject(0);
                        detailsMsgBean.setPictureImg(pna.getString("coverPhotoUrl"));
                        detailsMsgBean.setPictureUrl(pna.getString("panoramaUrl"));
                        JSONObject thumbnail = data.getJSONObject("thumbnail");
                        detailsMsgBean.setCover(thumbnail.getString("url"));
                        JSONArray videos = data.getJSONArray("videos");
                        JSONObject vd = videos.getJSONObject(0);
                        detailsMsgBean.setVideoImg(vd.getString("coverPhotoUrl"));
                        detailsMsgBean.setVideoUrl(vd.getString("url"));
                        synopsis=vd.getString("introduction");


                        Log.i("ddda",pna.getString("panoramaUrl"));
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




    //推荐
    private void getReComments(){
     RequestParams params = new RequestParams(BnUrl.deailsRecommentUrl);
        params.addBodyParameter("jf","thumbnail");
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                if(result!=null){
                    try {
                        JSONArray alist = result.getJSONArray("alist");
                        ReCommentBean rcb = null;
                        for(int i=0; i<alist.length() ;i++){
                            JSONObject object = alist.getJSONObject(i);
                            rcb = new ReCommentBean();
                            rcb.setId(object.getString("id"));
                            rcb.setCollect(object.getString("collectNumber"));
                            rcb.setTitle(object.getString("title"));
                            rcb.setIndration(object.getString("keyword"));
                            JSONObject object1 = object.getJSONObject("thumbnail");
                            rcb.setImg(object1.getString("url"));
                            reCommentBeanList.add(rcb);
                        }

                        Message message = new Message();
                        message.what = 2;
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

    private void setDetaulView(){
        if(detailsMsgBean.getTitle()!=null){
            titleTv.setText(detailsMsgBean.getTitle());
        }
        if(detailsMsgBean.getCover()!=null){
            Picasso.with(this).load(detailsMsgBean.getCover()).into(cover);
        }
        if(detailsMsgBean.getPictureImg()!=null){
            Picasso.with(this).load(detailsMsgBean.getPictureImg()).into(pictureImg);
        }
        if(detailsMsgBean.getVideoImg()!=null){
            Picasso.with(this).load(detailsMsgBean.getVideoImg()).into(videoImg);
        }
        describesTv.setText(detailsMsgBean.getDescribes());
        adressTv.setText(detailsMsgBean.getAdress());
        describesTv.setText(synopsis);

    }
    private void setReCommentVIew(List<ReCommentBean> list){
        Picasso.with(this).load(list.get(0).getImg()).into(reImg1);
        Picasso.with(this).load(list.get(1).getImg()).into(reImg2);
        reTirle1.setText(list.get(0).getTitle());
        reTirle2.setText(list.get(1).getTitle());
        reCollect1.setText(list.get(0).getCollect());
        reCollect2.setText(list.get(1).getCollect());
        reIndroation1.setText(list.get(0).getIndration());
        reIndroation2.setText(list.get(1).getIndration());
    }

    /**
     * 收藏
     */
    public void userColleage(String id){
        // user_id=134&article_id=1
        RequestParams params = new RequestParams(BnUrl.userColleageUrl);
        params.setHeader("cookie", SessionUtil.getCookie());
        params.addBodyParameter("user_id",msApp.getUserId());
        params.addBodyParameter("article_id",id);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject object) {
                if(object!=null){
                    try {
                        boolean result = object.getBoolean("result");
                        if(result){
                            Toast.makeText(DetailsActivity.this,"收藏成功",Toast.LENGTH_LONG).show();
                        }
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

    /**
     * 判断是否已经被收藏
     */
   public void isCollage(final String id){
       // id=136&article_id=1
       RequestParams params = new RequestParams(BnUrl.isCollectUrl);
       params.addBodyParameter("id",msApp.getUserId());
       params.addBodyParameter("article_id",id);
       x.http().post(params, new Callback.CommonCallback<JSONObject>() {
           @Override
           public void onSuccess(JSONObject result) {
                if(result!=null){
                    try {
                        boolean resultt = result.getBoolean("result");
                        if(resultt){
                            Toast.makeText(DetailsActivity.this,"已经被收藏",Toast.LENGTH_LONG).show();
                        }else{
                            userColleage(id);
                        }
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




    /**
     * 弹出头像修改提示框
     */
    public void createPop(){
        List<String> data=new ArrayList<>();
        data.add("拍照");
        data.add("手机相册");
        final PopupDialog popDialog=new PopupDialog(DetailsActivity.this,data);
        popDialog.setOnItemClickListener(new PopupDialog.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position){
                    //拍照
                    case 0:
                        //裁剪参数
                        CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
                        getTakePhoto().onPickFromCaptureWithCrop(getUri(),cropOptions);
                        break;
                    //手機相冊
                    case 1:

                        //裁剪参数
                        CropOptions cropOptions1=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
                        //裁剪后的输出
                        //系统ui
                        getTakePhoto().onPickFromGalleryWithCrop(getUri(),cropOptions1);
                        //自定义ui
//                        getTakePhoto().onPickMultipleWithCrop(1,cropOptions1);

                        break;
                    //取消
                    case 2:
                        popDialog.dismiss();
                        break;
                }
            }
        });
        popDialog.show();
    }

    /**
     * 图片保存路径
     * @return
     */
    private Uri getUri(){
        File file=new File(Environment.getExternalStorageDirectory(), "/Hywater/images/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);
        return imageUri;
    }

    /**
     * 页面跳转回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }


    /**
     * 图片选择成功
     * @param result
     */
    @Override
    public void takeSuccess(TResult result) {
        Log.e("msg",result.getImage().getPath().toString());
        Bitmap bitmap= BitmapFactory.decodeFile(result.getImage().getPath());
//        showLv.setImageBitmap(bitmap);
//        btLv.setVisibility(View.GONE);
        path=result.getImage().getPath();
        upUserImg(path);
    }

    /**
     * 图片选择失败
     * @param result
     * @param msg
     */
    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //创建TakePhoto实例
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return takePhoto;
    }

    /**
     * 权限申请回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //处理运行时权限
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
    }

    /**
     * 重写返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        size = 1;
    }

    /**
     * 判断用户是否登录
     */
    public void isLogin() {
        LinUtil.newInstance().getMsg(BnUrl.userLogin, null, this, false, new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {
                try {
                    boolean result = objects.getBoolean("result");
                    if (result) {
                        islogin = true;
                    }else {
                        islogin=false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
