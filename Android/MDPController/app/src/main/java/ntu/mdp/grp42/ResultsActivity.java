package ntu.mdp.grp42;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;

import ntu.mdp.grp42.bluetooth.Constants;

public class ResultsActivity extends AppCompatActivity implements Constants {

    TableLayout imageTable;
    boolean imageTableDrawn = false;
    int fragmentHeight, fragmentWidth;
    static ArrayList<ImageView> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);


        imageTable = findViewById(R.id.imageTable);
        imageTable.getViewTreeObserver().addOnPreDrawListener( () -> {
            if (!imageTableDrawn) {
                initImageTable(0);
                imageTableDrawn = true;
            }
            return true;
        });

        Bundle bundle = getIntent().getExtras();
        updateImageResult((Bitmap[]) bundle.get(FINAL_RESULTS));
    }

    public void initImageTable(int imageNum) {
        if (imageNum > 8 || imageNum < 4)
            return;
        imageTable.removeAllViews();
        int imageId = 1;
        int rowCount = (int) Math.ceil(imageNum / 2.0);
        imageList = new ArrayList<ImageView>();
        for (int y = 0; y < rowCount; y++) {
            TableRow row = new TableRow(this);
            row.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

            for (int x = 0; x < 2; x++) {
                ImageView imageView = new ImageView(this);
                imageView.setVisibility(View.INVISIBLE);
                imageView.setId(imageId++);
                imageView.setLayoutParams(new TableRow.LayoutParams(fragmentWidth / 2, fragmentHeight / rowCount));
                imageList.add(imageView);
                row.addView(imageView);
            }
            imageTable.addView(row);
        }
    }

    public void updateImageResult(Bitmap[] images) {
        try {
            for (int i = 0; i < images.length; i++) {
                ImageView imageView = imageList.get(i);
                imageView.setImageBitmap(images[i]);
                imageView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(ResultsActivity.this, "Error updating final results " + e, Toast.LENGTH_SHORT).show();
        }
    }
}