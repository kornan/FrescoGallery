package net.kornan.gallery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.view.CameraClickLinstener;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册胶卷adapter
 * Created by kornan on 16/5/11.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

    private List<ImageItem> dataList;
    private Context context;
    private ArrayList<ImageItem> selectedItems;
    private int max;
    private boolean multiSelect;
    private boolean isDigit;

    public CameraClickLinstener cameraClickLinstener;

    public GalleryAdapter(Context context, List<ImageItem> list, int max, boolean multiSelect,boolean isDigit) {
        selectedItems = new ArrayList<>();
        this.context = context;
        this.dataList = list;
        this.max = max;
        this.multiSelect = multiSelect;
        this.isDigit=isDigit;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ImageItem.Type.CAMERA.ordinal()) {
            View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_take_phone_item, null);
            return new CameraViewHolder(context, this, itemLayout);
        } else {
            View itemLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, null);
            return new ImageViewHolder(context, this, itemLayout,isDigit);
        }
    }

    public void setCameraClickLinstener(CameraClickLinstener cameraClickLinstener) {
        this.cameraClickLinstener = cameraClickLinstener;
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

    public ArrayList<ImageItem> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
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

    /**
     * 刷新顺序
     */
    public void refreshIndex() {
        for (int i = 0; i < getSelectedItems().size(); i++) {
            ImageItem item = getSelectedItems().get(i);
            item.selectedIndex = i+1;
        }
        notifyDataSetChanged();
    }

}
