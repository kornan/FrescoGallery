package net.kornan.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public final static String scheme[] = {"http://", "https://", "file://", "content://", "asset://", "res://"};

    public static Uri getUri(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) return null;
        for (String str : scheme) {
            if (imagePath.contains(str)) {
                return Uri.parse(imagePath);
            }
        }
        return Uri.fromFile(new File(imagePath));
    }

    // 压缩图片
    public static File[] compressImage(String path, File... files) {
        Bitmap bitmap = null;
        if (path == null)
            return files;
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        for (int i = 0; i < files.length; i++) {
            try {
                bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath());
                String file_name = files[i].getName();
                String sDStateString = android.os.Environment
                        .getExternalStorageState();
                if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
                    File SDFile = android.os.Environment
                            .getExternalStorageDirectory();
                    File myFile = new File(SDFile.getAbsolutePath()
                            + File.separator + file_name);

                    if (!myFile.exists()) {
                        myFile.createNewFile();
                    }
                    writePath(
                            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                                    480, 800), Bitmap.CompressFormat.JPEG, myFile, 90);
                    files[i] = myFile;
                }
                // writePath(ThumbnailUtils.extractThumbnail(bitmap, 480,
                // 800),);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error err) {
                err.printStackTrace();
            }
        }
        return files;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap
     * @param format
     * @param target
     */
    public static void writePath(Bitmap bitmap, Bitmap.CompressFormat format, File target, int quality) {
        if (target.exists()) {
            target.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(format, quality, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void resizeImageViewForScreen(SimpleDraweeView imageView, Uri uri, int width, int height) {
        GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP);
//        Log.e("Controller","uri "+uri);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(imageView.getController())
                .setImageRequest(request)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
//                        Log.e("Controller","onFinalImageSet "+id);
                        super.onFinalImageSet(id, imageInfo, animatable);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
//                        Log.e("Controller","onFailure "+id);
                        super.onFailure(id, throwable);
                    }
                })
                .build();
        imageView.setController(controller);
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
//        final int maxWidth = screenWidth / 2;
//        final int maxHeight = screenHeight / 2;

        final int maxWidth = screenWidth;
        final int maxHeight = screenHeight;
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
//            LogUtils.d("orientation=" + orientation);
            switch (orientation) {
//                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
//                    break;
//                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
//                    break;
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


    /**
     * @param path         图片路径
     * @param orientation  角度
     * @param screenWidth  宽度
     * @param screenHeight 高度
     * @param quality      质量
     */
    public static void setImageRotate(String path, int orientation, int screenWidth, int screenHeight, int quality) {
        try {
            if (!TextUtils.isEmpty(path)) {
                int degree = getBitmapDegree(path);
                if (degree != 0) {
                    Bitmap bitmap = rotateBitmap(path, orientation, screenWidth, screenHeight);
                    ImageUtils.writePath(bitmap, Bitmap.CompressFormat.JPEG, new File(path), 90);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }*/

}
