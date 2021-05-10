package org.cz.media.player.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.ContextThemeWrapper;

import androidx.annotation.AttrRes;

/**
 * @author Created by cz
 * @date 2020/8/19 10:14 AM
 * @email binigo110@126.com
 */
public class ContextHelper {

    private static boolean needOverride(Context context){
        TypedArray ta = context.obtainStyledAttributes(null, new int[]{R.attr.playerOverrideStyle});
        boolean overrideStyle = ta.getBoolean(0, false);
        ta.recycle();
        return overrideStyle;
    }

    public static Context wrapPlayerContext(Context context, @AttrRes int style) {
        if (context == null) {
            throw new IllegalArgumentException("outerContext must not be null");
        }
        if(!needOverride(context)){
            return wrapPlayerContextInternal(context,style);
        }
        return context;
    }

    static Context wrapPlayerContextInternal(Context context, @AttrRes int style) {
        TypedArray ta = context.obtainStyledAttributes(null, new int[]{style});
        int themeResId = ta.getResourceId(0, 0);
        if (themeResId != 0) {
            context = new ContextThemeWrapper(context, themeResId);
        }
        ta.recycle();
        return context;
    }
}
