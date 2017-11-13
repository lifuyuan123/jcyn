package com.sctjsj.jcyn.util;

import com.sctjsj.jcyn.bean.MsgBean;
import com.sctjsj.jcyn.bean.UrltitleBean;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Aaron on 2017/2/23.
 */

public class LinUtil {
    private LoadingDialog dialog;
    private static LinUtil instance=null;

    private LinUtil(){

    }
    public synchronized static LinUtil newInstance(){
        if(instance == null){
            instance=new LinUtil();
        }
        return instance;
    }

    //网络请求
    public void getMsg(String url, final List<UrltitleBean> list, final Context context , boolean isCoolkie, final OnNetRequestListner listener){

//        showLoading(true,"数据加载中...",context);
        RequestParams params=new RequestParams(url);

        if(isCoolkie){
            params.setHeader("cookies",SessionUtil.getCookie());
            Log.i("loginin-cookies-logn","="+SessionUtil.getCookie());
        }

        if(list!=null){
            if(list.size()>0){
                for(int i=0;i<list.size();i++){
                    params.addBodyParameter(list.get(i).getKey(),list.get(i).getValue());
                }
            }
        }
        Log.i("lgoin-params","="+params.toString());
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                listener.onSuccess(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                Toast.makeText(context,ex.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
//                dismissLoading();
            }
        });
    }


    public interface OnNetRequestListner{
        public void onSuccess(JSONObject objects);
    }
    /**
     * 显示加载dialog
     * @param str
     */
    public void showLoading(boolean b,String str,Context context){
        if(dialog==null){
            dialog=new LoadingDialog(context);
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
