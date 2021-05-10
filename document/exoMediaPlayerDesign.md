## 播放器UI组件设计

#### 总体设计

![media_player_ui](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/media_player_ui.png?raw=true)


#### 目录结构

```
|-- AppCompatMediaPlayerView
    |-- MediaPlayerView
        |-- ContentFrameLayout
            |-- FrameWrapper(状态桢)
                |-- VideoContent(视频界面)
                    |-- TextureView
                    |-- SurfaceView
                    |-- VideoDecoderGLSurfaceView
                |-- Progress(加载状态)
                |-- Error(网络/视频源)
            |-- SubTitleView(字幕)
        |-- FrameOverLay
            |-- GestureDetectOverLay(手势探测)
                |-- AcceleratePressGestureDetector(长按加速)
                |-- BrightnessGestureDetector(亮度调节)
                |-- DoubleTapGestureDetector(双击)
                |-- LongTapGestureDetector(长按)
                |-- MultipleTapGestureDetector(多击行为,如双击,双次点击)
                |-- PlayerProgressGestureDetector(播放进度调节)
                |-- SingleTapGestureDetector(单击)
                |-- SoundGestureDetector(音量调节)
            |-- ArtworkOverLay(封面)
        |-- AnimationLayout
            |-- MediaPlayerControl
                |-- LeftPanel
                |-- TopPanel
                |-- RightPanel
                |-- BottomPanel
                    |-- PlayButton
                    |-- PauseButton
                    |-- Fastforward
                    |-- Rewind
                    |-- Shuffle
                    |-- Repeat
               |-- CenterPanel
```

* AppCompatMediaPlayerView(:library)
    主要负责与业务对接时的兼容操作,当前职责为:
    1. 对接PlayerViewWrapper,使其支持扩展的加载状态,异常显示界面.
    2. 分离异常信息,用于自定义异常信息提示

* MediaPlayerView
    主要用于组合:ContentFrameLayout(播放面板),FrameOverLay(浮层),以及MediaPlayerControl(播放器的控制面板)
    * ContentFrameLayout 用于分离实际的 PlayerView 的音频数据源与渲染视图
    * FrameOverLay(浮层) 负责手势探测,以及显示额外的全屏信息
    * AnimationLayout 用于扩展布局内元素动画.以及控制面板的布局分层.
        * MediaPlayerControl 用于组合所有的播放器功能,如播放,暂停,快进.重复模式,以及其他扩展功能等.

* FrameWrapper
    用于包装播放器组件,使其支持多桢的状态切换功能.如视频播放异常,加载状态等.
    
    * 见[playerFrameWrapper](component/playerFrameWrapper.md)

* FrameOverLay
    扩展的FrameLayout,添加 自定义视图使其分离具体的浮层逻辑.支持如:手势探测,封面等业务自定义行为
    * GestureDetectOverLay 默认提供的手势探测
    * ArtworkOverLay 添加封面浮层

    * 见[playerOverlay](component/playerOverlay.md)

* AnimationLayout
    封装动画的组件,本控件基于一个简化的约束布局实现.所以内部支持控件以约束布局方式排版.之所以不用约束布局,是因为库依赖过于沉重
    * MediaPlayerControl 播放器播放控制面板,用于分离组件功能与播放器控制面板视图部分

* MediaPlayerControlLayout
    播放器控制面板.主要管理播放组件.以及所有面板控制相关操作
    
    * 见[playerComponent](component/playerComponent.md)
    * 见[playerControlLayout](component/playerControlLayout.md)
    

### 关于代码分层设计

Base 库内,主要包含了基本层面的所有播放器 UI 需要的功能及扩展.

见下图

![image1](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/design.png?raw=true)

标志可选的,代表可组合的功能模块.每个分层,都具备其独立性,在 Base/MediaPlayerView中实现基本功能扩展.除了提供基本的实现外,都可以自行扩展.

如示例图

![image2](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/video_hide_systembar2.png?raw=true)

具体实现过程为:

```
//全局样式定制
<style name="MediaPlayer.Compat.Blue">
    <!--The different status styles, like buffering or error-->
    <item name="playerView">@style/MediaPlayerView.Normal</item>
    <item name="playerControlLayoutBase">@style/PlayerControlLayoutBase.Blue</item>
    <item name="playerOverrideStyle">true</item>
</style>

<style name="MediaPlayerView.Normal">
    <item name="control_layout_id">@layout/media_player_control_normal_layout</item>
</style>

<style name="MediaPlayerView.Mini">
    <item name="control_layout_id">@layout/media_player_control_mini_layout</item>
</style>
...

//控制面板布局修改为:
//file:media_player_control_normal_layout
<org.cz.media.player.base.controller.MediaPlayerControlLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!--居中按钮,外层最好设定为一个父容器.因为布局分层的依赖关系.内部会修改此容器 id-->
    <FrameLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        <!-- 声明依赖为居中 -->
        app:layout_control_panel="center">

        <ImageView
            android:id="@+id/playerCenterPlayButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:src="@drawable/media_player_button_play_mini"
            android:padding="24dp"
            android:background="@drawable/player_play_selector"/>

    </FrameLayout>

    <!--declare the bottom layout-->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:paddingLeft="12dp"
        android:paddingTop="6dp"
        android:paddingRight="12dp"
        android:paddingBottom="6dp"
        android:gravity="center"
        android:background="#B3000000"
        <!-- 声明依赖为底部 -->
        app:layout_control_panel="bottom"
        <!-- 声明使用的显示/隐藏动画样式 -->
        app:layout_control_animation="@style/bottom_side_animation">

        <ImageView
            android:id="@id/playerPlayButton"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:padding="12dp"
            style="?playerPlayButton"/>

        <ImageView
            android:id="@id/playerPauseButton"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:padding="12dp"
            style="?playerPauseButton"/>

        <TextView
            android:id="@id/playerPositionText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="12sp"
            android:layout_marginLeft="6dp"
            style="?playerPositionText"/>

    </LinearLayout>
</org.cz.media.player.base.controller.MediaPlayerControlLayout>
```
以上修改样式,即确定了播放器的外观.内部的默认注册组件,会自动关联以下 id 相关组件

```
<item name="playerPlayButton" type="id"/>
<item name="playerPauseButton" type="id"/>
<item name="playerRewindButton" type="id"/>
<item name="playerForwardButton" type="id"/>
<item name="playerPrevButton" type="id"/>
<item name="playerNextButton" type="id"/>
<item name="playerShuffleButton" type="id"/>
<item name="playerRepeatToggleButton" type="id"/>
<item name="playerDurationText" type="id"/>
<item name="playerPositionText" type="id"/>
<item name="playerTimeBar" type="id"/>
```

若组件不存在,则自动屏幕相关功能.注意 PlayerTimeBar 必须存在.即使是 invisible 状态
通过实现 TimeBar 接口,可以更改为自实现的TimeBar 控件


### 状态桢扩展(可选,内置于AppCompatMediaPlayerView)
已经实现的状态桢包括缓冲状态,加载异常状态.如果需要显示不同的状态.可以自行扩展 PlayerFrameWrapper.
注册相关的自定义桢

```
AppCompatMediaPlayerView#setPlayerViewWrapper
/**
 * Setting up a new player frame wrapper.
 * @param playerViewWrapper
 */
public void setPlayerViewWrapper(AbsPlayerViewWrapper playerViewWrapper)
    ...
}
```

以上可替换为自实现的 PlayerViewWrapper 子类

### 浮层扩展(可选)

```
//手势扩展浮层
MediaPlayerGestureDetectOverlay().attachToPlayerView(context,playerView)
//封面扩展浮层
GlideArtworkOverlay(url).attachToPlayerView(context,playerView)
```

### 功能模块(可选/内置)

* PlayerNavigationComponent(导航栏组件状态控制)
* PlayerFastForwardComponent(快进组件)
* PlayerRewindComponent(快退组件)
* PlayerNextComponent(下一条组件)
* PlayerPreviousComponent(上一条组件)
* PlayerPlayPauseComponent(播放暂停条组件)
* PlayerRepeatComponent(重复模式(不重复/重复1次/一直重复)条组件)
* PlayerShuffleComponent(列表随机播放组件)
* PlayerTimeBarComponent(进度条组件)


每一个都存在一个唯一 String id.根据此 id,可替换默认实现的组件功能.
也可以自行扩展需要实现的组件,或者继承修改组件.

以上是播放器组件,部分可组合的部分.通过非继承,但是组合功能的形式,支持上层业务扩展.