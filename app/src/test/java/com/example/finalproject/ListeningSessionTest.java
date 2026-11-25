package com.example.finalproject;

import com.example.finalproject.listening.ListeningPart2Question;
import com.example.finalproject.listening.ListeningSession;
import com.example.finalproject.listening.ListeningSubQuestion;
import org.junit.Test;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

public class ListeningSessionTest {
    private ListeningSubQuestion question(String answer) {
        ListeningSubQuestion q = new ListeningSubQuestion();
        q.question = "Question"; q.correctAnswer = answer;
        q.optionA = "First"; q.optionB = "Second"; q.optionC = "Third"; q.optionD = "Fourth";
        return q;
    }
    private ListeningSession set() {
        return new ListeningSession(3, "set", "Demo", "https://example.com/audio.mp3",
                "", "", "draft", Arrays.asList(question("B"), question("D"), question("A")));
    }

    @Test public void countsCorrectWrongSkippedIndependently() {
        ListeningSession s = set();
        assertArrayEquals(new int[]{1, 1, 1}, s.score(Arrays.asList("B", "A", "")));
        assertArrayEquals(new int[]{0, 0, 3}, s.score(Collections.emptyList()));
        assertArrayEquals(new int[]{3, 0, 0}, s.score(Arrays.asList("B", "D", "A")));
    }
    @Test public void part2RejectsFourthChoiceAndInvalidAnswerKey() {
        ListeningSubQuestion q = question("B");
        ListeningSession s = new ListeningSession(2, "q", "Demo", "https://example.com/a.mp3",
                "", "", "draft", Collections.singletonList(q));
        assertTrue(s.isValid());
        assertArrayEquals(new int[]{0, 0, 1}, s.score(Collections.singletonList("D")));
        q.correctAnswer = "D";
        assertFalse(s.isValid());
    }
    @Test public void groupedQuestionsRequireCompleteOptionsAndThreeQuestions() {
        ListeningSession s = set();
        assertTrue(s.isValid());
        s.questions.get(1).optionD = "";
        assertFalse(s.isValid());
        s = set(); s.questions.remove(0);
        assertFalse(s.isValid());
    }
    @Test public void snapshotPreservesInheritedPart2FieldsAndScore() throws Exception {
        ListeningPart2Question q = new ListeningPart2Question();
        q.questionNumber = 7; q.correctAnswer = "B"; q.audioUrl = "https://example.com/a.mp3";
        ListeningSession s = new ListeningSession(2, "demo", "Demo", q.audioUrl,
                "", "", "draft", Collections.singletonList(q));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(s); }
        ListeningSession restored;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (ListeningSession) in.readObject();
        }
        assertTrue(restored.isValid());
        assertEquals(7, restored.questions.get(0).questionNumber);
        assertArrayEquals(new int[]{1, 0, 0}, restored.score(Collections.singletonList("B")));
    }
}
