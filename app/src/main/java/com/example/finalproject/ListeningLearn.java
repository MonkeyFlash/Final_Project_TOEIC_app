package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.example.finalproject.listening.ListeningAudioPlayer;
import com.example.finalproject.listening.ListeningSession;
import com.example.finalproject.listening.ListeningSubQuestion;
import java.util.ArrayList;
import java.util.Locale;

public class ListeningLearn extends AppCompatActivity {
    private ListeningSession session;
    private ArrayList<String> answers;
    private int index, audioPosition;
    private boolean review;
    private float speed = 1f;
    private ListeningAudioPlayer audio;
    private TextView play, audioStatus, audioTime;
    private SeekBar seek;
    private final int[] optionIds = {R.id.optionA, R.id.optionB, R.id.optionC, R.id.optionD};
    private final int[] radioIds = {R.id.rbA, R.id.rbB, R.id.rbC, R.id.rbD};
    private final int[] textIds = {R.id.tvAnswerA, R.id.tvAnswerB, R.id.tvAnswerC, R.id.tvAnswerD};

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.listening_activity_learn);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom); return insets;
        });
        Bundle input = state != null ? state : getIntent().getExtras();
        session = input == null ? null : (ListeningSession) input.getSerializable("session");
        if (session == null || !session.isValid()) { finish(); return; }
        answers = input.getStringArrayList("answers");
        if (answers == null || answers.size() != session.questions.size()) {
            answers = new ArrayList<>();
            for (int i = 0; i < session.questions.size(); i++) answers.add("");
        }
        index = Math.max(0, Math.min(input.getInt("index", 0), session.questions.size() - 1));
        review = input.getBoolean("review", false);
        audioPosition = input.getInt("audioPosition", 0);
        speed = input.getFloat("speed", 1f);
        ((TextView) findViewById(R.id.tvPartTitle)).setText(ListeningIntro.partName(session.part));
        play = findViewById(R.id.btnPlayAudio);
        audioStatus = findViewById(R.id.tvAudioStatus);
        audioTime = findViewById(R.id.tvAudioTime);
        seek = findViewById(R.id.seekAudio);
        audio = new ListeningAudioPlayer(this, new ListeningAudioPlayer.Listener() {
            public void state(String message, boolean ready, boolean playing, boolean error) {
                audioStatus.setText(message); play.setText(playing ? "Ⅱ" : "▶");
                play.setEnabled(ready); seek.setEnabled(ready);
                findViewById(R.id.btnReplay).setEnabled(ready);
                findViewById(R.id.btnSpeed).setEnabled(ready);
                findViewById(R.id.btnRetryAudio).setVisibility(error ? View.VISIBLE : View.GONE);
            }
            public void progress(int position, int duration) {
                seek.setMax(duration); seek.setProgress(position);
                audioTime.setText(time(position) + " / " + time(duration));
            }
        });
        play.setOnClickListener(v -> audio.toggle());
        findViewById(R.id.btnReplay).setOnClickListener(v -> audio.replay());
        findViewById(R.id.btnRetryAudio).setOnClickListener(v -> prepareAudio());
        findViewById(R.id.btnSpeed).setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Tốc độ nghe").setItems(new String[]{"0.75x", "1.0x", "1.25x", "1.5x"}, (dialog, which) -> {
                    speed = new float[]{0.75f, 1f, 1.25f, 1.5f}[which];
                    audio.setSpeed(speed); updateSpeed();
                }).show());
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) { if (fromUser) audio.seek(progress); }
            public void onStartTrackingTouch(SeekBar bar) {}
            public void onStopTrackingTouch(SeekBar bar) {}
        });
        for (int i = 0; i < optionIds.length; i++) {
            final String letter = String.valueOf((char) ('A' + i));
            findViewById(optionIds[i]).setOnClickListener(v -> {
                if (!review) { answers.set(index, letter); displayQuestion(); }
            });
        }
        findViewById(R.id.btnListeningPrevious).setOnClickListener(v -> { if (index > 0) { index--; displayQuestion(); } });
        findViewById(R.id.btnListeningNext).setOnClickListener(v -> {
            if (index + 1 < session.questions.size()) { index++; displayQuestion(); }
        });
        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            if (review) { finish(); return; }
            int skipped = session.score(answers)[2];
            new AlertDialog.Builder(this).setTitle("Nộp bài?")
                    .setMessage("Còn " + skipped + " câu chưa trả lời. Sau khi nộp bạn có thể xem lại, nhưng không sửa đáp án.")
                    .setNegativeButton("Tiếp tục làm", null).setPositiveButton("Nộp bài", (dialog, which) -> {
                        audio.pause();
                        startActivity(new Intent(this, ListeningResult.class)
                                .putExtra("session", session).putStringArrayListExtra("answers", answers));
                        finish();
                    }).show();
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> leave());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { leave(); }
        });
        ImageView image = findViewById(R.id.imgListening);
        if (session.imageUrl != null && !session.imageUrl.isEmpty()) {
            image.setVisibility(View.VISIBLE);
            Glide.with(this).load(session.imageUrl).error(android.R.drawable.ic_menu_report_image).into(image);
        } else image.setVisibility(View.GONE);
        updateSpeed(); displayQuestion();
    }

    private void displayQuestion() {
        ListeningSubQuestion q = session.questions.get(index);
        ((TextView) findViewById(R.id.tvQuestionNumber)).setText("Câu " + (index + 1) + "/" + session.questions.size()
                + " · Số gốc " + q.questionNumber);
        ((TextView) findViewById(R.id.tvListeningQuestion)).setText(session.part >= 3 ? q.question
                : session.part == 1 ? "Nghe và chọn mô tả phù hợp với ảnh." : "Nghe và chọn phản hồi phù hợp.");
        for (int i = 0; i < optionIds.length; i++) {
            String letter = String.valueOf((char) ('A' + i));
            View option = findViewById(optionIds[i]);
            option.setVisibility(session.part == 2 && i == 3 ? View.GONE : View.VISIBLE);
            option.setEnabled(!review);
            boolean selected = letter.equals(answers.get(index));
            ((RadioButton) findViewById(radioIds[i])).setChecked(selected);
            option.setBackgroundResource(selected ? R.drawable.bg_answer_selected : R.drawable.bg_answer_option);
            ((TextView) findViewById(textIds[i])).setText(review || session.part >= 3 ? q.option(i) : "");
        }
        findViewById(R.id.btnListeningPrevious).setEnabled(index > 0);
        findViewById(R.id.btnListeningNext).setEnabled(index + 1 < session.questions.size());
        ((TextView) findViewById(R.id.btnSubmit)).setText(review ? "Kết thúc xem lại" : "Nộp bài");
        findViewById(R.id.listeningExplanation).setVisibility(review ? View.VISIBLE : View.GONE);
        if (review) {
            String chosen = answers.get(index);
            String result = chosen.isEmpty() ? "Bạn đã bỏ qua câu này." : "Bạn chọn " + chosen
                    + (chosen.equals(q.correctAnswer) ? " · Chính xác!" : " · Chưa chính xác.");
            String explanation = q.explanation == null || q.explanation.isEmpty() ? "Chưa có giải thích chi tiết." : q.explanation;
            String transcript = session.audioText == null || session.audioText.isEmpty() ? "Chưa có transcript." : session.audioText;
            ((TextView) findViewById(R.id.tvListeningExplanation)).setText(result + "\nĐáp án đúng: " + q.correctAnswer
                    + "\n\n" + explanation + "\n\n" + transcript);
        }
    }

    private void leave() {
        if (review) { finish(); return; }
        new AlertDialog.Builder(this).setTitle("Thoát bài luyện tập?")
                .setMessage("Bài làm chưa nộp sẽ không được lưu.")
                .setNegativeButton("Ở lại", null).setPositiveButton("Thoát", (dialog, which) -> finish()).show();
    }

    private void updateSpeed() { ((TextView) findViewById(R.id.btnSpeed)).setText(speed + "x ▾"); }
    private void prepareAudio() { audio.prepare(session.audioUrl, audioPosition, speed); }
    private static String time(int ms) { return String.format(Locale.US, "%d:%02d", ms / 60000, ms / 1000 % 60); }
    @Override protected void onStart() { super.onStart(); if (audio != null) prepareAudio(); }
    @Override protected void onStop() {
        if (audio != null) { audioPosition = audio.position(); audio.release(); }
        super.onStop();
    }
    @Override protected void onSaveInstanceState(Bundle out) {
        out.putSerializable("session", session); out.putStringArrayList("answers", answers);
        out.putInt("index", index); out.putBoolean("review", review);
        out.putInt("audioPosition", audio != null ? audio.position() : audioPosition);
        out.putFloat("speed", speed); super.onSaveInstanceState(out);
    }
}
