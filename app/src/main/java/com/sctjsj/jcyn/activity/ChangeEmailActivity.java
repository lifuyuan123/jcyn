package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.RegexpValidateUtil;
import com.sctjsj.jcyn.util.SessionUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/3/27.
 */

public class ChangeEmailActivity extends BaseAppcompatActivity {
    @Bind(R.id.base_title_tv)TextView titleTv;
    @Bind(R.id.base_second_title_rl)RelativeLayout relativeLayout;
    @Bind(R.id.activity_change_email_et)EditText emailEt;
    @Bind(R.id.activity_change_email_bt)Button bt;
    @Bind(R.id.activity_change_email_old_bt)Button button;
    @Bind(R.id.old_et)EditText et;
    private LinUtil linUtil;

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_email_old);
        ButterKnife.bind(this);
        relativeLayout.setVisibility(View.GONE);
        titleTv.setText("修改邮箱");
        button.setVisibility(View.GONE);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));

    }

    @Override
    public void reloadData() {

    }
    @OnClick({R.id.base_back_rl,
              R.id.activity_change_email_bt,
              R.id.activity_change_email_old_bt})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.base_back_rl:
                finish();
                break;
            case R.id.activity_change_email_bt:
                if(emailEt.getText().length()>0){
                    if(RegexpValidateUtil.checkEmail(emailEt.getText().toString())){
                        sendEmail();
                    }else {
                        Toast.makeText(ChangeEmailActivity.this,"请输入正确邮箱",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(ChangeEmailActivity.this,"请输入邮箱",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.activity_change_email_old_bt:
                if(emailEt.getText().length()>0 && et.getText().length()>0){
                    checkEmail();
                }else {
                    Toast.makeText(ChangeEmailActivity.this,"请输入完整信息",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void sendEmail(){
        RequestParams params = new RequestParams(BnUrl.isBindEmailUrl);
        params.setHeader("cookie", SessionUtil.getCookie());
        params.addBodyParameter("email",emailEt.getText().toString());
        params.addBodyParameter("isbind","2");
        linUtil.newInstance().showLoading(false,"正在发送请稍后",ChangeEmailActivity.this);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("mssonSuccess",result.toString());
                try {
                    if(result.getBoolean("result")){
                        button.setVisibility(View.VISIBLE);
                        bt.setVisibility(View.GONE);
                        Toast.makeText(ChangeEmailActivity.this,"邮件发送成功",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(ChangeEmailActivity.this,"邮件发送失败",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
              linUtil.newInstance().dismissLoading();
                Log.e("mssononError","="+ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
              linUtil.newInstance().dismissLoading();
            }
        });

    }
    public void checkEmail(){
        RequestParams parmas = new RequestParams(BnUrl.isBindEmailUrl);
        parmas.setHeader("cookie",SessionUtil.getCookie());
        parmas.addBodyParameter("email",emailEt.getText().toString());
        parmas.addBodyParameter("code",et.getText().toString());
        parmas.addBodyParameter("isbind","2");
        x.http().post(parmas, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("cea",result.toString());

                try {
                    if(result.getBoolean("result")){
                        Intent intent = new Intent(ChangeEmailActivity.this,ChangeEmailBindEmailActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(ChangeEmailActivity.this,result.getString("msg"),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                 Log.e("cea",ex.toString());
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
