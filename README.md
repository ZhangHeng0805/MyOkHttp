# MyOkHttp [ZH Tools]
# 项目功能列表
* //1.原生OkHttp的Get和Post请求文本数据
* //2.使用OkHttpUtil的Post提交文本数据
* 3.使用OkHttpUtil下载大文件
* //4.上传文件和检索本地文件
* 5.请求单张图片并显示"
* 6.查询天气列表（API）
* 7.生成二维码（本地和网络API两种生成方式）
* 8.新华字典查询（API）
* 9.图书电商查询（API）
* //10.查询文件列表并下载（自制服务器）
* //11.自制地图（高德地图）
* //12.自制网络聊天室
* //13.自制聊天室(无效)
* //14.购物软件框架（自制服务器）
* //15.自制下拉刷新的ListView（测试）
* 16.手机通讯录
* 17.手机扫码
* 18.音乐资源(爬虫)
* 19.影视资源(爬虫)
* 20.翻译(爬虫)
* 21.疫情数据(爬虫)
> * 说明：本项目的功能列表主页界面为```Main3Activity```
> * 注意：本项目中```res/value/string.xml```文件中因涉及秘钥，所以没有上传该文件
## 本项目的Android的安装[APP下载](https://github.com/ZhangHeng0805/MyOkHttp/releases/download/V22.10.27/ZH.Tools_V22.10.27.apk)
* 1、使用原生的OkHttp完成请求和提交文本数据
* 2、使用Okhttputils完成大文件的下载，上传文件和分类检索本地文件，请求单张图片并显示
* 3、以及使用一些API数据接口完成天气的查询，生成二维码，新华字典查询，图书电商查询。。。
* 4、加入地图组件，使用高德地图的地图组件
* 本项目使用的的服务器是我的一个项目[File-Management-Server](https://github.com/ZhangHeng0805/File-Management-Server)
# 软件截图
<p>
<img height="400px" alt="主页菜单" src="https://user-images.githubusercontent.com/74289276/198841986-e994d978-148f-48ed-8d37-1d85fc0b8006.jpg"/>
<img height="400px" alt="音乐资源" src="https://user-images.githubusercontent.com/74289276/198842005-e62e493e-a10b-4ac7-bbc7-123a99d2949f.jpg"/>
<img height="400px" alt="影视资源" src="https://user-images.githubusercontent.com/74289276/198842033-e9031dc3-8bf5-495a-912f-ea0aecd5d90d.jpg"/>
<img height="400px" alt="翻译" src="https://user-images.githubusercontent.com/74289276/198842061-c090252c-7da1-43e9-b688-7ace77e73376.jpg"/>
<img height="400px" alt="疫情数据" src="https://user-images.githubusercontent.com/74289276/198842071-b91d7d28-d62e-4d4e-b991-f475676b0d07.jpg"/>
</p>
# 错误异常         
## 1、Android studio 打包apk后发给别人，安装失败。失败原因显示：应用是非正式发布版本，当前设备不支持安装。
在自己手机上也无法安装，之后连接USB进行调试时可以运行，打包成APK安装就会显示下图错误：
![110292301-f5273780-8027-11eb-96aa-afe16b53cfff](https://user-images.githubusercontent.com/74289276/110292703-9910e300-8028-11eb-926c-51ab608d97f6.png)
```text
错误原因：造成该问题的原因是Android Studio 3.0会在debug apk的manifest文件application标签里自动添加 android:testOnly="true"属性。该属性导致在IDE中使用Run生成的apk在大部分手机上只能用adb install -t 来安装。这种apk在某些手机上甚至安装不了。
解决方法：在gradle.properties 文件中添加如下指令：android.injected.testOnly=false
```
