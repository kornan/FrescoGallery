package net.kornan.gallery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.R;

/**
 * 照片集ViewHolder
 * Created by kornan on 16/5/16.
 */
public class GalleryPopupViewHolder extends RecyclerView.ViewHolder {
    public TextView bucketName;
    public TextView bucketSize;
    public SimpleDraweeView bucketImage;
    public GalleryPopupViewHolder(View itemView) {
        super(itemView);
        bucketName= (TextView) itemView.findViewById(R.id.bucket_name);
        bucketSize= (TextView) itemView.findViewById(R.id.bucket_size);
        bucketImage= (SimpleDraweeView) itemView.findViewById(R.id.bucket_cover_image);
    }
}
