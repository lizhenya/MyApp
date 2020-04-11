/**
 * 下午1:03:25
 */
package com.lzy.fastlib.util.net;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

/**
 * 类描述：Android网络状态及相关操作的工具类
 * 作者：lzy on 2015/3/14
 */
public class NetworkHelper {
    /**
     * 该URL是从http://www.ip138.com/网页源码（view-source:http://www.ip138.com/）中获取，可能会变动
     */
    private static final String QUERY_URL = "http://2017.ip138.com/ic.asp";
    private static final String IP_DEFAULT = "0.0.0.0";
    private static String ping;
    private static final int PING_SUCCESS = 0;
    private static final int PING_FAILE = 1;
    private static String ip;

    /**
     * <--方法描述：获取ConnectivityManager实例-->
     */
    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 方法描述：判断网络是否连接
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = getConnectivityManager(context);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 方法描述：判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        return getConnectivityManager(context) != null &&
                getConnectivityManager(context).getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 方法描述：判断当前网络是否移动网络
     */
    public static boolean isMobileNet(Context context) {
        ConnectivityManager connectivity = getConnectivityManager(context);
        NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 方法描述：获取简单的网络连接类型：wifi、移动蜂窝网络、未知网络和离线状态
     */
    public static ConnectivityType getSimpleConnectedType(Context context) {
        NetworkInfo mNetworkInfo = getConnectivityManager(context)
                .getActiveNetworkInfo();
        if (null != mNetworkInfo) {
            switch (mNetworkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return ConnectivityType.WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    return ConnectivityType.MOBILE;
                default:
                    return ConnectivityType.UNKNOWN;
            }
        }
        return ConnectivityType.OFFLINE;
    }

    /**
     * 方法描述：获取连接的具体网络类型：wifi、4G、3G、2G、未知、离线
     */
    public static ConnectivityType getConnectedType(Context context) {
        NetworkInfo mNetworkInfo = getConnectivityManager(context)
                .getActiveNetworkInfo();
        if (null != mNetworkInfo) {
            if (ConnectivityManager.TYPE_WIFI == mNetworkInfo.getType()) {
                return ConnectivityType.WIFI;
            }
            if (ConnectivityManager.TYPE_MOBILE == mNetworkInfo.getType()) {
                switch (mNetworkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return ConnectivityType.TWO_G;

                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        return ConnectivityType.THREE_G;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        return ConnectivityType.FOUR_G;
                    default:
                        return ConnectivityType.UNKNOWN;
                }
            } else {
                return ConnectivityType.UNKNOWN;
            }
        }
        return ConnectivityType.OFFLINE;
    }

    /**
     * 方法描述：获取内网或者手机移动网络内部IP地址（当手机连接WiFi时获取的ip为路由器分配的内部ip）
     */
    public static String getIntranetIpAddress(Context context) {
        if (isMobileNet(context)) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        if (isWifi(context)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            StringBuilder sb = new StringBuilder();
            sb.append(ipAddress & 0xFF).append(".");
            sb.append((ipAddress >> 8) & 0xFF).append(".");
            sb.append((ipAddress >> 16) & 0xFF).append(".");
            sb.append((ipAddress >> 24) & 0xFF);
            return sb.toString();
        }
        return NetworkHelper.IP_DEFAULT;
    }


    //获取公网ip地址
    public static void getPublicNetworkIp(final PublicNetworkIpCallback ipCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inStream = null;
                try {
                    URL infoUrl = new URL(QUERY_URL);
                    URLConnection connection = infoUrl.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int responseCode = httpConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        inStream.close();
                        int start = builder.indexOf("[");
                        int end = builder.indexOf("]");
                        ip = builder.substring(start + 1, end);
                        if (ipCallback != null) {
                            ipCallback.callback(ip);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 方法描述：wifi连接时判断当前WiFi是否可用
     *
     * @param ipString ip地址
     * @return success表示网络畅通，否则网络不通
     */
    public static int Ping(String ipString) {
        int result = -1;
        Process p;
        try {
            // ping -c 2 -w 100 中 ，-c 是指ping的次数 2是指ping 2次 ，-w 100
            // 以秒为单位指定超时间隔，是指超时时间为100秒
            p = Runtime.getRuntime().exec("ping -c 2 -w 100 " + ipString);
            int status = p.waitFor();

            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }

            if (status == 0) {
                result = PING_SUCCESS;
            } else {
                result = PING_FAILE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
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


    public static void setPing(String ping) {
        NetworkHelper.ping = ping;
    }

}
