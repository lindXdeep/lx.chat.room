package lx.lindx.talx.client;

import java.util.Scanner;

import lx.lindx.talx.client.net.Client;
import lx.lindx.talx.client.net.IMessageProcessor;
import lx.lindx.talx.client.net.UserAddress;

/**
 * App
 */
public class App {

  private static Client client;
  private static Scanner sc = new Scanner(System.in);

  public static void main(String... param) {

    if (runMenu(param)) {

      client.connect(new IMessageProcessor() {
        @Override
        public void processMessage(String message) {
          parseMessage(message);
        }
      });

      String str = new String();

      while (true) {

        while (sc.hasNext()) {
          str = sc.nextLine();
          
          client.sendMsg(str);
        }
      }
    }
  }

  private static void parseMessage(String message) {
    System.out.println(message);
  }

  /**
   * 
   * 
   * 
   * Run menu
   * 
   * @param param
   * @return
   */
  public static boolean runMenu(String... param) {

    if (param.length == 0) {

      client = new Client();
      return true;

    } else if (param.length == 1 && param[0].equals("--help") || param[0].equals("-h")) {

      Util.printHelp();

    } else if (param.length == 1 && param[0].equals("--about")) {

      Util.printLogo();

    } else if (param.length == 3 && (param[0].equals("--connect") || param[0].equals("-c"))) {

      client = new Client(new UserAddress(param[1], Integer.valueOf(param[2])));
      return true;

    } else {
      Util.printError(param);
    }
    return false;
  }
}