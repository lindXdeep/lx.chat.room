package lx.lindx.talx.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import lx.lindx.talx.server.error.ClientSocketExceprion;
import lx.lindx.talx.server.security.Crypt;

public class Protocol implements IMsgProtocol {

  private byte[] buffer;
  private boolean encrypted;

  private Connection connection;
  private Socket client;

  private Crypt crypt;

  public Protocol(Connection connection) {
    this.connection = connection;
    this.client = connection.getClient();
    this.buffer = new byte[1024]; // default

    this.crypt = new Crypt();
  }

  @Override
  public void executeKeyExchange() throws ClientSocketExceprion {

    Util.log("Waiting public key from client: " + Util.getAddress(client));
    readNBytes(557);

    try {

      crypt.setClientPubKey(buffer);

    } catch (GeneralSecurityException e) {
      Util.log("Connection from:" + Util.getAddress(client) + "rejected because public key is invalid");
      sendBytes("Access denied: public key is invalid.".concat(Util.getIp(client)).getBytes());
      connection.kill();
    }

    if (client.isClosed())
      return;

    Util.log("Received client public key from" + Util.getAddress(client));

    sendBytes(crypt.getPubKeyEncoded());
    Util.log("Sent server public key to client:" + Util.getAddress(client));

    encrypted = true;
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

  // wrapper for msg
  @Override
  public void sendMsg(String msg) throws ClientSocketExceprion {

    send(msg.getBytes());
  }

  // encrypted
  @Override
  public void send(byte[] bytes) throws ClientSocketExceprion {

    ByteBuffer buf = null;

    byte[] encodeParamAndCipherMsg = crypt.encrypt(bytes); // 18 + all....

    buf = ByteBuffer.allocate(4 + encodeParamAndCipherMsg.length); // 4 + 18 + all...
    buf.put(intToByte(encodeParamAndCipherMsg.length - 18)); // 4 // length
    buf.put(encodeParamAndCipherMsg); // 18 + all // param and cipher

    sendBytes(buf.array());
  }

  // not secure
  private void sendBytes(final byte[] bytes) throws ClientSocketExceprion {

    try {
      connection.out.write(bytes);
      connection.out.flush();
    } catch (IOException e) {
      throw new ClientSocketExceprion(
          "Can't write, because connection with" + Util.getAddress(client) + "has already closed it");
    }

    System.out.println("send: " + bytes.length);
  }

  @Override
  public byte[] read() {

    readNBytes(8192);

    int msgLength = byteToInt(Arrays.copyOfRange(buffer, 0, 4)); // 0 - 3
    byte[] encodeSpec = Arrays.copyOfRange(buffer, 4, 22); // 4 - 22
    byte[] cipherMsg = Arrays.copyOfRange(buffer, 22, msgLength + 22); // 22 + msg.length + shift(22)

    Util.log("recive: " + (4 + encodeSpec.length + msgLength));

    return crypt.decrypt(encodeSpec, cipherMsg);
  }



  @Override
  public void killIsNotEncrypted() {
    if (encrypted) {
      Util.log("Key exchange was successful");
    } else {
      Util.log("Сonnection is not secure");
      connection.kill();
    }
  }

  private void clearBuffer(final int length) {
    buffer = new byte[length];
  }

  private byte[] intToByte(final int i) {
    return new byte[] {

        (byte) ((i >> 24) & 0xFF),

        (byte) ((i >> 16) & 0xFF),

        (byte) ((i >> 8) & 0xFF),

        (byte) ((i >> 0) & 0xFF) };
  }

  private int byteToInt(byte[] arr) {
    return (arr != null || arr.length == 4) ?

        (int) ((0xFF & arr[0]) << 24 |

            (0xFF & arr[1]) << 16 |

            (0xFF & arr[2]) << 8 |

            (0xFF & arr[3]) << 0

        ) : 0x0;
  }
}
