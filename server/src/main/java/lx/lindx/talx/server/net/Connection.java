package lx.lindx.talx.server.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import lx.lindx.talx.server.Server;
import lx.lindx.talx.server.error.ClientSocketExceprion;
import lx.lindx.talx.server.utils.Log;

public class Connection extends Thread {

  private IMsgProtocol protocol;

  private byte[] buffer;

  public Connection(Socket client, Server server) {

    this.protocol = new Protocol(this);
  }



  
 

  public void kill() {
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
