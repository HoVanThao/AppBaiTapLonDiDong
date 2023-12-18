package com.example.navigationbottom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.navigationbottom.R;

public class IntroActivity extends AppCompatActivity {
    private AppCompatButton getInBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        addControls();
        addEvents();


    }
    private void addEvents() {
        getInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
        });
    }

    private void addControls() {
        getInBtn = findViewById(R.id.btn_getIn);
    }
}