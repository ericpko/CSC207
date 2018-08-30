package TransitSystemClasses;

import TransitSystemExceptions.*;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * A Transit Card used for travelling
 */
public class Card implements Serializable {

  private static final Logger logger = Logger.getLogger(Card.class.getName());
  // All cards in this transit system
  private static HashMap<String, Card> allCard;

  // Initializes the logger for card activities
  static {
    logger.setUseParentHandlers(false);
    try {
      Handler fileHandler = new FileHandler("log.txt", true);
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private final String cardHolderEmail;
  private final String cardNumber;
  private double balance;
  private boolean activated;

  private Trip currentTrip = null;
  // The fine for abnormal use of this card.
  private final double FINE = Fares.FINE.getFare();
  // All the transactions when tap in/out
  private final Map<LocalDateTime, Double> transactions = new TreeMap<>();
  // The transactions when loading transit pass to this card
  private final Map<LocalDateTime, Double> passTransactions = new TreeMap<>();
  // All trips recorded on this card
  private ArrayList<Trip> trips = new ArrayList<>();
  // All transit passes on this card
  private ArrayList<TransitPass> transitPasses = new ArrayList<>();

  /**
   * Constructs a new Card that has an owner of this Card called cardHolder and a unique ID called
   * cardNumber.
   *
   * @param cardHolderEmail the TransitUser that owns this Card.
   * @param cardNumber      the ID of this Card.
   */
  private Card(String cardHolderEmail, String cardNumber) {
    this.cardHolderEmail = cardHolderEmail;
    this.cardNumber = cardNumber;
    this.balance = 19;
    this.activated = true;
    allCard.put(this.cardNumber, this);
  }

  /**
   * Returns a new card for the transit user with cardHolderEmail
   *
   * @param cardHolderEmail email of the owner of this card
   * @return a new card
   */
  static Card makeCard(String cardHolderEmail) {
    Card newCard = new Card(cardHolderEmail, generateCardNumber());
    logger.log(Level.INFO, String.format("New card %s is made.", newCard.getCardNumber()));
    return newCard;
  }

  /**
   * Returns a random 12 digit string of integers. Note: the probability of two people ever having
   * the same 12 digit card number is 10^12.
   *
   * @return a random 12 digit string of integers
   */
  private static String generateCardNumber() {
    Random randomGen = new Random();
    List<Integer> listNumbers =
            randomGen
                    .ints(12, 0, 10) // inclusive to exclusive
                    .boxed()
                    .collect(Collectors.toList());
    StringBuilder cardNumbers = new StringBuilder();
    for (int n : listNumbers) {
      cardNumbers.append(n);
    }
    return cardNumbers.toString();
  }

  /**
   * Adds to the balance of this Card.
   *
   * @param value the value to add.
   */
  void addBalance(double value) {
    this.balance += value;
    String msg =
            LocalDateTime.now().toString() + ": Card " + cardNumber + " add" + value + " to balance.";
    logger.log(Level.INFO, msg);
  }

  /**
   * Returns the last three trips on this card. Returns all trips if there are less than three.
   *
   * @return the three most recent trip on this card as an array. The latest is at the front of the
   * returned array.
   */
  Trip[] getLastThreeTrips() {
    Trip[] result = new Trip[3];
    int num = this.trips.size();
    if (num > 3) {
      num = 3;
    }
    for (int i = 0; i < num; i++) {
      // The latest trip is at the back of the stack.
      result[i] = this.trips.get(this.trips.size() - i - 1);
    }
    return result;
  }

  /**
   * Loads a transit pass onto this card.
   *
   * @param time           The time of this transaction
   * @param newTransitPass The transit pass to load
   * @throws LowBalanceException if the balance on this card is not enough to buy a pass
   */
  void addTransitPass(LocalDateTime time, TransitPass newTransitPass) throws LowBalanceException {
    if (newTransitPass.getPrice() > balance) {
      throw new LowBalanceException("Your balance is not enough to pay for this transit pass.");
    }
    double price = newTransitPass.getPrice();
    balance -= price;
    passTransactions.put(time, price);
    transitPasses.add(newTransitPass);
    String msg =
            LocalDateTime.now().toString()
                    + ": Card "
                    + cardNumber
                    + " pay "
                    + price
                    + " for a "
                    + newTransitPass.getType();
    logger.log(Level.INFO, msg);
  }

  /**
   * Pay for fare, if this card has a valid transit pass, adjust the fare to 0.
   *
   * @param fare the amount of money to pay.
   * @param time the time of payment.
   */
  private double payFare(double fare, LocalDateTime time) {
    double realFare = fare;
    if (this.hasValidPass(time)) {
      realFare = 0;
    }
    balance -= realFare;
    transactions.put(time, realFare);

    String msg = LocalDateTime.now().toString() + ": Card " + cardNumber + " pay fare" + realFare;
    logger.log(Level.INFO, msg);
    return realFare;
  }

  /**
   * Checks whether this card has a transit pass in effect at the given time. Removes invalid pass
   * automatically.
   *
   * @param time the time to check valid pass
   * @return whether this card has a valid transit pass at the given time
   */
  boolean hasValidPass(LocalDateTime time) {
    LocalDate date = time.toLocalDate();
    for (int i = 0; i < transitPasses.size(); i++) {
      if (transitPasses.get(i).isValid(date)) {
        return true;
      } else {
        transitPasses.remove(i);
        i--;
      }
    }
    return false;
  }

  /**
   * Stores the finished current trip of this Card and starts a new trip
   *
   * @param newSubTrip the Trip to be added.
   */
  private void finishTrip(SubTrip newSubTrip) {
    this.currentTrip = new Trip(newSubTrip);
    this.trips.add(currentTrip);
  }

  /**
   * Finishes a trip without starting the next trip
   */
  private void finishTrip() {
    this.currentTrip = null;
  }

  /**
   * Taps into or out of a bus stop or a subway station with this card.
   *
   * @param time    the LocalDateTime of tapping
   * @param station the station / stop of tapping
   * @param route   the route of the station / stop
   * @param in      whether this card tapIn or tapOut
   * @throws LowBalanceException when card's balance is less than zero.
   */
  public void tapCard(LocalDateTime time, String station, String route, boolean in) // TODO
          throws LowBalanceException, CardSuspendedException, TripEnRouteException {
    if (balance < 0) {
      String msg =
              time + ": Card " + cardNumber + " taps at station " + station + " with low balance.";
      logger.log(Level.INFO, msg);
      throw new LowBalanceException("Low balance! Please load money onto this card.");
    } else if (!isActivated()) {
      String msg = time + ": The suspended card " + cardNumber + " taps at station " + station;
      logger.log(Level.INFO, msg);
      throw new CardSuspendedException("This Card has been suspended. Please call 555-555-5555");
    }
    if (BusSubTrip.getBusRoutes().containsKey(route)) {
      if (in) {
        tapIntoBus(time, station, route);
      } else {
        tapOutOfBus(time, station, route);
      }
    } else if (SubwaySubTrip.getSubwayRoutes().containsKey(route)) {
      if (in) {
        tapIntoSubway(time, station, route);
      } else {
        tapOutOfSubway(time, station, route);
      }
    }
  }

  /**
   * Helper method for tapCard. This is called if TransitUser enters bus.
   *
   * @param time    the LocalDateTime of tap-in
   * @param station the departure stop
   * @param route   the route of travel
   */
  private void tapIntoBus(LocalDateTime time, String station, String route) {
    BusSubTrip nextSubTrip = new BusSubTrip(time, station, route);
    float nextFare = nextSubTrip.calculateFare();
    if (this.currentTrip == null) {
      this.currentTrip = new Trip(nextSubTrip);
      this.trips.add(currentTrip);
      String msg = time + ": Card " + cardNumber + " successfully taps into bus stop " + station;
      logger.log(Level.INFO, msg);
    } else {
      try {
        this.currentTrip.appendTrip(nextSubTrip);
        String msg = time + ": Card " + cardNumber + " successfully taps into bus stop " + station;
        logger.log(Level.INFO, msg);
        float currFare = this.currentTrip.getCurrentTotalFare();
        // deducts up to cappedFare for continuous trip
        if (currFare + nextFare > Fares.MAX_FARE.getFare()) {
          nextFare = (float) Fares.MAX_FARE.getFare() - currFare;
        }
      } catch (TripEnRouteException en) {
        // deducts fine since illegally exit subway without paying fare previously
        String msg =
                time + ": Card " + cardNumber + " taps into bus stop " + station + " without exit.";
        logger.log(Level.INFO, msg);
        double realFare = payFare(FINE, time);
        this.currentTrip.getEnd().setFare(realFare);
        this.finishTrip(nextSubTrip);
      } catch (TripCanNotContinueException nc) {
        this.finishTrip(nextSubTrip);
        String msg = time + ": Card " + cardNumber + " successfully taps into bus stop " + station;
        logger.log(Level.INFO, msg);
      }
    }

    double realFare = payFare(nextFare, time);
    nextSubTrip.setFare(realFare);
  }

  /**
   * Helper method for tapCard. This is called if TransitUser exists bus.
   *
   * @param time    the LocalDateTime of tap out
   * @param station the arrival station /stop
   */
  private void tapOutOfBus(LocalDateTime time, String station, String route) {
    if (this.currentTrip == null) {
      String msg =
              time + ": Card " + cardNumber + " taps out of bus stop " + station + " without entry.";
      logger.log(Level.INFO, msg);
      payFare(FINE, time);

    } else {
      SubTrip endingTrip = this.currentTrip.getEnd();
      try {
        endingTrip.finishTrip(time, station, route);
        String msg =
                time + ": Card " + cardNumber + " successfully taps out of bus stop " + station;
        logger.log(Level.INFO, msg);
      } catch (TripNotEnRouteException | NullPointerException a) {
        String msg =
                time + ": Card " + cardNumber + " taps out of bus stop " + station + " without entry.";
        logger.log(Level.INFO, msg);
        payFare(FINE, time);
      } catch (ExitNotSameRouteException b) {
        String msg =
                time
                        + ": Card "
                        + cardNumber
                        + " taps out of stop "
                        + station
                        + " at a different route.";
        logger.log(Level.INFO, msg);
        double realFare = payFare(FINE, time);
        endingTrip.setFare(realFare); // setting the fare for this sub trip
        // finish this trip to avoid recalculating fine upon the next entry
        this.finishTrip();
      }
    }
  }

  /**
   * Helper method for tapCard. This is called if TransitUser exists subway.
   *
   * @param time    the LocalDateTime of tap out
   * @param station the arrival station / stop
   */
  private void tapOutOfSubway(LocalDateTime time, String station, String route)
          throws TripEnRouteException {
    if (this.currentTrip == null) {
      String msg =
              time
                      + ": Card "
                      + cardNumber
                      + " taps out of subway station "
                      + station
                      + " without entry.";
      logger.log(Level.INFO, msg);
      payFare(FINE, time);
    } else {
      SubTrip endingTrip = this.currentTrip.getEnd();
      try {
        endingTrip.finishTrip(time, station, route);
        String msg =
                time
                        + ": Card "
                        + cardNumber
                        + " taps out subway station "
                        + station
                        + " successfully.";
        logger.log(Level.INFO, msg);
        // set the fare for subway trip when exiting a station
        float nextFare = endingTrip.calculateFare();
        float currTotal = this.currentTrip.getCurrentTotalFare();
        if (currTotal + nextFare > Fares.MAX_FARE.getFare()) {
          nextFare = (float) Fares.MAX_FARE.getFare() - currTotal;
        }
        double realFare = payFare(nextFare, time);
        endingTrip.setFare(realFare);
      } catch (TripNotEnRouteException | NullPointerException a) {
        String msg =
                time
                        + ": Card "
                        + cardNumber
                        + " taps out of subway station "
                        + station
                        + " without entry.";
        logger.log(Level.INFO, msg);
        payFare(FINE, time);
      } catch (ExitNotSameRouteException b) {
        String msg =
                time
                        + ": Card "
                        + cardNumber
                        + " taps out of station "
                        + station
                        + " which is not a bus stop. ";
        logger.log(Level.INFO, msg);
        double realFare = payFare(FINE, time);
        endingTrip.setFare(realFare); // setting the fare for this sub trip
        // avoid recalculating fine upon the next entry
        this.finishTrip();
      }
    }
  }

  /**
   * Helper method for tapCard. This is called if TransitUser enters subway.
   *
   * @param time    the time this TransitUser enters subway.
   * @param station the name of the departure station.
   * @param route   the route of the subway trip.
   */
  private void tapIntoSubway(LocalDateTime time, String station, String route) {
    SubwaySubTrip nextSubTrip = new SubwaySubTrip(time, station, route);
    if (this.currentTrip == null) {
      this.currentTrip = new Trip(nextSubTrip);
      this.trips.add(currentTrip);
      String msg =
              time + ": Card " + cardNumber + " taps into subway station " + station + " successfully";
      logger.log(Level.INFO, msg);
    } else {
      try {
        this.currentTrip.appendTrip(nextSubTrip);
        String msg =
                time
                        + ": Card "
                        + cardNumber
                        + " taps into subway station "
                        + station
                        + " successfully";
        logger.log(Level.INFO, msg);
      } catch (TripEnRouteException en) {
        // deducts fine since illegally exit subway without paying fare previously
        String msg =
                time
                        + ": Card "
                        + cardNumber
                        + " taps into subway station "
                        + station
                        + " without exit";
        logger.log(Level.INFO, msg);
        double realFare = payFare(FINE, time);
        this.currentTrip.getEnd().setFare(realFare);
        this.finishTrip(nextSubTrip);
      } catch (TripCanNotContinueException nc) {
        this.finishTrip(nextSubTrip);
        String msg =
                time
                        + ": Card "
                        + cardNumber
                        + " taps into subway station "
                        + station
                        + " successfully";
        logger.log(Level.INFO, msg);
      }
    }
  }

  /**
   * Returns the total cost of fares and transit passes on this card between the month of the start
   * date and the month of the end date
   *
   * @param startDate a date in the starting month of calculation
   * @param endDate   a date in the ending month of calculation
   * @return the total cost of fares and transit passes between the start and end month (inclusive)
   */
  double getTotalCostsBetweenMonths(LocalDate startDate, LocalDate endDate) {
    LocalDate start = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
    LocalDate end =
            LocalDate.of(
                    endDate.getYear(), endDate.getMonth(), endDate.getMonth().length(endDate.isLeapYear()));
    double sum =
            transactions
                    .entrySet()
                    .stream()
                    .filter(
                            x ->
                                    (x.getKey().toLocalDate().isAfter(start)
                                            || x.getKey().toLocalDate().equals(start))
                                            && (x.getKey().toLocalDate().isBefore(end)
                                            || x.getKey().toLocalDate().equals(end)))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();
    sum +=
            passTransactions
                    .entrySet()
                    .stream()
                    .filter(
                            x ->
                                    (x.getKey().toLocalDate().isAfter(start)
                                            || x.getKey().toLocalDate().equals(start))
                                            && (x.getKey().toLocalDate().isBefore(end)
                                            || x.getKey().toLocalDate().equals(end)))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();
    return sum;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if (this.isActivated()) {
      result.append(
              String.format("Card Number: %s%n Balance: %s%n Status: activated", cardNumber, balance));
    } else {
      result.append(
              String.format(
                      "Card Number: %s%n Balance: %s%n Status: deactivated", cardNumber, balance));
    }
    result.append(System.lineSeparator());
    if (transitPasses.size() == 0) {
      result.append("There is no transit pass associated with this Card");
      return result.toString();
    }
    LocalDate current = LocalDate.now();
    for (TransitPass pass : transitPasses) {
      if (pass.isValid(current)) {
        result.append(pass.toString());
        result.append(System.lineSeparator());
      }
    }
    result.append(System.lineSeparator());
    return result.toString();
  }

  // setters and getters
  double getBalance() {
    return this.balance;
  }

  String getCardHolderEmail() {
    return cardHolderEmail;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  ArrayList<Trip> getTrips() {
    return trips;
  }

  public boolean isActivated() {
    return activated;
  }

  void setActivated(boolean b) {
    this.activated = b;
  }

  Map<LocalDateTime, Double> getTransactions() {
    return transactions;
  }

  Map<LocalDateTime, Double> getPassTransactions() {
    return passTransactions;
  }

  ArrayList<TransitPass> getTransitPasses() {
    return transitPasses;
  }

  public static void setAllCard(HashMap<String, Card> allCard) {
    Card.allCard = allCard;
  }
}
