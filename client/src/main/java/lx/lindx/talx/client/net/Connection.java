package lx.lindx.talx.client.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import lx.lindx.talx.client.model.UserAddress;

public class Connection extends Thread {

  private byte[] buffer;
  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;

  public Connection(UserAddress addr, InetService client) throws IOException {
    socket = new Socket(addr.getHost(), addr.getPort());
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());

    buffer = new byte[1048576];
  }

  @Override
  public void run() {

    while (true) {
      try {

        in.read(buffer);
        InetService.receive(new String(buffer, 0, buffer.length));

      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public void sendMsg(String str) {

    try {
      out.write(str.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
