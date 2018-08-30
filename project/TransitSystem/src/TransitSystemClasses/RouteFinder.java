package TransitSystemClasses;

import TransitSystemExceptions.RouteNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteFinder {

  private HashMap<String, Station> stations;

  public RouteFinder(String type) {
    this.stations = this.stationGenerator(type);
  }

  /**
   * Converts stations/stops in bus routes or subway routes to Station objects and puts in a
   * HashMap. This is a helper function for getShortestPathLength.
   *
   * @param type the type of path to find, "Subway" represents a path only contains subway stations,
   *             "All" represents a path that contains all stops and stations
   * @return A HashMap containing Stations
   */
  private HashMap<String, Station> stationGenerator(String type) {
    HashMap<String, String[]> allRoutes = new HashMap<>();

    // gets all routes needed to find a path
    if (type.equals("Subway")) {
      SubwaySubTrip.getSubwayRoutes()
              .keySet()
              .forEach(x -> allRoutes.put(x, SubwaySubTrip.getSubwayRoutes().get(x)));
    } else if (type.equals("All")) {
      SubwaySubTrip.getSubwayRoutes()
              .keySet()
              .forEach(x -> allRoutes.put(x, SubwaySubTrip.getSubwayRoutes().get(x)));
      BusSubTrip.getBusRoutes()
              .keySet()
              .forEach(x -> allRoutes.put(x, BusSubTrip.getBusRoutes().get(x)));
    }

    // constructs Stations
    HashMap<String, Station> stations = new HashMap<>();
    for (String route : allRoutes.keySet()) {
      for (String newStation : allRoutes.get(route)) {
        if (!stations.keySet().contains(newStation)) {
          Station station = new Station(route, newStation);
          stations.put(newStation, station);
        } else {
          Station station = stations.get(newStation);
          station.addRoute(route);
        }
      }
    }
    // set "next" for each Station in the HashMap
    for (String routeName : allRoutes.keySet()) {
      String[] originRoute = allRoutes.get(routeName);
      for (int k = 0; k < originRoute.length; k++) {
        Station station = stations.get(originRoute[k]);
        if (k < originRoute.length - 1) {
          station.addNext(stations.get(originRoute[k + 1]), routeName);
        }
        if (k > 0) {
          station.addNext(stations.get(originRoute[k - 1]), routeName);
        }
      }
    }
    return stations;
  }

  /**
   * Returns the shortest length between start and end subway stations, return 12 if there is not
   * route between these two stations
   *
   * @param start the name of the start station
   * @param end   the name of the end station
   * @return the shortest length between start and end
   */
  @SuppressWarnings("unchecked")
  public int getShortestPathLength(String start, String end) {
    if (start.equals(end)) {
      return 0;
    }
    int len = 0;
    boolean hasEnd = false;
    HashMap<String, Station> toProcess = (HashMap<String, Station>) this.stations.clone();
    ArrayList<String> toExpand = new ArrayList<>();
    ArrayList<String> newlyAdded = new ArrayList<>();
    toProcess.remove(start);
    toExpand.add(start);

    while (!toProcess.isEmpty() && !hasEnd) {
      for (String s : toExpand) {
        Station startPoint = stations.get(s);

        // add adjacent stations of new startPoint
        if (startPoint.getNext().size() != 0) {
          for (Station nxt : startPoint.getNext().keySet()) {
            if (toProcess.keySet().contains(nxt.getName())) {
              newlyAdded.add(nxt.getName());
              toProcess.remove(nxt.getName());
              if (nxt.getName().equals(end)) {
                hasEnd = true;
              }
            }
          }
        }
      }
      // update
      toExpand = newlyAdded;
      if (toExpand.isEmpty() && !hasEnd) {
        return 12;
      }
      newlyAdded = new ArrayList<>();
      len++;
    }
    return len;
  }

  // A helper function for getShortestPath, returns an ArrayList with the first item being the
  // shortest path length and the second item being a HashMap that represents a tree that contains
  // the shortest path between two stations
  @SuppressWarnings("unchecked")
  private ArrayList getTreePath(String start, String end) throws RouteNotFoundException {
    ArrayList treePath = new ArrayList();
    int len = 0;
    boolean hasFound = false;
    HashMap<String, Integer> Processed = new HashMap<>();
    ArrayList<String> toExpand = new ArrayList<>();
    ArrayList<String> newlyAdded = new ArrayList<>();
    Processed.put(end, 0);
    toExpand.add(end);

    // Obtains a tree that is rooted at "end" and has "start" as its leaf
    while (!hasFound && !(Processed.size() == stations.size())) {
      for (String s : toExpand) {
        Station endPoint = stations.get(s);

        // Adds adjacent stations of new startPoint
        if (endPoint.getNext().size() != 0) {
          for (Station nxt : endPoint.getNext().keySet()) {
            if (!Processed.keySet().contains(nxt.getName())) {
              newlyAdded.add(nxt.getName());
              Processed.put(nxt.getName(), len + 1);
              if (nxt.getName().equals(start)) {
                hasFound = true;
              }
            }
          }
        }
      }
      toExpand = newlyAdded;

      // throws an exception if there is no path between start and end station
      if (toExpand.isEmpty() && !hasFound) {
        throw new RouteNotFoundException(
                String.format("There doesn't exist route between %s and %s!", start, end));
      }
      newlyAdded = new ArrayList<>();
      len++; // update length of the path
    }
    treePath.add(len);
    treePath.add(Processed);
    return treePath;
  }

  /**
   * Returns an array of the stations on the shortest path between start and end
   *
   * @param start the start point of the path to find
   * @param end   the end point of the path to find
   * @return an array of Stations on the shortest path
   * @throws RouteNotFoundException if there does not exist a path
   */
  @SuppressWarnings("unchecked")
  private String[] getShortestPath(String start, String end) throws RouteNotFoundException {
    int len = (int) getTreePath(start, end).get(0);
    HashMap<String, Integer> tree = (HashMap<String, Integer>) getTreePath(start, end).get(1);
    String[] path = new String[len + 1];
    path[0] = start;
    Station startPoint = stations.get(start);
    for (int i = len - 1; i >= 0; i--) {
      String station;
      if (startPoint.getNext().size() != 0) {
        for (Station nxt : startPoint.getNext().keySet()) {
          station = nxt.getName();
          if (tree.keySet().contains(station) && tree.get(station) == i) {
            path[len - i] = station;
            startPoint = stations.get(station);
            break;
          }
        }
      }
    }
    return path;
  }

  /**
   * * A helper function for getRoutePlan that matches stationNames with routes
   *
   * @param start the start point of the path to find
   * @param end   the end point of the path to find
   * @return an array that matches stations with routes, [i][0] is station, [i][1] is route
   */
  private String[][] getOptimalPath(String start, String end) throws RouteNotFoundException {
    String[] path = getShortestPath(start, end);
    int length = path.length;

    // initialize target path
    String[][] target = new String[path.length][2];
    for (int i = 0; i < length; i++) {
      target[i][0] = path[i];
    }

    Station startPoint = stations.get(path[0]);
    for (Station nextStation : startPoint.getNext().keySet()) {
      if (nextStation.getName().equals(target[1][0])) {
        String route = startPoint.getNext().get(nextStation);
        target[0][1] = route;
        target[1][1] = route;
        break;
      }
    }

    for (int i = 2; i < length; i++) {
      if (target[i][1] == null) {
        Station prevStation = stations.get(target[i - 1][0]);
        for (Station nextStation : prevStation.getNext().keySet()) {
          if (nextStation.getName().equals(target[i][0])) {
            String route = prevStation.getNext().get(nextStation);
            target[i][1] = route;
            break;
          }
        }
      }
    }
    return target;
  }

  /**
   * Return a string that gives users direction
   *
   * @param start the start point of the path to find
   * @param end   the end point of the path to find
   * @return a string representation of the shortest path between start and end station
   * @throws RouteNotFoundException if there does not exist a path between start and end stations
   */
  public String getRoutePlan(String start, String end) throws RouteNotFoundException {
    String[][] target = getOptimalPath(start, end);
    StringBuilder result =
            new StringBuilder(
                    "Start at "
                            + target[0][0]
                            + System.lineSeparator()
                            + "Take "
                            + target[0][1]
                            + System.lineSeparator()
                            + target[0][0]);
    for (int i = 0; i < target.length - 1; i++) {
      if (!target[i][1].equals(target[i + 1][1])) {
        result
                .append(System.lineSeparator())
                .append("Transfer to ")
                .append(target[i + 1][1])
                .append(System.lineSeparator())
                .append(target[i][0]);
      }
      result.append(" --> ").append(target[i + 1][0]);
    }
    return result + System.lineSeparator();
  }
}
