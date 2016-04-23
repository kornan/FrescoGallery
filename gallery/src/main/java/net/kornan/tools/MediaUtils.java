package net.kornan.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author: kornan
 * @date: 2016-03-03 09:59
 */
public class MediaUtils {
    private final static String TAG = "MediaUtils";
    /**
     * 拍照
     */
    public static final int MEDIA_TYPE_IMAGE = 0x000001;
    /**
     * 视频
     */
    public static final int MEDIA_TYPE_VIDEO = 0x000002;
    /**
     * 拍照返回值
     */
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0x000003;
    /**
     * 摄影返回值
     */
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 0x000004;
    /**
     * 相册返回值(多选)
     */
    public static final int MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 0x000005;
    /**
     * 相册返回值(多选)
     */
    public static final int SINGLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 0x000006;
    /**
     * 裁减
     */
    public static final int CUT_DOWN_IMAGE_ACTIVITY_REQUEST_CODE = 0x000007;

    /**
     * 大图预览返回值
     */
    public static final int PREVIEW_IMAGE_ACTIVITY_REQUEST_CODE = 0x000008;

    /**
     * 获取本地文件Uri
     *
     * @param type MEDIA_TYPE_IMAGE or MEDIA_TYPE_VIDEO
     * @param name 名称
     * @return Uri
     */
    public static Uri getOutputMediaFileUri(int type, String name) {
        return Uri.fromFile(getOutputMediaFile(type, name));
    }

    /**
     * 获取本地文件File
     *
     * @param type
     * @return
     */
    private static File getOutputMediaFile(int type, String name) {
        File mediaStorageDir = null;
        try {
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    name);
            Log.d("TAG", "Successfully created mediaStorageDir: "
                    + mediaStorageDir);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "Error in Creating mediaStorageDir: "
                    + mediaStorageDir);
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("TAG",
                        "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * 跳转Activity
     *
     * @param cls    class
     * @param bundle 传递数据
     */
    public static void startActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 跳转Activity
     *
     * @param cls class
     */
    public static void startActivity(Context context, Class<?> cls) {
        startActivity(context, cls, null);
    }

    /**
     * 调用系统相机拍照
     */
    public static void takePhoto(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, MediaUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 调用系统相机拍照
     */
    public static void takePhoto(Activity activity, Uri fileUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (fileUri == null)
            fileUri = MediaUtils.getOutputMediaFileUri(MediaUtils.MEDIA_TYPE_IMAGE, "" + activity.getPackageName());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        activity.startActivityForResult(intent, MediaUtils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 获取最后一次拍照的图片
     *
     * @param context
     * @return
     */
    public static String getLatestCameraPicture(Context context) {

        if (!FileUtils.existSDCard()) {
            return null;
        }

        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor.moveToFirst()) {
            String path = cursor.getString(1);
            return path;
        }
        return null;
    }

    /**
     * 获取手机大小
     *
     * @param activity
     * @return
     */
    public static DisplayMetrics getScreenDimen(Activity activity) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(displaysMetrics);
        return displaysMetrics;
    }



    /**
     * 旋转图片
     *
     * @param path
     * @param orientation
     * @param screenWidth
     * @param screenHeight
     * @return
     */
    public static Bitmap rotateBitmap(String path, int orientation, int screenWidth, int screenHeight) {
        Bitmap bitmap = null;
        final int maxWidth = screenWidth / 2;
        final int maxHeight = screenHeight / 2;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int sourceWidth, sourceHeight;
            if (orientation == 90 || orientation == 270) {
                sourceWidth = options.outHeight;
                sourceHeight = options.outWidth;
            } else {
                sourceWidth = options.outWidth;
                sourceHeight = options.outHeight;
            }
            boolean compress = false;
            if (sourceWidth > maxWidth || sourceHeight > maxHeight) {
                float widthRatio = (float) sourceWidth / (float) maxWidth;
                float heightRatio = (float) sourceHeight / (float) maxHeight;

                options.inJustDecodeBounds = false;
                if (new File(path).length() > 512000) {
                    float maxRatio = Math.max(widthRatio, heightRatio);
                    options.inSampleSize = (int) maxRatio;
                    compress = true;
                }
                bitmap = BitmapFactory.decodeFile(path, options);
            } else {
                bitmap = BitmapFactory.decodeFile(path);
            }
            if (orientation > 0) {
                Matrix matrix = new Matrix();
                //matrix.postScale(sourceWidth, sourceHeight);
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            sourceWidth = bitmap.getWidth();
            sourceHeight = bitmap.getHeight();
            if ((sourceWidth > maxWidth || sourceHeight > maxHeight) && compress) {
                float widthRatio = (float) sourceWidth / (float) maxWidth;
                float heightRatio = (float) sourceHeight / (float) maxHeight;
                float maxRatio = Math.max(widthRatio, heightRatio);
                sourceWidth = (int) ((float) sourceWidth / maxRatio);
                sourceHeight = (int) ((float) sourceHeight / maxRatio);
                Bitmap bm = Bitmap.createScaledBitmap(bitmap, sourceWidth, sourceHeight, true);
                bitmap.recycle();
                return bm;
            }
        } catch (Exception e) {
        }
        return bitmap;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

}
