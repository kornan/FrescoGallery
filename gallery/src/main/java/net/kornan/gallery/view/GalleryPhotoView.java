package net.kornan.gallery.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import uk.co.senab.photoview.PhotoView;

/**
 * @author: kornan
 * @date: 2016-03-31 16:20
 */
public class GalleryPhotoView extends PhotoView {
    private DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;

    public GalleryPhotoView(Context context) {
        this(context, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    public GalleryPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public GalleryPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        selfInit();
    }

    private void selfInit() {
        if (mDraweeHolder == null) {
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                    .setProgressBarImage(new ImageLoadingDrawable())
                    .setFadeDuration(300)
//                    .setFailureImage()
                    .build();
            mDraweeHolder = DraweeHolder.create(hierarchy, getContext());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    protected boolean verifyDrawable(Drawable dr) {
        super.verifyDrawable(dr);
        return dr == mDraweeHolder.getHierarchy().getTopLevelDrawable();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return mDraweeHolder.onTouchEvent(event) || super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片
     *
     * @param uri
     * @param options
     */
    public void setImageUri(Uri uri, Uri lowResUri, ResizeOptions options) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequest imageRequest = buildImageRequest(uri, options);
        ImageRequest lowResImageRequest = buildImageRequest(lowResUri, null);
//        ImageRequest lowResImageRequest = ImageRequest.fromUri(lowResUri);

        AbstractDraweeController controller = (AbstractDraweeController) buildDraweeController(imageRequest, lowResImageRequest, imagePipeline);
        mDraweeHolder.setController(controller);
        setImageDrawable(mDraweeHolder.getTopLevelDrawable());
    }

    /**
     * 加载图片
     *
     * @param uri
     * @param options
     */
    public void setImageUri(Uri uri, ResizeOptions options) {
        AbstractDraweeController controller = (AbstractDraweeController) buildDraweeController(buildImageRequest(uri, options), null, Fresco.getImagePipeline());
        mDraweeHolder.setController(controller);
        setImageDrawable(mDraweeHolder.getTopLevelDrawable());
    }


    private ImageRequest buildImageRequest(Uri uri, ResizeOptions options) {
        return ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(options)
                .setProgressiveRenderingEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setAutoRotateEnabled(true)
                .build();
    }

    private DraweeController buildDraweeController(ImageRequest imageRequest, ImageRequest lowResImageRequest, ImagePipeline imagePipeline) {
        final DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
        return Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(lowResImageRequest)
                .setImageRequest(imageRequest)
                .setOldController(mDraweeHolder.getController())
                .setRetainImageOnFailure(true)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        Log.e("setControllerListener", "onFinalImageSet " + id);
                        super.onFinalImageSet(id, imageInfo, animatable);

                        CloseableReference<CloseableImage> imageCloseableReference = null;
                        try {
                            imageCloseableReference = dataSource.getResult();
                            if (imageCloseableReference != null) {
                                final CloseableImage image = imageCloseableReference.get();
                                if (image != null && image instanceof CloseableStaticBitmap) {
                                    CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                    final Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                    if (bitmap != null) {
                                        setImageBitmap(bitmap);
                                        setScaleType(ScaleType.FIT_CENTER);
                                    }
                                }
                            }
                        } finally {
                            dataSource.close();
                            CloseableReference.closeSafely(imageCloseableReference);
                        }
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        Log.e("setControllerListener", "onFailure " + id);
//                        Toast.makeText(getContext(), "下载图片失败", Toast.LENGTH_SHORT).show();
                    }
                }).build();
    }
}