package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class Part6_Result extends AppCompatActivity {

    ProgressBar progressCircle;
    TextView tvScore, tvPercentage, tvEncouragement;
    TextView tvCorrect, tvWrong, tvUnanswered;
    Button btnReview, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_part6_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        displayResultData();
        setupEvents();
    }

    private void initViews() {
        progressCircle = findViewById(R.id.progressCircle);
        tvScore = findViewById(R.id.tvScore);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvEncouragement = findViewById(R.id.tvEncouragement);

        tvCorrect = findViewById(R.id.tvCorrect);
        tvWrong = findViewById(R.id.tvWrong);
        tvUnanswered = findViewById(R.id.tvUnanswered);

        btnReview = findViewById(R.id.btnReview);
        btnHome = findViewById(R.id.btnHome);

        if (findViewById(R.id.btnBack) != null) {
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        }
    }

    private void displayResultData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        int correct = bundle.getInt("correctCount");
        int wrong = bundle.getInt("wrongCount");
        int unanswered = bundle.getInt("unansweredCount");
        int total = bundle.getInt("totalCount");

        int percentage = total > 0 ? (correct * 100 / total) : 0;

        tvScore.setText(correct + "/" + total);
        tvPercentage.setText(percentage + "%");
        progressCircle.setProgress(percentage);

        tvCorrect.setText("Số câu đúng: " + correct);
        tvWrong.setText("Số câu sai: " + wrong);
        tvUnanswered.setText("Chưa trả lời: " + unanswered);

        if (percentage >= 80) {
            tvEncouragement.setText("Xuất sắc! Tiếp tục phát huy nhé! 🎉");
        } else if (percentage >= 50) {
            tvEncouragement.setText("Khá tốt! Cố gắng thêm chút nữa! 💪");
        } else {
            tvEncouragement.setText("Cần luyện tập thêm nhiều hơn! 📚");
        }
    }

    private void setupEvents() {
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(Part6_Result.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (btnReview != null) {
            btnReview.setOnClickListener(v -> finish());
        }
    }
}
