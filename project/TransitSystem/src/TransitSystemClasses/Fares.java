package TransitSystemClasses;

/**
 * A Fares enum class to hold the different fare rates of the transit system.
 */
public enum Fares {
  FINE(6),
  MAX_FARE(6),
  busFare(2.0),
  subwayFare(0.5),
  weeklyPass(40),
  monthlyPass(100),
  newCard(9.99),
  cardInitValue(19);

  private double fare;

  Fares(double fare) {
    this.fare = fare;
  }

  public double getFare() {
    return fare;
  }

  /**
   * Returns a string representation of all fares listed in Fares
   */
  public String getFareInfo() {
    return "Fine for abnormal activity: $"
            + FINE.getFare()
            + System.lineSeparator()
            + "Max fare in a 2-hr continuous trip: $"
            + MAX_FARE.getFare()
            + System.lineSeparator()
            + "Fare for a single bus trip: $"
            + busFare.getFare()
            + System.lineSeparator()
            + "Fare for a station on subway: $"
            + subwayFare.getFare()
            + System.lineSeparator()
            + "Fare for a weekly pass: $"
            + weeklyPass.getFare()
            + System.lineSeparator()
            + "Fare for a monthly pass: $"
            + monthlyPass.getFare()
            + System.lineSeparator()
            + "Cost for a new card: $"
            + String.valueOf(newCard.getFare())
            + System.lineSeparator()
            + "Initial balance to load: $"
            + String.valueOf(cardInitValue.getFare())
            + System.lineSeparator();
  }

  public void setFare(double fare) {
    this.fare = fare;
  }
}
