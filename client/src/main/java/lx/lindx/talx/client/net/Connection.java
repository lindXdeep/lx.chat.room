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

    crypt = new Crypt(this);

    socket = new Socket(addr.getHost(), addr.getPort());
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
  }

  @Override
  public void run() {

    crypt.encryptConnection();

    while (true) {

      try {
        int i = 0;
        int b = 0;
   
        while ((b = in.read(buffer)) != -1) {
          i++;
        }

        System.out.println(i);

        i = 0;

        // InetService.receive(new String(buffer, 0, buffer.length));

      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  private void clearBuffer() {
    buffer = new byte[32];
  }

  private void clearBuffer(int size) {
    buffer = new byte[size];
  }

  public void readNBytes(final int length) {

    clearBuffer(length);

    try {
      in.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendBytes(final byte[] bytes) {
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

  public byte[] getBuffer() {
    return buffer;
  }
}
