package GUI;

import TransitSystemClasses.Card;
import TransitSystemClasses.Fares;
import TransitSystemClasses.TransitUserAccount;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.time.LocalDate;
import java.util.ArrayList;

public class TransitHistoryController {
  @FXML TextArea tripStack;
  @FXML ChoiceBox<String> cardSelector;
  @FXML MenuButton fromYear;
  @FXML MenuButton fromMonth;
  @FXML MenuButton toYear;
  @FXML MenuButton toMonth;
  @FXML TextArea transactionDisplay;

  private TransitUserAccount account;
  // All the year CheckYearItem for fromYear, etc.
  private ArrayList<CheckMenuItem> yearForFromOptions = new ArrayList<>();
  private ArrayList<CheckMenuItem> yearForToOptions = new ArrayList<>();
  private ArrayList<CheckMenuItem> monthForFromOptions = new ArrayList<>();
  private ArrayList<CheckMenuItem> monthForToOptions = new ArrayList<>();
  private String fromYearSelection;
  private String fromMonthSelection;
  private String toYearSelection;
  private String toMonthSelection;

  /** Print the three most recent trip of the selected card to the textfield. */
  public void onViewTrip() {
    if (cardSelector.getValue() == null || cardSelector.getValue().isEmpty()){
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a card to view its recent trips");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    try {
      tripStack.clear();
      String record =
          account.viewCardRecentTrip(
              account
                  .getAccountHolder()
                  .getMyCards()
                  .stream()
                  .filter(x -> x.getCardNumber().equals(cardSelector.getValue()))
                  .findFirst()
                  .orElse(null));
      tripStack.setText(record);
    } catch (Exception e) {
      Alert fail = new Alert(Alert.AlertType.ERROR, e.getMessage());
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
    }
  }

  /** Go back to the transit user interface. */
  public void onBack() {
    Main.primaryStage.setScene(Main.transitUserInterface);
    Main.primaryStage.setTitle("Transit User Interface");
    Main.primaryStage.show();
  }

  /**
   * Load information to this page.
   *
   * @param account which information is to be viewed on this interface.
   */
  void onCreate(TransitUserAccount account) {
    this.account = account;
    printCard();
    printYears();
    printMonth();
  }

  /** Print all card of this account to the choice box. */
  private void printCard() {
    this.cardSelector.getItems().clear();
    for (Card card : account.getAccountHolder().getMyCards()) {
      this.cardSelector.getItems().add(card.getCardNumber());
    }
  }

  /**
   * Show a alert box that contains the monthly average of this user from the selected start date to
   * selected end date.
   */
  public void onGetAverage() {
    if (fromMonthSelection == null
        || fromYearSelection == null
        || toYearSelection == null
        || toMonthSelection == null) {
      Alert fail =
          new Alert(
              Alert.AlertType.ERROR, "Please select start point and end point to show average.");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    if (toYearSelection.equals(String.valueOf(LocalDate.now().getYear()))
        && Integer.valueOf(toMonthSelection) > LocalDate.now().getMonthValue()) {
      Alert fail =
          new Alert(Alert.AlertType.ERROR, "Please select a month that is not after this month");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    LocalDate start =
        LocalDate.of(Integer.valueOf(fromYearSelection), Integer.valueOf(fromMonthSelection), 1);
    LocalDate end =
        LocalDate.of(Integer.valueOf(toYearSelection), Integer.valueOf(toMonthSelection), 1);
    double result = account.averageTransitCostPerMonth(start, end);
    Alert resultAlert =
        new Alert(
            Alert.AlertType.INFORMATION,
            String.format(
                "The average of your monthly use from %s to %s is %.2f.",
                fromYearSelection + "/" + fromMonthSelection,
                toYearSelection + "/" + toMonthSelection,
                result));
    resultAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    resultAlert.showAndWait();
  }

  /** Show transactions on a card between two months */
  public void onViewTransaction() {
    if (cardSelector.getValue() == null || cardSelector.getValue().isEmpty()){
      Alert fail = new Alert(Alert.AlertType.ERROR, "Please select a card to view its transactions");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    if (fromMonthSelection == null
        || fromYearSelection == null
        || toYearSelection == null
        || toMonthSelection == null) {
      Alert fail =
          new Alert(
              Alert.AlertType.ERROR, "Please select start point and end point to show average.");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    if (toYearSelection.equals(String.valueOf(LocalDate.now().getYear()))
        && Integer.valueOf(toMonthSelection) > LocalDate.now().getMonthValue()) {
      Alert fail =
          new Alert(Alert.AlertType.ERROR, "Please select a month that is not after this month");
      fail.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      fail.showAndWait();
      return;
    }
    LocalDate start =
        LocalDate.of(Integer.valueOf(fromYearSelection), Integer.valueOf(fromMonthSelection), 1);
    LocalDate end =
        LocalDate.of(Integer.valueOf(toYearSelection), Integer.valueOf(toMonthSelection), 1);
    transactionDisplay.clear();
    transactionDisplay.setText(
        account.viewCardTransactions(
            start,
            end,
            account
                .getAccountHolder()
                .getMyCards()
                .stream()
                .filter(x -> x.getCardNumber().equals(cardSelector.getValue()))
                .findFirst()
                .orElse(null)));
  }

  /**
   * Print all the month form 2010/01 to now. Print all month in year menu, and all the month in a
   * month menu.
   */
  private void printYears() {
    fromYear.getItems().clear();
    toYear.getItems().clear();
    LocalDate now = LocalDate.now();
    int currentYear = now.getYear();
    for (int i = 2010; i <= currentYear; i++) {
      CheckMenuItem yearForFrom = new CheckMenuItem();
      fromYear.getItems().add(yearForFrom);

      CheckMenuItem yearForTo = new CheckMenuItem();
      toYear.getItems().add(yearForTo);

      yearForFrom.setText(String.valueOf(i));
      this.yearForFromOptions.add(yearForFrom);

      yearForTo.setText(String.valueOf(i));
      this.yearForToOptions.add(yearForTo);

      yearForFrom.setOnAction(
          event -> {
            yearForFrom.setSelected(true);
            this.yearForFromOptions
                .stream()
                .filter(x -> x != yearForFrom)
                .forEach(x -> x.setSelected(false));
            fromYearSelection = yearForFrom.getText();
            fromYear.setText(yearForFrom.getText());
          });

      yearForTo.setOnAction(
          event -> {
            yearForTo.setSelected(true);
            this.yearForToOptions
                .stream()
                .filter(x -> x != yearForTo)
                .forEach(x -> x.setSelected(false));
            toYearSelection = yearForTo.getText();
            toYear.setText(yearForTo.getText());
          });
    }
  }

  /** Print month 1-12 to the MenuButton. */
  private void printMonth() {
    fromMonth.getItems().clear();
    toMonth.getItems().clear();
    for (int i = 1; i <= 12; i++) {
      CheckMenuItem monthForFrom = new CheckMenuItem();
      fromMonth.getItems().add(monthForFrom);
      monthForFromOptions.add(monthForFrom);

      CheckMenuItem monthForTo = new CheckMenuItem();
      toMonth.getItems().add(monthForTo);
      monthForToOptions.add(monthForTo);

      monthForFrom.setText(String.valueOf(i));
      monthForTo.setText(String.valueOf(i));

      monthForFrom.setOnAction(
          event -> {
            monthForFrom.setSelected(true);
            this.monthForFromOptions
                .stream()
                .filter(x -> x != monthForFrom)
                .forEach(x -> x.setSelected(false));
            fromMonthSelection = monthForFrom.getText();
            fromMonth.setText(monthForFrom.getText());
          });

      monthForTo.setOnAction(
          event -> {
            monthForTo.setSelected(true);
            this.monthForToOptions
                .stream()
                .filter(x -> x != monthForTo)
                .forEach(x -> x.setSelected(false));
            toMonthSelection = monthForTo.getText();
            toMonth.setText(monthForTo.getText());
          });
    }
  }
}
