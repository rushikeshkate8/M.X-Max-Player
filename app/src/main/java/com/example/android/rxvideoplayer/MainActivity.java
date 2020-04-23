package com.example.android.rxvideoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView videoList;
    File directory;
    VideoAdapter videoAdapter;
    boolean permission;
    public static int REQUEST_PERMISSION = 1;
    public static ArrayList<File> fileArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoList = findViewById(R.id.video_list);
        // to read media from internal and external memory
        directory = new File("/mnt/");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        videoList.setLayoutManager(gridLayoutManager);
        videoPermissions();
    }

    private void videoPermissions() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        } else {
            permission = true;
            getFileList(directory);
            videoAdapter = new VideoAdapter(getApplicationContext(), fileArrayList);
            videoList.setAdapter(videoAdapter);
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
                getFileList(directory);
                videoAdapter = new VideoAdapter(getApplicationContext(), fileArrayList);
                videoList.setAdapter(videoAdapter);
            }
            else
            {
                Toast.makeText(this, "Please Allow Permissions", Toast.LENGTH_SHORT);
            }
        }
    }
    public ArrayList<File> getFileList(File directory)
    {
        File fileList[] = directory.listFiles();
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
                    if(fileList[i].getName().endsWith(".mp4"))
                    {
                        for(int j = 0; j < fileArrayList.size(); j++)
                        {
                            if(fileArrayList.get(j).getName().equals((fileList[i].getName())))   //can be removed
                            {
                                permission = true;
                            }
                            else
                            {

                            }
                        }
                        if(permission)
                        {
                            permission = false;
                        }
                        else
                        {
                            fileArrayList.add(fileList[i]);
                        }

                    }
                }
            }
        }
        return fileArrayList;
    }


}

