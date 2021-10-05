package lx.talx.server.model;

public class UserBuilder {

  private int id;
  private String userName;
  private String email;
  private String password;

  private String authCode;

  private String nickName;

  private String key;

  public UserBuilder setId(final int id) {
    this.id = id;
    return this;
  }

  public UserBuilder setUserName(final String userName) {
    this.userName = userName;
    return this;
  }

  public UserBuilder setEmail(final String email) {
    this.email = email;
    return this;
  }

  public UserBuilder setPassword(final String password) {
    this.password = password;
    return this;
  }

  public UserBuilder setAuthCode(final String authCode) {
    this.authCode = authCode;
    return this;
  }

  public UserBuilder setNickName(final String nickName) {
    this.nickName = nickName;
    return this;
  }

  public UserBuilder setKey(String key) {
    this.key = key;
    return this;
  }

  public int getId() {
    return this.id;
  }

  public String getUserName() {
    return this.userName;
  }

  public String getEmail() {
    return this.email;
  }

  public String getPassword() {
    return this.password;
  }

  public String getAuthCode() {
    return this.authCode;
  }

  public String getNickName() {
    return this.nickName;
  }

  public String getKey() {
    return this.key;
  }

  public User build() {
    return new User(this);
  }
}
