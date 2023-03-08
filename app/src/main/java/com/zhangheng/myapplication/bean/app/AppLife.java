package com.zhangheng.myapplication.bean.app;


import com.zhangheng.myapplication.util.EncryptUtil;
import com.zhangheng.util.TimeUtil;

import java.sql.Date;

import cn.hutool.core.util.ArrayUtil;

public class AppLife {

    private Long createTime;
    private Integer maxDay;
    private Integer[] index;
    private String appId;

    private String sign;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getMaxDay() {
        return maxDay;
    }

    public void setMaxDay(Integer maxDay) {
        this.maxDay = maxDay;
    }

    public Integer[] getIndex() {
        return index;
    }

    public void setIndex(Integer[] index) {
        this.index = index;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String createSign() throws Exception {
        String token=createTime
                +maxDay
                +ArrayUtil.toString(index)
                +appId
                ;
        return EncryptUtil.getSignature(token, TimeUtil.toTime(new Date(createTime),TimeUtil.enDateFormat_Detailed));
    }

}
