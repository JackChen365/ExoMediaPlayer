## README


### 依赖库

```
//待上传
```

### 演示程序

*[下载](apk/app-debug.apk)*

### 编译工具及软件版本

```
application's build.gradle
repositories {
    [...]
    //待上传
    [...]
}


//library's build.gradle
android {
    compileSdkVersion 30
    buildToolsVersion "30.0.0"

    defaultConfig {
        minSdkVersion 19
        targetSdkVersion 30
        //支持矢量动画(加载缓冲动画)
        vectorDrawables.useSupportLibrary = true
        [...]
    }
    //Because the exoplayer-2.11.7 requeire the version 1.8 of the Java
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

}

dependencies {
    //The highest version of appcompat!!!
    implementation 'androidx.appcompat:appcompat:1.2.0'
    api 'com.google.android.exoplayer:exoplayer-core:2.11.7'
    compileOnly 'org.checkerframework:checker-qual:2.5.0'
}
```

配置样式

```
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        [...]
        <item name="mediaPlayer">@style/MediaPlayer.Compat</item>
</style>
```



### 项目结构

```
|-- ExoMediaPlayer
        |-- base
        |-- library
        |-- app
```

* base 实现所有播放器核心功能,暴露扩展,但不实现具体业务的基本播放器上层 UI 库
* library 上层业务扩展层,依赖 base. 用于分离核心播放器实现与业务需要的逻辑. 主要用于对接上层业务.简化业务使用.
* app 用于演示所有播放器扩展功能.


### 示例介绍

```
//文件目录
[app:main/org/cz/media/player/sample]
|-- sample
    |-- usage
        |-- AudioSampleActivity(演示音频播放+外置封面)
        |-- DefaultSampleActivity(演示默认风格的播放器界面)
        |-- MediaMultipleWindowSampleActivity(演示多个 PlayerView 切换的场景)
    |-- basic
        |-- BasicSample1Activity(加载不同来源视频)
        |-- BasicSample2Activity(演示定制面板)
    |-- list
        |-- MediaPlaylistActivity(播放视频列表)
    |-- overlay
        |-- ArtworkOverlaySampleActivity(视频封面浮层)
        |-- GestureDetectOverlaySampleActivity(视频手势操作浮层)
    |-- page
        |-- MediaPlayerPageSampleActivity(演示分页,观像台)
    |-- scene
        |-- OrientationSample1Activity(屏幕切换不重建)
        |-- OrientationSample2Activity(屏幕切换重建)
        |-- MultipleModeSampleActivity(多个 Player窗口切换)
    |-- styel
        |-- BlueStyleSampleActivity(全局样式修改)
        |-- MultipleStyleSampleActivity(不同样式播放器切换)
    |-- widget
        |-- ListPopupWindowSampleActivity(自定义弹窗)
        |-- LoadingSampleActivity(加载动画)
        |-- MediaControllerSampleActivity(播放控制面板展示)
        |-- TimeBarSampleActivity(拖动条展示)
```

### 演示文档目录

文件目录地址:([app]:main/org/cz/media/player/sample)

* basic 
    * [BasicSample1Activity](app/src/main/java/org/cz/media/player/sample/sample/basic/basic1.md)
    * [BasicSample2Activity](app/src/main/java/org/cz/media/player/sample/sample/basic/basic2.md)
* list
    * [MediaPlaylistActivity](app/src/main/java/org/cz/media/player/sample/sample/list/media_list.md)
* overlay
    * [ArtworkOverlaySampleActivity](app/src/main/java/org/cz/media/player/sample/sample/overlay/artwork_overlay.md)
    * [GestureDetectOverlaySampleActivity](app/src/main/java/org/cz/media/player/sample/sample/overlay/gesture_overlay.md)
* page
    * [MediaPlayerPageSampleActivity](app/src/main/java/org/cz/media/player/sample/sample/page/pager.md) 
* scene
    * [OrientationSample1Activity](app/src/main/java/org/cz/media/player/sample/sample/scene/orientation1.md)
    * [OrientationSample2Activity](app/src/main/java/org/cz/media/player/sample/sample/scene/orientation2.md)
    * [MultipleModeSampleActivity](app/src/main/java/org/cz/media/player/sample/sample/scene/multiple_mode.md)
* style
    * [BlueStyleSampleActivity](app/src/main/java/org/cz/media/player/sample/sample/style/blue_style.md)
    * [MultipleStyleSampleActivity](app/src/main/java/org/cz/media/player/sample/sample/style/multiple_style.md)
* widget
    * [ListPopupWindowSampleActivity](app/src/main/java/org/cz/media/player/sample/sample/widget/list_popup.md)
    * [MediaControllerSampleActivity](app/src/main/java/org/cz/media/player/sample/sample/widget/control_layout.md)
    * [TimeBarSampleActivity](app/src/main/java/org/cz/media/player/sample/sample/widget/time_bar.md)


### 使用文档
* [基础使用文档](document/exoMediaPlayer.md)
* [播放器UI组件设计](document/exoMediaPlayerDesign.md)
* [样式设计](document/mediaPlayerStyle.md)


### 其他文档
* [支持格式](document/supportedFormats.md)
* [部分性能测试](document/performanceTest.md)
* [调研记录](document/resechar.md)
* [功能性记录](document/functions.md)


### 参考文档
* ExoPlayer 官方文档(离线版)(document/exoplayer)

* ExoPlayer 官方示例实验(离线版)(document/codelab)
