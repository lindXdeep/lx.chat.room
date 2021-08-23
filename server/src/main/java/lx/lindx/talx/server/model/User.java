package lx.lindx.talx.server.model;

import java.util.Objects;

public class User {
  private int id;
  private String login;
  private String email;
  private String nickName;
  private String password;
  private String authCode;

  public User() {
  }

  public User(int id, String login, String email, String nickName, String password, String authCode) {
    this.id = id;
    this.login = login;
    this.email = email;
    this.nickName = nickName;
    this.password = password;
    this.authCode = authCode;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLogin() {
    return this.login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNickName() {
    return this.nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAuthCode() {
    return this.authCode;
  }

  public void setAuthCode(String authCode) {
    this.authCode = authCode;
  }

  @Override
  public String toString() {
    return "{" +
      " id='" + getId() + "'" +
      ", login='" + getLogin() + "'" +
      ", email='" + getEmail() + "'" +
      ", nickName='" + getNickName() + "'" +
      ", password='" + getPassword() + "'" +
      ", authCode='" + getAuthCode() + "'" +
      "}";
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 17 * hash + id;
    hash = 17 * hash + (login == null ? 0 : login.hashCode());
    hash = 17 * hash + (email == null ? 0 : email.hashCode());

    return hash;
  }

  @Override
  public boolean equals(Object o) {
      if(o == this)
        return true;
      if(o ==null || this.getClass() != o.getClass())
        return false;

      User user = (User) o;

      return Objects.equals(id, user.id) && 
             Objects.equals(login, user.login) &&
             Objects.equals(email, user.email);
  }  
}
