package net.kornan.gallery.view;


import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import net.kornan.gallery.R;

/**
 * Created by KORNAN on 2016/4/25.
 *
 * @author: kornan
 * @date: 2016-04-25 09:25
 */
public class GalleryToolbar extends RelativeLayout {
    private GalleryListener galleryListener;
    private Toolbar toolbar;

    public GalleryToolbar(Context context) {
        super(context);
        init(context, null);
    }

    public GalleryToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GalleryToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImagesSelectView);
//        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.toolbar, this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.base_toolbar_menu);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryListener != null) {
                    galleryListener.cancel();
                }
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_preview) {
                    if (galleryListener != null) {
                        galleryListener.showBigImage();
                    }
                } else if (menuItemId == R.id.action_complete) {
                    if (galleryListener != null) {
                        galleryListener.complete();
                    }
                }
                return true;
            }
        });
//        toolbar.getMenu().getItem(0);
    }

    public void setGalleryListener(GalleryListener galleryListener) {
        this.galleryListener = galleryListener;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
