package com.walktour.Utils.audios;

import com.walktour.gui.task.parsedata.model.task.moc.TaskInitiativeCallModel;

/**
 * Created by Yi.Lin on 2018/2/27.
 * 录放音播放器接口
 */

public interface IAudioManager {


    /**
     * 播放音频文件
     * @param rawId 音频文件资源
     */
    void startPlaying(int rawId);

    /**
     * 停止播放音频
     */
    void stopPlaying();

    /**
     * 录制音频
     * @param filePath 文件存放路径
     * @param fileName 文件名称
     * @param sampleRate 采样率
     */
    void startRecording(String filePath, String fileName, int sampleRate);

    /**
     * 停止录制音频
     */
    void stopRecording();

    /**
     * 获取录音文件路径
     * @return
     */
    String getRecordFilePath(String rcuFileName);

    /**
     * 获取录音文件名称
     * @return
     */
    String getRecordFileName(TaskInitiativeCallModel callModel);
}
