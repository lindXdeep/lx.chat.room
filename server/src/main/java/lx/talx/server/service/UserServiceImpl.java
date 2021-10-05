package lx.talx.server.service;

import java.sql.Connection;
import java.util.List;

import lx.talx.server.dao.IUserDao;
import lx.talx.server.dao.UserDaoDataBase;
import lx.talx.server.dao.UserDaoInMemory;
import lx.talx.server.model.User;

public class UserServiceImpl implements IUserService {

  private IUserDao userDao /* = new UserDaoInMemory() */;

  public UserServiceImpl() {

     DataBaseService.loadProperties();
     userDao = new UserDaoDataBase();

    // if (DataBaseService.connect) {
    //   System.out.println("-- -- -- database");
    //   userDao = new UserDaoDataBase();
    // } else {
    //   System.out.println("== == == in memory");
    //   userDao = new UserDaoInMemory();
    // }
  }

  @Override
  public void add(User user) {
    userDao.add(user);
  }

  @Override
  public List<User> listUsers() {
    return null;
  }

  @Override
  public User getUserByUserName(String username) {
    return userDao.getUserByUserName(username);
  }

  @Override
  public User getUserByEmail(String email) {
    return userDao.getUserByEmail(email);
  }

  @Override
  public User getUserByKey(String key) {
    return userDao.getUserByKey(key);
  }

  @Override
  public void delete(User user) {

  }

  @Override
  public void update(User user) {
    userDao.update(user);
  }
}
