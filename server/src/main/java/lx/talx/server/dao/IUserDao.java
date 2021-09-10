package lx.talx.server.dao;

import java.util.List;

import lx.talx.server.model.User;

public interface IUserDao {
  void add(User user);

  List<User> listUsers();

  User getUserByUserName(String username);

  User getUserByEmail(String email);

  void delete(User user);

  User getUserByKey(String key);
}
