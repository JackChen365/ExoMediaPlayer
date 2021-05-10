## 播放器上层UI 设计

* 横竖屏不同 UI 考虑.

![](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/ui/%E8%85%BE%E8%AE%AF%E8%A7%86%E9%A2%91%E7%95%8C%E9%9D%A2%E4%BA%A4%E4%BA%92/WechatIMG34.jpeg?raw=true)

![](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/ui/%E8%85%BE%E8%AE%AF%E8%A7%86%E9%A2%91%E7%95%8C%E9%9D%A2%E4%BA%A4%E4%BA%92/WechatIMG38.jpeg?raw=true)


* 异常界面嵌入
![](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/ui/%E8%85%BE%E8%AE%AF%E8%A7%86%E9%A2%91%E7%95%8C%E9%9D%A2%E4%BA%A4%E4%BA%92/WechatIMG35.jpeg?raw=true)


* 引导元素

![](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/ui/%E8%85%BE%E8%AE%AF%E8%A7%86%E9%A2%91%E7%95%8C%E9%9D%A2%E4%BA%A4%E4%BA%92/guide.jpeg?raw=true)

* 元素分布

左侧,底部,右侧,顶部.


首先一级固定面板.如播放进度等.
其次浮层面板.如点击倍速,选集后的临时弹出面板.
阻塞式面板,如网络异常.

1. 一级固定面板,主要展示播放操作.播放界停.但是横纵屏展示元素不同.
2. 左侧,右侧,可能需要动态添加元素.或者根据不同业务场景定制

3. 基本引导/浮层面板,如选集倍速的.应该由外围根据业务自由添加.


### 前期基本设计

![](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/player_control_layout.png?raw=true)

元素控制排版.主要为了协调不同元素所占的空间.因为如果仅控制底部的.那么后面添加的控件因为在不同层,可能会导致元素排版覆盖等问题.


每一个面板,会根据需求,做默认实现.可以根据扩展样式,定制修改样式.也会通过占位等形势,让外部可以有机会复写.



* 面板动画控制
暴露 xml 样式,由外部配置.












