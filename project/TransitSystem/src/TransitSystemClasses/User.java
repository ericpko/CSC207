package TransitSystemClasses;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A TransitSystemClasses.User that has private information.
 */
class User implements Serializable {

  // the first name of this user.
  private String firstName;
  // the last name of this user.
  private String lastName;
  // the email of this user.
  private String email;
  // SHA256 value of user's password
  private String password;

  /**
   * Constructs a new TransitSystemClasses.User with a name and email address.
   *
   * @param firstName the first name of this TransitSystemClasses.User.
   * @param lastName  the last name of this TransitSystemClasses.User.
   * @param email     the email of this TransitSystemClasses.User.
   */
  User(String firstName, String lastName, String email, String plainPassword) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = this.getSHA256(plainPassword);
  }

  /**
   * Returns whether the plainPassword is the correct password of this user.
   *
   * @param plainPassword is the plain password which is not encrypted.
   * @return whether the plainPassword is the password (before encryption) of this user.
   */
  boolean verifyLogin(String plainPassword) {
    return getSHA256(plainPassword).equals(password);
  }

  /**
   * Encrypts user's plain password using SHA256 algorithm. Cited from:
   * https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
   *
   * @param plainPassword is the plain password which is not encrypted.
   * @return the SHA256 value of the plainPassword
   */
  private String getSHA256(String plainPassword) {
    try {
      MessageDigest ms = MessageDigest.getInstance("SHA-256");
      byte[] bytes = ms.digest(plainPassword.getBytes());
      StringBuilder sha256 = new StringBuilder();
      String temp;
      for (Byte aByte : bytes) {
        temp = Integer.toHexString(aByte & 0xff);
        if (temp.length() == 1) {
          // if there is only one digit, use 0 to fill in higher digit
          sha256.append("0");
        }
        sha256.append(temp);
      }
      return sha256.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

  String getName() {
    return firstName + " " + lastName;
  }

  String getEmail() {
    return email;
  }

  void setName(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    return String.format("Name: %s%nEmail: %s%n", getName(), email);
  }

  void setPassword(String password) { this.password = getSHA256(password); }
}
