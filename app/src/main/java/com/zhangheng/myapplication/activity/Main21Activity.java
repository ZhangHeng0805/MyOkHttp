package com.zhangheng.myapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhangheng.myapplication.R;
import com.zhangheng.myapplication.reptile.EpidemicSituationAPI;
import com.zhangheng.myapplication.util.DialogUtil;
import com.zhangheng.myapplication.util.SystemUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import okhttp3.Call;

public class Main21Activity extends AppCompatActivity {

    private final String Tag = this.getClass().getSimpleName();

    private EditText m21_et_filter;
    private Button m21_btn_search;
    private RadioGroup m21_RG_check;
    private RadioButton m21_rb_risk, m21_rb_summary, m21_rb_provincial;
    private ListView m21_lv_result;

    private Map<String, String[]> covid19_data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main21);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        covid19_data.clear();
    }

    private void initView() {
        m21_et_filter = findViewById(R.id.m21_et_filter);
        m21_btn_search = findViewById(R.id.m21_btn_search);
        m21_RG_check = findViewById(R.id.m21_RG_check);
        m21_rb_risk = findViewById(R.id.m21_rb_risk);
        m21_rb_summary = findViewById(R.id.m21_rb_summary);
        m21_rb_provincial = findViewById(R.id.m21_rb_provincial);
        m21_lv_result = findViewById(R.id.m21_lv_result);

        listenerView();
    }

    private void listenerView() {
        //搜索
        m21_btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.closeInput(Main21Activity.this);
                String filter = m21_et_filter.getText().toString();
                if (covid19_data.isEmpty()) {
                    getData(filter);
                } else {
                    setAdapter(filter);
                }

            }
        });
        m21_RG_check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.m21_rb_risk://全国风险地区数据
                        m21_et_filter.setEnabled(true);
                        break;
                    case R.id.m21_rb_summary://全国概要数据
                        m21_et_filter.setEnabled(false);
                        m21_et_filter.setText("");
                        break;
                    case R.id.m21_rb_provincial://各省详细数据
                        m21_et_filter.setEnabled(true);
                        break;
                }
            }
        });
    }

    private void getData(@Nullable String filter) {
        DialogUtil dialogUtil = new DialogUtil(Main21Activity.this);

        int ceil = (int) Math.ceil(1e5 * Math.random());
        String Url = "https://voice.baidu.com/api/newpneumonia?from=page&callback=jsonp_" + new Date().getTime() + "_" + ceil;

        dialogUtil.createProgressDialog("查询中。。。");

        OkHttpUtils.get()
                .url(Url)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(Tag, "请求错误：" + e.toString());
                dialogUtil.closeProgressDialog();
                DialogUtil.dialog(Main21Activity.this, "查询错误", e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Document doc = Jsoup.parse(response);
                    String json = doc.getElementsByTag("body").text();
                    json = StrUtil.sub(json, json.indexOf("(") + 1, -1);
                    json = json.replace("\\/", "/");
                    covid19_data = EpidemicSituationAPI.getCOVID19_Str(json);

                    setAdapter(filter);
                } catch (Exception e) {
                    Log.e(Tag, "结果解析错误哦：" + e.toString());
                    DialogUtil.dialog(Main21Activity.this, "数据解析错误", e.getMessage());
                } finally {
                    dialogUtil.closeProgressDialog();
                }

            }
        });

    }

    private void setAdapter(@Nullable String filter) {
        if (!covid19_data.isEmpty()) {
            int checkedRadioButtonId = m21_RG_check.getCheckedRadioButtonId();
            String[] arr;
            switch (checkedRadioButtonId) {
                case R.id.m21_rb_risk://全国风险地区数据
                    arr = covid19_data.get("risk");
                    break;
                case R.id.m21_rb_summary://全国概要数据
                    arr = covid19_data.get("summary");
                    break;
                case R.id.m21_rb_provincial://各省详细数据
                    arr = covid19_data.get("provincial");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + checkedRadioButtonId);
            }
            if (!StrUtil.isEmpty(filter)) {
                String[] split;
                if (filter.indexOf(",") > -1) {
                    split = filter.split(",");
                } else if (filter.indexOf("，") > -1) {
                    split = filter.split("，");
                } else {
                    split = filter.split("省");
                }
                arr = filter(arr, split);
            }
            List<String> list = organizeData(arr);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    Main21Activity.this, R.layout.item_list_text2, list);
            m21_lv_result.setAdapter(adapter);

        } else {
            DialogUtil.dialog(Main21Activity.this, "数据解析错误", "数据为空");
        }
    }

    /**
     * 从数组中筛选
     *
     * @param data
     * @param filter
     * @return
     */
    public static String[] filter(String[] data, String[] filter) {
        List<String> list = new ArrayList<>();
        for (String s : data) {
            for (String a : filter) {
                if (s.indexOf(a) > -1) {
                    list.add(s);
                }
            }
//            System.out.println(s);
        }
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            arr[i] = s;
//            System.out.println(s);
        }
        return arr;
    }

    /**
     * 将数据按照一定字数分组
     *
     * @param datas
     * @return
     */
    private List<String> organizeData(String[] datas) {
        StringBuilder sb = new StringBuilder("");
        List<String> list = new ArrayList<>();

        for (String s : datas) {
            if (sb.length() < 5000 - s.length()) {
                sb.append(s + "\n");
            } else {
                list.add(sb.toString());
                sb.delete(0, sb.length());
                sb.append(s + "\n");
            }
        }
        list.add(sb.toString());

        return list;
    }


}
