package com.pin.video.recorder;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class VideoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = VideoPreviewActivity.class.getSimpleName();

    private FrameLayout mFlVideo;
    private VideoView mVideoView;
    private ImageView mIvThumb;
    private ImageView mIvPlay;

    private String mVideoPath;
    private String mVideoThumb;

    public static void startActivity(Context context, String videoPath, String videoThumb) {
        Intent intent = new Intent(context, VideoPreviewActivity.class);
        intent.putExtra("path", videoPath);
        intent.putExtra("thumb", videoThumb);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoDestroy();
    }

    private void initView() {
        mVideoPath = getIntent().getStringExtra("path");
        mVideoThumb = getIntent().getStringExtra("thumb");

        mFlVideo = findViewById(R.id.fl_video);
        mVideoView = findViewById(R.id.view_video);
        mIvThumb = findViewById(R.id.iv_thumb);
        mIvPlay = findViewById(R.id.iv_play);
        mIvPlay.setOnClickListener(this);

        mVideoView.setVideoPath(mVideoPath);
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
                int videoWidth = mp.getVideoWidth();
                int videoHeight = mp.getVideoHeight();
                float videoProportion = (float) videoWidth / (float) videoHeight;
                int screenWidth = mFlVideo.getWidth();
                int screenHeight = mFlVideo.getHeight();
                float screenProportion = (float) screenWidth / (float) screenHeight;
                if (videoProportion > screenProportion) {
                    lp.width = screenWidth;
                    lp.height = (int) ((float) screenWidth / videoProportion);
                } else {
                    lp.width = (int) (videoProportion * (float) screenHeight);
                    lp.height = screenHeight;
                }
                mVideoView.setLayoutParams(lp);

                Log.d(TAG, "videoWidth:" + videoWidth + ", videoHeight:" + videoHeight);
            }
        });
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mIvPlay.setVisibility(View.VISIBLE);
                mIvThumb.setVisibility(View.VISIBLE);
                Glide.with(VideoPreviewActivity.this)
                        .load(mVideoThumb)
                        .into(mIvThumb);
            }
        });
        videoStart();
    }

    private void videoStart() {
        mVideoView.start();
    }

    private void videoPause() {
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    private void videoDestroy() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                mIvThumb.setVisibility(View.GONE);
                mIvPlay.setVisibility(View.GONE);
                videoStart();
                break;
        }
    }
}
