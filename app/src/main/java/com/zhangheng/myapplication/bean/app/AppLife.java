package com.zhangheng.myapplication.bean.app;


import java.util.Arrays;

public class AppLife {

    private Long createTime;
    private int maxDay;
    private Integer[] index;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getMaxDay() {
        return maxDay;
    }

    public void setMaxDay(int maxDay) {
        this.maxDay = maxDay;
    }

    public Integer[] getIndex() {
        return index;
    }

    public void setIndex(Integer[] index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "AppLife{" +
                "createTime=" + createTime +
                ", maxDay=" + maxDay +
                ", index=" + Arrays.toString(index) +
                '}';
    }
}
