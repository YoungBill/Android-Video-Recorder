package com.pin.video.recorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pin.video.compression.SiliCompressor;
import com.pin.video.recorder.listener.CameraListener;
import com.pin.video.recorder.listener.ErrorListener;
import com.pin.video.recorder.listener.RecordStateListener;
import com.pin.video.recorder.listener.SimpleClickListener;
import com.pin.video.recorder.util.ExtractVideoInfoUtil;
import com.pin.video.recorder.util.FileUtil;

import java.io.File;

public class VideoCameraActivity extends AppCompatActivity {

    private static final String TAG = VideoCameraActivity.class.getSimpleName();

    private CameraView mCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_camera);

        initView();
    }

    private void initView() {
        mCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        mCameraView.setSaveVideoPath(getExternalFilesDir("record-video").getAbsolutePath());
        mCameraView.setMinDuration(3000); //设置最短录制时长
        mCameraView.setDuration(10000); //设置最长录制时长
        mCameraView.setFeatures(CameraView.BUTTON_STATE_ONLY_RECORDER);
        mCameraView.setTip("长按拍摄, 3~10秒");
        mCameraView.setRecordShortTip("录制时间3~10秒");
        mCameraView.setMediaQuality(CameraView.MEDIA_QUALITY_MIDDLE);
        mCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Log.e(TAG, "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(VideoCameraActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        mCameraView.setCameraLisenter(new CameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Log.d(TAG, "bitmap = " + bitmap.getWidth());
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                Log.d(TAG, "url:" + url + ", firstFrame.width: " + firstFrame.getWidth() + ", firstFrame.height: " + firstFrame.getHeight());
                compressVideo(url);
            }
        });
        mCameraView.setLeftClickListener(new SimpleClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        mCameraView.setRightClickListener(new SimpleClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(VideoCameraActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
        });
        mCameraView.setRecordStateListener(new RecordStateListener() {
            @Override
            public void recordStart() {

            }

            @Override
            public void recordEnd(long time) {
                Log.d(TAG, "录制完毕，录制时长：" + time);
            }

            @Override
            public void recordCancel() {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    /**
     * 视频压缩
     */
    private void compressVideo(final String srcPath) {
        showCompressLoading();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    File destDir = new File(getExternalFilesDir("record-video") + File.separator + "compressed-video");
                    if (!destDir.exists() || !destDir.isDirectory()) {
                        destDir.mkdirs();
                    }
                    String destDirPath = destDir.getAbsolutePath();
                    String compressedFilePath = SiliCompressor.with(VideoCameraActivity.this).compressVideo(srcPath, destDirPath, 720, 480, 900000);
                    Log.d(TAG, "视频压缩成功: " + compressedFilePath);

                    //获取视频第一帧图片
                    ExtractVideoInfoUtil extractVideoInfoUtil = new ExtractVideoInfoUtil(compressedFilePath);
                    Bitmap bitmap = extractVideoInfoUtil.extractFrame();
                    String firstFrameFilePath = FileUtil.saveBitmap(destDirPath, bitmap);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                    Log.d(TAG, "视频第一帧图片获取成功: " + firstFrameFilePath);

                    dismissCompressLoading();

                    VideoPreviewActivity.startActivity(VideoCameraActivity.this, compressedFilePath, firstFrameFilePath);
                    finish();
                } catch (Exception e) {
                    dismissCompressLoading();
                    Log.e(TAG, "视频压缩失败: " + e.getMessage());
                }
            }
        });
    }

    private void showCompressLoading() {
        NormalProgressDialog.showLoading(this, "正在处理...", false);
    }

    private void dismissCompressLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NormalProgressDialog.stopLoading();
            }
        });
    }
}
