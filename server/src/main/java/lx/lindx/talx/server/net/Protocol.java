package lx.lindx.talx.server.net;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import lx.lindx.talx.server.error.ClientSocketExceprion;
import lx.lindx.talx.server.security.Crypt;
import lx.lindx.talx.server.utils.Log;

public class Protocol implements IMsgProtocol {

  private byte[] buffer;
  private boolean encrypted;

  private Connection connection;
  private Socket client;

  private Crypt crypt;

  public Protocol(Connection connection) {
    this.buffer = new byte[1024]; // default

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

 

  // not secure
  private void sendBytes(final byte[] bytes) throws ClientSocketExceprion {

    try {
      connection.out.write(bytes);
      connection.out.flush();
    } catch (IOException e) {
      throw new ClientSocketExceprion(
          "Can't write, because connection with" + Log.getAddress(client) + "has already closed it");
    }

    System.out.println("send: " + bytes.length);
  }





  @Override
  public void killIsNotEncrypted() {
    if (encrypted) {
      Log.log("Key exchange was successful");
    } else {
      Log.log("Сonnection is not secure");
      connection.kill();
    }
  }

  private void clearBuffer(final int length) {
    buffer = new byte[length];
  }




}
