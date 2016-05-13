package net.kornan.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
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
}
