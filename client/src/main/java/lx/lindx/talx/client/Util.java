package lx.lindx.talx.client;

import java.io.IOException;
import java.util.Arrays;

import lx.lindx.talx.client.model.UserAddress;

public class Util {

  public static void printHelp() {

    String[] help = {

        "\nUsage: talx [OPTION] [HOST] [PORT]\n",

        "   ex: talx --connect 127.0.0.1 8181           default.\n",

        "\n\nAvailable Options:\n",

        "  -c --connect           Connect to server.\n",

        "\n" };

    toConsole(help);
  }

  public static void printLogo() {

    String[] logo = new String[] {

        "\n\t _______ _______        _     _ _     _  \n",

        "\t    |    |_____| |      |____/   \\___/  \n",

        "\t    |    |     | |_____ |    \\_ _/   \\_",

        " > messenger < \n\n" };

    toConsole(logo);
  }

  public static void printError(String[] param) {

    StringBuilder sb = new StringBuilder();
    sb.append("\nError: unknown option: ");

    for (String p : param) {
      sb.append(p);
      sb.append(" ");
    }

    sb.append("\n");

    System.out.println(sb.toString());

    printHelp();
  }

  private static void toConsole(String[] arr) {
    for (String s : arr) {
      System.out.print(s + " ");
    }
  }

  public static void toConsole(String msg) {
    System.out.println("\n".concat(msg).concat("\n"));
  }

  public static void toConsole(String msg, IOException e) {
    System.out.println(msg + e.getMessage());
  }

  public static void error(IOException e) {
    if (e.getMessage().equals("Connection refused")) {
      System.out.println("Error: ".concat(e.getMessage()).concat(" because, Server is not available.\n"));
    }
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
  public static InetService getInetService(InetService client, String[] param) {
    if (param.length == 0) {

      return new InetService();

    } else if (param.length == 1 && param[0].equals("--help") || param[0].equals("-h")) {

      Util.printHelp();

    } else if (param.length == 1 && param[0].equals("--about")) {

      Util.printLogo();

    } else if (param.length == 3 && (param[0].equals("--connect") || param[0].equals("-c"))) {

      return new InetService(new UserAddress(param[1], Integer.valueOf(param[2])));

    } else {
      Util.printError(param);
    }
    throw new RuntimeException("Client service has finished work");
  }
}
