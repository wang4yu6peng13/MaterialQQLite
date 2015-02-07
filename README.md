# MaterialQQLite
MaterialQQ Lite是根据开源项目MingQQ修改得来的，主要做了界面改进，功能上基本和MingQQ一致。<br>
现将代码开源，期待感兴趣的开发人员完善这个项目。
##重要说明（摘录）
为了满足Material Design控的需求，这个项目诞生了。幸运的是看到MingQQ开源，给了大家一种新的可能。<br>
和MingQQ相比，MaterialQQ Lite除了名字有点长外，有以下新的特性：<br>
1、界面Material化。（由于本app主要面向Android 4.x的用户，采用了大量Material库，可能和Android 5.x的效果有所区别，目前本人开发条件有限，无法做到原滋原味的Android 5.x效果） <br>
2、可选主题颜色，支持顶栏透明。 <br>
3、提高安全性。原MingQQ的用户数据储存在本地sdcard目录上，使得安全性极低。本app将数据储存在系统目录下，不root是读不到的。 <br>
4、聊天界面可以从左至右滑动返回。（与表情框会冲突，想滑到前面的表情页，需要按住表情页向左滑再向右就可以了，大家挑战一下） <br>
5、聊天框里的文字长按可以选择、复制 <br>
缺点： <br>
1、所有缺点和MingQQ是一样的。 <br>
2、目前不建议大家将本app作为QQ的替代，因为只有简单的文字聊天和表情功能，挂在后台时间长了可能会掉线。 <br>
3、采用WebQQ协议，登录不成功可能是因为开启了设备锁，而且和PCQQ不能同时在线。（可以和手机QQ一起挂，但是想想这么做有些奇怪） <br>
4、没有找到QQ空间的相关接口，暂时没有空间功能。 <br>
期许： <br>
1、有崩溃或其他使用异常，可以联系开发者。 <br>
2、本app同样开源，希望更多的开发者可以改善这个app，使它更完善。 <br>
3、但愿不要被官方封掉。能带动官方app做Material化的话，我们的最终目的就达到了。 <br>
声明：<br>
程序所使用的协议和部分资源图片的版权依法为腾迅公司所有，只供个人学习研究使用，请勿用于非法用途，否则后果自负。<br> 
雨棚 <br>
2015.2.5<br>
##记录（2015.02.07）
收录的暂时未解决的问题和建议：<br>
* Unable to resume activity {com.wyp.materialqqlite/com.wyp.materialqqlite.ui.MainActivity}: java.lang.NullPointerException<br>
* java.lang.RuntimeException: Error receiving broadcast Intent { act=android.intent.action.CLOSE_SYSTEM_DIALOGS flg=0x50000010 (has extras) } in com.wyp.materialqqlite.HomeWatcher$InnerRecevier@13c65917<br>
* 希望Tabs向上滚动隐藏<br>
* 希望聊天界面边缘滑动返回<br>
* 头像不够清晰<br>
* 希望可以设置回车发消息<br>
* 希望设置常驻通知栏开关<br>
* 部分帐号登陆会失败<br>
* 从其他界面回到主界面，总会跳到第一个Tab<br>
* 希望添加搜索QQ好友功能<br>
* 希望可以整理群消息<br>
* 抽屉的设置没有必要<br>
