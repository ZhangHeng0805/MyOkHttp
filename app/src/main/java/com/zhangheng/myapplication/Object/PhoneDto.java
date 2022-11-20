package com.zhangheng.myapplication.Object;

/**
 * 手机通讯录实体
 */
public class PhoneDto {
    private String name;    //联系人姓名
    private String telPhone;  //电话号码

    public PhoneDto(String name, String telPhone) {
        this.name = name;
        this.telPhone = telPhone;
    }

    public PhoneDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", telPhone:'" + telPhone + '\'' +
                '}';
    }
}
