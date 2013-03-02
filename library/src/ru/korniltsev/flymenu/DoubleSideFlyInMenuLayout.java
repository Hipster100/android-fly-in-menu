package ru.korniltsev.flymenu;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 29.10.12
 * Time: 7:58
 * TODO gradient shadows http://stackoverflow.com/questions/2936803/how-to-draw-a-smooth-dithered-gradient-on-a-canvas-in-android
 */
public class DoubleSideFlyInMenuLayout extends RelativeLayout {


    public static final String TAG = "DoubleSideFlyInMenuLayout";
    private static final int ANIMATION_INTERVAL = 16;
    private static final int ANIMATION_DURATION = 300;
    public static final int DEFAULT_MENU_MARGIN = 44;

    private int mMenuMargin;
    private boolean mAlignMenuRight;
    private View mMenu;
    private View mHost;
    private final float touchArea;
    private final int touchSlop;
    private final float speedThreshold;


    private Scroller mScroller;
    private MenuMode mMenuMode = MenuMode.NORMAL;

    /**
     * useful if you want the menu to be opened on activity creation
     */
    boolean shouldBeOpenedOnLayout;

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

                if (mMenuMode == MenuMode.PERSPECTIVE) {
                    int menuDx;
                    if (isOpened()) {
                        mMenu.setVisibility(View.VISIBLE);
                        menuDx = (mAlignMenuRight ? mMenuMargin : 0) - mMenu.getLeft();
                    } else {
                        int menuWidth = getWidth() - mMenuMargin;
                        menuDx = (mAlignMenuRight ? mMenuMargin + menuWidth / 2 : -menuWidth / 2) - mMenu.getLeft();
                        mMenu.setVisibility(View.GONE);
                    }
                    mMenu.offsetLeftAndRight(menuDx);
                }
                requestLayout();
            }
        }
    };
    private int minOffset;
    private int maxOffset;
    private boolean widthSetManually;
    private int mMenuWidth;


    public DoubleSideFlyInMenuLayout(Context context) {
        super(context);
        touchArea = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                44, context.getResources().getDisplayMetrics()
        );
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context, new DecelerateInterpolator());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MENU_MARGIN,
                getContext().getResources().getDisplayMetrics());
        mMenuMargin = margin % 2 == 0 ? margin : margin + 1;
        speedThreshold = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();

    }

    /**
     * simulating animation
     * changes views offset every 16 ms
     */
    private void performAnimation() {
        postDelayed(mPositionUpdater, ANIMATION_INTERVAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        findViews();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        measureChild(mHost, wms, hms);


        if (mMenuMode == MenuMode.NORMAL){
            wms = MeasureSpec.makeMeasureSpec(mMenuWidth, MeasureSpec.EXACTLY);
            hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            measureChild(mMenu, wms, hms);

            minOffset = mAlignMenuRight ? -mMenu.getMeasuredWidth() : 0;
            maxOffset = mAlignMenuRight ? 0 : mMenu.getMeasuredWidth();
        } else {
            wms = MeasureSpec.makeMeasureSpec(width - mMenuMargin, MeasureSpec.EXACTLY);
            hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            measureChild(mMenu, wms, hms);
            int menuWidth = width - mMenuMargin;
            minOffset = mAlignMenuRight ? -menuWidth : 0;
            maxOffset = mAlignMenuRight ? 0 : menuWidth;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAnimating)
            return;

        super.onLayout(changed, l, t, r, b);
        int menuWidth = r - l - mMenuMargin;

        mHost.layout(l + mOffset, t, r + mOffset, b);
        int menuPerspectiveOffset = menuWidth / 2;
        if (mMenuMode == MenuMode.PERSPECTIVE) {
            if (mAlignMenuRight) {
                if (isOpened()) {
                    mMenu.layout(mMenuMargin, t, r, b);
                } else {
                    mMenu.layout(mMenuMargin + menuPerspectiveOffset, t, r + menuPerspectiveOffset, b);
                }
            } else {
                if (isOpened()) {
                    mMenu.layout(l, t, r - mMenuMargin, b);
                } else {
                    mMenu.layout(l - menuPerspectiveOffset, t, r - mMenuMargin - menuPerspectiveOffset, b);
                }
            }
        } else {//mode normal mode
            if (mAlignMenuRight) {
                mMenu.layout(r-l-mMenu.getMeasuredWidth(), t, r, b);
            } else {
                mMenu.layout(l, t, l+ mMenu.getMeasuredWidth(), b);
            }
        }


        if (shouldBeOpenedOnLayout && !isOpened()) {
            mMenu.setVisibility(View.VISIBLE);
            int direction = mAlignMenuRight ? -1 : 1;
            mOffset += direction * menuWidth;
            mHost.offsetLeftAndRight(direction * menuWidth);
            if (mMenuMode == MenuMode.PERSPECTIVE) {
                mMenu.offsetLeftAndRight(direction * menuPerspectiveOffset);
            }
        }
        shouldBeOpenedOnLayout = false;
    }


    @Override
    protected boolean fitSystemWindows(Rect insets) {
        windowTopInset = insets.top;//TODO this insets should be set on the view itself not menu
        return super.fitSystemWindows(insets);
    }

    private boolean mAnimating;

    private int mLastDownX;
    private int lastTouchX;
    private boolean waitSlope;

    private int mOffset;


    public boolean isOpened() {
        return mOffset != 0;
    }


    VelocityTracker mFlingTracker = VelocityTracker.obtain();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;

        if (action != MotionEvent.ACTION_MOVE)
            Log.d(TAG, "onIntercept - " + ev.toString());

        if (action == MotionEvent.ACTION_UP)
            waitSlope = false;

        lastTouchX = (int) ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mFlingTracker.clear();
                if (shouldWaitForSlope(ev)) {

                    mLastDownX = (int) ev.getX();
                    waitSlope = true;
                    return isOpened();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (checkSlope(ev)) {
                    mMenu.setVisibility(View.VISIBLE);
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean checkSlope(MotionEvent ev) {
        boolean rightDirection;
        int xShift = (int) (ev.getX() - mLastDownX);
        if (mAlignMenuRight)
            rightDirection = xShift < 0;
        else
            rightDirection = xShift > 0;
        rightDirection = isOpened() ? !rightDirection : rightDirection;

        if (waitSlope && Math.abs(xShift) > touchSlop && rightDirection) {
            mAnimating = true;
            return true;
        }
        return false;
    }


    private boolean shouldWaitForSlope(MotionEvent ev) {
        if (isOpened()) {
            Rect hostRect = new Rect();
            mHost.getHitRect(hostRect);
            if (hostRect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
            else
                return false;
        } else {
            if (mAlignMenuRight)
                return ev.getX() > getWidth() - touchArea;
            else return ev.getX() < touchArea;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP)
            waitSlope = false;

        int prevTouchX = lastTouchX;
        lastTouchX = (int) event.getX();

        if (action != MotionEvent.ACTION_MOVE)
            Log.d(TAG, "onTouch - " + event.toString());


        if (action == MotionEvent.ACTION_DOWN) {
            mLastDownX = (int) event.getX();
        }


        if (mAnimating) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    mFlingTracker.addMovement(event);
                    int dx = lastTouchX - prevTouchX;
                    if (mOffset + dx < minOffset)
                        dx = minOffset - mOffset;
                    else if (mOffset + dx > maxOffset)
                        dx = maxOffset - mOffset;

                    shift(dx);
                    break;
                case MotionEvent.ACTION_UP:
                    mFlingTracker.computeCurrentVelocity(1000);
                    Log.d(TAG, "speed" + mFlingTracker.getXVelocity());
                    float speed = mFlingTracker.getXVelocity();
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
        } else {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    //start animating only after a finger moved more than touchSlop
                    if (checkSlope(event)) {
                        mAnimating = true;
                        mMenu.setVisibility(View.VISIBLE);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //did not start animating
                    // looks like the user clicked on host view
                    if (shouldWaitForSlope(event)) {
                        event.setLocation(mLastDownX, event.getY());
                        if (shouldWaitForSlope(event))
                            close();
                    }

                    break;
            }
        }
        return true;
    }

    public void setOpenedOnStart() {
        shouldBeOpenedOnLayout = true;
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
        mMenu.setVisibility(View.VISIBLE);
        int maxOffset = mMenu.getWidth() * (mAlignMenuRight ? -1 : 1);
        mScroller.startScroll(mOffset, 0, maxOffset - mOffset, 0,
                ANIMATION_DURATION);
        mAnimating = true;
        performAnimation();
    }

    /**
     * animation simulation
     *
     * @param dx
     */

    private void shift(int dx) {
        dx = dx % 2 == 0 ? dx : dx + 1;
        mOffset += dx;
        invalidate();
        mHost.offsetLeftAndRight(dx);
        if (mMenuMode == MenuMode.PERSPECTIVE) {
            mMenu.offsetLeftAndRight(dx / 2);
        }
    }

    boolean windowInsetsSet = false;
    int windowTopInset = 0;

    private void findViews() {
        mMenu = findViewWithTag("menu");
        mHost = findViewWithTag("host");
        if (mMenu == null || mHost == null)
            throw new IllegalStateException("You should add childs with 'host' and 'menu' tags");
        mHost.setClickable(true);
        mMenu.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        if (!windowInsetsSet && windowTopInset != 0) {//TODO
            int left = mMenu.getPaddingLeft();
            int right = mMenu.getPaddingRight();
            int top = mMenu.getPaddingTop();
            int bottom = mMenu.getPaddingBottom();
            mMenu.setPadding(left, top + windowTopInset, right, bottom);
            windowInsetsSet = true;
        }

    }


    public void setAlignMenuRight(boolean b) {
        this.mAlignMenuRight = b;
    }

    public void setMenuMargin(int px) {
        mMenuMargin = px;
        invalidate();
    }

    public void setMenuWidth(int width) {
        widthSetManually = true;
        mMenuWidth = width;
    }

    public void setMenuMode(MenuMode menuMode) {
        mMenuMode = menuMode;
    }

    public static enum MenuMode {
        PERSPECTIVE, NORMAL
    }
}
