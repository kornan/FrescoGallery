package net.kornan.gallery.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.factory.AlbumHelper;
import net.kornan.gallery.factory.ImageHandler;
import net.kornan.gallery.R;
import net.kornan.gallery.adapters.ImagesAdapter;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.tools.ImageUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 图片浏览界面
 */
public class ImagesActivity extends Activity implements OnClickListener, OnScrollListener {
    public final static String SELECT_IMAGE_KEY = "SELECT_IMAGES";
    private List<ImageItem> dataList;
    private GridView gridView;
    private ImagesAdapter adapter;
    private AlbumHelper helper;
    private TextView text_preview;
    private TextView text_over;
    /**
     * 选择图片的最大值,0为无限制,默认为9
     */
    private int select_max = 9;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;

    private ImageHandler mHandler = new ImageHandler(this);

    public int getSelectMax() {
        return select_max;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        initData();
        initView();
    }

    private void initView() {
        text_over = (TextView) findViewById(R.id.text_over);
        text_preview = (TextView) findViewById(R.id.text_preview);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnItemClickListener(null);
//        gridView.setOnScrollListener(this);
        adapter = new ImagesAdapter(this, dataList, select_max);
        gridView.setAdapter(adapter);
        text_preview.setOnClickListener(this);
        text_over.setOnClickListener(this);
        findViewById(R.id.rl_back).setOnClickListener(this);
        findViewById(R.id.ibtn_exit).setOnClickListener(this);
        findViewById(R.id.text_photo_number).setOnClickListener(this);
        if (select_max == 1) text_preview.setVisibility(View.GONE);
    }

    private void initData() {
        select_max = getIntent().getIntExtra(SELECT_IMAGE_KEY, 9);
//        dataList = helper.getImagesDefaultItemList();
        dataList = helper.getAllImagesItemList();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        // 滚动停止加载
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
    }

    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem
                    + visibleItemCount; i++) {
                String thumbnailPath = dataList.get(i).thumbnailPath;
                String imagePath = dataList.get(i).imagePath;
                SimpleDraweeView imageView = (SimpleDraweeView) gridView
                        .findViewWithTag(thumbnailPath);
                if (imageView != null) {
                    ImageUtils.resizeImageViewForScreen(imageView, Uri.fromFile(new File(thumbnailPath)), 300, 300);
                } else {
                    imageView = (SimpleDraweeView) gridView
                            .findViewWithTag(imagePath);
                    ImageUtils.resizeImageViewForScreen(imageView, Uri.fromFile(new File(imagePath)), 300, 300);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (helper != null) {
            helper.reset(adapter.getSelectedItems());
            dataList.clear();
            helper = null;
        }
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.text_preview) {
            if (adapter.getSelectedItems().size() > 0) {
                Intent intent = new Intent(this, GalleryPreviewActivity.class);
                PreviewData data = new PreviewData();
                data.setImageItems(adapter.getSelectedItems());
                intent.putExtra(GalleryPreviewActivity.PREVIEW_TAG, data);
                startActivity(intent);
            } else {
                Toast.makeText(this, "你还没有选择图片！", Toast.LENGTH_LONG).show();
            }

        } else if (v.getId() == R.id.text_over) {
            result(adapter.getSelectedItems());
        } else if (v.getId() == R.id.rl_back) {
            finish();
        } else if (v.getId() == R.id.ibtn_exit) {
            finish();
        } else if (v.getId() == R.id.text_photo_number) {
            finish();
        }
    }

    protected void result(List<ImageItem> imageItems) {
        if (imageItems != null && imageItems.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra(SELECT_IMAGE_KEY, (Serializable) imageItems);
            this.setResult(RESULT_OK, intent);
            this.finish();
        } else {
            this.setResult(RESULT_CANCELED);
            this.finish();
        }
    }


}
