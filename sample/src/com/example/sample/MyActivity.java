package com.example.sample;

import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup;
import ru.korniltsev.flymenu.DoubleSideFlyInMenuLayout;
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
        DoubleSideFlyInMenuLayout menu = new DoubleSideFlyInMenuLayout(this);
        setUpSliding(menu);
        menu.setMenuWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        menu.setAlignMenuRight(true);
        if (savedInstanceState!= null && savedInstanceState.getBoolean("opened", false)){
            setMenuOpened();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
