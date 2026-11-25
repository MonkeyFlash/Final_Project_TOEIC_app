package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import com.example.finalproject.listening.ListeningRepository;
import com.example.finalproject.listening.ListeningSession;

public class ListeningIntro extends AppCompatActivity {
    private ListeningSession session;
    private int part;
    private TextView status, start;

    public static String partName(int part) {
        String[] names = {"", "Photographs", "Question–Response", "Conversations", "Talks"};
        return part >= 1 && part <= 4 ? "Part " + part + " · " + names[part] : "Listening";
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_listening_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom); return insets;
        });
        part = getIntent().getIntExtra("part", 0);
        if (part < 1 || part > 4) { finish(); return; }
        ((TextView) findViewById(R.id.tvIntroPart)).setText(partName(part));
        String description = part == 1 ? "Nghe bốn mô tả và chọn câu phù hợp với ảnh. Trong lúc làm bài chỉ hiện A–D."
                : part == 2 ? "Nghe câu hỏi và ba phản hồi, chọn A, B hoặc C. Trong lúc làm bài không hiện nội dung lời thoại."
                : "Nghe " + (part == 3 ? "hội thoại" : "bài nói") + " và trả lời ba câu hỏi. Audio được giữ nguyên khi chuyển câu trong cùng bộ.";
        ((TextView) findViewById(R.id.tvIntroDescription)).setText(description);
        status = findViewById(R.id.tvIntroStatus);
        start = findViewById(R.id.btnStartListening);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnRetryListening).setOnClickListener(v -> load());
        start.setOnClickListener(v -> {
            if (session != null) startActivity(new Intent(this, ListeningLearn.class).putExtra("session", session));
        });
        if (savedInstanceState != null) session = (ListeningSession) savedInstanceState.getSerializable("session");
        if (session != null && session.isValid()) showSession(); else load();
    }

    private void load() {
        start.setEnabled(false); start.setText("Đang tải…");
        status.setText("Đang tải bài nghe từ Firestore…");
        findViewById(R.id.btnRetryListening).setVisibility(View.GONE);
        new ListeningRepository().loadDemo(part, new ListeningRepository.Callback() {
            public void loaded(ListeningSession loaded) {
                if (isFinishing() || isDestroyed()) return;
                session = loaded; showSession();
            }
            public void failed(String message) {
                if (isFinishing() || isDestroyed()) return;
                status.setText(message); start.setText("Chưa có bài nghe");
                findViewById(R.id.btnRetryListening).setVisibility(View.VISIBLE);
            }
        });
    }

    private void showSession() {
        status.setText(session.questions.size() + " câu trong lượt luyện tập này."
                + (session.draft ? "\nBản demo: đáp án đã đối chiếu tài liệu; đang chờ kiểm duyệt audio." : ""));
        start.setText("Bắt đầu luyện tập"); start.setEnabled(true);
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        out.putSerializable("session", session); super.onSaveInstanceState(out);
    }
}
