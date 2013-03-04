package com.example.sample;

import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup;
import ru.korniltsev.flymenu.DoubleSideFlyInMenuLayout;
import ru.korniltsev.flymenu.R;

public class MyActivity extends BaseActivity {
    public static final int SIDE_BAR_ID = 666;
    private DoubleSideFlyInMenuLayout mMenu;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setMenuView(R.layout.menu);
        mMenu = new DoubleSideFlyInMenuLayout(this);
        mMenu.setId(SIDE_BAR_ID);
        setUpSliding(mMenu);
        mMenu.setMenuWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenu.setShadowDrawable(getResources().getDrawable(R.drawable.shadow));
//        menu.setAlignMenuRight(true);
        if (savedInstanceState!= null && savedInstanceState.getBoolean("opened", false)){
            setMenuOpened();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("opened", mMenu.isOpened());
    }
}
