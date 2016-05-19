package net.kornan.gallery.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import net.kornan.gallery.factory.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: kornan
 * @date: 2016-03-04 16:30
 */
public class GridNoScrollView extends GridView {
    protected GridNoScrollAdapter adapter;

    protected List<ImageItem> datas = new ArrayList<>();

    public GridNoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        adapter = new GridNoScrollAdapter(context, datas);
        this.setAdapter(adapter);
    }

    public List<ImageItem> getDatas() {
        return datas;
    }

    public void setDatas(List<ImageItem> datas) {
        this.datas = datas;
    }

    public GridNoScrollAdapter getGridNoScrollAdapter() {
        return adapter;
    }

    public void reset(List<ImageItem> datas) {
        getDatas().clear();
        getDatas().addAll(datas);
        adapter.notifyDataSetChanged();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
