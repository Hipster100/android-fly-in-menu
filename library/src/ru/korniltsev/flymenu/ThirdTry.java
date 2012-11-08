package ru.korniltsev.flymenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 06.11.12
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class ThirdTry extends FrameLayout {
    public ThirdTry(Context context) {
        super(context);
    }

    public ThirdTry(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        View child = getChildAt(0);
        int wms = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int hms = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
//        child.measure(wms, hms);

        setMeasuredDimension(width, height);
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l , t, r, b);
        if (changed) {
            int menuWidth = r - l;
            View child = getChildAt(0);
            child.layout(l, t, r, b);
        }
    }
}
