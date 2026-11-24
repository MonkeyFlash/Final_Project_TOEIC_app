
package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class Part5_Question extends AppCompatActivity {

    TextView tvQuestion, tvAnswerA, tvAnswerB, tvAnswerC, tvAnswerD;
    TextView tvQuestionNumber, btnNext, btnPrevious;

    LinearLayout optionA, optionB, optionC, optionD;

    RadioButton rbA, rbB, rbC, rbD;

    FirebaseFirestore db;

    ArrayList<Question> listQuestions = new ArrayList<>();
    ArrayList<String> userAnswers = new ArrayList<>();
    int score = 0;

    int currentIndex = 0;
    boolean answerSelected = false;
    LinearLayout explanationBox;
    TextView tvResult, tvExplanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());

        setContentView(R.layout.activity_part5_question);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        initViews();

        db = FirebaseFirestore.getInstance();

        setupNavigation();

        questionFromDatabase();
        answer();
    }
    private int calculateScore() {

        int totalScore = 0;

        for (int i = 0; i < listQuestions.size(); i++) {

            String userAnswer = userAnswers.get(i);
            String correctAnswer = listQuestions.get(i).getCorrectAnswer();

            if (userAnswer.equals(correctAnswer)) {
                totalScore++;
            }
        }

        return totalScore;
    }



    private void checkAnswer(String selectedAnswer) {
        Question q = listQuestions.get(currentIndex);

        String correctAnswer = q.getCorrectAnswer();
        String explanation = q.getExplanation();

        explanationBox.setVisibility(View.VISIBLE);

        if (selectedAnswer.equals(correctAnswer)) {
            tvResult.setText("✓ Chính xác!");
        } else {
            tvResult.setText("✗ Chưa chính xác!");
        }

        tvExplanation.setText(
                "Đáp án đúng: " + correctAnswer + "\n\n" + explanation
        );
    }

    private void answer() {
        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerSelected) return;
                answerSelected = true;

                rbA.setChecked(true);
                rbB.setChecked(false);
                rbC.setChecked(false);
                rbD.setChecked(false);
                userAnswers.set(currentIndex, "A");
                checkAnswer("A");
            }
        });
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerSelected) return;
                answerSelected = true;

                rbA.setChecked(false);
                rbB.setChecked(true);
                rbC.setChecked(false);
                rbD.setChecked(false);
                userAnswers.set(currentIndex, "B");
                checkAnswer("B");
            }
        });
        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerSelected) return;
                answerSelected = true;

                rbA.setChecked(false);
                rbB.setChecked(false);
                rbC.setChecked(true);
                rbD.setChecked(false);
                userAnswers.set(currentIndex, "C");
                checkAnswer("C");
            }
        });
        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerSelected) return;
                answerSelected = true;

                rbA.setChecked(false);
                rbB.setChecked(false);
                rbC.setChecked(false);
                rbD.setChecked(true);
                userAnswers.set(currentIndex, "D");
                checkAnswer("D");
            }
        });
    }



    // HIỂN THỊ CÂU HỎI
    private void displayQuestion() {

        if (listQuestions.isEmpty()) {
            return;
        }

        answerSelected = false;

        Question q = listQuestions.get(currentIndex);

        // Hiển thị câu hỏi
        tvQuestion.setText(q.getQuestion());

        tvAnswerA.setText(q.getOptionA());
        tvAnswerB.setText(q.getOptionB());
        tvAnswerC.setText(q.getOptionC());
        tvAnswerD.setText(q.getOptionD());

        // RESET RADIO BUTTON
        String savedAnswer = userAnswers.get(currentIndex);

        rbA.setChecked(savedAnswer.equals("A"));
        rbB.setChecked(savedAnswer.equals("B"));
        rbC.setChecked(savedAnswer.equals("C"));
        rbD.setChecked(savedAnswer.equals("D"));

        answerSelected = !savedAnswer.isEmpty();

        // Ẩn phần giải thích của câu trước
        if (savedAnswer.isEmpty()) {

            explanationBox.setVisibility(View.GONE);
            tvResult.setText("");
            tvExplanation.setText("");

        } else {

            checkAnswer(savedAnswer);
        }

        // Cập nhật số câu
        tvQuestionNumber.setText(
                "Question " + (currentIndex + 1)
                        + "/" + listQuestions.size()
        );
    }

    // LẤY CÂU HỎI TỪ FIREBASE
    private void questionFromDatabase() {

        db.collection("questions")
                .whereEqualTo("part", 5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    listQuestions.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {

                        Question question = document.toObject(Question.class);

                        if (question != null) {
                            listQuestions.add(question);
                        }
                    }

                    // Trộn ngẫu nhiên câu hỏi
                    Collections.shuffle(listQuestions);

                    // Giới hạn tối đa 30 câu
                    if (listQuestions.size() > 30) {
                        listQuestions = new ArrayList<>(
                                listQuestions.subList(0, 30)
                        );
                    }
                    //Co che tinh diem
                    userAnswers.clear();

                    for (int i = 0; i < listQuestions.size(); i++) {
                        userAnswers.add("");
                    }

                    if (!listQuestions.isEmpty()) {

                        currentIndex = 0;

                        // Chỉ hiển thị sau khi Firebase tải xong
                        displayQuestion();

                    } else {

                        Toast.makeText(this,
                                "Không có câu hỏi Part 5",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Lỗi tải câu hỏi: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // NEXT VÀ PREVIOUS
    private void setupNavigation() {

        btnNext.setOnClickListener(v -> {

            if (currentIndex < listQuestions.size() - 1) {

                currentIndex++;

                displayQuestion();

            } else if (!listQuestions.isEmpty()) {

                Toast.makeText(this,
                        "Bạn đã đến câu cuối cùng!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnPrevious.setOnClickListener(v -> {

            if (currentIndex > 0) {

                currentIndex--;

                displayQuestion();
            }
        });
    }

    private void initViews() {

        tvQuestion = findViewById(R.id.tvQuestion);

        tvAnswerA = findViewById(R.id.tvAnswerA);
        tvAnswerB = findViewById(R.id.tvAnswerB);
        tvAnswerC = findViewById(R.id.tvAnswerC);
        tvAnswerD = findViewById(R.id.tvAnswerD);

        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);

        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);

        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);

        explanationBox = findViewById(R.id.explanationBox);
        tvResult = findViewById(R.id.tvResult);
        tvExplanation = findViewById(R.id.tvExplanation);

        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);


    }
}
