package net.kornan.gallery.view;

import android.media.MediaScannerConnection;
import android.view.View;

import net.kornan.gallery.factory.ImageBucket;

/**
 * 相册listenter
 * Created by kornan on 16/5/10.
 */
public interface GalleryListener extends MediaScannerConnection.MediaScannerConnectionClient{
    //
    void OnBucketChange(View view ,int position);
}
