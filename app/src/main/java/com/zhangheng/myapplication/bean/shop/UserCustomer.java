package com.zhangheng.myapplication.bean.shop;

public class UserCustomer {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCustomer{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
