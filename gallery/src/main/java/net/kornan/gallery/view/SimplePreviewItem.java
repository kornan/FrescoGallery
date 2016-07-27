package net.kornan.gallery.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import net.kornan.gallery.R;
import net.kornan.gallery.factory.ImageItem;
import net.kornan.tools.ImageUtils;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Introduction:
 * Created by KORNAN on 2016/7/7.
 */
public class SimplePreviewItem extends RelativeLayout implements GalleryPhotoView.PhotoControllerListener {

    private GalleryPhotoView galleryBigImage;
    private SimpleDraweeView gallerySmallImage;
    private ImageItem imageItem;

    public SimplePreviewItem(Context context) {
        super(context);
        init(context, null);
    }

    public SimplePreviewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SimplePreviewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener) {
        if (galleryBigImage != null) {
            galleryBigImage.setOnPhotoTapListener(onPhotoTapListener);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.simple_preview_item, this);
        galleryBigImage = (GalleryPhotoView) findViewById(R.id.gallery_big_image);
        gallerySmallImage = (SimpleDraweeView) findViewById(R.id.gallery_small_image);

        galleryBigImage.setPhotoControllerListener(this);
    }

    public void loadImage(ImageItem imageItem) {
        this.imageItem = imageItem;
        Uri thumbUri = ImageUtils.getUri(imageItem.thumbnailPath);
        Uri bigUri = ImageUtils.getUri(imageItem.imagePath);

        if (thumbUri != null)
            ImageUtils.resizeImageViewForScreen(gallerySmallImage, thumbUri, 120, 120);

        galleryBigImage.setImageUri(bigUri, null);

    }

    @Override
    public void onFailure(String id) {

    }

    @Override
    public void onFinalImageSet(String id) {
        gallerySmallImage.setVisibility(GONE);
    }
}
