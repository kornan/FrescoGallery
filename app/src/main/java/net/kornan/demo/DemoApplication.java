package net.kornan.demo;

import android.app.Application;

import net.kornan.gallery.GalleryHelper;

/**
 * Created by KORNAN on 2016/4/19.
 *
 * @author: kornan
 * @date: 2016-04-19 11:02
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GalleryHelper.init(this);
    }
}
