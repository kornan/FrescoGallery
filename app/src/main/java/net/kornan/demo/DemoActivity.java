package net.kornan.demo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import net.kornan.gallery.factory.AlbumHelper;
import net.kornan.gallery.factory.ImageBucket;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.gallery.ui.GalleryPreviewActivity;
import net.kornan.gallery.view.CameraClickLinstener;
import net.kornan.gallery.view.GalleryListener;
import net.kornan.gallery.view.GalleryPopupWindow;
import net.kornan.gallery.view.GalleryToolbar;
import net.kornan.gallery.view.ImagesSelectView;
import net.kornan.tools.FileUtils;
import net.kornan.tools.MediaUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DemoActivity extends AppCompatActivity implements GalleryListener, CameraClickLinstener {
    public final String TAG = getClass().getSimpleName();
    public final static String SELECT_IMAGE_KEY = "SELECT_IMAGES";
    public final static String SELECT_IMAGE_DATA = "SELECT_IMAGE_DATA";
    @InjectView(R.id.imageSelect)
    ImagesSelectView imageSelect;

    @InjectView(R.id.gallery_toolbar)
    GalleryToolbar galleryToolbar;

    @InjectView(R.id.btn_menu)
    Button btnMenu;

    @InjectView(R.id.rl_bottm)
    View rl_bottm;

    protected Uri mTakePhotoUri;
    protected MediaScannerConnection msc;
    protected GalleryPopupWindow galleryPopupWindow;
    protected List<ImageItem> selectedItems;
    protected int selectMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.inject(this);

        initData();

        galleryToolbar.setGalleryListener(this);
        imageSelect.setCameraClickLinstener(this);

        imageSelect.getSelectedItems().clear();
        imageSelect.getSelectedItems().addAll(selectedItems);
//        for(imageSelect.getDataList()){
//
//        }



        imageSelect.setSelectMax(selectMax);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (galleryPopupWindow == null) {
                    galleryPopupWindow = new GalleryPopupWindow().initGallery(DemoActivity.this);
                }
                if (galleryPopupWindow.isShowing()) {
                    galleryPopupWindow.dismiss();
                } else {
                    galleryPopupWindow.show(v);
                }
            }
        });

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
    public void showBigImage() {
        if (imageSelect.getAdapter().getSelectedItems().size() > 0) {
            Intent intent = new Intent(this, GalleryPreviewActivity.class);
            PreviewData data = new PreviewData();
            data.setImageItems(imageSelect.getAdapter().getSelectedItems());
            intent.putExtra(GalleryPreviewActivity.PREVIEW_TAG, data);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请选择图片！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void complete() {
        result(imageSelect.getAdapter().getSelectedItems());
    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void OnBucketChange() {
        galleryPopupWindow.dismiss();

//        ImageBucket bucket =  AlbumHelper.getHelper().init().getSelectedFolder();
//        if( !TextUtils.equals(bucket.path, this.currentFolderPath) ) {
//            this.currentFolderPath = bucket.path;
//            mFolderSelectButton.setText(bucket.bucketName);
//
//            ImageListContent.IMAGES.clear();
//            ImageListContent.IMAGES.addAll(bucket.imageList);
        imageSelect.getAdapter().notifyDataSetChanged();
//        } else {
//            Log.d(TAG, "OnFolderChange: " + "Same folder selected, skip loading.");
//        }
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
    public void cameraClick(View view) {
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
        outState.putSerializable("selectedItems", (Serializable) selectedItems);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTakePhotoUri = savedInstanceState.getParcelable("mTakePhotoUri");
        selectedItems = (List<ImageItem>) savedInstanceState.getSerializable("selectedItems");
    }

    /**
     * 拍照返回处理
     *
     * @param data
     * @param photoUri
     */
    public void takePhotoResult(Intent data, Uri photoUri) {
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
            imageItem.isSelected = true;
            imageItem.selectedIndex = imageSelect.getAdapter().getSelectedItems().size();
            imageSelect.getAdapter().getSelectedItems().add(imageItem);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageSelect.getAdapter().refreshIndex();
//                    imageSelect.getAdapter().notifyItemInserted(1);
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
                }
            });
        }
        msc.disconnect();
    }
}
