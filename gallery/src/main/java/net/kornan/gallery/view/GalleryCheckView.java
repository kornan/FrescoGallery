package net.kornan.gallery.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.kornan.gallery.R;

/**
 * 图片选择框
 * Created by kornan on 16/5/10.
 */
public class GalleryCheckView extends CheckBox {
    private boolean mChecked;
    private boolean mBroadcasting;
    private Drawable mButtonDrawable;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    public GalleryCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GalleryCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//        this.setButtonDrawable(null);
    }
    @Override
    public CharSequence getAccessibilityClassName() {
        return GalleryCheckView.class.getName();
    }

//    @Override
//    public void setChecked(boolean checked) {
//        if (mChecked != checked) {
//            mChecked = checked;
//            refreshDrawableState();
//
//            // Avoid infinite recursions if setChecked() is called from a listener
//            if (mBroadcasting) {
//                return;
//            }
//
//            mBroadcasting = true;
//            if (mOnCheckedChangeListener != null) {
//                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
//            }
//            mBroadcasting = false;
//        }
//    }
//
//    @Override
//    public boolean isChecked() {
//        return mChecked;
//    }
//
//    @Override
//    public void toggle() {
//        setChecked(!mChecked);
//    }
//    @Override
//    public boolean performClick() {
//        toggle();
//
//        final boolean handled = super.performClick();
//        if (!handled) {
//            // View only makes a sound effect if the onClickListener was
//            // called, so we'll need to make one here instead.
//            playSoundEffect(SoundEffectConstants.CLICK);
//        }
//
//        return handled;
//    }
//
//    /**
//     * Register a callback to be invoked when the checked state of this button
//     * changes.
//     *
//     * @param listener the callback to call on checked state change
//     */
//    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
//        mOnCheckedChangeListener = listener;
//    }
//
//    public interface OnCheckedChangeListener {
//        void onCheckedChanged(GalleryCheckView buttonView, boolean isChecked);
//    }
}
