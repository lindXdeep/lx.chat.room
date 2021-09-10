package lx.talx.server.security;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import lx.talx.server.core.Connection;
import lx.talx.server.utils.Log;
import lx.talx.server.utils.Util;

public class CryptProtocol {

  private byte[] buf;

  private Crypt crypt;

  private Connection connection;

  private boolean encrypted;

  public CryptProtocol(Connection connection) {
    this.connection = connection;
    this.crypt = new Crypt();
  }

  public void executeKeyExchange() {

    Log.info("Waiting public key from client: ".concat(Util.getAddress(connection.getClient())));

    try {

      byte[] buf = readUnencrypted(connection.read());
      crypt.setClientPubKey(buf);

    } catch (GeneralSecurityException e) {
      Log.infoInvalidPublicKey(connection.getClient());
      sendUnencrypted("Access denied: public key is invalid.".concat(Util.getIp(connection.getClient())).getBytes());
      connection.kill();
    }

    if (connection.getClient().isClosed())
      return;

    Log.info("Received client public key from" + Util.getAddress(connection.getClient()));

    sendUnencrypted(crypt.getPubKeyEncoded());
    Log.info("Sent server public key to client:" + Util.getAddress(connection.getClient()));

    encrypted = true;
  }

  private void sendUnencrypted(byte[] bytes) {

    ByteBuffer buf = null;

    buf = ByteBuffer.allocate(4 + bytes.length); // 4 + all...
    buf.put(Util.intToByte(bytes.length)); // 4 // length
    buf.put(bytes); // 4 + all

    connection.send(buf.array());
  }

  private byte[] readUnencrypted(byte[] msg) {
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

  public byte[] readEncrypted() throws RuntimeException {

    buf = connection.read();

    int msgLength = Util.byteToInt(Arrays.copyOfRange(buf, 0, 4)); // 0 - 3
    byte[] encodeSpec = Arrays.copyOfRange(buf, 4, 22); // 4 - 22
    byte[] cipherMsg = Arrays.copyOfRange(buf, 22, msgLength + 22); // 22 + msg.length + shift(22)

    Log.readCrypt(4 + encodeSpec.length + msgLength);

    return crypt.decrypt(encodeSpec, cipherMsg);
  }
}