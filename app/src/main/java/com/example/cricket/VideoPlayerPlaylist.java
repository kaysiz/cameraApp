package com.example.cricket;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cricket.Adapter.VideoAdapter;
import com.example.cricket.Model.VideoModel;

import java.util.ArrayList;

public class VideoPlayerPlaylist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerviewLayoutManager;
    private ArrayList<VideoModel> arrayListVideos;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_playlist);
        swipeRefreshLayout = findViewById(R.id.reload_playlist);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayListVideos = null;
                fetchVideosFromGallery();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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

        cursor = getApplicationContext().getContentResolver().query(uri,projection,MediaStore.Video.Media.DATA +" like ?", new String[]{"%Cricket%"},orderby);

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

        VideoAdapter videoAdapter = new VideoAdapter(getApplicationContext(), arrayListVideos, VideoPlayerPlaylist.this);
        recyclerView.setAdapter(videoAdapter);

    }
}
