package net.kornan.gallery.factory;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取图片集
 *
 * @author: kornan
 * @date: 2016-04-23 16:50
 */
public class AlbumHelper implements IAlbum {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private ContentResolver cr;
    private static AlbumHelper instance;
    /**
     * 是否创建了图片集
     */
    private boolean hasBuildImagesBucketList = false;
    /**
     * 缩略图列表
     */
    HashMap<String, String> thumbnailList = new HashMap<>();
    /**
     * 专辑列表
     */
    HashMap<String, ImageBucket> bucketList = new HashMap<>();
    /**
     * 所有图片列表
     */
    List<ImageItem> imageItems = new ArrayList<>();

    /**
     * 图片总数
     */
    private int totalNum = 0;
    /**
     * 是否已获取所有图片列表
     */
    private boolean hasBuildImagesItemList = false;

    private AlbumHelper() {
    }

    public synchronized static AlbumHelper getHelper() {
        if (instance == null) {
            instance = new AlbumHelper();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public synchronized void init(Context context) {
        init(context, 0, 0);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public synchronized void init(Context context, int minWidth, int minHeight) {
        if (this.context == null) {
            this.context = context;
            cr = context.getContentResolver();
        }
        AlbumFilter.getInstance(context).setFilter(minWidth, minHeight);
    }

    /**
     * 刷新数据
     *
     * @return
     */
    public List<ImageItem> refresh() {
        imageItems.clear();
        hasBuildImagesItemList = false;
        return getAllImagesItemList();
    }

    /**
     * 刷新数据
     *
     * @return
     */
    public List<ImageItem> refresh(int minWidth, int minHeight) {
        AlbumFilter.getInstance(context).setFilter(minWidth, minHeight);
        return refresh();
    }

    /**
     * 添加第一项为拍照
     */
    private void addTakePhone() {
        if (imageItems.size() > 0 && imageItems.get(0).type == ImageItem.Type.CAMERA) {
            return;
        }
        ImageItem item = new ImageItem();
        item.type = ImageItem.Type.CAMERA;
        imageItems.add(0, item);
    }

    /**
     * 移除第一项拍照
     *
     * @return
     */
    private void removeTakePhone() {
        if (imageItems.size() > 0 && imageItems.get(0).type == ImageItem.Type.CAMERA) {
            imageItems.remove(0);
        }
    }

    @Override
    public List<ImageItem> getAllImagesItemList() {
        if (!hasBuildImagesItemList) {
            buildImagesBucketList();
        }
        addTakePhone();
        return imageItems;
    }

    @Override
    public HashMap<String, ImageBucket> getAllImagesBucketList() {
        if (!hasBuildImagesItemList) {
            buildImagesBucketList();
        }
        return bucketList;
    }


    private void buildImagesBucketList() {

        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_MODIFIED};

        Cursor cur = cr.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                "width >=? and height>=?",
                new String[]{"" + AlbumFilter.getInstance(context).getMinWidth(), "" + AlbumFilter.getInstance(context).getMinHeight()},
                MediaStore.Images.Media._ID + " desc"
        );

        imageItems.clear();
        bucketList.clear();
        if (cur != null && cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.PICASA_ID);
            int dateModifiedIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED);
            totalNum = cur.getCount();

            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);
                long dateModified = cur.getLong(dateModifiedIndex);
//                Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: "
//                        + picasaId + " name:" + name + " path:" + path
//                        + " title: " + title + " size: " + size + " bucket: "
//                        + bucketName + "---");

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    bucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<>();
                    bucket.bucketName = bucketName;
                }

                bucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
                imageItem.dateModified = dateModified;
//                imageItem.thumbnailPath = thumbnailList.get(_id);//部分手机没有.thumbnails，或者已经被某些清理软件清除
                bucket.path = new File(path).getParentFile().getAbsolutePath();
                bucket.imageList.add(imageItem);
                imageItems.add(imageItem);

            } while (cur.moveToNext());
        }
//        addTakePhone();
        hasBuildImagesItemList = true;
    }

    @Override
    public HashMap<String, String> getAllThumbnail() {
        return null;
    }

    /**
     * 得到图片集
     *
     * @return
     */
    public List<ImageBucket> getImagesBucketList() {
        if (!hasBuildImagesBucketList) {
            getAllImagesBucketList();
        }
        List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
        for (Map.Entry<String, ImageBucket> entry : bucketList.entrySet()) {
            tmpList.add(entry.getValue());
        }
        addAllImageBucket(tmpList);
        return tmpList;
    }

    /**
     * 添加所有图片bucket
     *
     * @param datas 列表
     */
    private void addAllImageBucket(List<ImageBucket> datas) {
        ImageBucket bucket = new ImageBucket();
        bucket.imageList = new ArrayList<>();
        bucket.imageList.addAll(imageItems);
        bucket.bucketName = "所有图片";
        bucket.count = totalNum;
        datas.add(0, bucket);
    }

//    @Override
//    public HashMap<String, String> getAllThumbnail() {
//        String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
//                MediaStore.Images.Thumbnails.DATA};
//        Cursor cur = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
//                null, null, null);
//
//        if (cur != null && cur.moveToFirst()) {
//            int _id;
//            int image_id;
//            String image_path;
//            int _idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails._ID);
//            int image_idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
//            int dataColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
//
//            do {
//                _id = cur.getInt(_idColumn);
//                image_id = cur.getInt(image_idColumn);
//                image_path = cur.getString(dataColumn);
//
//                thumbnailList.put("" + image_id, image_path);
//            } while (cur.moveToNext());
//        }
//        return thumbnailList;
//    }
//    /**
//     * Try to return the absolute file path from the given Uri
//     *
//     * @param context
//     * @param uri
//     * @return the file path or null
//     */
//    public static String getRealFilePath(final Context context, final Uri uri) {
//        if (null == uri) return null;
//        final String scheme = uri.getScheme();
//        String data = null;
//        if (scheme == null)
//            data = uri.getPath();
//        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
//            data = uri.getPath();
//        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
//            if (null != cursor) {
//                if (cursor.moveToFirst()) {
//                    int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                    if (index > -1) {
//                        data = cursor.getString(index);
//                    }
//                }
//                cursor.close();
//            }
//        }
//        return data;
//    }

    public static ImageItem getImageItem(final Context context, final Uri uri) {
        if (null == uri) return null;
        ImageItem imageItem = null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int photoIDIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    int photoPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (photoPathIndex > -1) {
                        String _id = cursor.getString(photoIDIndex);
                        String path = cursor.getString(photoPathIndex);

                        imageItem = new ImageItem();
                        imageItem.imageId = _id;
                        imageItem.imagePath = path;
//                imageItem.thumbnailPath = thumbnailList.get(_id);//部分手机没有.thumbnails，或者已经被某些清理软件清除
                    }
                }
                cursor.close();
            }
        }
        return imageItem;
    }

//    /**
//     * 得到原始图像路径
//     *
//     * @param image_id
//     * @return
//     */
//    public String getOriginalImagePath(String image_id) {
//        String path = null;
//        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
//        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
//                MediaStore.Images.Media._ID + "=" + image_id, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//
//        }
//        return path;
//    }
//
//    /**
//     * 重置选中项
//     */
//    public void reset() {
//        Iterator<Map.Entry<String, ImageBucket>> itr = bucketList.entrySet()
//                .iterator();
//        while (itr.hasNext()) {
//            Map.Entry<String, ImageBucket> entry = itr
//                    .next();
//            ImageBucket bucket = entry.getValue();
//            Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
//                    + bucket.count + " ---------- ");
//            for (int i = 0; i < bucket.imageList.size(); ++i) {
//                ImageItem image = bucket.imageList.get(i);
//                image.isSelected = false;
//                Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath
//                        + ", " + image.thumbnailPath);
//            }
//        }
//    }

    /**
     * 取消选中项
     */
    public void reset(List<ImageItem> items) {
        for (ImageItem item : items) {
            item.isSelected = false;
        }
    }

    /**
     * 重置项
     */
    public void reset(List<ImageItem> items, boolean flag) {
        for (ImageItem item : items) {
            item.isSelected = flag;
        }
    }

    /**
     * 重置项
     */
    public void reset() {
        imageItems.clear();
        hasBuildImagesItemList = false;
    }

}
