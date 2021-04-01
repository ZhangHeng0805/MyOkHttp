package com.zhangheng.myapplication.Netty;

import java.nio.channels.SocketChannel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClientInitializer extends ChannelInitializer<io.netty.channel.socket.SocketChannel> {
    private NettyListener listener;

    public NettyClientInitializer(NettyListener listener) {
        if(listener == null){
            throw new IllegalArgumentException("listener == null ");
        }
        this.listener = listener;
    }


    @Override
    protected void initChannel(io.netty.channel.socket.SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));    // 开启日志，可以设置日志等级
        pipeline.addLast("IdleStateHandler", new IdleStateHandler(6, 0, 0));
        pipeline.addLast("StringDecoder", new StringDecoder());//解码器 这里要与服务器保持一致
        pipeline.addLast("StringEncoder", new StringEncoder());//编码器 这里要与服务器保持一致
        pipeline.addLast(new NettyClientHandler(listener));
    }
}
