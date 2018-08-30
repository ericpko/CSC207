package TransitSystemClasses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class that represents a subway station or a bus stop.
 */
class Station {
  // All routes that have this Station
  private ArrayList<String> routes = new ArrayList<>();
  // The name of this subway station or bus stop
  private String name;
  // A HashMap with the adjacent stations or stops of this station as key and their corresponding
  // route as value
  private HashMap<Station, String> next;

  Station(String route, String name) {
    this.routes.add(route);
    this.name = name;
    next = new HashMap<>();
  }

  String getName() {
    return name;
  }

  void addRoute(String route) {
    this.routes.add(route);
  }

  ArrayList<String> getRoute() {
    return routes;
  }

  void addNext(Station station, String line) {
    next.put(station, line);
  }

  HashMap<Station, String> getNext() {
    return next;
  }
}
