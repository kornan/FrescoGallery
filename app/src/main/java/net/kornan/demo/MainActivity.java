package net.kornan.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import net.kornan.gallery.factory.ImageItem;
import net.kornan.gallery.ui.GalleryBaseActivity;
import net.kornan.gallery.view.GridNoScrollView;
import net.kornan.tools.FileUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends GalleryBaseActivity implements AdapterView.OnItemClickListener {
    private static final int SELECT_IMAGE_MAX = 20;

    @InjectView(R.id.gridview)
    GridNoScrollView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        gridview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (gridview.getGridNoScrollAdapter().isCanAdd(position)) {
                if (!FileUtils.existSDCard()) {
                    Toast.makeText(getBaseContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
//                selectImageMax = SELECT_IMAGE_MAX - gridview.getDatas().size();
//                startGallery(selectImageMax);
                startGallery(gridview.getDatas(),SELECT_IMAGE_MAX);
            } else {
                startPreview(gridview.getDatas(),position,true,true);
            }
    }

    @Override
    public void selectMulImageResult(Intent data, ArrayList<ImageItem> items) {
        if (items != null) {
            gridview.getDatas().clear();
            gridview.getDatas().addAll(items);
            gridview.getGridNoScrollAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void previewImageResult(Intent data, ArrayList<ImageItem> items) {
        if(items!=null){
            gridview.getDatas().clear();
            gridview.getDatas().addAll(items);
            gridview.getGridNoScrollAdapter().notifyDataSetChanged();
        }
    }
}
