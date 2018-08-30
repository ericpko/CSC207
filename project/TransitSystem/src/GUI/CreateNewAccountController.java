package GUI;

import TransitSystemClasses.TransitUserAccount;
import TransitSystemExceptions.UserExistException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CreateNewAccountController {

  @FXML
  TextField email;
  @FXML
  TextField firstName;
  @FXML
  TextField lastName;
  @FXML
  PasswordField password;
  @FXML
  PasswordField reEnter;
  @FXML
  Label consistencyIndicator;
  @FXML
  Label emailChecker;
  @FXML
  Label firstNameChecker;
  @FXML
  Label lastNameChecker;
  @FXML
  Label passwordChecker;

  /**
   * Verify whether the password and reEnter password is consistent and longer than 8 digit
   */
  public void verifyCardConsistency() {
    if (password.getText().trim().length() >= 8 && password.getText().equals(reEnter.getText())) {
      consistencyIndicator.setStyle("-fx-background-color: #8EE531");
      consistencyIndicator.setText("");
      return;
    }
    consistencyIndicator.setStyle("-fx-background-color: #D3342F");
    if (password.getText().trim().length() < 8) {
      consistencyIndicator.setText("Password must be longer than 8 digits.");
    } else {
      consistencyIndicator.setText("Two passwords are inconsistency");
    }
  }

  /**
   * Operation when Create button is clicked, checks whether the input is legal.
   */
  public void onCreate() {
    String email = this.email.getText();
    String firstName = this.firstName.getText();
    String lastName = this.lastName.getText();
    String password = this.password.getText();
    String reEnter = this.reEnter.getText();
    boolean pass = true;
    if (email.trim().equals("")
            || !email.matches("^[a-z][a-zA-Z0-9.]*[a-zA-Z0-9]@[[a-z]+.]+[a-z]+$")) {
      emailChecker.setText("X");
      pass = false;
    }
    if (firstName.trim().equals("") || !firstName.matches("^[A-Za-z]+$")) {
      firstNameChecker.setText("X");
      pass = false;
    }
    if (lastName.trim().equals("") || !lastName.matches("^[A-Za-z]+$")) {
      lastNameChecker.setText("X");
      pass = false;
    }
    if (password.trim().equals("") || password.length() < 8) {
      passwordChecker.setText("X");
      pass = false;
    }
    if (reEnter.trim().equals("") || !password.equals(reEnter)) {
      pass = false;
    }
    if (!pass) {
      Alert error =
              new Alert(
                      Alert.AlertType.ERROR, "There are errors in your input, please check your input!");
      error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      error.setTitle("Create Account Fail!");
      error.setHeaderText("Create Account Fail! ╮(╯▽╰)╭");
      error.showAndWait();
      return;
    }
    try {
      TransitUserAccount.handleNewAccount(email, firstName, lastName, password);
    } catch (UserExistException e) {
      Alert fail = new Alert(Alert.AlertType.INFORMATION, e.getMessage());
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.setTitle("Failed!");
      fail.setHeaderText("Failed!");
      fail.showAndWait();
      return;
    }
    Alert success = new Alert(Alert.AlertType.INFORMATION, "Registration success!");
    success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    success.setTitle("You have successfully registered!");
    success.setHeaderText("You have successfully registered!");
    success.showAndWait();
    Main.primaryStage.setScene(Main.loginScene);
  }

  /**
   * Invoked when cancel button is clicked, return the the login Interface.
   */
  public void onCancel() {
    Main.primaryStage.setScene(Main.loginScene);
    Main.primaryStage.setMinHeight(768);
  }
}
