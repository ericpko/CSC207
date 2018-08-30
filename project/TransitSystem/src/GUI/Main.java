package GUI;

import TransitSystemClasses.*;
import TransitSystemExceptions.CriticalFileMissingException;
import TransitSystemExceptions.FileReadException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main extends Application {

  static Stage primaryStage;
  static Scene loginScene;
  static Scene transitUserInterface;
  static Scene tapInInterface;
  static Scene adminUserInterface;
  static Scene routePlannerInterface;
  private static DataSource dataSource; // all data of the transit system

  public static void main(String[] args) {
    initData();
    launch(args);
  }

  /**
   * Distribute archived data to its correct position.
   */
  private static void initData() {
    try {
      ArrayList<HashMap<String, String[]>> info =
              readStations("Stations.txt");
      SubwaySubTrip.setSubwayRoutes(info.get(0));
      BusSubTrip.setBusRoutes(info.get(1));
    } catch (Exception e) {
      e.printStackTrace();
    }
    dataSource = DataSource.getDataSource();
    if (dataSource.getAdminUserAccount() != null) {
      AdminUserAccount.setDefaultAdmin(dataSource.getAdminUserAccount());
    }
    TransitUserAccount.setAllTransitUserAccount(dataSource.getAllTransitUserAccounts());
    Card.setAllCard(dataSource.getAllCards());
    Payment.setPendingPayment(dataSource.getPendingPayment());
    BuyCardPayment.setAcceptedBuyCardPayment(dataSource.getAcceptedBuyCardPayment());
    if (dataSource.getAdminUserAccount() !=  null){
      for(Fares fare: Fares.values()){
        fare.setFare(dataSource.getFareInfo().get(fare.name()));
      }
    }
  }

  static DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Read bus and subway station information from filePath and return information.
   *
   * @param filePath the path of the file that stores the routes information.
   * @return ArrayList of two HashMap, the first HashMap stores all the subway info, the second one
   * stroe the bus info.
   * @throws CriticalFileMissingException when the station information file is missing.
   * @author Qingyuan Qie
   */
  private static ArrayList<HashMap<String, String[]>> readStations(String filePath)
          throws CriticalFileMissingException, FileReadException {
    File stationInfo = new File(filePath);
    HashMap<String, String[]> subwayStation = new HashMap<>();
    HashMap<String, String[]> busStation = new HashMap<>();
    Scanner content;
    ArrayList<HashMap<String, String[]>> result = new ArrayList<>();
    try {
      content = new Scanner(stationInfo);

    } catch (IOException e) {
      throw new CriticalFileMissingException("Station File missing.");
    }
    while (content.hasNextLine()) {
      String thisLine = content.nextLine();
      if (thisLine.trim().isEmpty()) {
        break;
      }
      int nameEnd = thisLine.indexOf("(");
      int columnIndex = thisLine.indexOf(":");
      String routeName = thisLine.substring(0, nameEnd);
      // +1, -1 is to get rid of the bracket.
      String transitType = thisLine.substring(nameEnd + 1, columnIndex - 1);
      ArrayList<String> thisRoute = new ArrayList<>();
      // Read until the end of the line.
      String restInfo = thisLine.substring(columnIndex + 2, thisLine.indexOf(";"));
      String[] stationArray = restInfo.split("-");
      for (String station : stationArray) {
        String trimStation = station.trim();
        // get rid of the double quote around the station name.
        thisRoute.add(trimStation);
      }
      if (transitType.equalsIgnoreCase("subway")) {
        subwayStation.put(routeName, thisRoute.toArray(new String[0]));
      } else if (transitType.equalsIgnoreCase("bus")) {
        busStation.put(routeName, thisRoute.toArray(new String[0]));
      } else {
        throw new FileReadException(
                String.format("%s: Designated transit type does not exist!", transitType));
      }
    }
    result.add(subwayStation);
    result.add(busStation);
    return result;
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    Main.primaryStage = primaryStage;
    Parent root = FXMLLoader.load(getClass().getResource("LoginInterface.fxml"));
    primaryStage.setTitle("Transit System");
    Scene scene = new Scene(root, 1024, 768);
    loginScene = scene;
    primaryStage.setScene(scene);
    primaryStage.setResizable(true);
    // Save system data to file when close.
    primaryStage.setOnCloseRequest(
            event -> {
              primaryStage.setScene(loginScene);
              Alert info =
                      new Alert(
                              Alert.AlertType.INFORMATION,
                              "To avoid information loss, click \"Safely Exit\" to exit program.");
              info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
              info.showAndWait();
              event.consume();
            });
    primaryStage.show();
  }
}
