package ntu.mdp.grp42.fragment;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.TaskActivity;

public class ResultsFragment extends Fragment {

    TableLayout imageTable;
    boolean imageTableDrawn = false;
    int fragmentHeight, fragmentWidth;
    static ArrayList<ImageView> imageList;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static void updateImageResult(Bitmap image, int imageToUpdate) {
        ImageView imageView = imageList.get(imageToUpdate-1);
        imageView.setImageBitmap(image);
        imageView.setVisibility(View.VISIBLE);
        TaskActivity.imageToUpdate = -1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_results, container, false);

        view.post(new Runnable() {
            @Override
            public void run() {
                // for instance
                fragmentHeight = view.getMeasuredHeight();
                fragmentWidth = view.getMeasuredWidth();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imageTable = view.findViewById(R.id.imageTable);
        imageTable.getViewTreeObserver().addOnPreDrawListener( () -> {
            if (!imageTableDrawn) {
                initImageTable(0);
                imageTableDrawn = true;
            }
            return true;
        });
    }

    public void initImageTable(int imageNum) {
        if (imageNum > 8 || imageNum < 4)
            return;
        imageTable.removeAllViews();
        int imageId = 1;
        int rowCount = (int) Math.ceil(imageNum / 2.0);
        imageList = new ArrayList<ImageView>();
        for (int y = 0; y < rowCount; y++) {
            TableRow row = new TableRow(this.getContext());
            row.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

            for (int x = 0; x < 2; x++) {
                ImageView imageView = new ImageView(this.getContext());
                imageView.setVisibility(View.INVISIBLE);
                imageView.setId(imageId++);
                imageView.setLayoutParams(new TableRow.LayoutParams(fragmentWidth / 2, fragmentHeight / rowCount));
                imageList.add(imageView);
                row.addView(imageView);
            }
            imageTable.addView(row);
        }
    }

    public void updateImageTable(int obstacleID) {
        ImageView imageView = imageTable.findViewById(obstacleID);
        imageView.setVisibility(View.VISIBLE);
//        imageView.setBackground();
    }
}