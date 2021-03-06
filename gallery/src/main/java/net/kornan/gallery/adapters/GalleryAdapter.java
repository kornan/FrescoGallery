package net.kornan.gallery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;

import java.util.List;

/**
 * 相册胶卷adapter
 * Created by kornan on 16/5/11.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

    private List<ImageItem> dataList;
    private Context context;
    private List<ImageItem> selectedItems;
    private int max;
    private boolean multiSelect;
    private boolean isDigit;

    public OnGalleryLinstener onGalleryLinstener;

    private DisplayMetrics displayMetrics;
    private int mWidth;

    public GalleryAdapter(Context context, List<ImageItem> list, int max, boolean multiSelect, boolean isDigit, List<ImageItem> selectedItems) {
        this.context = context;
        this.dataList = list;
        this.max = max;
        this.multiSelect = multiSelect;
        this.isDigit = isDigit;
        this.selectedItems = selectedItems;
        mWidth = context.getResources().getDisplayMetrics().widthPixels / 3;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ImageItem.Type.CAMERA.ordinal()) {
            View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_take_phone_item, null);
            View button = itemLayout.findViewById(R.id.ibtn_take_phone);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mWidth, mWidth);
            button.setLayoutParams(lp);
            return new CameraViewHolder(context, this, itemLayout);
        } else {
            View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_image_item, null);
            View button = itemLayout.findViewById(R.id.layout_image);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mWidth, mWidth);
            button.setLayoutParams(lp);
            return new ImageViewHolder(context, this, itemLayout, isDigit);
        }
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {

        if (dataList.get(position).type == ImageItem.Type.IMAGE) {
            //替换其它界面传过来的已选项。
            for (ImageItem imageItem : selectedItems) {
                if (dataList.get(position).imageId.equals(imageItem.imageId)) {
                    dataList.remove(position);
                    dataList.add(position, imageItem);
                    break;
                }
            }
        }

        holder.bindData(dataList.get(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        int type = ImageItem.Type.IMAGE.ordinal();
//        Log.e("getItemViewType","position:"+position);
        if (dataList.get(position).type != null) {
            type = dataList.get(position).type.ordinal();
        }
        return type;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (dataList != null) {
            count = dataList.size();
        }
        return count;
    }

    public void setOnGalleryLinstener(OnGalleryLinstener onGalleryLinstener) {
        this.onGalleryLinstener = onGalleryLinstener;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public boolean isDigit() {
        return isDigit;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public List<ImageItem> getSelectedItems() {
        return selectedItems;
    }

    /**
     * 刷新顺序
     */
    public void refreshIndex() {
        for (int i = 0; i < selectedItems.size(); i++) {
            ImageItem item = selectedItems.get(i);
            item.selectedIndex = i + 1;
        }
        notifyDataSetChanged();
    }


    public static interface OnGalleryLinstener {
        void onCameraClick(View view);

        void itemClick(View arg0, int arg1);

        void itemCheck(View arg0, int arg1);
    }
}
