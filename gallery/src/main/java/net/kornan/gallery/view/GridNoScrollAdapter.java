package net.kornan.gallery.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.R;
import net.kornan.tools.ImageUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: kornan
 * @date: 2016-03-05 15:33
 */
public class GridNoScrollAdapter extends BaseAdapter {
    /**
     * 选择图片最大值,默认9
     */
    private int max = 9;
    private Context context;
    private List<Uri> uriList;
    private SimpleDraweeView image;
    private LayoutInflater inflater;

    private boolean isCanEdit = true;

    public GridNoScrollAdapter(Context context) {
        this(context, 9, true);
    }

    public GridNoScrollAdapter(Context context, int max, boolean isCanEdit) {
        this.context = context;
        uriList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.max = max;
        this.isCanEdit = isCanEdit;
    }

    public int getMax() {
        return max;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int size = 0;
        if (uriList.size() < max && isCanEdit) {
            size = uriList.size() + 1;
        } else {
            size = uriList.size();
        }
        return size;
    }

    public boolean isCanAdd(int position) {
        // TODO Auto-generated method stub
        return position >= uriList.size();
    }

    public void add(Uri uri) {
        if (uri == null || uriList.size() >= max) return;
        uriList.add(uri);
    }

    public List<Uri> getList() {
        return uriList;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return uriList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = inflater.inflate(R.layout.gridview_item, parent, false);
        image = (SimpleDraweeView) convertView.findViewById(R.id.image);

        if (position < uriList.size()) {
            ImageUtils.resizeImageViewForScreen(image, uriList.get(position), 300, 300);
        } else {
            image.getHierarchy()
                    .setPlaceholderImage(R.drawable.photo_add);
        }
        return convertView;
    }
}
