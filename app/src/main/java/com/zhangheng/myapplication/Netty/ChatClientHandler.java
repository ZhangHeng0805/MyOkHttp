package com.zhangheng.myapplication.Netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s.trim());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getMessage().indexOf("远程主机强迫关闭了一个现有的连接")>=0){
            System.out.println("服务器关闭");
        }else {
            System.out.println("服务器错误："+ cause.getMessage());
        }
        ctx.close();
    }
}
