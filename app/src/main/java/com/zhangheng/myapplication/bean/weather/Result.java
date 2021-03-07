/**
  * Copyright 2021 json.cn 
  */
package com.zhangheng.myapplication.bean.weather;
import java.util.List;

/**
 * Auto-generated: 2021-03-05 10:31:22
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Result {

    private String city;
    private Realtime realtime;
    private List<Future> future;
    public void setCity(String city) {
         this.city = city;
     }
     public String getCity() {
         return city;
     }

    public void setRealtime(Realtime realtime) {
         this.realtime = realtime;
     }
     public Realtime getRealtime() {
         return realtime;
     }

    public void setFuture(List<Future> future) {
         this.future = future;
     }
     public List<Future> getFuture() {
         return future;
     }

}