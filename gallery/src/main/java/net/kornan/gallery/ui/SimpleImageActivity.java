package net.kornan.gallery.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.AlbumHelper;
import net.kornan.gallery.factory.ImageBucket;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.gallery.view.CameraClickLinstener;
import net.kornan.gallery.view.GalleryListener;
import net.kornan.gallery.view.GalleryPopupWindow;
import net.kornan.gallery.view.GalleryToolbar;
import net.kornan.gallery.view.ImagesSelectView;
import net.kornan.tools.FileUtils;
import net.kornan.tools.MediaUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 相册选择实现类
 * Created by kornan on 16/5/19.
 */
public class SimpleImageActivity extends AppCompatActivity implements GalleryListener, CameraClickLinstener, GalleryToolbar.GalleryToolbarLinstener {
    public final String TAG = getClass().getSimpleName();
    public final static String SELECT_IMAGE_KEY = "SELECT_IMAGES";
    public final static String SELECT_IMAGE_DATA = "SELECT_IMAGE_DATA";

    public final static String SELECT_FILTER_WIDTH = "select_filter_width";
    public final static String SELECT_FILTER_HEIGHT = "select_filter_height";

    public final static String SELECT_MULTI = "select_multi";

    private ImagesSelectView imageSelect;
    private GalleryToolbar galleryToolbar;
    private Button btnMenu;
    private View rl_bottm;

    protected Uri mTakePhotoUri;
    protected MediaScannerConnection msc;
    protected GalleryPopupWindow galleryPopupWindow;
    protected List<ImageItem> selectedItems;
    protected int selectMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_image);

        rl_bottm = findViewById(R.id.rl_bottm);
        btnMenu = (Button) findViewById(R.id.btn_menu);
        galleryToolbar = (GalleryToolbar) findViewById(R.id.gallery_toolbar);
        imageSelect = (ImagesSelectView) findViewById(R.id.imageSelect);

        initData();

        galleryToolbar.setGalleryToolbarLinstener(this);
        imageSelect.setCameraClickLinstener(this);

        imageSelect.getSelectedItems().clear();
        imageSelect.getSelectedItems().addAll(selectedItems);

        imageSelect.setSelectMax(selectMax);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryPopupWindow == null) {
                    galleryPopupWindow = new GalleryPopupWindow().initGallery(SimpleImageActivity.this);
                    galleryPopupWindow.setGalleryListener(SimpleImageActivity.this);
                }
                if (galleryPopupWindow.isShowing()) {
                    galleryPopupWindow.dismiss();
                } else {
                    galleryPopupWindow.show(v);
                }
            }
        });
        btnMenu.setText("所有图片");
        Log.d(TAG, "onCreate " + selectedItems);
//        if(selectedItems==null){
//            selectedItems = imageSelect.getSelectedItems();
//        }else{
//            imageSelect.setSelectedItems(selectedItems);
//        }
    }

    private void initData() {
        selectMax = getIntent().getIntExtra(SELECT_IMAGE_KEY, 9);
        selectedItems = (List<ImageItem>) getIntent().getSerializableExtra(SELECT_IMAGE_DATA);
    }

    @Override
    public void OnBucketChange(View view, int position) {
        galleryPopupWindow.dismiss();
        imageSelect.updateBucket(position);
        btnMenu.setText(imageSelect.getCurrentBucket().bucketName);
    }

    @Override
    protected void onDestroy() {
        if (imageSelect != null) {
            imageSelect.destroy();
        }
        super.onDestroy();
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

    @Override
    public void onCameraClick(View view) {
        startTakePhoto();
    }

    /**
     * 跳转到系统拍照
     */
    public void startTakePhoto() {
        if (!FileUtils.existSDCard()) {
            Toast.makeText(getBaseContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        //到onActivityResult时mTakePhotoUri可能会被释放,重写onSaveInstanceState
        mTakePhotoUri = FileUtils.getOutputMediaFileUri(this, MediaUtils.MEDIA_TYPE_IMAGE, "", Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName());
        MediaUtils.takePhoto(this, mTakePhotoUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (MediaUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                takePhotoResult(data, mTakePhotoUri);
            }
        } else if (resultCode == RESULT_CANCELED) {
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mTakePhotoUri", mTakePhotoUri);
        outState.putSerializable("selectedItems", (Serializable) imageSelect.getSelectedItems());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTakePhotoUri = savedInstanceState.getParcelable("mTakePhotoUri");
        List<ImageItem> selectedItems = (List<ImageItem>) savedInstanceState.getSerializable("selectedItems");
        if (selectedItems != null && selectedItems.size() > 0) {
            imageSelect.getSelectedItems().clear();
            imageSelect.getSelectedItems().addAll(selectedItems);
        }
    }

    protected void takePhotoResult(Intent data, Uri photoUri) {
//        try {
//            MediaStore.Images.Media.insertImage(getContentResolver(), photoUri.getPath(), "title", "description");
        msc = new MediaScannerConnection(this, this);
        msc.connect();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }


    public void onMediaScannerConnected() {
        msc.scanFile(mTakePhotoUri.getPath(), "image/jpeg");
    }

    public void onScanCompleted(String path, Uri uri) {

        final ImageItem imageItem = AlbumHelper.getImageItem(this, uri);
        if (imageItem != null) {

            if (imageSelect.getDataList().size() > 1) {
                imageSelect.getDataList().add(1, imageItem);
            } else {
                imageSelect.getDataList().add(imageItem);
            }
            //1:添加到所有相册;
            //2:以及添加到对应图集(未处理)
            imageItem.isSelected = true;
            imageItem.selectedIndex = imageSelect.getAdapter().getSelectedItems().size() + 1;
            imageSelect.getAdapter().getSelectedItems().add(imageItem);
//                imageSelect.refresh();
            onComplete();
        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
        msc.disconnect();
    }

    @Override
    public void onDelete() {

    }

    @Override
    public void onBack() {
        finish();
    }

    @Override
    public void onShowBigImage() {
        if (imageSelect.getAdapter().getSelectedItems().size() > 0) {
            Intent intent = new Intent(this, SimplePreviewActivity.class);
            PreviewData data = new PreviewData();
            data.setImageItems(imageSelect.getAdapter().getSelectedItems());
            intent.putExtra(SimplePreviewActivity.PREVIEW_TAG, data);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请选择图片！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onComplete() {
        result(imageSelect.getAdapter().getSelectedItems());
    }

    @Override
    public void onCancel() {
        finish();
    }


    /**
     * 到图片库(多选)
     *
     * @param activity     启动图库界面
     * @param selectImages 已选图片
     * @param max          多选时的最大值
     * @param minWidth     限制图片的最小宽度
     * @param minHeight    限制图片的最小高度
     * @param multi        是否多选状态
     * @param requestCode  请求标识
     */
    public static void launch(Activity activity, @Nullable List<ImageItem> selectImages, int max, int minWidth, int minHeight, boolean multi, int requestCode) {
        if (!FileUtils.existSDCard()) {
            Toast.makeText(activity, "SD卡不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.refresh(minWidth, minHeight);

        Intent intent = new Intent(activity, SimpleImageActivity.class);
        intent.putExtra(SimpleImageActivity.SELECT_IMAGE_KEY, max);
        intent.putExtra(SimpleImageActivity.SELECT_MULTI, multi);
        intent.putExtra(SimpleImageActivity.SELECT_FILTER_WIDTH, minWidth);
        intent.putExtra(SimpleImageActivity.SELECT_FILTER_HEIGHT, minHeight);
        intent.putExtra(SimpleImageActivity.SELECT_IMAGE_DATA, (Serializable) selectImages);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 到图片库(多选)
     *
     * @param fragment     启动图库界面
     * @param selectImages 已选图片
     * @param max          多选时的最大值
     * @param minWidth     限制图片的最小宽度
     * @param minHeight    限制图片的最小高度
     * @param multi        是否多选状态
     * @param requestCode  请求标识
     */
    public static void launch(Fragment fragment, @Nullable List<ImageItem> selectImages, int max, int minWidth, int minHeight, boolean multi, int requestCode) {
        if (!FileUtils.existSDCard()) {
            Toast.makeText(fragment.getContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        AlbumHelper helper = AlbumHelper.getHelper();
        helper.refresh(minWidth, minHeight);

        Intent intent = new Intent(fragment.getContext(), SimpleImageActivity.class);
        intent.putExtra(SimpleImageActivity.SELECT_IMAGE_KEY, max);
        intent.putExtra(SimpleImageActivity.SELECT_MULTI, multi);
        intent.putExtra(SimpleImageActivity.SELECT_FILTER_WIDTH, minWidth);
        intent.putExtra(SimpleImageActivity.SELECT_FILTER_HEIGHT, minHeight);
        intent.putExtra(SimpleImageActivity.SELECT_IMAGE_DATA, (Serializable) selectImages);
        fragment.startActivityForResult(intent, requestCode);
    }


    /**
     * @param activity    启动图库界面
     * @param minWidth    限制图片的最小宽度
     * @param minHeight   限制图片的最小高度
     * @param requestCode 请求标识
     */
    public static void launch(Activity activity, int minWidth, int minHeight, int requestCode) {
        launch(activity, null, 1, minWidth, minHeight, false, requestCode);
    }

    /**
     * @param fragment    启动图库界面
     * @param minWidth    限制图片的最小宽度
     * @param minHeight   限制图片的最小高度
     * @param requestCode 请求标识
     */
    public static void launch(Fragment fragment, int minWidth, int minHeight, int requestCode) {
        launch(fragment, null, 1, minWidth, minHeight, false, requestCode);
    }

}
