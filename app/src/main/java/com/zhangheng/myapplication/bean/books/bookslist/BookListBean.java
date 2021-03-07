/**
  * Copyright 2021 json.cn 
  */
package com.zhangheng.myapplication.bean.books.bookslist;

/**
 * Auto-generated: 2021-03-06 13:49:40
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class BookListBean {

    private String resultcode;
    private String reason;
    private Result result;
    private int error_code;
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

    public void setResult(Result result) {
         this.result = result;
     }
     public Result getResult() {
         return result;
     }

    public void setError_code(int error_code) {
         this.error_code = error_code;
     }
     public int getError_code() {
         return error_code;
     }

}