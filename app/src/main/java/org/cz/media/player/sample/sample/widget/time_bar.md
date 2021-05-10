## 拖动进度条

### 演示目的
* TimeBar样式
* 介绍TimeBar简单使用
* 注意事项

### TimeBar样式

```
<attr name="bar_buffered" format="reference"/>
<attr name="bar_scrubber" format="reference"/>
<attr name="bar_progress" format="reference"/>
<attr name="bar_disabled" format="reference"/>
<attr name="bar_background" format="reference"/>
<attr name="bar_scaled" format="float"/>
<attr name="bar_mark_read" format="reference"/>
<attr name="bar_mark_unread" format="reference"/>
<attr name="bar_press_scale" format="float"/>

<declare-styleable name="DefaultTimeBar">
    <attr name="bar_scrubber"/>
    <attr name="bar_buffered"/>
    <attr name="bar_progress"/>
    <attr name="bar_disabled"/>
    <attr name="bar_background"/>
    <attr name="bar_scaled"/>
    <attr name="bar_mark_read"/>
    <attr name="bar_mark_unread"/>
    <attr name="bar_press_scale"/>
</declare-styleable>

//默认样式
<style name="DefaultTimeBar">
    <item name="bar_scrubber">@drawable/player_time_bar_scrubber_drawable</item>
    <item name="bar_buffered">@drawable/player_time_bar_buffered_drawable</item>
    <item name="bar_progress">@drawable/player_time_bar_progress_drawable</item>
    <item name="bar_background">@drawable/player_time_bar_progress_background_drawable</item>
    <item name="bar_mark_read">@drawable/player_time_bar_mark_read_drawable</item>
    <item name="bar_mark_unread">@drawable/player_time_bar_mark_unread_drawable</item>
    <item name="bar_press_scale">1.2</item>
</style>
```

* bar_scrubber 拖动把手,给定一个Drawable 资源.需要设定Drawable尺寸
* bar_buffered 缓冲进度背景,给定一个Drawable 资源.需要设定Drawable尺寸
* bar_progress 进度背景,给定一个Drawable 资源.需要设定Drawable尺寸
* bar_background 默认背景,给定一个Drawable 资源.需要设定Drawable尺寸
* bar_scaled 默认缩放尺寸
* bar_mark_read 标记己阅节点 Drawable.需要设定Drawable尺寸
* bar_mark_unread 标记己阅节点 Drawable.需要设定Drawable尺寸



### TimeBar简单使用

```
<org.cz.media.player.base.timebar.DefaultTimeBar
    android:id="@+id/timeBar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@color/md_grey_200"/>

```

以上.会使用默认样式.



### 自定义样式

```
<style name="DefaultTimeBar.Blue">
    <item name="bar_scrubber">@drawable/player_time_bar_scrubber_blue_drawable</item>
    <item name="bar_buffered">@drawable/player_time_bar_buffered_blue_drawable</item>
    <item name="bar_progress">@drawable/player_time_bar_progress_blue_drawable</item>
    <item name="bar_background">@drawable/player_time_bar_progress_background_blue_drawable</item>
    <item name="bar_mark_read">@drawable/player_time_bar_mark_read_blue_drawable</item>
    <item name="bar_mark_unread">@drawable/player_time_bar_mark_unread_blue_drawable</item>
</style>
```

