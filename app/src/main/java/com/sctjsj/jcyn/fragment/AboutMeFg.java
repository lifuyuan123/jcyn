package com.sctjsj.jcyn.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.activity.CollectionActivity;
import com.sctjsj.jcyn.activity.LodingLoginActivity;
import com.sctjsj.jcyn.activity.RegisterActivity;
import com.sctjsj.jcyn.activity.SetActivity;
import com.sctjsj.jcyn.activity.UserDataActivity;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.UrltitleBean;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinUtil;
import com.sctjsj.jcyn.util.LogUtil;
import com.sctjsj.jcyn.util.NetUtil;
import com.sctjsj.jcyn.util.NetworkChangeReceiver;
import com.sctjsj.jcyn.util.SessionUtil;
import com.sctjsj.jcyn.util.alert.SweetAlertDialog;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aaron on 2017/2/6.
 */

public class AboutMeFg extends Fragment {
    @Bind(R.id.fg_home_out)
    RelativeLayout fgHomeOut;
    @Bind(R.id.view)
    View view;
    private MsApp msApp;
    private boolean login = false;
    @Bind(R.id.fg_mine_ll)
    LinearLayout userNameLl;
    @Bind(R.id.fg_mine_user_name_tv)
    TextView userNameTv;
    @Bind(R.id.fg_mine_civ)
    CircleImageView userImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_mine, null);
        ButterKnife.bind(this, view);
        msApp = (MsApp) getActivity().getApplication();
        Log.e("msg", msApp.getUserId());
        userNameTv.setVisibility(View.GONE);
        isLogin();
        Log.e("am", login + "");
        return view;
    }

    @OnClick({
            R.id.fg_mine_user_msg,
            R.id.fg_mine_set,
            R.id.fg_mine_mycolleage,
            R.id.fg_mine_download,
            R.id.fg_home_version_change,
            R.id.fg_mine_lgoin,
            R.id.fg_mine_register,
            R.id.fg_home_out
    })


    public void aboutMeOnclick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            //个人资料
            case R.id.fg_mine_user_msg:
                if (login) {
                    intent = new Intent(getActivity(), UserDataActivity.class);
                    startActivityForResult(intent, 100);
//                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "您尚未登录，请登录后查询", Toast.LENGTH_LONG).show();
                }
                break;
            //设置
            case R.id.fg_mine_set:
                if(login){
                    intent = new Intent(getActivity(), SetActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "您尚未登录，请登录后查询", Toast.LENGTH_LONG).show();
                }

                break;
            //收藏
            case R.id.fg_mine_mycolleage:
                if (login) {
                    intent = new Intent(getActivity(), CollectionActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "您尚未登录，请登录后查询", Toast.LENGTH_LONG).show();
                }
                break;
            //缓存
            case R.id.fg_mine_download:
                break;
            //版本更新
            case R.id.fg_home_version_change:
                checkVersionManual(BnUrl.verisonUpdata, 1);
                break;
            //登出
            case R.id.fg_home_out:
                if (login) {
                    loginout(BnUrl.loginoutUrl);
                } else {
                    Toast.makeText(getContext(), "没有登录", Toast.LENGTH_SHORT).show();
                }
                break;
            //登录
            case R.id.fg_mine_lgoin:
                intent = new Intent(getActivity(), LodingLoginActivity.class);
                startActivity(intent);
                break;
            //注册
            case R.id.fg_mine_register:
                intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void loginout(final String url) {
        if(!NetUtil.isConnected(getActivity())){
            Toast.makeText(msApp, "没有网络，请连接网络。", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("退出登录")
                    .setMessage("确定退出登录吗？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.i("url----", url);
                                        OkHttpClient okHttpClient = new OkHttpClient();
                                        final Request request1 = new Request.Builder()
//                                .addHeader("Cookie", SessionUtil.getCookie())//添加后点击第一次没反应，第二次执行
                                                .url(url)
                                                .build();
                                        Log.i("loginoutoncookie", "=" + SessionUtil.getCookie().toString());
                                        okHttpClient.newCall(request1).enqueue(new com.squareup.okhttp.Callback() {
                                            //请求失败
                                            @Override
                                            public void onFailure(Request request, IOException e) {
                                                Log.i("loginoutonFailuree", "=" + e.toString());
                                                Toast.makeText(getActivity(), "退出失败", Toast.LENGTH_SHORT).show();
                                            }

                                            //请求成功
                                @Override
                                public void onResponse(Response response) throws IOException {
                                    try {
                                        JSONObject object=new JSONObject(response.body().string());
                                        String resultMsg=object.getString("resultMsg");
                                        Log.i("loginoutresponse",resultMsg);
                                        if (response.message().equalsIgnoreCase("ok")&&resultMsg.equals("登出成功")) {
                                            msApp.saveAccount(msApp.getAccount(), "none");
                                            DbCookieStore.INSTANCE.removeAll();
                                            Log.i("out11111", "=" + DbCookieStore.INSTANCE.getCookies());
                                            startActivity(new Intent(getActivity(), LodingLoginActivity.class));
                                            getActivity().finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
//                        RequestParams params = new RequestParams(url);
////                            params.addBodyParameter("userName",app.getAccount());
////                            params.addBodyParameter("password",app.getPassword());
//                            params.addBodyParameter("userName",msApp.getAccount());
//                            params.addQueryStringParameter("userName",msApp.getAccount());
//                        params.setHeader("Cookie", SessionUtil.getCookie());
//                        Log.i("loginout-cookie", SessionUtil.getCookie().toString());
//                        Log.i("loginoutparems", "=" + params.toString());
//                        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
//                            @Override
//                            public void onSuccess(JSONObject result) {
//                                Log.e("loginout11", "=" + result.toString());
//                                msApp.saveAccount(msApp.getAccount(), "none");
//                                DbCookieStore.INSTANCE.removeAll();
//                                Log.i("out11111", "=" + DbCookieStore.INSTANCE.getCookies());
//                                startActivity(new Intent(getActivity(), LodingLoginActivity.class));
//                                getActivity().finish();
//                            }
//
//                            @Override
//                            public void onError(Throwable ex, boolean isOnCallback) {
//                                Log.e("loginoutonError11", "=" + ex.toString());
//                            }
//
//                            @Override
//                            public void onCancelled(CancelledException cex) {
//
//                            }
//
//                            @Override
//                            public void onFinished() {
//
//                            }
//                        });
                        }
                    }).show();
        }
    }

    /**
     * 判断用户是否登录
     */
    public void isLogin() {
        LinUtil.newInstance().getMsg(BnUrl.userLogin, null, getActivity(), false, new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {
                try {
                    boolean result = objects.getBoolean("result");
                    if (result) {
                        login = true;
                        getUserMsg(msApp.getUserId());
                        userNameLl.setVisibility(View.GONE);
                    } else {
                        fgHomeOut.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取用户信息
    public void getUserMsg(String id) {
        List<UrltitleBean> list = new ArrayList<>();
        list.add(new UrltitleBean("ctype", "user"));
        list.add(new UrltitleBean("id", id));
        list.add(new UrltitleBean("jf", "headPortraitUrl"));
        list.add(new UrltitleBean("size", "999"));
        LinUtil.newInstance().getMsg(BnUrl.UserDataUrl, list, getActivity(), true, new LinUtil.OnNetRequestListner() {
            @Override
            public void onSuccess(JSONObject objects) {
                Log.e("am", objects.toString());
                if (objects != null) {
                    try {
                        JSONObject data = objects.getJSONObject("data");
                        JSONObject headPortraitUrl = data.getJSONObject("headPortraitUrl");
                        String url = headPortraitUrl.getString("url");
                        String userName = data.getString("username");
                        Picasso.with(getActivity()).load(url).into(userImg);
                        userNameLl.setVisibility(View.GONE);
                        userNameTv.setVisibility(View.VISIBLE);
                        userNameTv.setText(userName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        login = false;
        ButterKnife.unbind(this);
    }


    //    下载文件保存路径
    private String downloadFilePath = Environment.getExternalStorageDirectory().getPath() + "/jcyn/download";

    private static AlertDialog downDialog;

    //手动检查版本
    public void checkVersionManual(String checkUrl, int type) {
        RequestParams params = new RequestParams(checkUrl);
        params.addBodyParameter("appTerminal", String.valueOf(type));
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.e("mms", response.toString());
                if (response != null) {
                    try {
                        boolean result = response.getBoolean("result");
                        if (result) {
                            String str = response.getString("resultData");
                            if (str != null) {
                                JSONObject info = new JSONObject(str);
                                if (info != null) {
//                                    int appTerminal=info.getInt("appTerminal");//app 终端

                                    int latestVersion = info.getInt("realVersion");//最新 app 版本
                                    String versionId = info.getString("versionId");//显示出来的版本号

                                    int isUpdate = info.getInt("releaseFlag");//是否准备好更新了 1：审核通过了，2：审核没通过
                                    int isForce = info.getInt("majorUpdate");//是否强制更新 1：强制更新，2：一般更新
                                    String downUrl = info.getString("downUrl");//更新地址
                                    String content = info.getString("contents");//更新内容
                                    //必须通过审核的新 apk，还要版本大于当前版本
                                    if (1 == isUpdate && latestVersion > getLocalVersion()) {

                                        if (1 == isForce) {
                                            alertImportant(versionId, content, downUrl);
                                        }

                                        if (2 == isForce) {
                                            alert(versionId, content, downUrl);
                                        }

                                    } else {
                                        //主动检查更新时
                                        AlertDialog.Builder bb = new AlertDialog.Builder(getActivity());
                                        bb.setTitle("检查更新");
                                        bb.setIcon(R.mipmap.icon_app_logo);
                                        bb.setMessage("当前已是最新版本，无需更新");
                                        bb.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        bb.show();

                                    }
                                }
                            }
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
     * 获取当前安装的版本号
     *
     * @return
     */
    private int getLocalVersion() {
        int version = -1;
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 提示安装apk
     *
     * @param context
     * @param path
     */
    private void installApp(Context context, String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 下载apk
     *
     * @param url
     */
    private void downloadApk(String url) {
        checkSavePath();
        RequestParams params = new RequestParams(url);
        params.addHeader("platform", "android_app");
        final String filePath = downloadFilePath + "/x.apk";
        params.setSaveFilePath(filePath);//设置文件保存路径
        params.setAutoResume(true);//设置自动断点续传
        params.setConnectTimeout(5 * 1000);
        params.setAutoRename(false);
        final Callback.Cancelable cancelAble = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {

                downDialog.dismiss();

                if (result == null) {
                    LinUtil.newInstance().dismissLoading();
                    final SweetAlertDialog sad = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                    sad.setTitleText("下载失败");
                    sad.setContentText("程序下载过程中遭遇未知异常！");
                    sad.setCancelable(false);
                    sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sad.dismiss();
                        }
                    });
                    sad.show();
                }


                SweetAlertDialog sad = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                sad.setTitleText("下载完成");
                sad.setContentText("立即安装最新版本？");
                sad.setCancelable(false);
                sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        installApp(getActivity(), result.getAbsolutePath());
                    }
                });
                sad.show();


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LinUtil.newInstance().dismissLoading();
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
                LinUtil.newInstance().showLoading(false, "正在下载最新版", getActivity());
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

                double a = Double.valueOf(total);
                double b = Double.valueOf(current);
                double c = b / a;
                DecimalFormat df = new DecimalFormat("#");
                final int p = Integer.valueOf(df.format(c * 100));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });


            }

        });

    }

    /**
     * 检查是否存在保存的文件夹
     */
    private void checkSavePath() {
        //创建下载文件夹
        File file = new File(downloadFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void alert(String version, String con, final String url) {
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("更新提示");
        dialog.setCancelable(true);
        dialog.setContentText("最新版本：" + version + "\n" + "更新内容：" + con);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dialog.dismiss();
                downloadApk(url);
            }
        });
        dialog.show();
    }

    /**
     * 重大更新
     *
     * @param con
     * @param url
     */
    private void alertImportant(String version, String con, final String url) {
        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity());
        dialog.setTitleText("重大更新提示");
        dialog.setCancelable(false);
        dialog.setContentText("最新版本：" + version + "\n" + "更新内容：" + con);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dialog.dismiss();
                downloadApk(url);
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 200 && data.getBooleanExtra("updata", false)) {
            getUserMsg(msApp.getUserId());
        }
    }
}
