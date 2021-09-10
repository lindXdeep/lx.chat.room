package lx.talx.client.net;

import java.nio.ByteBuffer;
import java.util.Arrays;

import lx.talx.client.error.ClientSocketExceprion;
import lx.talx.client.security.Crypt;
import lx.talx.client.utils.Log;
import lx.talx.client.utils.Util;

public class Protocol {

  private Connection connection;
  private Crypt crypt;
  private byte[] buf;

  public Protocol(Connection connection) {
    this.connection = connection;
    this.crypt = new Crypt();
  }

  public void executeKeyExchange() throws ClientSocketExceprion {

    Log.log("Generate public key");
    crypt.generatePublicKey();

    Log.log("Sending public key to server");
    sendUncrypt(crypt.getPubKeyEncoded());

    crypt.setServerPubKey(readUncrypt(connection.read()));
    Log.log("Public key from server received");
  }

  private void sendUncrypt(byte[] bytes) {

    ByteBuffer buf = null;

    buf = ByteBuffer.allocate(4 + bytes.length); // 4 + all...
    buf.put(Util.intToByte(bytes.length)); // 4 // length
    buf.put(bytes); // 4 + all

    connection.send(buf.array());
  }

  /**
   * return result message.
   * 
   * [length]:[byte msg]...
   * 
   * @param msg
   * @return
   */
  private byte[] readUncrypt(byte[] msg) {
    int msgLength = Util.byteToInt(Arrays.copyOfRange(msg, 0, 4));
    return Arrays.copyOfRange(msg, 4, msgLength + 4);
  }

  public void sendEncrypted(final byte[] bytes) {

    ByteBuffer buf = null;

    byte[] encodeParamAndCipherMsg = crypt.encrypt(bytes); // 18 + all....

    buf = ByteBuffer.allocate(4 + encodeParamAndCipherMsg.length); // 4 + 18 + all...
    buf.put(Util.intToByte(encodeParamAndCipherMsg.length - 18)); // 4 // length
    buf.put(encodeParamAndCipherMsg); // 18 + all // param and cipher

    connection.send(buf.array());
  }

  public byte[] readEncrypted() {

    buf = connection.read();

    int msgLength = Util.byteToInt(Arrays.copyOfRange(buf, 0, 4)); // 0 - 3
    byte[] encodeSpec = Arrays.copyOfRange(buf, 4, 22); // 4 - 22
    byte[] cipherMsg = Arrays.copyOfRange(buf, 22, msgLength + 22); // 22 + msg.length + shift(22)

    return crypt.decrypt(encodeSpec, cipherMsg);
  }

}


