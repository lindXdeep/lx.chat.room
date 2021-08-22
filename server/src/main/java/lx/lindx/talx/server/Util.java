package lx.lindx.talx.server;

import java.net.Socket;
import java.util.Arrays;

public class Util {

  private static boolean chprnt;

  private static StringBuilder sb;

  public static void log(String string) {
    if (!chprnt) {
      System.out.println("\n-> start.\n\t│\n\t├── " + string + "\n\t│");
    } else {
      System.out.println("\t├── " + string + "\n\t│");
    }
    chprnt = true;
  }

  public static String getAddress(Socket socket) {

    sb = new StringBuilder();
    sb.append(" [IP:");
    sb.append(socket.getLocalAddress());
    sb.append("] --> [PORT:");
    sb.append(socket.getPort());
    sb.append("] ");

    sb.deleteCharAt(5);

    return sb.toString();
  }

  public static String getLogo() {

    return Arrays.toString(new String[] {

        "\n\t            .                 ,         \n",

        "\t   .    , _ | _. _ ._ _  _   -+- _       \n",

        "\t    \\/\\/ (/,|(_.(_)[ | )(/,   | (_)    \n",

        "\t                                         \n",

        "\t _______ _______        _     _ _     _  \n",

        "\t    |    |_____| |      |____/   \\___/  \n",

        "\t    |    |     | |_____ |    \\_ _/   \\_",

        " > messenger < \n\n" });
  }

  public static String getInstruction() {
    return "\n\n Use command \"/help\" for more information about a command.\n";
  }
}