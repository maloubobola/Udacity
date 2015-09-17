package com.example.thomasthiebaud.android.movie.bean;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by thiebaudthomas on 09/09/15.
 */
public class CustomImageView extends ImageView {
    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) (getMeasuredWidth()*1.5));
    }

}