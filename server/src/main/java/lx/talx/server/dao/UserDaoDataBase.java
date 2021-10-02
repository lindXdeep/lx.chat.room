package lx.talx.server.dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import lx.talx.server.model.User;
import lx.talx.server.service.DataBaseService;
import lx.talx.server.utils.Log;

public class UserDaoDataBase implements IUserDao {

  private Connection connection;
  private Statement statement;
  private Savepoint savepoint;

  public UserDaoDataBase() {
    try {
      connection = DataBaseService.getConnection();

      if (connection != null) {
        System.out.println("connection ---> db");
      }

    } catch (SQLException e) {
      Log.error(e.getMessage());
    }

    createUserTable();
    System.out.println("Table create");
  }

  private void createUserTable() {

    String sqlCreateUsers = "CREATE TABLE IF NOT EXISTS users(\n" + //
        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" + //
        "user_name TEXT NOT NULL,\n" + //
        "email TEXT NOT NULL,\n" + //
        "password TEXT NOT NULL,\n" + //
        "auth_code TEXT NOT NULL,\n" + //
        "nick_name TEXT NOT NULL);\n";

    try {
      connection.setAutoCommit(false);
      statement = connection.createStatement();
      statement.executeUpdate(sqlCreateUsers);
    } catch (SQLException e1) {
      Log.info(e1.getMessage());
    } finally {
      try {
        connection.setAutoCommit(true);
        statement.close();
      } catch (SQLException e) {
        Log.error(e.getMessage());
      }
    }
  }

  @Override
  public void add(User user) {

  }

  @Override
  public List<User> listUsers() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getUserByUserName(String username) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getUserByEmail(String email) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(User user) {
    // TODO Auto-generated method stub

  }

  @Override
  public User getUserByKey(String key) {
    // TODO Auto-generated method stub
    return null;
  }
}
