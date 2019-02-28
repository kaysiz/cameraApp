package com.example.cricket;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.util.Date;

public class PlayVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private String str_video_url;
    private MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        Date modifiedDate = new Date();
        String path = Environment.getExternalStorageDirectory().toString()+"/Movies/" + getString(R.string.app_name);
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            long file_time = files[i].lastModified();
            long current_time = System.currentTimeMillis();
            Log.d("Files", "FileName:" + files[i].lastModified() + "current time is " + System.currentTimeMillis());
        }
        init();
    }


    private void init() {
        videoView = findViewById(R.id.videoView);
        str_video_url = getIntent().getStringExtra("video");
        videoView.setVideoPath(str_video_url);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        mediaController.requestFocus();
        videoView.start();
    }
}
