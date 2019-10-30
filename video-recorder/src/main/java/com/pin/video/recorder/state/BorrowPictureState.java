package com.pin.video.recorder.state;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.pin.video.recorder.CameraInterface;
import com.pin.video.recorder.CameraView;

public class BorrowPictureState implements State {

    private final String TAG = BorrowPictureState.class.getSimpleName();

    private CameraMachine machine;

    public BorrowPictureState(CameraMachine machine) {
        this.machine = machine;
    }

    @Override
    public void start(SurfaceHolder holder, float screenProp) {
        CameraInterface.getInstance().doStartPreview(holder, screenProp);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void stop() {

    }


    @Override
    public void focus(float x, float y, CameraInterface.FocusCallback callback) {
    }

    @Override
    public void switchz(SurfaceHolder holder, float screenProp) {

    }

    @Override
    public void restart() {

    }

    @Override
    public void capture() {

    }

    @Override
    public void record(Surface surface, float screenProp) {

    }

    @Override
    public void stopRecord(boolean isShort, long time) {
    }

    @Override
    public void cancel(SurfaceHolder holder, float screenProp) {
        CameraInterface.getInstance().doStartPreview(holder, screenProp);
        machine.getView().resetState(CameraView.TYPE_PICTURE);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void confirm() {
        machine.getView().confirmState(CameraView.TYPE_PICTURE);
        machine.setState(machine.getPreviewState());
    }

    @Override
    public void zoom(float zoom, int type) {
        Log.d(TAG, "zoom");
    }

    @Override
    public void flash(String mode) {

    }

}
