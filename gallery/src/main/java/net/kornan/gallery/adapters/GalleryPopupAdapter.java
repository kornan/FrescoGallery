package net.kornan.gallery.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageBucket;

import java.util.ArrayList;
import java.util.List;

/**
 * PopupAdapter
 * Created by kornan on 16/5/16.
 */
public class GalleryPopupAdapter extends RecyclerView.Adapter<GalleryPopupViewHolder>{

    private List<ImageBucket> datas=new ArrayList<>();

    public GalleryPopupAdapter(List<ImageBucket> datas) {
        this.datas=datas;
    }

    @Override
    public GalleryPopupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_popup_window_item, parent, false);
        return new GalleryPopupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryPopupViewHolder holder, int position) {
        ImageBucket bucket=datas.get(position);
        holder.bucketName.setText(bucket.bucketName);
        holder.bucketSize.setText(String.valueOf(bucket.imageList.size()));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
}
