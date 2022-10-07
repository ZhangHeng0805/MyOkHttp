package com.zhangheng.myapplication.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.zhangheng.myapplication.Netty.ChatInfo;
import com.zhangheng.myapplication.Object.ChatConfig;
import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.adapter.ChatBaseAdapter;
import com.zhangheng.myapplication.base.BaseFragment;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.OkHttpMessageUtil;
import com.zhangheng.myapplication.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

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
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

public class ToolFragment extends BaseFragment {

    private TextView m15_toolfragment_txt_title;
    private static final String TAG= ToolFragment.class.getSimpleName();
    private ListView mListView;
    private EditText m15_toolfragment_et_message;
    private Button m15_toolfragment_btn_submit;
    private ChatBaseAdapter adapter;
    private SharedPreferences preferences;
    private String phone,name,password,address;
    private String to="all",to_name="全部";
    private int msgType=1,chatType=1;
    private int chedkedItem;

    private static final int MSG_CONNECT = 0x001;
    private static final int MSG_RECEIVE = 0x002;
    private static final int MSG_SEND = 0x003;
    private static final int MSG_TITLE = 0x004;

    private static final String DATA_RECEIVE = "data_receive";
    private static final String DATA_SEND = "data_send";
    private static final String DATA_TITLE = "data_title";


    private SocketThread mSocketThread;
    private static Handler mMainHandler;
    private static Channel mChannel;
    private static ChannelFuture mFuture;

    private List<ChatInfo> chatInfos=new ArrayList<>();
    private List<ChatInfo> userChat=new ArrayList<>();

    @Override
    protected View initView() {
        Log.e(TAG,"工具框架Fragment页面被初始化了");
        View view = View.inflate(mContext, R.layout.m15_fragment_tool, null);
        mListView=view.findViewById(R.id.m15_toolfragment_listview_message);
        m15_toolfragment_et_message=view.findViewById(R.id.m15_toolfragment_et_message);
        m15_toolfragment_btn_submit=view.findViewById(R.id.m15_toolfragment_btn_submit);
        m15_toolfragment_txt_title=view.findViewById(R.id.m15_toolfragment_txt_title);

        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CONNECT:
                        Toast.makeText(getContext(), "服务器连接成功", Toast.LENGTH_SHORT).show();
                        m15_toolfragment_txt_title.setText("聊天室已连接");
                        sendOnline();
                        break;
                    case MSG_RECEIVE:
                        Bundle data = msg.getData();
                        String dataStr = data.getString(DATA_RECEIVE);
                        Log.d(TAG, "handleMessage: "+dataStr);
                        List<String> list=new ArrayList<>();
                        if (dataStr.indexOf("}{")>0){
                            String[] split = dataStr.split("\\}");
                            for (String s:split){
                                list.add(s+"}");
                            }
                        }else {
                            list.add(dataStr);
                        }
                        Log.d(TAG, "handleMessage: "+list.toString());
                        try {
                            for (String json:list) {
                                Gson gson=new Gson();
                                ChatInfo chatInfo = gson.fromJson(json, ChatInfo.class);
                                String from = chatInfo.getFrom();
                                String message = chatInfo.getMessage();

                                if (chatInfo.getFrom().equals("1")
                                        && message.startsWith("用户")
                                        && message.endsWith("进入聊天室了")) {//系统提示用户上线，添加该用户
                                    ChatInfo chatInfo1 = new ChatInfo();
                                    chatInfo1.setFrom_name(message.substring(message.indexOf("《") + 1, message.indexOf("》")));
                                    String s1 = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
                                    chatInfo1.setFrom(message.substring(message.indexOf("<") + 1, message.indexOf(">")));
                                    Log.d(TAG, "handleMessage1: " + s1);
                                    chatInfo.setMessage(message.replace("<" + s1 + ">", ""));
                                    if (!s1.equals(phone)) {
                                        userChat.add(chatInfo1);
                                    }
                                } else if (chatInfo.getFrom().equals("1")
                                        && message.startsWith("用户")
                                        && message.endsWith("退出聊天室了")) {//系统提示用户下线，删除该用户
                                    ChatInfo chatInfo1 = new ChatInfo();
                                    chatInfo1.setFrom_name(message.substring(message.indexOf("《") + 1, message.indexOf("》")));
                                    String s1 = message.substring(message.indexOf("<") + 1, message.indexOf(">"));
                                    chatInfo1.setFrom(s1);
                                    Log.d(TAG, "handleMessage2: " + s1);
                                    chatInfo.setMessage(message.replace("<" + s1 + ">", ""));
                                    if (!s1.equals(phone)) {
                                        for (int n=0;n<userChat.size();n++) {
                                            int i=-1;
                                            if (userChat.get(n).getFrom().equals(s1)){
                                                i=n;
                                            }
                                            Log.d(TAG, "handleMessage2: " + i);
                                            if (i >= 0) {
                                                userChat.remove(i);
                                            }
                                        }
                                    }
                                }else if (from.length()==11){//用户发消息，判读列表是否有该用户
                                    if (userChat.size()>0) {
                                        for (ChatInfo c : userChat) {
                                            if (!from.equals(phone)) {
                                                if (!from.equals(c.getFrom())) {
                                                    userChat.add(chatInfo);
                                                }
                                            }
                                        }
                                    }else {
                                        if (!from.equals(phone)) {
                                            userChat.add(chatInfo);
                                        }
                                    }
                                }

                                chatInfos.add(chatInfo);
                                adapter = new ChatBaseAdapter(phone, name, chatInfos, getContext());
                                mListView.setAdapter(adapter);
                                mListView.setSelection(chatInfos.size());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "接收消息错误："+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MSG_TITLE:
                        Bundle data1 = msg.getData();
                        String dataStr1 = data1.getString(DATA_TITLE);
                        Toast.makeText(getContext(), dataStr1, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        Listener();

        return view;
    }
    private void Listener(){
        m15_toolfragment_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info = m15_toolfragment_et_message.getText().toString();
                if (info.length()>0){
                    if (m15_toolfragment_txt_title.getText().toString().equals("聊天室已连接")) {
                        getPreferences();
                        if (name!=null&&phone!=null) {
                            if (isUserOnline()) {
                                ChatInfo chatInfo = new ChatInfo();
                                Gson gson = new Gson();
                                chatInfo.setChatType(chatType);
                                chatInfo.setMsgType(msgType);
                                chatInfo.setFrom_name(name);
                                chatInfo.setFrom(phone);
                                chatInfo.setTo(to);
                                chatInfo.setTo_name(to_name);
                                chatInfo.setMessage(info);
                                chatInfo.setTime(TimeUtil.getSystemTime());
                                String json = gson.toJson(chatInfo);
                                mSocketThread.sendMessage(json);
                                m15_toolfragment_et_message.setText("");
                            }else {
                                DialogUtil.dialog(getContext(),"聊天对象不存在","聊天对象已下线，请重新选择聊天对象");
                            }
                        }else {
                            exitChat();
                            DialogUtil.dialog(getContext(),"请登录后再操作","前往\"我的\"的进行登录后再来操作");
                        }
                    }else {
                        exitChat();
                        Toast.makeText(getContext(),"聊天服务器未连接，请先连接",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"信息不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        m15_toolfragment_txt_title.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        m15_toolfragment_et_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                chooseChatObject();
                return true;
            }
        });
    }

    private boolean isUserOnline(){
        boolean b = false;
        if (userChat.size()>0) {
            for (ChatInfo u : userChat) {
                if (u.getFrom().equals(to)) {
                    b = true;
                    break;
                } else {
                    b = false;
                }
            }
            if (!b){
                to="all";
                to_name="全部";
                m15_toolfragment_et_message.setHint(getResources().getString(R.string.m15_toolfragment_et_hint));
                b=true;
            }
        }else {
            to="all";
            to_name="全部";
            m15_toolfragment_et_message.setHint(getResources().getString(R.string.m15_toolfragment_et_hint));
            b=true;
        }
        return b;
    }
    private void sendOnline(){
        Gson gson = new Gson();
        ChatInfo Info = new ChatInfo();
        Info.setTime(TimeUtil.getSystemTime());
        Info.setTo("all");
        Info.setChatType(1);
        Info.setMsgType(1);
        Info.setFrom(phone);
        Info.setFrom_name(name);
        if (mChannel.isActive()){
            Info.setMessage("系统0805:我上线了");
            String json=gson.toJson(Info);
            mSocketThread.sendMessage(json);
        }else {

        }
    }

    private void sendOffline(){
        Gson gson = new Gson();
        ChatInfo Info = new ChatInfo();
        Info.setTime(TimeUtil.getSystemTime());
        Info.setTo("all");
        Info.setTo_name("全部");
        Info.setChatType(1);
        Info.setMsgType(1);
        Info.setFrom(phone);
        Info.setFrom_name(name);
        Info.setMessage("系统0805:我下线了");
        String json=gson.toJson(Info);
        mSocketThread.sendMessage(json);
    }
    private void chooseChatObject(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请选择你的聊天对象：");
        List<String> namelist=new ArrayList<>();
        List<String> phonelist=new ArrayList<>();
        if (userChat.size()!=0) {
            for (ChatInfo c : userChat) {
                if (phonelist.indexOf(c.getFrom())<0) {
                    namelist.add(c.getFrom_name());
                    phonelist.add(c.getFrom());
                }
            }
        }else {
        }
        Log.d(TAG, "chooseChatObject: "+userChat.size());
        final String[] users = new String[namelist.size()+1];
        final String[] phones = new String[phonelist.size()+1];
        if (namelist.size()>0) {
            for (int i = 0; i < namelist.size(); i++) {
                users[i] = namelist.get(i);
                phones[i] = phonelist.get(i);
            }
        }
        users[namelist.size()]="全部";
        phones[phonelist.size()]="all";
        chedkedItem = namelist.size();
        builder.setSingleChoiceItems(users, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d(TAG, "选择点击onClick: "+which);
//                Toast.makeText(getContext(), "你选择了" + users[which], Toast.LENGTH_SHORT).show();
                chedkedItem=which;
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d(TAG, "选择点击onClick: "+chedkedItem);
                m15_toolfragment_et_message.setHint("" +
                        ">@"+users[chedkedItem]+",长按选择聊天对象");
                if (users[chedkedItem].equals("全部")){
                    to="all";
                    to_name="全部";
                    msgType=1;
                    chatType=1;
                }else {
                    to=phones[chedkedItem];
                    to_name=users[chedkedItem];
                    msgType=1;
                    chatType=2;
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();  //创建AlertDialog对象
        dialog.show();                           //显示对话框
    }
    @Override
    protected void inttData() {
        Log.e(TAG,"工具框架Fragment数据被初始化了");
        super.inttData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitChat();
    }
private void exitChat(){
    if (mChannel!=null) {
        if (mChannel.isActive()) {
            sendOffline();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChannel.close();
                }
            },1000);
        }
    }else {
    }
}
    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        if (mChannel!=null){
            if (mChannel.isActive()) {
                m15_toolfragment_txt_title.setText("聊天室已连接");

            }else {
                if (name!=null&&phone!=null) {
                    chatConfig();
                }else {
                    DialogUtil.dialog(getContext(),"请登录后再操作","前往”\"我的\"的进行登录后再来操作");
                }
            }
        }else {
            if (name!=null&&phone!=null) {
                chatConfig();
            }else {
                DialogUtil.dialog(getContext(),"请登录后再操作","前往”\"我的\"的进行登录后再来操作");
            }
        }
    }

    private void getPreferences(){
        preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        password = preferences.getString("password", null);
        address = preferences.getString("address", null);
        Log.d(TAG, "getPreferences: "+phone+name);
    }

    private void chatConfig(){
        getPreferences();
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("聊天连接中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"config/chatconfig";
        OkHttpUtils
                .get()
                .url(url)
                .addParams("port","2")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("chatconfig错误：",e.getMessage());
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("加载失败");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentActivity activity = getActivity();
                                activity.finish();
                            }
                        });
                        d.show();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson=new Gson();
                        ChatConfig chatConfig = new ChatConfig();
                        try {
                            chatConfig = gson.fromJson(response, ChatConfig.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(getContext(),"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(getContext(),"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (chatConfig!=null){
                            String ip = chatConfig.getIp();
                            String port = chatConfig.getPort();
                            if (ip.length()>0&&port.length()>0){
                                connectToServer(ip, Integer.valueOf(port));
                            }else {
                                Toast toast = Toast.makeText(getContext(), "错误：服务器的ip和端口获取为空", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }else {
                            Log.d("chatconfig：","chatconfig为空");
                            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                            d.setTitle("网络连接失败");
                            d.setMessage("");
                            d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FragmentActivity activity = getActivity();
                                    activity.finish();
                                }
                            });
                            d.show();
                        }
                    }
                });
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
                message.what = MSG_SEND;
                Bundle extra = new Bundle();
                String data="";

                // Start the client.
                mFuture = clientBootStrap.connect(mIp, mPort).sync();
                if (mFuture.isSuccess()) {
                    mChannel = mFuture.channel();
                    mSendThread = new SendThread(mChannel);
                    mSendThread.start();
                }else {

                }
                extra.putString(DATA_SEND, data);
                message.setData(extra);
                mMainHandler.sendMessage(message);
//                sendMessage(data);
                // Wait until the connection is closed.
                mChannel.closeFuture().sync();
            } catch (Exception e) {
                Message message = mMainHandler.obtainMessage();
                message.what = MSG_TITLE;
                Bundle extra = new Bundle();
                String data="";
                if (e.getMessage().indexOf("Connection refused")>=0){
                    Log.e("服务器连接：","服务器连接失败");
                    data="服务器连接拒绝(服务器可能没开，或者防火墙拦截)";
                }
                Log.e("错误：",e.getMessage());
                extra.putString(DATA_TITLE, data);
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
            mMainHandler.sendEmptyMessage(MSG_CONNECT);

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
            message.what = MSG_RECEIVE;
            Bundle extra = new Bundle();
            extra.putString(DATA_RECEIVE, data);
            message.setData(extra);
            mMainHandler.sendMessage(message);

            byt.release();  // 释放资源
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            Log.e("错误：",cause.getMessage());

            Message message = mMainHandler.obtainMessage();
            message.what = MSG_TITLE;
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
            extra.putString(DATA_TITLE, data);
            message.setData(extra);
            mMainHandler.sendMessage(message);
            ctx.close();
        }

    }
}
