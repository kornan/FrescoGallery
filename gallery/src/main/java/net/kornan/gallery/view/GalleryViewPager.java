package net.kornan.gallery.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author: kornan
 * @date: 2016-04-05 21:03
 */
public class GalleryViewPager extends ViewPager {

    public GalleryViewPager(Context context) {
        super(context);
    }


    public GalleryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
