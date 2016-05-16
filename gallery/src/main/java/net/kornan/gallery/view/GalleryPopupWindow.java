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

/**
 * PopupWindow
 * Created by kornan on 16/5/16.
 */
public final class GalleryPopupWindow extends PopupWindow {

    public RecyclerView recyclerView;
    public View conentView;
    private GalleryPopupAdapter galleryPopupAdapter;
    public GalleryPopupWindow initGallery(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = layoutInflater.inflate(R.layout.gallery_popup_window, null, false);
        recyclerView = (RecyclerView) conentView.findViewById(R.id.rec_popup);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL, 0));
        galleryPopupAdapter=new GalleryPopupAdapter();
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

    public final void show(View view){
        showAtLocation(view, Gravity.BOTTOM, 10, 150);
    }

}
