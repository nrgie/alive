package com.blueobject.app.alive.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.blueobject.app.alive.R;

import java.io.IOException;

public class AudioPlayerView extends View {
    private static final String NULL_PARAMETER_ERROR = "`stopText`, `playText` and `loadingText`" +
            " must have some value, if `useIcons` is set to false. Set `useIcons` to true, or add strings to stopText`, " +
            "`playText` and `loadingText` in the AudioPlayerView.xml";
    private Context context;
    private MediaPlayer mediaPlayer;
    private String playText;
    private String stopText;
    private String loadingText;
    private String url;
    private boolean useIcons;
    private boolean audioReady;
    private boolean usesCustomIcons;

    int length;

    boolean started = false;

    //Callbacks
    public interface OnAudioPlayerViewListener {
        void onAudioPreparing();

        void onAudioReady();

        void onAudioFinished();
    }

    private OnAudioPlayerViewListener listener;

    public void setOnAudioPlayerViewListener(OnAudioPlayerViewListener listener) {
        this.listener = listener;
    }

    private void sendCallbackAudioFinished() {
        if (listener != null)
            listener.onAudioFinished();
    }

    private void sendCallbackAudioReady() {
        if (listener != null)
            listener.onAudioReady();
    }

    private void sendCallbackAudioPreparing() {
        if (listener != null)
            listener.onAudioPreparing();
    }

    //Constructors
    public AudioPlayerView(Context context) {
        super(context);
        this.context = context;
        setUpMediaPlayer();
    }

    public AudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        getAttributes(attrs);

        setUpMediaPlayer();
    }

    public AudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        getAttributes(attrs);

        setUpMediaPlayer();
    }

    public void getAttributes(AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AudioPlayerTextView, 0, 0);

        try {
            stopText = a.getString(R.styleable.AudioPlayerTextView_stopText);
            playText = a.getString(R.styleable.AudioPlayerTextView_playText);
            loadingText = a.getString(R.styleable.AudioPlayerTextView_loadingText);
            useIcons = a.getBoolean(R.styleable.AudioPlayerTextView_useIcons, true);

            if ((stopText != null && playText != null && loadingText != null) && useIcons)
                usesCustomIcons = true;
            else if ((stopText == null || playText == null || loadingText == null) && !useIcons)
                throw new UnsupportedOperationException(NULL_PARAMETER_ERROR);

        } finally {
            a.recycle();
        }
    }

    private void setUpMediaPlayer() {

        setBackground(context.getDrawable(R.drawable.ic_play_circle_outline_black_36dp));
        this.setOnClickListener(onViewClickListener);

        mediaPlayer = MediaPlayer.create(getContext(), R.raw.demo_en);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                setBackground(context.getDrawable(R.drawable.ic_play_circle_outline_black_36dp));
                started = false;
            }
        });

    }

    private OnClickListener onViewClickListener = new OnClickListener() {

        public void onClick(View v) {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        length = mediaPlayer.getCurrentPosition();
                        setBackground(context.getDrawable(R.drawable.ic_play_circle_outline_black_36dp));
                    } else {
                        if(!started) {
                            mediaPlayer.start();
                            started = true;
                        } else {
                            mediaPlayer.seekTo(length);
                            mediaPlayer.start();
                        }

                        setBackground(context.getDrawable(R.drawable.ic_pause_circle_outline_black_36dp));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
}
