package lx.lindx.talx.client;

import java.io.IOException;
import java.util.Arrays;

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
    System.out.println(msg + e.getMessage() );
  }

  public static void error(IOException e) {
    if (e.getMessage().equals("Connection refused")) {
      System.out.println("Error: ".concat(e.getMessage()).concat(" because, Server is not available.\n") );
    }
  }
}
