package lx.talx.server.error;

public class CantReadBytesExeption extends RuntimeException {

  public CantReadBytesExeption() {
    this("Error: Can't Read Bytes");
  }

  public CantReadBytesExeption(String str) {
    super(str);
  }
}
