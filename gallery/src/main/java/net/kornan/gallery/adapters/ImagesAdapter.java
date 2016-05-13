package net.kornan.gallery.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.ui.GalleryPreviewActivity;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.tools.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends BaseAdapter {

    private List<ImageItem> dataList;
    //    private Handler mHandler;
    private Context context;
    private ArrayList<ImageItem> selectedItems;
    private int max;

    public ImagesAdapter(Context context, List<ImageItem> list, int max) {

        this.context = context;
        this.dataList = list;
//        this.mHandler = mHandler;
        selectedItems = new ArrayList<>();
        this.max = max;
    }

    public ArrayList<ImageItem> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    class ImageHolder {
        SimpleDraweeView imageView;
        CheckBox checkBox;
    }

    private View setCamera(final int position, View convertView, ViewGroup parent) {
//        ImageItem item = dataList.get(position);
        convertView = View.inflate(context, R.layout.gallery_take_phone_item, null);
        ImageButton takePhone = (ImageButton) convertView
                .findViewById(R.id.ibtn_take_phone);
//        takePhone.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("gallery_takePhone","拍照");
//            }
//        });
        return convertView;
    }

    private View setImage(final int position, View convertView, ViewGroup parent) {
        final ImageHolder holder;
        ImageItem item = dataList.get(position);
        if (convertView == null) {
            holder = new ImageHolder();
            convertView = View.inflate(context, R.layout.item_image, null);
            holder.imageView = (SimpleDraweeView) convertView
                    .findViewById(R.id.imageView1);
            holder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkBox1);
            convertView.setTag(holder);
        } else {
            holder = (ImageHolder) convertView.getTag();
            holder.imageView.setImageURI(Uri.parse("res:///" + R.drawable.image_default_bg));
        }

        String path;
        if (!TextUtils.isEmpty(item.thumbnailPath)) {
            path = item.thumbnailPath;
            holder.imageView.setTag(item.thumbnailPath);
            setImageViewForCache(item.thumbnailPath, holder.imageView);
        } else {
            path = item.imagePath;
            holder.imageView.setTag(item.imagePath);
            setImageViewForCache(item.imagePath, holder.imageView);
        }

        if (max > 1) {
            holder.imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(context, GalleryPreviewActivity.class);
                    PreviewData data = new PreviewData();
                    ArrayList<ImageItem> imgs = new ArrayList<>();
                    imgs.add(dataList.get(position));
                    data.setImageItems(imgs);
                    intent.putExtra(GalleryPreviewActivity.PREVIEW_TAG, data);
                    context.startActivity(intent);
                }
            });
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(item.isSelected);
            holder.checkBox
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {
                                if (selectedItems.size() >= max) {
                                    Toast.makeText(context,
                                            "最多选择" + max + "张图片", Toast.LENGTH_LONG).show();
                                    holder.checkBox.setChecked(false);
                                    return;
                                }
                                selectedItems.add(dataList.get(position));
                            } else {
                                selectedItems.remove(dataList.get(position));
                            }
                            dataList.get(position).isSelected = isChecked;
                        }
                    });
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    selectedItems.add(dataList.get(position));
//                    mHandler.sendEmptyMessage(1);
                }
            });
        }
        return convertView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageItem item = dataList.get(position);
        if (item.type == ImageItem.Type.CAMERA) {
            return setCamera(position, convertView, parent);
        } else {
            return setImage(position, convertView, parent);
        }
    }

    private void setImageViewForCache(String imageUrl, SimpleDraweeView imageView) {
        ImageUtils.resizeImageViewForScreen(imageView, Uri.fromFile(new File(imageUrl)), 150, 150);
    }
}
