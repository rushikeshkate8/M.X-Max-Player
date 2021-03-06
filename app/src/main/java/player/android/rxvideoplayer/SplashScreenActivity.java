package player.android.rxvideoplayer;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
            .withFullScreen()

            .withTargetActivity(MainActivity.class)

            .withSplashTimeOut(1000)

            .withBackgroundColor(Color.parseColor("#FFFFFF"))

            .withLogo( R.drawable.ic_play_circle_filled_black_24dp);
    View easySplashScreen = config.create();
    setContentView(easySplashScreen);
  }
}