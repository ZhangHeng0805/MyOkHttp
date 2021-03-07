/**
  * Copyright 2021 json.cn 
  */
package com.zhangheng.myapplication.bean.books;
import java.util.List;

/**
 * Auto-generated: 2021-03-06 12:35:44
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class BooksRootBean {

    private String resultcode;
    private String reason;
    private List<Result> result;
    public void setResultcode(String resultcode) {
         this.resultcode = resultcode;
     }
     public String getResultcode() {
         return resultcode;
     }

    public void setReason(String reason) {
         this.reason = reason;
     }
     public String getReason() {
         return reason;
     }

    public void setResult(List<Result> result) {
         this.result = result;
     }
     public List<Result> getResult() {
         return result;
     }

}