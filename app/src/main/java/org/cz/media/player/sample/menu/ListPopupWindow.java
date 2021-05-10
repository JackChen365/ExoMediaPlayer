package org.cz.media.player.sample.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.cz.media.player.sample.R;

import java.util.List;

/**
 * @author Created by cz
 * @date 2020/9/14 2:12 PM
 * @email bingo110@126.com
 */
public class ListPopupWindow {
    private Context context;
    private PopupWindow popupWindow;
    //the view where PopupWindow lie in
    private View anchorView;
    private ListView popupListView;
    //ListView item data
    private List<String> listData;
    //the animation for PopupWindow
    private int windowAnimationStyle;
    private AdapterView.OnItemClickListener itemClickListener;
    private boolean focusable;

    public ListPopupWindow(@Nullable Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }
        this.context = context;
    }

    public void setAnchorView(@Nullable View anchor) {
        anchorView = anchor;
    }

    public void setItemData(List<String> mItemData) {
        this.listData = mItemData;
    }

    public void setWindowAnimationStyle(int animationStyle) {
        this.windowAnimationStyle = animationStyle;
    }

    /**
     * Set whether this window should be modal when shown.
     *
     * <p>If a popup window is modal, it will receive all touch and key input.
     * If the user touches outside the popup window's content area the popup window
     * will be dismissed.
     *
     * @param focusable {@code true} if the popup window should be modal, {@code false} otherwise.
     */
    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isShowing(){
        return popupWindow != null && popupWindow.isShowing();
    }

    public void dismiss(){
        if (isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * Sets a listener to receive events when a list item is clicked.
     *
     * @param clickListener Listener to register
     *
     * @see ListView#setOnItemClickListener(AdapterView.OnItemClickListener)
     */
    public void setOnItemClickListener(@Nullable AdapterView.OnItemClickListener clickListener) {
        itemClickListener = clickListener;
        if (popupListView != null) {
            popupListView.setOnItemClickListener(itemClickListener);
        }
    }

    @SuppressLint("Range")
    public void show() {
        if (anchorView == null) {
            throw new IllegalArgumentException("PopupWindow show location view can  not be null");
        }
        if (listData == null) {
            throw new IllegalArgumentException("please fill ListView Data");
        }
        popupListView = new ListView(context);
        popupListView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        popupListView.setVerticalScrollBarEnabled(false);
        popupListView.setDivider(null);
        popupListView.setAdapter(new ArrayAdapter<>(context, R.layout.simple_list_item, listData));
        if (itemClickListener != null) {
            popupListView.setOnItemClickListener((parent, view, position, id) -> {
                popupWindow.dismiss();
                if(null!=itemClickListener){
                    itemClickListener.onItemClick(parent,view,position,id);
                }
            });
        }
        // Measure layout here, then we can get measureHeight.
        popupListView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int width = popupListView.getMeasuredWidth();
        int height = computerListHeight(popupListView);

        popupWindow = new PopupWindow(popupListView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setWidth(popupListView.getMeasuredWidth());
        // WRAP_CONTENT works well for height, but not for width.
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        if (windowAnimationStyle != 0) {
            popupWindow.setAnimationStyle(windowAnimationStyle);
        }
        popupWindow.setFocusable(focusable);
        popupWindow.setOutsideTouchable(true);

        Rect location = getAnchorLocationRect(anchorView);
        if (location != null) {
            int x = location.left + (anchorView.getWidth()-width) / 2;
            popupWindow.showAtLocation(anchorView, Gravity.TOP|Gravity.START, x,location.top-height);
        }
    }

    public static int computerListHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View mView = adapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();

        }
        return totalHeight;
    }

    public Rect getAnchorLocationRect(View v) {
        if (v == null) return null;
        int[] loc_int = new int[2];
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

}
