package lx.talx.client.utils;

import java.net.Socket;

public class Util {

  private static StringBuilder sb;

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

  public static void printCursor() {
    System.out.print("::> ");
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

  public static String byteToStr(byte[] b) {
    return new String(b, 0, b.length);
  }

  public static byte[] strToByte(String str) {
    return str.getBytes();
  }
}
