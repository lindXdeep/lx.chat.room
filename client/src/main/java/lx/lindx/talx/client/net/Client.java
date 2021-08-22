package lx.lindx.talx.client.net;

import java.io.IOException;
import java.net.Socket;

import lx.lindx.talx.client.Util;

public class Client {

  private final UserAddress addr;
  private Connection connection;

  private static IMessageProcessor msgProcessor;

  public Client() {
    this(new UserAddress("127.0.0.1", 8181));
  }

  public Client(final UserAddress userAddress) {
    this.addr = userAddress;
  }

  public void connect(IMessageProcessor iMessageProcessor) {
    
    Client.msgProcessor = iMessageProcessor;

    Util.toConsole("Trying to connect to ".concat(addr.getHost()).concat(" ").concat(String.valueOf(addr.getPort())));

    try {

      this.connection = new Connection(addr, this);
      this.connection.start();

      Util.toConsole("Connection with" + addr.getStringAddr() + "established!");
    } catch (IOException e) {
      Util.error(e);
    }
  }

  public static void receive(String string) {
    msgProcessor.processMessage(string);
  }

  public void sendMsg(String str) {
    connection.sendMsg(str);
  }
}
