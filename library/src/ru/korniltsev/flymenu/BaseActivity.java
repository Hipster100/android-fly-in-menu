package ru.korniltsev.flymenu;

import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;


public class BaseActivity extends FragmentActivity {

    View menu;
    DoubleSideFlyInMenuLayout container;

    public final void setMenuView(int resId){
        menu = getLayoutInflater().inflate(resId, null);
    }

    public final void setUpSliding(){
        setUpSliding(new DoubleSideFlyInMenuLayout(this));
    }

    public final void setUpSliding(int containerId){
        setUpSliding((DoubleSideFlyInMenuLayout) getLayoutInflater().inflate(containerId, null));
    }

    public final void setUpSliding(DoubleSideFlyInMenuLayout l){
        container = l;
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decor.removeView(decorChild);

        container.addView(menu);
        container.addView(decorChild);
        menu.setTag("menu");
        decorChild.setTag("host");

        decor.addView(container, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));

        TypedArray a = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowBackground});
        int background = a.getResourceId(0, 0);
        decorChild.setBackgroundResource(background);
        a.recycle();
    }




}
