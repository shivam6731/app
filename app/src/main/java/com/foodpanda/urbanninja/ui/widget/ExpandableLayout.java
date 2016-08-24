package com.foodpanda.urbanninja.ui.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.foodpanda.urbanninja.R;

public class ExpandableLayout extends LinearLayout {
    private int widthMeasureSpec;
    private int heightMeasureSpec;
    private boolean attachedToWindow;
    private boolean firstLayout = true;
    private boolean inLayout;
    private ObjectAnimator expandAnimator;
    private OnExpandListener listener;

    public ExpandableLayout(Context context) {
        super(context);
        this.init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        this.setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;

        View child = findExpandableView();
        if (child != null) {
            LayoutParams p = (LayoutParams) child.getLayoutParams();

            if (p.weight != 0) {
                throw new IllegalArgumentException("ExpandableView can't use weight");
            }

            int visibility = (!p.isExpanded && !p.isExpanding) ? GONE : VISIBLE;
            child.setVisibility(visibility);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        inLayout = true;
        super.onLayout(changed, l, t, r, b);
        inLayout = false;
        firstLayout = false;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        attachedToWindow = false;
        View child = findExpandableView();

        if (expandAnimator != null && expandAnimator.isRunning()) {
            expandAnimator.end();
            expandAnimator = null;
        }

        if (child != null) {
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            p.height = p.originalHeight;
            int visibility = p.isExpanded ? VISIBLE : GONE;
            child.setVisibility(visibility);
            p.isExpanding = false;
        }
    }

    @Override
    public void requestLayout() {
        if (!inLayout) {
            super.requestLayout();
        }
    }

    public View findExpandableView() {
        for (int i = 0; i < this.getChildCount(); i++) {
            LayoutParams p = (LayoutParams) this.getChildAt(i).getLayoutParams();
            if (p.canExpand) {
                return this.getChildAt(i);
            }
        }

        return null;
    }

    boolean isExpandableView(View expandableView) {
        LayoutParams p = (LayoutParams) expandableView.getLayoutParams();

        return p.canExpand;
    }

    public boolean isExpanded() {
        View child = findExpandableView();
        if (child != null) {
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            if (p.isExpanded) {
                return true;
            }
        }

        return false;
    }

    public boolean toggleExpansion() {
        return this.setExpanded(!isExpanded(), true);
    }

    public boolean setExpanded(boolean isExpanded, boolean shouldAnimate) {
        boolean result = false;
        View child = findExpandableView();
        if (child != null && isExpanded != this.isExpanded()) {
            result = isExpanded ?
                this.expand(child, shouldAnimate) :
                this.collapse(child, shouldAnimate);
        }
        this.requestLayout();

        return result;
    }

    private boolean expand(View child, boolean shouldAnimate) {
        boolean result = false;

        if (!isExpandableView(child)) {
            throw new IllegalArgumentException("expand(), View is not expandableView");
        }

        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (firstLayout || !attachedToWindow || !shouldAnimate) {
            p.isExpanded = true;
            p.isExpanding = false;
            p.height = p.originalHeight;
            child.setVisibility(View.VISIBLE);
            result = true;
        } else if (!p.isExpanded && !p.isExpanding) {
            this.playExpandAnimation(child);
            result = true;
        }

        return result;
    }

    private void playExpandAnimation(final View child) {
        final LayoutParams p = (LayoutParams) child.getLayoutParams();

        if (p.isExpanding) {
            return;
        }

        child.setVisibility(View.VISIBLE);
        p.isExpanding = true;
        measure(widthMeasureSpec, heightMeasureSpec);

        final int measuredHeight = child.getMeasuredHeight();
        p.height = 0;

        expandAnimator = ObjectAnimator.ofInt(p, "height", 0, measuredHeight);
        expandAnimator.setDuration(
            getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime
            )
        );
        expandAnimator.addUpdateListener(animation -> {
            dispatchOffset(child);
            child.requestLayout();
        });
        expandAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                performToggleState(child);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        expandAnimator.start();
    }

    private boolean collapse(View child, boolean shouldAnimation) {
        boolean result = false;

        if (!isExpandableView(child)) {
            throw new IllegalArgumentException("collapse(), View is not expandableView");
        }

        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (firstLayout || !attachedToWindow || !shouldAnimation) {
            p.isExpanded = false;
            p.isExpanding = false;
            p.height = p.originalHeight;
            child.setVisibility(View.GONE);
            result = true;
        } else if (p.isExpanded && !p.isExpanding) {
            this.playCollapseAnimation(child);
            result = true;
        }

        return result;
    }

    private void playCollapseAnimation(final View child) {
        final LayoutParams p = (LayoutParams) child.getLayoutParams();

        if (p.isExpanding) {
            return;
        }

        child.setVisibility(View.VISIBLE);
        p.isExpanding = true;

        measure(widthMeasureSpec, heightMeasureSpec);
        final int measuredHeight = child.getMeasuredHeight();

        expandAnimator = ObjectAnimator.ofInt(p, "height", measuredHeight, 0);
        expandAnimator.setDuration(
            getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime
            )
        );
        expandAnimator.addUpdateListener(animation -> {
            dispatchOffset(child);
            child.requestLayout();
        });
        expandAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                performToggleState(child);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        expandAnimator.start();
    }

    private void dispatchOffset(View child) {
        if (listener != null) {
            listener.onExpandOffset(this, child, child.getHeight(), !isExpanded());
        }
    }

    private void performToggleState(View child) {
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p.isExpanded) {
            p.isExpanded = false;
            if (listener != null) {
                listener.onToggle(this, child, false);
            }
            child.setVisibility(View.GONE);
            p.height = p.originalHeight;
        } else {
            p.isExpanded = true;
            if (listener != null) {
                listener.onToggle(this, child, true);
            }
        }
        p.isExpanding = false;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(
        android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && (p instanceof LayoutParams);
    }

    public class LayoutParams extends LinearLayout.LayoutParams {
        private static final int NO_MESURED_HEIGHT = -10;
        int originalHeight = NO_MESURED_HEIGHT;
        boolean isExpanded;
        boolean canExpand;
        boolean isExpanding;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
            canExpand = a.getBoolean(R.styleable.ExpandableLayout_canExpand, false);
            originalHeight = this.height;
            a.recycle();
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
            originalHeight = this.height;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            originalHeight = this.height;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
            originalHeight = this.height;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(android.widget.LinearLayout.LayoutParams source) {
            super(source);
            originalHeight = this.height;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            originalHeight = this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public interface OnExpandListener {
        void onToggle(ExpandableLayout view, View child, boolean isExpanded);

        void onExpandOffset(ExpandableLayout view, View child, float offset, boolean isExpanding);
    }

    public void setOnExpandListener(OnExpandListener listener) {
        this.listener = listener;
    }
}
