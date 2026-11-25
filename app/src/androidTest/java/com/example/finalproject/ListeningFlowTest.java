package com.example.finalproject;

import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.finalproject.listening.ListeningSession;
import com.example.finalproject.listening.ListeningSubQuestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Collections;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ListeningFlowTest {
    private Intent exercise(boolean review) {
        ListeningSubQuestion q = new ListeningSubQuestion();
        q.correctAnswer = "B"; q.questionNumber = 7;
        q.optionA = "Hidden A"; q.optionB = "Hidden B"; q.optionC = "Hidden C";
        ListeningSession s = new ListeningSession(2, "test", "Test", "https://example.com/audio.mp3",
                "", "", "draft", Collections.singletonList(q));
        return new Intent(ApplicationProvider.getApplicationContext(), ListeningLearn.class)
                .putExtra("session", s).putExtra("review", review)
                .putStringArrayListExtra("answers", new ArrayList<>(Collections.singletonList(review ? "B" : "")));
    }

    @Test public void part2HidesContentAllowsEditingAndRestoresSelection() {
        try (ActivityScenario<ListeningLearn> scenario = ActivityScenario.launch(exercise(false))) {
            scenario.onActivity(activity -> {
                assertEquals(View.GONE, activity.findViewById(R.id.optionD).getVisibility());
                assertEquals(View.GONE, activity.findViewById(R.id.imgListening).getVisibility());
                assertEquals("", ((TextView) activity.findViewById(R.id.tvAnswerB)).getText().toString());
                assertEquals(View.GONE, activity.findViewById(R.id.listeningExplanation).getVisibility());
                activity.findViewById(R.id.optionA).performClick();
                activity.findViewById(R.id.optionC).performClick();
                assertTrue(((RadioButton) activity.findViewById(R.id.rbC)).isChecked());
                assertFalse(((RadioButton) activity.findViewById(R.id.rbA)).isChecked());
            });
            scenario.recreate();
            scenario.onActivity(activity -> assertTrue(((RadioButton) activity.findViewById(R.id.rbC)).isChecked()));
        }
    }

    @Test public void reviewRevealsContentAndLocksAnswers() {
        try (ActivityScenario<ListeningLearn> scenario = ActivityScenario.launch(exercise(true))) {
            scenario.onActivity(activity -> {
                assertEquals("Hidden B", ((TextView) activity.findViewById(R.id.tvAnswerB)).getText().toString());
                assertEquals(View.VISIBLE, activity.findViewById(R.id.listeningExplanation).getVisibility());
                activity.findViewById(R.id.optionA).performClick();
                assertTrue(((RadioButton) activity.findViewById(R.id.rbB)).isChecked());
                assertFalse(((RadioButton) activity.findViewById(R.id.rbA)).isChecked());
            });
        }
    }
}
