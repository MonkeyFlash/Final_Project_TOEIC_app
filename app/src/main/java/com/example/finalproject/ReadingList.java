package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReadingList extends AppCompatActivity {
    TextView btnBack;
    LinearLayout itemPart5, itemPart6, itemPart7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.list_activity_reading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        initViews();
        goLearn();
    }

    private void goLearn() {
        itemPart5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingList.this, Part5.class );
                startActivity(intent);
            }
        });
        itemPart6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadingList.this, Part6.class );
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        itemPart5 = findViewById(R.id.itemPart5);
        itemPart6 = findViewById(R.id.itemPart6);
        itemPart7 = findViewById(R.id.itemPart7);

    }
}