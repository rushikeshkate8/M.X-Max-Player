package com.example.android.rxvideoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView videoList;
    SearchView searchView;
    File directory;
    VideoAdapter videoAdapter;
    boolean permission;
    boolean containsFile = false;
    public static int REQUEST_PERMISSION = 1;
    public static ArrayList<File> fileArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoList = findViewById(R.id.video_list);
        videoList.setItemViewCacheSize(20);
        videoList.setDrawingCacheEnabled(true);
        videoList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        // to read media from internal and external memory
        directory = new File("/mnt/");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        videoList.setLayoutManager(gridLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(MainActivity.this, R.dimen.item_offset);
        videoList.addItemDecoration(itemDecoration);
        videoPermissions();
        RecyclerFastScroller recyclerFastScroller = findViewById(R.id.fastScroller);
        recyclerFastScroller.attachRecyclerView(videoList);
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
             /*   videoAdapter = new VideoAdapter(getApplicationContext(), fileArrayList);
                videoList.setAdapter(videoAdapter); */
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
    /*
    public ArrayList<File> getFileList(File directory)
    {
        File fileList[] = directory.listFiles();
        ArrayList<File> filteredOutput = new ArrayList<>();
        if(fileList != null && fileList.length > 0)
        {
            for(int i = 0; i < fileList.length; i++)
            {
                if(fileList[i].isDirectory())
                {
                    getFileList(fileList[i]);
                }
                else
                {
                    permission = false;
                    if(fileList[i].getName().endsWith(".mp4") || fileList[i].getName().endsWith(".3gp"))
                    {
                            fileArrayList.add(fileList[i]);
                    }
                }
            }
        }

        videoAdapter = new VideoAdapter(getApplicationContext(), fileArrayList);
        videoList.setAdapter(videoAdapter);
        return fileArrayList;
    }
    */

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
}
