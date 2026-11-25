package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.finalproject.listening.ListeningSession;
import java.util.ArrayList;

public class ListeningResult extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_part5_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom); return insets;
        });
        ListeningSession session = (ListeningSession) getIntent().getSerializableExtra("session");
        ArrayList<String> answers = getIntent().getStringArrayListExtra("answers");
        if (session == null || !session.isValid() || answers == null) { finish(); return; }
        int[] score = session.score(answers);
        int total = session.questions.size(), percentage = score[0] * 100 / total;
        ((TextView) findViewById(R.id.tvScore)).setText(score[0] + "/" + total);
        ((TextView) findViewById(R.id.tvPercentage)).setText(percentage + "%");
        ((ProgressBar) findViewById(R.id.progressCircle)).setProgress(percentage);
        ((TextView) findViewById(R.id.tvCorrect)).setText("Số câu đúng: " + score[0]);
        ((TextView) findViewById(R.id.tvWrong)).setText("Số câu sai: " + score[1]);
        ((TextView) findViewById(R.id.tvUnanswered)).setText("Chưa trả lời: " + score[2]);
        ((TextView) findViewById(R.id.tvEncouragement)).setText(percentage >= 80
                ? "Làm tốt lắm! Tiếp tục luyện nghe nhé." : "Hãy nghe lại và xem các câu cần cải thiện.");
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnReview).setOnClickListener(v -> startActivity(
                new Intent(this, ListeningLearn.class).putExtra("session", session)
                        .putExtra("review", true).putStringArrayListExtra("answers", answers)));
        findViewById(R.id.btnHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)); finish();
        });
    }
}
