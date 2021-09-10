package lx.talx.server.dao;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;

import lx.talx.server.model.User;

public class UserDaoInMemory implements IUserDao {

  Path dbusers = Paths.get("dbusers");

  HashMap<String, User> users;

  public UserDaoInMemory() {

    if (Files.exists(dbusers, LinkOption.NOFOLLOW_LINKS)) {
      readDbInMemory();
    } else {
      users = new HashMap<String, User>();
    }
  }

  private void readDbInMemory() {
    try (ObjectInputStream dbIn = new ObjectInputStream(new FileInputStream(dbusers.toString()))) {
      users = (HashMap<String, User>) dbIn.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void writeOnDisk() {
    try (ObjectOutputStream dbOut = new ObjectOutputStream(new FileOutputStream(dbusers.toString()))) {
      dbOut.writeObject(users);
      dbOut.flush();
      dbOut.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void add(User user) {
    users.put(user.getUserName(), user);
    writeOnDisk();
  }

  @Override
  public List<User> listUsers() {
    return null;
  }

  @Override
  public User getUserByUserName(String username) {
    return users.get(username);
  }

  @Override
  public User getUserByEmail(String email) {

    Iterator<Entry<String, User>> it = users.entrySet().iterator();

    while (it.hasNext()) {

      Entry<String, User> i = it.next();

      if (i.getValue().getEmail().equals(email))
        return i.getValue();
    }
    return null;
  }

  @Override
  public User getUserByKey(String key) {

    Iterator<Entry<String, User>> it = users.entrySet().iterator();

    while (it.hasNext()) {
      User u = it.next().getValue();
      if (u.getAuthCode().concat(u.getPassword()).equals(key)) {
        return u;
      }
    }
    return null;
  }

  @Override
  public void delete(User user) {

  }

}
