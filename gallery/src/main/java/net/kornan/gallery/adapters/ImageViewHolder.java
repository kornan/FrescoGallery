package net.kornan.gallery.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.view.GalleryCheckView;

/**
 * 相册胶卷 ViewHolder
 * Created by kornan on 16/5/11.
 */
public class ImageViewHolder extends GalleryViewHolder implements CompoundButton.OnCheckedChangeListener {

    public SimpleDraweeView imageView;
    public GalleryCheckView checkBox;
    public boolean isDigit;

    private ImageItem imageItem;
    private int position;

    public ImageViewHolder(Context context, GalleryAdapter adapter, View itemView, boolean isDigit) {
        super(context, adapter, itemView);
        imageView = (SimpleDraweeView) itemView
                .findViewById(R.id.imageView1);
        checkBox = (GalleryCheckView) itemView
                .findViewById(R.id.checkBox1);
        this.isDigit = isDigit;
        if (isDigit) {
            checkBox.setBackgroundResource(R.drawable.gallery_checkbox_digit_icon);
        } else {
            checkBox.setBackgroundResource(R.drawable.checkbox_icon);
        }

    }

    @Override
    public void bindData(final ImageItem item, final int position) {
        this.position = position;
        this.imageItem = item;

        String path;
        if (!TextUtils.isEmpty(item.thumbnailPath)) {
            path = item.thumbnailPath;
            imageView.setTag(item.thumbnailPath);
            setImageViewForCache(item.thumbnailPath, imageView);
        } else {
            path = item.imagePath;
            imageView.setTag(item.imagePath);
            setImageViewForCache(item.imagePath, imageView);
        }

        imageView.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(item.isSelected);
        if (item.isSelected && isDigit) {
            checkBox.setText(String.valueOf(item.selectedIndex));
        } else {
            checkBox.setText("");
        }

        checkBox.setOnCheckedChangeListener(this);
        if (!adapter.isMultiSelect()) {
            checkBox.setVisibility(View.GONE);
        } else {
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {
        // TODO Auto-generated method stub
        if (isChecked) {
            if (adapter.getSelectedItems().size() >= adapter.getMax()) {
                Toast.makeText(context,
                        String.format(context.getString(R.string.gallery_most_prompt), adapter.getMax()), Toast.LENGTH_LONG).show();
                checkBox.setChecked(false);
                return;
            }
            imageItem.selectedIndex = adapter.getSelectedItems().size();
            adapter.getSelectedItems().add(imageItem);
        } else {
            imageItem.selectedIndex = -1;
            adapter.getSelectedItems().remove(imageItem);
//                    adapter.getSelectedItems().remove()
        }
        adapter.refreshIndex();
        imageItem.isSelected = isChecked;


    }

    @Override
    public void onClick(View v) {
        if (adapter.onGalleryLinstener != null) {
            adapter.onGalleryLinstener.itemClick(v, position);
        }

//        PreviewData data = new PreviewData();
//        ArrayList<ImageItem> imgs = new ArrayList<>();
//        imgs.add(item);
//        data.setImageItems(imgs);
//        SimplePreviewActivity.launch(v.getContext(), data);
    }
}
