package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class VocabTopic extends AppCompatActivity {

    LinearLayout cardTopic1, cardTopic2, cardTopic3, cardTopic4, cardTopic5;
    TextView tvCountTopic1, tvCountTopic2, tvCountTopic3, tvCountTopic4, tvCountTopic5;
    ProgressBar progressTopic1, progressTopic2, progressTopic3, progressTopic4, progressTopic5;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vocabulary_activity_topic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        initViews();
        setupTopicClicks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại số lượng từ mỗi khi quay lại màn hình chủ đề
        loadWordCountsFromFirestore();
    }

    private void initViews() {
        cardTopic1 = findViewById(R.id.cardTopic1);
        cardTopic2 = findViewById(R.id.cardTopic2);
        cardTopic3 = findViewById(R.id.cardTopic3);
        cardTopic4 = findViewById(R.id.cardTopic4);
        cardTopic5 = findViewById(R.id.cardTopic5);

        tvCountTopic1 = findViewById(R.id.tvCountTopic1);
        tvCountTopic2 = findViewById(R.id.tvCountTopic2);
        tvCountTopic3 = findViewById(R.id.tvCountTopic3);
        tvCountTopic4 = findViewById(R.id.tvCountTopic4);
        tvCountTopic5 = findViewById(R.id.tvCountTopic5);

        progressTopic1 = findViewById(R.id.progressTopic1);
        progressTopic2 = findViewById(R.id.progressTopic2);
        progressTopic3 = findViewById(R.id.progressTopic3);
        progressTopic4 = findViewById(R.id.progressTopic4);
        progressTopic5 = findViewById(R.id.progressTopic5);
    }

    private void setupTopicClicks() {
        cardTopic1.setOnClickListener(v -> openVocabList(1, "Office & Business"));
        cardTopic2.setOnClickListener(v -> openVocabList(2, "Travel & Airport"));
        cardTopic3.setOnClickListener(v -> openVocabList(3, "Finance & Banking"));
        cardTopic4.setOnClickListener(v -> openVocabList(4, "Marketing"));
        cardTopic5.setOnClickListener(v -> openVocabList(5, "Human Resources"));
    }

    // Đếm số lượng từ thực tế của từng topic từ Firebase
    private void loadWordCountsFromFirestore() {
        countWordsForTopic(1, tvCountTopic1, progressTopic1);
        countWordsForTopic(2, tvCountTopic2, progressTopic2);
        countWordsForTopic(3, tvCountTopic3, progressTopic3);
        countWordsForTopic(4, tvCountTopic4, progressTopic4);
        countWordsForTopic(5, tvCountTopic5, progressTopic5);
    }

    private void countWordsForTopic(int topicId, TextView tvCount, ProgressBar progressBar) {
        db.collection("vocabulary")
                .whereEqualTo("topic", topicId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalWords = queryDocumentSnapshots.size();

                    // Hiển thị số từ thực tế của chủ đề
                    if (tvCount != null) {
                        tvCount.setText(totalWords + " words");
                    }

                    // Tạm thời nếu chưa có cơ chế đánh dấu learned thì hiển thị số từ
                    if (progressBar != null) {
                        progressBar.setMax(totalWords > 0 ? totalWords : 1);
                        progressBar.setProgress(0); // Chưa học từ nào
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvCount != null) {
                        tvCount.setText("0 words");
                    }
                });
    }

    private void openVocabList(int topicId, String topicName) {
        Intent intent = new Intent(VocabTopic.this, VocabList.class);
        intent.putExtra("topicId", topicId);
        intent.putExtra("topicName", topicName);
        startActivity(intent);
    }
}
