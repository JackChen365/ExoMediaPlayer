package org.cz.media.player.library.extension.detect;

import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author Created by cz
 * @date 2020/9/4 11:41 PM
 * @email binigo110@126.com
 *
 * Detect double tap event.
 *
 */
public final class DoubleTapGestureDetector extends MultipleTapGestureDetector {

    public DoubleTapGestureDetector(View layout) {
        super(layout, ViewConfiguration.getDoubleTapTimeout(),2);
    }
}
