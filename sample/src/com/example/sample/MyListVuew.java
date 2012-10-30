package com.example.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 30.10.12
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class MyListVuew extends ListView{
    public MyListVuew(Context context) {
        super(context);
    }

    public MyListVuew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListVuew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("MYLISTVIEW", "" + MeasureSpec.getSize(widthMeasureSpec));
        int measureSpec = MeasureSpec.getMode(widthMeasureSpec);
        switch (measureSpec){
            case MeasureSpec.AT_MOST:
                Log.d("MYLISTVIEW", "AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                Log.d("MYLISTVIEW", "EXACTLY");
                break;
            default:
                Log.d("MYLISTVIEW", "UNSPECIFIED");
                break;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
