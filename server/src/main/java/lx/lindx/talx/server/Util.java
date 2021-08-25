package lx.lindx.talx.server;

import java.net.Socket;
import java.util.Arrays;

public class Util {

  private static final String ver = "ver-0.1";

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

    sb.append(getIp(socket));
    sb.append("--> [PORT:");
    sb.append(socket.getPort());
    sb.append("] ");

    return sb.toString();
  }

  public static String getIp(Socket socket) {

    sb = new StringBuilder();
    sb.append(" [IP:");
    sb.append(socket.getLocalAddress());
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

        " > messenger < \n\n", "\t\t\t".concat(Util.getVer()).concat("\n\n") });
  }

  private static String getVer() {
    return ver;
  }

  public static String getInstruction() {
    return "\n\n Use command \"/help\" for more information about a command.\n";
  }

  public static String getHelp() {

    return "\n"

        .concat("/about         show info about messenger.\n")

        .concat("/auth          sign in.\n")

        .concat("/new           Create account.\n")

        .concat("/end           Exit.\n");

  }

  public static void logCommand(Connection connection) {

    byte[] buffer = connection.getBuffer();
    Socket client = connection.getClient();

    String command = new String(buffer, 0, buffer.length).trim();
    String lengthCommand = String.valueOf(command.length());
    String addr = client.getInetAddress().toString();
    String port = String.valueOf(client.getPort());

    Util.log(
        addr.concat(":" + port).concat(" / msg size[").concat(lengthCommand).concat("] / command: ").concat(command));
  }
}
