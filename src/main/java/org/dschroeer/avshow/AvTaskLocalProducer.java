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
