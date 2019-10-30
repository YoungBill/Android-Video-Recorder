package com.pin.video.recorder.listener;

public interface RecordStateListener {

    void recordStart();

    void recordEnd(long time);

    void recordCancel();
}
