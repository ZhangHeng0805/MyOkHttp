package com.zhangheng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {

    private ListView listView;
    private String[] strArr = new String[] {
            "原生OkHttp的Get和Post请求文本数据",//1
            "使用OkHttpUtil的Post提交文本数据",//2
            "使用OkHttpUtil下载大文件",//4
            "上传文件和检索文件",//5
            "请求单张图片并显示",//6
            "查询天气列表（API）",//7
            "生成二维码（API）",//8
            "新华字典查询（API）",//9
            "图书电商查询（API）",//10
            "查询文件列表并下载（自制服务器）",//10
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        listView=findViewById(R.id.list_view_1);
        setAdapter();

    }
    private void setAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Main3Activity.this, R.layout.item_list_text, strArr);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(Main3Activity.this,"点击："+i,Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (i){
                    case 0:
                        intent=new Intent(Main3Activity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent=new Intent(Main3Activity.this,Main2Activity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent=new Intent(Main3Activity.this,Main4Activity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent=new Intent(Main3Activity.this,Main5Activity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent=new Intent(Main3Activity.this,Main6Activity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent=new Intent(Main3Activity.this,Main7Activity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent=new Intent(Main3Activity.this,Main8Activity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent=new Intent(Main3Activity.this,Main9Activity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent=new Intent(Main3Activity.this,Main10Activity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent=new Intent(Main3Activity.this,Main11Activity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }
}
