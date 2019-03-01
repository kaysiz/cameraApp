package com.example.cricket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class HomeActivity extends AppCompatActivity {

    private RelativeLayout game_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

       game_button = findViewById(R.id.rl_game_mode);
       game_button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               openMenuActivity();
           }
       });
    }

    public void openMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
