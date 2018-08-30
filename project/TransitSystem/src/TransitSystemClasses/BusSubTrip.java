package TransitSystemClasses;

import TransitSystemExceptions.ExitNotSameRouteException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

public class BusSubTrip extends SubTrip {

  private static final float FARE_PER_TRIP = (float) Fares.busFare.getFare();
  private static HashMap<String, String[]> busRoutes = new HashMap<>();

  BusSubTrip(LocalDateTime timeStart, String startPoint, String route) {
    super(timeStart, startPoint, route);
  }

  public static HashMap<String, String[]> getBusRoutes() {
    return BusSubTrip.busRoutes;
  }

  public static void setBusRoutes(HashMap<String, String[]> busRoutes) {
    BusSubTrip.busRoutes = busRoutes;
  }

  @Override
  float calculateFare() {
    return FARE_PER_TRIP;
  }

  @Override
  String[] getRoute() {
    return busRoutes.get(getRouteName());
  }

  @Override
  public String toString() {
    return "BusTrip " + super.toString();
  }

  @Override
  void endTrip(LocalDateTime timeEnd, String endPoint, String endRoute)
          throws ExitNotSameRouteException {
    if (!this.getRouteName().equals(endRoute)) {
      throw new ExitNotSameRouteException("Entry and exit on different route!");
    }
    super.endTrip(timeEnd, endPoint, endRoute);
  }

  @Override
  void setNumOfStops() {
    String[] routes = getRoute();
    int startIndex = Arrays.asList(routes).indexOf(getStartPoint());
    int endIndex = Arrays.asList(routes).indexOf(getEndPoint());
    this.numOfStops = Math.abs(startIndex - endIndex);
  }
}
