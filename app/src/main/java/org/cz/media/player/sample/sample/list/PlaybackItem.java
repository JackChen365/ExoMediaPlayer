package org.cz.media.player.sample.sample.list;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaybackItem implements Parcelable {
    public final String url;
    public final int width;
    public final int height;

    public PlaybackItem(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected PlaybackItem(Parcel in) {
        this.url = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<PlaybackItem> CREATOR = new Creator<PlaybackItem>() {
        @Override
        public PlaybackItem createFromParcel(Parcel source) {
            return new PlaybackItem(source);
        }

        @Override
        public PlaybackItem[] newArray(int size) {
            return new PlaybackItem[size];
        }
    };
}
