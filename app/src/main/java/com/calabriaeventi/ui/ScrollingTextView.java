package com.calabriaeventi.ui;


import android.content.Context;

import android.graphics.Rect;

import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import android.widget.TextView;


public class ScrollingTextView extends AppCompatTextView {

    public ScrollingTextView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    public ScrollingTextView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public ScrollingTextView(Context context) {

        super(context);

    }

    @Override

    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(true, direction, previouslyFocusedRect);
        }
    }

    @Override

    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(true);
        }
    }

    @Override

    public boolean isFocused() {
        return true;
    }

}