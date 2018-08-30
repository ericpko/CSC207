package GUI;

import TransitSystemClasses.BusSubTrip;
import TransitSystemClasses.RouteFinder;
import TransitSystemClasses.SubwaySubTrip;
import TransitSystemExceptions.RouteNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.HashMap;

public class RoutePlannerController {
  @FXML MenuButton startStationMenu;
  @FXML MenuButton endStationMenu;
  @FXML ToggleButton subwayFirst;
  @FXML ToggleButton notSubwayFirst;
  @FXML TextArea routeDisplay;
  private ArrayList<CheckMenuItem> startStationOptions = new ArrayList<>();
  private ArrayList<CheckMenuItem> endStationOptions = new ArrayList<>();
  private String startStationSelection;
  private String endStationSelection;

  /** Go back to the login interface. */
  public void onBack() {
    Main.primaryStage.setScene(Main.loginScene);
    Main.primaryStage.setTitle("Login Interface");
    Main.primaryStage.show();
  }

  /** Init the page or refresh the page */
  void onCreate() {
    printStation();
    notSubwayFirst.setSelected(false);
  }

  /**
   * Change the route plan option to Subway first. The route planned under this option will use
   * subway if possible.
   */
  public void onSubwayFirst() {
    if (subwayFirst.isSelected()) {
      notSubwayFirst.setSelected(false);
    } else {
      notSubwayFirst.setSelected(true);
    }
    if (startStationSelection != null && endStationSelection != null) {
      showRoute();
    }
    subwayFirst.setStyle("-fx-background-color: #00FFFF");
    notSubwayFirst.setStyle("-fx-background-color: #b3abab");
  }

  /**
   * Change the route plan option to Subway first. The route planned under this option will be as
   * short as possible.
   */
  public void onNotSubwayFirst() {
    if (notSubwayFirst.isSelected()) {
      subwayFirst.setSelected(false);
    } else {
      subwayFirst.setSelected(true);
    }
    if (startStationSelection != null && endStationSelection != null) {
      showRoute();
    }
    subwayFirst.setStyle("-fx-background-color: #b3abab");
    notSubwayFirst.setStyle("-fx-background-color: #00FFFF");
  }

  /** Return the route information to the text area. */
  public void onView() {
    if (startStationSelection == null) {
      Alert noStationSelected = new Alert(Alert.AlertType.ERROR, "Please select your start point!");
      noStationSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noStationSelected.showAndWait();
      return;
    }
    if (endStationSelection == null) {
      Alert noStationSelected = new Alert(Alert.AlertType.ERROR, "Please select your end point!");
      noStationSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noStationSelected.showAndWait();
      return;
    }
    if (endStationSelection.equals(startStationSelection)) {
      Alert noStationSelected =
          new Alert(
              Alert.AlertType.ERROR, "You selected the same location as your start and end point.");
      noStationSelected.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      noStationSelected.showAndWait();
      return;
    }
    showRoute();
  }

  /** Format the route information that comes from the route planner */
  private void showRoute() {
    String result = "";
    String msg;
    if (subwayFirst.isSelected()) {
      if (isSubwayStation(startStationSelection) && isSubwayStation(endStationSelection)) {
        RouteFinder r = new RouteFinder("Subway");
        try {
          msg = r.getRoutePlan(startStationSelection, endStationSelection);
          result += "Subway only: " + System.lineSeparator() + msg;
        } catch (RouteNotFoundException e) {
          result +=
              "There is no continuous subway route between the two stations you selected."
                  + System.lineSeparator();
        }
      } else {
        result += "Your trip does not start or end at a subway station." + System.lineSeparator();
      }
    }
    RouteFinder r = new RouteFinder("All");
    try {
      msg = r.getRoutePlan(startStationSelection, endStationSelection);
      result += System.lineSeparator() + "Shortest Route:" + System.lineSeparator() + msg;
    } catch (RouteNotFoundException re) {
      result += "There is no continuous route between the two stations you selected. ";
    }
    routeDisplay.setText(result);
  }

  /** Return whether station is a subway station, return if this is not a subway route. */
  private boolean isSubwayStation(String station) {
    HashMap<String, String[]> allSubwayRoutes = SubwaySubTrip.getSubwayRoutes();
    for (String[] route : allSubwayRoutes.values()) {
      for (String s : route) {
        if (s.equals(station)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Print all the stations to the choice box. */
  private void printStation() {
    HashMap<String, String[]> allSubwayRoutes = SubwaySubTrip.getSubwayRoutes();
    HashMap<String, String[]> allBusRoutes = BusSubTrip.getBusRoutes();
    startStationMenu.getItems().clear();
    endStationMenu.getItems().clear();
    printStartRouteStation("Subway", allSubwayRoutes);
    printStartRouteStation("Bus", allBusRoutes);
    printEndRouteStation("Subway", allSubwayRoutes);
    printEndRouteStation("Bus", allBusRoutes);
  }

  /**
   * Helper function for PrintStation. It add all the stations to startStationMenu.
   *
   * @param type the type of the transportation
   * @param routeMap the route map of the corresponding transportation
   */
  private void printStartRouteStation(String type, HashMap<String, String[]> routeMap) {
    for (String route : routeMap.keySet()) {
      Menu routeMenu = new Menu();
      routeMenu.setText(route + "(" + type + ")");
      for (String station : routeMap.get(route)) {
        CheckMenuItem thisOption = new CheckMenuItem(station);
        thisOption.setOnAction(
            event -> {
              thisOption.setSelected(true);
              // Set every other option to unchecked except for this one
              startStationOptions
                  .stream()
                  .filter(x -> x != thisOption)
                  .forEach(x -> x.setSelected(false));
              // Store the selection in the routeSelection
              startStationSelection = station;
              startStationMenu.setText(station);
            });
        routeMenu.getItems().add(thisOption);
        startStationOptions.add(thisOption);
      }
      startStationMenu.getItems().add(routeMenu);
    }
  }

  /**
   * Helper function for PrintStation. It add all the stations to startStationMenu.
   *
   * @param type the type of the transportation
   * @param routeMap the route map of the corresponding transportation
   */
  private void printEndRouteStation(String type, HashMap<String, String[]> routeMap) {
    for (String route : routeMap.keySet()) {
      Menu routeMenu = new Menu();
      routeMenu.setText(route + "(" + type + ")");
      for (String station : routeMap.get(route)) {
        CheckMenuItem thisOption = new CheckMenuItem(station);
        thisOption.setOnAction(
            event -> {
              thisOption.setSelected(true);
              // Set every other option to unchecked except for this one
              endStationOptions
                  .stream()
                  .filter(x -> x != thisOption)
                  .forEach(x -> x.setSelected(false));
              // Store the selection in the routeSelection
              endStationSelection = station;
              endStationMenu.setText(station);
            });
        routeMenu.getItems().add(thisOption);
        endStationOptions.add(thisOption);
      }
      endStationMenu.getItems().add(routeMenu);
    }
  }
}
