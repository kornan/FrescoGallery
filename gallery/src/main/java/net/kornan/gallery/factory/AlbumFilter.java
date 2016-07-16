package net.kornan.gallery.factory;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by KORNAN on 2016/4/23.
 * 图片过滤
 *
 * @author: kornan
 * @date: 2016-04-23 17:09
 */
public class AlbumFilter {
    private final static String TAG = AlbumFilter.class.getSimpleName();
    private final static String TAG_FILTER = "album_filter";
    private final static String MIN_WIDTH = "min_width";
    private final static String MIN_HEIGHT = "min_height";

    private static AlbumFilter instance;
    private SharedPreferences sharedPreferences;

    private AlbumFilter(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(TAG_FILTER, Context.MODE_PRIVATE);
    }

    public synchronized static AlbumFilter getInstance(Context context) {
        if (instance == null) {
            instance = new AlbumFilter(context);
        }
        return instance;
    }

    public void setFilter(int minWidth, int minHeight) {
        sharedPreferences.edit().putInt(MIN_WIDTH, minWidth).putInt(MIN_HEIGHT, minHeight).apply();
    }

    public int getMinWidth() {
        return sharedPreferences.getInt(MIN_WIDTH, 0);
    }

    public int getMinHeight() {
        return sharedPreferences.getInt(MIN_HEIGHT, 0);
    }
}

