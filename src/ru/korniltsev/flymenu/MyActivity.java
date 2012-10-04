package ru.korniltsev.flymenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MyActivity extends Activity {

    FlyInMenuLayout mMenuContainer;


    String [] data = {"qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ","qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ","qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - ", "qweqweqwe", "lorem ipsum so", " ", " - - - "};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);

        ListView l = (ListView) findViewById(R.id.list1);
        l.setAdapter(adapter);
        ListView m = (ListView) findViewById(R.id.menu);
        m.setAdapter(adapter);
        mMenuContainer = (FlyInMenuLayout) findViewById(R.id.root);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenuContainer.toggle();
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mMenuContainer.isOpened())
            mMenuContainer.close();
        else
            super.onBackPressed();
    }
}
