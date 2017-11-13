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
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.BnUrl;
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
 * Created by Aaron on 2017/3/31.
 */

public class ChangeEmailBindEmailActivity extends BaseAppcompatActivity {

    @Bind(R.id.base_title_tv)TextView titleTv;
    @Bind(R.id.base_second_title_rl)RelativeLayout relativeLayout;
    @Bind(R.id.bind_new_email)EditText editText;
    @Bind(R.id.bind_new_et)EditText bindEt;
    @Bind(R.id.send_noction_bt)Button bt;
    //  确认按钮
    @Bind(R.id.bind_new_bt)Button button;

    private MsApp msApp;
    @Override
    public void setView() {
        setContentView(R.layout.activity_change_email_new);
        ButterKnife.bind(this);
        relativeLayout.setVisibility(View.GONE);
        titleTv.setText("绑定邮箱");
        msApp = (MsApp) getApplication();
        button.setVisibility(View.GONE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));

    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.base_back_rl,
            R.id.bind_new_bt,
            R.id.send_noction_bt,
    })
    public void bindOnclick(View view){
        switch (view.getId()){
            case R.id.base_back_rl:
                finish();
                break;
            case R.id.bind_new_bt:
                if(editText.getText().length()>0 && bindEt.getText().length()>0){
                    bindEmail();
                }else {
                    Toast.makeText(ChangeEmailBindEmailActivity.this,"请输入完整信息",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.send_noction_bt:
                if(editText.getText().length()>0){
                    if(RegexpValidateUtil.checkEmail(editText.getText().toString())){
                        sendEmail();
                    }else {
                        Toast.makeText(ChangeEmailBindEmailActivity.this,"请输入正确邮箱",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(ChangeEmailBindEmailActivity.this,"请输入邮箱",Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    public void sendEmail(){
        RequestParams params = new RequestParams(BnUrl.isBindEmailUrl);
        params.setHeader("cookie", SessionUtil.getCookie());
        params.addBodyParameter("email",editText.getText().toString());
        params.addBodyParameter("isbind","0");
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("bea",result.toString());
                try {
                    if(result.getBoolean("result")){
                        bt.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                    }
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
    public void bindEmail(){
        RequestParams params = new RequestParams(BnUrl.bindEamilUrl);
        params.setHeader("cookie",SessionUtil.getCookie());
        params.addBodyParameter("userId",msApp.getUserId());
        params.addBodyParameter("email",editText.getText().toString());
        params.addBodyParameter("code",bindEt.getText().toString());
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("bea",result.toString());
                try {
                    if(result.getBoolean("result")){
                        Toast.makeText(ChangeEmailBindEmailActivity.this,"绑定成功",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ChangeEmailBindEmailActivity.this,SetActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Toast.makeText(ChangeEmailBindEmailActivity.this,"绑定失败",Toast.LENGTH_LONG).show();
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
