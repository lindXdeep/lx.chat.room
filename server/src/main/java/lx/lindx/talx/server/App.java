package lx.lindx.talx.server;

public class App {

  private static final int PORT = 8181;
  
  public static void main(String[] args) {

    new Server(PORT).start();
    
  }
}
