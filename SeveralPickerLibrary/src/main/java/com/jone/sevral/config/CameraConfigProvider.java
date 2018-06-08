package com.jone.sevral.config;

public interface CameraConfigProvider {

    @CameraConfig.MediaAction
    int getMediaAction();

    @CameraConfig.MediaQuality
    int getMediaQuality();


    @CameraConfig.SensorPosition
    int getSensorPosition();

    int getDegrees();


    @CameraConfig.FlashMode
    int getFlashMode();

    @CameraConfig.CameraFace
    int getCameraFace();

    void setMediaQuality(int mediaQuality);

    void setPassedMediaQuality(int mediaQuality);

    void setVideoDuration(int videoDuration);

    void setVideoFileSize(long videoFileSize);

    void setMinimumVideoDuration(int minimumVideoDuration);

    void setFlashMode(int flashMode);

    void setSensorPosition(int sensorPosition);

    int getDeviceDefaultOrientation();

    void setDegrees(int degrees);

    void setMediaAction(int mediaAction);

    void setDeviceDefaultOrientation(int deviceDefaultOrientation);

    int getPassedMediaQuality();

    void setCameraConfig(CameraConfig cameraConfig);
}
