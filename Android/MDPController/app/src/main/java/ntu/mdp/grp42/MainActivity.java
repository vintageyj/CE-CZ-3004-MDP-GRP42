package ntu.mdp.grp42;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ntu.mdp.grp42.databinding.ActivityMainBinding;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
//    private GifImageView gifImageView;
//    private GifImageView gifAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
//
//        gifImageView = findViewById(R.id.imageGIF);
//        gifAppName = findViewById(R.id.appNameGIF);
//        gifImageView.
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, TaskActivity.class));
            finish();
        }, 3000);
    }
}