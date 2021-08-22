package lx.lindx.talx.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import lx.lindx.talx.server.error.ClientSocketExceprion;

public class Connectrion extends Thread {

  private Socket client;
  private Server server;
  private byte[] buffer;

  private BufferedOutputStream out;
  private InputStream in;

  public Connectrion(Socket client, Server server) {

    this.buffer = new byte[32];

    this.client = client;
    this.server = server;

    try {
      this.out = new BufferedOutputStream(client.getOutputStream());
      this.in = client.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Util.log("Create I/O connection with " + client.toString());
  }

  @Override
  public void run() {

    menu();
    System.out.println("-----------end menu-----------");
  }

  private void menu() {
    try {

      sendMsg(Util.getLogo());
      sendMsg(Util.getInstruction());

      while (true) {

        sendCursor(10); // data will be in buffer

        System.out.println(new String(buffer, 0, buffer.length));
      }

    } catch (ClientSocketExceprion e) {
      Util.log(e.getMessage());
    }
  }

  private void sendCursor(int bufferSize) throws ClientSocketExceprion {
    sendMsg("\n\n > ");
    buffer = new byte[bufferSize];
    try {
      in.read(buffer);
    } catch (IOException e) {
      throw new ClientSocketExceprion(
          "Client" + Util.getAddress(client) + "-/-> Server:[" + server.getPort() + "] ::: Connection reset");
    }
  }

  private void sendMsg(String msg) throws ClientSocketExceprion {
    try {
      out.write(msg.getBytes());
      out.flush();
    }catch(IOException e){
      throw new ClientSocketExceprion(
          "Can't write, because connection with" + Util.getAddress(client) + "has already closed it");
    }
  }
}
