package TransitSystemClasses;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A DataGenerator to generate all statistical information for AdminUser.
 */
public class DataGenerator implements Serializable {

  private HashMap<String, Card> allCards;
  private ArrayList<BuyCardPayment> acceptedBuyCardPayment;

  public DataGenerator() {
    DataSource source = DataSource.getDataSource();
    allCards = source.getAllCards();
    acceptedBuyCardPayment = source.getAcceptedBuyCardPayment();
  }

  /**
   * Returns the total number of bus stops reached on the given date, otherwise return 0.
   *
   * @param date the date to find bus stops reached.
   * @return the total number of bus stops reached on the given date, otherwise return 0.
   */
  public int getDailyBusStopsReached(LocalDate date) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getTrips()
                      .stream()
                      .filter(trip -> trip.getStart().getTimeStart().toLocalDate().equals(date))
                      .mapToInt(Trip::totalBusStop)
                      .sum();
    }
    return result;
  }

  /**
   * Returns the total number of subway stations reached on the given date, otherwise return 0.
   *
   * @param date the date to find subway stations reached.
   * @return the total number of subway stations reached on the given date, otherwise return 0.
   */
  public int getDailySubwayStationsReached(LocalDate date) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getTrips()
                      .stream()
                      .filter(trip -> trip.getStart().getTimeStart().toLocalDate().equals(date))
                      .mapToInt(Trip::totalSubwayStation)
                      .sum();
    }
    return result;
  }

  /**
   * Returns the transit pass revenue of the given date, or zero otherwise.
   *
   * @param date the date to find revenue.
   * @return the revenue generated on the given date.
   */
  public double getDailyPassRevenue(LocalDate date) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getPassTransactions()
                      .entrySet()
                      .stream()
                      .filter(x -> x.getKey().toLocalDate().equals(date))
                      .mapToDouble(Map.Entry::getValue)
                      .sum();
    }
    return result;
  }

  /**
   * Returns the fare revenue of the given date, or zero otherwise.
   *
   * @param date the date to find revenue.
   * @return the revenue generated on the given date.
   */
  public double getDailyFareRevenue(LocalDate date) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getTransactions()
                      .entrySet()
                      .stream()
                      .filter(x -> x.getKey().toLocalDate().equals(date))
                      .mapToDouble(Map.Entry::getValue)
                      .sum();
    }
    return result;
  }

  /**
   * Returns the revenue from selling cards of the given date, or zero otherwise.
   *
   * @param date the date to find revenue.
   * @return the revenue generated on the given date.
   */
  public double getDailyCardRevenue(LocalDate date) {
    return acceptedBuyCardPayment
            .stream()
            .filter(x -> x.getTime().toLocalDate().equals(date))
            .count()
            * Fares.newCard.getFare();
  }

  /**
   * Returns the monthly transit pass revenue of the given month.
   *
   * @param year  the year of the given month
   * @param month the int representation of the month.
   * @return the monthly transit pass revenue of the given month.
   */
  public double getMonthlyPassRevenue(int year, int month) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getPassTransactions()
                      .entrySet()
                      .stream()
                      .filter(
                              x ->
                                      x.getKey().toLocalDate().getMonth().getValue() == month
                                              && x.getKey().toLocalDate().getYear() == year)
                      .mapToDouble(Map.Entry::getValue)
                      .sum();
    }
    return result;
  }

  /**
   * Returns the fare revenue of the given month.
   *
   * @param year  the year of the given month
   * @param month the int of the month.
   * @return the monthly revenue of the given month.
   */
  public double getMonthlyFareRevenue(int year, int month) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getTransactions()
                      .entrySet()
                      .stream()
                      .filter(
                              x ->
                                      x.getKey().toLocalDate().getMonth().getValue() == month
                                              && x.getKey().toLocalDate().getYear() == year)
                      .mapToDouble(Map.Entry::getValue)
                      .sum();
    }
    return result;
  }

  /**
   * Returns the card revenue of the given month.
   *
   * @param year  the year of the given month
   * @param month the int of the month.
   * @return the monthly revenue of the given month.
   */
  public double getMonthlyCardRevenue(int year, int month) {
    return acceptedBuyCardPayment
            .stream()
            .filter(x -> x.getTime().getMonthValue() == month && x.getTime().getYear() == year)
            .count()
            * Fares.newCard.getFare();
  }

  /**
   * Returns the total number of bus stops reached on the given month, otherwise return 0.
   *
   * @param year  the year of the given month
   * @param month the int representation of the given month
   * @return the total number of bus stops reached on the given month, otherwise return 0.
   */
  public int getMonthlyBusStopsReached(int year, int month) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getTrips()
                      .stream()
                      .filter(
                              trip ->
                                      trip.getStart().getTimeStart().getMonth().getValue() == month
                                              && trip.getStart().getTimeStart().getYear() == year)
                      .mapToInt(Trip::totalBusStop)
                      .sum();
    }
    return result;
  }

  /**
   * Returns the total number of subway stations reached on the given month, otherwise return 0.
   *
   * @param year  the year of the given month
   * @param month the int representation of the given month
   * @return the total number of subway stations reached on the given month, otherwise return 0.
   */
  public int getMonthlySubwayStationsReached(int year, int month) {
    int result = 0;
    for (Card card : allCards.values()) {
      result +=
              card.getTrips()
                      .stream()
                      .filter(
                              t ->
                                      t.getStart().getTimeStart().getMonth().getValue() == month
                                              && t.getStart().getTimeStart().getYear() == year)
                      .mapToInt(Trip::totalSubwayStation)
                      .sum();
    }
    return result;
  }
}
