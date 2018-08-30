package GUI;

import TransitSystemClasses.AdminUserAccount;
import TransitSystemClasses.DataSource;
import TransitSystemClasses.Fares;
import TransitSystemClasses.Payment;
import TransitSystemExceptions.ChangePasswordException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Optional;

/** Controller of the AdminUserInterface. */
public class AdminUserController {

  @FXML TextArea pendingList;
  @FXML private ChoiceBox<String> transactionSelection;
  @FXML private CheckBox approveCheck;
  @FXML private CheckBox declineCheck;
  @FXML private Tab routeEditor;
  @FXML private Tab statistics;
  @FXML private PasswordField oldPassword;
  @FXML private PasswordField newPassword;
  @FXML private PasswordField reenter;
  @FXML private TextArea priceInfo;
  @FXML private ChoiceBox<Fares> priceSelector;

  // The AdminUserAccount of this Interface;
  private AdminUserAccount account;
  private DataSource source = Main.getDataSource();

  /** Confirm a Pending transaction */
  public void onConfirm() {
    if (transactionSelection.getValue() == null || transactionSelection.getValue().isEmpty()){
      return;
    }
    account.handleTransaction(transactionSelection.getValue(), approveCheck.isSelected());
    refresh();
  }

  /** Print all pending transaction to the TextArea */
  private void printPendingList() {
    ArrayList<Payment> list = source.getPendingPayment();
    pendingList.clear();
    StringBuilder text = new StringBuilder();
    for (Payment payment : list) {
      text.append(payment.toString());
      text.append(System.lineSeparator());
      text.append("===============================");
      text.append(System.lineSeparator());
    }
    pendingList.setText(text.toString());
  }

  /** Print all pending transaction to the transactionSelection */
  private void printToTransactionSelection() {
    ArrayList<Payment> list = source.getPendingPayment();
    transactionSelection.getItems().clear();
    for (Payment payment : list) {
      transactionSelection.getItems().add(payment.toString());
    }
  }

  /**
   * Loading all information and initialize the AdminUserInterface when the interface is loading
   *
   * @param account the account of this login session.
   */
  void onCreate(AdminUserAccount account) {
    this.account = account;
    printPendingList();
    printToTransactionSelection();
    declineCheck.setSelected(true);
    approveCheck.setSelected(false);
    loadViewData();
    loadRouteEditor();
    loadChangePrice();
  }

  /** Refresh the page and update the information. */
  private void refresh() {
    onCreate(account);
  }

  /** Log out and set the primary state to the loginInterface */
  public void onLogOut() {
    Alert signOut = new Alert(Alert.AlertType.INFORMATION, "You have signed out.");
    signOut.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    signOut.setTitle("Sign out!");
    signOut.showAndWait();
    Main.primaryStage.setTitle("Transit System");
    Main.primaryStage.setScene(Main.loginScene);
  }

  /** Set the decline check to unselected when approve is checked. */
  public void onApproveCheck() {
    declineCheck.setSelected(false);
  }

  /** Set the approve check to unselected when decline is checked. */
  public void onDeclineCheck() {
    approveCheck.setSelected(false);
  }

  /** Load the view data Tab. */
  private void loadViewData() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("DataViewer.fxml"));
      loader.load();
      DataViewerController controller = loader.getController();
      BorderPane pane = controller.getMainPane();
      statistics.setContent(pane);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Load and print information to the routeEditor interface. */
  private void loadRouteEditor() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("RouteEditorInterface.fxml"));
      loader.load();
      RouteEditorInterfaceController controller = loader.getController();
      AnchorPane pane = controller.getMainPane();
      routeEditor.setContent(pane);
      controller.printRoute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void onChangePassword() {
    String old = oldPassword.getText();
    String new1 = newPassword.getText();
    String reenter = this.reenter.getText();
    if (old.isEmpty() || new1.isEmpty() || reenter.isEmpty()) {
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please enter information to all the field!");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    if (new1.length() < 8) {
      Alert fail =
          new Alert(Alert.AlertType.ERROR, "Your new password should be at least eight digits!");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    if (!new1.equals(reenter)) {
      Alert fail = new Alert(Alert.AlertType.ERROR, "Two password is not consistent");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    try {
      account.handelChangePassword(old, new1);
      Alert success =
          new Alert(Alert.AlertType.INFORMATION, "You have successfully changed password!");
      success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      success.showAndWait();
    } catch (ChangePasswordException e) {
      Alert fail = new Alert(Alert.AlertType.ERROR, e.getMessage());
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
    }
  }

  /**
   * Load the change price interface and fill it with necessary information.
   */
  private void loadChangePrice(){
    priceInfo.setText(Fares.newCard.getFareInfo());
    priceSelector.getItems().clear();
    for (Fares fare: Fares.values()){
      priceSelector.getItems().add(fare);
    }
  }

  public void onChangePrice(){
    if (priceSelector.getValue() == null){
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a price to change");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    TextInputDialog newValueDialog = new TextInputDialog("Please input the new price: ");
    Optional<String> result = newValueDialog.showAndWait();
    if (result.isPresent()){
      try{
        double newValue = Double.valueOf(result.get());
        account.handleChangePrice(priceSelector.getValue(),newValue);
        loadChangePrice();
      } catch (Exception e){
        Alert fail = new Alert(Alert.AlertType.ERROR, "Please input a valid value!");
        fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        fail.showAndWait();
      }
    }
  }
}
