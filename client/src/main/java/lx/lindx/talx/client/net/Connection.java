package lx.lindx.talx.client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import jdk.jshell.execution.Util;
import lx.lindx.talx.client.InetService;
import lx.lindx.talx.client.error.ClientSocketExceprion;
import lx.lindx.talx.client.model.UserAddress;

public class Connection extends Thread {

  private IMsgProtocol protocol;

  private byte[] buffer;
  private Socket socket;
  DataInputStream in;
  DataOutputStream out;

  private InetService client;

  public Connection(UserAddress addr, InetService client) throws IOException {

    this.client = client;
    this.protocol = new Protocol(this);

    socket = new Socket(addr.getHost(), addr.getPort());
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
  }

  @Override
  public void run() {

    try {

      protocol.executeKeyExchange();

    } catch (ClientSocketExceprion e1) {
      e1.printStackTrace();
    }

    while (true) {

      byte[] b = protocol.read();
      client.receive(new String(b, 0, b.length));

    }

  }

  // private void clearBuffer() {
  // buffer = new byte[32];
  // }

  // private void clearBuffer(int size) {
  // buffer = new byte[size];
  // }

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
      protocol.sendMsg(str);
    } catch (ClientSocketExceprion e) {
      e.printStackTrace();
    }

  }

  public byte[] getBuffer() {
    return buffer;
  }
}
