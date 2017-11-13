package com.sctjsj.jcyn.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.util.LoadingDialog;
import com.sctjsj.jcyn.util.LogUtil;
import com.sctjsj.jcyn.util.NetUtil;
import com.sctjsj.jcyn.util.NetworkChangeReceiver;
import com.sctjsj.jcyn.util.NoNetworkRemindView;


/**
 * Created by mayikang on 17/2/6.
 */

/**
 * 封装的  AppcompatActivity 基类
 */

public abstract class BaseAppcompatActivity extends AppCompatActivity {
    /**页面根布局**/
    private View rootView;
    private ViewGroup root;
    /**无网络提示控件**/
    private NoNetworkRemindView noNetworkRemindView;

    //网络状态发生变化时，是否需要提示重新加载页面
    private boolean isNeedNetRmind=true;
    /** 日志输出标志 **/
    protected final String TAG = this.getClass().getSimpleName();

    private LoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
        LogUtil.e(TAG+":onCreate");
        //锁定屏幕
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //注册网络变化监听
        NetworkChangeReceiver.setOnNetworkChangedListener(new NetworkChangeReceiver.OnNetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean isConnected, int type) {
                //无网络
                if(!isConnected){
                     showNoNetworkRemind();
                } else {
                    dismissNoNetworkRemind();
                }

            }
        });



    }

    /**
     * 设置布局
     */

    public abstract void setView();
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e(TAG+":onResume");

        if(NetUtil.isConnected(this)){
            dismissNoNetworkRemind();
        }else {
            showNoNetworkRemind();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e(TAG+":onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e(TAG+":onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG+":onDestroy");

    }

    /**
     * 设置是否需要网络变化提示
     * @param b
     */
    public void setNeedNetRmind(boolean b){
        this.isNeedNetRmind=b;
    }

    /**
     * 显示无网络提示
     */
    public void showNoNetworkRemind(){

        if(isNeedNetRmind){
            //获取页面根布局
            rootView=  getWindow().getDecorView().findViewById(android.R.id.content);
            root= (ViewGroup) rootView;

            if(noNetworkRemindView==null){
                noNetworkRemindView=new NoNetworkRemindView(this);
                //设置点击重新加载数据的监听
                noNetworkRemindView.setOnReloadClickListerer(new NoNetworkRemindView.ReloadClickListener() {
                    @Override
                    public void reload() {
                        reloadData();
                    }

                    @Override
                    public void setNetwork() {
                        goToSetNetwork();
                    }
                });

                root.addView(noNetworkRemindView);
            }

            //重新获取一次根布局
//            rootView=  getWindow().getDecorView().findViewById(android.R.id.content);
//            root= (ViewGroup) rootView;

            //循环遍历根布局下的所有子布局，隐藏其他布局，显示无网络提示
            for (int i=0;i<root.getChildCount();i++){
                if(root.getChildAt(i) instanceof NoNetworkRemindView){
                    root.getChildAt(i).setVisibility(View.VISIBLE);
                }else {
                    root.getChildAt(i).setVisibility(View.GONE);
                }
            }

        }

    }

    /**
     * 关闭无网络提示
     */
    public void dismissNoNetworkRemind(){

        if(isNeedNetRmind){
            //获取页面根布局
            rootView=  getWindow().getDecorView().findViewById(android.R.id.content);
            root= (ViewGroup) rootView;

            //循环遍历根布局下的所有子布局，显示其他布局，隐藏无网络提示
            for (int i=0;i<root.getChildCount();i++){
                if(root.getChildAt(i) instanceof NoNetworkRemindView){
                    root.getChildAt(i).setVisibility(View. GONE);
                }else {
                    root.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
        }


    }

    /**
     * 重新加载数据的方法必须重写
     */
    public  abstract void reloadData();

    /**
     * 去设置网络
     */
    public void goToSetNetwork(){
         NetUtil.openSetting(this);
    }

    /**
     * 显示加载dialog
     * @param str
     */
    public void showLoading(boolean b,String str){
        if(dialog==null){
            dialog=new LoadingDialog(this);
        }
        dialog.setCancelable(b);
        dialog.setLoadingStr(str);
        dialog.show();
    }

    /**
     * 关闭加载 dialog
     */
    public void dismissLoading(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }

}
