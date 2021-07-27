# DynamicLoad
动态加载技术：

核心是想是动态调用外部的dex文件，极端情况下，Android APK自身带有的dex文件只是一个程序的入口（或者是空壳），所有的功能都是通过服务器下载最新的dex文件完成

应用在运行的适合去加载一些本地的可执行文件实现一些特定的功能

动态加载那些文件：

动态加载so库

动态加载dex ,jar,apk文件

简单实现方式:
1、获取包的管理器，获取资源包信息类，找到资源对象
2、通过反射获取AssetManager对象以及addAssetPath方法，去添加传进来的皮肤包路径
3、创建Resources对象，得到资源包里面的资源对象
4、通过Resources获取到资源id和外部传入的资源id对比，去加载资源
5、在页面Activity启动的时候，去实现LayoutInflater.Factory2，收集需要换肤的控件
6、在LayoutInflater.Factory2实现类中，调用onCreateView去实例化控件，并且收集需要换肤的控件
7、遍历所有控件的属性，拿到控件，控件资源id,资源id类型，资源id名字，然后去和通过Resources获取的资源id做对比，去加载资源包里面的资源
具体步骤参考CSDN链接[]
