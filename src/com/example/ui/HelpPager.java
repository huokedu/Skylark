package com.example.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.skylark.R;
import com.example.ui.BaseSampleActivity;
import com.viewpagerindicator.LinePageIndicator;

public class HelpPager extends BaseSampleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_lines);

        myAdapter = new MyFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(myAdapter);
        mIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}