package com.zhangheng.myapplication.bean.shop.submitgoods;

import java.util.List;
import java.util.Map;

public class SubmitGoods {

    private String name;

    private String phone;

    private String address;

    private List<goods> goods_list;

    private double count_price;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<goods> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(List<goods> goods_list) {
        this.goods_list = goods_list;
    }

    public double getCount_price() {
        return count_price;
    }

    public void setCount_price(double count_price) {
        this.count_price = count_price;
    }
}
