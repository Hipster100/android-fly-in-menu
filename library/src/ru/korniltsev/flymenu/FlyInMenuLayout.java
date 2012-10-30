package ru.korniltsev.flymenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created with IntelliJ IDEA.
 * User: Anatoly Korniltsev
 * Date: 24.09.12
 * Time: 17:21
 */
public class FlyInMenuLayout extends FrameLayout {
    /**
     * animation duration in miliseconds
     */
    private static final int ANIMATION_DURATION = 400;
    /**
     * UI update interval
     * defines fps
     */
    private static final int ANIMATION_INTERVAL = 16;

    /**
     * left and right offset
     */
    private static final int TOUCH_OFFSET = 44;//dp

    /**
     * Host is a view holding the main content
     */
    private View mHost;
    /**
     * View that would be shown on swype or by colled {@link #toggle() toggle} or {@link #open() open} methods
     */
    private View mMenu;

    private View menuShadow;
    private View hostShadow;
    /**
     * Width ow the view
     */
    private int mWidth;

    /**
     * Current offset
     * 0 if menu is closed
     * >0 if opened/swiping
     */
    private int mOffset;

    /**
     * max offset of the host view
     */
    private int maxOffset;


    /**
     * represents current menu state
     */
    private boolean mMenuOpened = false;

    /**
     * represents current animation state
     */
    private boolean mAnimating = false;

    /**
     * helps to simulate animations - calculates offset by time
     */
    private final Scroller mScroller;

    /**
     * Touch move distance to consider the gesture as swipe
     * This helps to filter noize on click
     */
    private final float touchSlop;

    /**
     *
     */
    private final float touchArea;

    /**
     *
     */
    float lastTouchX;

    /**
     *
     */
    float lastDownX;

    /**
     * Animation implementation
     * shifts the views on animation/swiping
     * Todo callbacks
     */
    private Runnable mPositionUpdater = new Runnable() {
        @Override
        public void run() {
            mScroller.computeScrollOffset();
            if (mScroller.getCurrX() != mScroller.getFinalX())
                performAnimation();
            else
                mAnimating = false;
            shift(mScroller.getCurrX() - mOffset);
            mOffset = mScroller.getCurrX();
            mMenuOpened = mOffset != 0;
        }
    };

    public FlyInMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(getContext(), new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return (float) (Math.pow(v-1, 5) + 1);
            }
        });
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        touchArea = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TOUCH_OFFSET, context.getResources().getDisplayMetrics()
        );
        menuShadow = new View(context);
        menuShadow.setBackgroundColor(0x33fa0000);
        hostShadow = new View(context);
        hostShadow.setBackgroundColor(0);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHost == null || mMenu == null)
            throw new IllegalStateException("host and menu should be set ");

        if (changed) {
            mWidth = right - left;
            maxOffset = (int) (mWidth - touchArea);
            //TODO test changed
        }
        mHost.layout(left, top, right, bottom);
        hostShadow.layout(left, top, right, bottom);

        mMenu.layout(left  , top, right, bottom);
        menuShadow.layout(left - maxOffset, top, left, bottom);
    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        saveView(child);
    }

    /**
     * looking for host and menu views
     * @param child
     */
    private void saveView(View child) {
        Object tag = child.getTag();
        if ("host".equals(tag)){
            mHost = child;
            addView(hostShadow);
        } else if ("menu".equals(tag)){
            mMenu = child;
            addView(menuShadow);
        }
    }

    /**
     * toggles open state
     */
    public void toggle() {
        if (!mAnimating) {
//            mMenu.invalidate();
            if (mMenuOpened)
                close();
            else
                open();
        }
    }

    /**
     * closes menu
     */
    public void close() {
        mScroller.startScroll(mOffset, 0, -mOffset, 0, (int) (ANIMATION_DURATION * (float) mOffset / maxOffset));
        mAnimating = true;
        performAnimation();
    }

    /**
     * opens menu
     */
    public void open() {
        mScroller.startScroll(mOffset, 0, maxOffset - mOffset, 0,
                (int) (ANIMATION_DURATION * (float) (maxOffset - mOffset) / maxOffset));
        mAnimating = true;
        performAnimation();
    }

    public boolean isOpened(){
        return !mAnimating && mMenuOpened;
    }

    public boolean isAnimating(){
        return mAnimating;
    }

    /**
     * animation simalation
     * @param offset
     */
    private void shift(int offset) {
        setupShadow();
        invalidate();
        mHost.offsetLeftAndRight(offset);
        menuShadow.offsetLeftAndRight(offset);

    }

    /**
     * calculates shadow on menu
     */
    private void setupShadow() {
        int def = 0x88;
        int newShadowColor = (int) (-def * ((float)mOffset/maxOffset) + def) << 24; //0x??000000
        menuShadow.setBackgroundColor(newShadowColor);
    }

    /**
     * simulating animation
     * changes views offset every 16 ms
     */
    private void performAnimation() {
        postDelayed(mPositionUpdater, ANIMATION_INTERVAL);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;

        if (action != MotionEvent.ACTION_MOVE)
            Log.d("SLIDER=intercept", ev.toString());

        //if animating, child views won't get any MotionEvent
        if (mAnimating)
            return true;
        // intercept touches on visible host view
        if (mMenuOpened && ev.getX() >= mOffset) return true;
        // intercept only after swipe
        if (!mMenuOpened && ev.getX() < touchArea) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastDownX = ev.getX();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    //TODO this can be passed to onTouchEvent
                    if (ev.getX() - lastDownX > touchSlop) {
                        lastTouchX = ev.getX();
                        mAnimating = true;
                        return true;
                    }
            }
            return false;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

//        if (action != MotionEvent.ACTION_MOVE)
//            Log.d("SLIDER=intercept", event.toString());

        //ignore touches during animation
        if (mAnimating && action == MotionEvent.ACTION_DOWN)
            return false;

        float newTouchX = event.getX();
        if (!mAnimating)
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    lastDownX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //start animating only after a finger moved more than touchSlop
                    if (lastDownX - event.getX() > touchSlop)
                        mAnimating = true;
                    break;
                case MotionEvent.ACTION_UP:
                    //did not start animating
                    // looks like the user clicked on host view
                    close();
                    break;
            }
        else
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    //user moves the finger over the screen - we move view
                    int dx = (int) (newTouchX - lastTouchX + 0.5);
                    if (mOffset + dx >= 0 && mOffset + dx <= maxOffset) {
                        mOffset += dx;
                        shift(dx);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mAnimating = false;
                    //the user has taken off finger so we animate the hostView to the closest border
                    if (mMenuOpened && newTouchX < mWidth * 3f / 4f
                            || !mMenuOpened && newTouchX < mWidth / 4f)
                        close();
                    else
                        open();
                    break;
            }

        lastTouchX = newTouchX;
        return true;
    }



}
