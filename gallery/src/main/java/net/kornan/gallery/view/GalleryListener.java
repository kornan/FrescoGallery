package net.kornan.gallery.view;

import android.media.MediaScannerConnection;

/**
 * 相册listenter
 * Created by kornan on 16/5/10.
 */
public interface GalleryListener extends MediaScannerConnection.MediaScannerConnectionClient{
    //预览
    void showBigImage();

    //完成
    void complete();

    //取消
    void cancel();
}
