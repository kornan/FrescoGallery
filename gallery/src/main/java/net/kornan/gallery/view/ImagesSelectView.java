package net.kornan.gallery.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import net.kornan.gallery.R;
import net.kornan.gallery.adapters.GalleryAdapter;
import net.kornan.gallery.factory.AlbumHelper;
import net.kornan.gallery.factory.ImageBucket;
import net.kornan.gallery.factory.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 1:单选或多选
 * 2:多选最大值
 *
 * @author: kornan
 * @date: 2016-04-19 11:59
 */
public class ImagesSelectView extends RelativeLayout {

    protected List<ImageItem> dataList = new ArrayList<>();
    protected RecyclerView gridView;
    protected GalleryAdapter adapter;
    private AlbumHelper helper;
//    private CheckedBox Digital recording
    /**
     * 选择图片的最大值,0为无限制,默认为9
     */
    private int selectMax = 9;
    private boolean multiSelect = true;
    private boolean isDigit = true;
    private List<ImageItem> selectedItems = new ArrayList<>();
    private ImageBucket currentBucket;

    public ImagesSelectView(Context context) {
        super(context);
        init(context, null);
    }

    public ImagesSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImagesSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public GalleryAdapter getAdapter() {
        return adapter;
    }

    public void setCameraClickLinstener(CameraClickLinstener cameraClickLinstener) {
        adapter.setCameraClickLinstener(cameraClickLinstener);
    }

    public List<ImageItem> getDataList() {
        return dataList;
    }

    public List<ImageItem> getSelectedItems() {
        return selectedItems;
    }

    public int getSelectMax() {
        return selectMax;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
        adapter.setMax(selectMax);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImagesSelectView);
        multiSelect = typedArray.getBoolean(R.styleable.ImagesSelectView_multiSelect, true);
        isDigit = typedArray.getBoolean(R.styleable.ImagesSelectView_isDigit, true);
        selectMax = typedArray.getInt(R.styleable.ImagesSelectView_optionalMax, 9);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.gridview_images_layout, this);
        gridView = (RecyclerView) findViewById(R.id.image_grid);
        gridView.addItemDecoration(new DividerGridItemDecoration(context));
        helper = AlbumHelper.getHelper();
        helper.init(getContext().getApplicationContext());
        dataList.addAll(helper.getAllImagesItemList());
        gridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new GalleryAdapter(getContext(), dataList, selectMax, multiSelect, isDigit, selectedItems);
        gridView.setAdapter(adapter);
        gridView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 更改图集
     *
     * @param position
     */
    public void updateBucket(int position) {
        if (helper != null) {
            currentBucket = helper.getImagesBucketList().get(position);
            dataList.clear();
            dataList.addAll(currentBucket.imageList);
            adapter.notifyDataSetChanged();
        }
    }

    public ImageBucket getCurrentBucket() {
        return currentBucket;
    }

    public void refresh() {
        if (helper != null) {
            helper.refresh();
            adapter.notifyDataSetChanged();
        }
    }


    public void destroy() {
        if (helper != null) {
            helper.reset(adapter.getSelectedItems());
            selectedItems.clear();
            helper = null;
        }
    }
}
