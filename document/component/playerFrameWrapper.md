## PlayerFrameWrapper

> 一个通用的状态桢管理组件.主要提供以下功能

1. 使任一ViewGroup支持任一多的状态桢扩展
2. 扩展了基本的样式关联
3. 实现桢转场动画控制
4. 实现桢的动态触发器

可以自行继承AbsPlayerViewWrapper实现自己的状态桢管理.以下为内置的播放器状态桢管理实现

```
public class PlayerViewWrapper extends AbsPlayerViewWrapper {
    public static final int FRAME_CONTAINER=0;
    public static final int FRAME_BUFFERING = R.id.playerBufferingLayout;
    public static final int FRAME_ERROR=R.id.playerErrorLayout;

    static{
        registerFrame(R.id.playerBufferingLayout, R.layout.media_view_frame_progress);
        registerFrame(R.id.playerErrorLayout, R.layout.media_view_frame_load_error);
    }

    public PlayerViewWrapper(View hostView) {
        super(hostView, 0);
    }

    public PlayerViewWrapper(ViewGroup hostView, int style) {
        super(hostView, style);
    }

    @Override
    public void onInflateFrameView(View view) {
        super.onInflateFrameView(view);
    }
}
```


### 使用示例
* 直接在xml内配置拖动模式(仅适用于根布局子控件)

```
// 包装一个ViewGroup,并传入一个small样式,用于模板文件装载到小布局内
val viewFrameWrapper1 = FrameWrapper(frameView1,R.style.FrameSmallStyle)
// 包装一个ViewGroup,并传入一个正常的全屏样式,用于全屏的样式加载等功能
val viewFrameWrapper2 = FrameWrapper(frameView2,R.style.FrameFullScreenStyle)


//设置桢变化监听
viewFrameWrapper2.addOnViewFrameChangeListener(object :OnViewFrameChangeListener{
    override fun onShowFrame(frameWrapper:AbsFrameWrapper,id: Int, view: View) {
        if(FrameWrapper.CONTAINER==id){
            // frameWrapper.showContentView()
        }
    }
    override fun onHideFrame(frameWrapper:AbsFrameWrapper,id: Int, view: View) {
        if(FrameWrapper.CONTAINER==id){
            // frameWrapper.hideContentView()
        }
    }
})

//设置不同桢
viewFrameWrapper2.setFrame(FrameWrapper.CONTAINER)
viewFrameWrapper2.setFrame(FrameWrapper.PROGRESS)
viewFrameWrapper2.setFrame(FrameWrapper.EMPTY)
...

```

### 具体使用流程

* 使用一个类继承AbsFrameWrapper

```
class FrameWrapper(hostView: ViewGroup,style:Int=R.style.FrameStyle) : AbsFrameWrapper(hostView,style) {

    // 以下桢数,为动态扩展桢数,可以无限扩展.一般为这4种
    companion object{
        const val CONTAINER=0 //原来内容固定为0
        const val PROGRESS=1 //加载进度
        const val EMPTY=2 // 空内容体
        const val ERROR=3 // 异常体

        init {
            registerFrame(R.id.playerBufferingLayout, R.layout.media_view_frame_progress);
            registerFrame(R.id.playerErrorLayout, R.layout.media_view_frame_load_error);
        }
    }

    // 当某一桢显示时调用
    override fun onShowFrame(id: Int, view: View) {
        super.onShowFrame(id, view)
    }
    // 当某一桢隐藏时调用
    override fun onHideFrame(id: Int, view: View) {
        super.onHideFrame(id, view)
    }

}
```


* 动态注册模板

```
//检索控制 id,以及动态加载布局.
registerFrame(R.id.playerBufferingLayout, R.layout.media_view_frame_progress);
```

* 如果模板具有可复用性,不必重复创建,详情配置模板样式文件,可参考此布局扩展

> [player_styles](../../library/src/main/res/values/styles.xml)

*此模板文件关联2个布局*

[media_view_frame_load_error](../../library/src/main/res/layout/media_view_frame_load_error.xml)<br>
[media_view_frame_buffering](../../library/src/main/res/layout/media_view_frame_buffering.xml)<br>

//最后一个参数的样式,代表动态的样式集
PlayerFrameWrapper(videoSurfaceView,R.style.MediaPlayerStatusStyle)

* 默认内容桢会在切换时隐藏,如果不需要隐藏,请使用以下方法

```
viewFrameWrapper1.setContentDisplay(false)
```

### 其他说明

* 目前操作不够复杂.如果需要动态以布局方法添加状态桢.可以交流添加此功能.

* 样式定制并不复杂.布局层只是最简单的属性,样式是为了外围动态定制状态桢
