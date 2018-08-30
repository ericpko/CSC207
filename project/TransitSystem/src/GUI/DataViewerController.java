package GUI;

import TransitSystemClasses.DataGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class DataViewerController {

  private DataGenerator dataSource = new DataGenerator();

  @FXML private BorderPane mainPane;

  @FXML private CategoryAxis xAxisBarGraph;
  @FXML private NumberAxis yAxisBarGraph;
  @FXML private BarChart<String, Double> barGraph;
  @FXML private PieChart pieChartGraph;
  @FXML private TextArea infoTextArea;

  @FXML private DatePicker datePicker1;
  @FXML private DatePicker datePicker2;
  @FXML private ToggleGroup dataSelectionToggleGroup;
  @FXML private RadioButton dailyRevRadioButton;
  @FXML private RadioButton monthlyRevRadioButton;
  @FXML private RadioButton busVsSubwayRadioButton;
  @FXML private RadioButton revenuePerMonthRadioButton;
  @FXML private RadioButton trafficPerMonthRadioButton;

  /** Shows data when dataSelectionToggleGroup and dataPicker1 are selected */
  public void dataSelectionRadioButtonSelected() {
    if (datePicker1.getValue() != null) {
      RadioButton dataSelection = (RadioButton) dataSelectionToggleGroup.getSelectedToggle();
      if (dataSelection == dailyRevRadioButton) {
        getDailyRevenueReport();
      } else if (dataSelection == monthlyRevRadioButton) {
        getMonthlyRevenueReport();
      } else if (dataSelection == busVsSubwayRadioButton) {
        getBusVsSubway();
      } else if (dataSelection == revenuePerMonthRadioButton) {
        getRevenuePerMonth();
      } else if (dataSelection == trafficPerMonthRadioButton) {
        getTrafficPerMonth();
      }
    }
  }

  /** Displays stops/stations reached if busVsSubwayRadioButton is selected */
  private void getBusVsSubway() {
    LocalDate date1 = datePicker1.getValue();
    LocalDate date2;
    if (datePicker2.getValue() != null) {
      date2 = datePicker2.getValue();
      if (date2.isBefore(date1)) {
        date2 = date1;
      }
    } else {
      date2 = date1;
    }
    List<Integer> stopsReached = getBusVsSubwayStopsData(date1, date2);
    makeBusVsSubwayPieChart(stopsReached);
    makeBusVsSubwayBarChart(stopsReached, date1, date2);
    reportBusVsSubwayIntoText(stopsReached, date1, date2);
  }

  /** Helper function for getBusVsSubway, displays data through PieChart */
  private void makeBusVsSubwayPieChart(List<Integer> stopsReached) {
    pieChartGraph.getData().clear();
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    pieChartData.add(new PieChart.Data("Bus Stops", stopsReached.get(0)));
    pieChartData.add(new PieChart.Data("Subway stations", stopsReached.get(1)));
    pieChartGraph.dataProperty().setValue(pieChartData);
    pieChartGraph.setTitle("Bus stops vs Subway stations reached");

    final Label caption = new Label("");
    caption.setTextFill(Color.DARKORANGE);
    caption.setStyle("-fx-font: 24 arial;");

    setPieChartData(caption);
  }

  /** Helper function for getBusVsSubway, displays data through BarChart */
  private void makeBusVsSubwayBarChart(
      List<Integer> stopsReached, LocalDate date1, LocalDate date2) {
    barGraph.getData().clear();
    xAxisBarGraph.getCategories().clear();
    ObservableList<String> xAxisCategories = FXCollections.observableArrayList();
    xAxisBarGraph.setCategories(xAxisCategories);
    xAxisCategories.add(String.format("%s to %s", date1, date2));
    yAxisBarGraph = new NumberAxis();
    barGraph.setTitle("Bus stops vs Subway stations reached");
    xAxisBarGraph.setLabel("Transportation");
    yAxisBarGraph.setLabel("Number of stops/stations reached");

    XYChart.Series<String, Double> series1 = new XYChart.Series<>();
    series1.setName("Bus");
    XYChart.Series<String, Double> series2 = new XYChart.Series<>();
    series2.setName("Subway");

    series1
        .getData()
        .add(
            new XYChart.Data<>(
                String.format("%s to %s", date1, date2), Double.valueOf(stopsReached.get(0))));
    series2
        .getData()
        .add(
            new XYChart.Data<>(
                String.format("%s to %s", date1, date2), Double.valueOf(stopsReached.get(1))));
    barGraph.getData().addAll(series1, series2);
  }

  /** Helper function for getBusVsSubway, displays data in Info TextArea */
  private void reportBusVsSubwayIntoText(
      List<Integer> stopsReached, LocalDate date1, LocalDate date2) {
    String msg =
        String.format(
            "From %s to %s, "
                + "the total number of bus stops reached is %d%n"
                + "the total number of subway stations reached is %d%n",
            date1, date2, stopsReached.get(0), stopsReached.get(1));
    infoTextArea.setText(msg);
  }

  private List<Integer> getBusVsSubwayStopsData(LocalDate date1, LocalDate date2) {
    List<Integer> stopsReached = new ArrayList<>();
    long duration = DAYS.between(date1, date2);
    int numberOfBusStopsReached = 0;
    int numberOfSubwayStationsReached = 0;
    for (int i = 0; i <= duration; i++) {
      numberOfBusStopsReached += dataSource.getDailyBusStopsReached(date1.plusDays(i));
      numberOfSubwayStationsReached += dataSource.getDailySubwayStationsReached(date1.plusDays(i));
    }
    stopsReached.add(numberOfBusStopsReached);
    stopsReached.add(numberOfSubwayStationsReached);
    return stopsReached;
  }

  /** Displays yearly revenue report */
  private void getRevenuePerMonth() {
    Map<String, Double> revenuePerMonthMap = getRevenuePerMonthHashMap(); // collect data once
    makeRevenuePerMonthPieChart(revenuePerMonthMap);
    makeRevenuePerMonthBarChart(revenuePerMonthMap);
    reportRevenuePerMonthInfoText(revenuePerMonthMap);
  }

  /** Helper function for getRevenuePerMonth, displays data through PieChart */
  private void makeRevenuePerMonthPieChart(Map<String, Double> revenuePerMonthMap) {
    pieChartGraph.getData().clear();
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    for (int i = 1; i <= 12; i++) {
      String month = Month.of(i).getDisplayName(TextStyle.FULL, Locale.CANADA);
      PieChart.Data dataEntry = new PieChart.Data(month, revenuePerMonthMap.get(month));
      pieChartData.add(dataEntry);
    }

    pieChartGraph.dataProperty().setValue(pieChartData);
    pieChartGraph.setTitle("Revenue Per Month");

    final Label caption = new Label("");
    caption.setTextFill(Color.DARKORANGE);
    caption.setStyle("-fx-font: 24 arial;");

    setPieChartData(caption);
  }

  // A helper function for all pie chart display
  private void setPieChartData(Label caption) {
    for (final PieChart.Data data : pieChartGraph.getData()) {
      data.getNode()
          .addEventHandler(
              MouseEvent.MOUSE_PRESSED,
              e -> {
                caption.setTranslateX(e.getSceneX());
                caption.setTranslateY(e.getSceneY());
                caption.setText(String.valueOf(data.getPieValue()) + "%");
              });
    }
  }

  /** Helper function for getRevenuePerMonth, displays data through BarChart */
  private void makeRevenuePerMonthBarChart(Map<String, Double> revenuePerMonth) {
    barGraph.getData().clear();
    xAxisBarGraph.getCategories().clear();
    ObservableList<String> xAxisCategories = FXCollections.observableArrayList();
    xAxisBarGraph.setCategories(xAxisCategories);
    yAxisBarGraph = new NumberAxis();
    barGraph.setTitle("Revenue Per Month");
    xAxisBarGraph.setLabel("Months");
    yAxisBarGraph.setLabel("Revenue");

    XYChart.Series<String, Double> series = new XYChart.Series<>();
    series.setName("Revenue");

    for (int i = 1; i <= 12; i++) {
      String month = Month.of(i).getDisplayName(TextStyle.FULL, Locale.CANADA);
      xAxisCategories.add(month);
      XYChart.Data<String, Double> dataEntry =
          new XYChart.Data<>(month, revenuePerMonth.get(month));
      series.getData().add(dataEntry);
    }

    barGraph.getData().add(series);
  }

  /** Helper function for getRevenuePerMonth, displays data in Info TextArea */
  private void reportRevenuePerMonthInfoText(Map<String, Double> revenuePerMonthMap) {
    StringBuilder msg = new StringBuilder();
    for (int i = 1; i <= 12; i++) {
      String month = Month.of(i).getDisplayName(TextStyle.FULL, Locale.CANADA);
      double revenue = revenuePerMonthMap.get(month);
      msg.append(String.format("The total revenue for the month of %s is: %.2f%n", month, revenue));
    }
    infoTextArea.setText(msg.toString());
  }

  /** Helper function for getRevenuePerMonth, obtains data needed from dataSource */
  private Map<String, Double> getRevenuePerMonthHashMap() {
    HashMap<String, Double> revenuePerMonthMap = new HashMap<>();
    int year = datePicker1.getValue().getYear();
    for (int i = 1; i <= 12; i++) {
      String month = Month.of(i).getDisplayName(TextStyle.FULL, Locale.CANADA);
      double fareRevenue = dataSource.getMonthlyFareRevenue(year, i);
      double passRevenue = dataSource.getMonthlyPassRevenue(year, i);
      double cardRevenue = dataSource.getMonthlyCardRevenue(year, i);
      revenuePerMonthMap.put(month, fareRevenue + passRevenue + cardRevenue);
    }
    return revenuePerMonthMap;
  }

  /** Display yearly report for stops / stations reached */
  private void getTrafficPerMonth() {
    int year = datePicker1.getValue().getYear();
    ArrayList<Integer> bus = new ArrayList<>();
    ArrayList<Integer> subway = new ArrayList<>();
    for (int i = 1; i <= 12; i++) {
      bus.add(dataSource.getMonthlyBusStopsReached(year, i));
      subway.add(dataSource.getMonthlySubwayStationsReached(year, i));
    }
    StringBuilder msg = new StringBuilder();
    for (int i = 1; i <= 12; i++) {
      String month = Month.of(i).getDisplayName(TextStyle.FULL, Locale.CANADA);
      msg.append(
          String.format(
              "The bus stops reached for the month of %s is: %s%n the subway stations reached is %s%n",
              month, bus.get(i - 1), subway.get(i - 1)));
    }
    infoTextArea.setText(msg.toString());

    makeTrafficPerMonthBarChart(bus, subway);
    makeTrafficPerMonthPieChart(bus, subway);
  }

  /** Helper function for getTrafficPerMonth, displays data through BarChart */
  private void makeTrafficPerMonthBarChart(ArrayList<Integer> bus, ArrayList<Integer> subway) {
    barGraph.getData().clear();
    xAxisBarGraph.getCategories().clear();
    ObservableList<String> xAxisCategories = FXCollections.observableArrayList();
    xAxisBarGraph.setCategories(xAxisCategories);
    yAxisBarGraph = new NumberAxis();
    barGraph.setTitle("Traffic Per Month");
    xAxisBarGraph.setLabel("Months");
    yAxisBarGraph.setLabel("Stops / Stations Reached");

    XYChart.Series<String, Double> series1 = new XYChart.Series<>();
    series1.setName("Bus Stops");

    XYChart.Series<String, Double> series2 = new XYChart.Series<>();
    series2.setName("Subway Stations");


    for (int i = 1; i <= 12; i++) {
      String month = Month.of(i).getDisplayName(TextStyle.FULL, Locale.CANADA);
      xAxisCategories.add(month);
      double stop = bus.get(i - 1);
      double station = subway.get(i - 1);
      XYChart.Data<String, Double> dataEntry1 = new XYChart.Data<>(month, stop);
      series1.getData().add(dataEntry1);
      XYChart.Data<String, Double> dataEntry2 = new XYChart.Data<>(month, station);
      series2.getData().add(dataEntry2);
    }

    barGraph.getData().addAll(series1, series2);
  }

  /** Helper function for getTrafficPerMonth, displays data through PieChart */
  private void makeTrafficPerMonthPieChart(ArrayList<Integer> bus, ArrayList<Integer> subway) {
    pieChartGraph.getData().clear();
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    for (int i = 1; i <= 12; i++) {
      String month = Month.of(i).getDisplayName(TextStyle.FULL, Locale.CANADA);
      double amount = bus.get(i - 1) + subway.get(i - 1);
      PieChart.Data dataEntry = new PieChart.Data(month, amount);
      pieChartData.add(dataEntry);
    }

    pieChartGraph.dataProperty().setValue(pieChartData);
    pieChartGraph.setTitle("Traffic Per Month");

    final Label caption = new Label("");
    caption.setTextFill(Color.DARKORANGE);
    caption.setStyle("-fx-font: 24 arial;");

    setPieChartData(caption);
  }

  /** Display revenue of the date obtained from datePicker1 */
  private void getDailyRevenueReport() {
    LocalDate date = datePicker1.getValue();
    List<Double> revenueData = getDailyRevenueData(date);
    String msg =
        String.format(
            "The fare revenue of %s is %.2f %n"
                + "transit pass revenue is %.2f %n"
                + "card revenue is %.2f %n",
            date.toString(), revenueData.get(0), revenueData.get(1), revenueData.get(2));
    infoTextArea.setText(msg);
    showBarChartForRevenue(revenueData, date);
    showPieChartForRevenue(revenueData);
  }

  /** Display revenue of the month obtained from datePicker1 */
  private void getMonthlyRevenueReport() {
    LocalDate date = datePicker1.getValue();
    List<Double> revenueData = getMonthlyRevenueData(date);
    String msg =
        String.format(
            "The fare revenue of %s is %.2f %n"
                + "transit pass revenue is  %.2f %n"
                + "card revenue is %.2f %n",
            date.getMonth().getDisplayName(TextStyle.FULL, Locale.CANADA),
            revenueData.get(0),
            revenueData.get(1),
            revenueData.get(2));
    infoTextArea.setText(msg);
    showBarChartForRevenue(revenueData, date);
    showPieChartForRevenue(revenueData);
  }

  /** Helper function for getDailyRevenueReport, return a list of different types of revenue */
  private List<Double> getDailyRevenueData(LocalDate date) {
    List<Double> dailyRevenue = new ArrayList<>();
    double fareRevenue = dataSource.getDailyFareRevenue(date);
    double passRevenue = dataSource.getDailyPassRevenue(date);
    double cardRevenue = dataSource.getDailyCardRevenue(date);
    dailyRevenue.add(fareRevenue);
    dailyRevenue.add(passRevenue);
    dailyRevenue.add(cardRevenue);
    return dailyRevenue;
  }

  /** Helper function for getMonthlyRevenueReport, return a list of different types of revenue */
  private List<Double> getMonthlyRevenueData(LocalDate date) {
    List<Double> monthlyRevenue = new ArrayList<>();
    double fareRevenue =
        dataSource.getMonthlyFareRevenue(date.getYear(), date.getMonth().getValue());
    double passRevenue =
        dataSource.getMonthlyPassRevenue(date.getYear(), date.getMonth().getValue());
    double cardRevenue = dataSource.getMonthlyCardRevenue(date.getYear(), date.getMonthValue());
    monthlyRevenue.add(fareRevenue);
    monthlyRevenue.add(passRevenue);
    monthlyRevenue.add(cardRevenue);
    return monthlyRevenue;
  }

  /**
   * Helper function for getDaily(Monthly)RevenueReport, display BarChart for different types of
   * revenue
   */
  private void showBarChartForRevenue(List<Double> revenueData, LocalDate date) {

    barGraph.getData().clear();
    xAxisBarGraph.getCategories().clear();
    ObservableList<String> xAxisCategories = FXCollections.observableArrayList();
    xAxisBarGraph.setCategories(xAxisCategories);

    yAxisBarGraph = new NumberAxis();
    barGraph.setTitle("Revenue");
    xAxisBarGraph.setLabel("Source");
    yAxisBarGraph.setLabel("Revenue");

    String category = "";
    if (dataSelectionToggleGroup.getSelectedToggle() == monthlyRevRadioButton) {
      category = date.getMonth().getDisplayName(TextStyle.FULL, Locale.CANADA);
    } else if (dataSelectionToggleGroup.getSelectedToggle() == dailyRevRadioButton) {
      category = date.toString();
    }
    xAxisCategories.add(category);

    XYChart.Series<String, Double> series1 = new XYChart.Series<>();
    series1.setName("Fare");
    XYChart.Series<String, Double> series2 = new XYChart.Series<>();
    series2.setName("Transit Pass");
    XYChart.Series<String, Double> series3 = new XYChart.Series<>();
    series3.setName("Card");

    series1.getData().add(new XYChart.Data<>(category, revenueData.get(0)));
    series2.getData().add(new XYChart.Data<>(category, revenueData.get(1)));
    series3.getData().add(new XYChart.Data<>(category, revenueData.get(2)));
    barGraph.getData().addAll(series1, series2, series3);
  }

  /**
   * Helper function for getDaily(Monthly)RevenueReport, display PieChart for different types of
   * revenue
   */
  private void showPieChartForRevenue(List<Double> revenueData) {
    pieChartGraph.getData().clear();
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

    PieChart.Data fareData = new PieChart.Data("fare", revenueData.get(0));
    PieChart.Data passData = new PieChart.Data("transit pass", revenueData.get(1));
    PieChart.Data cardData = new PieChart.Data("card", revenueData.get(2));
    pieChartData.add(fareData);
    pieChartData.add(passData);
    pieChartData.add(cardData);

    pieChartGraph.dataProperty().setValue(pieChartData);
    pieChartGraph.setTitle("Revenue");

    final Label caption = new Label("");
    caption.setTextFill(Color.DARKORANGE);
    caption.setStyle("-fx-font: 24 arial;");

    setPieChartData(caption);
  }

  BorderPane getMainPane() {
    return mainPane;
  }
}
