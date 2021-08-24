package lx.lindx.talx.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import lx.lindx.talx.server.error.ClientSocketExceprion;
import lx.lindx.talx.server.security.Crypt;

public class Connectrion extends Thread {

  private Crypt crypt;

  private Socket client;
  private Server server;
  private byte[] buffer;

  private BufferedOutputStream out;
  private InputStream in;
  private BufferedReader inStr;

  public Connectrion(Socket client, Server server) {

    this.crypt = new Crypt(this);

    this.client = client;
    this.server = server;

    try {
      this.out = new BufferedOutputStream(client.getOutputStream());
      this.in = client.getInputStream();
      this.inStr = new BufferedReader(new InputStreamReader(client.getInputStream()));

    } catch (IOException e) {
      e.printStackTrace();
    }
    Util.log("Create I/O connection with " + client.toString());
  }

  @Override
  public void run() {

    crypt.encryptConnection();

    System.out.println("-----------end menu-----------");
    // menu();
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

    try {
      out.write(msg.getBytes());
      out.flush();
    } catch (IOException e) {
      throw new ClientSocketExceprion(
          "Can't write, because connection with" + Util.getAddress(client) + "has already closed it");
    }
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
}
