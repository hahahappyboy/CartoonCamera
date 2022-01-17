# CartoonCamera(Android卡通人脸转换APP)

# 写在前面~
APP界面参考了微信小程序AI卡通秀，项目是在[MagicCamera](https://github.com/Dean1990/MagicCamera)上魔改的。博主技术菜也没有时间做优化~程序大概率会有很多bug，只能说按正常操作使用不会出问题，若遇到什么问题欢迎留言，请大家见谅！
卡通人脸转换算法的实现：[https://blog.csdn.net/iiiiiiimp/article/details/118701276](https://blog.csdn.net/iiiiiiimp/article/details/118701276)

# 效果~
## 拍照或者从相册中选图片

<img src="https://img-blog.csdnimg.cn/762823d21e164d549da0887745e565e5.gif" width="200">     <img src="https://img-blog.csdnimg.cn/229e19ad99f848c181d1ca7bf60ede4e.gif" width="200">

## 前景融合
<img src="https://img-blog.csdnimg.cn/c230e0f63de24a2b91988444c744d5c3.gif" width="200">     <img src="https://img-blog.csdnimg.cn/d095c6d03d2c4951ad039b7fd90714b3.gif" width="200">

## 背景融合
<img src="https://img-blog.csdnimg.cn/5f0e08872b7c4f16acb8cdb7621102a6.gif" width="200">     <img src="https://img-blog.csdnimg.cn/2c320914dec44e01b9dbf1f381cc8d84.gif" width="200">

## 个性签名
<img src="https://img-blog.csdnimg.cn/c2755c3ce0d741da817a0846f30db9c6.gif" width="200">  <img src="https://img-blog.csdnimg.cn/cd66cf9f32ba4297a3ad241a859387bc.gif" width="200">

# 如何运行~

 1、 下载代码

Android的代码：[https://github.com/hahahappyboy/CartoonCamera](https://github.com/hahahappyboy/CartoonCamera)
Flask后台的代码：[https://github.com/hahahappyboy/CartoonCameraFlaskWeb](https://github.com/hahahappyboy/CartoonCameraFlaskWeb)
没错！这个项目后端是用Flask写的~

![在这里插入图片描述](https://img-blog.csdnimg.cn/154dfef3df20429ebd791f1c38b648bd.png)

2、 下载模型，放在CartoonCameraFlaskWeb-main中的save_model文件夹里
模型地址链接：[https://pan.baidu.com/s/1d-gBjExzvu0rM7jOMNiKIA](https://pan.baidu.com/s/1d-gBjExzvu0rM7jOMNiKIA) 
提取码：iimp 

![在这里插入图片描述](https://img-blog.csdnimg.cn/7d8276d42b6f48c5bce6ff118577bdc8.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_12,color_FFFFFF,t_70,g_se,x_16)

3、 让手机能够访问访问Flask本地服务器
具体步骤见：[https://blog.csdn.net/iiiiiiimp/article/details/121517019](https://blog.csdn.net/iiiiiiimp/article/details/121517019)
大概流程就是：让电脑防火墙开启5000端口->用电脑连接手机热点，将热点属性设为专用->CartoonCameraFlaskWeb-main的Terminal中输入`flask run -h 0.0.0.0`
输入`flask run -h 0.0.0.0`后会显示如下结果，表示Flask程序以启动。最后一行显示的`http://192.168.179.130:5000/` 就是你Flask程序的入口地址

![在这里插入图片描述](https://img-blog.csdnimg.cn/0212fd5589274540b07b264d841e6243.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16)

之后用手机浏览器访问一下`http://192.168.179.130:5000/` 这个地址，显示这个页面就说明手机可以访问本地的Flask服务器了

<img src="https://img-blog.csdnimg.cn/de2290397acc4fdfb486557121bfd5c9.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16" width="30%">

5、 在AndroidStudio把CartoonCamera-master中的HttpContants文件中把`String URL`变量值改为`http://192.168.179.130:5000/`。

![在这里插入图片描述](https://img-blog.csdnimg.cn/937ff31682ef478b8a7e8a7024b91d8d.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16)

4、 在AndroidStudio中运行CartoonCamera-master

 <img src="https://img-blog.csdnimg.cn/81d7b8d924b34218ae5902c37ed03eb6.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16" width="30%">
 
# 注意的bug
1、 在Android代码CartoonCamera-master的CameraActivity.java类的takePhoto函数中我保存拍照的照片的方法是通过让线程睡5s来确保图片保存在了相册中，然后才将相册中的照片发送给Flask程序。
因为我搞了很久都没有解决AsyncTask通知主线程照片保存完毕的操作，于是就干脆让主线程硬等5s。如果不放心你可以改成10s，哈哈哈哈~

![在这里插入图片描述](https://img-blog.csdnimg.cn/9f936554cafa423382c0ffb01e1ecfc2.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16)

2、 Android代码的摄像头是反着的！俺也不知道为什么，调用摄像头的代码也不是俺写的~

 <img src="https://img-blog.csdnimg.cn/7bc752baf6564d2b9a6ed16132941a96.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16" width="30%">
 
 3、 Android代码拍照保存的图片是横着的耶~我也不知道为啥子，因此我在Flask中才使用cv2.rotate把照片选择回来
 
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/48ab69e9ce0c4aa1b2ddcc96cbe7c84a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16) 4、  卡通转换有时会出现这样的结果。
 
 <img src="https://img-blog.csdnimg.cn/7f769af0a0a64e5390b4c233b5954d8c.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_166" width="30%">
 
有两个原因。
一是，这种属于情况刚启动Flask程序，程序还没睡醒~点击APP中的“再来一张”按钮，重新试一次应该就没问题了。
二是你传的照片中Flask后台程序没有检测到人脸，这个你看后台程序，如果显示这个结果那多半就是了，换张图片即可。

![在这里插入图片描述](https://img-blog.csdnimg.cn/a56aaac105dc4c75b14f197960791f94.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16)

目前就想到这些bug，以后想起在补充⑧~ 
# 写在后面~

![在这里插入图片描述](https://img-blog.csdnimg.cn/fcecb6a33d9b4be8a59110b57630f77f.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAaWlpaWlpaW1w,size_20,color_FFFFFF,t_70,g_se,x_16)
