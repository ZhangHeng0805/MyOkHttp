/**
  * Copyright 2021 json.cn 
  */
package com.zhangheng.myapplication.bean.books.bookslist;
import java.util.List;

/**
 * Auto-generated: 2021-03-06 13:49:40
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Result {

    private List<Data> data;
    private String totalNum;
    private int pn;
    private String rn;
    public void setData(List<Data> data) {
         this.data = data;
     }
     public List<Data> getData() {
         return data;
     }

    public void setTotalNum(String totalNum) {
         this.totalNum = totalNum;
     }
     public String getTotalNum() {
         return totalNum;
     }

    public void setPn(int pn) {
         this.pn = pn;
     }
     public int getPn() {
         return pn;
     }

    public void setRn(String rn) {
         this.rn = rn;
     }
     public String getRn() {
         return rn;
     }

}