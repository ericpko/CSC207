package TransitSystemClasses;

import TransitSystemExceptions.ExitNotSameRouteException;
import TransitSystemExceptions.TripEnRouteException;

import java.time.LocalDateTime;
import java.util.HashMap;

public class SubwaySubTrip extends SubTrip {

  private static final float FARE_PER_STATION = (float) Fares.subwayFare.getFare();
  private static HashMap<String, String[]> subwayRoutes = new HashMap<>();

  SubwaySubTrip(LocalDateTime timeStart, String startPoint, String route) {
    super(timeStart, startPoint, route);
  }

  public static HashMap<String, String[]> getSubwayRoutes() {
    return SubwaySubTrip.subwayRoutes;
  }

  public static void setSubwayRoutes(HashMap<String, String[]> subwayRoutes) {
    SubwaySubTrip.subwayRoutes = subwayRoutes;
  }

  /**
   * Returns the fare of this SubTrip based on the start and the end point of the SubwaySubTrip.
   * Note that the shortest path is calculated.
   *
   * @throws TripEnRouteException if the route is still En Route.
   */
  @Override
  float calculateFare() throws TripEnRouteException {
    if (isEnRoute()) {
      throw new TripEnRouteException("This TransitSystemClasses.Trip is still en route.");
    }
    RouteFinder routeFinder = new RouteFinder("Subway");
    int pathLength = routeFinder.getShortestPathLength(getStartPoint(), getEndPoint());
    return FARE_PER_STATION * pathLength;
  }

  @Override
  public String toString() {
    return "SubwayTrip " + super.toString();
  }

  /**
   * Ends this SubwaySubTrip at the given timeEnd and endPoint
   *
   * @throws ExitNotSameRouteException is thrown when the endPoint is not a subway station.
   */
  @Override
  void endTrip(LocalDateTime timeEnd, String endPoint, String route)
          throws ExitNotSameRouteException {
    if (!subwayRoutes.keySet().contains(route)) {
      throw new ExitNotSameRouteException("Not exit from subway station!");
    }
    super.endTrip(timeEnd, endPoint, route);
  }

  /**
   * Sets the number of stops of this SubwaySubTrip to the minimum possible number of subway
   * stations from the start point to end point of this SubWaySubTrip.
   */
  @Override
  void setNumOfStops() {
    RouteFinder routeFinder = new RouteFinder("Subway");
    super.numOfStops = routeFinder.getShortestPathLength(getStartPoint(), getEndPoint());
  }

  @Override
  String[] getRoute() {
    return subwayRoutes.get(getRouteName());
  }
}
