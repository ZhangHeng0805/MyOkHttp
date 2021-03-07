package com.zhangheng.myapplication.Object;

public class User {

    private Integer id;
    private String lastName;
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "\t|id："+id+"\t\t|姓名："+lastName+"\t\t|邮箱："+email;
    }
}
