package com.example.cricket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MenuActivity extends AppCompatActivity {

    private RelativeLayout record_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        record_button = findViewById(R.id.rl_menu_record);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecordingActivity();
            }
        });
    }

    public void openRecordingActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
