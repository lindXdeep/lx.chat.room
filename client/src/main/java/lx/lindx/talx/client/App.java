package lx.lindx.talx.client;

import java.util.Scanner;

import lx.lindx.talx.client.net.InetService;
import lx.lindx.talx.client.net.IMessageProcessor;

/**
 * App
 */
public class App {

  private static InetService client;
  private static Scanner sc = new Scanner(System.in);

  private static IMessageProcessor msgProcessor = new IMessageProcessor() {
    @Override
    public void processMessage(String message) {
      parseMessage(message);
    }
  };

  public static void main(String... param) {

    try {

      client = Util.getInetService(client, param).connect();
      client.setMsgProcessor(msgProcessor);

    } catch (RuntimeException e) {
      Util.toConsole(e.getMessage());
    }

    while (true) {

      while (sc.hasNext()) {
        client.send(sc.nextLine());
   
      }

    }

  }

  private static void parseMessage(String message) {
    System.out.print(message);
  }

}