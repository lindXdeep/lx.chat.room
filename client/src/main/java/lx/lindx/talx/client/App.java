package lx.lindx.talx.client;

/**
 * App
 */
public class App {

  private static String PORT = "8181";
  private static String address = "127.0.0.1";
  private static Client client;
  private static boolean acceptConnect;

  public static void main(String... param) {

    if (param.length == 0) {

      client = new Client(address, PORT);
      acceptConnect = true;

    } else if (param.length == 1 && param[0].equals("--help") || param[0].equals("-h")) {

      Util.printHelp();

    } else if (param.length == 1 && param[0].equals("--about")) {

      Util.printLogo();

    } else if (param.length == 3 && (param[0].equals("--connect") || param[0].equals("-c"))) {

      client = new Client(param[1], param[2]);
      acceptConnect = true;

    } else {
      Util.printError(param);
    }

    if (acceptConnect) {
      client.start();
    }
  }
}