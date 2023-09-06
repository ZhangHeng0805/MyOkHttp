# MyOkHttp [ZH Tools]
# 项目功能列表
* //1.原生OkHttp的Get和Post请求文本数据
* //2.使用OkHttpUtil的Post提交文本数据
* 3.文件下载工具
* //4.上传文件和检索本地文件
* 5.图片显示工具"
* 6.天气查询工具
* 7.二维码生成工具
* 8.新华字典查询工具
* 9.图书电商查询工具
* //10.查询文件列表并下载（自制服务器）
* 11.密码工具
* 12.画板工具
* 13.精选文案
* 14.每天60秒读懂世界
* //15.自制下拉刷新的ListView（测试）
* 16.电话本工具
* 17.手机扫码工具
* 18.音乐工具(不同平台的免费音乐)
* 19.影视资源(全网影视资源搜索播放)
* 20.翻译工具
* 21.全国疫情实时大数据
* 22.历史上的今天
* 23.热搜热榜单
* 24.小爱AI聊天
* 25.视频解析下载(抖音/快手/微视...)
* 26.文字转语音
* 27.AI机器人ChatGPT
> * 交流QQ群: **660875372**，群内可以有最新的软件资源，还可以一起沟通意见
> * 说明：本项目的功能列表主页界面为```Main3Activity```
> * 注意：本项目中```res/value/string.xml```文件中因涉及秘钥，所以没有上传该文件
## 本项目的Android的安装[APP下载](https://gitee.com/ZhangHeng0805/MyOkHttp/releases/download/app/ZH%20Tools_V23.04.28.apk)
* 1、使用原生的OkHttp完成请求和提交文本数据
* 2、使用Okhttputils完成大文件的下载，上传文件和分类检索本地文件，请求单张图片并显示
* 3、以及使用一些API数据接口完成天气的查询，生成二维码，新华字典查询，图书电商查询。。。
* 4、加入地图组件，使用高德地图的地图组件
* 5、使用Java爬虫获取第三数据
* 本项目使用的的服务器暂时没有上传开源
# 软件截图

> * [更多APP截图](https://mp.weixin.qq.com/s?__biz=MzIwMDQ2OTg4NA==&mid=2247484240&idx=1&sn=13e248becef09284612d29e9bd81ef6e&chksm=96fdff43a18a765502c01ac0734b54763e23096356dda62bd4068e45b1bcdf00b611d2a88fae#rd)
> * ![公众号文章](https://user-images.githubusercontent.com/74289276/219852601-1ddae8a4-5a7e-4bc2-b728-e03b2f044bab.png)

<div>
<p>
<img height="400px" alt="主页菜单" src="https://user-images.githubusercontent.com/74289276/219852813-db2c5c54-8872-4963-9ab4-375a6d877318.png"/>
<img height="400px" alt="音乐资源" src="https://user-images.githubusercontent.com/74289276/219853081-fac24a9b-02c5-4646-85e1-76cc30534305.png"/>
<img height="400px" alt="影视资源" src="https://user-images.githubusercontent.com/74289276/218678600-65d8358a-c405-49d8-add1-b5351892f90e.png"/>
<img height="400px" alt="画板" src="https://user-images.githubusercontent.com/74289276/219853002-4ae4c9ce-e1e8-49ef-83cf-7398a82bc865.png"/>
<img height="400px" alt="翻译" src="https://user-images.githubusercontent.com/74289276/198842061-c090252c-7da1-43e9-b688-7ace77e73376.jpg"/>
<img height="400px" alt="疫情数据" src="https://user-images.githubusercontent.com/74289276/198842071-b91d7d28-d62e-4d4e-b991-f475676b0d07.jpg"/>
<img height="400px" alt="短视频解析" src="https://user-images.githubusercontent.com/74289276/219853138-f855468b-4fda-41c1-b245-d89b1a53c374.png"/>
<img height="400px" alt="ChatGPT" src="https://user-images.githubusercontent.com/74289276/218676977-647c0523-4d41-4eea-b2ce-28adc4f51f27.png"/>
<img height="400px" alt="APP-QQ交流群" src="https://user-images.githubusercontent.com/74289276/219581238-a2ed4ce1-19c2-42e9-b9f8-b5a4f10fa81f.jpg"/>
</p>
</div>
# 错误异常         
## 1、Android studio 打包apk后发给别人，安装失败。失败原因显示：应用是非正式发布版本，当前设备不支持安装。
在自己手机上也无法安装，之后连接USB进行调试时可以运行，打包成APK安装就会显示下图错误：
![110292301-f5273780-8027-11eb-96aa-afe16b53cfff](https://user-images.githubusercontent.com/74289276/110292703-9910e300-8028-11eb-926c-51ab608d97f6.png)
```text
错误原因：造成该问题的原因是Android Studio 3.0会在debug apk的manifest文件application标签里自动添加 android:testOnly="true"属性。该属性导致在IDE中使用Run生成的apk在大部分手机上只能用adb install -t 来安装。这种apk在某些手机上甚至安装不了。
解决方法：在gradle.properties 文件中添加如下指令：android.injected.testOnly=false
```
