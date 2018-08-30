package GUI;

import TransitSystemClasses.TransitUserAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.util.Optional;

public class TransitUserInterfaceController {
  @FXML
  Label helloBanner;
  @FXML
  Button changeName;
  @FXML
  Button signOut;
  @FXML
  Button viewTrip;
  @FXML
  Button editCard;
  private TransitUserAccount account;

  /**
   * Pass information from previous scene and do some initialize work.
   */
  void onCreate(TransitUserAccount account) {
    helloBanner.setText("Hello, " + account.getName());
    this.account = account;
  }

  /**
   * Go back to the login interface.
   */
  public void onSignOut() {
    Alert signOut = new Alert(Alert.AlertType.INFORMATION, "You have signed out.");
    signOut.setTitle("Sign out!");
    signOut.showAndWait();
    Main.primaryStage.setScene(Main.loginScene);
  }

  /**
   * Change the name of the holder of this account.
   */
  public void onChangeName() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Change Name");
    dialog.setHeaderText("Change your Name");
    dialog.setContentText("Please enter your new first name: ");
    Optional<String> newName = dialog.showAndWait();
    String newFirst = "", newLast = "";
    if (newName.isPresent()) {
      newFirst = newName.get();
      dialog.setContentText("Please enter your new last Name");
      newName = dialog.showAndWait();
      if (newName.isPresent()) {
        newLast = newName.get();
      }
    }
    try {
      this.account.changeName(newFirst, newLast);
      helloBanner.setText("Hello, " + account.getName());
    } catch (InvalidNameException e) {
      Alert invalidName = new Alert(Alert.AlertType.ERROR, "You input is invalid.");
      invalidName.setContentText(e.getMessage());
      invalidName.showAndWait();
    }
  }

  /**
   * Load the EditCardInterface
   *
   * @throws IOException if the login interface fxml doesn't
   */
  public void onEditCard() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("EditCardInterface.fxml"));
    Parent root = loader.load();
    EditCardController controller = loader.getController();
    controller.onCreate(account);
    Scene cardScene = new Scene(root);
    Main.primaryStage.setScene(cardScene);
  }

  /**
   * Load the view Trip interface
   *
   * @throws IOException if the view trip interface fxml file doesn't file.
   */
  public void onViewTrip() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("TransitHistoryInterface.fxml"));
    Parent root = loader.load();
    TransitHistoryController controller = loader.getController();
    controller.onCreate(account);
    Main.primaryStage.setScene(new Scene(root));
    Main.primaryStage.setTitle("View Transit Trips");
    Main.primaryStage.show();
  }
}
