package com.zhangheng.myapplication.reptile;

import com.zhangheng.log.Log;
import com.zhangheng.util.TimeUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 疫情API
 *
 * @author 张恒
 * @program: reptile
 * @email zhangheng.0805@qq.com
 * @date 2022-09-30 07:29
 */
public class EpidemicSituationAPI {
    /**
     * 获取全球实时新冠疫情数据
     *
     * @return json json数据
     */
    public static String getCOVID19_JSON() throws IOException {
        String json = "";
        try {
            int ceil = (int) Math.ceil(1e5 * Math.random());
            String Url = "https://voice.baidu.com/api/newpneumonia?from=page&callback=jsonp_" + new Date().getTime() + "_" + ceil;
            Document doc = Jsoup.connect(Url).get();
            json = doc.getElementsByTag("body").text();
            json = StrUtil.sub(json, json.indexOf("(") + 1, -1);
            json = json.replace("\\/", "/");
        } catch (IOException e) {
            Log.error("第三方新冠疫情[https://voice.baidu.com/api/newpneumonia]数据获取失败:" + e.getMessage());
        }
        return json;
    }

    /**
     * 解析新冠疫情JSON数据
     * 获取各省级疫情信息
     *
     * @param json getCOVID19_JSON()
     * @return {upateTime:更新时间,caseList:[{area:地区,updateTime:更新时间,confirmed:累计确诊,died:累计死亡,
     * crued:累计治愈,confirmedRelative:新增确诊(相对前一天),diedRelative:新增死亡(相对前一天),curedRelative:新增治愈(相对前一天),
     * asymptomaticRelative:新增无症状(相对前一天),asymptomaticLocalRelative:新增本地无症状(相对前一天),asymptomatic:累计无症状,
     * nativeRelative:新增本地确诊(相对前一天),curConfirm:现有确诊,curConfirmRelative:相对现有确诊(相对前一天),
     * noNativeRelativeDays:没有新增病历的天数,overseasInputRelative:新增境外输入(相对前一天),moreUrl:详情链接,totalNum:中高风险地区数}]}
     */
    public static List<Map<String, String>> getCOVID19_Provincial_Data(String json) throws IOException {
        List<Map<String, String>> case_list = new ArrayList<>();
        if (!StrUtil.isEmptyIfStr(json)) {
            JSONObject obj = JSONUtil.parseObj(json);
            Integer status = obj.getInt("status");
            if (status.equals(0)) {
                JSONObject data = obj.getJSONObject("data");
                //更新时间
//                String upateTime = data.getStr("upateTime");
//                map.put("upateTime", upateTime);
                //全国各地疫情数据
                JSONArray caseList = data.getJSONArray("caseList");
                for (Object c : caseList) {
                    Map<String, String> cMap = new HashMap<>();
                    JSONObject jc = JSONUtil.parseObj(c);
                    //相对时间
//                    String relativeTime = jc.getStr("relativeTime");
//                    Date date = com.zhangheng.weixin1.utils.TimeUtil.fromUnixToTime(relativeTime);
//                    cMap.put("relativeTime", TimeUtil.toTime(date));
                    //更新时间
                    String updateTime = jc.getStr("updateTime");
                    Date date = TimeUtil.UnixToDate(updateTime);
                    cMap.put("updateTime", TimeUtil.toTime(date));
                    //省份
                    String area = jc.getStr("area");
                    cMap.put("area", area);
                    //累计确诊
                    String confirmed = jc.getStr("confirmed");
                    cMap.put("confirmed", confirmed);
                    //累计死亡
                    String died = jc.getStr("died");
                    cMap.put("died", died);
                    //累计治愈
                    String crued = jc.getStr("crued");
                    cMap.put("crued", crued);
                    //新增确诊(相对前一天)
                    String confirmedRelative = jc.getStr("confirmedRelative");
                    cMap.put("confirmedRelative", confirmedRelative);
                    //新增死亡(相对前一天)
                    String diedRelative = jc.getStr("diedRelative");
                    cMap.put("diedRelative", diedRelative);
                    //新增治愈(相对前一天)
                    String curedRelative = jc.getStr("curedRelative");
                    cMap.put("curedRelative", curedRelative);
                    //新增无症状(相对前一天)
                    String asymptomaticRelative = jc.getStr("asymptomaticRelative");
                    cMap.put("asymptomaticRelative", asymptomaticRelative);
                    //新增本地无症状(相对前一天)
                    String asymptomaticLocalRelative = jc.getStr("asymptomaticLocalRelative");
                    cMap.put("asymptomaticLocalRelative", asymptomaticLocalRelative);
                    //累计无症状
                    String asymptomatic = jc.getStr("asymptomatic");
                    cMap.put("asymptomatic", asymptomatic);
                    //新增本地确诊(相对前一天)
                    String nativeRelative = jc.getStr("nativeRelative");
                    cMap.put("nativeRelative", nativeRelative);
                    //现有确诊
                    String curConfirm = jc.getStr("curConfirm");
                    cMap.put("curConfirm", curConfirm);
                    //相对现有确诊(相对前一天)
                    String curConfirmRelative = jc.getStr("curConfirmRelative");
                    cMap.put("curConfirmRelative", curConfirmRelative);
                    //没有新增病历的天数
                    String noNativeRelativeDays = jc.getStr("noNativeRelativeDays");
                    cMap.put("noNativeRelativeDays", StrUtil.isEmptyIfStr(noNativeRelativeDays) ? "" : noNativeRelativeDays);
                    //新增境外输入(相对前一天)
                    String overseasInputRelative = jc.getStr("overseasInputRelative");
                    cMap.put("overseasInputRelative", StrUtil.isEmptyIfStr(overseasInputRelative) ? "" : overseasInputRelative);

                    /*风险地区*/
                    JSONObject dj = jc.getJSONObject("dangerousAreas");
                    //详情链接
                    String moreUrl = dj.getStr("moreUrl");
                    cMap.put("moreUrl", moreUrl);
                    //中高风险地区数
                    Integer totalNum = dj.getInt("totalNum");
                    cMap.put("totalNum", totalNum.toString());

                    case_list.add(cMap);
                }
//                map.put("caseList", case_list);
            } else {
                Log.error("第三方新冠疫情[https://voice.baidu.com/api/newpneumonia]省级数据获取错误:" + obj.getStr("msg"));
            }
        }
        return case_list;
    }

    /**
     * 解析新冠疫情JSON数据
     * 获取疫情概要信息
     *
     * @param json getCOVID19_JSON()
     * @return {upateTime:更新时间,summaryDataIn:{confirmed:累计确诊,cured:累计治愈,died:累计死亡},
     * dataRelativeYestday:{confirmed:与昨天相比的累计确诊,cured:昨天相比的累计治愈,died:昨天相比的累计死亡}}
     */
    public static Map<String, Map<String, Integer>> getCOVID19_Resumes_Data(String json) throws IOException {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        if (!StrUtil.isEmptyIfStr(json)) {
            JSONObject obj = JSONUtil.parseObj(json);
            Integer status = obj.getInt("status");
            if (status.equals(0)) {
                JSONObject data = obj.getJSONObject("data");
//                //更新时间
//                String upateTime = data.getStr("upateTime");
//                map.put("upateTime", upateTime);

                /*当前国内的疫情概要数据*/
                JSONObject summaryDataIn = data.getJSONObject("summaryDataIn");
                Map<String, Integer> DataInMap = new HashMap<>();
                //累计确诊
                Integer confirmed = summaryDataIn.getInt("confirmed");
                DataInMap.put("confirmed", confirmed);
                //累计治愈
                Integer cured = summaryDataIn.getInt("cured");
                DataInMap.put("cured", cured);
                //累计死亡
                Integer died = summaryDataIn.getInt("died");
                DataInMap.put("died", died);
                map.put("summaryDataIn", DataInMap);

                /*当前和昨天的国内疫情对比的概要数据*/
                JSONObject summaryDataRelativeYestday = data.getJSONObject("summaryDataRelativeYestday");
                Map<String, Integer> DataRelativeYestdayMap = new HashMap<>();
                //与昨天相比的累计确诊
                Integer confirmed1 = summaryDataRelativeYestday.getInt("confirmed");
                DataRelativeYestdayMap.put("confirmed", confirmed1);
                //与昨天相比的累计治愈
                Integer cured1 = summaryDataRelativeYestday.getInt("cured");
                DataRelativeYestdayMap.put("cured", cured1);
                //与昨天相比的累计死亡
                Integer died1 = summaryDataRelativeYestday.getInt("died");
                DataRelativeYestdayMap.put("died", died1);
                map.put("dataRelativeYestday", DataRelativeYestdayMap);

            } else {
                Log.error("第三方新冠疫情[https://voice.baidu.com/api/newpneumonia]概要数据获取错误:" + obj.getStr("msg"));
            }
        }
        return map;
    }

    /**
     * 疫情风险地区
     *
     * @param json getCOVID19_JSON()
     * @return [{
     * address:地区,nativeRelative:新增本土,asymptomaticRelative:新增无症状,
     * highLevelNum:高风险地区数,midLevelNum:中风险地区数,dangerousAreas:风险地区信息
     * }]
     */
    public static List<Map<String, String>> getCOVID19_Risk_Data(String json) throws IOException {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            JSONArray jsonArray = JSONUtil.parseObj(json).getJSONObject("data").getJSONObject("resumes").getJSONObject("china").getJSONArray("list");
            for (Object o : jsonArray) {
                JSONObject obj = JSONUtil.parseObj(o);
                Map<String, String> map = new HashMap<>();
                //地区
                String address = obj.getStr("province") + "-" + obj.getStr("area");
                map.put("address", address);
                //新增本土
                String nativeRelative = obj.getStr("nativeRelative");
                map.put("nativeRelative", nativeRelative);
                //新增无症状
                String asymptomaticRelative = obj.getStr("asymptomaticRelative");
                map.put("asymptomaticRelative", asymptomaticRelative);
                /*风险地区*/
                JSONObject dangerousAreas = obj.getJSONObject("dangerousAreas");
                //高风险地区数
                String highLevelNum = dangerousAreas.getStr("highLevelNum");
                map.put("highLevelNum", highLevelNum);
                //中风险地区数
                String midLevelNum = dangerousAreas.getStr("midLevelNum");
                map.put("midLevelNum", midLevelNum);
                list.add(map);
                //风险地区信息
                JSONArray subList = dangerousAreas.getJSONArray("subList");
                StringBuilder sb = new StringBuilder("");
                if (subList.size() > 0) {
                    for (Object sub : subList) {
                        JSONObject jsonObject = JSONUtil.parseObj(sub);
                        sb.append("- ["+jsonObject.getStr("level")+"]")
                                .append(" "+jsonObject.getStr("area"))
                                .append(" <新增:"+jsonObject.getInt("isNew")+">\n");
                    }
                }
                map.put("dangerousAreas",sb.toString());
//                System.out.println(map);
            }
        } catch (Exception e) {
            Log.error("第三方新冠疫情[https://voice.baidu.com/api/newpneumonia]风险地区数据解析错误:" + e.getMessage());
        }
        return list;
    }

    /**
     * 将数字转换为带正负符号的字符串
     *
     * @param num
     * @return
     */
    public static String numStyle(Integer num) {
        return num > 0 ? "+" + num : num.toString();
    }

    /**
     * 将疫情数据字符串格式化
     *
     * @return summary:全国概要数据,
     * provincial:各省详细数据
     * risk:风险地区数据
     */
    public static Map<String, String[]> getCOVID19_Str(String json) throws IOException {
//        String json = getCOVID19_JSON();
        Map<String, String[]> map = new HashMap<>();
        //全国概要数据
        Map<String, Map<String, Integer>> resumes_data = getCOVID19_Resumes_Data(json);
        //各省详细数据
        List<Map<String, String>> provincial_data = getCOVID19_Provincial_Data(json);
        //风险地区数据
        List<Map<String, String>> risk_data = getCOVID19_Risk_Data(json);
        /*全国概要数据*/
        String[] summary = new String[1];
        Map<String, Integer> map1 = resumes_data.get("summaryDataIn");
        Map<String, Integer> map2 = resumes_data.get("dataRelativeYestday");
        String upateTime = JSONUtil.parseObj(json).getJSONObject("data").getStr("upateTime");
        StringBuilder sb = new StringBuilder();
        sb.append("【今日全国疫情概要数据】\n")
                .append("* 累计确诊:" + map1.get("confirmed") + " (较昨日" + numStyle(map2.get("confirmed")) + ")\n")
                .append("* 累计治愈:" + map1.get("cured") + " (较昨日" + numStyle(map2.get("cured")) + ")\n")
                .append("* 累计死亡:" + map1.get("died") + " (较昨日" + numStyle(map2.get("died")) + ")\n")
                .append("* 更新时间:" + upateTime )
//                .append("\n")
        ;
        summary[0] = sb.toString();
        map.put("summary", summary);
        /*各省详细数据*/
        String[] provincial = new String[provincial_data.size()];
        int i = 0;
        for (Map<String, String> c : provincial_data) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("\t#" + c.get("area") + "：\n")
                    .append("\t\t累计确诊[" + c.get("confirmed") + "]\n")
                    .append("\t\t累计治愈[" + c.get("crued") + "]\n")
                    .append("\t\t累计死亡[" + c.get("died") + "]\n")
                    .append("\t\t新增确诊[" + c.get("confirmedRelative") + "]\n")
                    .append("\t\t新增治愈[" + c.get("curedRelative") + "]\n")
                    .append("\t\t新增死亡[" + c.get("diedRelative") + "]\n")
                    .append("\t\t新增无症状[" + c.get("asymptomaticRelative") + "]\n")
                    .append("\t\t新增本地无症状[" + c.get("asymptomaticLocalRelative") + "]\n")
                    .append("\t\t累计无症状[" + c.get("asymptomatic") + "]\n")
                    .append("\t\t新增本地确诊[" + c.get("nativeRelative") + "]\n")
                    .append("\t\t现有确诊[" + c.get("curConfirm") + "]\n")
                    .append("\t\t与昨天现有确诊相差[" + numStyle(Integer.valueOf(c.get("curConfirmRelative"))) + "]\n")
                    .append("\t\t无新增情况[" + c.get("noNativeRelativeDays") + "]\n")
                    .append("\t\t新增境外输入[" + c.get("overseasInputRelative") + "]\n")
                    .append("\t\t中高风险地区[" + c.get("totalNum") + "]\n")
                    .append("\t\t更新时间[" + c.get("updateTime") + "]\n")
//                .append("\n")
            ;
            provincial[i++] = sb2.toString();
        }
        map.put("provincial", provincial);
        /*风险地区数据*/
        String[] risk = new String[risk_data.size()];
        i = 0;
        for (Map<String, String> rd : risk_data) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("#" + rd.get("address") + "：\n")
                    .append("新增本土确诊[" + rd.get("nativeRelative") + "]\t")
                    .append("新增无症状感染[" + rd.get("asymptomaticRelative") + "]\n")
                    .append("高风险地区数[" + rd.get("highLevelNum") + "]\t")
                    .append("中风险地区数[" + rd.get("midLevelNum") + "]\n")
                    .append(""+rd.get("dangerousAreas"));
            risk[i++] = sb2.toString();
//            System.out.println(sb2.toString());
        }
        map.put("risk", risk);
        return map;
    }
}
