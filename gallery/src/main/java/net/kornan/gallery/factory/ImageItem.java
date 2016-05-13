package net.kornan.gallery.factory;

import java.io.Serializable;

/**
 * 一个图片对象
 *
 * @author kornan
 */
public class ImageItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        IMAGE, CAMERA
    }

    /**
     * 图片_id
     */
    public String imageId;
    /**
     * 缩略图路径（某些手机没有）
     */
    public String thumbnailPath;
    /**
     * 图片路径_data
     */
    public String imagePath;
    /**
     * 图片是否已选
     */
    public boolean isSelected = false;

    /**
     * item类型：CAMERA为拍照,默认为IMAGE
     */
    public Type type=Type.IMAGE;
}
