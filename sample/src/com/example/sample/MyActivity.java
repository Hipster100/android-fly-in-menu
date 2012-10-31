package com.example.sample;

import android.os.Bundle;
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
        setMenuView(R.layout.menu);
        setUpSliding(R.layout.slider_layout);
        if (savedInstanceState!= null && savedInstanceState.getBoolean("opened", false))
            setMenuOpened();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
            outState.putBoolean("opened", getContainer().isOpened());
    }


}
