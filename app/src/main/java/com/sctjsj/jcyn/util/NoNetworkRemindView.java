package com.sctjsj.jcyn.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sctjsj.jcyn.R;


/**
 * Created by mayikang on 17/2/6.
 */

public class NoNetworkRemindView extends RelativeLayout implements View.OnClickListener{
    private ReloadClickListener reloadClickListener;
    private RelativeLayout mRL;
    public NoNetworkRemindView(Context context) {
        super(context);
        initView(context);
    }


    private void initView(Context context){
        mRL= (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.no_network_remind_layout,this);

        TextView mTVReload= (TextView) mRL.findViewById(R.id.no_network_view_tv_reload);
        TextView mTVSetNet=(TextView) mRL.findViewById(R.id.no_network_view_tv_set_net);

        mTVReload.setOnClickListener(this);
        mTVSetNet.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.no_network_view_tv_reload:
                if(reloadClickListener!=null){
                    reloadClickListener.reload();
                    mRL.setVisibility(GONE);
                }
                break;
            case R.id.no_network_view_tv_set_net:
                if(reloadClickListener!=null){
                    reloadClickListener.setNetwork();
                }
                break;
        }
    }



    public  void setOnReloadClickListerer(ReloadClickListener reloadClickListener){
        this.reloadClickListener=reloadClickListener;
    }

    public interface ReloadClickListener{
        public void reload();
        public void setNetwork();

    }


}
