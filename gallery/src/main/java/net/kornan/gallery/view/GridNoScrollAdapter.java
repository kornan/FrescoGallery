package net.kornan.gallery.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.tools.ImageUtils;

import java.io.File;
import java.util.List;

/**
 * @author: kornan
 * @date: 2016-03-05 15:33
 */
public class GridNoScrollAdapter extends BaseAdapter {
    /**
     * 选择图片最大值,默认9
     */
    private final static int MAX = 9;
    private Context context;
    //    private List<Uri> uriList;
    private SimpleDraweeView image;
    private LayoutInflater inflater;
    private int max;
    private boolean isCanEdit = true;
    private List<ImageItem> datas;
    /**
     * 0:最多张的时候 不显示+，1：一直显示+，大于9张的时候，在第9张显示数字
     */
    private int default_type = 1;

    public GridNoScrollAdapter(Context context, List<ImageItem> datas) {
        this(context, datas, MAX, true);
    }

    public GridNoScrollAdapter(Context context, List<ImageItem> datas, int max, boolean isCanEdit) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.max = max;
        this.isCanEdit = isCanEdit;
        this.datas = datas;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int size = 0;
        if(datas.size() <max&& isCanEdit){
            size = datas.size() + 1;
        }else{
            size = max+1;
        }

        return size;
    }

    public boolean isCanAdd(int position) {
        // TODO Auto-generated method stub
        if(position>=datas.size()||position>=max){
            return true;
        }else{
            return false;
        }
    }

//    public void add(Uri uri) {
//        if (uri == null || datas.size() >= max) return;
//        datas.add(uri.getPath());
//    }

    public List<ImageItem> getList() {
        return datas;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return datas.get(position);
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
        TextView tv_number=(TextView)convertView.findViewById(R.id.tv_number);
        if(position==max-1&&datas.size()>max){
            tv_number.setVisibility(View.VISIBLE);
            tv_number.setText("+"+(datas.size()-max));
        }else{
            tv_number.setVisibility(View.GONE);
        }
        if (position < datas.size()&&position<max) {
            ImageUtils.resizeImageViewForScreen(image, Uri.fromFile(new File(datas.get(position).imagePath)), 180, 180);
        } else {
            image.getHierarchy()
                    .setPlaceholderImage(R.drawable.photo_add);
        }
        return convertView;
    }
}
