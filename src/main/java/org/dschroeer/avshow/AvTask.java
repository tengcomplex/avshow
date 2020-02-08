package org.dschroeer.avshow;

import java.util.Arrays;

public class AvTask {
  private String[] picturePath;
  private String audioPath;
  private int audioDurationInSeconds;

  public AvTask() {
    super();
  }

  public AvTask(String[] picPath, String audioPath) {
    super();
    this.picturePath = picPath;
    this.audioPath = audioPath;
  }

  public AvTask(String picPath, String audioPath) {
    this(new String[] {picPath}, audioPath);
  }

  public String[] getPicturePath() {
    return picturePath;
  }

  public String getPicturePath(int index) {
    return picturePath[index];
  }

  public void setPicturePath(String[] picturePath) {
    this.picturePath = picturePath;
  }

  public void setAudioPath(String audioPath) {
    this.audioPath = audioPath;
  }

  public String getPictureNameWithoutLeadingFolder(int index) {
    return picturePath[index].replaceFirst(Config.PICTURE_FOLDER, "");
  }

  public String getAudioPath() {
    return audioPath;
  }

  public int getAudioDurationInSeconds() {
    return audioDurationInSeconds;
  }

  public void setAudioDurationInSeconds(int audioDurationInSeconds) {
    this.audioDurationInSeconds = audioDurationInSeconds;
  }

  public String getAudioTrackNameWithoutLeadingFolder() {
    return audioPath != null ? audioPath.replaceFirst(Config.AUDIO_FOLDER, "") : "Error: null audio";
  }

  @Override
  public String toString() {
    return "AvTask [picture=" + Arrays.toString(picturePath) + ", audio=" + audioPath
        + ", audioDurationInSeconds=" + audioDurationInSeconds + "]";
  }
}
