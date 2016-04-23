package net.kornan.gallery;

import android.app.Application;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author: kornan
 * @date: 2016-03-31 16:27
 */
public class GalleryHelper {

    /**
     * 初始化Gallery
     *
     * @param application
     */
    public static void init(Application application) {

        File diskCache;
        diskCache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + application.getPackageName());//设置缓存目录

        if (!diskCache.exists()) {
            diskCache.mkdirs();
        }

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(application)
                .setBaseDirectoryPath(diskCache)
                .build();

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(application)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setDownsampleEnabled(true)//如果false，只支持JPG;
                .setMainDiskCacheConfig(diskCacheConfig)//设置本地缓存目录
                .build();

        Fresco.initialize(application, config);
    }
}
