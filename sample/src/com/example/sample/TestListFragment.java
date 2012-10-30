package com.example.sample;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 29.10.12
 * Time: 8:29
 * To change this template use File | Settings | File Templates.
 */
public class TestListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(ru.korniltsev.flymenu.R.layout.fragment_test_list, container, false);
        ListView v = (ListView) ret.findViewById(android.R.id.list);
        v.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 100;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView ret = new TextView(getActivity());
                ret.setText(position + "");
                ret.setBackgroundResource(ru.korniltsev.flymenu.R.drawable.aka_selector);
                ret.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return ret;
            }
        });
        return ret;
    }
}
