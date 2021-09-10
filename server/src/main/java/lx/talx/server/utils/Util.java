package lx.talx.server.utils;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Util {

  private static final String ver = "ver-0.1";
  private static StringBuilder sb;
  private static MessageDigest sha1;

  static {
    try {
      sha1 = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
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

  public static int getFreePort() {

    int min = 49152;
    int max = 65535;

    return (int) (Math.random() * (max - min) + min);
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

  public static String getInstruction() {
    return "\n\n Use command \"/help\" for more information about a command.\n";
  }

  public static String getVer() {
    return ver;
  }

  public static String cursor(Socket socket) {
    return "\n[".concat(socket.getInetAddress().toString().substring(1)).concat("]> ");
  }

  public static String toHash(String str) {
    
    StringBuilder sbhash = new StringBuilder();

    for (byte b : sha1.digest(str.getBytes()))
      sbhash.append(String.format("%02X", b));
    
    return sbhash.toString();
  }

  public static JSONObject parseCredential(byte[] buffer, int index) {

    try {
      return (JSONObject) new JSONParser().parse(new String(buffer, index, buffer.length - index));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static int byteToInt(byte[] bytes) {
    return (bytes != null || bytes.length == 4) ?

      (int) ((0xFF & bytes[0]) << 24 |

          (0xFF & bytes[1]) << 16 |

          (0xFF & bytes[2]) << 8 |

          (0xFF & bytes[3]) << 0

      ) : 0x0;
  }

  public static byte[] intToByte(int i) {
    return new byte[] {

      (byte) ((i >> 24) & 0xFF),

      (byte) ((i >> 16) & 0xFF),

      (byte) ((i >> 8) & 0xFF),

      (byte) ((i >> 0) & 0xFF) };
  }

  public static byte[] strToByte(String str) {
    return str.getBytes();
  }

  public static String byteToStr(byte[] msg) {
    return new String(msg, 0, msg.length);
  }
}
