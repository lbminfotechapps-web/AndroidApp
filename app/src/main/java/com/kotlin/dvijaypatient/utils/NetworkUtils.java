package com.kotlin.dvijaypatient.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

///**
//* Created by Dhanashree N. Borade on 2019-12-31.
//* Copyright (c) 2019 LBM Infotech Pvt. Ltd. All rights reserved.
//*/

/**
 * <p>Utility methods to check the current network connection status.</p>
 *
 * <p>This requires the caller to hold the permission
 * {@link android.Manifest.permission#ACCESS_NETWORK_STATE}.</p>
 */
public class NetworkUtils {

    /**
     * The absence of a connection type.
     */
    public static final int TYPE_NONE = -1;
    /**
     * Unknown network class.
     */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /**
     * Class of broadly defined "2G" networks.
     */
    public static final int NETWORK_CLASS_2_G = 1;
    /**
     * Class of broadly defined "3G" networks.
     */
    public static final int NETWORK_CLASS_3_G = 2;
    /**
     * Class of broadly defined "4G" networks.
     */
    public static final int NETWORK_CLASS_4_G = 3;

    public static NetworkInfo getInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
    }

    public static int getType(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return TYPE_NONE;
        }
        return info.getType();
    }

    public static int getSubType(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return TYPE_NONE;
        }
        return info.getSubtype();
    }

    /**
     * Returns the NETWORK_TYPE_xxxx for current data connection.
     */
    public static int getNetworkType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                .getNetworkType();
    }

    /**
     * Check if there is any connectivity
     */
    public static boolean isConnected(Context context) {
        return getType(context) != TYPE_NONE;
    }

    /**
     * Check if there is any connectivity to a Wifi network
     */
    public static boolean isWifiConnection(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return false;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if there is any connectivity to a mobile network
     */
    public static boolean isMobileConnection(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return false;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_MOBILE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if the current connection is fast.
     */
    public static boolean isConnectionFast(Context context) {
        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return false;
        }
        switch (info.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_ETHERNET:
                return true;
            case ConnectivityManager.TYPE_MOBILE:
                int networkClass = getNetworkClass(getNetworkType(context));
                switch (networkClass) {
                    case NETWORK_CLASS_UNKNOWN:
                    case NETWORK_CLASS_2_G:
                        return false;
                    case NETWORK_CLASS_3_G:
                    case NETWORK_CLASS_4_G:
                        return true;
                }
            default:
                return false;
        }
    }

    private static int getNetworkClassReflect(int networkType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getNetworkClass = TelephonyManager.class.getDeclaredMethod("getNetworkClass", int.class);
        if (!getNetworkClass.isAccessible()) {
            getNetworkClass.setAccessible(true);
        }
        return (int) getNetworkClass.invoke(null, networkType);
    }

    /**
     * Return general class of network type, such as "3G" or "4G". In cases where classification is
     * contentious, this method is conservative.
     */
    public static int getNetworkClass(int networkType) {
        try {
            return getNetworkClassReflect(networkType);
        } catch (Exception ignored) {
        }

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16: // TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17: // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18: // TelephonyManager.NETWORK_TYPE_IWLAN:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public static String getConnectionQuality(Context context) {

        NetworkInfo info = getInfo(context);
        if (info == null || !info.isConnected()) {
            return "UNKNOWN";
        }

        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int numberOfLevels = 5;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String name = wifiInfo.getSSID();
            String linkSpeed = String.valueOf(wifiInfo.getLinkSpeed());
            String netInfo = null;
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            if (level == 2)
                netInfo = "POOR";
            else if (level == 3)
                netInfo = "MODERATE";
            else if (level == 4)
                netInfo = "GOOD";
            else if (level == 5)
                netInfo = "EXCELLENT";
            else
                netInfo = "UNKNOWN";
            return String.format("Name:%s, MaxSpeed:%s Mbps, Strength:%s", name, linkSpeed, netInfo);
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkClass = getNetworkClass(getNetworkType(context));
            String name = String.format("%s[%s]", info.getTypeName(), info.getSubtypeName());
            String provider = info.getExtraInfo();
            String netInfo = null;
            if (networkClass == 1)
                netInfo = "POOR";
            else if (networkClass == 2)
                netInfo = "GOOD";
            else if (networkClass == 3)
                netInfo = "EXCELLENT";
            else
                netInfo = "UNKNOWN";
            return String.format("Name:%s, Provider:%s, Strength:%s", name, provider, netInfo);
        } else
            return "UNKNOWN";
    }

    private NetworkUtils() {
        throw new AssertionError();
    }

    public static String getNetworkInfo(Context context) {
        String network = null;
        String connQuality = null;
        if (NetworkUtils.isConnected(context)) {
            if (NetworkUtils.isWifiConnection(context)) {
                network = "Wifi";
                connQuality = NetworkUtils.getConnectionQuality(context);
            } else if (NetworkUtils.isMobileConnection(context)) {
                network = "Mobile";
                connQuality = NetworkUtils.getConnectionQuality(context);
            }
        } else {
            network = "No Internet Connection";
            connQuality = "";
        }
        return String.format("Network:%s, %s", network, connQuality).replace(", ,", ",");
    }
}
