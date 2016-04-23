package net.kornan.gallery.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author: kornan
 * @date: 2016-03-04 16:30
 */
public class GridNoScrollView extends GridView {

    public GridNoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
