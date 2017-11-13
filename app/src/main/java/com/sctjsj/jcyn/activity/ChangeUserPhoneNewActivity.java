package com.sctjsj.jcyn.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/3/13.
 */

public class ChangeUserPhoneNewActivity extends BaseAppcompatActivity {
    @Bind(R.id.base_title_tv)TextView title;
    @Bind(R.id.base_second_title_tv)TextView secondTitle;

    //容器布局
    @Bind(R.id.activity_change_telephone_new_ll)LinearLayout mLLParent;
    //新手机号
    @Bind(R.id.new_phonenum_et)EditText mETNewTel;
    //原手机号
    @Bind(R.id.new_oldphonesms_et)EditText mETSms;

    private String cookie;
    private MsApp app;

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_telephone_newnum);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
        title.setText("修改手机号");
        secondTitle.setVisibility(View.GONE);
        app = (MsApp) getApplication();

        cookie=getIntent().getStringExtra("cookie");
        //避免获取不到 cookie
        if(TextUtils.isEmpty(cookie)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("异常提示");
            builder.setIcon(R.mipmap.icon_app_start);
            builder.setMessage("程序发生了未知错误，即将退出！");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(ChangeUserPhoneNewActivity.this,IndexActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.show();
        }

    }

    @Override
    public void reloadData() {

    }
    @OnClick({R.id.base_back_rl,R.id.new_phone_bt})
    public void newPhoneOnclick(View view){
        switch (view.getId()){
            case R.id.base_back_rl:
                finish();
                break;
            case R.id.new_phone_bt:
                if(checkB4Sms()){
                    checkNewTel();
                }
                break;
        }
    }



    private boolean checkB4Sms(){

        if(TextUtils.isEmpty(mETNewTel.getText().toString())){
            Snackbar.make(mLLParent,"请输入新手机号",Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(mETSms.getText().toString())){
            Snackbar.make(mLLParent,"请输入短信验证码",Snackbar.LENGTH_SHORT).show();
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
        params.addBodyParameter("code",mETSms.getText().toString());
        params.addBodyParameter("mobile",mETNewTel.getText().toString());
        params.addBodyParameter("isCode","FALSE");
        x.http().post(params, new Callback.ProgressCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("发送短信", result.toString());
                try {
                    boolean results=result.getBoolean("result");
                    //发送成功
                    if(results){
                        Snackbar.make(mLLParent,"短信发送成功",Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(ChangeUserPhoneNewActivity.this,BindNewPhoneActivity.class);
                                intent.putExtra("cookie",cookie);
                                intent.putExtra("tel",mETNewTel.getText().toString());
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
                Snackbar.make(mLLParent,"短信发送失败",Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });
    }

    /**
     * 校验新手机号
     */
    private void checkNewTel(){
        RequestParams params=new RequestParams(BnUrl.validateOldTelUrl);
        params.setUseCookie(false);
        params.addHeader("Cookie",cookie);
        params.addBodyParameter("NewPhone",mETNewTel.getText().toString());
        params.addBodyParameter("code",mETSms.getText().toString());
        params.addBodyParameter("uid",app.getUserId());

        x.http().post(params, new Callback.ProgressCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response!=null){
                    try {
                        boolean result=response.getBoolean("result");
                        if(result){
                            sendSms();
                        }else {
                            Snackbar.make(mLLParent,"新手机号验证失败",Snackbar.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                Snackbar.make(mLLParent,"新手机号验证失败",Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });

    }
}
