package com.sctjsj.jcyn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;
import com.sctjsj.jcyn.application.MsApp;
import com.sctjsj.jcyn.bean.CollectionBean;
import com.sctjsj.jcyn.util.BnUrl;
import com.sctjsj.jcyn.util.LinCheckBox;
import com.sctjsj.jcyn.util.SessionUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Aaron on 2017/2/13.
 */

public class CollectionActivity extends AppCompatActivity{
//    private ArrayList<Bean> mList = new ArrayList<>();
    private MsApp msApp;
    private boolean isLogin=false;
    private List<CollectionBean> list = new ArrayList<>();
    @Bind(R.id.activity_collection_gv)GridView gv;
    @Bind(R.id.basegreen_second_title_tv)TextView indroationTv;
    @Bind(R.id.basegreen_title_tv)TextView titleTv;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.e("ca",list.toString());
                    gv.setAdapter(new CollectionAdapter(0));
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        msApp = (MsApp) getApplication();
        indroationTv.setText("编辑");
        titleTv.setText("我的收藏");
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_color));
        getCollectionMsg();


//
//        gv.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return mList.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return mList.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return 0;
//            }
//
//            @Override
//            public View getView(final int position, View convertView, ViewGroup parent) {
//                ViewHolder holder;
//                if (convertView == null) {
//                    holder = new ViewHolder();
//                    convertView = View.inflate(CollectionActivity.this, R.layout.activity_collection_item, null);
//                    holder.cb = (LinCheckBox) convertView.findViewById(R.id.activity_collection_item_lcb);
//                    convertView.setTag(holder);
//                } else {
//                    holder = (ViewHolder) convertView.getTag();
//                }
//
//                final Bean bean = mList.get(position);
//                holder.cb.setOnCheckedChangeListener(new LinCheckBox.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(LinCheckBox checkBox, boolean isChecked) {
//                        bean.isChecked = isChecked;
//                    }
//                });
//                holder.cb.setChecked(bean.isChecked);
//
//                return convertView;
//            }
//
//            class ViewHolder {
//                LinCheckBox cb;
//            }
//        });
//        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Bean bean = (Bean) parent.getAdapter().getItem(position);
//                bean.isChecked = !bean.isChecked;
//                LinCheckBox checkBox = (LinCheckBox) view.findViewById(R.id.activity_collection_item_lcb);
//                checkBox.setChecked(bean.isChecked, true);
//            }
//        });
    }
//
//    class Bean implements Serializable {
//        boolean isChecked;
//    }
    @OnClick({R.id.basegreen_back_rl,R.id.basegreen_second_rl})
    public void CollectionOnlick(View view){
        switch (view.getId()){
            case R.id.basegreen_back_rl:
                finish();
            break;
            case R.id.basegreen_second_rl:
                if(indroationTv.getText().toString().equals("编辑")){
                    gv.setAdapter(new CollectionAdapter(1));
                    indroationTv.setText("删除");
                }else if(indroationTv.getText().toString().equals("删除")){

                     deleteCollectionMsg();
                     indroationTv.setText("编辑");
                }
                break;
        }
    }
   public void getCollectionMsg(){
       RequestParams params = new RequestParams(BnUrl.userCollection);
       // ctype=collect&cond={usere:{id:1}}&jf=article|thumbnail&size=999
       params.setHeader("cookie",SessionUtil.getCookie());
       params.addBodyParameter("ctype","collect");
       String usereId = "{user:{id:"+msApp.getUserId()+"},isDelete:1}";
       params.addBodyParameter("cond",usereId);
       params.addBodyParameter("jf","article|thumbnail");
       params.addBodyParameter("size","999");
       x.http().post(params, new Callback.CommonCallback<JSONObject>() {
           @Override
           public void onSuccess(JSONObject result) {
                 Log.e("ca",result.toString());
               if(result!=null){
                   try {
                       JSONArray resultList = result.getJSONArray("resultList");
                       for(int i =0 ; i<resultList.length() ;i++){
                           JSONObject object = resultList.getJSONObject(i);
                           JSONObject article = object.getJSONObject("article");
                           JSONObject thumbnail = article.getJSONObject("thumbnail");
                           CollectionBean cb = new CollectionBean();
                           cb.setCheck(false);
                           cb.setImg(thumbnail.getString("url"));
                           cb.setTitle(article.getString("title"));
                           cb.setIndroation(article.getString("keyword"));
                           cb.setId(article.getString("id"));
                           cb.setCollectNum(article.getString("collectNumber"));
                           list.add(cb);
                       }
                       Message message = new Message();
                       message.what = 1;
                       handler.sendMessage(message);
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

    public void deleteCollectionMsg(){
        String param="";
        //循环判断收藏中那些id状态为true
        for(int i=0; i<list.size();i++){
            if(list.get(i).isCheck()) {
                param+= list.get(i).getId()+",";
            }
            Log.e("cabc",list.get(i).isCheck()+"");
        }
        Log.e("cabc",param);
        //userid=1&params=1
        RequestParams params = new RequestParams(BnUrl.deleteCollectionUrl);
        params.setHeader("cookie",SessionUtil.getCookie());
        params.addBodyParameter("userid",msApp.getUserId());
        params.addBodyParameter("params",param);
        x.http().post(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
              Log.e("cabc",result.toString());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                list.clear();
                getCollectionMsg();
            }
        });
    }

    private class CollectionAdapter extends BaseAdapter{
        int chekBox = 0;

        public CollectionAdapter(int chekBox) {
            this.chekBox = chekBox;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(view == null){
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(CollectionActivity.this).inflate(R.layout.activity_collection_item,null);
                viewHolder.checkBox = (LinCheckBox) view.findViewById(R.id.activity_collection_item_lcb);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.activity_collection_item_iv);
                viewHolder.title = (TextView) view.findViewById(R.id.activity_collection_item_titletv);
                viewHolder.indroation = (TextView) view.findViewById(R.id.activity_collecion_indroation_tv);
                viewHolder.collectNum = (TextView) view.findViewById(R.id.fg_home_recommend_1_tv);
                viewHolder.boxLl = (LinearLayout) view.findViewById(R.id.activity_collection_ll);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            Picasso.with(CollectionActivity.this).load(list.get(position).getImg()).into(viewHolder.imageView);
            viewHolder.title.setText(list.get(position).getTitle());
            viewHolder.indroation.setText(list.get(position).getIndroation());
            viewHolder.collectNum.setText(list.get(position).getCollectNum());
            if(chekBox == 0){
                viewHolder.checkBox.setVisibility(View.GONE);
                viewHolder.boxLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CollectionActivity.this,DetailsActivity.class);
                        intent.putExtra("videoId",list.get(position).getId());
                        startActivity(intent);
                    }
                });

            }else {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
                viewHolder.checkBox.setOnCheckedChangeListener(new LinCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(LinCheckBox checkBox, boolean isChecked) {
                        if(isChecked){
                            list.get(position).setCheck(true);
                        }
                    }
                });
            }
            return view;
        }
        class ViewHolder{
            ImageView imageView;
            TextView title,indroation,collectNum;
            LinCheckBox checkBox;
            LinearLayout boxLl;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list.clear();
    }
}
