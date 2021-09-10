package lx.lindx.talx.server.utils;

import java.net.Socket;
import java.util.Arrays;

import lx.lindx.talx.server.net.Connection;

public class Log {

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

    Log.log(
        addr.concat(":" + port).concat(" / msg size[").concat(lengthCommand).concat("] / command: ").concat(command));
  }
}
