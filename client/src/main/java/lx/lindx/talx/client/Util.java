package lx.lindx.talx.client;

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

  private static void toConsole(String[] arr) {

    for (String s : arr) {
      System.out.print(s + " ");
    }
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

}
