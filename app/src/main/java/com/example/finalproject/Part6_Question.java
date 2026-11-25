package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
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
import java.util.List;

public class Part6_Question extends AppCompatActivity {

    TextView tvQuestion, tvAnswerA, tvAnswerB, tvAnswerC, tvAnswerD;
    TextView tvQuestionNumber, btnNext, btnPrevious, btnBack, tvPassage;

    LinearLayout optionA, optionB, optionC, optionD;

    RadioButton rbA, rbB, rbC, rbD;

    FirebaseFirestore db;

    ArrayList<Question> listQuestions = new ArrayList<>();
    ArrayList<String> listPassages = new ArrayList<>();
    ArrayList<String> passageForQuestion = new ArrayList<>();
    ArrayList<String> userAnswers = new ArrayList<>();

    int currentIndex = 0;
    boolean answerSelected = false;
    boolean isReviewMode = false;

    LinearLayout explanationBox;
    TextView tvResult, tvExplanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.enableEdgeToEdge(getWindow());

        setContentView(R.layout.activity_part6_question);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

            if (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer)) {
                totalScore++;
            }
        }

        return totalScore;
    }

    private void checkAnswer(String selectedAnswer) {
        if (listQuestions == null || listQuestions.isEmpty() || currentIndex < 0 || currentIndex >= listQuestions.size()) {
            return;
        }

        Question q = listQuestions.get(currentIndex);

        String correctAnswer = q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim() : "";
        String explanation = q.getExplanation() != null ? q.getExplanation().trim() : "Không có giải thích chi tiết.";

        explanationBox.setVisibility(View.VISIBLE);

        if (selectedAnswer.equalsIgnoreCase(correctAnswer)) {
            tvResult.setText("✓ Chính xác!");
            tvResult.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            tvResult.setText("✗ Chưa chính xác!");
            tvResult.setTextColor(Color.parseColor("#C62828"));
        }

        tvExplanation.setText("Đáp án đúng: " + correctAnswer + "\n\n" + explanation);
    }

    private void answer() {
        optionA.setOnClickListener(v -> {
            if (isReviewMode || answerSelected) return;
            selectAnswer("A");
        });

        optionB.setOnClickListener(v -> {
            if (isReviewMode || answerSelected) return;
            selectAnswer("B");
        });

        optionC.setOnClickListener(v -> {
            if (isReviewMode || answerSelected) return;
            selectAnswer("C");
        });

        optionD.setOnClickListener(v -> {
            if (isReviewMode || answerSelected) return;
            selectAnswer("D");
        });

        rbA.setClickable(false);
        rbB.setClickable(false);
        rbC.setClickable(false);
        rbD.setClickable(false);
    }

    private void selectAnswer(String option) {
        answerSelected = true;

        rbA.setChecked(option.equals("A"));
        rbB.setChecked(option.equals("B"));
        rbC.setChecked(option.equals("C"));
        rbD.setChecked(option.equals("D"));

        userAnswers.set(currentIndex, option);
        checkAnswer(option);
    }

    // HIỂN THỊ CÂU HỎI VÀ ĐOẠN VĂN
    private void displayQuestion() {
        if (listQuestions == null || listQuestions.isEmpty() || currentIndex < 0 || currentIndex >= listQuestions.size()) {
            return;
        }

        Question q = listQuestions.get(currentIndex);

        // Hiển thị đoạn văn Part 6
        if (passageForQuestion != null && currentIndex < passageForQuestion.size()) {
            String passage = passageForQuestion.get(currentIndex);
            tvPassage.setText(passage != null ? passage : "");
        }

        // Hiển thị câu hỏi
        tvQuestion.setText(q.getQuestion() != null ? q.getQuestion() : "");

        tvAnswerA.setText(q.getOptionA() != null ? q.getOptionA() : "");
        tvAnswerB.setText(q.getOptionB() != null ? q.getOptionB() : "");
        tvAnswerC.setText(q.getOptionC() != null ? q.getOptionC() : "");
        tvAnswerD.setText(q.getOptionD() != null ? q.getOptionD() : "");

        // RESET RADIO BUTTON
        String savedAnswer = userAnswers.get(currentIndex);

        rbA.setChecked(savedAnswer.equals("A"));
        rbB.setChecked(savedAnswer.equals("B"));
        rbC.setChecked(savedAnswer.equals("C"));
        rbD.setChecked(savedAnswer.equals("D"));

        if (isReviewMode) {
            answerSelected = true;
            if (savedAnswer.isEmpty()) {
                explanationBox.setVisibility(View.VISIBLE);
                tvResult.setText("− Bỏ qua câu này");
                tvResult.setTextColor(Color.parseColor("#596783"));
                tvExplanation.setText("Đáp án đúng: " + (q.getCorrectAnswer() != null ? q.getCorrectAnswer() : "") + "\n\n" + (q.getExplanation() != null ? q.getExplanation() : ""));
            } else {
                checkAnswer(savedAnswer);
            }
        } else {
            answerSelected = !savedAnswer.isEmpty();
            if (savedAnswer.isEmpty()) {
                explanationBox.setVisibility(View.GONE);
                tvResult.setText("");
                tvExplanation.setText("");
            } else {
                checkAnswer(savedAnswer);
            }
        }

        // Cập nhật số câu
        tvQuestionNumber.setText("Question " + (currentIndex + 1) + "/" + listQuestions.size());

        if (currentIndex == listQuestions.size() - 1) {
            btnNext.setText(isReviewMode ? "End" : "Finish");
        } else {
            btnNext.setText("Next");
        }
    }

    // LẤY TOÀN BỘ PASSAGE VÀ CÂU HỎI TỪ FIREBASE
    private void questionFromDatabase() {

        listQuestions.clear();
        listPassages.clear();
        passageForQuestion.clear();
        userAnswers.clear();

        db.collection("questions_part6")
                .get()
                .addOnSuccessListener(passageSnapshots -> {

                    List<DocumentSnapshot> passageDocuments =
                            new ArrayList<>(passageSnapshots.getDocuments());

                    // Sắp xếp Passage_1, Passage_2, Passage_3...
                    Collections.sort(passageDocuments, (d1, d2) ->
                            Integer.compare(
                                    getNumberFromId(d1.getId()),
                                    getNumberFromId(d2.getId())
                            ));
                    if (passageDocuments.size() > 30) {
                        passageDocuments = new ArrayList<>(passageDocuments.subList(0, 30));
                    }

                    if (passageDocuments.isEmpty()) {
                        Toast.makeText(this,
                                "Không tìm thấy Passage Part 6!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Bắt đầu lấy Passage đầu tiên
                    loadPassageSequentially(passageDocuments, 0);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Lỗi tải dữ liệu: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // TẢI TỪNG PASSAGE THEO THỨ TỰ
    private void loadPassageSequentially(
            List<DocumentSnapshot> passageDocuments, int index) {

        // Đã tải hết Passage
        if (index >= passageDocuments.size()) {

            // Khởi tạo câu trả lời cho tất cả câu hỏi
            userAnswers.clear();

            for (int i = 0; i < listQuestions.size(); i++) {
                userAnswers.add("");
            }

            // Hiển thị câu hỏi đầu tiên
            if (!listQuestions.isEmpty()) {
                currentIndex = 0;
                displayQuestion();
            } else {
                Toast.makeText(this,
                        "Không có câu hỏi Part 6",
                        Toast.LENGTH_SHORT).show();
            }

            return;
        }

        // Lấy Passage hiện tại
        DocumentSnapshot passageDocument = passageDocuments.get(index);

        // Lấy nội dung đoạn văn
        String passageText = passageDocument.getString("passageText");

        if (passageText == null) {
            passageText = "";
        }

        final String currentPassageText = passageText;

        // Lưu đoạn văn
        listPassages.add(currentPassageText);

        // Truy cập subcollection câu hỏi của Passage này
        passageDocument.getReference()
                .collection("questions_part6")
                .get()
                .addOnSuccessListener(questionSnapshots -> {

                    List<DocumentSnapshot> questionDocuments =
                            new ArrayList<>(questionSnapshots.getDocuments());

                    // Sắp xếp Question_1, Question_2, Question_3...
                    Collections.sort(questionDocuments, (d1, d2) ->
                            Integer.compare(
                                    getNumberFromId(d1.getId()),
                                    getNumberFromId(d2.getId())
                            ));

                    // Duyệt từng document câu hỏi
                    for (DocumentSnapshot questionDocument : questionDocuments) {

                        // Chuyển dữ liệu Firebase thành object Question
                        Question question =
                                questionDocument.toObject(Question.class);

                        if (question != null) {

                            // Thêm câu hỏi vào danh sách
                            listQuestions.add(question);

                            // Gắn đoạn văn của câu hỏi đó
                            passageForQuestion.add(currentPassageText);
                        }
                    }

                    // Sau khi tải xong Passage hiện tại
                    // tiếp tục tải Passage tiếp theo
                    loadPassageSequentially(passageDocuments, index + 1);

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "Lỗi tải câu hỏi: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
    private int getNumberFromId(String documentId) {

        try {
            String[] parts = documentId.split("_");

            return Integer.parseInt(parts[parts.length - 1]);

        } catch (Exception e) {
            return 0;
        }
    }

    // NEXT, PREVIOUS VÀ BACK
    private void setupNavigation() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnNext.setOnClickListener(v -> {
            if (currentIndex < listQuestions.size() - 1) {
                currentIndex++;
                displayQuestion();
            } else if (!listQuestions.isEmpty()) {
                if (!isReviewMode) {
                    finishQuiz();
                } else {
                    Toast.makeText(this, "Bạn đã ở câu cuối cùng!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayQuestion();
            }
        });
    }

    private void finishQuiz() {
        isReviewMode = true;

        int total = listQuestions.size();
        int correct = calculateScore();
        int answered = 0;
        for (int i = 0; i < userAnswers.size(); i++) {
            if (!userAnswers.get(i).isEmpty()) {
                answered++;
            }
        }
        int wrong = answered - correct;
        int unanswered = total - answered;

        Intent intent = new Intent(Part6_Question.this, Part6_Result.class);

        Bundle bundle = new Bundle();
        bundle.putInt("correctCount", correct);
        bundle.putInt("wrongCount", wrong);
        bundle.putInt("unansweredCount", unanswered);
        bundle.putInt("totalCount", total);

        intent.putExtras(bundle);

        startActivity(intent);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvPassage = findViewById(R.id.tvPassage);
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
