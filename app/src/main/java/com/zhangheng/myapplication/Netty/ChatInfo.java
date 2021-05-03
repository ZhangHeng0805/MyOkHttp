package com.zhangheng.myapplication.Netty;

public class ChatInfo {

    private String from;//发送人的手机号
    private String from_name;//发送人的名称
    private String to;//接收人
    private String to_name;//接收人
    private String message;//消息内容
    private int chatType;//1.公开 //2.私聊
    private int msgType;//1.用户消息 2.服务器消息
    private String time;//发送时间

    public ChatInfo() {
    }

    public ChatInfo(String from, String from_name, String to, String to_name, String message, int chatType, int msgType, String time) {
        this.from = from;
        this.from_name = from_name;
        this.to = to;
        this.to_name = to_name;
        this.message = message;
        this.chatType = chatType;
        this.msgType = msgType;
        this.time = time;
    }




    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
