package net.kornan.gallery.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;

/**
 * 相册胶卷 拍照ViewHolder
 * Created by kornan on 16/5/11.
 */
public class CameraViewHolder extends GalleryViewHolder {
    protected Uri mTakePhotoUri;
    public ImageButton ibtn_take_phone;

    public CameraViewHolder(Context context, GalleryAdapter adapter, View itemView) {
        super(context, adapter, itemView);
        ibtn_take_phone = (ImageButton) itemView.findViewById(R.id.ibtn_take_phone);
        itemView.setOnClickListener(this);
        ibtn_take_phone.setOnClickListener(this);
    }

    @Override
    public void bindData(ImageItem arg0, int arg1) {

    }

    @Override
    public void onClick(View v) {
        if (adapter.getSelectedItems().size() >= adapter.getMax()) {
            Toast.makeText(context,
                    "最多选择" + adapter.getMax() + "张图片", Toast.LENGTH_LONG).show();
            return;
        }
        if (adapter.cameraClickLinstener != null) {
            adapter.cameraClickLinstener.cameraClick(itemView);
        }
    }
}
