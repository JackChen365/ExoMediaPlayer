## PlayerComponent

### 关联演示
* app/sample/widget/ListPopupWindowSampleActivity.java(播放速度切换)
* app/sample/scene/OrientationSample1Activity.java(屏幕方向切换)

### 展示图例

![control_layout](https://github.com/momodae/LibraryResources/blob/master/ExoMediaPlayer/image/control_layout.gif?raw=true)

### 组件扩展

组件提供一个相对通用的播放器功能组件扩展机制.用于分离部分(仅部分)播放器逻辑,外围业务扩展逻辑与内部 UI的关系
另一个作用是,用于外围统一调用所有播放器功能.

*目前已经实现播放器组件*

* PlayerNavigationComponent(导航栏组件状态控制)
* PlayerFastForwardComponent(快进组件)
* PlayerRewindComponent(快退组件)
* PlayerNextComponent(下一条组件)
* PlayerPreviousComponent(上一条组件)
* PlayerPlayPauseComponent(播放暂停条组件)
* PlayerRepeatComponent(重复模式(不重复/重复1次/一直重复)条组件)
* PlayerShuffleComponent(列表随机播放组件)
* PlayerTimeBarComponent(进度条组件)

*Module[app]内扩展组件*

* FillScreenComponent(点击全屏组件)
* SpeedChangeComponent(切换播放速度组件)


### 自定义组件

```
public class SpeedChangeComponent extends PlayerComponent {
    private List<String> listData=new ArrayList<>();

    public SpeedChangeComponent(List<String> listData) {
        if(null!=listData){
            this.listData.addAll(listData);
        }
    }

    /**
     * Return the component's name. Here we use 'speed'.
     * @return
     */
    @Override
    public String getComponentName() {
        return "Speed";
    }


    @Override
    public void update(MediaPlayerViewHolder viewHolder) {
        //We don't have to update the component.
    }

    /**
     * When you execute the component.It display a popupWindow.
     * And you could change the player's speed.
     */
    @Override
    public void execute() {
        MediaPlayerViewHolder viewHolder = getViewHolder();
        View playerSpeedButton=viewHolder.itemView.findViewById(R.id.playerSpeedButton);
        if(null!=playerSpeedButton){
            Context context = playerSpeedButton.getContext();
            ListPopupWindow listPopupWindow = new ListPopupWindow(context);
            listPopupWindow.setItemData(listData);
            listPopupWindow.setFocusable(true);
            listPopupWindow.setAnchorView(playerSpeedButton);
            listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                Player player = getPlayer();
                if(null!=player){
                    float speed=1f;
                    try {
                        speed=Float.parseFloat(listData.get(position));
                    } catch (Exception e){
                    }
                    PlaybackParameters playbackParameters = new PlaybackParameters(speed);
                    player.setPlaybackParameters(playbackParameters);
                }
            });
            listPopupWindow.show();
        }
    }

    /**
     * Create the component's view.
     * The view with the id:playerSpeedButton is where you define in your layout resources.
     * @param context
     * @param viewHolder
     */
    @Override
    public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
        View playerSpeedButton=viewHolder.itemView.findViewById(R.id.playerSpeedButton);
        if(null!=playerSpeedButton){
            playerSpeedButton.setOnClickListener(this);
        }
    }
}

```

#### 自定义组件分为以下几步
1. 定义一个唯一的字符串名称

```
@Override
public String getComponentName() {
    return "Speed";
}

```

2. 控制组件更新逻辑.一般非内部组件不需要实现.在播放器状态发生变化时.会自动调用.也可以根据组件逻辑状态:如网络状态显示组件.根据网络切换,手动更新

```
@Override
public void update(MediaPlayerViewHolder viewHolder) {
    //Here we don't have to update the component if not necessary.
}
```

3. 组件执行事件

组件事件也不需要我写实现.一般是组件关联的控件点击时.执行什么行为.这里之所以抽象出 execute 方法主要是方便外围业务.分离控件与业务的调用逻辑.
比如,点击控件需要弹出对话框,如果程序内部,需要手动代码调用.此时.逻辑如果只与控件的点击事件绑定.就无法在任何时机发起.对后期的业务扩展会有一定影响.

```
/**
 * When you execute the component.It display a popupWindow.
 * And you could change the player's speed.
 */
@Override
public void execute() {
    //Show a toast or do something when the component need to response.
}
```

4. 绑定组件行为到具体控件

组件关联的控件并不是唯一对应的.而是根据组件逻辑自行决定.ViewHolder 扩展为加快对应控件检测,以及分离具体的播放面板与具体的业务藕合.
ViewHolder 内部预定义了一批通用的组件,注意这些控件并不一定会有.在默认面板内.他们都会自动初始化.但是自定义面板内,可能有一部分不需要的控件逻辑.如随机播放模式,则不会初始化
此处,如果有自定义的面板逻辑.如加速按钮.则需要自己手动查找.

```
@Override
public void onComponentCreated(Context context, MediaPlayerViewHolder viewHolder) {
    View playerSpeedButton=viewHolder.itemView.findViewById(R.id.playerSpeedButton);
    if(null!=playerSpeedButton){
        playerSpeedButton.setOnClickListener(this);
    }
}
```

### 使用示例

* 注册自定义组件
```
val speedLevelList= listOf("0.50","0.75","Normal","1.25","1.50","1.75")
playerView.registerPlayerComponent(SpeedChangeComponent(speedLevelList))
```


* 外围手动调用组件逻辑

```
//双击切换播放状态
val doubleTapGestureDetector = DoubleTapGestureDetector(playerGestureLayout)
doubleTapGestureDetector.setOnTapListener {
    println("Double tap.")
    val playerComponent = playerView.getPlayerComponent<PlayerComponent>(PlayerComponent.COMPONENT_PLAY_PAUSE)
    playerComponent?.execute()
}
playerGestureLayout.addGestureDetector(doubleTapGestureDetector)
```


* 关于替换内置组件功能,实现自定义功能.
注意此功能未测试.内置组件功能,与播放器存在较强的逻辑关联. 如播放组件,需要时时监听播放状态,动态切换播放按钮等.<br>
如切换至下一个视频源组件,如果到了最后一个.需要自动禁用等. 以上功能都需要完善的处理,并且内部已经进行了事件通知<br>

代码片断来自:[MediaPlayerControlLayout]
```
@Override
public void onRepeatModeChanged(int repeatMode) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_REPEAT,viewHolder);
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
}

@Override
public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_SHUFFLE,viewHolder);
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
}

@Override
public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_TIME_BAR,viewHolder);
}

@Override
public void onTimelineChanged(Timeline timeline, @Player.TimelineChangeReason int reason) {
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_NAVIGATION,viewHolder);
    playerComponentManager.updateComponent(PlayerComponent.COMPONENT_TIME_BAR,viewHolder);
}
```

如果希望更改内置组件,可自行拷贝已经实现的内置组件.注意保留组件名称.组件名称一致会替换掉已经注册的组件.另外还需要保留原有更新操作.然后自行添加其他操作.
