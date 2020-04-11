package com.lzy.fastlib.util.net2;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * Date：2020/4/11
 * 作者：李振亚
 * 邮箱：lizhenya@ijiuyue.com
 **/
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetCallback extends ConnectivityManager.NetworkCallback {
    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.e("NEEE", "-----------------onAvailable---------------->");
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        Log.e("NEEE", "-----------------onUnavailable---------------->");
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        Log.e("NEEE", "-----------------onCapabilitiesChanged---------------->");
    }


    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
        Log.e("NEEE", "-----------------onLosing---------------->");
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.e("NEEE", "-----------------onLost---------------->");
    }


}
