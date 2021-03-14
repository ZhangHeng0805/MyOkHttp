package com.zhangheng.myapplication.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPageUtil {

        public static void main(String[] args) {
            String url = "fsfsfs http:www.baidu.com" ;

            Set<String> rs = WebPageUtil.extractUrls(url) ;
            System.out.println( rs );
        }

        private static final Pattern pattern = initializePattern();


        public static Set<String> extractUrls(String input) {
            Set<String> extractedUrls = new HashSet<String>();


            if (input != null) {
                Matcher matcher = pattern.matcher(input);
                while (matcher.find()) {
                    String urlStr = matcher.group();
                    if (!urlStr.startsWith("http")) {
                        urlStr = "http://" + urlStr;
                    }


                    extractedUrls.add(urlStr);
                }
            }


            return extractedUrls;
        }


        /** Singleton like one time call to initialize the Pattern */
        private static Pattern initializePattern() {
            return Pattern.compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" +
                    "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" +
                    "|mil|biz|info|mobi|name|aero|jobs|museum" +
                    "|travel|[a-z]{2}))(:[\\d]{1,5})?" +
                    "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" +
                    "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                    "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" +
                    "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" +
                    "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" +
                    "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");
        }

}
