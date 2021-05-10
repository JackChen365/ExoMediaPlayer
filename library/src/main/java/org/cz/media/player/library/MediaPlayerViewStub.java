package org.cz.media.player.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import java.lang.ref.WeakReference;

/**
 * @author Created by cz
 * @date 2020/9/18 5:50 PM
 * @email bingo110@126.com
 */
public class MediaPlayerViewStub extends View {
    private int mInflatedId;
    private int mLayoutResource;

    private WeakReference<View> mInflatedViewRef;

    private LayoutInflater mInflater;
    private OnInflateListener mInflateListener;

    public MediaPlayerViewStub(Context context) {
        this(context, 0);
    }

    /**
     * Creates a new ViewStub with the specified layout resource.
     *
     * @param context The application's environment.
     * @param layoutResource The reference to a layout resource that will be inflated.
     */
    public MediaPlayerViewStub(Context context, @LayoutRes int layoutResource) {
        this(context, null);

        mLayoutResource = layoutResource;
    }

    public MediaPlayerViewStub(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaPlayerViewStub(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MediaPlayerViewStub(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MediaPlayerViewStub, defStyleAttr, defStyleRes);
        mInflatedId = a.getResourceId(R.styleable.MediaPlayerViewStub_android_inflatedId, NO_ID);
        mLayoutResource = a.getResourceId(R.styleable.MediaPlayerViewStub_android_layout, 0);
        setId(a.getResourceId(R.styleable.MediaPlayerViewStub_android_id, NO_ID));
        a.recycle();

        setVisibility(GONE);
        setWillNotDraw(true);
    }

    /**
     * Returns the id taken by the inflated view. If the inflated id is
     * {@link View#NO_ID}, the inflated view keeps its original id.
     *
     * @return A positive integer used to identify the inflated view or
     *         {@link #NO_ID} if the inflated view should keep its id.
     *
     * @see #setInflatedId(int)
     * @attr ref android.R.styleable#ViewStub_inflatedId
     */
    @IdRes
    public int getInflatedId() {
        return mInflatedId;
    }

    /**
     * Defines the id taken by the inflated view. If the inflated id is
     * {@link View#NO_ID}, the inflated view keeps its original id.
     *
     * @param inflatedId A positive integer used to identify the inflated view or
     *                   {@link #NO_ID} if the inflated view should keep its id.
     *
     * @see #getInflatedId()
     * @attr ref android.R.styleable#ViewStub_inflatedId
     */
    public void setInflatedId(@IdRes int inflatedId) {
        mInflatedId = inflatedId;
    }

    /** @hide **/
    public Runnable setInflatedIdAsync(@IdRes int inflatedId) {
        mInflatedId = inflatedId;
        return null;
    }

    /**
     * Returns the layout resource that will be used by {@link #setVisibility(int)} or
     * {@link #inflate()} to replace this StubbedView
     * in its parent by another view.
     *
     * @return The layout resource identifier used to inflate the new View.
     *
     * @see #setLayoutResource(int)
     * @see #setVisibility(int)
     * @see #inflate()
     * @attr ref android.R.styleable#ViewStub_layout
     */
    @LayoutRes
    public int getLayoutResource() {
        return mLayoutResource;
    }

    /**
     * Specifies the layout resource to inflate when this StubbedView becomes visible or invisible
     * or when {@link #inflate()} is invoked. The View created by inflating the layout resource is
     * used to replace this StubbedView in its parent.
     *
     * @param layoutResource A valid layout resource identifier (different from 0.)
     *
     * @see #getLayoutResource()
     * @see #setVisibility(int)
     * @see #inflate()
     * @attr ref android.R.styleable#ViewStub_layout
     */
    public void setLayoutResource(@LayoutRes int layoutResource) {
        mLayoutResource = layoutResource;
    }

    /** @hide **/
    public Runnable setLayoutResourceAsync(@LayoutRes int layoutResource) {
        mLayoutResource = layoutResource;
        return null;
    }

    /**
     * Set {@link LayoutInflater} to use in {@link #inflate()}, or {@code null}
     * to use the default.
     */
    public void setLayoutInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }

    /**
     * Get current {@link LayoutInflater} used in {@link #inflate()}.
     */
    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override
    @SuppressLint("MissingSuperCall")
    public void draw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
    }

    /**
     * When visibility is set to {@link #VISIBLE} or {@link #INVISIBLE},
     * {@link #inflate()} is invoked and this StubbedView is replaced in its parent
     * by the inflated layout resource. After that calls to this function are passed
     * through to the inflated view.
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     *
     * @see #inflate()
     */
    @Override
    public void setVisibility(int visibility) {
        if (mInflatedViewRef != null) {
            View view = mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
            } else {
                throw new IllegalStateException("setVisibility called on un-referenced view");
            }
        } else {
            super.setVisibility(visibility);
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate();
            }
        }
    }

    /** @hide **/
    public Runnable setVisibilityAsync(int visibility) {
        if (visibility == VISIBLE || visibility == INVISIBLE) {
            ViewGroup parent = (ViewGroup) getParent();
            return new ViewReplaceRunnable(inflateViewNoAdd(parent));
        } else {
            return null;
        }
    }

    private View inflateViewNoAdd(ViewGroup parent) {
        final LayoutInflater factory;
        if (mInflater != null) {
            factory = mInflater;
        } else {
            Context context = getContext();
            factory = LayoutInflater.from(context);
        }
        final View view = factory.inflate(mLayoutResource, parent, false);

        if (mInflatedId != NO_ID) {
            view.setId(mInflatedId);
        }
        return view;
    }

    private void replaceSelfWithView(View view, ViewGroup parent) {
        final int index = parent.indexOfChild(this);
        parent.removeViewInLayout(this);

        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            parent.addView(view, index, layoutParams);
        } else {
            parent.addView(view, index);
        }
    }

    /**
     * Inflates the layout resource identified by {@link #getLayoutResource()}
     * and replaces this StubbedView in its parent by the inflated layout resource.
     *
     * @return The inflated layout resource.
     *
     */
    public View inflate() {
        final ViewParent viewParent = getParent();

        if (viewParent != null && viewParent instanceof ViewGroup) {
            if (mLayoutResource != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final View view = inflateViewNoAdd(parent);
                replaceSelfWithView(view, parent);

                mInflatedViewRef = new WeakReference<>(view);
                if (mInflateListener != null) {
                    mInflateListener.onInflate(this, view);
                }

                return view;
            } else {
                throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
        }
    }

    /**
     * Specifies the inflate listener to be notified after this ViewStub successfully
     * inflated its layout resource.
     *
     * @param inflateListener The OnInflateListener to notify of successful inflation.
     *
     * @see android.view.ViewStub.OnInflateListener
     */
    public void setOnInflateListener(OnInflateListener inflateListener) {
        mInflateListener = inflateListener;
    }

    /**
     * Listener used to receive a notification after a ViewStub has successfully
     * inflated its layout resource.
     *
     * @see android.view.ViewStub#setOnInflateListener(android.view.ViewStub.OnInflateListener)
     */
    public static interface OnInflateListener {
        /**
         * Invoked after a ViewStub successfully inflated its layout resource.
         * This method is invoked after the inflated view was added to the
         * hierarchy but before the layout pass.
         *
         * @param stub The ViewStub that initiated the inflation.
         * @param inflated The inflated View.
         */
        void onInflate(org.cz.media.player.library.MediaPlayerViewStub stub, View inflated);
    }

    /** @hide **/
    public class ViewReplaceRunnable implements Runnable {
        public final View view;

        ViewReplaceRunnable(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            replaceSelfWithView(view, (ViewGroup) getParent());
        }
    }
}
