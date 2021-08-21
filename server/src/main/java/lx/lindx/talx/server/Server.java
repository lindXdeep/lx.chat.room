package lx.lindx.talx.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  private final int PORT;
  private Thread thread;

  public Server(int port) {
    PORT = port;
  }

  public void start() {
    thread = new Thread(() -> {
      try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        Util.log("Server is started!");
        while (true) {
          Util.log("Waiting connections...");
          Socket socket = serverSocket.accept();
          Util.log("Client" + Util.getAddress(socket) + "connected!");
          new Connectrion(socket, this).start();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    thread.start();
  }
}
