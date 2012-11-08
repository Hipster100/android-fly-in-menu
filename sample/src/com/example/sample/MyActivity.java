package com.example.sample;

import android.os.Bundle;
import android.view.Menu;
import ru.korniltsev.flymenu.BaseActivity;
import ru.korniltsev.flymenu.R;

public class MyActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
//        View child = decorView.getChildAt(0);
//        decorView.removeView(child);
//
//        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        ThirdTry container = new ThirdTry(this);
//
//        container.setLayoutParams(p);
//        child.setLayoutParams(p);
//        decorView.addView(container);
//        container.addView(child);
//
//
//        container.setBackgroundColor(0xefefefef);
        setMenuView(R.layout.menu);
        setUpSliding(R.layout.slider_layout);
        if (savedInstanceState!= null && savedInstanceState.getBoolean("opened", false))
            setMenuOpened();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
