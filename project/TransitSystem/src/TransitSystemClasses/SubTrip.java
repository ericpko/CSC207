package TransitSystemClasses;

import TransitSystemExceptions.ExitNotSameRouteException;
import TransitSystemExceptions.TripEnRouteException;
import TransitSystemExceptions.TripNotEnRouteException;

import java.io.Serializable;
import java.time.LocalDateTime;

abstract class SubTrip implements Serializable {

  // number of stations / stops reached
  int numOfStops = 0;
  private LocalDateTime timeStart;
  private LocalDateTime timeEnd;
  private String startPoint;
  private String endPoint;
  // the realFare charged on card of this trip
  private double fare = 0;
  // the SubTrip following this SubTrip
  private SubTrip connection;
  // whether this SubTrip has finished
  private boolean enRoute = true;
  // the route of the starting station / stop of this SubTrip
  private String routeName;

  SubTrip(LocalDateTime timeStart, String startPoint, String routeName) {
    this.timeStart = timeStart;
    this.startPoint = startPoint;
    this.routeName = routeName;
  }

  /**
   * Sets the timeEnd and endPoint of this TransitSystemClasses.SubTrip and marks this trip as over
   * by setting enRoute to false.
   *
   * @throws ExitNotSameRouteException, throws this Exception when exit from a different route
   */
  void endTrip(LocalDateTime timeEnd, String endPoint, String route)
          throws ExitNotSameRouteException {
    enRoute = false;
    this.timeEnd = timeEnd;
    this.endPoint = endPoint;
  }

  /**
   * Sets the timeEnd and endPoint of this TransitSystemClasses.SubTrip and marks this trip as over
   * by setting enRoute to false, set the fare of this trip appropriately.
   *
   * @throws TripNotEnRouteException throws the Exception when the trip has already finished
   */
  void finishTrip(LocalDateTime timeEnd, String endPoint, String route)
          throws TripNotEnRouteException, ExitNotSameRouteException {
    if (!enRoute) {
      throw new TripNotEnRouteException("Exit without entry!");
    }
    endTrip(timeEnd, endPoint, route);
    this.setNumOfStops();
  }

  /**
   * Returns the estimated fare of this trip according to the charging scheme.
   *
   * @return the estimated fare of this trip.
   * @throws TripEnRouteException if the TransitSystemClasses.Trip is not over yet.
   */
  abstract float calculateFare() throws TripEnRouteException;

  /**
   * Returns the route of this TransitSystemClasses.SubTrip.
   */
  abstract String[] getRoute();

  @Override
  public String toString() {
    try {
      return "Started at "
              + getTimeStart().toString()
              + " at "
              + getStartPoint()
              + System.lineSeparator()
              + " and end at "
              + getTimeEnd().toString()
              + " at "
              + getEndPoint()
              + ".";
    } catch (TripEnRouteException e) {
      return "Started at "
              + getStartPoint()
              + " at "
              + getTimeStart().toString()
              + " This sub trip is en route.";
    }
  }

  // Setters and getters.
  String getRouteName() {
    return routeName;
  }

  String getStartPoint() {
    return startPoint;
  }

  String getEndPoint() {
    return endPoint;
  }

  int getNumOfStops() {
    return this.numOfStops;
  }

  abstract void setNumOfStops();

  SubTrip getConnection() {
    return connection;
  }

  void setConnection(SubTrip connection) throws TripEnRouteException {
    if (isEnRoute()) {
      throw new TripEnRouteException("This trip is still en route.");
    }
    this.connection = connection;
  }

  double getFare() {
    return fare;
  }

  void setFare(double fare) {
    this.fare = fare;
  }

  boolean isEnRoute() {
    return enRoute;
  }

  LocalDateTime getTimeStart() {
    return timeStart;
  }

  private LocalDateTime getTimeEnd() throws TripEnRouteException {
    if (isEnRoute()) {
      throw new TripEnRouteException("This trip is still en route.");
    }
    return timeEnd;
  }
}
