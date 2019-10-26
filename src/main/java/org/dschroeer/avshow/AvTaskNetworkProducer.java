package org.dschroeer.avshow;

import java.util.logging.Logger;

public class AvTaskNetworkProducer implements AvTaskProducer {
  private static final Logger L = Logger.getLogger(AvTaskNetworkProducer.class.getName());

  @Override
  public AvTask next() {
    return new AvTask("", "Unknown");
  }
}
