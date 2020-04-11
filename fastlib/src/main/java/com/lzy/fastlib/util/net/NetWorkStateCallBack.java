package com.lzy.fastlib.util.net;

/**
 * 类描述：如要实时监听网络状态，需实现该接口
 * 作者：lzy on 2015/3/14
 */
public interface NetWorkStateCallBack {
    void netWorkState(ConnectivityType type);
}
