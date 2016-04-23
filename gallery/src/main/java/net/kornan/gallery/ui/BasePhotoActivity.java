package net.kornan.gallery.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;

import net.kornan.gallery.PhotoEditActivity;
import net.kornan.gallery.factory.AlbumHelper;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.factory.PreviewData;
import net.kornan.gallery.view.GridNoScrollAdapter;
import net.kornan.gallery.view.GridNoScrollView;
import net.kornan.tools.FileUtils;
import net.kornan.tools.MediaUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by KORNAN on 2016/3/9.
 *
 * @author: kornan
 * @date: 2016-03-09 09:23
 */
public abstract class BasePhotoActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener {

    private int SELECT_IMAGE_MAX = 9;

    protected static String mPhotoTargetFolder;
    protected Uri mTakePhotoUri;
    protected int mScreenWidth = 720;
    protected int mScreenHeight = 1280;

    protected AlbumHelper helper;

    protected GridNoScrollView gridNoScrollView;
    protected GridNoScrollAdapter gridNoScrollAdapter;
    protected int selectImageMax = 9;
    protected int selectPosition;
    protected ArrayList<ImageItem> gridImageItem = new ArrayList<>();

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
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
    }

    /**
     * 多图选择返回必需初始化手动gridNoScrollView
     */
    protected void initGrid(GridNoScrollView gridNoScrollView) {
        this.gridNoScrollView = gridNoScrollView;
        gridNoScrollAdapter = new GridNoScrollAdapter(this);
        gridImageItem = new ArrayList<>();
        gridNoScrollView.setAdapter(gridNoScrollAdapter);
        gridNoScrollView.setOnItemClickListener(gridNoscrollItemClickListener);
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

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                startTakePhoto();
                break;
            case 1:
                if (!FileUtils.existSDCard()) {
                    Toast.makeText(getBaseContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectImageMax = SELECT_IMAGE_MAX - gridImageItem.size();
                startGallery(selectImageMax);
                break;
            case 2:
                break;
        }

    }

    public void setImageMax(int max) {
        SELECT_IMAGE_MAX = selectImageMax = max;
    }

    /**
     * 跳转到系统拍照
     */
    public void startTakePhoto() {
        if (!FileUtils.existSDCard()) {
            Toast.makeText(getBaseContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        mTakePhotoUri = MediaUtils.getOutputMediaFileUri(MediaUtils.MEDIA_TYPE_IMAGE, "" + getPackageName());
        MediaUtils.takePhoto(this, mTakePhotoUri);
    }

    /**
     * 跳转到选择图片
     */
    protected void startGallery(int max) {
        Intent intent = new Intent(this, ImagesActivity.class);
        intent.putExtra(ImagesActivity.SELECT_IMAGE_KEY, max);
        startActivityForResult(intent, MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 跳转到图片编辑
     */
    protected void startEditPhoto(String path) {
        Intent intent = new Intent(this, PhotoEditActivity.class);
        intent.putExtra(PhotoEditActivity.TAG, path);
        startActivityForResult(intent, MediaUtils.CUT_DOWN_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 弹出框
     */
    public void showSheet() {
        ActionSheet.createBuilder(this, this.getSupportFragmentManager()).setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("拍照", "相册")
                .setCancelButtonTitle("取消")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();
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

    protected void startCutDown(Uri uri) {
        mTakePhotoUri = MediaUtils.getOutputMediaFileUri(MediaUtils.MEDIA_TYPE_IMAGE, getPackageName());
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
     * 跳转到预览界面
     *
     * @param imageItems 图片集
     * @param position   从第几张开始，第一张为0
     * @param isResult   是否需要返回数据
     */
    protected void startPreview(ArrayList<ImageItem> imageItems, int position, boolean isResult) {
        Intent intent = new Intent(this, GalleryPreviewActivity.class);
        PreviewData data = new PreviewData();
        data.setImageItems(imageItems);
        data.setDelete(true);
        data.setIndex(position);
        intent.putExtra(GalleryPreviewActivity.PREVIEW_TAG, data);
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
    protected void startPreview(ImageItem imageItem, boolean isResult) {
        ArrayList<ImageItem> imgs = new ArrayList<>();
        imgs.add(imageItem);
        startPreview(imgs, 0, isResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (MediaUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                takePhotoResult(data, mTakePhotoUri);
            } else if (MediaUtils.SINGLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                ArrayList<ImageItem> tmps = (ArrayList<ImageItem>) data.getSerializableExtra(ImagesActivity.SELECT_IMAGE_KEY);
                selectImageResult(data, tmps);
            } else if (MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                ArrayList<ImageItem> tmps = (ArrayList<ImageItem>) data.getSerializableExtra(ImagesActivity.SELECT_IMAGE_KEY);
                selectMulImageResult(data, tmps);
            } else if (MediaUtils.CUT_DOWN_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                cutDownResult(data, mTakePhotoUri);
            } else if (MediaUtils.PREVIEW_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
                previewImageResult(data);
            }
        } else if (resultCode == RESULT_CANCELED) {
            photoCanceled();
        }
    }

    /**
     * 进入相册，然后取消操作
     */
    protected void photoCanceled() {

    }

    /**
     * 大图预览返回处理
     *
     * @param data
     */
    public void previewImageResult(Intent data) {
        ArrayList<ImageItem> items = (ArrayList<ImageItem>) data.getSerializableExtra(GalleryPreviewActivity.PREVIEW_TAG);

        if (gridNoScrollAdapter != null) {
            gridImageItem.clear();
            gridNoScrollAdapter.getList().clear();
            if (data != null) {
                gridImageItem.addAll(items);
                for (ImageItem item : items) {
                    gridNoScrollAdapter.add(Uri.fromFile(new File(item.imagePath)));
                }
            }
            gridNoScrollAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 拍照返回处理
     *
     * @param data
     * @param photoUri
     */
    public void takePhotoResult(Intent data, Uri photoUri) {
        ImageItem imageItem = new ImageItem();
        imageItem.imagePath = photoUri.getPath();
        gridImageItem.add(imageItem);
        if (gridNoScrollAdapter != null) {
            gridNoScrollAdapter.add(Uri.fromFile(new File(imageItem.imagePath)));
            gridNoScrollAdapter.notifyDataSetChanged();
        }
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
     * 选择多张图片返回处理
     *
     * @param data
     * @param items
     */
    public void selectMulImageResult(Intent data, ArrayList<ImageItem> items) {
        if (data != null) {
            gridImageItem.addAll(items);
            if (gridNoScrollAdapter != null) {
                for (ImageItem item : items) {
                    gridNoScrollAdapter.add(Uri.fromFile(new File(item.imagePath)));
                }
                gridNoScrollAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 裁剪图片返回处理
     *
     * @param data
     * @param photoUri
     */
    public void cutDownResult(Intent data, Uri photoUri) {
    }

    /**
     * 多图ItemClick
     */
    private AdapterView.OnItemClickListener gridNoscrollItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectPosition = position;
            if (gridNoScrollAdapter.isCanAdd(selectPosition)) {
                showSheet();
            } else {
                startPreview(gridImageItem, position, true);
            }
        }
    };
}
