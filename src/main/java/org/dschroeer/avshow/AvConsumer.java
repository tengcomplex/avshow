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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvConsumer implements Runnable {
  private static final Logger L = Logger.getLogger(AvConsumer.class.getName());

  private final AvTaskCache cache;
  private final Gui gui;
  private final AvProducer producer;

  private Process currentAudioProcess;

  public AvConsumer(AvTaskCache cache, Gui gui, AvProducer producer) {
    this.cache = cache;
    this.gui = gui;
    this.producer = producer;
  }

  private PictureConsumer pictureConsumer;

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
        pictureConsumer = new PictureConsumer(task);
        if (task.getAudioPath() == null) {
          return;
        }
        pictureConsumer.start();
        currentAudioProcess = Runtime.getRuntime().exec(RuntimeCommand.getPlayAudioCommand(task.getAudioPath()));
        int exitCode = currentAudioProcess.waitFor();
        L.info("exit code: " + exitCode);
      } catch (IOException | InterruptedException | IllegalArgumentException | NullPointerException e) {
        L.log(Level.WARNING, "Exception ", e);
      } finally {
        pictureConsumer.interrupt();
      }
    }
  }

  class PictureConsumer extends Thread {
    private AvTask avTask;

    public PictureConsumer(AvTask avTask) {
      this.avTask = avTask;
    }

    @Override
    public void run() {
      long sleeptime = (avTask.getAudioDurationInSeconds() * 1000) / avTask.getPicturePath().length;
      for (int ii = 0; ii < avTask.getPicturePath().length; ii++) {
        try {
          gui.setPicture(avTask.getPicturePath()[ii]);
          gui.setFileNames(avTask.getAudioTrackNameWithoutLeadingFolder(),
              avTask.getPictureNameWithoutLeadingFolder(ii));
          Thread.sleep(sleeptime);
        } catch (IllegalArgumentException | NullPointerException | IOException e) {
          L.log(Level.WARNING, "Exception ", e);
        } catch (InterruptedException e) {
          L.info("interrupted");
          return;
        }
      }
    }
  }

  public void cleanup() {
    L.info("Interrupting current thread and destroying audio process");
    Thread.currentThread().interrupt();
    if (currentAudioProcess != null) {
      currentAudioProcess.destroy();
    }
    pictureConsumer.interrupt();
  }
}
