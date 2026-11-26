package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VocabList extends AppCompatActivity {

    TextView btnBack, tvTopicName, tvProgressDetail, tvWordCount, tvProgressPercent;
    ProgressBar progressVocabulary;
    TextView btnStudyFlashcard, btnQuiz;

    // RecyclerView và Adapter hiển thị danh sách từ vựng linh hoạt
    RecyclerView rvVocabulary;
    VocabAdapter vocabAdapter;
    List<VocabularyItem> listWords = new ArrayList<>();

    FirebaseFirestore db;
    int topicId = 1;
    String topicName = "";

    // Bộ phát âm TextToSpeech
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vocabulary_activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nhận dữ liệu truyền từ VocabTopic
        topicId = getIntent().getIntExtra("topicId", 1);
        topicName = getIntent().getStringExtra("topicName");

        initViews();
        setupRecyclerView();
        initTextToSpeech();

        db = FirebaseFirestore.getInstance();

        setupEvents();

        // Lấy danh sách từ vựng từ Firebase
        vocabularyFromDatabase();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTopicName = findViewById(R.id.tvTopicName);
        tvProgressDetail = findViewById(R.id.tvProgressDetail);
        tvWordCount = findViewById(R.id.tvWordCount);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressVocabulary = findViewById(R.id.progressVocabulary);
        btnStudyFlashcard = findViewById(R.id.btnStudyFLashCard);
        btnQuiz = findViewById(R.id.btnQuiz);
        rvVocabulary = findViewById(R.id.rvVocabulary);

        if (topicName != null && !topicName.isEmpty()) {
            tvTopicName.setText(topicName);
        }
    }

    // Thiết lập RecyclerView và Adapter
    private void setupRecyclerView() {
        // Cài đặt bố cục danh sách dọc (LinearLayoutManager)
        rvVocabulary.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter và lắng nghe sự kiện bấm nút phát âm
        vocabAdapter = new VocabAdapter(this, listWords, word -> speakWord(word));
        rvVocabulary.setAdapter(vocabAdapter);
    }

    // Khởi tạo TextToSpeech để phát âm tiếng Anh
    private void initTextToSpeech() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnStudyFlashcard.setOnClickListener(v -> {
            Intent intent = new Intent(VocabList.this, Flashcard.class);
            intent.putExtra("topicId", topicId);
            intent.putExtra("topicName", topicName);
            startActivity(intent);
        });

        btnQuiz.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng Quiz đang phát triển", Toast.LENGTH_SHORT).show()
        );
    }

    // Phát âm từ vựng bằng TextToSpeech
    private void speakWord(String word) {
        if (tts != null && word != null && !word.isEmpty()) {
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // LẤY TỪ VỰNG TỪ FIREBASE (Giống phong cách Part5_Question)
    private void vocabularyFromDatabase() {
        db.collection("vocabulary")
                .whereEqualTo("topic", topicId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listWords.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        VocabularyItem item = document.toObject(VocabularyItem.class);
                        if (item != null) {
                            listWords.add(item);
                        }
                    }

                    // Thông báo cho Adapter cập nhật lại danh sách trên giao diện
                    vocabAdapter.notifyDataSetChanged();

                    // Cập nhật lại thanh tiến độ và số lượng từ thực tế
                    updateProgressUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải từ vựng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Cập nhật Progress Card và Word Count khớp với số từ thực tế
    private void updateProgressUI() {
        int totalWords = listWords.size();
        // Hiện tại giả định số từ đã học (hoặc bạn có thể đếm theo thuộc tính learned nếu có)
        int learnedWords = 0;

        // Tính phần trăm tiến độ
        int percent = totalWords > 0 ? (learnedWords * 100 / totalWords) : 0;

        // Cập nhật lên Card tiến độ
        if (tvProgressPercent != null) {
            tvProgressPercent.setText(percent + "%");
        }

        if (progressVocabulary != null) {
            progressVocabulary.setMax(totalWords > 0 ? totalWords : 1);
            progressVocabulary.setProgress(learnedWords);
        }

        if (tvProgressDetail != null) {
            tvProgressDetail.setText(learnedWords + "/" + totalWords + " words learned · " + (totalWords - learnedWords) + " to review");
        }

        // Cập nhật số lượng từ trên Header (dưới tên chủ đề)
        if (tvWordCount != null) {
            tvWordCount.setText(totalWords + " words · " + learnedWords + " learned");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
