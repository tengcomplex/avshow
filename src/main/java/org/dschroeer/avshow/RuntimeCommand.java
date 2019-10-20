package org.dschroeer.avshow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RuntimeCommand {
  private static final String BASE_FIND_AUDIO_COMMAND = "find " + Config.AUDIO_FOLDER + "/ -type f " + Config.AUDIO_TYPES;
  private static final String SHUFFLE_COMMAND = "shuf -n1";

  private static final String[] shellCommand = { "/bin/sh", "-c", null };
  /**
   * Elements of {@link Config#AUDIO_COMMAND} plus one, where we put the audio path.
   */
  private static final String[] playAudioCommand = Config.AUDIO_COMMAND.toArray(new String[Config.AUDIO_COMMAND.size() + 1]);

  private static String[] getEnrichedCommand(String[] base, String cmd) {
    String[] ret = new String[base.length];
    System.arraycopy(base, 0, ret, 0, ret.length);
    ret[ret.length - 1] = cmd;
    return ret;
  }

  private static String[] getShellCommand(String cmd) {
    return getEnrichedCommand(shellCommand, cmd);
  }

  static String[] getFindPictureCommand() {
    return getShellCommand("ls " + Config.PICTURE_FOLDER + "/*.* | " + SHUFFLE_COMMAND);
  }

  static String[] getFindAudioCommand(String pattern) {
    if (pattern != null) {
      return getShellCommand(BASE_FIND_AUDIO_COMMAND + " -regextype posix-egrep -iregex \".*(" + pattern + ").*\" | " + SHUFFLE_COMMAND);
    }
    return getShellCommand(BASE_FIND_AUDIO_COMMAND + " | " + SHUFFLE_COMMAND);
  }

  static String[] getPlayAudioCommand(String audioPath) {
    return getEnrichedCommand(playAudioCommand, audioPath);
  }

  static String getFirstLineFromProcess(String[] cmd) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(cmd);
    p.waitFor();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
      String line;
      if ((line = in.readLine()) != null) {
        return line;
      }
    }
    return null;
  }
}
