package com.zhangheng.myapplication.getphoneMessage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RegisterMessage {
    private static Context mContext;
    private static TelephonyManager mTelephonyManager;
    private ConnectivityManager mConnMngr;
    private static SubscriptionManager mSubscriptionManager;
    public RegisterMessage(Context context) {
        mContext = context;
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager == null) {
            throw new Error("telephony manager is null");
        }
        mConnMngr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mSubscriptionManager = SubscriptionManager.from(mContext);
    }
    public String getMsisdn(int slotId) {//slotId 0为卡1 ，1为卡2
        return getLine1NumberForSubscriber(getSubIdForSlotId(slotId));
    }
    private int getSubIdForSlotId(int slotId) {
        int[] subIds = getSubId(slotId);
        if (subIds == null || subIds.length < 1 || subIds[0] < 0) {
            return -1;
        }
        Log.d("选择的卡号", String.valueOf(subIds[0]));
        return subIds[0];
    }
    private static int[] getSubId(int slotId) {
        Method declaredMethod;
        int[] subArr = null;
        try {
            declaredMethod = Class.forName("android.telephony.SubscriptionManager").getDeclaredMethod("getSubId", new Class[]{Integer.TYPE});
            declaredMethod.setAccessible(true);
            subArr = (int[]) declaredMethod.invoke(mSubscriptionManager,slotId);
        } catch (Exception e) {
            e.printStackTrace();
            declaredMethod = null;
        }
        if(declaredMethod == null) {
            subArr = null;
        }
        Log.d("getSubId = ", String.valueOf(subArr[0]));
        return subArr;
    }
    private String getLine1NumberForSubscriber(int subId){
        Method method;
        String status = null;
        try {
            method = mTelephonyManager.getClass().getMethod("getLine1NumberForSubscriber", int.class);
            method.setAccessible(true);
            status = String.valueOf(method.invoke(mTelephonyManager, subId));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.d("getLine1NumberForSubscriber = ",status);
        return status;
    }
}
