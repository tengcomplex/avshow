package org.dschroeer.avshow;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AvTaskLocalProducer implements AvTaskProducer {
  private static final Logger L = Logger.getLogger(AvTaskLocalProducer.class.getName());

  public AvTaskLocalProducer() {
    L.info("Using command " + Arrays.toString(Config.LOCAL_PRODUCER_COMMAND));
  }


  @Override
  public AvTask next() {
    try {
      String task = RuntimeCommand.getFirstLineFromProcess(Config.LOCAL_PRODUCER_COMMAND);
System.out.println("AvTaskLocalProducer.next(), task:" + task);
      ObjectMapper objectMapper = new ObjectMapper();
      AvTask avTask = objectMapper.readValue(task, AvTask.class);
System.out.println("AvTaskLocalProducer.next(), myAvTask:" + avTask);
      return avTask;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new AvTask("", "Unknown");
  }
}
