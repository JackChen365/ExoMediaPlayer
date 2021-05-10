package org.cz.media.player.library.status.listener;

import android.view.View;

import org.cz.media.player.library.status.AbsPlayerViewWrapper;


/**
 * @author Created by cz
 * @date 2020-05-22 13:26
 * @email binigo110@126.com
 */
public interface OnLayoutFrameChangeListener {

    void onFrameShown(AbsPlayerViewWrapper frameWrapper, int id, View view);

    void onFrameHidden(AbsPlayerViewWrapper frameWrapper, int id, View view);
}
