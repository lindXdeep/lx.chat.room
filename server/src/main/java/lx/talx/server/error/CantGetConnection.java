package lx.talx.server.error;

public class CantGetConnection extends RuntimeException{
  
  public CantGetConnection() {
    this("Error: Can't get database connection");
  }

  public CantGetConnection(String str) {
    super(str);
  }
}
