package net.kornan.gallery.factory;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by KORNAN on 2016/3/5.
 *
 * @author: kornan
 * @date: 2016-03-05 17:37
 */
public class ImageHandler extends Handler {
    WeakReference<Activity> mActivityReference;
    private HandlerCallback handlerCallback;

    public interface HandlerCallback {
        void handleMessage(Message msg);
    }

    public void setHandlerCallback(HandlerCallback callback) {
        this.handlerCallback = callback;
    }

    public ImageHandler(Activity activity) {
        mActivityReference = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        if (handlerCallback != null)
            handlerCallback.handleMessage(msg);
    }

}
