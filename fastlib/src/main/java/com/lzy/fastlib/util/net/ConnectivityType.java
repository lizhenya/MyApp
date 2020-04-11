package com.lzy.fastlib.util.net;

/**
 * 类描述：网络状态类型的枚举类
 * 作者：lzy on 2015/3/14
 */
public enum ConnectivityType {
    UNKNOWN("未知网络"), MOBILE("移动蜂窝网络"), WIFI("wifi网络"), TWO_G("2G"), THREE_G("3G"), FOUR_G("4G"),FIVE_G("5G"), OFFLINE("网络连接已断开");
    private String type;

    ConnectivityType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
