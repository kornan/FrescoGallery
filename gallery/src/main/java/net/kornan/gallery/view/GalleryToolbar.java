package net.kornan.gallery.view;


import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.IGallery;

/**
 * Created by KORNAN on 2016/4/25.
 *
 * @author: kornan
 * @date: 2016-04-25 09:25
 */
public class GalleryToolBar extends RelativeLayout implements IGallery {

    Toolbar toolbar;

    public GalleryToolBar(Context context) {
        super(context);
        init(context, null);
    }

    public GalleryToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GalleryToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImagesSelectView);
//        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.toolbar, this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        toolbar.inflateMenu(R.menu.base_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {

                } else if (menuItemId == R.id.action_notification) {

                }
                return true;
            }
        });


    }

    @Override
    public void showBigImage() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void cancel() {

    }
}
