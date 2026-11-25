package com.example.finalproject.listening;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import java.io.IOException;

/** One player per exercise: changing a question within a set preserves playback. */
public class ListeningAudioPlayer {
    public interface Listener {
        void state(String message, boolean ready, boolean playing, boolean error);
        void progress(int position, int duration);
    }
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AudioManager audioManager;
    private final Listener listener;
    private MediaPlayer player;
    private boolean ready;
    private float speed = 1f;
    private int lastPosition;
    private final AudioManager.OnAudioFocusChangeListener focusListener = change -> {
        if (change <= 0) pause();
    };
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (ready && player != null) {
                listener.progress(position(), player.getDuration());
                handler.postDelayed(this, 500);
            }
        }
    };
    private final Runnable timeout = () -> fail("Tải audio quá lâu. Kiểm tra mạng rồi thử lại.");

    public ListeningAudioPlayer(Context context, Listener listener) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.listener = listener;
    }

    public void prepare(String url, int position, float speed) {
        release();
        lastPosition = Math.max(0, position); this.speed = speed;
        MediaPlayer next = new MediaPlayer(); player = next;
        listener.state("Đang tải audio…", false, false, false);
        next.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build());
        next.setOnPreparedListener(mp -> {
            if (player != mp) return;
            handler.removeCallbacks(timeout); ready = true;
            if (lastPosition > 0) mp.seekTo(Math.min(lastPosition, mp.getDuration()));
            listener.state("Audio sẵn sàng. Nhấn ▶ để nghe.", true, false, false);
            handler.post(tick);
        });
        next.setOnCompletionListener(mp -> {
            if (player != mp) return;
            lastPosition = 0;
            audioManager.abandonAudioFocus(focusListener);
            listener.state("Đã nghe hết. Có thể phát lại.", true, false, false);
        });
        next.setOnErrorListener((mp, what, extra) -> {
            if (player == mp) fail("Không phát được audio. Nhấn thử tải lại.");
            return true;
        });
        try {
            next.setDataSource(url); next.prepareAsync();
            handler.postDelayed(timeout, 25000);
        } catch (IOException | RuntimeException e) { fail("Không tải được audio. Kiểm tra mạng rồi thử lại."); }
    }

    public void toggle() {
        if (!ready || player == null) return;
        if (player.isPlaying()) { pause(); return; }
        if (audioManager.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            listener.state("Thiết bị đang sử dụng âm thanh. Vui lòng thử lại.", true, false, false); return;
        }
        try {
            player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed)); player.start();
            listener.state("Đang phát", true, true, false);
        } catch (RuntimeException e) { fail("Không phát được audio. Hãy thử tải lại."); }
    }

    public void pause() {
        if (ready && player != null && player.isPlaying()) {
            player.pause(); listener.state("Đã tạm dừng", true, false, false);
        }
        audioManager.abandonAudioFocus(focusListener);
    }

    public void replay() {
        if (!ready || player == null) return;
        boolean wasPlaying = player.isPlaying(); seek(0);
        if (!wasPlaying) toggle();
    }

    public void seek(int position) {
        if (ready && player != null) {
            lastPosition = Math.max(0, Math.min(position, player.getDuration())); player.seekTo(lastPosition);
        }
    }

    public void setSpeed(float value) {
        speed = value;
        if (!ready || player == null) return;
        try {
            boolean playing = player.isPlaying();
            player.setPlaybackParams(player.getPlaybackParams().setSpeed(speed));
            if (!playing) player.pause();
        } catch (RuntimeException e) { fail("Không đổi được tốc độ. Hãy tải lại audio."); }
    }

    public int position() {
        if (ready && player != null) lastPosition = player.getCurrentPosition();
        return lastPosition;
    }
    private void fail(String message) { release(); listener.state(message, false, false, true); }
    public void release() {
        handler.removeCallbacksAndMessages(null); ready = false;
        if (player != null) { player.release(); player = null; }
        audioManager.abandonAudioFocus(focusListener);
    }
}
