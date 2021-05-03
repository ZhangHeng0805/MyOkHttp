package com.zhangheng.myapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhangheng.myapplication.Netty.ChatInfo;
import com.zhangheng.myapplication.R;

import java.util.List;

public class ChatBaseAdapter extends BaseAdapter {
    private String name,phone;
    private List<ChatInfo> data;
    private Context context;

    public ChatBaseAdapter(String phone,String name, List<ChatInfo> data, Context context) {
        this.phone=phone;
        this.name = name;
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.size();
    }

    @Override
    public long getItemId(int i) {
        return data.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ChatInfo info = data.get(i);
        Hodler hodler;
        if (view==null){
            hodler=new Hodler();
            view=View.inflate(context, R.layout.item_m15_toolfragment_chatlist,null);
            hodler.chatlist_RL_left=view.findViewById(R.id.chatlist_RL_left);
            hodler.chatlist_RL_right=view.findViewById(R.id.chatlist_RL_right);
//            hodler.chatlist_LL_left=view.findViewById(R.id.chatlist_LL_left);
//            hodler.chatlist_LL_right=view.findViewById(R.id.chatlist_LL_right);
            hodler.chatlist_txt_left_to=view.findViewById(R.id.chatlist_txt_left_to);
            hodler.chatlist_txt_right_to=view.findViewById(R.id.chatlist_txt_right_to);
            hodler.chatlist_txt_left_time=view.findViewById(R.id.chatlist_txt_left_time);
            hodler.chatlist_txt_right_time=view.findViewById(R.id.chatlist_txt_right_time);
            hodler.chatlist_txt_left_name=view.findViewById(R.id.chatlist_txt_left_name);
            hodler.chatlist_txt_right_name=view.findViewById(R.id.chatlist_txt_right_name);
            hodler.chatlist_txt_left_message=view.findViewById(R.id.chatlist_txt_left_message);
            hodler.chatlist_txt_right_message=view.findViewById(R.id.chatlist_txt_right_message);

            view.setTag(hodler);
        }else {
            hodler= (Hodler) view.getTag();
        }
        if (info.getFrom().equals(phone)){//如果消息发送者是自己
                hodler.chatlist_RL_left.setVisibility(View.GONE);
                hodler.chatlist_RL_right.setVisibility(View.VISIBLE);
                hodler.chatlist_txt_right_name.setText(info.getFrom_name());
                hodler.chatlist_txt_right_time.setText(info.getTime());
                hodler.chatlist_txt_right_message.setText(info.getMessage());
                hodler.chatlist_txt_right_to.setText("@"+info.getTo_name());
        }else {
            if (info.getChatType()==1) {//公开消息类型
                if (info.getMsgType()==1) {//用户消息
                    hodler.chatlist_RL_right.setVisibility(View.GONE);
                    hodler.chatlist_RL_left.setVisibility(View.VISIBLE);
                    hodler.chatlist_txt_left_name.setText(info.getFrom_name());
                    hodler.chatlist_txt_left_name.setTextColor(context.getColor(R.color.black_50));
                    hodler.chatlist_txt_left_time.setText(info.getTime());
                    hodler.chatlist_txt_left_message.setText(info.getMessage());
                    hodler.chatlist_txt_left_message.setTextColor(context.getColor(R.color.black));
                    hodler.chatlist_txt_left_to.setText("@"+info.getTo_name());
                }else if (info.getMsgType()==2){//服务器消息
                    hodler.chatlist_RL_right.setVisibility(View.GONE);
                    hodler.chatlist_RL_left.setVisibility(View.VISIBLE);
                    hodler.chatlist_txt_left_name.setText("来自:《"+info.getFrom_name()+"》的广播");
                    hodler.chatlist_txt_left_name.setTextColor(context.getColor(R.color.blue));
                    hodler.chatlist_txt_left_time.setText(info.getTime());
                    hodler.chatlist_txt_left_message.setText(info.getMessage());
                    hodler.chatlist_txt_left_message.setTextColor(context.getColor(R.color.red));
                    hodler.chatlist_txt_left_to.setText("@"+info.getTo_name());
                }
            }else if (info.getChatType()==2){//私聊消息类型
                if (info.getTo().equals(phone)){//私聊对象是自己
                    hodler.chatlist_RL_left.setVisibility(View.GONE);
                    hodler.chatlist_RL_right.setVisibility(View.VISIBLE);
                    hodler.chatlist_txt_right_name.setText("来自:《"+info.getFrom_name()+"》私聊");
                    hodler.chatlist_txt_right_name.setTextColor(context.getColor(R.color.red));
                    hodler.chatlist_txt_right_time.setText(info.getTime());
                    hodler.chatlist_txt_right_message.setText(info.getMessage());
                    hodler.chatlist_txt_right_message.setTextColor(context.getColor(R.color.yellow));
                    hodler.chatlist_txt_right_to.setText("@"+info.getTo_name());
                }else {
                    hodler.chatlist_RL_right.setVisibility(View.GONE);
                    hodler.chatlist_RL_left.setVisibility(View.GONE);
                }

            }

        }

        return view;
    }
    class Hodler{
        private RelativeLayout chatlist_RL_left,chatlist_RL_right;
//        private LinearLayout chatlist_LL_left,chatlist_LL_right;
        private TextView chatlist_txt_left_name,chatlist_txt_left_time,chatlist_txt_left_message,chatlist_txt_left_to
                ,chatlist_txt_right_time,chatlist_txt_right_name,chatlist_txt_right_message,chatlist_txt_right_to;

    }
}
