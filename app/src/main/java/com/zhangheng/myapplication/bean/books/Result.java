/**
  * Copyright 2021 json.cn 
  */
package com.zhangheng.myapplication.bean.books;

/**
 * Auto-generated: 2021-03-06 12:35:44
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Result {

    private String id;
    private String catalog;
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setCatalog(String catalog) {
         this.catalog = catalog;
     }
     public String getCatalog() {
         return catalog;
     }

    @Override
    public String toString() {
        return catalog;
    }
}