package TransitSystemClasses;

import TransitSystemExceptions.TripCanNotContinueException;
import TransitSystemExceptions.TripEnRouteException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class Trip implements Comparable<Trip>, Serializable {
  // The first SubTrip
  private SubTrip start;
  // The last SubTrip
  private SubTrip end;

  Trip(SubTrip start) {
    this.start = this.end = start;
  }

  /**
   * Turns an array of trips into a readable String representation.
   *
   * @param trips an array of trips need to be turned into enumerated string
   * @return an enumerated string representation of all the three trips
   */
  static String getEnumeratedTrips(Trip[] trips) {
    StringBuilder result = new StringBuilder();
    int num = trips.length;
    if (num == 0) {
      result.append("There is no trip.");
    } else {
      result.append("------------------------------------------------------------");
      result.append(System.lineSeparator());
      for (int i = 0; i < num; i++) {
        if (trips[i] == null) {
          // End the loop if there is already nothing else to show.
          break;
        }
        result.append(i + 1);
        result.append(".");
        result.append(trips[i].toString());
        result.append(System.lineSeparator());
      }
    }
    result.append("------------------------------------------------------------");
    return result.toString();
  }

  /**
   * Appends a new SubTrip to the end of this Trip if can be counted as a continuous trip
   *
   * @param connection the next SubTrip to be appended
   * @throws TripCanNotContinueException if connect is not qualified as a continuous trip
   * @throws TripEnRouteException        if the last sub trip of this Trip has not finished
   */
  void appendTrip(SubTrip connection) throws TripCanNotContinueException, TripEnRouteException {
    if (!canContinue(connection.getTimeStart(), connection.getStartPoint())) {
      throw new TripCanNotContinueException("TransitSystemClasses.Trip can't continue.");
    }
    this.end.setConnection(connection);
    this.end = this.end.getConnection();
  }

  /**
   * Returns whether this trip can continue at the given time and station
   *
   * @param time    the time to check
   * @param station the name of the station to check
   * @return whether this trip can continue at the given time and station
   * @throws TripEnRouteException if the last sub trip of this trip is still en route
   */
  private boolean canContinue(LocalDateTime time, String station) throws TripEnRouteException {
    if (this.end.isEnRoute()) {
      throw new TripEnRouteException("Last trip has not ended yet!");
    }
    if (!station.equals(end.getEndPoint())) {
      return false;
    }
    long timeDuration = this.start.getTimeStart().until(time, ChronoUnit.MINUTES);
    return timeDuration <= 120;
  }

  /**
   * Returns the total amount of fare of this trip.
   */
  float getCurrentTotalFare() {
    SubTrip curNode = start;
    float sum = 0;
    while (curNode != null) {
      sum += curNode.getFare();
      curNode = curNode.getConnection();
    }
    return sum;
  }

  /**
   * Returns the total number of bus stops reached during this trip.
   */
  int totalBusStop() {
    SubTrip cur = this.start;
    int result = 0;
    while (cur != null) {
      if (cur instanceof BusSubTrip) {
        result += cur.getNumOfStops();
      }
      cur = cur.getConnection();
    }
    return result;
  }

  /**
   * Returns the total number of subway stations reached during this trip.
   */
  int totalSubwayStation() {
    SubTrip cur = this.start;
    int result = 0;
    while (cur != null) {
      if (cur instanceof SubwaySubTrip) {
        result += cur.getNumOfStops();
      }
      cur = cur.getConnection();
    }
    return result;
  }

  /**
   * Compares Trips based on the start time of each Trip.
   *
   * @return 1 if this Trip starts later than other, 0 if the same, -1 other wise.
   */
  @Override
  public int compareTo(Trip other) {
    if (this.start.getTimeStart().until(other.start.getTimeStart(), ChronoUnit.MINUTES) > 0) {
      return -1;
    } else if (this.start.getTimeStart().until(other.start.getTimeStart(), ChronoUnit.MINUTES)
            == 0) {
      return 0;
    }
    return 1;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    SubTrip curNode = this.start;
    while (curNode != null) {
      result.append(curNode.toString());
      result.append(System.lineSeparator());
      curNode = curNode.getConnection();
    }
    float thisFare = getCurrentTotalFare();
    result.append("Total Fare: ");
    result.append(thisFare);
    return result.toString();
  }

  SubTrip getStart() {
    return start;
  }

  SubTrip getEnd() {
    return end;
  }
}
