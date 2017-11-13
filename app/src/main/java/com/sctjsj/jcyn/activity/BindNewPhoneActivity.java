package com.sctjsj.jcyn.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.BnUrl;

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

public class BindNewPhoneActivity extends BaseAppcompatActivity{
    @Bind(R.id.base_title_tv)TextView title;
    @Bind(R.id.base_second_title_tv)TextView secondTitle;

    @Bind(R.id.bindnewphone_ll)LinearLayout mLLParent;
    @Bind(R.id.new_num)TextView mTVRemind;
    @Bind(R.id.bind_phone_et)EditText mETsms;
    private String cookie;
    private MsApp app;
    private String tel;


    @Override
    public void setView() {
        setContentView(R.layout.activity_bind_telephone);
        ButterKnife.bind(this);
        title.setText("修改手机号");
        secondTitle.setVisibility(View.GONE);
        app = (MsApp) getApplication();
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
        cookie=getIntent().getStringExtra("cookie");
        //避免获取不到 cookie
        if(TextUtils.isEmpty(cookie)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("异常提示");
            builder.setIcon(R.mipmap.icon_app_start);
            builder.setMessage("程序发生了未知错误，即将退出！");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BindNewPhoneActivity.this, IndexActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.show();
        }

        tel = getIntent().getStringExtra("tel");
        if (!TextUtils.isEmpty(tel)) {
            mTVRemind.setText("将手机号变更为：" + tel);
        }

    }

    @Override
    public void reloadData() {

    }
    @OnClick({R.id.base_back_rl,R.id.bind_phone_bt})
    public void bindOnclick(View view){
        switch (view.getId()){
            case R.id.base_back_rl:
                finish();
                break;
            case R.id.bind_phone_bt:

                if(checkB4Confirm()){
                    if(app.getUserId()!=null){
                        confirm();
                    }

                }
                break;
        }

    }

    /**
     * 确认绑定新手机
     */
    private void confirm(){
        RequestParams params=new RequestParams(BnUrl.bindNewPhone);
        params.setUseCookie(false);
        params.addHeader("Cookie",cookie);
        params.addBodyParameter("userId", app.getUserId());
        params.addBodyParameter("phone",tel);
        params.addBodyParameter("code",mETsms.getText().toString());

        x.http().post(params, new Callback.ProgressCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response!=null){
                    try {
                        boolean result=response.getBoolean("result");
                        if(result){
                            Snackbar.make(mLLParent,"手机号更换成功",Snackbar.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            },1500);
                        }else {
                            Snackbar.make(mLLParent,"手机号更换失败",Snackbar.LENGTH_SHORT).show();
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

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted(){
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }
        });


    }

    private boolean checkB4Confirm(){

        if(TextUtils.isEmpty(mETsms.getText().toString())){

            return false;
        }
        return true;
    }

}
