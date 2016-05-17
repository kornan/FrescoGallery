package net.kornan.gallery.factory;

import net.kornan.gallery.factory.ImageItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KORNAN on 2016/3/12.
 *
 * @author: kornan
 * @date: 2016-03-12 14:12
 */
public class PreviewData implements Serializable {
    private List<ImageItem> imageItems;
    private boolean delete=false;
    private int index=0;

    public List<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
