# MyOkHttp

## 项目参考依赖[**okhttputils**](https://github.com/hongyangAndroid/okhttputils)和原生的[okhttp](https://github.com/square/okhttp)
## 本项目的Android的安装[APP下载](https://github.com/ZhangHeng0805/MyOkHttp/releases/download/V1-3.26/MyOkHttp_V1-3.26.apk)
* 1、使用原生的OkHttp完成请求和提交文本数据
* 2、使用Okhttputils完成大文件的下载，上传文件和分类检索本地文件，请求单张图片并显示
* 3、以及使用一些API数据接口完成天气的查询，生成二维码，新华字典查询，图书电商查询。。。
* 4、加入地图组件，使用高德地图的地图组件
* 本项目文件上传的服务器是我的一个项目[File-Management-Server](https://github.com/ZhangHeng0805/File-Management-Server)
# 错误异常         
## 1、Android studio 打包apk后发给别人，安装失败。失败原因显示：应用是非正式发布版本，当前设备不支持安装。
在自己手机上也无法安装，之后连接USB进行调试时可以运行，打包成APK安装就会显示下图错误：
![110292301-f5273780-8027-11eb-96aa-afe16b53cfff](https://user-images.githubusercontent.com/74289276/110292703-9910e300-8028-11eb-926c-51ab608d97f6.png)
```text
错误原因：造成该问题的原因是Android Studio 3.0会在debug apk的manifest文件application标签里自动添加 android:testOnly="true"属性。该属性导致在IDE中使用Run生成的apk在大部分手机上只能用adb install -t 来安装。这种apk在某些手机上甚至安装不了。
解决方法：在gradle.properties 文件中添加如下指令：android.injected.testOnly=false
```
