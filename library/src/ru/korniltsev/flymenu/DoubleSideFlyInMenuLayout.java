package ru.korniltsev.flymenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 29.10.12
 * Time: 7:58
 * To change this template use File | Settings | File Templates.
 */
public class DoubleSideFlyInMenuLayout extends ViewGroup {


    public static final String TAG = "DoubleSideFlyInMenuLayout";
    private static final int ANIMATION_INTERVAL = 16;
    private static final int ANIMATION_DURATION = 300;
    public static final int DEFAULT_MENU_MARGIN = 44;
    public static final float DEFAULT_SPEED_THRESHOLD = 0.5f;
    private int mMenuMargin;
    private boolean mAlignMenuRight;
    private View mMenu;
    private View mHost;
    private final float touchArea;
    private final int touchSlop;
    private final float speedThreshold;


    private Scroller mScroller;
    /**
     * Animation implementation
     * shifts the views on animation/swiping
     * Todo callbacks
     */
    private Runnable mPositionUpdater = new Runnable() {
        @Override
        public void run() {
            mScroller.computeScrollOffset();
            int dx = mScroller.getCurrX() - mOffset;
            shift(dx);
            if (mScroller.getCurrX() != mScroller.getFinalX())
                performAnimation();
            else {
                mAnimating = false;
                int hostDX = mScroller.getFinalX() - mHost.getLeft();
                mHost.offsetLeftAndRight(hostDX);
                mOffset = mScroller.getFinalX();

                int menuDx;
                if (isOpened()) {
                    menuDx = (mAlignMenuRight ? mMenuMargin  : 0) - mMenu.getLeft();
                } else {
                    int menuWidth = getWidth() - mMenuMargin;
                    menuDx = (mAlignMenuRight ? mMenuMargin + menuWidth / 2 : -menuWidth / 2) - mMenu.getLeft();
                }
                mMenu.offsetLeftAndRight(menuDx);
            }
        }
    };
    private int minOffset;
    private int maxOffset;

    /**
     * simulating animation
     * changes views offset every 16 ms
     */
    private void performAnimation() {
        postDelayed(mPositionUpdater, ANIMATION_INTERVAL);
    }


    public DoubleSideFlyInMenuLayout(Context context) {
        super(context);
        touchArea = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                44, context.getResources().getDisplayMetrics()
        );
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context, new DecelerateInterpolator());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MENU_MARGIN,
                getContext().getResources().getDisplayMetrics());
        speedThreshold =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SPEED_THRESHOLD,
                getContext().getResources().getDisplayMetrics());
    }

    public DoubleSideFlyInMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchArea = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                44, context.getResources().getDisplayMetrics()
        );
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context, new DecelerateInterpolator());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MENU_MARGIN,
                getContext().getResources().getDisplayMetrics());
        mMenuMargin = margin % 2 == 0 ? margin : margin + 1;
        speedThreshold =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SPEED_THRESHOLD,
                getContext().getResources().getDisplayMetrics());

        TypedArray styles = context.obtainStyledAttributes(attrs, R.styleable.DoubleSideFlyInMenuLayout);
        try {
            mAlignMenuRight = styles.getBoolean(R.styleable.DoubleSideFlyInMenuLayout_align_menu_right, false);
            mMenuMargin = (int) styles.getDimension(R.styleable.DoubleSideFlyInMenuLayout_menu_margin,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44,
                            getContext().getResources().getDisplayMetrics()));

        } finally {
            styles.recycle();
        }
    }


    public void setMenuMargin(int px) {
        mMenuMargin = px;
        invalidate();
        throw new RuntimeException("untested method, remove exception and test!");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        findViews();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        measureChild(mHost, wms, hms);

        wms = MeasureSpec.makeMeasureSpec(width - mMenuMargin, MeasureSpec.EXACTLY);
        hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        measureChild(mMenu, wms, hms);

        int menuWidth = width - mMenuMargin;
        minOffset = mAlignMenuRight ? -menuWidth : 0;
        maxOffset = mAlignMenuRight ? 0 : menuWidth;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout");
        int menuPerspectiveOffset = (r - l - mMenuMargin) / 2;
        if (changed) {
            mHost.layout(l, t, r, b);
            if (mAlignMenuRight)
                mMenu.layout(mMenuMargin + menuPerspectiveOffset, t, r + menuPerspectiveOffset, b);
            else
                mMenu.layout(l - menuPerspectiveOffset, t, r - mMenuMargin - menuPerspectiveOffset, b);
        }
    }


    private boolean mAnimating;

    private int mLastDownX;
    private int lastTouchX;
    private boolean waitSlope;

    private int mOffset;


    private boolean isOpened() {
        return mOffset != 0;
    }

    long lastTime;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
//        if (action != MotionEvent.ACTION_MOVE)
//        Log.d(TAG, "onIntercept - " + ev.toString());

        if (action == MotionEvent.ACTION_UP)
            waitSlope = false;

        lastTouchX = (int) ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (shouldWaitForSlope(ev)) {
//                    Log.d(TAG, "Down" + ev.getX());
                    mLastDownX = (int) ev.getX();
                    waitSlope = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int xShift = (int) (ev.getX() - mLastDownX);
                boolean rightDirection;
                if (mAlignMenuRight)
                    rightDirection = xShift < 0;
                else
                    rightDirection = xShift > 0;
                rightDirection = isOpened() ? !rightDirection : rightDirection;

                if (waitSlope && Math.abs(xShift) > touchSlop && rightDirection) {
                    mAnimating = true;
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean shouldWaitForSlope(MotionEvent ev) {
        if (!isOpened() && ((mAlignMenuRight && ev.getX() > getWidth() - touchArea)
                || (!mAlignMenuRight && ev.getX() < touchArea)))
            return true;
        if (isOpened() && ((mAlignMenuRight && ev.getX() < getWidth() - mMenu.getWidth())
                || (!mAlignMenuRight && ev.getX() > mMenu.getWidth())))
            return true;
        return false;
    }


    float speed = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP)
            waitSlope = false;

        int prevTouchX = lastTouchX;
        lastTouchX = (int) event.getX();
        long prevTime = lastTime;
        lastTime = System.currentTimeMillis();

        if (mAnimating) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int dx = lastTouchX - prevTouchX;
                    speed = (dx + 0f) / (lastTime - prevTime);

                    if (mOffset + dx < minOffset)
                        dx = minOffset - mOffset;
                    else if (mOffset + dx > maxOffset)
                        dx = maxOffset - mOffset;

                    shift(dx);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "+" + speed);
                    if (Math.abs(speed) > speedThreshold) {
                        boolean shouldOpen = speed > 0;
                        if (mAlignMenuRight) shouldOpen = !shouldOpen;
                        if (shouldOpen) open();
                        else close();
                        Log.d(TAG, "animation by speed");
                    } else {
                        boolean shouldOpen = lastTouchX < getWidth() / 2;
                        shouldOpen = mAlignMenuRight ? shouldOpen : !shouldOpen;
                        if (shouldOpen)
                            open();
                        else close();
                    }
                    break;
            }
        }
        return true;
    }

    public void toggle() {
        if (isOpened())
            close();
        else open();
    }

    /**
     * closes menu
     */
    public void close() {
        mScroller.startScroll(mOffset, 0, -mOffset, 0, ANIMATION_DURATION);
        mAnimating = true;
        performAnimation();
    }

    /**
     * opens menu
     */
    public void open() {
        int maxOffset = mMenu.getWidth() * (mAlignMenuRight ? -1 : 1);
        mScroller.startScroll(mOffset, 0, maxOffset - mOffset, 0,
                ANIMATION_DURATION);
        mAnimating = true;
        performAnimation();
    }

    /**
     * animation simalation
     *
     * @param dx
     */

    private void shift(int dx) {
        dx = dx %2 ==0 ?dx : dx+1;
        mOffset += dx;
        invalidate();
        mHost.offsetLeftAndRight(dx);
        mMenu.offsetLeftAndRight(dx/2);
    }

    private void findViews() {
        mMenu = findViewWithTag("menu");
        mHost = findViewWithTag("host");
        if (mMenu == null || mHost == null)
            throw new IllegalStateException("You should add childs with 'host' and 'menu' tags");
        mMenu.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }


}
