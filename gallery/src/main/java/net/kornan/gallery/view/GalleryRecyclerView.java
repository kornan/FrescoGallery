package net.kornan.gallery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


/**
 * RecyclerView
 * Created by kornan on 16/5/18.
 */
public class GalleryRecyclerView extends RecyclerView {

    private View mCurrentView;

    private OnItemScrollChangeListener mItemScrollChangeListener;

    public void setOnItemScrollChangeListener(
            OnItemScrollChangeListener mItemScrollChangeListener) {
        this.mItemScrollChangeListener = mItemScrollChangeListener;
    }

    public interface OnItemScrollChangeListener {
        void onChange(View view, int position);
    }

    public GalleryRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.addOnScrollListener(onScrollListener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        mCurrentView = getChildAt(0);

        if (mItemScrollChangeListener != null) {
            mItemScrollChangeListener.onChange(mCurrentView,
                    getChildAdapterPosition(mCurrentView));
        }
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            View newView = getChildAt(0);

            if (mItemScrollChangeListener != null) {
                if (newView != null && newView != mCurrentView) {
                    mCurrentView = newView;
                    mItemScrollChangeListener.onChange(mCurrentView,
                            getChildAdapterPosition(mCurrentView));

                }
            }
        }
    };
}
