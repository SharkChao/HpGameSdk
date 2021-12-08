package com.hupu.gamesdk.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.BidiFormatter;
import android.text.TextDirectionHeuristics;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;


/**
 * 移动设备相关信息类
 */
public class HPDeviceInfo {

//    private Context context;
    private static float scale;
    private static int screenWidth;
    private static int screenHeight;
    private static int fullscreenHeight;
    public static final String CTWAP = "ctwap";
    public static final String CTNET = "ctnet";
    public static final String CMWAP = "cmwap";
    public static final String CMNET = "cmnet";
    public static final String WAP_3G = "3gwap";
    public static final String NET_3G = "3gnet";
    public static final String UNIWAP = "uniwap";
    public static final String UNINET = "uninet";



    public static final int NETWORK_NONE = 0; // 没有网络连接
    public static final int NETWORK_WIFI = 1; // wifi连接
    public static final int NETWORK_2G = 2; // 2G
    public static final int NETWORK_3G = 3; // 3G
    public static final int NETWORK_4G = 4; // 4G
    public static final int NETWORK_MOBILE = 5; // 手机流量

    public static int NETTYPE = NETWORK_NONE;


    public static final String APP_HTTP_VERSION = "7.3.23";


    public static final int TYPE_NET_WORK_DISABLED = 0;// 网络不可用
    public static final int TYPE_CM_CU_WAP = 4;// 移动联通wap10.0.0.172
    public static final int TYPE_CT_WAP = 5;// 电信wap 10.0.0.200
    public static final int TYPE_OTHER_NET = 6;// 电信,移动,联通,wifi 等net网络
    public static Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");

    private static String webUa = null;

    private HPDeviceInfo(Context c) {
//        context = c;
        instance = this;
        // scale = context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取手机型号的方法，该方法为静态方法，可以直接使用
     * <p>用法示例如下所示
     *     <pre>{@code
     *           params.put("device_model",HPDeviceInfo.getSystemMobile());
     *     }
     *     </pre>
     * <p/>
     *
     * @return 返回 {@code String} 类型的手机型号
     */
    public static String getSystemMobile() {
        return Build.MODEL;
    }

    /**
     * 获取当前手机的安卓SDK版本号，该方法为静态方法，可以直接使用
     * <p>用法示例如下所示
     *     <pre>{@code
     *           params.put("device_model",HPDeviceInfo.getSystemMobile());
     *     }
     *     </pre>
     * </p>
     * @return 返回 {@code int} 类型的版本号
     */
    public static int getSystemInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机厂商，该方法为静态方法，可直接调用
     * <p>用法示例如下所示
     *     <pre> {@code
     *            OtherBaseSender.sendSetAndroidToken(this, serverInterface, HPDeviceInfo.getSystemManufacturer());
     *     }
     *     </pre>
     * <p/>
     * @return 返回 {@code String} 类型的生产厂商号
     */
    public static String getSystemManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取系统版本号，该方法为静态方法，可直接调用
     * <p>用法示例如下所示
     *     <pre> {@code
     *            mParam.put("_osv", HPDeviceInfo.getSystemVersion());
     *     }
     *     </pre>
     * <p/>
     * @return 返回 {@code String} 类型的系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    public static void init(Context c) {
        new HPDeviceInfo(c);
    }

    /**
     * 获取屏幕宽度，该方法为静态方法，可直接调用
     * <p>用法示例如下所示
     *     <pre> {@code
     *            params.width = (int)((HPDeviceInfo.getScreenWidth()/720.0)*94);
     *     }
     *     </pre>
     * <p/>
     * @return 返回 {@code int} 类型的屏幕宽度
     */
    public static int getScreenWidth() {
        return screenWidth;
    }

    /**
     * 设置屏幕宽度
     * <p>该方法已经在HupuApp中调用过一次，正常情况下不需要再进行调用。调用的代码如下所示
     *     <pre> {@code
     *            HPDeviceInfo.setScreenWidth(display.getWidth());
     *     }
     *     </pre>
     * <p/>
     * @param screenWidth 屏幕宽度，类型为 {@code int} 类型
     */
    public static void setScreenWidth(int screenWidth) {
        HPDeviceInfo.screenWidth = screenWidth;
    }
    /**
     * 获取屏幕高度，该方法为静态方法，可直接调用
     * <p>用法示例如下所示
     *     <pre> {@code
     *            params.width = (int)((HPDeviceInfo.getScreenWidth()/720.0)*94);
     *     }
     *     </pre>
     * 注意:<b>该方法获取的宽度为不包含虚拟键盘的屏幕高度，注意与getFullScreenHeight方法作区分<b/>
     * <p/>
     * @return 返回 {@code int} 类型的屏幕宽度
     */
    public static int getScreenHeight() {
        return screenHeight;
    }
    /**
     * 获取屏幕高度，该方法为静态方法，可直接调用
     * <p>用法示例如下所示
     *     <pre> {@code
     *            if(HPDeviceInfo.getScreenWidth()>HPDeviceInfo.getFullScreenHeight())
     *     }
     *     </pre>
     * 注意:<b>该方法获取的宽度为包含虚拟键盘的屏幕高度，注意与getScreenHeight方法作区分<b/>
     * <p/>
     * @return 返回 {@code int} 类型的屏幕宽度
     */
    public static int getFullScreenHeight() {
        return fullscreenHeight;
    }

    /**
     * 设置屏幕高度，该方法为静态方法，可直接调用
     * <p>该方法已经在HupuApp中调用过一次，正常情况下不应再次调用，调用的代码如下所示
     *     <pre> {@code
     *             HPDeviceInfo.setScreenHeight(display.getHeight());
     *     }
     *     </pre>
     * 注意:<b>该方法设置的为屏幕高度，不包含虚拟键盘的高度，注意与setFullScreenHeight的区别</b>
     * </p>
     *
     * @param screenHeight 屏幕高度,类型为 {@code int} 类型,<b>注意传入的高度为不带虚拟键盘的屏幕高度</b>
     */
    public static void setScreenHeight(int screenHeight) {
        HPDeviceInfo.screenHeight = screenHeight;
    }

    /**
     * 设置屏幕高度，该方法为静态方法，可直接调用
     * <p>该方法已经在HupuApp中调用过一次，正常情况下不应再次调用，调用的代码如下所示
     *     <pre> {@code
     *             HPDeviceInfo.setFullScreenHeight(HPDeviceInfo.getHasVirtualKey(HuPuApp.this));
     *     }
     *     </pre>
     * 注意:<b>该方法设置的为屏幕高度，包含虚拟键盘的高度，注意与setScreenHeight的区别</b>
     * </p>
     *
     * @param fullscreenHeight 屏幕高度,类型为 {@code int} 类型,<b>注意传入的高度为带虚拟键盘的屏幕高度</b>
     */
    public static void setFullScreenHeight(int fullscreenHeight) {
        HPDeviceInfo.fullscreenHeight = fullscreenHeight;
    }

    public static float getScale() {
        return HPDeviceInfo.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public static HPDeviceInfo instance;


    /**
     * 获取是否有可用的网络
     * <p>该方法判断是否手机有可用的网络,示例代码如下
     *     <pre> {@code
     *            if (!HPDeviceInfo.isNetWorkEnable(context))
     *     }
     *     </pre>
     * </p>
     * @param context 为上下文参数,如果传入的值为null,则使用{@code context = HPBaseApplication.getInstance()}获取上下文参数
     * @return 返回的类型为 {@code boolean} 类型,当手机有网络连接时返回true,当手机无网络连接时返回false
     */
    public static boolean isNetWorkEnable(Context context) {
        // return isWifi(context)|| isMobile(context);
        if(context == null) {
            return false;
        }
        return isNetAvailable(context);
    }




    /**
     * 判断是否为pad
     * <p>判断是否为平板,示例代码如下所示
     *     <pre> {@code
     *         if(isPad(context))
     *     }
     *     </pre>
     *
     * </p>
     * @param context 传入的上下文,类型为 {@code Context}
     * @return 返回的类型为 {@code boolean} 类型，如果为平板则返回 {@code true} ,如果不为平板则返回 {@code false}
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * @return true: wifi network is available, false: wifi network is
     * unavailable.
     * @Description Checking if wifi is available
     */
    public static boolean isWifi() {
		/*ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (ni == null)
			return false;
		NetworkInfo.State st = ni.getState();
		return ni.getState() == State.CONNECTED;*/
        return NETTYPE == NETWORK_WIFI;
    }

    public static boolean checkNetIs2Gor3G() {
      /*  ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        } else
            return false;*/
        return NETTYPE == NETWORK_MOBILE;
    }

    /**
     * 将dp单位的数值转换成像素
     * <p>将dp类型转换成像素,示例代码如下所示
     *     <pre> {@code
     *            layoutParams.height = HPDeviceInfo.DipToPixels(this, 211);
     *     }
     *     </pre>
     * </p>
     * @param context 上下文值,类型为 {@code Context} 类型
     * @param dip 需要转换的dp的值,类型为{@code int} 类型
     * @return 转换后的像素值,类型为 {@code int} 类型 <b>如果转换后为小数,小数位小于0.5则向下取整,反之则向上取整<b/>
     */
    public static int DipToPixels(Context context, int dip) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        float valueDips = dip;
        int valuePixels = (int) (valueDips * SCALE + 0.5f);
        return valuePixels;
    }

    /**
     *
     * @param context Context
     * @return true: mobile network is available, false: mobile network is
     * unavailable.
     * @Description Checking if Mobile network is available
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (ni == null)
            return false;
        return ni.getState() == State.CONNECTED;
        // boolean isMobileAvail = ni.isAvailable();
        // boolean isMobileConnect = ni.isConnected();
        // return isMobileAvail && isMobileConnect;
    }

    /**
     * 判断网络连接是否可用
     * <p>判断网络连接是否可用,示例代码如下所示
     *     <pre>{@code
     *           if (HPDeviceInfo.isNetAvailable(baseAct))
     *     }
     *     </pre>
     * </p>
     * @param context Context
     * @return 网络状态是否可用,类型为 {@code boolean} 类型,网络连接可用时返回true,网络连接不可用时返回false
     */
    public static boolean isNetAvailable(Context context) {
        if (null != context && null != context.getSystemService(Context.CONNECTIVITY_SERVICE)) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null)
                return false;
            return ni.getState() == State.CONNECTED;
        }
        return false;
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {   //判断网络连接是否打开
            return mNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * 是否有sd卡
     * <p>判断手机是否装有sd卡,示例代码如下所示
     *     <pre> {@code
     *            if (HPDeviceInfo.isSdcardExist())
     *     }
     *     </pre>
     * </p>
     * @return 是否安装SD卡,类型为 {@code boolean}类型, 如果安装了SD卡返回{@code true},未安装SD卡返回{@code false}
     */
    public static boolean isSdcardExist() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }



    private static String deviceId;

    /**
     * 获取当前App的版本号
     * <p>获取当前App的版本号,示例代码如下所示
     *     <pre>{@code
     *           txtVersion.setText(HPDeviceInfo.getAppVersion(this));
     *     }
     *     </pre>
     * </p>
     * @param context 上下文,类型为{@code Context}
     * @return 当前App的版本号,类型为 {@code String} 类型,如:7.3.20.11411
     */
    public static String getAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return packInfo != null ? packInfo.versionName : "";
    }

    /**
     * 获取当前App的version code
     * <p>获取当前App的version code,示例代码如下所示
     *     <pre>{@code
     *           params.put("code", HPDeviceInfo.getAppVersionCode(act));
     *     }
     *     </pre>
     * </p>
     * @param context 上下文,类型为{@code Context}类型
     * @return App的version code,类型为{@code int}类型,如7451
     */
    public static int getAppVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return packInfo != null ? packInfo.versionCode : 0;
    }

    /**
     * 随机生成6位数字
     */
    public static String getPwd(){
        int newNum = (int)((Math.random()*9+1)*100000);
        return String.valueOf(newNum)+"21213";
    }

    /**
     * 获取设备的Device ID
     * <p>获取设备的Device ID.
     * 如果能获取设备的Android ID,则直接返回设备的Android ID
     * 如果无法获取设备的Android ID,则返回设备的Client ID.
     * 示例代码如下:
     *     <pre> {@code
     *            json.put("client", HPDeviceInfo.getDeviceID(AlienWebView.this.getContext()));
     *     }
     *     </pre>
     * </p>
     * @param context 上下文,类型为Context类型
     * @return 设备的Device ID,具体见上文该方法的描述,类型为{@code String}
     */
    public static String getDeviceID(Context context) {
        String mDeviceId;
        mDeviceId = HPDeviceInfo.getAndroidID(context);
      //  mDeviceId ="283454353453534964";
        //HPDeviceInfo.getDeviceInfo(context);
        //新用户代码
       /* SharedPreferencesMgr.init(context, "hupugamemate");
        if(ObjectUtils.isNotEmpty(SharedPreferencesMgr.getString("devicedId",""))){
            mDeviceId = SharedPreferencesMgr.getString("devicedId","");
        }else{
            mDeviceId = getPwd();
            SharedPreferencesMgr.setString("devicedId",mDeviceId);
        }
        Log.v("zwb","mDeviceId:"+mDeviceId);*/
        if (mDeviceId == null) {
            mDeviceId = checkDeviceId();
        } else if (mDeviceId.length() < 8) {
            mDeviceId = checkDeviceId();
        } else {
            // 解决deviceid 00000000的
            if (mDeviceId.contains("00499901064")
                    || mDeviceId.contains("0000000")
                    || mDeviceId.contains("1111111")
                    || mDeviceId.contains("2222222")
                    || mDeviceId.contains("3333333")
                    || mDeviceId.contains("4444444")
                    || mDeviceId.contains("5555555")
                    || mDeviceId.contains("6666666")
                    || mDeviceId.contains("7777777")
                    || mDeviceId.contains("8888888")
                    || mDeviceId.contains("9999999")
                    || mDeviceId.contains("123456789")
                    || mDeviceId.contains("987654321")) {
                mDeviceId = checkDeviceId();
            }

        }
        return mDeviceId;
    }


    /**
     * 获取设备的IMEI
     * <p>
     * 获取设备的IMEI.
     * 如果能获取设备的IMEI,则直接返回设备的IMEI
     * 如果无法获取设备的IMEI,则返回设备的Client ID.
     * 示例代码如下:
     *     <pre> {@code
     *            mImei = HPDeviceInfo.getDeviceImei(HPMiddleWareBaseApplication.getInstance());
     *     }
     *     </pre>
     * </p>
     * @param context 上下文,类型为Context类型
     * @return 设备的IMEI,具体见上文该方法的描述,类型为{@code String}
     */
    public static String getDeviceImei(Context context){
        String mDeviceId;
        mDeviceId = HPDeviceInfo.getDeviceInfo(context);
        if (mDeviceId == null) {
            mDeviceId = checkDeviceId();
        } else if (mDeviceId.length() < 8) {
            mDeviceId = checkDeviceId();
        } else {
            // 解决deviceid 00000000的
            if (mDeviceId.contains("00499901064")
                    || mDeviceId.contains("0000000")
                    || mDeviceId.contains("1111111")
                    || mDeviceId.contains("2222222")
                    || mDeviceId.contains("3333333")
                    || mDeviceId.contains("4444444")
                    || mDeviceId.contains("5555555")
                    || mDeviceId.contains("6666666")
                    || mDeviceId.contains("7777777")
                    || mDeviceId.contains("8888888")
                    || mDeviceId.contains("9999999")
                    || mDeviceId.contains("123456789")
                    || mDeviceId.contains("987654321")) {
                mDeviceId = checkDeviceId();
            }

        }
//		}
        return mDeviceId;
    }

    private static String checkDeviceId() {
        return setDeviceToSpf();
    }

    private static String setDeviceToSpf() {
        String UUID = HPDeviceInfo.getUUID();
        return UUID;
    }

    public static String getDeviceInfo(Context context) {

        TelephonyManager tel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            deviceId = tel.getDeviceId();
        } catch (Exception e) {
        }
        if (deviceId == null) {
            deviceId = HPDeviceInfo.getAndroidID(context);
        }
        return deviceId;
    }


    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static int[] getScreenSize(DisplayMetrics dm) {
        int[] result = new int[2];
        result[0] = dm.widthPixels;
        result[1] = dm.heightPixels;
        return result;
    }

    /**
     * 获取IP地址
     * <p>获取设备的IP地址
     * 如果是在WIFI状态下,获取WIFI的IP地址
     * 如果是在GPRS状态下,获取GPRS的IP地址
     * 示例代码如下:
     *     <pre> {@code
     *            params.put("ip",HPDeviceInfo.getIp(act));
     *     }
     *     </pre>
     * </p>
     * @param ctx 上下文,类型为{@code Context}
     * @return IP地址,类型为 {@code String}类型
     */
    public static String getIp(Context ctx) {
        String ip = null;
        if (isWifi()) {
            ip = getIpByWifi(ctx);
        } else {
            ip = getIpByGprs();
        }
        if (ip == null) ip = "";
        return ip;
    }

    private static String getIpByWifi(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
//		//判断wifi是否开启
//		if (!wifiManager.isWifiEnabled()) {
//			wifiManager.setWifiEnabled(true);
//		}
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    private static String getIpByGprs() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }


        return null;
    }


    /**
     * 获取设备的
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        try {
            return android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";





    /**
     * 通过反射，获取包含虚拟键的整体屏幕高度
     * 解决当系统有虚拟键盘是获取的设备高度不正确
     *
     * @return
     */
    public static int getHasVirtualKey(Context context) {
        int dpi = 0;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }


    //获取wifi ssid
    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (null != wifiInfo && !TextUtils.isEmpty(wifiInfo.getSSID())) {
            return Base64.encodeToString(wifiInfo.getSSID().toString().getBytes(), Base64.NO_WRAP);//wifiInfo.getSSID();//Base64.encodeToString(appsStr.toString().getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        } else {
            return "";
        }
    }


    public static String getThirdAppList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);

        StringBuilder appsStr = new StringBuilder();
        // 判断是否系统应用：
        //List<PackageInfo> apps = new ArrayList<PackageInfo>();
        if (null != packageInfoList && packageInfoList.size() > 0) {
            for (int i = 0; i < packageInfoList.size(); i++) {
                PackageInfo pak = (PackageInfo) packageInfoList.get(i);
                //判断是否为系统预装的应用
                if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                    // 第三方应用
                    // apps.add(pak);
                  //  appsStr.append(pak.applicationInfo.loadLabel(packageManager));
                   // appsStr.append(":");
                    appsStr.append(pak.packageName);
                    appsStr.append(",");

                } else {
                    //系统应用
                }
            }
        }
        //return Base64.encodeToString(appsStr.toString().getBytes(), Base64.DEFAULT);
          return appsStr.toString();
    }





    public static String getDPI(Context context) {
//        double dpi = context.getResources().getDisplayMetrics().density;
        double dpi = context.getResources().getDisplayMetrics().scaledDensity;
        if (dpi == 0.75) return "ldpi";
        else if (dpi == 1) return "mdpi";
        else if (dpi == 1.5) return "hdpi";
        else if (dpi == 2) return "xhdpi";
        else if (dpi == 3) return "xxhdpi";
        else if (dpi == 4) return "xxxhdpi";
        return "xhdpi";
    }
    /**
     * 获取设备运营商
     * @return ["中国电信CTCC":"3"]["中国联通CUCC:"2"]["中国移动CMCC":"1"]["other":"0"]
     */
    public static String getOperatorType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getNetworkOperator();
        String opeType = "0";
        // 中国联通
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = "2";
            // 中国移动
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = "1";
            // 中国电信
        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = "3";
        }
        return opeType;
    }





    /**
     * 获取手机内部存储空间的大小.
     * 获取手机内部存储空间的大小
     * @param context 上下文,格式为 <code>Context<code/>
     * @return 手机内部存储空间的大小,格式为 <code>String</code>,例如1024M
     * <b>注意:当传入的Context为空时,返回空字符串""<b/>
     */
    public static String getInternalStorageSize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSizeLong = statFs.getBlockSizeLong();
        long blockCountLong = statFs.getBlockCountLong();
        long size = blockCountLong * blockSizeLong;
        if(context != null) {
            return Formatter.formatFileSize(context, size);
        }
        return "";
    }

    /**
     * 获取手机内部剩余存储空间的大小.
     * 获取手机剩余内部存储空间的大小
     * @param context 上下文,格式为 <code>Context</code>
     * @return 手机剩余存储空间的大小,格式为 <code>String</code>,例如1024M
     * <b>注意:当传入的Context为空时,返回空字符串""<b/>
     */
    public static String getAvailableInternalStorageSize(Context context) {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();
        if(context != null) {
            return Formatter.formatFileSize(context, availableBlocksLong
                    * blockSizeLong);
        }
        return "";
    }

    /**
     * 是否有可用网络
     *
     * @param context Context
     * @return true：网络可用，false：网络不可用
     */
    public static boolean isNetworkAvailable(Context context) {
        // 检测权限
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Network network = cm.getActiveNetwork();
                    if (network != null) {
                        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                        if (capabilities != null) {
                            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
                        }
                    }
                } else {
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    return networkInfo != null && networkInfo.isConnected();
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getTimeZone(){
        Calendar mDummyDate;
        mDummyDate = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        mDummyDate.setTimeZone(now.getTimeZone());
        mDummyDate.set(now.get(Calendar.YEAR), 11, 31, 13, 0, 0);
        return getTimeZoneText(now.getTimeZone(),true);
    }

    public static String getTimeZoneText(TimeZone tz, boolean includeName) {
        Date now = new Date();

        SimpleDateFormat gmtFormatter = new SimpleDateFormat("ZZZZ");
        gmtFormatter.setTimeZone(tz);
        String gmtString = gmtFormatter.format(now);
        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        Locale l = Locale.getDefault();
        boolean isRtl = TextUtils.getLayoutDirectionFromLocale(l) == View.LAYOUT_DIRECTION_RTL;
        gmtString = bidiFormatter.unicodeWrap(gmtString,
                isRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR);

        if (!includeName) {
            return gmtString;
        }

        return gmtString;
    }
}
