package GUI;

import TransitSystemClasses.AdminUserAccount;
import TransitSystemClasses.TransitUserAccount;
import TransitSystemExceptions.LoginFailException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

  @FXML
  CheckBox adminCheckBox;
  @FXML
  PasswordField password;
  @FXML
  TextField username;
  @FXML
  Button loginButton;
  @FXML
  Button createAccount;

  /**
   * Handle a login request from a TransitUser or AdminUser
   *
   * @throws IOException when the fxml file doesn't
   */
  public void handleLogin() throws IOException {
    if (!adminCheckBox.selectedProperty().getValue()) {
      try {
        TransitUserAccount account =
                TransitUserAccount.handleTransitUserLogin(username.getText(), password.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TransitUserInterface.fxml"));
        Parent root = loader.load();
        TransitUserInterfaceController controller = loader.getController();
        controller.onCreate(account);
        Scene transitUserInterface = new Scene(root);
        Main.transitUserInterface = transitUserInterface;
        Main.primaryStage.setScene(transitUserInterface);
        Main.primaryStage.setTitle("Transit User Interface");
      } catch (LoginFailException e) {
        Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage() + " (╯‵□′)╯︵┻━┻");
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        error.setTitle("Log in Fail!");
        error.setHeaderText("Log in fail!");
        error.showAndWait();
      }
    } else {
      // AdminUser login
      try {
        AdminUserAccount account =
                AdminUserAccount.handleAdminLogin(username.getText(), password.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminUserInterface.fxml"));
        Parent root = loader.load();
        AdminUserController controller = loader.getController();
        controller.onCreate(account);
        Scene adminUserInterface = new Scene(root, 1024, 768);
        Main.adminUserInterface = adminUserInterface;
        Main.primaryStage.setScene(adminUserInterface);
        Main.primaryStage.setTitle("Admin User Interface");
      } catch (LoginFailException e) {
        Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage() + " (╯‵□′)╯︵┻━┻");
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        error.setTitle("Log in Fail!");
        error.setHeaderText("Log in fail!");
        error.showAndWait();
      }
    }
  }

  /**
   * Go to the tap interface.
   *
   * @throws IOException if the TapTransit.fxml doesn't exist.
   */
  public void onGoToTap() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("TapTransit.fxml"));
    Parent root = loader.load();
    Scene tapInterface = new Scene(root);
    Main.tapInInterface = tapInterface;
    TapTransitController controller = loader.getController();
    controller.onCreate();
    Main.primaryStage.setScene(tapInterface);
    Main.primaryStage.setTitle("Tap Interface");
  }

  /**
   * Load the new CreateUserInterface
   *
   * @throws IOException if the interface fxml file doesn't.
   */
  public void handleCreateNewAccount() throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("CreateNewAccount.fxml"));
    Scene scene = new Scene(root);
    Stage primary = Main.primaryStage;
    primary.setScene(scene);
    primary.setTitle("Create a New Transit User Account");
  }

  /**
   * Save all data to a serialized file and save transit route to a readable txt file.
   */
  public void saveAndExit() {
    try {
      Main.getDataSource().saveToFile("./serialized_data.ser");
      Platform.exit();
    } catch (IOException e) {
      Alert saveError = new Alert(Alert.AlertType.ERROR, "Can't save system data to file");
      saveError.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      saveError.setContentText("Unknown error has occurs, please contact developer!");
      saveError.showAndWait();
      e.printStackTrace();
    }
  }

  /**
   * Go to the route planner interface
   *
   * @throws IOException if the RoutePlanner.fxml doesn't exist.
   */
  public void onRoutePlanner() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("RoutePlanner.fxml"));
    Parent root = loader.load();
    Scene routePlannerInterface = new Scene(root);
    Main.routePlannerInterface = routePlannerInterface;
    RoutePlannerController controller = loader.getController();
    controller.onCreate();
    Main.primaryStage.setScene(routePlannerInterface);
    Main.primaryStage.setTitle("Route Planner Interface");
  }
}
