package com.example.finalproject.listening;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Small snapshot of one exercise, retained for rotation and independent answer review. */
public class ListeningSession implements Serializable {
    public int part;
    public String documentId, title, audioUrl, audioText, imageUrl;
    public boolean draft;
    public ArrayList<ListeningSubQuestion> questions;

    public ListeningSession(int part, String id, String title, String audioUrl,
                            String audioText, String imageUrl, String status,
                            List<ListeningSubQuestion> questions) {
        this.part = part;
        this.documentId = id;
        this.title = title;
        this.audioUrl = audioUrl;
        this.audioText = audioText;
        this.imageUrl = imageUrl;
        this.draft = "draft".equals(status);
        this.questions = new ArrayList<>(questions);
    }

    public boolean isValid() {
        if (part < 1 || part > 4 || audioUrl == null || !audioUrl.startsWith("https://")
                || questions == null || questions.isEmpty() || questions.size() > 30) return false;
        if (part == 1 && (imageUrl == null || !imageUrl.startsWith("https://"))) return false;
        if (part <= 2 && questions.size() != 1) return false;
        if (part >= 3 && questions.size() != 3) return false;
        String allowed = part == 2 ? "ABC" : "ABCD";
        for (ListeningSubQuestion q : questions) {
            if (q == null || q.correctAnswer == null || q.correctAnswer.length() != 1
                    || !allowed.contains(q.correctAnswer)) return false;
            if (part >= 3) {
                if (q.question == null || q.question.trim().isEmpty()) return false;
                for (int i = 0; i < 4; i++) if (q.option(i).trim().isEmpty()) return false;
            }
        }
        return true;
    }

    /** correct, wrong, skipped. Only a valid selected letter can count as correct. */
    public int[] score(List<String> answers) {
        int correct = 0, wrong = 0, skipped = 0;
        String allowed = part == 2 ? "ABC" : "ABCD";
        for (int i = 0; i < questions.size(); i++) {
            String answer = i < answers.size() ? answers.get(i) : "";
            if (answer == null || answer.length() != 1 || !allowed.contains(answer)) skipped++;
            else if (answer.equals(questions.get(i).correctAnswer)) correct++;
            else wrong++;
        }
        return new int[]{correct, wrong, skipped};
    }
}
