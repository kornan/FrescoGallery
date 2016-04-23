package net.kornan.gallery.factory;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by KORNAN on 2016/4/23.
 *
 * @author: kornan
 * @date: 2016-04-23 16:50
 */
public class AlbumHelper implements AlbumInterface {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private ContentResolver cr;
    private static AlbumHelper instance;
    /**
     * 是否创建了图片集
     */
    private boolean hasBuildImagesBucketList = false;
    /*
     * 缩略图列表
     */
    HashMap<String, String> thumbnailList = new HashMap<>();
    /*
     * 专辑列表
     */
    HashMap<String, ImageBucket> bucketList = new HashMap<>();
    /*
     * 所有图片列表
     */
    List<ImageItem> imageItems = new ArrayList<>();

    private AlbumHelper() {
    }

    public static AlbumHelper getHelper() {
        if (instance == null) {
            instance = new AlbumHelper();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
            cr = context.getContentResolver();
        }
    }

    @Override
    public List<ImageItem> getAllImagesItemList() {
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cur;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, MediaStore.Images.Media.WIDTH + " >=300 and " + MediaStore.Images.Media.HEIGHT + ">=300", null,
                    MediaStore.Images.Media._ID + " desc");
        } else {
            cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                    MediaStore.Images.Media._ID + " desc");
        }
        if (cur != null && cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                String _id = cur.getString(photoIDIndex);
                String path = cur.getString(photoPathIndex);

                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
//                imageItem.thumbnailPath = thumbnailList.get(_id);//部分手机没有.thumbnails，或者已经被某些清理软件清除
                imageItems.add(imageItem);

            } while (cur.moveToNext());
        }
        return imageItems;
    }

    @Override
    public HashMap<String, ImageBucket> getAllImagesBucketList() {
// 构造相册索引
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        // 得到一个游标
        Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        if (cur != null && cur.moveToFirst()) {
            // 获取指定列的索引
            int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.PICASA_ID);
            // 获取图片总数
            int totalNum = cur.getCount();

            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);

                Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: "
                        + picasaId + " name:" + name + " path:" + path
                        + " title: " + title + " size: " + size + " bucket: "
                        + bucketName + "---");

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
//                imageItem.thumbnailPath = thumbnailList.get(_id);//部分手机没有.thumbnails，或者已经被某些清理软件清除
                bucket.imageList.add(imageItem);

            } while (cur.moveToNext());
        }
        return bucketList;
    }

    @Override
    public HashMap<String, String> getAllThumbnail() {
        String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};
        Cursor cur = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);

        if (cur != null && cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

            do {
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
        return thumbnailList;
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 得到原始图像路径
     *
     * @param image_id
     * @return
     */
    public String getOriginalImagePath(String image_id) {
        String path = null;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Images.Media._ID + "=" + image_id, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        }
        return path;
    }

    /**
     * 重置选中项
     */
    public void reset() {
        Iterator<Map.Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ImageBucket> entry = itr
                    .next();
            ImageBucket bucket = entry.getValue();
            Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
                    + bucket.count + " ---------- ");
            for (int i = 0; i < bucket.imageList.size(); ++i) {
                ImageItem image = bucket.imageList.get(i);
                image.isSelected = false;
                Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath
                        + ", " + image.thumbnailPath);
            }
        }
    }

    /**
     * 取消选中项
     */
    public void reset(List<ImageItem> items) {

        for (ImageItem item : items) {
            item.isSelected = false;
        }
    }
}
