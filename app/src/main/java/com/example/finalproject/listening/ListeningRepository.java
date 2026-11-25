package com.example.finalproject.listening;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Collections;

public class ListeningRepository {
    public interface Callback {
        void loaded(ListeningSession session);
        void failed(String message);
    }

    // Explicit demo selection keeps unreviewed drafts out of any future production query.
    private static final String[] DEMO_IDS = {"", "demo_zenlish_2026_test1_85514",
            "demo_zenlish_2026_test1_85552", "demo_zenlish_2026_test1_85631",
            "demo_zenlish_2026_test1_86218"};

    public void loadDemo(int part, Callback callback) {
        if (part < 1 || part > 4) { callback.failed("Part không hợp lệ."); return; }
        FirebaseFirestore.getInstance().collection("listening_part" + part)
                .document(DEMO_IDS[part]).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) { callback.failed("Chưa có bài luyện tập cho Part này."); return; }
                    if (!Boolean.TRUE.equals(document.getBoolean("demoOnly"))) {
                        callback.failed("Bài này chưa được cấu hình cho demo."); return;
                    }
                    try {
                        ListeningSession session = map(part, document);
                        if (session == null || !session.isValid()) callback.failed("Dữ liệu bài nghe thiếu audio, ảnh hoặc đáp án hợp lệ.");
                        else callback.loaded(session);
                    } catch (RuntimeException e) {
                        callback.failed("Cấu trúc dữ liệu bài nghe không hợp lệ.");
                    }
                })
                .addOnFailureListener(error -> callback.failed(
                        "Không tải được bài nghe. Kiểm tra mạng và quyền đọc Firestore, rồi thử lại."));
    }

    private ListeningSession map(int part, DocumentSnapshot document) {
        switch (part) {
            case 1: {
                ListeningPart1Question q = document.toObject(ListeningPart1Question.class);
                return q == null ? null : new ListeningSession(part, document.getId(), q.title,
                        q.audioUrl, q.audioText, q.imageUrl, q.status, Collections.singletonList(q));
            }
            case 2: {
                ListeningPart2Question q = document.toObject(ListeningPart2Question.class);
                return q == null ? null : new ListeningSession(part, document.getId(), q.title,
                        q.audioUrl, q.audioText, q.imageUrl, q.status, Collections.singletonList(q));
            }
            case 3: {
                ListeningPart3Set set = document.toObject(ListeningPart3Set.class);
                return set == null ? null : new ListeningSession(part, document.getId(), set.title,
                        set.audioUrl, set.audioText, set.imageUrl, set.status, set.questions);
            }
            case 4: {
                ListeningPart4Set set = document.toObject(ListeningPart4Set.class);
                return set == null ? null : new ListeningSession(part, document.getId(), set.title,
                        set.audioUrl, set.audioText, set.imageUrl, set.status, set.questions);
            }
            default: return null;
        }
    }
}
