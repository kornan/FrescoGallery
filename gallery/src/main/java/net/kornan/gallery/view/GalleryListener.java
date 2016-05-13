package net.kornan.gallery.view;

/**
 * Created by kornan on 16/5/10.
 */
public interface GalleryListener {
    //预览
    void showBigImage();

    //完成
    void complete();

    //取消
    void cancel();
}
