package net.kornan.gallery.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.factory.ImageItem;
import net.kornan.tools.ImageUtils;

import java.io.File;

/**
 * 相册胶卷 ViewHolder
 * Created by kornan on 16/5/11.
 */
public abstract class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected Context context;
    protected GalleryAdapter adapter;

    public GalleryViewHolder(Context context, GalleryAdapter adapter, View itemView) {
        super(itemView);
        this.adapter = adapter;
        this.context = context;
        itemView.setOnClickListener(this);
    }

    public abstract void bindData(ImageItem arg0, int arg1);

    public void setImageViewForCache(String imageUrl, SimpleDraweeView imageView) {
        int width = context.getResources().getDisplayMetrics().widthPixels / 4;
        if (width > 300) width = 300;
        ImageUtils.resizeImageViewForScreen(imageView, Uri.fromFile(new File(imageUrl)), width, width);
    }
}
