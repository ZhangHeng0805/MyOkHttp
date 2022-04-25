package com.zhangheng.myapplication.util;

import java.util.Random;

/**
 * @program: My Application
 * @description: 随机操作
 * @author: 张恒
 * @create: 2022-01-14 14:17
 */
public class RandomrUtil {
    /**
     * 大写字母字符数组
     */
    public static final char[] CAPITAL_LETTER=new char[]{
      'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };
    /**
     * 小写字母字符数组
     */
    public static final char[] LOWERCASE_LETTER=new char[]{
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
    };
    /**
     * 英文符号字符数组
     * ~!@#$%^&*()_-+=[]{}:|<>./?
     */
    public static final char[] SYMBOLS_EN=new char[]{
      '~','!','@','#','$','%','^','&','*','(',')','_','-','+','=','[',']','{','}',':','|','<','>',
            '.','/','?'
    };
    /**
     * 英文符号字符串
     */
    public static final String SYMBOLS_EN_STR="~!@#$%^&_*-+=()[]{}<>|./?";
    /**
     * 数字字符数组
     */
    private static final char[] NUMBER=new char[]{
            '1','2','3','4','5','6','7','8','9','0',
    };

    public static void main(String[] args) {
        String passWord = createPassWord(16, "0124");
        System.out.println(passWord);
    }

    /**
     * 生成字母数字特殊符号不同长度的密码组合
     * @param length 密码长度 （长度尽量在20位以内）
     * @param type [0:纯数字(0~9)]
     *             [1:小写字母(a~z)]
     *             [2:大写字母(A~Z)]
     *             [3:英文符号]
     *             [例："123"(小写字母+大写字母+英文符号)]
     * @return
     */
    public static String createPassWord(int length,String type){
        StringBuffer password=new StringBuffer();
        char[] index = type.toCharArray();
        char [][] list={NUMBER,LOWERCASE_LETTER,CAPITAL_LETTER,SYMBOLS_EN_STR.toCharArray()};
        while (password.length()<length){
            char i = index[createRandom(0, index.length)];
            char[] chars;
            try {
                chars = list[Integer.parseInt(String.valueOf(i))];
            }catch (ArrayIndexOutOfBoundsException e1){
                System.err.println("参数type=\""+type+"\"中的数字错误(组合错误)");
                throw e1;
            }catch (NumberFormatException e2){
                System.err.println("参数type=\""+type+"\"中的格式错误(字符错误)");
                throw e2;
            }
            char c = chars[createRandom(0, chars.length)];
            password.append(c);
        }

        return password.toString();
    }

    /**
     * 生成自定范围的随机数[min,max]
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return
     */
    public static int createRandom(int min,int max){
        if (min>max){
            int tem=max;
            max=min;
            min=tem;
        }
        Random random = new Random();
        int i=random.nextInt(max)%(max-min+1) + min;
        return i;
    }

    /**
     * 生成1~max范围的书籍数[1,max]
     * @param max 最大值（包含）
     * @return
     */
    public static int createRandom(int max){
        Random random = new Random();
        int i=random.nextInt(max)+1;
        return i;
    }
}
