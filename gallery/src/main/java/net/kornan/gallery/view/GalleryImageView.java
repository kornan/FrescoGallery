package net.kornan.gallery.view;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.DraweeHolder;

/**
 * Created by KORNAN on 2016/4/5.
 *
 * @author: kornan
 * @date: 2016-04-05 18:40
 */
public class GalleryImageView extends DraweeHolder<GenericDraweeHierarchy> {


    /**
     * Creates a new instance of DraweeHolder.
     *
     * @param hierarchy
     */
    public GalleryImageView(GenericDraweeHierarchy hierarchy) {
        super(hierarchy);
    }

    @Override
    public void onDraw() {
        try {
            super.onDraw();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
