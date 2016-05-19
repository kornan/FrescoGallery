package net.kornan.gallery.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.facebook.imagepipeline.common.ResizeOptions;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.gallery.view.GalleryPhotoView;
import net.kornan.gallery.view.GalleryToolbar;
import net.kornan.tools.ImageUtils;
import net.kornan.tools.MediaUtils;

import java.util.ArrayList;

/**
 * 本地图片预览
 */
public class SimplePreviewActivity extends Activity implements OnPageChangeListener, GalleryToolbar.GalleryToolbarLinstener {
    private final static int WIDTH = 480;
    private final static int HEIGHT = 800;

    public final static String PREVIEW_TAG = "paths";
    public final static String PREVIEW_TITLE_BAR = "title_bar";

    private GalleryToolbar toolbar;

    private ViewPager pager;
    private PreViewPageAdapter adapter;
    private int position = 0;
    public ArrayList<ImageItem> drr = new ArrayList<>();

    private void initView() {

        toolbar = (GalleryToolbar) findViewById(R.id.gallery_toolbar);
        toolbar.setGalleryToolbarLinstener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.addOnPageChangeListener(this);
    }

    private void initData() {
        try {
            PreviewData previewData = (PreviewData) getIntent().getSerializableExtra(PREVIEW_TAG);
            boolean isCloseTitlebar = getIntent().getBooleanExtra(PREVIEW_TITLE_BAR, false);
//            if (isCloseTitlebar) {
//                rl_titleBar.setVisibility(View.GONE);
//            }
            if (previewData != null) {
                position = previewData.getIndex();
//                btn_photo_del.setVisibility(previewData.isDelete() ? View.VISIBLE : View.GONE);
                if (previewData.isDelete()) {
                    toolbar.setType(GalleryToolbar.TYPE_EDIT);
                }
                drr.addAll(previewData.getImageItems());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new PreViewPageAdapter();
        for (int i = 0; i < drr.size(); i++) {
            GalleryPhotoView imageView = new GalleryPhotoView(this);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            adapter.add(imageView);
            loadImage(i, i != position);
        }
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        toolbar.setTitle(String.valueOf(position + 1 + " / " + adapter.getCount()));
    }

    private void result() {
        Intent intent = new Intent();
        intent.putExtra(PREVIEW_TAG, drr);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_simple_preview);
        initView();
        initData();
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

    private void loadImage(int position, boolean isDefalut) {
        DisplayMetrics displayMetrics = MediaUtils.getScreenDimen(this);
        Uri thumbUri = ImageUtils.getUri(drr.get(position).thumbnailPath);
        Uri bigUri = ImageUtils.getUri(drr.get(position).imagePath);

        if (isDefalut) {
            adapter.getView(position).setImageUri(bigUri, new ResizeOptions(WIDTH, HEIGHT));

        } else {
            adapter.getView(position).setImageUri(bigUri, null);

//            adapter.getView(position).setImageUri(bigUri, new ResizeOptions(displayMetrics.widthPixels, displayMetrics.heightPixels));
//            adapter.getView(position).setImageURI(bigUri);
        }
        //fresco:渐进式JPEG图仅仅支持网络图
//        if (thumbUri != null) {
//            adapter.getView(position).setImageUri(bigUri, thumbUri, new ResizeOptions(displayMetrics.widthPixels, displayMetrics.heightPixels));
//        } else {
//            adapter.getView(position).setImageUri(bigUri, new ResizeOptions(displayMetrics.widthPixels, displayMetrics.heightPixels));
//        }
    }

//    private boolean isScrolling = false;
//    private boolean left = false;
//    private boolean right = false;

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
//        Log.e("adapter", "onPageScrollStateChanged=" + arg0);
//        if (arg0 == 1) {
//            isScrolling = true;
//        } else {
//            isScrolling = false;
//        }
//
//        if (arg0 == 2) {
//            right = left = false;
//        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
//        Log.e("adapter", "onPageScrolled " + arg0 + " " + arg1 + " " + arg2);
    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
//        Log.e("adapter", "onPageSelected " + arg0 );
        position = arg0;
        if (adapter.getCount() > 0) {
            toolbar.setTitle(String.valueOf(position + 1 + " / " + adapter.getCount()));
        }
//        if (adapter.getView(position).isActivated() && adapter.getView(position).getVisibleRectangleBitmap().isRecycled()) {
//            Log.e("adapter", "onPageSelected " + arg0 );
        loadImage(position, false);
//        }
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

        private ArrayList<GalleryPhotoView> listViews = new ArrayList<>();


        public PreViewPageAdapter() {
        }

        public GalleryPhotoView getView(int position) {
            return listViews.get(position);
        }

        public void add(GalleryPhotoView view) {
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
}
