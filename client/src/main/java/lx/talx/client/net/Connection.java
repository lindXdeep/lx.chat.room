package lx.talx.client.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lx.talx.client.error.*;
import lx.talx.client.utils.*;

public class Connection extends Thread {

  private boolean connected;

  private byte[] buffer;
  private int defBufSeze = 13107200; // 100 MegaBit // 12,5 MB // default

  private DataInputStream in;
  private DataOutputStream out;
  private Socket socket;

  private ServerAddress address;

  public Connection(ServerAddress address) {

    this.address = address;
    this.buffer = new byte[defBufSeze];
  }

  public boolean connect() {
    return connect(address.getPort());
  }

  public boolean connect(int port) {

    Log.log("Trying to connect to " + address + "\n");

    int i = 5;

    while (i-- > 0 & socket == null) {

      try {
        this.socket = new Socket(address.getHost(), port);

        connected = true;

        if (connected) {
          Log.log("Connection with" + address + "established!");
          this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
          this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }

      } catch (IOException e1) {

        Log.progress(String.format(" %s", i > 0 ? "." : ".\n\n"));

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e2) {
          e2.printStackTrace();
        }
      }
    }

    return connected;
  }

  public byte[] getBuffer() {
    return buffer;
  }

  public byte[] read() throws CantReadBytesExeption {

    allocateBuffer();

    try {
      in.read(buffer);
    } catch (IOException e) {
      throw new CantReadBytesExeption();
    }
    return buffer;
  }

  public void send(byte[] bytes) {
    try {
      out.write(bytes);
      out.flush();
    } catch (IOException e) {
      throw new CantWriteBytesExeption();
    }
  }

  private void allocateBuffer() {
    allocateBuffer(defBufSeze);
  }

  public void allocateBuffer(final int size) {
    this.buffer = new byte[size];
  }

  public boolean getStatus() {
    return this.connected;
  }

  public boolean kill() {

    if (socket != null) {
      try {
        Thread.sleep(1000);
        socket.close();
        connected = false;
        socket = null;
        return true;
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
}
