package GUI;

import TransitSystemClasses.Card;
import TransitSystemClasses.Fares;
import TransitSystemClasses.TransitUserAccount;
import TransitSystemExceptions.LowBalanceException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class EditCardController {

  @FXML
  TextArea cardStack;
  @FXML
  ChoiceBox<String> deactivateSelection;
  @FXML
  ChoiceBox<String> activateSelection;
  @FXML
  Button removeCard;
  @FXML
  ChoiceBox<String> removeCardSelection;
  @FXML
  ChoiceBox<String> loadValueSelection;
  @FXML
  ChoiceBox<String> loadPassSelection;
  private TransitUserAccount account;

  /**
   * Initialize the information of this account to the interface.
   *
   * @param account the account used to init the interface.
   */
  void onCreate(TransitUserAccount account) {
    this.account = account;
    ArrayList<Card> cards = account.getAccountHolder().getMyCards();
    cardStack.clear();
    if (cards.size() == 0) {
      cardStack.setText("You don't have any Card yet!");
    }
    // Print all the card on the screen.
    StringBuilder text = new StringBuilder();
    for (Card card : cards) {
      text.append(card.toString());
      text.append(System.lineSeparator());
      text.append("==================================");
      text.append(System.lineSeparator());
    }
    cardStack.setText(text.toString());
    printActivateCard();
    printDeactivateCard();
    printAllCardTo(removeCardSelection);
    printAllCardTo(loadValueSelection);
    printAllCardTo(loadPassSelection);
  }

  /**
   * Ask the user to input their credit card info to buy a new card, start a new buy card request and wait for approval
   * of AdminUser.
   */
  public void onBuyCard() {
    String[] creditCardInfo = paymentWindow();
    if (creditCardInfo == null || Arrays.stream(creditCardInfo).anyMatch(Objects::isNull)) {
      Alert fail =
              new Alert(
                      Alert.AlertType.ERROR,
                      "To buy a new card, please input your credit Card information");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    account.purchaseCard(creditCardInfo[0], creditCardInfo[2], creditCardInfo[1]);
    Alert success =
            new Alert(
                    Alert.AlertType.INFORMATION, "Your request is pending for confirmation by approval");
    success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    success.setTitle("Success!");
    success.showAndWait();
    refresh();
  }

  /**
   * Deactivate the selected card.
   */
  public void onDeactivate() {
    String selection = deactivateSelection.getValue();
    if (selection == null) {
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a card!");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    // if the choice box is not selected to a valid card number, do nothing,
    if (selection.contains("p")) {
      Alert invalid = new Alert(Alert.AlertType.ERROR, "Please select a card to deactivate!");
      invalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      invalid.setTitle("Deactivate fail!");
      invalid.showAndWait();
    }
    for (Card card : account.getAccountHolder().getMyCards()) {
      if (card.getCardNumber().equals(selection)) {
        try {
          account.deactivateCard(card);
          Alert success =
                  new Alert(
                          Alert.AlertType.INFORMATION,
                          String.format("You have successfully deactivate %s", card.getCardNumber()));
          success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          success.setTitle("Success");
          success.showAndWait();
          refresh();
          return;
        } catch (Exception e) {
          Alert invalid =
                  new Alert(Alert.AlertType.ERROR, "Card deactivate fail!" + e.getMessage());
          invalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          invalid.setTitle("Deactivate fail!");
          invalid.showAndWait();
        }
      }
    }
  }

  /**
   * Activate the selected card.
   */
  public void onActivate() {
    String selection = activateSelection.getValue();
    if (selection == null) {
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a card!");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    // if the choice box is not selected to a valid card number, do nothing,
    if (selection.contains("p")) {
      Alert invalid = new Alert(Alert.AlertType.ERROR, "Please select a card to activate!");
      invalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      invalid.setTitle("Deactivate fail!");
      invalid.showAndWait();
    }
    for (Card card : account.getAccountHolder().getMyCards()) {
      if (card.getCardNumber().equals(selection)) {
        try {
          account.activateCard(card);
          Alert success =
                  new Alert(
                          Alert.AlertType.INFORMATION,
                          String.format("You have successfully activate %s", card.getCardNumber()));
          success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          success.setTitle("Success");
          success.showAndWait();
          // Refresh the output.
          refresh();
          return;
        } catch (Exception e) {
          Alert invalid = new Alert(Alert.AlertType.ERROR, "Card activate fail! " + e.getMessage());
          invalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          invalid.setTitle("Activate fail!");
          invalid.showAndWait();
        }
      }
    }
  }

  /**
   * Go back to the transitUSerInterface.
   */
  public void onCancel() {
    Main.primaryStage.setScene(Main.transitUserInterface);
    onCreate(account);
  }

  /**
   * Remove the selected card and transfer the balance/debt to another card. This operation will fail if this
   * user has only one or 0 card.
   */
  public void onRemoveCard() {
    if (removeCardSelection.getValue() == null) {
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a card!");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    if (account.getAccountHolder().getMyCards().size() <= 1) {
      Alert invalid =
              new Alert(
                      Alert.AlertType.ERROR,
                      "You have one or you don't have any card, you can't remove any card now");
      invalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      invalid.showAndWait();
      return;
    }
    Alert info =
            new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "You will have to select a card to transfer the card balance/debt to.");
    info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    info.showAndWait();
    String selection = removeCardSelection.getValue();
    Card cardToRemove = searchCard(selection);
    // if the choice box is not selected to a valid card number, do nothing.
    if (selection.contains("p")) {
      Alert invalid = new Alert(Alert.AlertType.ERROR, "Please select a card to remove!");
      invalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      invalid.setTitle("Remove card fail!");
      invalid.showAndWait();
      return;
    }
    // Ask user to select a card to transfer the balance the balance to.
    ChoiceDialog<String> transfer = new ChoiceDialog<>();
    for (Card card : account.getAccountHolder().getMyCards()) {
      if (!card.getCardNumber().equals(selection)) {
        transfer.getItems().add(card.getCardNumber());
      }
    }
    transfer.setTitle("Select a card");
    transfer.setContentText("Please select a card you want to transfer you fund on this card to.");
    Optional<String> choice = transfer.showAndWait();
    if (choice.isPresent()) {
      String transferCardNum = choice.get();
      Card card = searchCard(transferCardNum);
      try {
        account.removeCard(cardToRemove, card);
        Alert success =
                new Alert(
                        Alert.AlertType.INFORMATION,
                        String.format(
                                "You have successfully remove card %s", cardToRemove.getCardNumber()));
        success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        success.showAndWait();
        refresh();
      } catch (Exception e) {
        Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage());
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        error.showAndWait();
      }
    }
  }

  /**
   * Start a new load value request for the card selected.
   */
  public void onLoadValue() {
    if (loadValueSelection.getValue() == null) {
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a card!");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    Alert info =
            new Alert(
                    Alert.AlertType.INFORMATION,
                    "By load value, you will need to input your credit card information,"
                            + "and the transaction will need to be confirmed by a admin user");
    info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    info.showAndWait();

    // Select the value:
    TextInputDialog loadValue =
            new TextInputDialog("Please input the value you want to load, must be 10, 20, 50, 100");
    Optional<String> value = loadValue.showAndWait();
    float result;
    if (value.isPresent()) {
      String inputValue = value.get().trim();
      if (!inputValue.matches("[0-9]+")) {
        Alert fail = new Alert(Alert.AlertType.ERROR, "Invalid input value.");
        fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        fail.showAndWait();
        return;
      }
      result = Float.valueOf(inputValue);
    } else {
      return;
    }

    String[] creditInfoResult = paymentWindow();

    try {
      if (creditInfoResult == null || Arrays.stream(creditInfoResult).anyMatch(Objects::isNull)) {
        Alert fail = new Alert(Alert.AlertType.ERROR, "Please input your credit Card information");
        fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        fail.showAndWait();
        return;
      }
      account.addValue(
              result,
              searchCard(loadValueSelection.getValue()),
              creditInfoResult[0],
              creditInfoResult[2],
              creditInfoResult[1]);
      Alert success =
              new Alert(
                      Alert.AlertType.INFORMATION, "You request is received and is pending for confirmed");
      success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      success.showAndWait();
    } catch (Exception e) {
      Alert failed = new Alert(Alert.AlertType.ERROR, e.getMessage());
      failed.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      failed.showAndWait();
    }
  }

  /**
   * Load a pass to the selected card, note that the fee of the transit pass is deducted from the card that is being
   * loaded a pass.
   */
  public void onLoadPass() {
    String selection = loadPassSelection.getValue();
    if (selection == null) {
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a card!");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    if (selection.contains("p")) {
      Alert invalid = new Alert(Alert.AlertType.ERROR, "Please select a card to load pass!");
      invalid.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      invalid.showAndWait();
      return;
    }
    Card card = searchCard(selection);
    Alert info =
            new Alert(
                    Alert.AlertType.CONFIRMATION,
                    String.format(
                            "There are two types of transit pass, which due in 7 days or 30 days with price %s and %s.The fee will be deducted from the card you select."
                                    + System.lineSeparator(),
                            Fares.weeklyPass.getFare(),
                            Fares.monthlyPass.getFare()));
    info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    info.showAndWait();
    LocalDateTime current = LocalDateTime.now();
    if (account.hasPass(current)) {
      Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
      warning.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      String currentPass = account.passInfo(current);
      warning.setContentText(
              currentPass
                      + System.lineSeparator()
                      + "You current have the above transit pass in effect. Are you sure you want purchase another pass?");
      Optional<ButtonType> confirm = warning.showAndWait();
      if (confirm.isPresent() && confirm.get() != ButtonType.OK){
        return;
      }
    }
    ChoiceDialog<String> typeSelect = new ChoiceDialog<>("7 days");
    typeSelect.getItems().add("30 days");
    Optional<String> choice = typeSelect.showAndWait();
    String passType = "";
    if (choice.isPresent()) {
      passType = choice.get();
    } else{
      return;
    }
    int duration = Integer.valueOf(passType.substring(0, passType.indexOf(" ")));
    try {
      account.purchaseTransitPass(current, card, current.toLocalDate(), duration);
      Alert success =
              new Alert(Alert.AlertType.INFORMATION, "You have successfully buy a new transit pass.");
      success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      success.showAndWait();
      refresh();
    } catch (LowBalanceException e) {
      Alert fail =
              new Alert(
                      Alert.AlertType.ERROR,
                      "You don't have enough balance to buy the transit pass! Please load value to this card first.");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
    } catch (Exception e) {
      Alert fail = new Alert(Alert.AlertType.ERROR, e.getMessage());
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
    }
  }

  /**
   * Search the card with cardId
   *
   * @param cardId the card you want to search
   * @return the card with the given cardId
   */
  private Card searchCard(String cardId) {
    return account
            .getAccountHolder()
            .getMyCards()
            .stream()
            .filter(x -> x.getCardNumber().equals(cardId))
            .findFirst()
            .orElse(null);
  }

  /**
   * Refresh the interface
   */
  public void refresh() {
    onCreate(account);
  }

  /**
   * Fill the choice box of deactivateSelection.
   */
  private void printDeactivateCard() {
    deactivateSelection.getItems().remove(0, deactivateSelection.getItems().size());
    deactivateSelection.getItems().add("Please select a card to deactivate");
    for (Card card : account.getAccountHolder().getMyCards()) {
      if (card.isActivated()) {
        deactivateSelection.getItems().add(card.getCardNumber());
      }
    }
  }

  /**
   * Fill the choices box of activateSelection
   */
  private void printActivateCard() {
    activateSelection.getItems().remove(0, activateSelection.getItems().size());
    activateSelection.getItems().add("Please select a card to activate");
    for (Card card : account.getAccountHolder().getMyCards()) {
      if (!card.isActivated()) {
        activateSelection.getItems().add(card.getCardNumber());
      }
    }
  }

  /**
   * Load the choiceBox with all the card
   */
  private void printAllCardTo(ChoiceBox<String> choiceBox) {
    choiceBox.getItems().remove(0, choiceBox.getItems().size());
    choiceBox.getItems().add("Please select a card");
    for (Card card : account.getAccountHolder().getMyCards()) {
      choiceBox.getItems().add(card.getCardNumber());
    }
  }

  /**
   * Show a dialog window that collect the credit card information. The code for creating a custom
   * dialog is cited from: https://blog.csdn.net/qq_26954773/article/details/78215554 with
   * significant change.
   *
   * @return the credit card info in form of [name, card number, cvv]
   */
  private String[] paymentWindow() {
    String[] creditInfoResult = new String[3];
    // Create the custom dialog.
    Dialog<String[]> dialog = new Dialog<>();
    dialog.setTitle("Input your Credit Card Information");
    dialog.setHeaderText("Please enter your credit card information");

    // Set the button types.
    ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

    // Create the username and password labels and fields.
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField name = new TextField();
    name.setPromptText("Credit Card Holder");
    TextField creditCardNumber = new PasswordField();
    creditCardNumber.setPromptText("Credit Card Number");
    TextField cvv = new TextField();
    cvv.setPromptText("cvv: ");

    grid.add(new Label("Credit Card Holder:"), 0, 0);
    grid.add(name, 1, 0);
    grid.add(new Label("Credit Card Number:"), 0, 1);
    grid.add(creditCardNumber, 1, 1);
    grid.add(new Label("cvv: "), 0, 2);
    grid.add(cvv, 1, 2);

    dialog.getDialogPane().setContent(grid);

    // Convert the result when the login button is clicked.
    dialog.setResultConverter(
            dialogButton -> {
              if (dialogButton == confirmButtonType) {
                return new String[]{name.getText(), creditCardNumber.getText(), cvv.getText()};
              }
              return null;
            });
    Optional<String[]> creditInfo = dialog.showAndWait();
    if (creditInfo.isPresent()) {
      creditInfoResult = creditInfo.get();
    }
    if (validateCreditCardInfo(creditInfoResult)) {
      return creditInfoResult;
    } else {
      return null;
    }
  }

  /**
   * Validate the credit card information
   *
   * @param info an array of string in form of {CardHolderName, CardNumber, CVV}
   * @return whether it is an valid credit card information
   */
  private boolean validateCreditCardInfo(String[] info) {
    if (info == null) {
      return false;
    }
    if (info[0] == null | info[1] == null | info[2] == null) {
      return false;
    }
    if (info[1].isEmpty()) {
      return false;
    }
    if (info[1].trim().length() != 16 | !info[2].matches("[0-9]*")) {
      return false;
    }
    return (info[2].trim().length() == 3 && info[2].matches("[0-9]*"));
  }
}
