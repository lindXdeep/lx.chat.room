package lx.talx.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import lx.talx.server.error.CantGetConnection;
import lx.talx.server.utils.Log;

public class DataBaseService {

  private static String url;
  private static Properties prop;
  public static boolean connect;

  public static Connection getConnection() throws SQLException {
    
    if (connect) {
      return DriverManager.getConnection(url);
    }

    throw new CantGetConnection();
  }

  public static void loadProperties() {

    try (InputStream in = DataBaseService.class.getClassLoader().getResourceAsStream("db.properties")) {

      prop = new Properties();

      if (in == null) {
        Log.error("Sorry, unable to find db.properties");
        connect = false;
        return;
      }

      prop.load(in);
      url = prop.getProperty("sqlitedb.url");
      Log.info("database is loaded: " + url);
      connect = true;
    } catch (IOException e) {
      Log.error("Can't read database paremeters from db.properties");
      connect = false;
    }
  }
}
