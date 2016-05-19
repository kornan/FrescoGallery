package net.kornan.gallery.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import net.kornan.gallery.R;
import net.kornan.gallery.adapters.GalleryPopupAdapter;
import net.kornan.gallery.factory.AlbumHelper;

/**
 * PopupWindow
 * Created by kornan on 16/5/16.
 */
public final class GalleryPopupWindow extends PopupWindow implements GalleryPopupAdapter.OnItemClickLitener {

    private AlbumHelper albumHelper;
    public RecyclerView recyclerView;
    public View conentView;
    private GalleryPopupAdapter galleryPopupAdapter;
    private Activity activity;
    private GalleryListener galleryListener;

    public GalleryPopupWindow initGallery(Activity activity) {
        this.activity = activity;
        albumHelper = AlbumHelper.getHelper();
        albumHelper.init(activity);

        galleryPopupAdapter = new GalleryPopupAdapter(albumHelper.getImagesBucketList());
        galleryPopupAdapter.setOnItemClickLitener(this);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = layoutInflater.inflate(R.layout.gallery_popup_window, null, false);
        recyclerView = (RecyclerView) conentView.findViewById(R.id.rec_popup);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL, 0));
        recyclerView.setAdapter(galleryPopupAdapter);

        this.setContentView(conentView);
        this.setWidth(dm.widthPixels);
        this.setHeight((int) (dm.heightPixels * 0.80));
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        this.setAnimationStyle(R.style.gallery_popup_window_anim_style);

        return this;
    }

    public void setGalleryListener(GalleryListener galleryListener) {
        this.galleryListener = galleryListener;
    }

    public final void show(View view) {
        showAtLocation(view, Gravity.BOTTOM, 10, 150);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (galleryListener != null) {
            galleryListener.OnBucketChange(view, position);
        }
    }
}
