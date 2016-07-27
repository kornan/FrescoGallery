package net.kornan.gallery.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.facebook.imagepipeline.common.ResizeOptions;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.gallery.view.GalleryPhotoView;
import net.kornan.gallery.view.GalleryToolbar;
import net.kornan.gallery.view.SimplePreviewItem;
import net.kornan.tools.ImageUtils;
import net.kornan.tools.MediaUtils;

import java.util.ArrayList;

/**
 * 本地图片预览
 */
public class SimplePreviewActivity extends Activity implements OnPageChangeListener, GalleryToolbar.GalleryToolbarLinstener {
    public final static String PREVIEW_TAG = "paths";

    private GalleryToolbar toolbar;
    private CheckBox cb_image;
    private ViewPager pager;
    private PreViewPageAdapter adapter;

    private int position = 0;

    private ArrayList<ImageItem> drr = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_simple_preview);

        toolbar = (GalleryToolbar) findViewById(R.id.gallery_toolbar);
        pager = (ViewPager) findViewById(R.id.viewpager);
        cb_image = (CheckBox) findViewById(R.id.cb_image);

        initView();
        initData();
    }

    private void initView() {
        toolbar.setGalleryToolbarLinstener(this);
        pager.addOnPageChangeListener(this);
        cb_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("fresco", position + " loadImage " + isChecked);
                drr.get(position).isSelected = isChecked;
            }
        });
    }

    private void initData() {
        try {
            PreviewData previewData = (PreviewData) getIntent().getSerializableExtra(PREVIEW_TAG);

            if (previewData != null) {
                position = previewData.getIndex();
                if (previewData.isDelete()) {
                    toolbar.setType(GalleryToolbar.TYPE_EDIT);
                }
                drr.addAll(previewData.getImageItems());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new PreViewPageAdapter();
        SimplePreviewItem item;
        for (int i = 0; i < drr.size(); i++) {
            item = new SimplePreviewItem(this);
            item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            adapter.add(item);
        }
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        cb_image.setChecked(drr.get(position).isSelected);
        toolbar.setTitle(String.valueOf(position + 1 + " / " + adapter.getCount()));
    }

    private void result() {
        Intent intent = new Intent();
        intent.putExtra(PREVIEW_TAG, drr);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deleteImage() {
        adapter.remove(position);
        drr.remove(position);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (adapter.getCount() == 0) {
            result();
            return;
        } else {
            if (position >= adapter.getCount()) {
                position = adapter.getCount() - 1;
            }
            toolbar.setTitle(String.valueOf(position + 1 + " / " + adapter.getCount()));
            pager.setCurrentItem(position);
        }
    }

    private void loadImage(int position) {
        adapter.getView(position).loadImage(drr.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
        if (ViewPager.SCROLL_STATE_IDLE == arg0) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        position = arg0;
        Log.e("fresco", "loadImage " + arg0);
        cb_image.setChecked(drr.get(arg0).isSelected);
        if (adapter.getCount() > 0) {
            toolbar.setTitle(String.valueOf(position + 1 + " / " + adapter.getCount()));
        }
    }

    @Override
    public void onDelete() {
        deleteImage();
    }

    @Override
    public void onBack() {
        result();
    }

    @Override
    public void onShowBigImage() {
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onCancel() {

    }

    class PreViewPageAdapter extends PagerAdapter {

        private ArrayList<SimplePreviewItem> listViews = new ArrayList<>();


        public PreViewPageAdapter() {
        }

        public SimplePreviewItem getView(int position) {
            return listViews.get(position);
        }

        public void add(SimplePreviewItem view) {
            listViews.add(view);
        }

        public void remove(int position) {
            if (position < getCount())
                listViews.remove(position);
        }

        @Override
        public int getCount() {
            return listViews.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            if (getCount() > 0)
                arg0.removeView(listViews.get(arg1 % getCount()));
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            loadImage(arg1);
            try {
                arg0.addView(listViews.get(arg1 % getCount()), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listViews.get(arg1 % getCount());
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    public static void launch(Activity activity, PreviewData previewData, int requestCode) {
        Intent intent = new Intent(activity, SimplePreviewActivity.class);
        intent.putExtra(SimplePreviewActivity.PREVIEW_TAG, previewData);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launch(Context activity, PreviewData previewData) {
        Intent intent = new Intent(activity, SimplePreviewActivity.class);
        intent.putExtra(SimplePreviewActivity.PREVIEW_TAG, previewData);
        activity.startActivity(intent);
    }
}
