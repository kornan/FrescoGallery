package net.kornan.gallery;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

/**
 * 自定定图片编辑界面
 *
 * @author: kornan
 * @date: 2016-03-09 09:23
 */
public class PhotoEditActivity extends AppCompatActivity {
    public final static String TAG = "PhotoEdit";
    private SimpleDraweeView drawee_image;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        drawee_image = (SimpleDraweeView) findViewById(R.id.drawee_image);
        initData();
    }

    private void initData() {
        imagePath = getIntent().getStringExtra(TAG);
        drawee_image.setImageURI(Uri.fromFile((new File(imagePath))));
    }

    protected void request() {

    }

}
