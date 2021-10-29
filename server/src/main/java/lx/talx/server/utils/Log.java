package lx.talx.server.utils;

import java.net.Socket;

import static lx.talx.server.utils.Util.getLogger;
public class Log {

  private static boolean chprnt;

  public static void info(String string) {
    if (!chprnt) {
      System.out.println("\n-> start.\n\t │\n\t ├── " + string + "\n\t  │");
    } else {
      getLogger().info(string);
    }
    chprnt = true;
  }

  public static void infoInvalidPublicKey(Socket client) {
    Log.info("Connection from:" + Util.getAddress(client) + "rejected because public key is invalid");
  }

  public static void readSec(byte[] buf) {
    info("Read Sec-Key: " + Util.byteToStr(buf));
  }

  public static void authorize(byte[] buf) {
    info("Read auth-buf: " + Util.byteToStr(buf));
  }

  public static void readCrypt(int l) {
    info("Read crypt-protocol: " + l + " byte");
  }

  public static void error(String error) {
    getLogger().error(error);
  }

  public static void status(String user, String status) {
    info("Status: " + user + " - " + status);
  }

  public static void log(String user, String command) {
    info("Log: @".concat(user).concat(" ") + command);
  }
}
