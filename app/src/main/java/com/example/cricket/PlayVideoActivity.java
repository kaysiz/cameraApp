package com.example.cricket;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

public class PlayVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private ImageView imageView;
    private SeekBar seekBar;
    private String str_video_url;
    private Boolean isPlay = false;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        init();
    }

    private void init() {
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.toggleButton);
        seekBar = findViewById(R.id.seekbar);
        str_video_url = getIntent().getStringExtra("video");
        videoView.setVideoPath(str_video_url);
        handler = new Handler();
        videoView.start();
        isPlay = true;
        imageView.setImageResource(R.drawable.pausebutton);
        updateSeekeBar();
    }

    private void updateSeekeBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    public Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(videoView.getCurrentPosition());
            seekBar.setMax(videoView.getDuration());
            handler.postDelayed(this, 100);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    handler.removeCallbacks(updateTimeTask);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    handler.removeCallbacks(updateTimeTask);
                    videoView.seekTo(seekBar.getProgress());
                    updateSeekeBar();
                }
            });
        }
    };

    public void toggle_method(View v) {
        if (isPlay) {
            videoView.pause();
            isPlay = false;
            imageView.setImageResource(R.drawable.playbutton);
        }
        else {
            videoView.start();
            updateSeekeBar();
            isPlay = true;
            imageView.setImageResource(R.drawable.pausebutton);
        }
    }
}
