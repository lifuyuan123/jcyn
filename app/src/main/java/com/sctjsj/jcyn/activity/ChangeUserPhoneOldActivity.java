package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/3/13.
 */

public class ChangeUserPhoneOldActivity extends BaseAppcompatActivity {
    @Bind(R.id.base_title_tv)TextView title;
    @Bind(R.id.base_second_title_tv)TextView secondTitle;
    @Bind(R.id.old_phonenum_et)EditText oldEt;
    @Bind(R.id.old_phonepicture_et)EditText picEt;
    @Bind(R.id.old_phone_ll)LinearLayout llparent;
    @Bind(R.id.old_phone_iv)ImageView img;

    private OkHttpClient client;
    private String cookie;
    private LinUtil linUtil;
    private MsApp app;


    @Override
    public void setView() {
        setContentView(R.layout.activity_change_telephone_oldnum);
        ButterKnife.bind(this);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
        title.setText("修改手机号");
        secondTitle.setVisibility(View.GONE);
        app = (MsApp) getApplication();
        getPicVerifyCode();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicVerifyCode();
            }
        });

    }

    @Override
    public void reloadData() {

    }
    @OnClick({R.id.base_back_rl,R.id.old_phone_bt})
    public void oldOnclick(View view){
         switch (view.getId()){
             case R.id.base_back_rl:
                 finish();
                 break;
             case R.id.old_phone_bt:
                 if(checkB4Sms()){
                     checkOriTel();
                 }
                 break;
         }
    }



    /**
     * 加载验证码图片
     */
    private void getPicVerifyCode(){
//        mRLCodeParent.removeAllViews();
//        ImageView tempIV=new ImageView( ModifyTelphoneActivity.this);
//        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//        tempIV.setLayoutParams(params);
//        tempIV.setScaleType(ImageView.ScaleType.FIT_XY);
//        mRLCodeParent.addView(tempIV);
        client = new OkHttpClient();
        final CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        final OkHttpDownloader downloader = new OkHttpDownloader(client);
        Picasso picasso=new Picasso.Builder(ChangeUserPhoneOldActivity.this).downloader(downloader).build();
        picasso.load(BnUrl.getVerifyCode).skipMemoryCache().into(img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                List<HttpCookie> list=cookieManager.getCookieStore().getCookies();
                //获取此次请求的cookie
                if(list!=null&&list.size()>0){
                    cookie=list.get(0).toString();
                    Log.e("cookie",cookie);
                }
            }

            @Override
            public void onError() {
                Snackbar.make(llparent,"图片验证码加载失败",Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 发送短信之前，校验填写信息完整
     * @return
     */
    private boolean checkB4Sms(){

        if(TextUtils.isEmpty(oldEt.getText().toString())){
            Snackbar.make(llparent,"请填写原手机号",Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(picEt.getText().toString())){
            Snackbar.make(llparent,"请填写图片验证码",Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     *  发送短信
     */
    private void sendSms(){
        RequestParams params=new RequestParams(BnUrl.getSMSUrl);
        params.setUseCookie(false);
        params.addHeader("Cookie",cookie);
        params.addBodyParameter("code",picEt.getText().toString());
        params.addBodyParameter("mobile",oldEt.getText().toString());
        x.http().post(params, new Callback.ProgressCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

                try {
                    boolean results=response.getBoolean("result");
                    //发送成功
                    if(results){
                        Snackbar.make(llparent,"短信发送成功",Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(ChangeUserPhoneOldActivity.this,ChangeUserPhoneNewActivity.class);
                                intent.putExtra("cookie",cookie);
                                startActivity(intent);
                                finish();
                            }
                        }, 1500);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                linUtil.newInstance().dismissLoading();
                Snackbar.make(llparent,"短信发送失败",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                linUtil.newInstance().dismissLoading();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                linUtil.newInstance().showLoading(false,"提交中",ChangeUserPhoneOldActivity.this);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });
    }


    /**
     * 校验原手机号是否可用
     */
    private void checkOriTel(){
        RequestParams params=new RequestParams(BnUrl.validateOldTelUrl);
        params.setUseCookie(false);
        params.addHeader("Cookie",cookie);
        Log.e("cookie",cookie);
        params.addBodyParameter("phone",oldEt.getText().toString());
        params.addBodyParameter("code",picEt.getText().toString());
        params.addBodyParameter("userId",app.getUserId());

//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                Log.e("cuoa",result);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });

        x.http().post(params, new Callback.ProgressCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.e("cup",response.toString());
                if(response!=null){
                    try {
                        boolean result=response.getBoolean("result");
                        //验证通过
                        if(result){
                            sendSms();
                        } else {
                            Snackbar.make(llparent,"旧手机号验证失败",Snackbar.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("cuup",ex.toString());
                LinUtil.newInstance().dismissLoading();
                Snackbar.make(llparent,"旧手机号验证失败",Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                linUtil.newInstance().dismissLoading();
            }

            @Override
            public void onFinished() {
                linUtil.newInstance().dismissLoading();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                linUtil.newInstance().showLoading(false,"",ChangeUserPhoneOldActivity.this);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });

    }

}
