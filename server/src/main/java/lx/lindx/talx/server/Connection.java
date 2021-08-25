package lx.lindx.talx.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import lx.lindx.talx.server.error.ClientSocketExceprion;

public class Connection extends Thread {

  private IMsgProtocol protocol;

  private Socket client;
  private Server server;
  private byte[] buffer;

  BufferedOutputStream out;
  InputStream in;

  // private boolean encrypted;

  public Connection(Socket client, Server server) {

    this.client = client;
    this.server = server;

    // this.crypt = new Crypt();
    this.protocol = new Protocol(this);

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

    try {

      protocol.executeKeyExchange();

    } catch (ClientSocketExceprion e) {
      Util.log(e.getMessage());
    }

    menu();

    System.out.println("-----------end menu-----------");
  }

  private void menu() {
    try {

      sendMsg(Util.getLogo());
      sendMsg(Util.getInstruction());

      while (true) {

        sendCursor(); // data will be in buffer

        switch (new String(buffer, 0, buffer.length).trim()) {
          case "/help":
            sendMsg(Util.getHelp());
            break;
          case "/about":
            sendMsg(Util.getLogo().substring(140, Util.getLogo().length() - 1));
            break;
          case "/new":
            createAccount();
            break;
          case "/auth":
            authorize();
            break;
          case "/end":
            // TODO: kill connection
            break;
          default:
            sendMsg(Util.getInstruction());
        }

        Util.logCommand(this);

      }

    } catch (ClientSocketExceprion e) {
      Util.log(e.getMessage());
    }
  }

  private void createAccount() {

    for (String b : askCredentials()) {
      System.out.print(b + " ");
    }

  }

  private void authorize() {

  }

  private String[] askCredentials() {

    String mail = null;
    String pass = null;
    String authcode = null;
    String login = null;

    mail = askConcreteCredentialType("Email", 32);
    pass = askConcreteCredentialType("Password", 64);
    authcode = askConcreteCredentialType("Authcode", 6);
    login = askConcreteCredentialType("Login", 16);

    return new String[] { mail, pass, authcode, login };
  }

  private String askConcreteCredentialType(String credentialType, int bufferSize) {

    try {
      clearBuffer(bufferSize);
      sendMsg(credentialType + ": ");
      in.read(buffer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new String(buffer, 0, buffer.length).trim();
  }

  // not secure
  // public void readNBytes(final int length) {

  // clearBuffer(length);

  // try {
  // in.read(buffer);
  // } catch (IOException e) {
  // e.printStackTrace();
  // }
  // }

  // not secure
  // public void sendBytes(final byte[] bytes) {

  // try {
  // out.write(bytes);
  // out.flush();
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }

  /**
   * Read message from cliet
   * 
   * @throws ClientSocketExceprion
   */
  private void sendCursor() throws ClientSocketExceprion {
    clearBuffer();
    sendMsg("\n > ");
    try {
      in.read(buffer);
    } catch (IOException e) {
      throw new ClientSocketExceprion(
          "Client" + Util.getAddress(client) + "-/-> Server:[" + server.getPort() + "] ::: Connection reset");
    }
  }

  /**
   * Send message to client
   * 
   * @param msg
   * @throws ClientSocketExceprion
   */
  public void sendMsg(String msg) throws ClientSocketExceprion {
    protocol.sendMsg(msg);
  }





  

  private void clearBuffer() {
    buffer = new byte[32];
  }

  private void clearBuffer(int size) {
    buffer = new byte[size];
  }

  public Socket getClient() {
    return this.client;
  }

  public byte[] getBuffer() {
    return this.buffer;
  }

  public void kill() {
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public InputStream getStdIn() {
    return this.in;
  }

  public BufferedOutputStream getStdOut() {
    return this.out;
  }
}
