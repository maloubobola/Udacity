package barqsoft.footballscores.commons.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

import barqsoft.footballscores.R;

/**
 * Created by thomasthiebaud on 15/12/15.
 */
public class CustomPagerTabStrip extends PagerTabStrip {
    public CustomPagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomPagerTabStrip);
        setTabIndicatorColor(a.getColor(R.styleable.CustomPagerTabStrip_indicatorColor,context.getResources().getColor(R.color.primary_dark)));
        a.recycle();
    }
}