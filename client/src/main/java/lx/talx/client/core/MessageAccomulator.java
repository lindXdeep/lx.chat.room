package lx.talx.client.core;

import lx.talx.client.api.Connect;
import lx.talx.client.service.IMessageProcessor;
import lx.talx.client.utils.Util;

public class MessageAccomulator {

  private Connect connect;

  private Thread thread;

  public MessageAccomulator(Connect connect) {
    this.connect = connect;
  }

  public void readMeaasges(IMessageProcessor msgProcessor) {

    thread = new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {

        String recive = Util.byteToStr(connect.read());

        if (recive.matches("^@[a-zA-Z]{3,64}\\s.{0,4096}")) {
          msgProcessor.processMessage(recive);

        } else if (recive.matches("^/[a-zA-Z]{1,64}\\s.{0,4096}")) {
          msgProcessor.processCommand(recive);
        }
      }
    });
    thread.start();
  }
}
