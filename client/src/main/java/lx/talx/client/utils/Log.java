package lx.talx.client.utils;

import java.io.IOException;

public class Log {

  private static boolean r = false;

  public static void printHelp() {

    String[] help = {

        "\nUsage: talx [OPTION] [HOST] [PORT]\n",

        "   ex: talx --connect 127.0.0.1 8181           default.\n",

        "\n\nAvailable Options:\n",

        "  -c --connect           Connect to server.\n",

        "\n" };

    info(help);
  }

  public static void printLogo() {

    String[] logo = new String[] {

        "\n\t _______ _______        _     _ _     _  \n",

        "\t    |    |_____| |      |____/   \\___/  \n",

        "\t    |    |     | |_____ |    \\_ _/   \\_",

        " > messenger < \n\n" };

    info(logo);
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

  public static void error(IOException e) {
    if (e.getMessage().equals("Connection refused")) {
      System.out.println("Error: ".concat(e.getMessage()).concat(" because, Server is not available.\n"));
    }
  }

  public static void error(String message) {
    System.out.println("\nError: ".concat(message).concat("\n"));
  }

  public static void info(String msg) {
    System.out.println("Info: ".concat(msg));
  }

  public static void info(String msg, IOException e) {
    System.out.println(msg + e.getMessage());
  }

  public static void info(String[] arr) {
    for (String s : arr) {
      System.out.print(s + " ");
    }
  }

  public static void log(String message) {
    info(message);
  }

  public static void progress(String str) {
    System.out.printf(str);
  }
}
