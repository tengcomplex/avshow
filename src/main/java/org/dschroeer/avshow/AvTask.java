/*
 *  Avshow is a slideshow program.
 *  Copyright (C) 2019-2020  David Schröer <tengcomplexATgmail.com>
 *
 *
 *  This file is part of Avshow.
 *
 *  Avshow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Avshow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Avshow.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
