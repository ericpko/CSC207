package TransitSystemClasses;

import TransitSystemExceptions.ChangePasswordException;
import TransitSystemExceptions.LoginFailException;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.*;

/**
 * An AdminUserAccount has access to all the statistical data of the whole transit system.
 */
public class AdminUserAccount implements Serializable {

  // A default AdminUser
  private static AdminUserAccount defaultAdmin =
          new AdminUserAccount(new User("Super", "Admin", "admin@example.com", "password"));
  private User accountHolder;
  private static final Logger logger = Logger.getLogger(AdminUserAccount.class.getName());

  // Initialize the logger to log the information of operations by TransitUsers.
  static {
    logger.setUseParentHandlers(false);
    try {
      Handler fileHandler = new FileHandler("AdminUserAccountLog.txt");
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private AdminUserAccount(User accountHolder) {
    this.accountHolder = accountHolder;
  }

  /**
   * Login into a account
   *
   * @param email         the email of the AdminUser
   * @param plainPassword the plainPassword of the user
   * @return the AdminUserAccount if the password is correct
   * @throws LoginFailException if the username and password doesn't match.
   */
  public static AdminUserAccount handleAdminLogin(String email, String plainPassword)
          throws LoginFailException {
    if (email.equals(defaultAdmin.getAccountHolder().getEmail())) {
      if (defaultAdmin.getAccountHolder().verifyLogin(plainPassword)) {
        logger.log(Level.INFO, String.format("Admin User %s Login in.", email));
        return defaultAdmin;
      } else {
        logger.log(
                Level.INFO,
                String.format("Admin User %s Login fail because of wrong password.", email));
        throw new LoginFailException("Either password or account email is incorrect");
      }
    }
    logger.log(
            Level.INFO, String.format("Login in fail:Admin User %s: account doesn't exist", email));
    throw new LoginFailException("Either password or account email is incorrect");
  }


  /**
   * Accept or decline a transaction.
   *
   * @param transaction the id of the transaction to handle
   * @param approve     approve the transaction if true, reject the transaction otherwise
   */
  public void handleTransaction(String transaction, boolean approve) {
    if (approve) {
      Payment.confirmId(transaction);
    } else {
      Payment.declineId(transaction);
    }
  }

  /**
   * Change password to newPassword if oldPassword matches the current Password of the AdminUserAccount.
   *
   * @param oldPassword the current Password of the admin user account
   * @param newPassword the new password
   */
  public void handelChangePassword(String oldPassword, String newPassword) throws ChangePasswordException {
    if (accountHolder.verifyLogin(oldPassword)) {
      accountHolder.setPassword(newPassword);
      logger.log(Level.INFO, String.format("AdminUser %s change password successfully.", accountHolder.getEmail()));
      return;
    }
    logger.log(Level.INFO, String.format("AdminUser %s change password fail.", accountHolder.getEmail()));
    throw new ChangePasswordException("Change password fail! The password you enter doesn't match current password");
  }

  /**
   * Handle a change price request.
   */
  public void handleChangePrice(Fares fareToChange, double newValue){
    logger.log(Level.INFO, String.format("AdminUser change %s price from %.2f to %.2f", fareToChange.name(), fareToChange.getFare(), newValue));
    fareToChange.setFare(newValue);
  }

  private User getAccountHolder() {
    return accountHolder;
  }

  public static void setDefaultAdmin(AdminUserAccount defaultAdmin) {
    AdminUserAccount.defaultAdmin = defaultAdmin;
  }

  static AdminUserAccount getDefaultAdmin() {
    return defaultAdmin;
  }
}
