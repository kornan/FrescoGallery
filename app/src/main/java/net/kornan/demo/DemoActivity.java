package net.kornan.demo;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import net.kornan.gallery.factory.AlbumHelper;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.gallery.ui.GalleryPreviewActivity;
import net.kornan.gallery.view.CameraClickLinstener;
import net.kornan.gallery.view.GalleryListener;
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

public class DemoActivity extends AppCompatActivity implements GalleryListener, CameraClickLinstener, MediaScannerConnection.MediaScannerConnectionClient {

    public final static String SELECT_IMAGE_KEY = "SELECT_IMAGES";

    @InjectView(R.id.imageSelect)
    ImagesSelectView imageSelect;

    @InjectView(R.id.gallery_toolbar)
    GalleryToolbar galleryToolbar;

    protected Uri mTakePhotoUri;
    protected MediaScannerConnection msc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.inject(this);
        galleryToolbar.setGalleryListener(this);
        imageSelect.setCameraClickLinstener(this);
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
            Toast.makeText(this, "你还没有选择图片！", Toast.LENGTH_LONG).show();
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTakePhotoUri = savedInstanceState.getParcelable("mTakePhotoUri");
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageSelect.getAdapter().notifyItemInserted(1);
                }
            });

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "空", Toast.LENGTH_SHORT).show();
                }
            });
        }

//        Log.e("SOS", "scan completed ");
        msc.disconnect();
    }
}
