package active.since93.recyclerview.audio;

import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;

/**
 * Created by myzupp on 26-02-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class MediaPlayerUtils {

    private static MediaPlayer mediaPlayer;
    private static MediaPlayerUtils.Listener listener;
    private static Handler mHandler;

    /**
     * Get database instance
     * @return database handler instance
     */
    public static void getInstance() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if(mHandler == null) {
            mHandler = new Handler();
        }
    }

    /**
     * Release mediaPlayer
     */
    public static void releaseMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pauseMediaPlayer() {
        mediaPlayer.pause();
    }

    public static void playMediaPlayer() {
        mediaPlayer.start();
        mHandler.postDelayed(onEverySecond, 100);
    }

    public static void applySeekBarValue(int selectedValue) {
        mediaPlayer.seekTo(selectedValue);
        mHandler.postDelayed(onEverySecond, 100);
    }

    /**
     * Start mediaPlayer
     * @param audioUrl sd card media file
     * @throws IOException exception
     */
    public static void startAndPlayMediaPlayer(String audioUrl, final MediaPlayerUtils.Listener listener) throws IOException {
        MediaPlayerUtils.listener = listener;
        getInstance();
        if(isPlaying()) {
            pauseMediaPlayer();
        }
        releaseMediaPlayer();
        getInstance();
        mediaPlayer.setDataSource(audioUrl);
        mediaPlayer.prepare();
        mediaPlayer.setOnCompletionListener(onCompletionListener);

        mHandler.postDelayed(onEverySecond, 100);
        playMediaPlayer();
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static int getTotalDuration() {
        return mediaPlayer.getDuration();
    }

    private static MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            MediaPlayerUtils.releaseMediaPlayer();
            listener.onAudioComplete();
        }
    };

    private static Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            try {
                if (isPlaying()) {
                    mHandler.postDelayed(onEverySecond, 100);
                    listener.onAudioUpdate(mediaPlayer.getCurrentPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    interface Listener {
        void onAudioComplete();
        void onAudioUpdate(int currentPosition);
    }

}
