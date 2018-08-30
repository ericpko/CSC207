package GUI;

import TransitSystemClasses.AdminUserAccount;
import TransitSystemClasses.BusSubTrip;
import TransitSystemClasses.SubwaySubTrip;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.*;

public class RouteEditorInterfaceController {
  private static final Logger logger = Logger.getLogger(AdminUserAccount.class.getName());

  static {
    logger.setUseParentHandlers(false);
    try {
      Handler fileHandler = new FileHandler("AdminUserAccountLog.txt", true);
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  TextArea routeStack;
  private HashMap<String, String[]> allBusRoutes = BusSubTrip.getBusRoutes();
  private HashMap<String, String[]> allSubwayRoutes = SubwaySubTrip.getSubwayRoutes();
  @FXML
  private AnchorPane mainPane;

  /**
   * Add a new route.
   */
  public void onAddNew() {
    String type = "";
    String name = "";
    ArrayList<String> route = new ArrayList<>();
    Alert routeType = new Alert(Alert.AlertType.CONFIRMATION);
    routeType.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    routeType.setTitle("Add a new route");
    routeType.setHeaderText("Is this a subway route or a bus route");

    ButtonType subwayRoute = new ButtonType("Subway");
    ButtonType busRoute = new ButtonType("Bus");
    ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    routeType.getButtonTypes().setAll(subwayRoute, busRoute, cancel);

    Optional<ButtonType> result = routeType.showAndWait();
    if (result.isPresent()) {
      if (result.get() == subwayRoute) {
        type = "Subway";
      } else if (result.get() == busRoute) {
        type = "Bus";
      } else if (result.get() == cancel) {
        routeType.close();
        return;
      }
    }

    TextInputDialog routeName = new TextInputDialog();
    routeName.setTitle("Add a new route");
    routeName.setHeaderText("Enter the name of this route ");
    Optional<String> nameResult = routeName.showAndWait();
    if (nameResult.isPresent()) {
      name = nameResult.get();
      if (type.equals("Bus")) {
        if (allBusRoutes.containsKey(name)) {
          Alert sameNameConfirm =
                  new Alert(
                          Alert.AlertType.CONFIRMATION,
                          "There is a bus route that has the same name! Continue doing so will overwrite that origin route");
          sameNameConfirm.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          sameNameConfirm.showAndWait();
        }
      } else if (type.equals("Subway")) {
        if (allSubwayRoutes.containsKey(name)) {
          Alert sameNameConfirm =
                  new Alert(
                          Alert.AlertType.CONFIRMATION,
                          "There is a subway route that has the same name! Continue doing so will overwrite that origin route");
          sameNameConfirm.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          sameNameConfirm.showAndWait();
        }
      }
    }
    while (true) {
      TextInputDialog stop = new TextInputDialog();
      stop.setTitle("Add a new route");
      stop.setHeaderText(
              "Enter the name of each station/stop,"
                      + System.lineSeparator()
                      + "click confirm, enter \"END\" to terminate.");
      Optional<String> resultName = stop.showAndWait();
      if (resultName.isPresent()) {
        if (resultName.get().equals("END")) {
          if (type.equals("Bus")) {
            allBusRoutes.put(name, route.toArray(new String[0]));
          } else if (type.equals("Subway")) {
            allSubwayRoutes.put(name, route.toArray(new String[0]));
          }
          logger.log(
                  Level.INFO,
                  String.format(
                          "A new route is added: %s: %s",
                          name, String.join(" -> ", route.toArray(new String[0]))));
          Alert success =
                  new Alert(Alert.AlertType.INFORMATION, "You have successfully added a new route.");
          success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
          success.showAndWait();
          printRoute();
          stop.close();
          return;
        }
        route.add(resultName.get());
      }
    }
  }

  /**
   * Remove a route.
   */
  public void onRemove() {
    TextInputDialog routeName = new TextInputDialog();
    routeName.setTitle("Remove a route");
    routeName.setContentText("Please enter the name of the route that you want to remove");
    Optional<String> result = routeName.showAndWait();
    if (result.isPresent()) {
      String name = result.get();
      if (!allSubwayRoutes.containsKey(name) && !allBusRoutes.containsKey(name)) {
        Alert error =
                new Alert(Alert.AlertType.ERROR, String.format("Route \"%s\" doesn't exist.", name));
        error.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        error.showAndWait();
        return;
      }
      String[] route;
      if (allBusRoutes.containsKey(name)) {
        route = allBusRoutes.get(name);
        allBusRoutes.remove(name);
      } else {
        route = allSubwayRoutes.get(name);
        allSubwayRoutes.remove(name);
      }
      logger.log(
              Level.INFO,
              String.format("A new route is removed: %s: %s", name, String.join(" -> ", route)));
      Alert success =
              new Alert(Alert.AlertType.INFORMATION, "You have successfully removed route" + name);
      success.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      success.showAndWait();
      printRoute();
    }
  }

  /**
   * Print all route into the textArea routeStack
   */
  void printRoute() {
    HashMap<String, String[]> allBusRoutes = BusSubTrip.getBusRoutes();
    HashMap<String, String[]> allSubwayRoutes = SubwaySubTrip.getSubwayRoutes();
    routeStack.clear();
    StringBuilder text = new StringBuilder();
    for (String routeName : allBusRoutes.keySet()) {
      String[] thisRoute = allBusRoutes.get(routeName);
      text.append(routeName);
      text.append("(Bus)");
      text.append(String.join(" -> ", thisRoute));
      text.append(System.lineSeparator());
    }
    for (String routeName : allSubwayRoutes.keySet()) {
      String[] thisRoute = allSubwayRoutes.get(routeName);
      text.append(routeName);
      text.append("(Subway):");
      text.append(String.join("->", thisRoute));
      text.append(System.lineSeparator());
    }
    routeStack.setText(text.toString());
  }

  AnchorPane getMainPane() {
    return mainPane;
  }

  public void onLogOut() {
    Main.primaryStage.setScene(Main.loginScene);
    Main.primaryStage.setTitle("Login Interface");
    Main.primaryStage.show();
  }
}
