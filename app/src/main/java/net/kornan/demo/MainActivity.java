package net.kornan.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.ui.BaseGalleryActivity;
import net.kornan.gallery.ui.ImagesActivity;
import net.kornan.gallery.view.GridNoScrollView;
import net.kornan.tools.FileUtils;
import net.kornan.tools.MediaUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseGalleryActivity implements AdapterView.OnItemClickListener {
    private int SELECT_IMAGE_MAX = 20;

    @InjectView(R.id.gridview)
    GridNoScrollView gridview;

    @InjectView(R.id.btn_select)
    Button btn_select;

    int selectImageMax=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        gridview.setOnItemClickListener(this);
    }

//    @OnClick(R.id.btn_select)
//    void startSelect() {
//        Intent intent = new Intent(this, DemoActivity.class);
//        startActivity(intent);
//    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (gridview.getGridNoScrollAdapter().isCanAdd(position)) {
                if (!FileUtils.existSDCard()) {
                    Toast.makeText(getBaseContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectImageMax = SELECT_IMAGE_MAX - gridview.getDatas().size();
                startGallery(selectImageMax);
            } else {
//                startPreview(gridImageItem, position, true);
            }
    }

    @Override
    public void selectMulImageResult(Intent data, ArrayList<ImageItem> items) {
        if (items != null) {
            gridview.getDatas().addAll(items);
            gridview.getGridNoScrollAdapter().notifyDataSetChanged();
        }
    }

    /**
     * 跳转到选择图片
     */
    protected void startGallery(int max) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra(ImagesActivity.SELECT_IMAGE_KEY, max);
        startActivityForResult(intent, MediaUtils.MULTIPLE_SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }
}
