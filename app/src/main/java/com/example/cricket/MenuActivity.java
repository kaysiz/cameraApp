package com.example.cricket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Default camera is USB, use front camera?");
                builder.setIcon(R.drawable.exo_icon_next);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        openFrontCameraActivity();    // stop chronometer here

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        openUSBCameraActivity();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        last_play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryActivity();
            }
        });
    }

    public void openUSBCameraActivity() {
        Intent intent = new Intent(this, FairView.class);
        startActivity(intent);
    }

    public void openFrontCameraActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openGalleryActivity() {
        Intent intent = new Intent(this, VideoPlayerPlaylist.class);
        startActivity(intent);
    }
}
