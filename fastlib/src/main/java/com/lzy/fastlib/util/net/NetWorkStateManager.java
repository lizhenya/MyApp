package com.lzy.fastlib.util.net;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * 类描述：网络状态入口类（包括临时网络状态的获取与实时网络状态的获取）
 * 作者：lzy on 2015/3/14
 */

public class NetWorkStateManager {
    private Context mCtx;
    private static volatile NetWorkStateManager manager;
    private static final Object objLock = new Object();
    private NetWorkReceiver receiver;

    private NetWorkStateManager(Context context) {
        this.mCtx = context;
    }

    public static NetWorkStateManager getInstance(Context context) {
        if (manager == null) {
            synchronized (objLock) {
                if (manager == null) {
                    return new NetWorkStateManager(context);
                }
            }
        }
        return manager;
    }

    /**
     * 方法描述：临时一次获取网络状态
     *
     * @return ConnectivityType类型
     */
    public ConnectivityType getNetWorkState() {
        return NetworkHelper.getConnectedType(mCtx);
    }

    public ConnectivityType getSimpleNetWorkState() {
        return NetworkHelper.getSimpleConnectedType(mCtx);
    }

    /**
     * 方法描述：实时网络状态的获取
     *
     * @param callBack 实时网络状回调接口
     */
    public void getRealTimeNetWorkState(NetWorkStateCallBack callBack) {
        receiver = register(mCtx);
        receiver.setNetWorkStateCallBack(callBack);
    }

    /**
     * 获取内网ip
     */
    public String getIntranetIp() {
        return NetworkHelper.getIntranetIpAddress(mCtx);
    }

    /**
     * 获取公网IP
     */
    public void getPublicNetworkIp(PublicNetworkIpCallback callback) {
        NetworkHelper.getPublicNetworkIp(callback);
    }

    /**
     * 方法描述：动态注册广播接收器（修复兼容7.0版本）
     */
    private NetWorkReceiver register(Context context) {
        this.mCtx = context;
        receiver = new NetWorkReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
        return receiver;
    }

    public void unregister(NetWorkReceiver receiver) {
        mCtx.unregisterReceiver(receiver);
    }

    /**
     * 方法描述：打开网络设置界面
     *
     * @param activity 上下文
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }
}
