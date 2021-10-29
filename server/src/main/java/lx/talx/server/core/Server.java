package lx.talx.server.core;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lx.talx.server.security.AuthProcessor;
import lx.talx.server.utils.Log;
import lx.talx.server.utils.Util;

/**
 * Server
 */
public class Server extends Thread {

  private int PORT;
  private Socket socket;

  private AuthProcessor authProcessor;
  private ConnectionPool connectionPool;

  public Server(int port, Properties properties) {
    this.PORT = port;
    this.authProcessor = new AuthProcessor(properties, this);
    this.connectionPool = new ConnectionPool(authProcessor);
  }

  @Override
  public void run() {

    while (true) {
      try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        Log.info("Server is started on: -> " + PORT);
        while (true) {
          Log.info("Waiting connections...");
          socket = serverSocket.accept();
          Log.info("Client" + Util.getAddress(socket) + "connected!");

          ExecutorService executorService = Executors.newFixedThreadPool(100);
          executorService.submit(new Connection(socket, this));
          executorService.shutdown();
        }
      } catch (BindException e) {
        this.PORT = Util.getFreePort();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public String getSocketAddr() {
    return socket.getInetAddress().toString();
  }

  public AuthProcessor getAuthProcessor() {
    return authProcessor;
  }

  public Socket getSocket() {
    return socket;
  }

  public ConnectionPool getConnectionPool() {
    return connectionPool;
  }
}