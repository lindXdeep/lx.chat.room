package lx.talx.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import lx.talx.server.model.User;
import lx.talx.server.model.UserBuilder;
import lx.talx.server.service.DataBaseService;

public class UserDaoDataBase implements IUserDao {

  private Connection connection;
  private Statement statement;

  public UserDaoDataBase() {

    try {
      connection = DataBaseService.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    createUserTable();
  }

  private void createUserTable() {

    String sql = """
        CREATE TABLE IF NOT EXISTS users (
          id        INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
          user_name TEXT    NOT NULL,
          email     TEXT    NOT NULL,
          password  TEXT    NOT NULL,
          auth_code TEXT    NOT NULL,
          nick_name TEXT    NOT NULL,
          key       TEXT    NOT NULL
        );""";

    try {
      connection.setAutoCommit(false);
      statement = connection.createStatement();
      statement.executeUpdate(sql);
    } catch (SQLException e1) {
      e1.printStackTrace();
    } finally {
      try {
        connection.setAutoCommit(true);
        statement.close();
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
    }
  }

  @Override
  public void add(User user) {

    String sql = """
        INSERT INTO users (user_name, email, password, auth_code, nick_name, key)
          VALUES(?,?,?,?,?,?);
        """;

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setString(1, user.getUserName());
      preparedStatement.setString(2, user.getEmail());
      preparedStatement.setString(3, user.getPassword());
      preparedStatement.setString(4, user.getAuthCode());
      preparedStatement.setString(5, user.getNickName());
      preparedStatement.setString(6, user.getKey());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<User> listUsers() {
    return null;
  }

  @Override
  public User getUserByUserName(String username) {

    return selectFromUsers( //
        "SELECT id, user_name, email, password, auth_code, nick_name, key " + //
            "FROM users WHERE user_name=?;", //
        username);
  }

  @Override
  public User getUserByEmail(String email) {

    return selectFromUsers( //
        "SELECT id, user_name, email, password, auth_code, nick_name, key " + //
            "FROM users WHERE email=?;", //
        email);
  }

  @Override
  public void delete(User user) {
    // TODO Auto-generated method stub
  }

  @Override
  public User getUserByKey(String key) {

    String sql = //
        "SELECT id, user_name, email, password, auth_code, nick_name, key " + //
            "FROM users WHERE key=?;";

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

      preparedStatement.setString(1, key);

      return resultSet(preparedStatement.executeQuery());

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  private User selectFromUsers(String sqlQuery, String where) {

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

      preparedStatement.setString(1, where);
      return resultSet(preparedStatement.executeQuery());

    } catch (SQLException e2) {
      e2.printStackTrace();
    }
    return null;
  }

  private User resultSet(ResultSet resultSet) throws SQLException {

    if (resultSet.next()) {
      return new UserBuilder() //
          .setId(resultSet.getInt("id")) //
          .setUserName(resultSet.getString("user_name")) //
          .setEmail(resultSet.getString("email")) //
          .setPassword(resultSet.getString("password")) //
          .setAuthCode(resultSet.getString("auth_code")) //
          .setNickName(resultSet.getString("nick_name")) //
          .setKey(resultSet.getString("key")) //
          .build();
    }
    return null;
  }
}
