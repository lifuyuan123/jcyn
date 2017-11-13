package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.SessionUtil;
import com.zcw.togglebutton.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/2/27.
 */

public class SetActivity extends BaseAppcompatActivity {
    //绑定布局
    @Bind(R.id.basegreen_second_title_tv)TextView smallTileTv;
    @Bind(R.id.basegreen_second_title_iv)ImageView smallTitleIv;
    @Bind(R.id.basegreen_title_tv)TextView titleTv;
//    @Bind(R.id.activity_set_change_message_tb)ToggleButton toggleButton;

    private MsApp msApp;

    private boolean isEmail=false;

    private Intent intent = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                     if(isEmail){
                         intent = new Intent(SetActivity.this,ChangeEmailActivity.class);
                         startActivity(intent);
                     }else {
                         intent = new Intent(SetActivity.this,BindEmailActivity.class);
                         startActivity(intent);
                     }
                    break;
            }
        }
    };


    @Override
    public void setView() {
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        msApp = (MsApp) getApplication();
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        smallTileTv.setVisibility(View.GONE);
        smallTitleIv.setVisibility(View.GONE);
        titleTv.setText("设置");
//        if(msApp.getNotication()){
//            toggleButton.setToggleOn();
//        }else {
//            toggleButton.setToggleOff();
//        }
//        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
//            @Override
//            public void onToggle(boolean on) {
//                if(!on){
//                    msApp.saveNotication(false);
//                }else {
//                    msApp.saveNotication(true);
//                }
//            }
//        });
    }

    @Override
    public void reloadData() {

    }

    @OnClick({R.id.activity_set_change_password_rl,
              R.id.activity_set_change_phonenum_rl,
              R.id.activity_set_change_email_rl,
              R.id.basegreen_back_rl})
    public void setOnClick(View view){
        Intent intent = null;
        switch (view.getId()){
            case R.id.activity_set_change_password_rl:
                intent = new Intent(this,ChangPassWordActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_set_change_phonenum_rl:
                intent = new Intent(this,ChangeUserPhoneOldActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_set_change_email_rl:
//                startActivity(new Intent(this,BindEmailActivity.class));
                checkIsBind();
                break;
            case R.id.basegreen_back_rl:
                finish();
                break;
        }

    }

    public void checkIsBind(){
        RequestParams params = new RequestParams(BnUrl.isBindEmailUrl);
        params.setHeader("cookie", SessionUtil.getCookie());
        params.addBodyParameter("isbind","3");
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("setonSuccess",result.toString());
                try {
                    if(result.getBoolean("result")){
                        isEmail = true;
                    }
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("setonError",ex.toString());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("setononCancelled",cex.toString());
            }

            @Override
            public void onFinished() {
                Log.e("setonFinished","onFinished");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        isEmail = false;
    }
}
