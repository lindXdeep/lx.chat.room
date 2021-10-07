package lx.talx.server.dao;

import java.io.IOException;
import java.io.InputStream;
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

  private String sqlCreateTable;
  private String sqlAddUser;
  private String sqlDeleteUser;
  private String sqlGetUserByKey;
  private String sqlUpdateUsers;
  private String sqlSelectWhereUsername;
  private String sqlSelectWhereEmail;

  public UserDaoDataBase() {

    try {
      connection = DataBaseService.getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    try {

      ClassLoader loader = this.getClass().getClassLoader();

      sqlCreateTable = new String(loader.getResourceAsStream("sql/sqlCreateTableUsers.sql").readAllBytes());
      sqlAddUser = new String(loader.getResourceAsStream("sql/sqlAddUser.sql").readAllBytes());
      sqlDeleteUser = new String(loader.getResourceAsStream("sql/sqlDeleteUser.sql").readAllBytes());
      sqlGetUserByKey = new String(loader.getResourceAsStream("sql/sqlGetUserByKey.sql").readAllBytes());
      sqlUpdateUsers = new String(loader.getResourceAsStream("sql/sqlUpdateUsers.sql").readAllBytes());
      sqlSelectWhereUsername = new String(loader.getResourceAsStream("sql/sqlSelectWhereUsername.sql").readAllBytes());
      sqlSelectWhereEmail = new String(loader.getResourceAsStream("sql/sqlSelectWhereEmail.sql").readAllBytes());

    } catch (IOException e3) {
      e3.printStackTrace();
    }

    createUserTable();
  }

  private void createUserTable() {

    try {
      statement = connection.createStatement();
      statement.executeUpdate(sqlCreateTable);
    } catch (SQLException e1) {
      e1.printStackTrace();
    } finally {
      try {
        statement.close();
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
    }
  }

  @Override
  public void add(User user) {

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlAddUser)) {

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
    return selectFromUsers(sqlSelectWhereUsername, username);
  }

  @Override
  public User getUserByEmail(String email) {
    return selectFromUsers(sqlSelectWhereEmail, email);
  }

  @Override
  public void delete(User user) {

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteUser)) {
      preparedStatement.setLong(1, user.getId());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public User getUserByKey(String key) {

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlGetUserByKey)) {
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

  @Override
  public void update(User user) {

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateUsers)) {

      preparedStatement.setString(1, user.getEmail());
      preparedStatement.setString(2, user.getPassword());
      preparedStatement.setString(3, user.getAuthCode());
      preparedStatement.setString(4, user.getNickName());
      preparedStatement.setString(5, user.getKey());

      preparedStatement.setInt(6, user.getId());

      preparedStatement.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
