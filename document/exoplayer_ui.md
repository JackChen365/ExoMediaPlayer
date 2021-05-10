

```
<declare-styleable name="AspectRatioFrameLayout">
    <attr name="resize_mode"/>
</declare-styleable>

<declare-styleable name="DefaultTimeBar">
    <attr name="bar_height"/>
    <attr name="touch_target_height"/>
    <attr name="ad_marker_width"/>
    <attr name="scrubber_enabled_size"/>
    <attr name="scrubber_disabled_size"/>
    <attr name="scrubber_dragged_size"/>
    <attr name="scrubber_drawable"/>
    <attr name="played_color"/>
    <attr name="scrubber_color"/>
    <attr name="buffered_color"/>
    <attr name="unplayed_color"/>
    <attr name="ad_marker_color"/>
    <attr name="played_ad_marker_color"/>
</declare-styleable>

<declare-styleable name="PlayerControlView">
    <attr name="show_timeout"/>
    <attr name="rewind_increment"/>
    <attr name="fastforward_increment"/>
    <attr name="repeat_toggle_modes"/>
    <attr name="show_shuffle_button"/>
    <attr name="time_bar_min_update_interval"/>
    <attr name="controller_layout_id"/>
    <!-- DefaultTimeBar attributes -->
    <attr name="bar_height"/>
    <attr name="touch_target_height"/>
    <attr name="ad_marker_width"/>
    <attr name="scrubber_enabled_size"/>
    <attr name="scrubber_disabled_size"/>
    <attr name="scrubber_dragged_size"/>
    <attr name="scrubber_drawable"/>
    <attr name="played_color"/>
    <attr name="scrubber_color"/>
    <attr name="buffered_color"/>
    <attr name="unplayed_color"/>
    <attr name="ad_marker_color"/>
    <attr name="played_ad_marker_color"/>
</declare-styleable>


<declare-styleable name="PlayerView">
    <attr format="boolean" name="use_artwork"/>
    <attr format="color" name="shutter_background_color"/>
    <attr format="reference" name="default_artwork"/>
    <attr format="boolean" name="use_controller"/>
    <attr format="boolean" name="hide_on_touch"/>
    <attr format="boolean" name="hide_during_ads"/>
    <attr format="boolean" name="auto_show"/>
    <attr format="enum" name="show_buffering">
      <enum name="never" value="0"/>
      <enum name="when_playing" value="1"/>
      <enum name="always" value="2"/>
    </attr>
    <attr format="boolean" name="keep_content_on_player_reset"/>
    <attr format="boolean" name="use_sensor_rotation"/>
    <attr format="reference" name="player_layout_id"/>
    <attr name="surface_type"/>
    <!-- AspectRatioFrameLayout attributes -->
    <attr name="resize_mode"/>
    <!-- PlayerControlView attributes -->
    <attr name="show_timeout"/>
    <attr name="rewind_increment"/>
    <attr name="fastforward_increment"/>
    <attr name="repeat_toggle_modes"/>
    <attr name="show_shuffle_button"/>
    <attr name="time_bar_min_update_interval"/>
    <attr name="controller_layout_id"/>
    <!-- DefaultTimeBar attributes -->
    <attr name="bar_height"/>
    <attr name="touch_target_height"/>
    <attr name="ad_marker_width"/>
    <attr name="scrubber_enabled_size"/>
    <attr name="scrubber_disabled_size"/>
    <attr name="scrubber_dragged_size"/>
    <attr name="scrubber_drawable"/>
    <attr name="played_color"/>
    <attr name="scrubber_color"/>
    <attr name="buffered_color"/>
    <attr name="unplayed_color"/>
    <attr name="ad_marker_color"/>
    <attr name="played_ad_marker_color"/>
</declare-styleable>
```

### Subtitle

* Does not support multi-line with a cue list but feed line instead.

```
val list = mutableListOf<Cue>()
list.add(Cue(dataProvider.word+"\n"+dataProvider.word))
```

