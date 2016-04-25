package net.kornan.gallery.factory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by KORNAN on 2016/4/23.
 *
 * @author: kornan
 * @date: 2016-04-23 16:14
 */
public interface IAlbum {
    //获取所有图片
    List<ImageItem> getAllImagesItemList();

    //获取所有图片集
    HashMap<String, ImageBucket> getAllImagesBucketList();

    //获取系统所有缩略图
    HashMap<String, String> getAllThumbnail();
}
