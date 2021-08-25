package lx.lindx.talx.client.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import lx.lindx.talx.client.Util;
import lx.lindx.talx.client.error.ClientSocketExceprion;
import lx.lindx.talx.client.security.Crypt;

public class Protocol implements IMsgProtocol {

  private Connection connection;
  private Crypt crypt;
  private byte[] buffer;

  public Protocol(Connection connection) {
    this.connection = connection;
    crypt = new Crypt();
  }

  @Override
  public void sendMsg(String msg) throws ClientSocketExceprion {
    // TODO Auto-generated method stub

  }

  @Override
  public void send(byte[] bytes) throws ClientSocketExceprion {
    // TODO Auto-generated method stub
  }

  @Override
  public byte[] read() {

    readNBytes(8192);

    int msgLength = byteToInt(Arrays.copyOfRange(buffer, 0, 4)); // 0 - 3
    byte[] encodeSpec = Arrays.copyOfRange(buffer, 4, 22); // 4 - 22
    byte[] cipherMsg = Arrays.copyOfRange(buffer, 22, msgLength + 22); // 22 + msg.length + shift(22)

    return crypt.decrypt(encodeSpec, cipherMsg);
  }

  @Override
  public void executeKeyExchange() throws ClientSocketExceprion {

    Util.toConsole("Sending public key to server");
    sendBytes(crypt.getPubKeyEncoded());

    readNBytes(557);
    crypt.setServerPubKey(buffer);
    Util.toConsole("Public key from server received");

    // TODO: delelte
    readNBytes(256);
    System.out.println(Arrays.equals(crypt.getSharedKeySecret(), connection.getBuffer()));
  }

  // not secure
  private void readNBytes(final int length) {

    clearBuffer(length);

    try {
      connection.in.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // not secure
  private void sendBytes(final byte[] bytes) {
    try {
      connection.out.write(bytes);
      connection.out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private int byteToInt(byte[] arr) {

    return (arr != null || arr.length == 4) ?

        (int) ((0xFF & arr[0]) << 24 |

            (0xFF & arr[1]) << 16 |

            (0xFF & arr[2]) << 8 |

            (0xFF & arr[3]) << 0

        ) : 0x0;
  }

  private void clearBuffer(final int length) {
    buffer = new byte[length];
  }

  @Override
  public void killIsNotEncrypted() {
    // TODO Auto-generated method stub
  }

}
