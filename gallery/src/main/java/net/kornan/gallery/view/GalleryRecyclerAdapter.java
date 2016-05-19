package net.kornan.gallery.view;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.tools.ImageUtils;

import java.io.File;
import java.util.List;

/**
 * Created by kornan on 16/5/18.
 */
public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder> {

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    private LayoutInflater mInflater;
    private List<ImageItem> mDatas;

    public GalleryRecyclerAdapter(Context context, List<ImageItem> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
            mImg = (SimpleDraweeView) arg0
                    .findViewById(R.id.gallery_image_view);
        }

        SimpleDraweeView mImg;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.gallery_recycler_item,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        ImageUtils.resizeImageViewForScreen(viewHolder.mImg, Uri.fromFile(new File(mDatas.get(i).imagePath)), 180, 180);
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
                }
            });
        }
    }
}
