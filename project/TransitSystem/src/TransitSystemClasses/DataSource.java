package TransitSystemClasses;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.YEARS;

/**
 * A class that manages the data of the transit system.
 */
public class DataSource {
  // Singleton pattern
  private static DataSource dataSource = new DataSource("./serialized_data.ser");
  private HashMap<String, Card> allCards;
  private HashMap<String, TransitUserAccount> allTransitUserAccounts;
  private AdminUserAccount adminUserAccount;
  private ArrayList<BuyCardPayment> acceptedBuyCardPayment;
  private ArrayList<Payment> pendingPayment;
  private HashMap<String, Double> fareInfo;

  private DataSource(String filePath) {
    // populate ArrayLists from stored serialized data on file.
    File file = new File(filePath);
    if (file.exists()) {
      readFromFile(filePath);
    } else {
      initNew();
    }
  }

  public static DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Initializes the data sets to empty if can't read info from file.
   */
  private void initNew() {
    allCards = new HashMap<>();
    allTransitUserAccounts = new HashMap<>();
    acceptedBuyCardPayment = new ArrayList<>();
    pendingPayment = new ArrayList<>();
  }

  @SuppressWarnings("unchecked")
  private void readFromFile(String filePath) {
    try {
      InputStream file = new FileInputStream(filePath);
      InputStream buffer = new BufferedInputStream(file);
      ObjectInput input = new ObjectInputStream(buffer);

      // Deserialize to the ArrayLists or HashMaps
      allCards = (HashMap<String, Card>) input.readObject();
      allTransitUserAccounts = (HashMap<String, TransitUserAccount>) input.readObject();
      adminUserAccount = (AdminUserAccount) input.readObject();
      acceptedBuyCardPayment = (ArrayList<BuyCardPayment>) input.readObject();
      pendingPayment = (ArrayList<Payment>) input.readObject();
      fareInfo = (HashMap<String, Double>) input.readObject();
      // Remove out-of-date information (info from three years ago)
      allCards
              .values()
              .forEach(
                      x -> {
                        Map<LocalDateTime, Double> transactions = x.getTransactions();
                        transactions
                                .keySet()
                                .forEach(
                                        y -> {
                                          if (YEARS.between(y.toLocalDate(), LocalDate.now()) > 3) {
                                            transactions.remove(y);
                                          }
                                        });
                      });
      allCards
              .values()
              .forEach(
                      x -> {
                        Map<LocalDateTime, Double> transactions = x.getPassTransactions();
                        transactions
                                .keySet()
                                .forEach(
                                        y -> {
                                          if (YEARS.between(y.toLocalDate(), LocalDate.now()) > 3) {
                                            transactions.remove(y);
                                          }
                                        });
                      });
      allCards
              .values()
              .forEach(
                      x -> {
                        ArrayList<Trip> trips = x.getTrips();
                        trips.removeIf(
                                y ->
                                        YEARS.between(y.getEnd().getTimeStart().toLocalDate(), LocalDate.now())
                                                > 3);
                      });

      acceptedBuyCardPayment.removeIf(
              x -> YEARS.between(x.getTime().toLocalDate(), LocalDate.now()) > 3);

      input.close();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      initNew();
    }
  }

  public void saveToFile(String filePath) throws IOException {
    OutputStream file = new FileOutputStream(filePath);
    OutputStream buffer = new BufferedOutputStream(file);
    ObjectOutput output = new ObjectOutputStream(buffer);
    adminUserAccount = AdminUserAccount.getDefaultAdmin();
    // Serialize the ArrayLists
    output.writeObject(allCards);
    output.writeObject(allTransitUserAccounts);
    output.writeObject(adminUserAccount);
    output.writeObject(acceptedBuyCardPayment);
    output.writeObject(pendingPayment);
    saveFares();
    output.writeObject(fareInfo);
    output.close();
    saveStations("Stations.txt");
  }

  /**
   * Saves the station information into a readable txt file.
   *
   * @param filePath the path of the Station.txt path.
   */
  private void saveStations(String filePath) throws IOException {
    FileWriter fileWriter = new FileWriter(filePath);
    BufferedWriter writer = new BufferedWriter(fileWriter);
    for (String route : SubwaySubTrip.getSubwayRoutes().keySet()) {
      String[] thisRoute = SubwaySubTrip.getSubwayRoutes().get(route);
      writer.write(route + "(Subway)" + ": ");
      writer.write(String.join(" - ", thisRoute));
      writer.write(";");
      writer.write(System.lineSeparator());
    }
    for (String route : BusSubTrip.getBusRoutes().keySet()) {
      String[] thisRoute = BusSubTrip.getBusRoutes().get(route);
      writer.write(route + "(Bus)" + ": ");
      writer.write(String.join(" - ", thisRoute));
      writer.write(";");
      writer.write(System.lineSeparator());
    }
    writer.flush();
    writer.close();
  }

  private void saveFares(){
    fareInfo = new HashMap<>();
    for (Fares fare: Fares.values()){
      fareInfo.put(fare.name(), fare.getFare());
    }
  }

  public HashMap<String, Card> getAllCards() {
    return allCards;
  }

  public ArrayList<Payment> getPendingPayment() {
    return pendingPayment;
  }

  public ArrayList<BuyCardPayment> getAcceptedBuyCardPayment() {
    return acceptedBuyCardPayment;
  }

  public AdminUserAccount getAdminUserAccount() {
    return adminUserAccount;
  }

  public HashMap<String, TransitUserAccount> getAllTransitUserAccounts() {
    return allTransitUserAccounts;
  }

  public HashMap<String, Double> getFareInfo() {
    return fareInfo;
  }
}
