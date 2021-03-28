package com.zhangheng.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.zhangheng.myapplication.util.TimeUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Main13Activity extends AppCompatActivity implements View.OnClickListener {

    private EditText m13_et_ip,m13_et_port,m13_et_info;
    private Button m13_btn_connection,m13_btn_submit;
    private TextView m13_tv_message;


    private SocketThread mSocketThread;
    private static Handler mMainHandler;
    private static Channel mChannel;
    private static ChannelFuture mFuture;

    private ScrollView m13_scrollview;

    public static final int MSG_CONNECT = 0x001;
    public static final int MSG_RECEIVE = 0x002;
    public static final int MSG_SEND = 0x003;

    public static final String DATA_RECEIVE = "data_receive";
    public static final String DATA_SEND = "data_send";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main13);
        m13_et_ip=findViewById(R.id.m13_et_ip);
        m13_et_port=findViewById(R.id.m13_et_port);
        m13_et_info=findViewById(R.id.m13_et_info);
        m13_btn_connection=findViewById(R.id.m13_btn_connection);
        m13_btn_submit=findViewById(R.id.m13_btn_submit);
        m13_tv_message=findViewById(R.id.m13_tv_message);
        m13_btn_connection.setOnClickListener(this);
        m13_btn_submit.setOnClickListener(this);
        m13_scrollview=findViewById(R.id.m13_scrollview);

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CONNECT:
                        Toast.makeText(Main13Activity.this, "服务器连接成功", Toast.LENGTH_SHORT).show();
                        m13_btn_connection.setText("已连接");
                        m13_btn_connection.setEnabled(false);
                        m13_tv_message.setText(TimeUtil.getSystemTime()+" 聊天记录：");
                        break;
                    case MSG_RECEIVE:
                        Bundle data = msg.getData();
                        String dataStr = data.getString(DATA_RECEIVE);
                        CharSequence originData = m13_tv_message.getText();
                        String result = originData + "\n" + dataStr;
                        m13_tv_message.setText(result);
                        m13_scrollview.smoothScrollTo(0, m13_tv_message.getBottom());
                        break;
                }
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        final String ip ;
        final String port ;
        ip = m13_et_ip.getText().toString();
        port = m13_et_port.getText().toString();
        if (mChannel!=null){
            if (mChannel.isActive()) {
                m13_btn_connection.setText("已连接");
                m13_btn_connection.setEnabled(false);
            }else {
                m13_btn_connection.setText("连接聊天");
                m13_btn_connection.setEnabled(true);
                m13_tv_message.setText("请先连接聊天");
            }
        }else {
            m13_btn_connection.setText("连接聊天");
            m13_btn_connection.setEnabled(true);
            m13_tv_message.setText("请先连接聊天");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mChannel!=null) {
            if (mChannel.isActive()) {
                mChannel.close();
            }
        }else {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.m13_btn_submit:
                String info = m13_et_info.getText().toString();
                if (info.length()>0){
                    if (m13_btn_connection.getText().toString().equals("已连接")) {
                        mSocketThread.sendMessage(info);
                        m13_et_info.setText("");
                    }else {
                        Toast.makeText(Main13Activity.this,"聊天服务器未连接，请先连接",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Main13Activity.this,"信息不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.m13_btn_connection:
                String ip = m13_et_ip.getText().toString();
                String port = m13_et_port.getText().toString();
                if (ip.length()>0&&port.length()>0){
                    connectToServer(ip, Integer.valueOf(port));
                }else {
                    Toast toast = Toast.makeText(Main13Activity.this, "ip和端口不能为空", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }


    private void connectToServer(String ip, int port) {
        mSocketThread = new SocketThread(ip, port);
        mSocketThread.start();
    }

    private static class SocketThread extends Thread {

        private String mIp;
        private int mPort;
        private SendThread mSendThread;

        public SocketThread(String ip, int port) {
            this.mIp = ip;
            this.mPort = port;
        }

        @Override
        public void run() {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            Bootstrap clientBootStrap = new Bootstrap();
            clientBootStrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline=ch.pipeline();
                            pipeline.addLast(new EchoClientHandler(mMainHandler));
                        }
                    });
            try {
                Message message = mMainHandler.obtainMessage();
                message.what = Main13Activity.MSG_RECEIVE;
                Bundle extra = new Bundle();
                String data="";
                // Start the client.
                mFuture = clientBootStrap.connect(mIp, mPort).sync();
                if (mFuture.isSuccess()) {
                    mChannel = mFuture.channel();
                   data="=====客户端"+mChannel.localAddress()+" 连接成功=====";
                    mSendThread = new SendThread(mChannel);
                    mSendThread.start();
                }else {
                    data="连接失败";
                }
                extra.putString(Main13Activity.DATA_RECEIVE, data);
                message.setData(extra);
                mMainHandler.sendMessage(message);
                // Wait until the connection is closed.
                mChannel.closeFuture().sync();
            } catch (Exception e) {
                Message message = mMainHandler.obtainMessage();
                message.what = Main13Activity.MSG_RECEIVE;
                Bundle extra = new Bundle();
                String data="";
                if (e.getMessage().indexOf("Connection refused")>=0){
                    Log.e("服务器连接：","服务器连接失败");
                    data="服务器连接拒绝(服务器可能没开，或者防火墙拦截)";
                }
               Log.e("错误：",e.getMessage());
                extra.putString(Main13Activity.DATA_RECEIVE, data);
                message.setData(extra);
                mMainHandler.sendMessage(message);
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        }

        public void sendMessage(String data) {
            Handler socketHandler = mSendThread.getSocketHandler();
            Message message = socketHandler.obtainMessage();
            message.what = MSG_SEND;
            Bundle bundle = new Bundle();
            bundle.putString(DATA_SEND, data);
            message.setData(bundle);
            socketHandler.sendMessage(message);
        }

    }

    private static class SendThread extends Thread {
        private Handler mSocketHandler;
        private Channel mChannel;

        public SendThread(Channel channel) {
            mChannel = channel;
        }

        @Override
        public void run() {
            // init child thread handler
            if (mSocketHandler == null) {
                Looper.prepare();
                mSocketHandler = new Handler(Looper.myLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case MSG_SEND:
                                String data = msg.getData().getString(DATA_SEND);
                                ByteBuf byteBuf = Unpooled.buffer(data.length());   //使用非池化Buffer去申请内存，这里待优化
                                byteBuf.writeBytes(data.getBytes());
                                mChannel.writeAndFlush(byteBuf);
                                break;
                        }
                    }
                };
            }
            Looper.loop();
        }

        public Handler getSocketHandler() {
            return mSocketHandler;
        }
    }

    public static class EchoClientHandler extends ChannelInboundHandlerAdapter {
        private Handler mMainHandler;

        public EchoClientHandler(Handler handler) {
            mMainHandler = handler;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            mMainHandler.sendEmptyMessage(Main13Activity.MSG_CONNECT);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byt = (ByteBuf) msg;
            byte[] bytesSrc = new byte[byt.readableBytes()];
            byt.readBytes(bytesSrc);
            String data = new String(bytesSrc);
            Message message = mMainHandler.obtainMessage();
            message.what = Main13Activity.MSG_RECEIVE;
            Bundle extra = new Bundle();
            extra.putString(Main13Activity.DATA_RECEIVE, data);
            message.setData(extra);
            mMainHandler.sendMessage(message);

            byt.release();  // 释放资源
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            Log.e("错误：",cause.getMessage());

            Message message = mMainHandler.obtainMessage();
            message.what = Main13Activity.MSG_RECEIVE;
            Bundle extra = new Bundle();
            String data="";
            if (cause.getMessage().startsWith("Connection timed out")){
                data="聊天连接超时";

            }else if (cause.getMessage().indexOf("Connection reset by peer")>=0){
                data="聊天服务器断开连接，请退出!";
            }else if (cause.getMessage().indexOf("Connection refused")>=0){
                data="服务器未连接";
            }
            else{
                data="错误："+cause.getMessage();
            }
            extra.putString(Main13Activity.DATA_RECEIVE, data);
            message.setData(extra);
            mMainHandler.sendMessage(message);
            ctx.close();
        }

    }
}
