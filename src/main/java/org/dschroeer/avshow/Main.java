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

public class Main {

  public static void main(String[] args) {
    System.setProperty("java.util.logging.SimpleFormatter.format",
        "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$s %2$s %5$s%6$s%n");
    Gui gui = new Gui();
    AvTaskCache cache = new AvTaskCache();
    AvProducer producer = new AvProducer(cache);
    AvConsumer consumer = new AvConsumer(cache, gui, producer);
    producer.setConsumer(consumer);
    producer.setTaskProducer(AvProducer.createTaskPruducer());
    gui.setConsumer(consumer);
    new Thread(producer).start();
    new Thread(consumer).start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        cache.cleanup();
        consumer.cleanup();
      }
    });
  }

}
