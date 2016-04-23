package net.kornan.tools;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
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
     * @param context
     * @return
     */
    public static File getInitPackageFolder(Context context){
        String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+context.getPackageName();
        File file=new File(dir);

        return file;
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
