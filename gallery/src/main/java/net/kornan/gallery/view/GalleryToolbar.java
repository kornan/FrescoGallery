package net.kornan.gallery.view;


import android.content.Context;
import android.content.res.TypedArray;
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
public class GalleryToolbar extends RelativeLayout implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    public final static int TYPE_SELETE = 1;
    public final static int TYPE_REVIEW = 2;
    public final static int TYPE_EDIT = 3;

    private GalleryToolbarLinstener galleryToolbarLinstener;
    private Toolbar toolbar;
    private int type;

    private MenuItem actionComplete;
    private MenuItem actionPreview;
    private MenuItem actionDelete;

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

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GalleryToolbar);
        type = typedArray.getInt(R.styleable.GalleryToolbar_type, 0);
        typedArray.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.toolbar, this);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        toolbar=(Toolbar) view;
        toolbar.inflateMenu(R.menu.gallery_base_toolbar_menu);
        actionComplete = toolbar.getMenu().findItem(R.id.action_complete);
        actionPreview = toolbar.getMenu().findItem(R.id.action_preview);
        actionDelete = toolbar.getMenu().findItem(R.id.action_delete);

        if (type == TYPE_SELETE) {
            actionPreview.setVisible(true);
            actionComplete.setVisible(true);
            actionDelete.setVisible(false);
        } else if (type == TYPE_REVIEW) {
            actionPreview.setVisible(false);
            actionComplete.setVisible(false);
            actionDelete.setVisible(false);
//            toolbar.inflateMenu(R.menu.gallery_preview_toolbar_menu);
        } else if (type == TYPE_EDIT) {
//            toolbar.inflateMenu(R.menu.gallery_edit_toolbar_menu);
            actionPreview.setVisible(false);
            actionComplete.setVisible(false);
            actionDelete.setVisible(true);
        } else {
            actionPreview.setVisible(false);
            actionComplete.setVisible(false);
            actionDelete.setVisible(false);
        }

        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnMenuItemClickListener(this);
//        toolbar.getMenu().getItem(0);
    }

    public void setTitle(CharSequence title){
        toolbar.setTitle(title);
    }

    public void setType(int type){
        if (type == TYPE_SELETE) {
            actionPreview.setVisible(true);
            actionComplete.setVisible(true);
            actionDelete.setVisible(false);
        } else if (type == TYPE_REVIEW) {
            actionPreview.setVisible(false);
            actionComplete.setVisible(false);
            actionDelete.setVisible(false);
//            toolbar.inflateMenu(R.menu.gallery_preview_toolbar_menu);
        } else if (type == TYPE_EDIT) {
//            toolbar.inflateMenu(R.menu.gallery_edit_toolbar_menu);
            actionPreview.setVisible(false);
            actionComplete.setVisible(false);
            actionDelete.setVisible(true);
        } else {
            actionPreview.setVisible(false);
            actionComplete.setVisible(false);
            actionDelete.setVisible(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (galleryToolbarLinstener != null) {
            galleryToolbarLinstener.onBack();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.action_preview) {
            if (galleryToolbarLinstener != null) {
                galleryToolbarLinstener.onShowBigImage();
            }
        } else if (menuItemId == R.id.action_complete) {
            if (galleryToolbarLinstener != null) {
                galleryToolbarLinstener.onComplete();
            }
        } else if (menuItemId == R.id.action_delete) {
            if (galleryToolbarLinstener != null) {
                galleryToolbarLinstener.onDelete();
            }
        }
        return true;
    }

    public void setGalleryToolbarLinstener(GalleryToolbarLinstener galleryToolbarLinstener) {
        this.galleryToolbarLinstener = galleryToolbarLinstener;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public interface GalleryToolbarLinstener {
        //删除
        void onDelete();

        // 返回
        void onBack();

        //预览
        void onShowBigImage();

        //完成
        void onComplete();

        //取消
        void onCancel();
    }
}
