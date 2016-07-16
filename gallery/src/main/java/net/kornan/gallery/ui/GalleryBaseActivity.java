package net.kornan.gallery.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.tools.FileUtils;
import net.kornan.tools.MediaUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择通用类
 * Created by KORNAN on 2016/3/9.
 */
public abstract class GalleryBaseActivity extends AppCompatActivity {

    protected static String mPhotoTargetFolder;
    protected Uri mTakePhotoUri;
    protected int mScreenWidth = 720;
    protected int mScreenHeight = 1280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        DisplayMetrics dm = MediaUtils.getScreenDimen(this);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("takePhotoUri", mTakePhotoUri);
        outState.putString("photoTargetFolder", mPhotoTargetFolder);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTakePhotoUri = savedInstanceState.getParcelable("takePhotoUri");
        mPhotoTargetFolder = savedInstanceState.getString("photoTargetFolder");
    }


    /**
     * 跳转到选择图片
     */
    protected void startGallery(int max) {
        Intent intent = new Intent(this, SimpleImageActivity.class);
        intent.putExtra(SimpleImageActivity.SELECT_IMAGE_KEY, max);
        startActivityForResult(intent, MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 跳转到选择图片
     */
    protected void startGallery(List<ImageItem> selectImages, int max) {
        startGallery(selectImages, max, 0, 0);
    }

    public void startGallery(int minWidth, int minHeight) {
        SimpleImageActivity.launch(this, minWidth, minHeight, MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 跳转到多选图片
     */
    protected void startGallery(int max, int minWidth, int minHeight) {
        SimpleImageActivity.launch(this, null, max, minWidth, minHeight, true, MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 跳转到多选图片
     */
    protected void startGallery(List<ImageItem> selectImages, int max, int minWidth, int minHeight) {
        SimpleImageActivity.launch(this, selectImages, max, minWidth, minHeight, true, MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }


    private int outputX = 300;
    private int outputY = 300;

    private boolean isAspect = true;//是否等比例裁剪==用户自主设置比例

    public void setIsAspect(boolean isAspect) {
        this.isAspect = isAspect;
    }

    public void setOutputX(int outputX) {
        this.outputX = outputX;
    }

    public void setOutputY(int outputY) {
        this.outputY = outputY;
    }

    /**
     * 跳转系统裁剪
     *
     * @param uri
     */
    protected void startCutDown(Uri uri) {
        mTakePhotoUri = createTakePhotoUri();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // edit by rzq
        if (isAspect) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        } else {
            //修改宝宝封面，用户封面的比例尺寸
            intent.putExtra("aspectX", 4);
            intent.putExtra("aspectY", 3);
        }
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, MediaUtils.CUT_DOWN_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 跳转到系统拍照
     */
    public void startTakePhoto() {
        if (!FileUtils.existSDCard()) {
            Toast.makeText(getBaseContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        mTakePhotoUri = createTakePhotoUri();
        MediaUtils.takePhoto(this, mTakePhotoUri);
    }

    /**
     * 跳转到预览界面
     *
     * @param imageItems 图片集
     * @param position   从第几张开始，第一张为0
     * @param isResult   是否需要返回数据
     */
    protected void startPreview(List<ImageItem> imageItems, int position, boolean isDelete, boolean isResult) {
        Intent intent = new Intent(this, SimplePreviewActivity.class);
        PreviewData data = new PreviewData();
        data.setImageItems(imageItems);
        data.setDelete(isDelete);
        data.setIndex(position);
        intent.putExtra(SimplePreviewActivity.PREVIEW_TAG, data);
        if (isResult) {
            startActivityForResult(intent, MediaUtils.PREVIEW_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            startActivity(intent);
        }
    }

    /**
     * 跳转到预览界面
     *
     * @param imageItem 图片
     * @param isResult  是否需要返回数据
     */
    protected void startPreview(ImageItem imageItem, boolean isDelete, boolean isResult) {
        ArrayList<ImageItem> imgs = new ArrayList<>();
        imgs.add(imageItem);
        startPreview(imgs, 0, isDelete, isResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (MediaUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                takePhotoResult(data, mTakePhotoUri);
            } else if (MediaUtils.SINGLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                ArrayList<ImageItem> tmps = (ArrayList<ImageItem>) data.getSerializableExtra(SimpleImageActivity.SELECT_IMAGE_KEY);
//                selectImageResult(data, tmps);
            } else if (MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                ArrayList<ImageItem> tmps = (ArrayList<ImageItem>) data.getSerializableExtra(SimpleImageActivity.SELECT_IMAGE_KEY);
                selectMulImageResult(data, tmps);
            } else if (MediaUtils.CUT_DOWN_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                cutDownResult(data, mTakePhotoUri);
            } else if (MediaUtils.PREVIEW_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                ArrayList<ImageItem> items = (ArrayList<ImageItem>) data.getSerializableExtra(SimplePreviewActivity.PREVIEW_TAG);
                previewImageResult(data, items);
            }
        } else if (resultCode == RESULT_CANCELED) {

        }
    }

    /**
     * 拍照返回处理
     *
     * @param data
     * @param photoUri
     */
    protected void takePhotoResult(Intent data, Uri photoUri) {
    }

    /**
     * 选择图片返回处理
     *
     * @param data
     * @param imgs
     */
    public void selectImageResult(Intent data, ArrayList<ImageItem> imgs) {
    }


    /**
     * 大图预览返回处理
     *
     * @param data
     */
    protected void previewImageResult(Intent data, ArrayList<ImageItem> items) {
    }

    /**
     * 如果没有提供路径，调用生成默认的Uri,用来存放图片,可重写此方法更改拍照路径
     *
     * @return
     */
    protected Uri createTakePhotoUri() {
        return FileUtils.getOutputMediaFileUri(this, MediaUtils.MEDIA_TYPE_IMAGE, "" + getPackageName());
    }

    /**
     * 选择多张图片返回处理
     *
     * @param data
     * @param items
     */
    public void selectMulImageResult(Intent data, ArrayList<ImageItem> items) {
    }

    ;

    /**
     * 裁剪图片返回处理
     *
     * @param data
     * @param photoUri
     */
    public void cutDownResult(Intent data, Uri photoUri) {
    }
}
