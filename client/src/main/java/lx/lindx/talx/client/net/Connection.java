package lx.lindx.talx.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lx.lindx.talx.client.InetService;
import lx.lindx.talx.client.error.ClientSocketExceprion;
import lx.lindx.talx.client.model.UserAddress;
import lx.lindx.talx.client.security.Crypt;

public class Connection extends Thread {

  // private Crypt crypt;
  private IMsgProtocol protocol;

  private byte[] buffer;
  private Socket socket;
  DataInputStream in;
  DataOutputStream out;

  public Connection(UserAddress addr, InetService client) throws IOException {

    // crypt = new Crypt(this);

    socket = new Socket(addr.getHost(), addr.getPort());
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());

    protocol = new Protocol(this);
  }

  @Override
  public void run() {

    try {

      protocol.executeKeyExchange();

    } catch (ClientSocketExceprion e1) {
      e1.printStackTrace();
    }

    clearBuffer(8192);

    while (true) {

      byte[] b = protocol.read();
      System.out.println(b.length);

      System.out.println(new String(b, 0, b.length));

      System.out.println("========== end ============");

      break;
    }

  }

  // private void clearBuffer() {
  // buffer = new byte[32];
  // }

  private void clearBuffer(int size) {
    buffer = new byte[size];
  }

  // public void readNBytes(final int length) {

  // clearBuffer(length);

  // try {
  // in.read(buffer);
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // }

  // public void sendBytes(final byte[] bytes) {
  // try {
  // out.write(bytes);
  // out.flush();
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }

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
