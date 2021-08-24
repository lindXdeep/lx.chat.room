package lx.lindx.talx.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import javax.crypto.spec.SecretKeySpec;

import lx.lindx.talx.client.Util;
import lx.lindx.talx.client.model.UserAddress;
import lx.lindx.talx.client.security.Crypt;

public class Connection extends Thread {

  private Crypt crypt;

  private byte[] buffer;
  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;

  public Connection(UserAddress addr, InetService client) throws IOException {

    crypt = new Crypt();

    socket = new Socket(addr.getHost(), addr.getPort());
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
  }

  @Override
  public void run() {

    Util.toConsole("Sending public key to server");
    sendBytes(crypt.getPublicKeyEncoded());

    readNBytes(557);
    crypt.setServerPubKey(buffer);
    Util.toConsole("Public key from server received");

    readNBytes(16);
    System.out.println(Arrays.equals(crypt.getKeyAES().getEncoded(), buffer));

    System.out.println("--------------------------end--------------------");
    return;

   /*  while (true) {

      buffer = new byte[1048576];

      try {

        in.read(buffer);

        InetService.receive(new String(buffer, 0, buffer.length));

      } catch (IOException e) {
        e.printStackTrace();
      }
    } */

  }

  private void clearBuffer() {
    buffer = new byte[32];
  }

  private void clearBuffer(int size) {
    buffer = new byte[size];
  }

  private void readNBytes(final int length) {

    clearBuffer(length);

    try {
      in.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void sendBytes(final byte[] bytes) {
    try {
      out.write(bytes);
      out.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendMsg(final String str) {

    try {
      out.write(str.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
