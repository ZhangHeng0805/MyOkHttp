package com.zhangheng.myapplication.util;

public class OkHttpMessageUtil {

    public static String error(Exception e) {
        String msg = null;
        String message = e.getMessage();
        if (message.indexOf("404") > 1 || message.indexOf("not found") > 1) {
            msg = "服务器未连接";
        } else if (message.startsWith("Unable to resolve host")) {
            msg = "网络异常";
        } else if (message.indexOf("timeout") >= 0) {
            msg = "连接超时";
        } else if (message.startsWith("failed to connect to") || message.startsWith("Failed to connect to")) {
            msg = "连接失败";
        } else if (message.startsWith("unexpected end of stream on")) {
            msg = "找不到服务器";
        } else if (message.startsWith("request failed , reponse's code is")) {
            Integer code = Integer.valueOf(message.substring(message.indexOf(" : ") + 3));
            msg = getCode(code);
        } else {
            msg = message;
        }
        return msg;
    }

    private static String getCode(Integer code) {
        String m = "";
        switch (code) {
            case 400:
                m = "错误请求";
                break;
            case 401:
                m = "未授权";
                break;
            case 403:
                m = "拒绝请求";
                break;
            case 404:
                m = "未找到";
                break;
            case 406:
                m = "不接受";
                break;
            case 408:
                m = "请求超时";
                break;
            case 413:
                m = "请求实体过大";
                break;
            case 414:
                m = "请求的 URI 过长";
                break;
            case 500:
                m = "服务器内部错误";
                break;
            case 503:
                m = "服务器暂时不可用";
                break;
            case 504:
                m = "网关超时";
                break;
            case 505:
                m = "HTTP 版本不受支持";
                break;
            default:
                m = "状态码：" + code;
                break;
        }
        return m;
    }

    public static String response(String response) {
        String msg = "";
        if (response.indexOf("WEB服务器没有运行") > 1) {
            msg = "WEB服务器没有运行";
        } else {
            msg = null;
        }
        return msg;
    }
}
