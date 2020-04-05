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

public class AvProducer implements Runnable {

  private AvTaskCache cache;
  private AvConsumer consumer;
  private AvTaskProducer taskProducer;

  public void setTaskProducer(AvTaskProducer taskProducer) {
    this.taskProducer = taskProducer;
  }

  public AvProducer(AvTaskCache cache) {
    this.cache = cache;
  }

  public void setConsumer(AvConsumer consumer) {
    this.consumer = consumer;
  }

  @Override
  public void run() {
    while (true) {
      try {
        if (cache.isFull()) {
          synchronized (this) {
            wait();
          }
        }
        cache.add(taskProducer.next());
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        synchronized (consumer) {
          consumer.notify();
        }
      }
    }
  }

  static AvTaskProducer createTaskProducer() {
    switch (Config.TASK_PRODUCER_MODE) {
    case RANDOM_PICTURE_MATCHING_AUDIO_TRACK:
      return new RandomPictureMatchingAudioTrackAvTaskProducer();
    case SERVICE_LOCAL:
      return new AvTaskLocalProducer();
    default:
      throw new IllegalArgumentException("Unsupported mode " + Config.TASK_PRODUCER_MODE);
    }
  }

}
