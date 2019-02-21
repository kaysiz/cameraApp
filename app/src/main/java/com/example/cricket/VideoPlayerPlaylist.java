package com.example.cricket;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.cricket.Adapter.VideoAdapter;
import com.example.cricket.Model.VideoModel;

import java.util.ArrayList;

public class VideoPlayerPlaylist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerviewLayoutManager;
    private ArrayList<VideoModel> arrayListVideos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_playlist);

        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.videoplayerplaylist);
        recyclerviewLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(recyclerviewLayoutManager);
        arrayListVideos = new ArrayList<>();
        fetchVideosFromGallery();

    }

    private void fetchVideosFromGallery() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathImage = null;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA,
                                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                                MediaStore.Video.Media._ID,
                                MediaStore.Video.Thumbnails.DATA };

        String orderby = MediaStore.Images.Media.DATE_TAKEN;

        cursor = getApplicationContext().getContentResolver().query(uri,projection,MediaStore.Video.Media.DATA +" like ?", new String[]{" %codehesion%"},orderby + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);

        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathImage = cursor.getString(column_index_data);

            VideoModel videoModel = new VideoModel();
            videoModel.setBoolean_selected(false);
            videoModel.setStr_path(absolutePathImage);
            videoModel.setStr_thumb(cursor.getString(thum));

            arrayListVideos.add(videoModel);
        }

        VideoAdapter videoAdapter = new VideoAdapter(getApplicationContext(), arrayListVideos);
        recyclerView.setAdapter(videoAdapter);

    }
}
