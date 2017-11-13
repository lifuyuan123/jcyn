package com.sctjsj.jcyn.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.UrltitleBean;
import com.sctjsj.jcyn.bean.UserDataBean;
import com.sctjsj.jcyn.bean.UserImgChange;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
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
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aaron on 2017/2/27.
 * 个人资料页
 */

public class UserDataActivity extends BaseAppcompatActivity implements TakePhoto.TakeResultListener,InvokeListener {
    String path=null;
    String imgId=null;
    private boolean isLogin=false;
    private MsApp msApp;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private String realName="";
    private Boolean updata=false;

    private UserDataBean userDataBean = new UserDataBean();
    //绑定布局
    @Bind(R.id.basegreen_second_title_tv)TextView smallTileTv;
    @Bind(R.id.basegreen_second_title_iv)ImageView smallTitleIv;
    @Bind(R.id.basegreen_title_tv)TextView titleTv;
    @Bind(R.id.activity_user_civ)CircleImageView userCiv;
    @Bind(R.id.activity_user_name_tv)TextView userName;
    @Bind(R.id.activity_user_sex_tv)TextView userSex;
    @Bind(R.id.activity_user_telephone_tv)TextView telephoneTv;
    @Bind(R.id.activity_user_place_tv)TextView adressTv;
    @Bind(R.id.activity_user_email_tv)TextView emailTV;
     private UserImgChange userImgChange;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //设置信息
                    setDataView();
                    break;
                case 1:
                    if(isLogin){
                        if(!msApp.getUserId().equals("none")){
                            //获取用户资料
                            getMsg(msApp.getUserId());
                        }
                    }
                    break;
            }
        }
    };

//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_data);
//        ButterKnife.bind(this);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
//        msApp= (MsApp) getApplication();
//        //设置头
//        smallTileTv.setText("编辑");
//        smallTitleIv.setVisibility(View.GONE);
//        titleTv.setText("个人资料");
//        isLoginMsg();
//        userCiv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createPop();
////                if(path!=null){
////                    upUserImg(path);
////                }
//            }
//        });
//    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_user_data);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        msApp= (MsApp) getApplication();
        //设置头
        smallTileTv.setText("编辑");
        smallTitleIv.setVisibility(View.GONE);
        titleTv.setText("个人资料");
        isLoginMsg();
        userCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPop();
//                if(path!=null){
//                    upUserImg(path);
//                }
            }
        });
    }

    @Override
    public void reloadData() {
        startActivity(new Intent(UserDataActivity.this,UserDataActivity.class));
        finish();
    }

    private void getMsg(String id)
    {
        //ctype=user&id=82&jf=headPortraitUrl&size=999
        List<UrltitleBean> list = new ArrayList<>();
        list.add(new UrltitleBean("ctype","user"));
        list.add(new UrltitleBean("id",id));
        list.add(new UrltitleBean("jf","headPortraitUrl"));
        list.add(new UrltitleBean("size","999"));
        LinUtil.newInstance().getMsg(BnUrl.UserDataUrl, list, this ,true, new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {
                Log.e("mmm",objects.toString());
                if(objects!=null){
                    try {
                        JSONObject data = objects.getJSONObject("data");
                        JSONObject headPortraitUrl = data.getJSONObject("headPortraitUrl");
                        userDataBean.setName(data.getString("username"));
                        userDataBean.setEmail(data.getString("email"));
                        userDataBean.setImgUrl(headPortraitUrl.getString("url"));
                        userDataBean.setTelephone(data.getString("phone"));
                        userDataBean.setAdress(data.getString("address"));
                        realName=data.getString("realName");
                        String sex=null;
                        switch (Integer.parseInt(data.getString("gender"))){
                            case 1:
                                sex = "男";
                                break;
                            case 2:
                                sex = "女";
                                break;
                            default:
                                sex = "";
                                break;
                        }
                        if(sex != null){
                            userDataBean.setSex(sex);
                        }
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }
    private void setDataView(){
        Picasso.with(this).load(userDataBean.getImgUrl()).into(userCiv);
        if(realName!=""){
            userName.setText(realName);
        }else {
            userName.setText(userDataBean.getName());
        }
        telephoneTv.setText(userDataBean.getTelephone());
        emailTV.setText(userDataBean.getEmail());
        userSex.setText(userDataBean.getSex());
        adressTv.setText(userDataBean.getAdress());
    }
    @OnClick({
            R.id.activity_user_email_ll,
            R.id.activity_user_phone_ll,
            R.id.basegreen_second_rl,
            R.id.basegreen_back_rl})
    public void userDataClick(View view){
        Intent intent=null;
        switch (view.getId()){
            case R.id.basegreen_second_rl:
                intent = new Intent(this,ChangeUserMsgActivity.class);
                intent.putExtra("name",userName.getText().toString());
                intent.putExtra("adress",adressTv.getText().toString());
                intent.putExtra("sex",userSex.getText().toString());
                startActivity(intent);
                finish();
                break;
            case R.id.activity_user_email_ll:
//                userImgChange.getUserImgChang().getImg(true);

                break;
            case R.id.activity_user_phone_ll:

                break;
            case R.id.basegreen_back_rl:
                setResult(200,new Intent().putExtra("updata",updata));
                finish();
                break;

        }
    }

    /**
     * 上传用户资料
     */
    public void upUserImg(String path){
        Log.i("uda",path);
        RequestParams params = new RequestParams(BnUrl.upLoadImgUrl);
        params.setHeader("cookie", SessionUtil.getCookie());
        Log.e("cuma",SessionUtil.getCookie());
        params.setMultipart(true);
        params.addBodyParameter("file",new File(path));
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("uda",result.toString());
                if(result!=null){
                    try {
                        boolean resultt=result.getBoolean("result");
                        if(resultt){
                            String resultData = result.getString("resultData");
                            JSONArray jsonArray = new JSONArray(resultData);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String acyId = jsonObject.getString("acyId");
                            String userId = msApp.getUserId();
                            changeUserImg(acyId,userId);
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
        final PopupDialog popDialog=new PopupDialog(UserDataActivity.this,data);
        popDialog.setOnItemClickListener(new PopupDialog.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position){
                    //拍照
                    case 0:
                        //裁剪参数
                        CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
                        getTakePhoto().onPickFromCaptureWithCrop(getUri(),cropOptions);
                        updata=true;
                        break;
                    //手機相冊
                    case 1:

                        //裁剪参数
                        CropOptions cropOptions1=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();
                        //裁剪后的输出
                        //系统ui
                        //takePhoto.onPickFromGalleryWithCrop(getUri(),cropOptions);
                        //自定义ui
                        getTakePhoto().onPickMultipleWithCrop(1,cropOptions1);
                        updata=true;
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
     * 修改用户头像
     */
    public void changeUserImg(String id,String userId){
        RequestParams params = new RequestParams(BnUrl.changeUserMsgUrl);
        params.setHeader("cookie",SessionUtil.getCookie());
        String dataId = "{id:"+userId+",headPortraitUrl:{id:"+id+"}}";
        params.addBodyParameter("ctype","user");
        params.addBodyParameter("data",dataId);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject objects) {
                Log.i("uda",objects.toString());
                if(objects!=null){
                    try {
                        boolean result = objects.getBoolean("result");
                        if(result){
                            getMsg(msApp.getUserId());
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
        Log.e("uuda",path);
    }

    /**
     * 图片选择失败
     * @param result
     * @param msg
     */
    @Override
    public void takeFail(TResult result, String msg) {
        Log.e("uda",msg+",,,,"+result.toString());
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
        if(keyCode==KeyEvent.KEYCODE_BACK){
            setResult(200,new Intent().putExtra("updata",updata));
        }
        return super.onKeyDown(keyCode, event);
    }

    public void isLoginMsg(){
        RequestParams params = new RequestParams(BnUrl.userLogin);
        params.setHeader("Cookie",SessionUtil.getCookie());
        Log.e("cuma",SessionUtil.getCookie());
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {

                Log.i("uda",result.toString());
                try {
                    isLogin = result.getBoolean("result");
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
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



}

