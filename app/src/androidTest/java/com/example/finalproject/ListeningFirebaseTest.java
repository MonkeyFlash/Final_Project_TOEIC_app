package com.example.finalproject;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.finalproject.listening.ListeningRepository;
import com.example.finalproject.listening.ListeningSession;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/** Read-only integration check, using the device's existing signed-in app session. */
@RunWith(AndroidJUnit4.class)
public class ListeningFirebaseTest {
    private ListeningSession readSession(int part) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ListeningSession> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        new ListeningRepository().loadDemo(part, new ListeningRepository.Callback() {
            public void loaded(ListeningSession session) { result.set(session); latch.countDown(); }
            public void failed(String message) { error.set(message); latch.countDown(); }
        });
        assertTrue("Timed out reading Part " + part, latch.await(25, TimeUnit.SECONDS));
        assertNull(error.get(), error.get());
        assertNotNull(result.get());
        return result.get();
    }

    @Test public void readsAndMapsAllFourDemoCollections() throws Exception {
        assumeTrue("Sign in to the app before running the live Firestore check",
                FirebaseAuth.getInstance().getCurrentUser() != null);
        for (int part = 1; part <= 4; part++) {
            ListeningSession result = readSession(part);
            assertTrue(result.isValid());
            assertEquals(part <= 2 ? 1 : 3, result.questions.size());
        }
    }

    @Test public void realPart3AudioContinuesWhileChangingQuestions() throws Exception {
        assumeTrue(FirebaseAuth.getInstance().getCurrentUser() != null);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ListeningLearn.class)
                .putExtra("session", readSession(3));
        try (ActivityScenario<ListeningLearn> scenario = ActivityScenario.launch(intent)) {
            AtomicBoolean ready = new AtomicBoolean();
            long deadline = SystemClock.elapsedRealtime() + 28000;
            while (!ready.get() && SystemClock.elapsedRealtime() < deadline) {
                scenario.onActivity(a -> ready.set(a.findViewById(R.id.btnPlayAudio).isEnabled()));
                SystemClock.sleep(100);
            }
            assertTrue("Audio did not become ready", ready.get());
            scenario.onActivity(a -> a.findViewById(R.id.btnPlayAudio).performClick());
            SystemClock.sleep(1200);
            scenario.onActivity(a -> {
                SeekBar seek = a.findViewById(R.id.seekAudio);
                assertTrue("No audio duration", seek.getMax() > 1000);
                assertTrue("Playback did not advance", seek.getProgress() > 0);
                a.findViewById(R.id.btnListeningNext).performClick();
                assertTrue(((TextView) a.findViewById(R.id.tvQuestionNumber)).getText().toString().contains("2/3"));
                assertEquals("Ⅱ", ((TextView) a.findViewById(R.id.btnPlayAudio)).getText().toString());
                a.findViewById(R.id.btnPlayAudio).performClick();
                assertEquals("▶", ((TextView) a.findViewById(R.id.btnPlayAudio)).getText().toString());
            });
        }
    }
}
