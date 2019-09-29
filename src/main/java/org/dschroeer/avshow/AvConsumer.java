/*
 *  Avshow is a slideshow program.
 *  Copyright (C) 2019  David Schröer <tengcomplexATgmail.com>
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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dschroeer.avshow.AvTaskCache.AvTask;

public class AvConsumer implements Runnable {

  static private final Logger L = Logger.getLogger(AvConsumer.class.getName());

  private AvTaskCache cache;
  private Gui gui;
  private Process currentAudioProcess;
  private AvProducer producer;
  /**
   * Elements of {@link Config#AUDIO_COMMAND} plus one, where we put the audio path.
   */
  private final String[] audioCmd = Config.AUDIO_COMMAND.toArray(new String[Config.AUDIO_COMMAND.size() + 1]);

  public AvConsumer(AvTaskCache cache, Gui gui, AvProducer producer) {
    this.cache = cache;
    this.gui = gui;
    this.producer = producer;
  }

  @Override
  public void run() {

    while (true) {
      try {
        if (cache.isEmpty()) {
          synchronized (this) {
            wait();
          }
        }
        if (Thread.currentThread().isInterrupted()) {
          return;
        }
        AvTask task = cache.poll();
        synchronized (producer) {
          producer.notify();
        }
        L.info("task: " + task);
        gui.setPicture(task.getPicPath());
        gui.setFileNames(task.getAudioTrackNameWithoutLeadingFolder(), task.getPictureNameWithoutLeadingFolder());
        if (task.getAudioPath() == null) {
          return;
        }
        audioCmd[audioCmd.length - 1] = task.getAudioPath();
        currentAudioProcess = Runtime.getRuntime().exec(audioCmd);
        int exitCode = currentAudioProcess.waitFor();
        L.info("exit code: " + exitCode);
      } catch (IOException | InterruptedException | IllegalArgumentException e) {
        L.log(Level.WARNING, "Exception ", e);
      }
    }
  }

  public void cleanup() {
    L.info("Interrupting current thread and destroying audio process");
    Thread.currentThread().interrupt();
    if (currentAudioProcess != null) {
      currentAudioProcess.destroy();
    }
  }
}
