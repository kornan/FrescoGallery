package net.kornan.tools;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by KORNAN on 2016/3/9.
 *
 * @author: kornan
 * @date: 2016-03-09 19:12
 */
public class FileUtils {
    /**
     * 判断SDCard是否可用
     */
    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取应用默认目录
     *
     * @param context
     * @return
     */
    public static File getInitPackageFolder(Context context) {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getPackageName();
        File file = new File(dir);

        return file;
    }

    /**
     * 获取本地文件Uri
     *
     * @param type MEDIA_TYPE_IMAGE or MEDIA_TYPE_VIDEO
     * @param name 名称
     * @return Uri
     */
    public static Uri getOutputMediaFileUri(Context context, int type, String name, String folder) {
        return Uri.fromFile(getOutputMediaFile(context, type, name, folder));
    }

    public static Uri getOutputMediaFileUri(Context context, int type, String name) {
        return getOutputMediaFileUri(context, type, name, null);
    }

    /**
     * 获取本地文件File
     *
     * @param type
     * @return
     */
    private static File getOutputMediaFile(Context context, int type, String name, String folder) {
        File mediaStorageDir = null;
        try {
            if (!TextUtils.isEmpty(folder)) {
                mediaStorageDir = new File(folder);
            } else {
                mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        context.getPackageName());
            }
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

        File mediaFile;
        if (!TextUtils.isEmpty(name)) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + name);
        } else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            if (type == MediaUtils.MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + timeStamp + ".jpg");
            } else if (type == MediaUtils.MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "VID_" + timeStamp + ".mp4");
            } else {
                return null;
            }
        }


        return mediaFile;
    }

    /**
     * 获取SD大小
     *
     * @return
     */
    public static long getAllSize() {
        if (!existSDCard()) {
            return 0l;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();
        return availableBlocks * blockSize;
    }

    /**
     * 多个SD卡时 取外置SD卡
     *
     * @return
     */
    public static String getExternalStorageDirectory() {
        Map<String, String> map = System.getenv();
        String[] values = new String[map.values().size()];
        map.values().toArray(values);
        String path = values[values.length - 1];
        if (path.startsWith("/mnt/") && !Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                .equals(path)) {
            return path;
        } else {
            return null;
        }
    }

}
