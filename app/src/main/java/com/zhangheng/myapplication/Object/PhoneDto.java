package com.zhangheng.myapplication.Object;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneDto phoneDto = (PhoneDto) o;
        return Objects.equals(name, phoneDto.name) &&
                Objects.equals(telPhone, phoneDto.telPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, telPhone);
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", telPhone:'" + telPhone + '\'' +
                '}';
    }
}
