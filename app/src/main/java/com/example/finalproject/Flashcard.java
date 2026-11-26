package com.example.finalproject;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Flashcard extends AppCompatActivity {

    // 1. Khai báo các view trên giao diện
    TextView btnBack, tvTopicTitle, tvWordProgress;
    LinearLayout cardFlashcard;
    TextView btnSpeaker, tvWord, tvPronunciation, tvWordType;
    View dividerLine;
    TextView tvMeaning, tvExample, tvFlip;
    Button btnEasy, btnHard, btnForgot;

    // 2. Dữ liệu và Firestore
    FirebaseFirestore db;
    List<VocabularyItem> listWords = new ArrayList<>();
    int currentIndex = 0;
    int topicId = 1;
    String topicName = "";

    // Trạng thái thẻ đang lật hay chưa (false: mặt trước, true: mặt sau)
    boolean isFlipped = false;

    // Công cụ đọc phát âm tiếng Anh
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vocabulary_activity_flashcard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nhận dữ liệu truyền từ VocabList sang
        topicId = getIntent().getIntExtra("topicId", 1);
        topicName = getIntent().getStringExtra("topicName");

        // Khởi tạo Firestore và TextToSpeech
        db = FirebaseFirestore.getInstance();
        initTextToSpeech();

        initViews();
        setupEvents();

        // Tải danh sách từ vựng từ Firebase
        loadWordsFromDatabase();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTopicTitle = findViewById(R.id.tvTopicTitle);
        tvWordProgress = findViewById(R.id.tvWordProgress);

        cardFlashcard = findViewById(R.id.cardFlashcard);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        tvWord = findViewById(R.id.tvWord);
        tvPronunciation = findViewById(R.id.tvPronunciation);
        tvWordType = findViewById(R.id.tvWordType);

        dividerLine = findViewById(R.id.dividerLine);
        tvMeaning = findViewById(R.id.tvMeaning);
        tvExample = findViewById(R.id.tvExample);
        tvFlip = findViewById(R.id.tvFlip);

        btnEasy = findViewById(R.id.btnEasy);
        btnHard = findViewById(R.id.btnHard);
        btnForgot = findViewById(R.id.btnForgot);

        if (topicName != null && !topicName.isEmpty()) {
            tvTopicTitle.setText(topicName);
        }
    }

    // Khởi tạo bộ phát âm TextToSpeech chuẩn giọng tiếng Anh (US)
    private void initTextToSpeech() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    private void setupEvents() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Bấm vào thẻ hoặc dòng chữ "Tap to flip" để lật thẻ
        cardFlashcard.setOnClickListener(v -> flipCard());
        tvFlip.setOnClickListener(v -> flipCard());

        // Nút loa: phát âm từ hiện tại
        btnSpeaker.setOnClickListener(v -> speakCurrentWord());

        // 3 nút đánh giá mức độ nhớ: chuyển sang từ tiếp theo
        btnEasy.setOnClickListener(v -> nextWord());
        btnHard.setOnClickListener(v -> nextWord());
        btnForgot.setOnClickListener(v -> nextWord());
    }

    // Tải từ vựng từ Firestore theo topicId (Giống cách viết của VocabList)
    private void loadWordsFromDatabase() {
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

                    if (!listWords.isEmpty()) {
                        // Trộn ngẫu nhiên thứ tự từ vựng
                        Collections.shuffle(listWords);

                        currentIndex = 0;
                        displayCurrentCard();
                    } else {
                        Toast.makeText(this, "Chưa có từ vựng nào trong chủ đề này", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải từ vựng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Hiển thị dữ liệu của từ vựng hiện tại lên thẻ
    private void displayCurrentCard() {
        if (listWords.isEmpty() || currentIndex >= listWords.size()) return;

        VocabularyItem currentItem = listWords.get(currentIndex);

        // Cập nhật số thứ tự: Ví dụ "1/5"
        tvWordProgress.setText((currentIndex + 1) + "/" + listWords.size());

        // Điền thông tin từ vựng
        tvWord.setText(currentItem.getWord());
        tvPronunciation.setText(currentItem.getPronunciation());
        tvWordType.setText("(" + currentItem.getPOS() + ")");
        tvMeaning.setText(currentItem.getMeaning());
        tvExample.setText(currentItem.getExample());

        // Reset về mặt trước: Ẩn nghĩa và ví dụ
        isFlipped = false;
        hideMeaning();
    }

    // Lật thẻ qua lại giữa mặt trước (Từ vựng) và mặt sau (Nghĩa + Ví dụ)
    private void flipCard() {
        if (isFlipped) {
            hideMeaning();
            isFlipped = false;
        } else {
            showMeaning();
            isFlipped = true;
        }
    }

    // Ẩn nghĩa tiếng Việt (Mặt trước)
    private void hideMeaning() {
        if (dividerLine != null) dividerLine.setVisibility(View.GONE);
        tvMeaning.setVisibility(View.GONE);
        tvExample.setVisibility(View.GONE);
        tvFlip.setText("↻ Chạm để xem nghĩa");
    }

    // Hiện nghĩa tiếng Việt (Mặt sau)
    private void showMeaning() {
        if (dividerLine != null) dividerLine.setVisibility(View.VISIBLE);
        tvMeaning.setVisibility(View.VISIBLE);
        tvExample.setVisibility(View.VISIBLE);
        tvFlip.setText("↻ Chạm để ẩn nghĩa");
    }

    // Phát âm từ vựng bằng TextToSpeech
    private void speakCurrentWord() {
        if (listWords.isEmpty() || currentIndex >= listWords.size()) return;
        String word = listWords.get(currentIndex).getWord();
        if (tts != null && word != null && !word.isEmpty()) {
            tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // Chuyển sang thẻ từ tiếp theo
    private void nextWord() {
        currentIndex++;
        if (currentIndex < listWords.size()) {
            displayCurrentCard();
        } else {
            Toast.makeText(this, "🎉 Bạn đã hoàn thành các thẻ từ vựng này!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Giải phóng tài nguyên khi thoát màn hình
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
