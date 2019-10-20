package org.dschroeer.avshow;

import java.util.logging.Logger;

import org.dschroeer.avshow.AvTaskCache.AvTask;

public class AvTaskLocalProducer implements AvTaskProducer {
  private static final Logger L = Logger.getLogger(AvTaskLocalProducer.class.getName());

  @Override
  public AvTask next() {
    return new AvTask("", "Unknown");
  }
}
