package com.example.cricket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class HomeActivity extends AppCompatActivity {

    private RelativeLayout game_button;
    private RelativeLayout practice_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        game_button = findViewById(R.id.rl_game_mode);
        practice_button = findViewById(R.id.rl_practice_mode);
        game_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenuActivity();
            }
        });
        practice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPracticeMode();
            }
        });
    }

    private void openPracticeMode() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
