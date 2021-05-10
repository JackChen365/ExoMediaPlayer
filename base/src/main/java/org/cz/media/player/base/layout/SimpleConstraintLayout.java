package org.cz.media.player.base.layout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.cz.media.player.base.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Created by cz
 * @date 2020/6/9 1:58 PM
 * @email binigo110@126.com
 */
public class SimpleConstraintLayout extends ViewGroup {
    private static final String TAG="SimpleConstraintLayout";
    public static final int NONE= View.NO_ID;
    public static final int PARENT=0x00;

    public static final int LEFT_TO_LEFT=0x00;
    public static final int LEFT_TO_RIGHT=0x01;
    public static final int TOP_TO_TOP=0x02;
    public static final int TOP_TO_BOTTOM=0x03;
    public static final int RIGHT_TO_RIGHT=0x04;
    public static final int RIGHT_TO_LEFT=0x05;
    public static final int BOTTOM_TO_BOTTOM=0x06;
    public static final int BOTTOM_TO_TOP=0x07;
    private static final int RULE_COUNT=0x08;

    private static final int[] ALL_RULES=new int[]{
            PARENT,LEFT_TO_LEFT,LEFT_TO_RIGHT,
            TOP_TO_TOP,TOP_TO_BOTTOM,
            RIGHT_TO_RIGHT,RIGHT_TO_LEFT,
            BOTTOM_TO_BOTTOM,BOTTOM_TO_TOP
    };

    /**
     * The dependency graph. We organize all the child views relative relationship as a graph.
     */
    private DependencyGraph dependencyGraph=new DependencyGraph();
    /**
     * If the layout is dirty. We are going to refresh the hierarchy and re-calculate all the nodes.
     */
    private boolean dirtyHierarchy;

    public SimpleConstraintLayout(Context context) {
        super(context);
    }

    public SimpleConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        dirtyHierarchy = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(dirtyHierarchy){
            dirtyHierarchy = false;
            dependencyGraph.clear();
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                dependencyGraph.add(child);
            }
            dependencyGraph.verifyHierarchyGraph(ALL_RULES);
        }

        int myWidth = -1;
        int myHeight = -1;

        int width = 0;
        int height = 0;

        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // Record our dimensions if they are known;
        if (widthMode != MeasureSpec.UNSPECIFIED) {
            myWidth = widthSize;
        }
        if (heightMode != MeasureSpec.UNSPECIFIED) {
            myHeight = heightSize;
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            width = myWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = myHeight;
        }
        final boolean isWrapContentWidth = widthMode != MeasureSpec.EXACTLY;
        final boolean isWrapContentHeight = heightMode != MeasureSpec.EXACTLY;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                applyHorizontalSizeRules(params);
                measureChildHorizontal(child, params, myWidth, myHeight);
                positionChildHorizontal(child, params, myWidth, isWrapContentWidth);
            }
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();

                applyVerticalSizeRules(params);
                measureChild(child, params, myWidth, myHeight);
                positionChildVertical(child, params, myHeight, isWrapContentHeight);

                if (isWrapContentWidth) {
                    width = Math.max(width, params.right);
                }

                if (isWrapContentHeight) {
                    height = Math.max(height, params.bottom);
                }
            }

        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (isWrapContentWidth) {
            // Width already has left padding in it since it was calculated by looking at
            // the right of each child view
            width += paddingRight;

            if (layoutParams.width >= 0) {
                width = Math.max(width, layoutParams.width);
            }

            width = Math.max(width, getSuggestedMinimumWidth());
            width = resolveSize(width, widthMeasureSpec);
        }

        if (isWrapContentHeight) {
            // Height already has top padding in it since it was calculated by looking at
            // the bottom of each child view
            height += paddingBottom;

            if (layoutParams.height >= 0) {
                height = Math.max(height, layoutParams.height);
            }

            height = Math.max(height, getSuggestedMinimumHeight());
            height = resolveSize(height, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    private LayoutParams verifyHorizontalChain(LayoutParams params) {
        LayoutParams layoutParams=params;
        LayoutParams relatedViewParams = getRelatedViewParams(params.rules, RIGHT_TO_LEFT);
        if(null!=relatedViewParams){
            LayoutParams oppositeRelatedViewParams = getRelatedViewParams(relatedViewParams.rules, LEFT_TO_RIGHT);
            if(null!=oppositeRelatedViewParams){
                //keep going.
                layoutParams = verifyHorizontalChain(relatedViewParams);
            }
        }
        return layoutParams;
    }


    private void applyHorizontalSizeRules(LayoutParams childParams) {
        int[] rules = childParams.getRules();
        LayoutParams anchorParams;
        // -1 indicated a "soft requirement" in that direction. For example:
        // left=10, right=-1 means the view must start at 10, but can go as far as it wants to the right
        // left =-1, right=10 means the view must end at 10, but can go as far as it wants to the left
        // left=10, right=20 means the left and right ends are both fixed
        childParams.left = -1;
        childParams.right = -1;

        anchorParams = getRelatedViewParams(rules, LEFT_TO_RIGHT);
        if (anchorParams != null) {
            childParams.left = anchorParams.right + childParams.leftMargin;
        }
        anchorParams = getRelatedViewParams(rules, RIGHT_TO_LEFT);
        if (anchorParams != null) {
            childParams.right = anchorParams.left - childParams.rightMargin;
        }
        anchorParams = getRelatedViewParams(rules, LEFT_TO_LEFT);
        if (anchorParams != null) {
            childParams.left = anchorParams.left + childParams.leftMargin;
        }
        anchorParams = getRelatedViewParams(rules, RIGHT_TO_RIGHT);
        if (anchorParams != null) {
            childParams.right = anchorParams.right - childParams.rightMargin;
        }
    }

    private void measureChildHorizontal(View child, LayoutParams params, int myWidth, int myHeight) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int childWidthMeasureSpec = getChildMeasureSpec(params.left,
                params.right, params.width,
                params.leftMargin, params.rightMargin,
                paddingLeft, paddingRight,
                myWidth);
        int childHeightMeasureSpec;
        if (params.width == LayoutParams.MATCH_PARENT) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(myHeight, MeasureSpec.AT_MOST);
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private void applyVerticalSizeRules(LayoutParams childParams) {
        int[] rules = childParams.getRules();
        LayoutParams anchorParams;

        childParams.top = -1;
        childParams.bottom = -1;

        anchorParams = getRelatedViewParams(rules, BOTTOM_TO_TOP);
        if (anchorParams != null) {
            childParams.bottom = anchorParams.top - childParams.bottomMargin;
        }

        anchorParams = getRelatedViewParams(rules, TOP_TO_BOTTOM);
        if (anchorParams != null) {
            childParams.top = anchorParams.bottom + childParams.topMargin;
        }

        anchorParams = getRelatedViewParams(rules, TOP_TO_TOP);
        if (anchorParams != null) {
            childParams.top = anchorParams.top + childParams.topMargin;
        }
        anchorParams = getRelatedViewParams(rules, BOTTOM_TO_BOTTOM);
        if (anchorParams != null) {
            childParams.bottom = anchorParams.bottom - childParams.bottomMargin;
        }
    }

    private LayoutParams verifyVerticalChain(LayoutParams params) {
        LayoutParams layoutParams=params;
        LayoutParams relatedViewParams = getRelatedViewParams(params.rules, TOP_TO_BOTTOM);
        if(null!=relatedViewParams){
            LayoutParams oppositeRelatedViewParams = getRelatedViewParams(relatedViewParams.rules, BOTTOM_TO_TOP);
            if(null!=oppositeRelatedViewParams){
                //keep going.
                layoutParams = verifyHorizontalChain(relatedViewParams);
            }
        }
        return layoutParams;
    }

    private View getRelatedView(int[] rules, int relation) {
        int id = rules[relation];
        if (id != 0) {
            DependencyGraph.Node node = dependencyGraph.keyNodeList.get(id);
            if (node == null) return null;
            View v = node.view;

            // Find the first non-GONE view up the chain
            while (v.getVisibility() == View.GONE) {
                rules = ((LayoutParams) v.getLayoutParams()).getRules();
                node = dependencyGraph.keyNodeList.get((rules[relation]));
                if (node == null) return null;
                v = node.view;
            }

            return v;
        }

        return null;
    }

    private LayoutParams getRelatedViewParams(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v != null) {
            ViewGroup.LayoutParams params = v.getLayoutParams();
            if (params instanceof LayoutParams) {
                return (LayoutParams) v.getLayoutParams();
            }
        }
        return null;
    }

    /**
     * Get a measure spec that accounts for all of the constraints on this view.
     * This includes size contstraints imposed by the RelativeLayout as well as
     * the View's desired dimension.
     *
     * @param childStart The left or top field of the child's layout params
     * @param childEnd The right or bottom field of the child's layout params
     * @param childSize The child's desired size (the width or height field of
     *        the child's layout params)
     * @param startMargin The left or top margin
     * @param endMargin The right or bottom margin
     * @param startPadding mPaddingLeft or mPaddingTop
     * @param endPadding mPaddingRight or mPaddingBottom
     * @param mySize The width or height of this view (the RelativeLayout)
     * @return MeasureSpec for the child
     */
    private int getChildMeasureSpec(int childStart, int childEnd,
                                    int childSize, int startMargin, int endMargin, int startPadding,
                                    int endPadding, int mySize) {
        int childSpecMode = 0;
        int childSpecSize = 0;

        // Figure out start and end bounds.
        int tempStart = childStart;
        int tempEnd = childEnd;

        // If the view did not express a layout constraint for an edge, use
        // view's margins and our padding
        if (tempStart < 0) {
            tempStart = startPadding + startMargin;
        }
        if (tempEnd < 0) {
            tempEnd = mySize - endPadding - endMargin;
        }

        // Figure out maximum size available to this view
        int maxAvailable = tempEnd - tempStart;
        if (childStart >= 0 && childEnd >= 0&&childSize == LayoutParams.MATCH_PARENT) {
            // Constraints fixed both edges, so child must be an exact size
            childSpecMode = MeasureSpec.EXACTLY;
            childSpecSize = maxAvailable;
        } else {
            if (childSize >= 0) {
                // Child wanted an exact size. Give as much as possible
                childSpecMode = MeasureSpec.EXACTLY;

                if (maxAvailable >= 0) {
                    // We have a maxmum size in this dimension.
                    childSpecSize = Math.min(maxAvailable, childSize);
                } else {
                    // We can grow in this dimension.
                    childSpecSize = childSize;
                }
            } else if (childSize == LayoutParams.MATCH_PARENT) {
                // Child wanted to be as big as possible. Give all availble
                // space
                childSpecMode = MeasureSpec.EXACTLY;
                childSpecSize = maxAvailable;
            } else if (childSize == LayoutParams.WRAP_CONTENT) {
                // Child wants to wrap content. Use AT_MOST
                // to communicate available space if we know
                // our max size
                if (maxAvailable >= 0) {
                    // We have a maxmum size in this dimension.
                    childSpecMode = MeasureSpec.AT_MOST;
                    childSpecSize = maxAvailable;
                } else {
                    // We can grow in this dimension. Child can be as big as it
                    // wants
                    childSpecMode = MeasureSpec.UNSPECIFIED;
                    childSpecSize = 0;
                }
            }
        }
        return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
    }

    /**
     * Measure a child. The child should have left, top, right and bottom information
     * stored in its LayoutParams. If any of these values is -1 it means that the view
     * can extend up to the corresponding edge.
     *
     * @param child Child to measure
     * @param params LayoutParams associated with child
     * @param myWidth Width of the the RelativeLayout
     * @param myHeight Height of the RelativeLayout
     */
    private void measureChild(View child, LayoutParams params, int myWidth, int myHeight) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int childWidthMeasureSpec = getChildMeasureSpec(params.left,
                params.right, params.width,
                params.leftMargin, params.rightMargin,
                paddingLeft, paddingRight,
                myWidth);
        int childHeightMeasureSpec = getChildMeasureSpec(params.top,
                params.bottom, params.height,
                params.topMargin, params.bottomMargin,
                paddingTop, paddingBottom,
                myHeight);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private void positionChildHorizontal(View child, LayoutParams params, int myWidth,
                                         boolean wrapContent) {
        int[] rules = params.getRules();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        if (params.left < 0 && params.right >= 0) {
            // Right is fixed, but left varies
            params.left = params.right - child.getMeasuredWidth();
        } else if (params.left >= 0 && params.right < 0) {
            // Left is fixed, but right varies
            params.right = params.left + child.getMeasuredWidth();
        } else if (params.left < 0 && params.right < 0) {
            // Both left and right vary
            if (rules[LEFT_TO_LEFT] == PARENT && rules[RIGHT_TO_RIGHT] == PARENT) {
                if (!wrapContent) {
                    int childWidth = child.getMeasuredWidth();
                    int left = (myWidth - childWidth) / 2;
                    params.left = left;
                    params.right = left + childWidth;
                } else {
                    params.left = paddingLeft + params.leftMargin;
                    params.right = params.left + child.getMeasuredWidth();
                }
            } else if(rules[LEFT_TO_LEFT] == PARENT){
                params.left = paddingLeft + params.leftMargin;
                params.right = params.left + child.getMeasuredWidth();
            } else if(rules[RIGHT_TO_RIGHT] == PARENT){
                params.left = myWidth-paddingRight-params.rightMargin-child.getMeasuredWidth();
                params.right = myWidth-paddingRight-params.rightMargin;
            }
        } else if (params.left > 0 && params.right > 0){
            //align-center in a specific view horizontally.
            if ((rules[LEFT_TO_LEFT] != NONE||rules[LEFT_TO_RIGHT] != NONE)
            && (rules[RIGHT_TO_RIGHT] != NONE||rules[RIGHT_TO_LEFT] != NONE)) {
                int childWidth = child.getMeasuredWidth();
                int contentWidth = params.right - params.left;
                params.left+=(contentWidth-childWidth)/2;
                params.right=params.left+childWidth;
            }
        }
    }

    private void positionChildVertical(View child, LayoutParams params, int myHeight,
                                       boolean wrapContent) {
        int[] rules = params.getRules();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        if (params.top < 0 && params.bottom >= 0) {
            // Bottom is fixed, but top varies
            params.top = params.bottom - child.getMeasuredHeight();
        } else if (params.top >= 0 && params.bottom < 0) {
            // Top is fixed, but bottom varies
            params.bottom = params.top + child.getMeasuredHeight();
        } else if (params.top < 0 && params.bottom < 0){
            // Both top and bottom vary
            if (rules[TOP_TO_TOP] == PARENT && rules[BOTTOM_TO_BOTTOM] == PARENT) {
                if (!wrapContent) {
                    int childHeight = child.getMeasuredHeight();
                    int top = (myHeight - childHeight) / 2;
                    params.top = top;
                    params.bottom = top + childHeight;
                } else {
                    params.top = paddingTop + params.topMargin;
                    params.bottom = params.top + child.getMeasuredHeight();
                }
            } else if(rules[TOP_TO_TOP] == PARENT){
                params.top = paddingTop + params.topMargin;
                params.bottom = params.top + child.getMeasuredHeight();
            } else if(rules[BOTTOM_TO_BOTTOM] == PARENT){
                params.top = myHeight-paddingBottom-params.bottomMargin-child.getMeasuredHeight();
                params.bottom = myHeight-paddingBottom-params.bottomMargin;
            }
        } else if (params.top > 0 && params.bottom > 0){
            //align-center in a specific view vertically.
            if ((rules[TOP_TO_TOP] != NONE || rules[TOP_TO_BOTTOM] != NONE) &&
                    (rules[BOTTOM_TO_BOTTOM] != NONE || rules[BOTTOM_TO_TOP] != NONE)) {
                int childHeight = child.getMeasuredHeight();
                int contentHeight = params.bottom - params.top;
                params.top+=(contentHeight-childHeight)/2;
                params.bottom=params.top+childHeight;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //  The layout has actually already been performed and the positions
        //  cached.  Apply the cached values to the children.
        int count = getChildCount();
        Resources resources = getResources();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams st = (LayoutParams) child.getLayoutParams();
                child.layout(st.left, st.top, st.right, st.bottom);
            }
        }
    }

    private class DependencyGraph {
        /**
         * List of all views in the graph.
         */
        private ArrayList<Node> nodeList = new ArrayList<>();

        /**
         * List of nodes in the graph. Each node is identified by its
         * view id (see View#getId()).
         */
        private SparseArray<Node> keyNodeList = new SparseArray<>();

        /**
         * Temporary data structure used to build the list of roots
         * for this graph.
         */
        private SparseArray<Node> rootNodeList = new SparseArray<>();

        /**
         * Clears the graph.
         */
        void clear() {
            final ArrayList<Node> nodes = nodeList;
            nodes.clear();

            keyNodeList.clear();
            rootNodeList.clear();
        }

        /**
         * Adds a view to the graph.
         *
         * @param view The view to be added as a node to the graph.
         */
        void add(View view) {
            final int id = view.getId();
            final Node node = new Node(view);

            if (id != View.NO_ID) {
                keyNodeList.put(id, node);
            }

            nodeList.add(node);
        }

        /**
         * Builds a sorted list of views. The sorting order depends on the dependencies
         * between the view. For instance, if view C needs view A to be processed first
         * and view A needs view B to be processed first, the dependency graph
         * is: B -> A -> C. The sorted array will contain views B, A and C in this order.
         *
         * @param rules The list of rules to take into account.
         */
        void verifyHierarchyGraph(int... rules) {
//            final SparseArray<Node> roots = findRootList(rules);
//            int index = 0;
//
//            int childCount = getChildCount();
//            View[] sorted=new View[childCount];
//            while (roots.size() > 0) {
//                final Node node = roots.removeFirst();
//                final View view = node.view;
//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//
//                final int key = view.getId();
//
//                sorted[index++] = view;
//
//                final HashSet<Node> dependents = node.dependents;
//                for (Node dependent : dependents) {
//                    final SparseArray<Node> dependencies = dependent.dependencies;
//
//                    dependencies.remove(key);
//                    if (dependencies.size() == 0) {
//                        roots.add(dependent);
//                    }
//                }
//            }
//            if (index < sorted.length) {
//                throw new IllegalStateException("Circular dependencies cannot exist"
//                        + " in SimpleConstraintLayout");
//            }
        }

        /**
         * Finds the roots of the graph. A root is a node with no dependency and
         * with [0..n] dependents.
         *
         * @param rulesFilter The list of rules to consider when building the
         *        dependencies
         *
         * @return A list of node, each being a root of the graph
         */
        private SparseArray<Node> findRootList(int[] rulesFilter) {
            final SparseArray<Node> keyNodes = keyNodeList;
            final ArrayList<Node> nodes = nodeList;
            final int count = nodes.size();

            // Find roots can be invoked several times, so make sure to clear
            // all dependents and dependencies before running the algorithm
            for (int i = 0; i < count; i++) {
                final Node node = nodes.get(i);
                node.dependents.clear();
                node.dependencies.clear();
            }

            // Builds up the dependents and dependencies for each node of the graph
            for (int i = 0; i < count; i++) {
                final Node node = nodes.get(i);

                final LayoutParams layoutParams = (LayoutParams) node.view.getLayoutParams();
                final int[] rules = layoutParams.rules;
                final int rulesCount = rulesFilter.length;

                // Look only the the rules passed in parameter, this way we build only the
                // dependencies for a specific set of rules
                for (int j = 0; j < rulesCount; j++) {
                    final int rule = rules[rulesFilter[j]];
                    if (rule > 0) {
                        // The node this node depends on
                        final Node dependency = keyNodes.get(rule);
                        // Skip unknowns and self dependencies
                        if (dependency == null || dependency == node) {
                            continue;
                        }
                        // Add the current node as a dependent
                        dependency.dependents.put(rule,node);
                        // Add a dependency to the current node
                        node.dependencies.put(rule, dependency);
                    }
                }
            }

            final SparseArray<Node> rootNodeList = this.rootNodeList;
            rootNodeList.clear();

            // Finds all the roots in the graph: all nodes with no dependencies
            for (int i = 0; i < count; i++) {
                final Node node = nodes.get(i);
                LayoutParams layoutParams = (LayoutParams) node.view.getLayoutParams();
                if(layoutParams.rules[LEFT_TO_LEFT]==PARENT){
                    rootNodeList.put(LEFT_TO_LEFT,node);
                }
                if(layoutParams.rules[TOP_TO_TOP]==PARENT){
                    rootNodeList.put(TOP_TO_TOP,node);
                }
                if(layoutParams.rules[RIGHT_TO_RIGHT]==PARENT){
                    rootNodeList.put(RIGHT_TO_RIGHT,node);
                }
                if(layoutParams.rules[BOTTOM_TO_BOTTOM]==PARENT){
                    rootNodeList.put(BOTTOM_TO_BOTTOM,node);
                }
            }
            return rootNodeList;
        }

        /**
         * A node in the dependency graph. A node is a view, its list of dependencies
         * and its list of dependents.
         * <p>
         * A node with no dependent is considered a root of the graph.
         */
        class Node {
            /**
             * The view representing this node in the layout.
             */
            View view;
            /**
             * The list of dependents for this node; a dependent is a node
             * that needs this node to be processed first.
             */
            final SparseArray<Node> dependents = new SparseArray<>();
            /**
             * The list of dependencies for this node.
             */
            final SparseArray<Node> dependencies = new SparseArray<>();

            public Node(View view) {
                this.view = view;
            }
        }
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link ViewGroup.LayoutParams#WRAP_CONTENT} and no spanning.
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /**
     * Per-child layout information associated with RelativeLayout.
     *
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintLeft_toLeftOf
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintLeft_toRightOf
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintRight_toRightOf
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintRight_toLeftOf
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintTop_toTopOf
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintTop_toBottomOf
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintBottom_toTopOf
     * @attr ref R.styleable#SimpleConstraintLayout_layout_simple_constraintBottom_toBottomOf
     */
    public static class LayoutParams extends MarginLayoutParams {
        private int[] rules = new int[RULE_COUNT];
        private int verticalChainStyle=0;
        private int horizontalChainStyle=0;

        private int left, top, right, bottom;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            Arrays.fill(rules,NONE);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SimpleConstraintLayout_Layout);
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_left_toLeftOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(LEFT_TO_LEFT, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                } else if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_left_toRightOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(LEFT_TO_RIGHT, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                } else if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_top_toTopOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(TOP_TO_TOP, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                } else if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_top_toBottomOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(TOP_TO_BOTTOM, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                } else if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_right_toRightOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(RIGHT_TO_RIGHT, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                } else if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_right_toLeftOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(RIGHT_TO_LEFT, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                } else if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_bottom_toBottomOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(BOTTOM_TO_BOTTOM, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                } else if (attr == R.styleable.SimpleConstraintLayout_Layout_layout_controller_bottom_toTopOf) {
                    int id = a.getResourceId(attr, View.NO_ID);
                    addRule(BOTTOM_TO_TOP, View.NO_ID!=id?id:a.getInt(attr, View.NO_ID));
                }
            }
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * Adds a layout rule to be interpreted by the SimpleConstraintLayout. Use this for
         * verbs that take a target, such as a sibling (ALIGN_RIGHT) or a boolean
         * value (VISIBLE).
         */
        public void addRule(int verb, int anchor) {
            rules[verb] = anchor;
        }

        /**
         * Retrieves a complete list of all supported rules, where the index is the rule
         * verb, and the element value is the value specified, or "false" if it was never
         * set.
         *
         * @return the supported rules
         * @see #addRule(int, int)
         */
        public int[] getRules() {
            return rules;
        }
    }

}
