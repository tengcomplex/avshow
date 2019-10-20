package org.dschroeer.avshow;

import org.dschroeer.avshow.AvTaskCache.AvTask;

public interface AvTaskProducer {
  public AvTask next();
}
