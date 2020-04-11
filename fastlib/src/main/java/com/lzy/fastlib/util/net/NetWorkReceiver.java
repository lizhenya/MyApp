package com.lzy.fastlib.util.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * 类描述：用于实时监听网络状态变化的广播接收器
 * 作者：lzy on 2015/3/14
 */
public class NetWorkReceiver extends BroadcastReceiver {
    private NetWorkStateCallBack callBack;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null == action) {
            return;
    }
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityType connectedType = NetworkHelper.getConnectedType(context);
            if (callBack == null) {
                throw new NullPointerException("java.lang.NullPointerException at" + NetWorkReceiver.class.getSimpleName() + ".callBack");
            }
            callBack.netWorkState(connectedType);
        }
    }

    public void setNetWorkStateCallBack(NetWorkStateCallBack callBack) {
        this.callBack = callBack;
    }
}
