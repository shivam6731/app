package com.foodpanda.urbanninja.ui.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
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
    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;
    private boolean mAttachedToWindow;
    private boolean mFirstLayout = true;
    private boolean mInLayout;
    private ObjectAnimator mExpandAnimator;
    private OnExpandListener mListener;

    public ExpandableLayout(Context context) {
        super(context);
        this.init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ExpandableLayout(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        this.setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        View child = findExpandableView();
        if (child != null) {
            LayoutParams p = (LayoutParams) child.getLayoutParams();

            if (p.weight != 0) {

                throw new IllegalArgumentException(
                    "ExpandableView can't use weight");
            }

            if (!p.isExpanded && !p.isExpanding) {
                child.setVisibility(View.GONE);
            } else {
                child.setVisibility(View.VISIBLE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        super.onLayout(changed, l, t, r, b);
        mInLayout = false;
        mFirstLayout = false;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        View child = findExpandableView();
        if (mExpandAnimator != null && mExpandAnimator.isRunning()) {
            mExpandAnimator.end();
            mExpandAnimator = null;
        }
        if (child != null) {
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            if (p.isExpanded) {
                p.height = p.originalHeight;
                child.setVisibility(View.VISIBLE);
            } else {
                p.height = p.originalHeight;
                child.setVisibility(View.GONE);
            }
            p.isExpanding = false;
        }
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    public View findExpandableView() {
        for (int i = 0; i < this.getChildCount(); i++) {
            LayoutParams p = (LayoutParams) this.getChildAt(i)
                .getLayoutParams();
            if (p.canExpand) {

                return this.getChildAt(i);
            }
        }

        return null;
    }

    boolean checkExpandableView(View expandableView) {
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
        if (child != null) {
            if (isExpanded != this.isExpanded()) {
                if (isExpanded) {
                    result = this.expand(child, shouldAnimate);
                } else {
                    result = this.collapse(child, shouldAnimate);
                }
            }
        }
        this.requestLayout();

        return result;
    }

    private boolean expand(View child, boolean shouldAnimate) {
        boolean result = false;
        if (!checkExpandableView(child)) {

            throw new IllegalArgumentException(
                "expand(), View is not expandableView");
        }
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (mFirstLayout || mAttachedToWindow == false || !shouldAnimate) {
            p.isExpanded = true;
            p.isExpanding = false;
            p.height = p.originalHeight;
            child.setVisibility(View.VISIBLE);
            result = true;
        } else {
            if (!p.isExpanded && !p.isExpanding) {
                this.playExpandAnimation(child);
                result = true;
            }
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
        this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
        final int measuredHeight = child.getMeasuredHeight();
        p.height = 0;

        mExpandAnimator = ObjectAnimator.ofInt(p, "height", 0, measuredHeight);
        mExpandAnimator.setDuration(getContext().getResources().getInteger(
            android.R.integer.config_shortAnimTime));
        mExpandAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dispatchOffset(child);
                child.requestLayout();
            }
        });
        mExpandAnimator.addListener(new AnimatorListener() {

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
        mExpandAnimator.start();
    }

    private boolean collapse(View child, boolean shouldAnimation) {
        boolean result = false;
        if (!checkExpandableView(child)) {

            throw new IllegalArgumentException(
                "collapse(), View is not expandableView");
        }
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (mFirstLayout || mAttachedToWindow == false || !shouldAnimation) {
            p.isExpanded = false;
            p.isExpanding = false;
            p.height = p.originalHeight;
            child.setVisibility(View.GONE);
            result = true;
        } else {
            if (p.isExpanded && !p.isExpanding) {
                this.playCollapseAnimation(child);
                result = true;
            }
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
        this.measure(mWidthMeasureSpec, mHeightMeasureSpec);
        final int measuredHeight = child.getMeasuredHeight();

        mExpandAnimator = ObjectAnimator.ofInt(p, "height", measuredHeight, 0);
        mExpandAnimator.setDuration(getContext().getResources().getInteger(
            android.R.integer.config_shortAnimTime));
        mExpandAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dispatchOffset(child);
                child.requestLayout();
            }
        });
        mExpandAnimator.addListener(new AnimatorListener() {

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
        mExpandAnimator.start();
    }

    private void dispatchOffset(View child) {
        if (mListener != null) {
            mListener.onExpandOffset(this, child, child.getHeight(),
                !isExpanded());
        }
    }

    private void performToggleState(View child) {
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p.isExpanded) {
            p.isExpanded = false;
            if (mListener != null) {
                mListener.onToggle(this, child, false);
            }
            child.setVisibility(View.GONE);
            p.height = p.originalHeight;
        } else {
            p.isExpanded = true;
            if (mListener != null) {
                mListener.onToggle(this, child, true);
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
        return new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT);
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
            TypedArray a = c.obtainStyledAttributes(attrs,
                R.styleable.ExpandableLayout);
            canExpand = a.getBoolean(R.styleable.ExpandableLayout_canExpand,
                false);
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

        void onExpandOffset(ExpandableLayout view, View child,
                            float offset, boolean isExpanding);
    }
}
