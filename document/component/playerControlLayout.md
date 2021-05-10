## MediaPlayerControlLayout

### 关联演示
* app/sample/widget/MediaControllerSampleActivity.java(控制面板区域展示)

### 图例

* ![artwork](../image/control_layout.gif)

### 类视图

```
|-- SimpleConstraintLayout
    |-- AnimationControlLayout
        |-- MediaPlayerControlLayout
```

* SimpleConstraintLayout 用于区域面板的约束实现.

* AnimationControlLayout 使各大区域支持动画样式扩展,各区域布局方式动态界面实现与样式配置面板整合实现.以及面板的显示与隐藏功能实现.

* MediaPlayerControlLayout 组件与播放器功能实现.