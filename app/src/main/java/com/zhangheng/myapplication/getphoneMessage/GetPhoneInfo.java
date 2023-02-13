package com.zhangheng.myapplication.getphoneMessage;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.setting.ServerSetting;
import com.zhangheng.myapplication.util.EncryptUtil;
import com.zhangheng.myapplication.util.PhoneInfoUtils;
import com.zhangheng.util.TimeUtil;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import static android.content.Context.TELEPHONY_SERVICE;


/*获取手机信息*/
public class GetPhoneInfo {

    private static ServerSetting setting;

    public static String getHead(Context context) {
        return getHead(context,false);
    }
    public static String getHead(Context context,Boolean isUpdate) {
        if (isUpdate) {
            String model = GetPhoneInfo.model;
            String net = Address.getIPAddress(context);
            String release = GetPhoneInfo.release;
            String versionCode = versionCode(context);
            String tel = EncryptUtil.enBase64Str(phoneNum(context),"UTF-8");
            String id = getID(context);
            String notice = EncryptUtil.enBase64Str(GetPhoneInfo.notice,"UTF-8");
            Date date = new Date();
            long time = date.getTime();
            String sign=time+id+model+net+release+versionCode+tel+notice+ TimeUtil.toTime(date,TimeUtil.enDateFormat_Detailed);
            JSONObject obj = JSONUtil.createObj();
            obj.set("ID",id);
            obj.set("model",model);
            obj.set("sdk",net);
            obj.set("release",release);
            obj.set("Appversion",versionCode);
            obj.set("tel",tel);
            obj.set("notice", notice);
            obj.set("time",time);
            try {
                obj.set("token", EncryptUtil.getSignature(sign,id));
            } catch (Exception e) {
                e.printStackTrace();
            }
//            System.out.println(obj.toStringPretty());
            String json = obj.toString();
            if (setting==null)
                setting = new ServerSetting(context);
            setting.setFlag_phone_info(json);
            return json;
        }else {
            if (setting==null)
                setting = new ServerSetting(context);
            return setting.getFlag_phone_info();
        }

//        String head = "(model:" + model
//                + ") (sdk:" + net
//                + ") (release:" + release
//                + ") (Appversion:" + versionCode
//                + ") (tel:" + phoneNum
//                + ") (ID:" + id
//                + ")";
    }

    public static String model = Build.BRAND+" "+Build.MODEL; // 手机型号
//    public static String sdk = Build.VERSION.SDK; // SDK号
    public static String release = "Android" + Build.VERSION.RELEASE; // android系统版本号

    public static String notice=null;

    public static String versionCode(Context context) {//应用版本
        String versionCode = context.getResources().getString(R.string.app_name)+"_" + PhoneSystem.getVersionCode(context);
        return versionCode;
    }

    public static String phoneNum(Context context){//获取本机手机号码（有可能获取不到）
        String getPhone = null;
        PhoneInfoUtils phoneInfoUtils = new PhoneInfoUtils(context);
        TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }
        String line1Number=null;
        try {
            line1Number = phoneManager.getLine1Number();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (line1Number == null) {
            getPhone=null;
        } else if (line1Number.startsWith("+86")) {
            getPhone = line1Number.replace("+86", "");
        } else {
            getPhone = line1Number;//得到电话号码
        }

        String providersName = phoneInfoUtils.getProvidersName();
        String nativePhoneNumber = phoneInfoUtils.getNativePhoneNumber();

        if (getPhone!=null){
            getPhone = "[" + providersName + "]" + getPhone;
        }else {
            getPhone = "[" + providersName + "]" + nativePhoneNumber;
        }

        return getPhone;
    }

    /**
     * 获取设备唯一ID
     * @param context
     * @return
     */
    public static String getID(Context context) {

        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }
        String id=null;
        try {
            BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
            m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            id = Build.SERIAL;// 获取序列号
            if (id.equals("unknown")) {
                id = m_BluetoothAdapter.getAddress();//蓝牙的MAC地址
                if (id.equals("02:00:00:00:00:00")) {
                    id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);//获取ANDROID_ID
                    if (id.equals("unknown")) {
                        id = null;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (setting==null)
            setting=new ServerSetting(context);
        String phoneLocation = setting.getFlag_phone_location();
        notice=JSONUtil.parseObj(phoneLocation).getStr("aoiName");
        if (StrUtil.isEmpty(id)) {
            id = "85" + //自己拼接构造IMEI Pseudo-Unique ID
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 +
                    Build.HOST.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 +
                    Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 digits
        }
        return id;
    }
    public static String getLocalIpAddress() {
        String localIp = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface ni = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        localIp = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return localIp;
    }

}
