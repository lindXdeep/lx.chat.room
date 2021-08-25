package lx.lindx.talx.client;

import java.io.IOException;

import lx.lindx.talx.client.model.UserAddress;
import lx.lindx.talx.client.net.Connection;
import lx.lindx.talx.client.net.IMessageProcessor;

public class InetService {

  private final UserAddress addr;
  private Connection connection;

  private static IMessageProcessor msgProcessor;

  public InetService() {
    this(new UserAddress("127.0.0.1", 8181));
  }

  public InetService(final UserAddress userAddress) {
    this.addr = userAddress;
  }

  public void setMsgProcessor(IMessageProcessor msgProcessor) {
    InetService.msgProcessor = msgProcessor;
  }

  public InetService connect() {

    Util.toConsole("Trying to connect to ".concat(addr.getHost()).concat(" ").concat(String.valueOf(addr.getPort())));

    try {

      this.connection = new Connection(addr, this);
      this.connection.start();

      Util.toConsole("Connection with" + addr.getStringAddr() + "established!");
    } catch (IOException e) {
      Util.error(e);
    }
    return this;
  }

  public static void receive(String str) {
    msgProcessor.processMessage(str);
  }

  public void send(String str) {
    connection.sendMsg(str);
  }
}
