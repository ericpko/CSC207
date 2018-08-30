package TransitSystemClasses;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Transit Pass that can be loaded onto a Card
 */
class TransitPass implements Serializable {
  private LocalDate startTime;
  private LocalDate endTime;
  private double price;
  private String type;

  private TransitPass(LocalDate startTime, LocalDate endTime, String type, double price) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.price = price;
    this.type = type;
  }

  /**
   * Factory method of this transit pass.
   *
   * @param startTime the start time of the transit pass
   * @param duration  the duration of this transit pass
   * @return a transit pass started to be valid at startTime and has the given duration
   */
  static TransitPass makeTransitPass(LocalDate startTime, int duration) {
    LocalDate endTime = startTime.plusDays(duration);
    double price;
    String type;
    if (duration == 7) {
      price = Fares.weeklyPass.getFare();
      type = "weekly transit pass";
    } else {
      price = Fares.monthlyPass.getFare();
      type = "monthly transit pass";
    }
    return new TransitPass(startTime, endTime, type, price);
  }

  /**
   * Returns whether this transit pass is valid at given date.
   *
   * @param currentDate date to determine
   * @return whether the pass is valid.
   */
  boolean isValid(LocalDate currentDate) {
    return (currentDate.isBefore(endTime) && currentDate.isAfter(startTime))
            || currentDate.equals(startTime);
  }

  @Override
  public String toString() {
    return String.format(
            "A %s days pass valid from %s to %s is on this card",
            startTime.until(endTime).getDays(), startTime.toString(), (endTime.minusDays(1)).toString());
  }

  double getPrice() {
    return price;
  }

  String getType() {
    return type;
  }
}
