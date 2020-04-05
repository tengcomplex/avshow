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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AvTaskLocalProducer implements AvTaskProducer {
  private static final Logger L = Logger.getLogger(AvTaskLocalProducer.class.getName());

  private final ObjectMapper objectMapper = new ObjectMapper();

  public AvTaskLocalProducer() {
    L.info("Using command " + Arrays.toString(Config.LOCAL_PRODUCER_COMMAND));
  }

  @Override
  public AvTask next() {
    try {
      String task = RuntimeCommand.getFirstLineFromProcess(Config.LOCAL_PRODUCER_COMMAND);
      return objectMapper.readValue(task, AvTask.class);
    } catch (IOException | InterruptedException | RuntimeException e) {
      L.log(Level.WARNING, "Error reading task from " + Arrays.toString(Config.LOCAL_PRODUCER_COMMAND), e);
    }
    return new AvTask("", "Unknown");
  }
}
