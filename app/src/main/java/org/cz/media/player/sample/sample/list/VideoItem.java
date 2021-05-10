package org.cz.media.player.sample.sample.list;

public class VideoItem {
    public final String url;
    public final String thumbnail;
    public final int width;
    public final int height;
    public int position;

    public VideoItem(String url, String thumbnail, int width, int height) {
        this.url = url;
        this.thumbnail=thumbnail;
        this.width = width;
        this.height = height;
    }

}
