package GUI;

import TransitSystemClasses.BusSubTrip;
import TransitSystemClasses.SubwaySubTrip;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class TapTransitController {

  @FXML
  TextField cardNum;
  @FXML
  MenuButton stationMenu;
  private ArrayList<CheckMenuItem> stationOptions = new ArrayList<>();
  private String[] routeSelection; // In the form of [routeName, stationName]

  /**
   * Tap into the selected station.
   */
  public void onTapIn() {
    if (routeSelection == null) {
      Alert noStationSelected = new Alert(Alert.AlertType.ERROR, "Please select a station!");
      noStationSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noStationSelected.showAndWait();
      return;
    }
    if (Main.getDataSource().getAllCards().keySet().contains(cardNum.getText())) {
      try {
        Main.getDataSource()
                .getAllCards()
                .get(cardNum.getText())
                .tapCard(LocalDateTime.now(), routeSelection[1], routeSelection[0], true);
        Alert success = new Alert(Alert.AlertType.INFORMATION, "You have successfully tapped in!");
        success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        success.setTitle("Success!");
        success.showAndWait();
      } catch (Exception e) {
        Alert failed = new Alert(Alert.AlertType.ERROR, e.getMessage());
        failed.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        failed.setTitle("Tap Failed!");
        failed.showAndWait();
      }
    } else {
      Alert failed =
              new Alert(
                      Alert.AlertType.ERROR,
                      String.format("Card Number %s doesn't exist.", cardNum.getText()));
      failed.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      failed.showAndWait();
    }
  }

  /**
   * Tap out from the given station.
   */
  public void onTapOut() {
    if (routeSelection == null) {
      Alert noStationSelected = new Alert(Alert.AlertType.ERROR, "Please select a station!");
      noStationSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noStationSelected.showAndWait();
      return;
    }
    if (Main.getDataSource().getAllCards().keySet().contains(cardNum.getText())) {
      try {
        Main.getDataSource()
                .getAllCards()
                .get(cardNum.getText())
                .tapCard(LocalDateTime.now(), routeSelection[1], routeSelection[0], false);
        Alert success = new Alert(Alert.AlertType.INFORMATION, "You have successfully tapped out!");
        success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        success.setTitle("Success!");
        success.showAndWait();
      } catch (Exception e) {
        Alert failed = new Alert(Alert.AlertType.ERROR, e.getMessage());
        failed.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        failed.setTitle("Tap Failed!");
        failed.showAndWait();
      }
    } else {
      Alert failed =
              new Alert(
                      Alert.AlertType.ERROR,
                      String.format("Card Number %s doesn't exist.", cardNum.getText()));
      failed.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      failed.showAndWait();
    }
  }

  /**
   * Go back to the login interface.
   */
  public void onBack() {
    Main.primaryStage.setScene(Main.loginScene);
    Main.primaryStage.setTitle("Login Interface");
    Main.primaryStage.show();
  }

  /**
   * Init the page or refresh the page
   */
  void onCreate() {
    cardNum.clear();
    printStation();
  }

  /**
   * Print stations to the route selection MenuButton
   */
  private void printStation() {
    HashMap<String, String[]> allSubwayRoutes = SubwaySubTrip.getSubwayRoutes();
    HashMap<String, String[]> allBusRoutes = BusSubTrip.getBusRoutes();
    stationMenu.getItems().clear();
    printRouteStation("Subway", allSubwayRoutes);
    printRouteStation("Bus", allBusRoutes);
  }

  /**
   * Helper function for PrintStation. It add all the stations to stationMenu.
   *
   * @param type     the type of the transportation
   * @param routeMap the route map of the corresponding transportation
   */
  private void printRouteStation(String type, HashMap<String, String[]> routeMap) {
    for (String route : routeMap.keySet()) {
      Menu routeMenu = new Menu();
      routeMenu.setText(route + "(" + type + ")");
      for (String station : routeMap.get(route)) {
        CheckMenuItem thisOption = new CheckMenuItem(station);
        thisOption.setOnAction(
                event -> {
                  thisOption.setSelected(true);
                  // Set every other option to unchecked except for this one
                  stationOptions
                          .stream()
                          .filter(x -> x != thisOption)
                          .forEach(x -> x.setSelected(false));
                  // Store the selection in the routeSelection
                  routeSelection = new String[]{route, station};
                  stationMenu.setText(station);
                });
        routeMenu.getItems().add(thisOption);
        stationOptions.add(thisOption);
      }
      stationMenu.getItems().add(routeMenu);
    }
  }
}
