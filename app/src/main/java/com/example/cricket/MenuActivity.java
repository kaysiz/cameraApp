package com.example.cricket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MenuActivity extends AppCompatActivity {

    private RelativeLayout record_button;
    private RelativeLayout last_play_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        record_button = findViewById(R.id.rl_menu_record);
        last_play_button = findViewById(R.id.rl_menu_review);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecordingActivity();
            }
        });

        last_play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryActivity();
            }
        });
    }

    public void openRecordingActivity() {
        Intent intent = new Intent(this, FairView.class);
        startActivity(intent);
    }

    public void openGalleryActivity() {
        Intent intent = new Intent(this, VideoPlayerPlaylist.class);
        startActivity(intent);
    }
}
