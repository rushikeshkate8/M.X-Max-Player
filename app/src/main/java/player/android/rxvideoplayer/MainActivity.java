package player.android.rxvideoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;
    InterstitialAd interstitialAd;
    RecyclerView videoList;
    File directory;
    VideoAdapter videoAdapter;
    boolean permission;
    public static int REQUEST_PERMISSION = 1;
    public static ArrayList<File> fileArrayList = new ArrayList<>();
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        toolbar = findViewById( R.id.toolBar );
        setSupportActionBar( toolbar );
        videoList = findViewById(R.id.video_list);
        videoList.setItemViewCacheSize(20);
        videoList.setDrawingCacheEnabled(true);
        videoList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        // to read media from internal and external memory
        directory = new File("/mnt/");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        videoList.setLayoutManager(gridLayoutManager);
        videoPermissions();
        RecyclerFastScroller recyclerFastScroller = findViewById(R.id.fastScroller);
        recyclerFastScroller.attachRecyclerView(videoList);
        MobileAds.initialize(this, "ca-app-pub-6301359771562604/5236326991");
        mAdView = findViewById(R.id.adView);
       prepareAd();
       prepareInterstitialAd();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate( new Runnable() {
            @Override
            public void run() {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        if(interstitialAd.isLoaded())
                        {   //player.setPlayWhenReady( false );
                            interstitialAd.show();
                            prepareInterstitialAd();
                        }
                    }
                });
            }
        }, 1, 60, TimeUnit.SECONDS);
    }

    private void videoPermissions() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        } else {
            permission = true;
            setFileList(directory);
            configureAdapter();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                permission = true;
                setFileList(directory);
                configureAdapter();
            }
            else
            {
                Toast.makeText(this, "Please Allow Permissions", Toast.LENGTH_SHORT);
            }
        }
    }
    public void setFileList(File directory)
    {
        File fileList[] = directory.listFiles();
        if(fileList != null && fileList.length > 0)
        {
            for(int i = 0; i < fileList.length; i++)
            {
                if(fileList[i].isDirectory())
                {
                    setFileList(fileList[i]);
                }
                else
                {
                    permission = false;
                    if(fileList[i].getName().endsWith(".mp4") || fileList[i].getName().endsWith(".3gp")||fileList[i].getName().endsWith(".avi")||fileList[i].getName().endsWith(".webm")||fileList[i].getName().endsWith(".3gpp")||fileList[i].getName().endsWith(".3gpp2")||fileList[i].getName().endsWith(".webm")||fileList[i].getName().endsWith(".mkv"))
                    {
                        fileArrayList.add(fileList[i]);
                    }
                }
            }

        }
    }

    public void configureAdapter()
    {
        videoAdapter = new VideoAdapter(getApplicationContext(), fileArrayList);
        videoList.setAdapter(videoAdapter);
        ProgressBar progressBar = findViewById( R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search videos");
        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor( Color.WHITE);
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                videoAdapter.getFilter().filter(newText);
                return false;
            }
        } );
        return true;
    }
    public void prepareAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    public void prepareInterstitialAd()
    {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-6301359771562604/1933244919");
        interstitialAd.loadAd( new AdRequest.Builder().build());
    }
}
