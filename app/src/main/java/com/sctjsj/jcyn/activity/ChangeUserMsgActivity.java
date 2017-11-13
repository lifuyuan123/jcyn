package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.UrltitleBean;
import com.sctjsj.jcyn.bean.UserMsgBean;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.SessionUtil;

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
 * Created by Aaron on 2017/3/1.
 */

public class ChangeUserMsgActivity extends BaseAppcompatActivity {
    //    @Bind(R.id.base_title_tv)TextView titleTv;
//    @Bind(R.id.base_second_title_tv)TextView secondTitleTv;
    @Bind(R.id.activity_change_usermsg_name_et)
    EditText userNameEt;
    @Bind(R.id.activity_change_usermsg_adress_et)
    EditText userAdressEt;
    @Bind(R.id.activity_change_usermsg_sex_et)
    EditText userSexEt;
    @Bind(R.id.basegreen_back_rl)
    RelativeLayout basegreenBackRl;
    @Bind(R.id.basegreen_title_tv)
    TextView basegreenTitleTv;
    @Bind(R.id.basegreen_second_title_tv)
    TextView basegreenSecondTitleTv;
    @Bind(R.id.basegreen_second_rl)
    RelativeLayout basegreenSecondRl;
    //    @Bind(R.id.base_second_title_rl)RelativeLayout confirmBt;
//    @Bind(R.id.base_back_rl)RelativeLayout backRl;
    private String userName;
    private String userAdress;
    private String userSex;
    private boolean isLogin = false;
    private MsApp app;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (isLogin) {
                        getUserMsg();
                    } else {
                        Toast.makeText(ChangeUserMsgActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_usermsg);
        ButterKnife.bind(this);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.app_white));
        app = (MsApp) getApplication();
        basegreenTitleTv.setText("修改用户信息");
        basegreenSecondTitleTv.setText("确认");
        isLoginMsg();
        basegreenSecondRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changUserMsg();
            }
        });
        basegreenBackRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(ChangeUserMsgActivity.this, UserDataActivity.class);
                startActivity(inten);
                finish();
            }
        });
    }

    @Override
    public void reloadData() {

    }

    public void getUserMsg() {
        List<UrltitleBean> list = new ArrayList<>();
        list.add(new UrltitleBean("ctype", "user"));
        list.add(new UrltitleBean("id", app.getUserId()));
        list.add(new UrltitleBean("jf", "headPortraitUrl"));
        list.add(new UrltitleBean("size", "999"));
        LinUtil.newInstance().getMsg(BnUrl.UserDataUrl, list, this, true, new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {
                if (objects != null) {
                    try {
                        JSONObject data = objects.getJSONObject("data");
                        userNameEt.setText(data.getString("realName"));
                        userAdressEt.setText(data.getString("address"));
                        switch (data.getInt("gender")) {
                            case 1:
                                userSexEt.setText("男");
                                break;
                            case 2:
                                userSexEt.setText("女");
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void changUserMsg() {
        // ctype=user&data={id:2,gender:1,realName:"修改昵称",address:"修改地址"}
        userName = userNameEt.getText().toString();
        userSex = userSexEt.getText().toString();
        userAdress = userAdressEt.getText().toString();
        RequestParams params = new RequestParams(BnUrl.changeUserMsgUrl);
        params.setHeader("cookie", SessionUtil.getCookie());
        params.addBodyParameter("ctype", "user");
        UserMsgBean umb = new UserMsgBean();
        umb.setId(app.getUserId());
        umb.setAddress(userAdress);
        switch (userSex) {
            case "男":
                umb.setGender("1");
                break;
            case "女":
                umb.setGender("2");
                break;
            default:
                umb.setGender("");
                break;
        }
        umb.setRealName(userName);
        params.addBodyParameter("data", new Gson().toJson(umb));
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("mmm11", result.toString());
                if (result != null) {
                    try {
                        boolean msg = result.getBoolean("result");
                        if (msg) {
                            Toast.makeText(ChangeUserMsgActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                            Intent inten = new Intent(ChangeUserMsgActivity.this, UserDataActivity.class);
                            startActivity(inten);
                            finish();
                        }
                        Toast.makeText(ChangeUserMsgActivity.this, "修改失败", Toast.LENGTH_LONG).show();
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

    public void isLoginMsg() {
        RequestParams params = new RequestParams(BnUrl.userLogin);
        params.setHeader("Cookie", SessionUtil.getCookie());
        Log.e("cuma", SessionUtil.getCookie());
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {


                try {
                    isLogin = result.getBoolean("result");
                    Message message = new Message();
                    message.what = 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
