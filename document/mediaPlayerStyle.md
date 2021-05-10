## Style

### 样式设计目的

> 通过本样式体系,可以动态修改控件层级内,任何一级子控件的样式.达到根据业务.定义不同的全局播放器样式目的

* 布局层级关系

```
|-- MediaPlayView
    |-- SubtitleView
    |-- AspectRadioLayout 3:4
    |-- AnimationControlLayout 
        PlayerControlLayout 
            |-- Linearlayout
                |-- ButtonGroup
                    |-- previous
                    |-- next
                    |-- play
                |-- TimeBar
```

* 样式层级关系

```
|-- mediaPlayer
    |-- mediaPlayerView
    |-- playerStatusLayout
    |-- playerControlLayout
    |-- playerAnimationControlLayout
    // the control layout animation style
    |-- playerControllerLayout_layout
    |-- playerControlLayoutBase
        // the timebar with the text style
        |-- playerPositionText
        |-- playerTimeBar
        |-- playerDurationText

        // all the basic button style
        |-- playerShuffleButton
        |-- playerShuffleButtonOn
        |-- playerShuffleButtonOff
        |-- playerRepeatButton
        |-- playerRepeatButtonOff
        |-- playerRepeatButtonOne
        |-- playerRepeatButtonAll
        |-- playerPreviousButton
        |-- playerNextButton
        |-- playerFastForwardButton
        |-- playerRewindButton
        |-- playerPlayButton
        |-- playerPauseButton
```


* 默认实现样式

```
<style name="MediaPlayer">
    <item name="playerView">@style/MediaPlayerView</item>
    <item name="playerControlLayout">@style/MediaPlayerControlLayout</item>
    <item name="playerAnimationControlLayout">@style/PlayerAnimationControlLayout</item>
    <item name="playerControllerLayout_layout">@style/PlayerControllerLayout_layout</item>
    <item name="playerControlLayoutBase">@style/PlayerControlLayoutBase</item>
</style>


<style name="MediaPlayerView">
    <item name="use_controller">true</item>
    <item name="hide_on_touch">true</item>
    <item name="auto_show">true</item>
    <item name="show_buffering">always</item>
    <item name="keep_content_on_player_reset">true</item>
    <item name="use_sensor_rotation">true</item>
    <item name="surface_type">surface_view</item>
    <item name="resize_mode">fit</item>
</style>

<style name="MediaPlayerControlLayout">
    <item name="player_rewind_increment">5000</item>
    <item name="player_forward_increment">5000</item>
    <item name="player_repeat_toggle_modes">none</item>
    <item name="player_show_shuffle_button">false</item>
    <item name="player_button_enabled_alpha">1</item>
    <item name="player_button_disabled_alpha">0.3</item>
    <item name="player_time_bar_min_update_interval">200</item>
</style>

<style name="PlayerControlLayoutBase">
    <!--The basic button style-->
    <item name="playerShuffleButton">@style/PlayerPositionText</item>
    <item name="playerShuffleButtonOn">@style/PlayerPositionText</item>
    <item name="playerShuffleButtonOff">@style/PlayerPositionText</item>

    <item name="playerRepeatButton">@style/PlayerPositionText</item>
    <item name="playerRepeatButtonOff">@style/PlayerPositionText</item>
    <item name="playerRepeatButtonOne">@style/PlayerPositionText</item>
    <item name="playerRepeatButtonAll">@style/PlayerPositionText</item>

    <item name="playerPreviousButton">@style/MediaButton.Previous</item>
    <item name="playerNextButton">@style/MediaButton.Next</item>
    <item name="playerFastForwardButton">@style/MediaButton.FastForward</item>
    <item name="playerRewindButton">@style/MediaButton.Rewind</item>
    <item name="playerPlayButton">@style/MediaButton.Play</item>
    <item name="playerPauseButton">@style/MediaButton.Pause</item>

    <item name="playerPositionText">@style/PlayerPositionText</item>
    <item name="playerTimeBar">@style/DefaultTimeBar</item>
    <item name="playerDurationText">@style/PlayerDurationText</item>
</style>


<style name="PlayerAnimationControlLayout">
    <item name="control_show_timeout">5000</item>
    <item name="control_left_layout">@layout/media_controller_left_placeholder_layout</item>
    <item name="control_top_layout">@layout/media_controller_top_placeholder_layout</item>
    <item name="control_right_layout">@layout/media_controller_right_placeholder_layout</item>
    <item name="control_bottom_layout">@layout/media_controller_bottom_placeholder_layout</item>
    <item name="control_center_layout">@layout/media_controller_placeholder_layout</item>
</style>

 <style name="PlayerControllerLayout_layout">
    <item name="layout_controller_animation">@style/default_animation</item>
</style>
```


样式从根样式入口:mediaPlayer进入.

一级子样式:

```
    |-- mediaPlayerView
    |-- playerStatusLayout
    |-- playerControlLayout
    |-- playerAnimationControlLayout
    // the control layout animation style
    |-- playerControllerLayout_layout
    |-- playerControlLayoutBase
```

* mediaPlayerView 是 MediaPlayerView的控件配置样式

```
<style name="MediaPlayerView">
    <item name="use_controller">true</item>
    <item name="hide_on_touch">true</item>
    <item name="auto_show">true</item>
    <item name="show_buffering">always</item>
    <item name="keep_content_on_player_reset">true</item>
    <item name="surface_type">surface_view</item>
    <item name="player_layout_id">@layout/media_simple_player_view</item>
    <item name="resize_mode">fit</item>
</style>
```


* playerStatusLayout 为扩展的状态桢样式.主要为扩展视频界面的缓冲/异常页,在 mediaPlayer 库中扩展.并不在 base 库中.

```
//文件位于:[mediaplayer/values/styels.xml]
<!--    The status style-->
<style name="MediaPlayerStatusStyle">
    <!-- The buffering layout-->
    <item name="playerBufferingLayout">@style/PlayerBufferingLayoutStyle</item>
    <item name="playerBufferingProgress">@style/PlayerBufferingProgressStyle</item>
    <item name="playerBufferingText">@style/PlayerBufferingTextStyle</item>
    <!-- The error layout-->
    <item name="playerErrorLayout">@style/PlayerErrorLayoutStyle</item>
    <item name="playerErrorImage">@style/PlayerErrorImageStyle</item>
    <item name="playerErrorText">@style/PlayerErrorTextStyle</item>
</style>
```

* playerControlLayout 控制面板的基础属性.

```
//文件位于:[base/values/styels.xml]
<style name="MediaPlayerControlLayout">
    <item name="player_rewind_increment">5000</item>
    <item name="player_forward_increment">5000</item>
    <item name="player_repeat_toggle_modes">none</item>
    <item name="player_show_shuffle_button">false</item>
    <item name="player_button_enabled_alpha">1</item>
    <item name="player_button_disabled_alpha">0.3</item>
    <item name="player_time_bar_min_update_interval">200</item>
</style>
```

* playerAnimationControlLayout 播放器控制面板样式

```
//文件位于:[base/values/styels.xml]
<style name="PlayerAnimationControlLayout">
    <item name="control_show_timeout">15000</item>
    <item name="control_left_layout">@layout/media_controller_left_layout</item>
    <item name="control_top_layout">@layout/media_controller_top_layout</item>
    <item name="control_right_layout">@layout/media_controller_right_layout</item>
    <item name="control_bottom_layout">@layout/media_controller_bottom_layout</item>
    <item name="control_center_layout">@layout/media_controller_center_placeholder_layout</item>
</style>

//其中一个布局展示
<?xml version="1.0" encoding="utf-8"?>
<View xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="0dp"
    android:layout_height="match_parent"
    //设定左侧区域动画样式
    app:layout_control_animation="@style/left_side_animation"/>

//动画样式
<style name="left_side_animation">
    <item name="android:windowEnterAnimation">@anim/left_enter_animation</item>
    <item name="android:windowExitAnimation">@anim/left_exit_animation</item>
</style>
```

* playerControllerLayout_layout 控制面板子控件样式

```
//默认动画样式
<style name="PlayerControllerLayout_layout">
    <item name="layout_control_animation">@style/default_animation</item>
</style>
```

* playerControlLayoutBase 控制面板所有子控件样式集

```
<style name="PlayerControlLayoutBase">
    <!--The basic button style-->
    <item name="playerShuffleButton">@style/MediaButton.ShuffleOn</item>
    <item name="playerShuffleButtonOn">@style/MediaButton.ShuffleOn</item>
    <item name="playerShuffleButtonOff">@style/MediaButton.ShuffleOff</item>

    <item name="playerRepeatButton">@style/MediaButton.RepeatOne</item>
    <item name="playerRepeatButtonOff">@style/MediaButton.RepeatOff</item>
    <item name="playerRepeatButtonOne">@style/MediaButton.RepeatOne</item>
    <item name="playerRepeatButtonAll">@style/MediaButton.RepeatAll</item>

    <item name="playerPreviousButton">@style/MediaButton.Previous</item>
    <item name="playerNextButton">@style/MediaButton.Next</item>
    <item name="playerFastForwardButton">@style/MediaButton.FastForward</item>
    <item name="playerRewindButton">@style/MediaButton.Rewind</item>
    <item name="playerPlayButton">@style/MediaButton.Play</item>
    <item name="playerPauseButton">@style/MediaButton.Pause</item>

    <item name="playerPositionText">@style/PlayerPositionText</item>
    <item name="playerTimeBar">@style/DefaultTimeBar</item>
    <item name="playerDurationText">@style/PlayerDurationText</item>
</style>
```

* Button 样式示例

```
<style name="MediaButton">
    <item name="android:background">?android:attr/selectableItemBackground</item>
    <item name="android:layout_width">48dp</item>
    <item name="android:layout_height">48dp</item>
</style>

<style name="MediaButton.Play">
    <item name="android:src">@drawable/media_icon_play</item>
    <item name="android:contentDescription">@string/media_controls_play_description</item>
</style>

```

复写层级关系为:mediaPlayer/PlayerControlLayoutBase/xxx

### 样式修改示例

* 示例参考:[BlueStyleSampleActivity.java](../app/src/main/java/org/cz/media/player/sample/sample/style/BlueStyleSampleActivity.kt)
* 示例修改请参考[player_style.xml](../library/src/main/res/values/player-style.xml)

### 自定义播放控制面板时预定义的按钮样式

```
<!--The style of shuffle button-->
<attr name="playerShuffleButton" format="reference"/>
<attr name="playerShuffleButtonOn" format="reference"/>
<attr name="playerShuffleButtonOff" format="reference"/>

<!--the style of RepeatButton-->
<attr name="playerRepeatButton" format="reference"/>
<attr name="playerRepeatButtonOff" format="reference"/>
<attr name="playerRepeatButtonOne" format="reference"/>
<attr name="playerRepeatButtonAll" format="reference"/>

<!--The basic button style-->
<attr name="playerPreviousButton" format="reference"/>
<attr name="playerNextButton" format="reference"/>
<attr name="playerFastForwardButton" format="reference"/>
<attr name="playerRewindButton" format="reference"/>
<attr name="playerPlayButton" format="reference"/>
<attr name="playerPauseButton" format="reference"/>
<attr name="playerPositionText" format="reference"/>
<attr name="playerDurationText" format="reference"/>

以上为定义的默认功能按钮样式.

<item name="playerShuffleButton">@style/MediaButton.ShuffleOn</item>
<item name="playerShuffleButtonOn">@style/MediaButton.ShuffleOn</item>
<item name="playerShuffleButtonOff">@style/MediaButton.ShuffleOff</item>

<item name="playerRepeatButton">@style/MediaButton.RepeatOne</item>
<item name="playerRepeatButtonOff">@style/MediaButton.RepeatOff</item>
<item name="playerRepeatButtonOne">@style/MediaButton.RepeatOne</item>
<item name="playerRepeatButtonAll">@style/MediaButton.RepeatAll</item>

<item name="playerPreviousButton">@style/MediaButton.Previous</item>
<item name="playerNextButton">@style/MediaButton.Next</item>
<item name="playerFastForwardButton">@style/MediaButton.FastForward</item>
<item name="playerRewindButton">@style/MediaButton.Rewind</item>
<item name="playerPlayButton">@style/MediaButton.Play</item>
<item name="playerPauseButton">@style/MediaButton.Pause</item>

以上为默认实现的按钮功能样式.
```

* 在动态修改控制面板时.可以直接使用以上定义的引用样式.

```
<LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:paddingTop="4dp"
        android:orientation="horizontal">

        <ImageButton
            android:id="@id/playerPrevButton"
            style="?attr/playerPreviousButton"/>

        <ImageButton
            android:id="@id/playerRewindButton"
            style="?attr/playerRewindButton"/>

        <ImageButton
            android:id="@id/playerShuffleButton"
            style="?attr/playerShuffleButton"/>

        [...]
</LinearLayout>
```