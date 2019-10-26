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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class AvTaskCache {

  static private final Logger L = Logger.getLogger(AvTaskCache.class.getName());

  private Queue<AvTask> queue = new ConcurrentLinkedQueue<>();

  public boolean isFull() {
    return queue.size() >= Config.CACHE_SIZE;
  }

  public boolean isEmpty() {
    return queue == null || queue.isEmpty();
  }

  public void add(AvTask task) {
    queue.add(task);
    L.info("AvTask:" + task + ", new cache size:" + queue.size());
  }

  public void cleanup() {
    queue.clear();
    queue = null;
  }

  public AvTask poll() {
    return queue.poll();
  }
}
