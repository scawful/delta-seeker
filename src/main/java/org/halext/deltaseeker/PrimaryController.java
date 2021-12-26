package org.halext.deltaseeker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.websocket.DeploymentException;

import org.halext.deltaseeker.service.Client;
import org.halext.deltaseeker.service.Model;
import org.halext.deltaseeker.service.Parser;
import org.halext.deltaseeker.service.data.Historical;
import org.halext.deltaseeker.service.data.Order;
import org.halext.deltaseeker.service.data.OrderBook;
import org.halext.deltaseeker.service.data.Position;
import org.halext.deltaseeker.service.data.Watchlist;
import org.halext.deltaseeker.service.data.Watchlist.Symbol;
import org.halext.deltaseeker.service.graphics.Chart;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class PrimaryController {

    List<Watchlist> watchlists;

    private static final String COMMA_DELIMITER = ",";
    Client client = new Client();
    Parser parser = new Parser();
    Chart chart;
    
    private Boolean instrumentSet;

    private String transferFunctionType = "SIGMOID";

    /**
     * GUI Elements 
     */
    @FXML LineChart<String, Number> mainChart;
    @FXML BarChart <String, Number> volumeChart;
    @FXML LineChart<String, Number> indicatorChart;
    @FXML NumberAxis priceAxis; 
    @FXML NumberAxis indicatorAxis;
    @FXML CategoryAxis dateCategoryAxis;
    @FXML TextField tickerInput;
    @FXML Label modelOutput;

    @FXML TableView<Order> ordersTable;
    @FXML TableView<Symbol> watchlistTable;
    @FXML TableView<Position> positionsTable;
    @FXML TableView<OrderBook> orderBookTable;

    @FXML ComboBox<String> orderBookComboBox;
    @FXML ComboBox<String> watchlistComboBox;
    @FXML ComboBox<String> quoteComboBox;
    @FXML ComboBox<String> timeIntervalComboBox;
    @FXML ComboBox<String> timeUnitComboxBox;
    @FXML ComboBox<String> frequencyIntervalComboBox;
    @FXML ComboBox<String> frequencyUnitComboBox;
    @FXML DatePicker startDatePicker;
    @FXML DatePicker endDatePicker;
    @FXML CheckBox extendedHoursCheckBox;

    @FXML TextField maxIterationsInput;
    @FXML TextField maxErrorInput;
    @FXML TextField learningRateInput;
    @FXML TextField movingAveragePeriod;

    @FXML Canvas chartCanvas;
    @FXML GraphicsContext graphicsContext;

    @FXML ListView<Object> transferFunctionTypes;
    @FXML ListView<Object> networkTypes;
    @FXML MenuBar menuBar;
    @FXML ColorPicker movingAverageColorPicker;

    @FXML CheckBox inputOpen;
    @FXML CheckBox inputClose;
    @FXML CheckBox inputHigh;
    @FXML CheckBox inputLow;
    @FXML CheckBox inputVolume;

    @FXML CheckBox outputOpen;
    @FXML CheckBox outputClose;
    @FXML CheckBox outputHigh;
    @FXML CheckBox outputLow;
    @FXML CheckBox outputVolume;
    
    @FXML XYChart.Series<String, Number> priceSeries;
    @FXML XYChart.Series<String, Number> volumeSeries;

    @FXML public void changeTransferFunction(MouseEvent arg0) {
        transferFunctionType = (String) transferFunctionTypes.getSelectionModel().getSelectedItem();
        System.out.println("clicked on " + transferFunctionTypes.getSelectionModel().getSelectedItem()); 
    }
    
    //----------------------------------------------------------------------------------------------------
    //
    //  Private Ancillary Functions 
    //
    //----------------------------------------------------------------------------------------------------
    
    private int[] buildInputParameters() {
        ArrayList<Integer> inputs = new ArrayList<>();
        if ( inputOpen.isSelected() )
            inputs.add(0);

        if ( inputClose.isSelected() )
            inputs.add(1);

        if ( inputHigh.isSelected() )
            inputs.add(2);

        if ( inputLow.isSelected() )
            inputs.add(3);

        if ( inputVolume.isSelected() ) 
            inputs.add(4);
        
        return inputs.stream().mapToInt(i -> i).toArray();
    }

    private int[] buildOutputParameters() {
        ArrayList<Integer> outputs = new ArrayList<>();
        if ( outputOpen.isSelected() )
            outputs.add(0);

        if ( outputClose.isSelected() )
            outputs.add(1);

        if ( outputHigh.isSelected() )
            outputs.add(2);

        if ( outputLow.isSelected() )
            outputs.add(3);

        if ( outputVolume.isSelected() ) 
            outputs.add(4);
        
        return outputs.stream().mapToInt(i -> i).toArray();
    }

    //----------------------------------------------------------------------------------------------------
    //
    //  Initialization Functions 
    //
    //----------------------------------------------------------------------------------------------------

    private void initTransferTypes() {
        ArrayList<String> transferTypes = new ArrayList<>();
        transferTypes.add("SIGMOID");
        transferTypes.add("GAUSSIAN");
        transferTypes.add("LINEAR");
        transferTypes.add("LOG");
        transferTypes.add("RAMP");
        transferTypes.add("SGN");
        transferTypes.add("SIN");
        transferTypes.add("STEP");
        transferTypes.add("TANH");
        transferTypes.add("TRAPEZOID");
        transferFunctionTypes.getItems().setAll(FXCollections.observableArrayList(transferTypes));
    }

    private void initNetworkTypes() {
        ArrayList<String> networkTypeArray = new ArrayList<>();
        networkTypeArray.add("Adaline");
        networkTypeArray.add("AutoencoderNetwork");
        networkTypeArray.add("BAM");
        networkTypeArray.add("CompetitiveNetwork");
        networkTypeArray.add("ConvolutionalNetwork");
        networkTypeArray.add("Hopfield");
        networkTypeArray.add("Instar");
        networkTypeArray.add("Kohonen");
        networkTypeArray.add("MaxNet");
        networkTypeArray.add("MultiLayerPerceptron");
        networkTypeArray.add("NeuroFuzzyPerceptron");
        networkTypeArray.add("Outstar");
        networkTypeArray.add("Perceptron");
        networkTypeArray.add("RBFNetwork");
        networkTypes.getItems().setAll(FXCollections.observableArrayList(networkTypeArray));
    }

    private void initPriceHistoryParameters() {
        // day, month, year, or ytd
        ArrayList<String> periodType = new ArrayList<>();
        periodType.add("day");
        periodType.add("month");
        periodType.add("year");
        periodType.add("ytd");
        timeIntervalComboBox.setItems(FXCollections.observableArrayList(periodType));

        // day: 1, 2, 3, 4, 5, 10*
        // month: 1*, 2, 3, 6
        // year: 1*, 2, 3, 5, 10, 15, 20
        // ytd: 1*
        ArrayList<String> period = new ArrayList<>();
        period.add("1");
        period.add("2");
        period.add("3");
        period.add("4");
        period.add("5");
        period.add("6");
        period.add("10");
        period.add("15");
        period.add("20");
        timeUnitComboxBox.setItems(FXCollections.observableArrayList(period));

        // day: minute*
        // month: daily, weekly*
        // year: daily, weekly, monthly*
        // ytd: daily, weekly*
        ArrayList<String> frequencyType = new ArrayList<>();
        frequencyType.add("minute");
        frequencyType.add("daily");
        frequencyType.add("weekly");
        frequencyType.add("monthly");
        frequencyIntervalComboBox.setItems(FXCollections.observableArrayList(frequencyType));

        // minute: 1*, 5, 10, 15, 30
        // daily: 1*
        // weekly: 1*
        // monthly: 1*
        ArrayList<String> frequency = new ArrayList<>();
        frequency.add("1");
        frequency.add("5");
        frequency.add("10");
        frequency.add("15");
        frequency.add("30");
        frequencyUnitComboBox.setItems(FXCollections.observableArrayList(frequency));

        watchlistComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            int position = timeIntervalComboBox.getSelectionModel().getSelectedIndex();
            
         }); 

        // create a event handler
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                graphicsContext.setStroke( movingAverageColorPicker.getValue() );
            }
        };
    
        // set listener
        movingAverageColorPicker.setOnAction(event);
    }

    private void initCanvas() {
        this.graphicsContext = this.chartCanvas.getGraphicsContext2D();

        // graphicsContext.setFill(Color.LIGHTGRAY);
        // graphicsContext.setLineWidth(5);

        // graphicsContext.fill();
        // graphicsContext.strokeRect(
        //         0,              //x of the upper left corner
        //         0,              //y of the upper left corner
        //         canvasWidth,    //width of the rectangle
        //         canvasHeight);  //height of the rectangle

        graphicsContext.setFill(Color.RED);
        graphicsContext.setStroke( movingAverageColorPicker.getValue() );
        graphicsContext.setLineWidth(2);
        
        chartCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, 
                new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                graphicsContext.beginPath();
                graphicsContext.moveTo(event.getX(), event.getY());
                graphicsContext.stroke();

            }
        });

        chartCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
                new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                graphicsContext.lineTo(event.getX(), event.getY());
                graphicsContext.stroke();
                // graphicsContext.closePath();
                // graphicsContext.beginPath();
                graphicsContext.moveTo(event.getX(), event.getY());
            }
        });

        chartCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, 
                new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                graphicsContext.lineTo(event.getX(), event.getY());
                graphicsContext.stroke();
                graphicsContext.closePath();
            }
        });

    }

    //----------------------------------------------------------------------------------------------------
    //
    //  Primary Controller Entry
    //
    //----------------------------------------------------------------------------------------------------

    @FXML
    public void initialize() {
        instrumentSet = false;
        chart = new Chart(mainChart, volumeChart);
        chart.initBackground();

        initTransferTypes();
        initNetworkTypes();
        initPriceHistoryParameters();
        initCanvas();
        try {
            client.retrieveKeyFile();
            watchlists = parser.parseWatchlistData( client.getWatchlistSingleAccount() );   

            ObservableList<String> names = FXCollections.observableArrayList();
            for ( int i = 0; i < watchlists.size(); i++ ) {
                names.add(watchlists.get(i).getName());
            }
            watchlistComboBox.setItems(names);
            watchlistComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                int position = watchlistComboBox.getSelectionModel().getSelectedIndex();
                buildWatchlist(position);
                System.out.println("clicked on " + watchlistComboBox.getSelectionModel().getSelectedItem());
             }); 

            ObservableList<String> books = FXCollections.observableArrayList();
            books.add("NASDAQ");
            books.add("OPTIONS");
            books.add("FUTURES");
            books.add("FUTURES_OPTIONS");
            books.add("FOREX");
            books.add("LISTED");
            orderBookComboBox.setItems(books);

            ObservableList<String> quotes = FXCollections.observableArrayList();
            quotes.add("EQUITIES");
            quotes.add("OPTIONS");
            quotes.add("FOREX");
            quotes.add("FUTURES_OPTIONS");
            quoteComboBox.setItems(quotes);

            buildAccountOrders();
            buildAccountPositions();
            
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    //----------------------------------------------------------------------------------------------------
    //
    //  Data Builder Functions 
    //
    //----------------------------------------------------------------------------------------------------

    @FXML
    @SuppressWarnings("unchecked")
    public void buildWatchlist(int position) {
        TableColumn<Symbol, String> symbolColumn = new TableColumn<>("Symbol");
        TableColumn<Symbol, String> typeColumn = new TableColumn<>("Type");
        TableColumn<Symbol, String> bidColumn = new TableColumn<>("Bid");
        TableColumn<Symbol, String> askColumn = new TableColumn<>("Ask");

        watchlistTable.setEditable(true);
        symbolColumn.setMinWidth(75);
        symbolColumn.setCellValueFactory(new PropertyValueFactory<Symbol, String>("symbol"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<Symbol, String>("assetType"));

        watchlistTable.setItems(watchlists.get(position).createSymbolList());
        watchlistTable.getColumns().addAll(symbolColumn, typeColumn, bidColumn, askColumn); 

        watchlistTable.getSelectionModel().setCellSelectionEnabled(true);
        watchlistTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                try {
                    selectItem();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });        
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void buildAccountOrders() {
        TableColumn<Order, String> symbolColumn = new TableColumn<>("Symbol");
        TableColumn<Order, String> orderTypeColumn = new TableColumn<>("Order Type");
        TableColumn<Order, String> assetTypeColumn = new TableColumn<>("Asset Type");
        TableColumn<Order, String> priceColumn = new TableColumn<>("Price");
        TableColumn<Order, String> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Order, String> enteredTimeColumn = new TableColumn<>("Entered Time");

        symbolColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("symbol"));
        orderTypeColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("orderType"));
        assetTypeColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("assetType"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("quantity"));
        enteredTimeColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("enteredTime"));

        try {
            ordersTable.setItems(FXCollections.observableArrayList(parser.parseOrders( client.getOrders(10) )));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        ordersTable.getColumns().addAll(symbolColumn, assetTypeColumn, orderTypeColumn, priceColumn, quantityColumn, enteredTimeColumn); 
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void buildAccountPositions() {
        TableColumn<Position, String> symbolColumn = new TableColumn<>("Symbol");
        TableColumn<Position, String> assetTypeColumn = new TableColumn<>("Asset Type");
        TableColumn<Position, String> avgPriceColumn = new TableColumn<>("Average Price");
        TableColumn<Position, String> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Position, String> currentDayProfitLossColumn = new TableColumn<>("P/L Day");
        TableColumn<Position, String> currentDayProfitLossPercentageColumn = new TableColumn<>("P/L %");


        symbolColumn.setCellValueFactory(new PropertyValueFactory<Position, String>("symbol"));
        assetTypeColumn.setCellValueFactory(new PropertyValueFactory<Position, String>("assetType"));
        avgPriceColumn.setCellValueFactory(new PropertyValueFactory<Position, String>("averagePrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Position, String>("longQuantity"));
        currentDayProfitLossColumn.setCellValueFactory(new PropertyValueFactory<Position, String>("currentDayProfitLoss"));
        currentDayProfitLossPercentageColumn.setCellValueFactory(new PropertyValueFactory<Position, String>("currentDayProfitLossPercentage"));

        try {
            positionsTable.setItems(FXCollections.observableArrayList( parser.parsePositions( client.getAccountData() )));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        positionsTable.getColumns().addAll(symbolColumn, assetTypeColumn, avgPriceColumn, quantityColumn, currentDayProfitLossColumn, currentDayProfitLossPercentageColumn); 
    }

    @FXML
    public void buildOrderBook() {
        
    }


    //----------------------------------------------------------------------------------------------------
    //
    //  
    //
    //----------------------------------------------------------------------------------------------------

    @FXML
    public void selectItem() throws ParseException {
        // check the table's selected item and get selected item
        if (watchlistTable.getSelectionModel().getSelectedItem() != null) {
            Symbol symbol = watchlistTable.getSelectionModel().getSelectedItem();
            Model pricePredictionModel = new Model();
            try {
                pricePredictionModel.setTransferFunctionType(transferFunctionType);
                pricePredictionModel.createPriceHistory(symbol.getSymbol(), 
                                                        timeIntervalComboBox.getSelectionModel().getSelectedItem(),
                                                        timeUnitComboxBox.getSelectionModel().getSelectedItem(),
                                                        frequencyIntervalComboBox.getSelectionModel().getSelectedItem(),
                                                        frequencyUnitComboBox.getSelectionModel().getSelectedItem(),
                                                        true);
                pricePredictionModel.loadDataSet(buildInputParameters(), buildOutputParameters(), 0);
                pricePredictionModel.trainNetwork(Integer.parseInt(maxIterationsInput.getText()), 
                                                  Double.parseDouble(maxErrorInput.getText()),  
                                                  Double.parseDouble(learningRateInput.getText()));
                pricePredictionModel.loadNetwork();
                pricePredictionModel.evaluateNetwork();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadChart( symbol.getSymbol() );
            modelOutput.setText(pricePredictionModel.getDebugOutput());
        }
    }

    @FXML
    public void loadChart( String ticker ) {
        if ( Boolean.TRUE.equals(instrumentSet) ) {
            this.priceSeries.getData().clear();
            this.volumeSeries.getData().clear();
            mainChart.getData().clear();
            volumeChart.getData().clear();
        }
        instrumentSet = true;
        this.priceSeries = new XYChart.Series<>();
        this.volumeSeries = new XYChart.Series<>();
        Double minLow = Historical.getMinLow();
        Double maxHigh = Historical.getMaxHigh();
        priceAxis.setLowerBound(minLow.intValue() - 1);
        priceAxis.setUpperBound(maxHigh.intValue() + 1);
        priceAxis.setTickUnit((maxHigh.intValue() - minLow.intValue()) / 10);
        priceSeries.setName(ticker);
        for ( int i = 0; i < Historical.getNumCandles(); i++ )
        {
            String shortDate = Historical.candles.get(i).getDatetime().toString().substring(4,10);
            priceSeries.getData().add(new XYChart.Data<>(shortDate, Historical.candles.get(i).getOpen() ));
            volumeSeries.getData().add(new XYChart.Data<>(shortDate, Historical.candles.get(i).getVolume()));
        }
        chart.updateChartBackground();
        mainChart.getData().add(priceSeries);
        volumeChart.getData().add(volumeSeries);
        chart.setPriceLineColor(priceSeries);
        
        // Platform.runLater(()
        //         -> {

        //     Set<Node> nodes = mainChart.lookupAll(".series" + 0);
        //     for (Node n : nodes) {
        //         n.setStyle("-fx-background-color: white;\n"
        //                 + "    -fx-background-insets: 0, 2;\n"
        //                 + "    -fx-background-radius: 5px;\n"
        //                 + "    -fx-padding: 1px;");
        //     }

        //     // b6bcd1
        //     Node ns = mainChart.lookup(".default-color" + legendColorIndex + ".chart-line-symbol");
        //     ns.setStyle("-fx-background-color: #9DAAD9;");
        //     priceSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #9DAAD9;");
        // });

        mainChart.axisSortingPolicyProperty();
        mainChart.setAnimated(false);// disable animation
        volumeChart.setVisible(true);
    }
    
    @FXML
    public void loadMovingAverage() {
        Integer period = Integer.parseInt(movingAveragePeriod.getText());
        int minPeriod = 0;
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        series.setName(period.toString() + "MA");
        int n = Historical.candles.size();
        for ( int i = 0; i < n; i++ )
        {
            double periodSum = 0;
            String shortDate = Historical.candles.get(i).getDatetime().toString().substring(4,10);

            if ( i < period ) {
                minPeriod++;
            } else {
                minPeriod = period;
            }

            for ( int j = 0; j < minPeriod; j++ ) {
                periodSum += Historical.candles.get(i - j).getOpen();
            }
            periodSum /= minPeriod;

            series.getData().add(new XYChart.Data<>(shortDate, periodSum));
        }
        mainChart.getData().add(series);
        Color color = movingAverageColorPicker.getValue();
        chart.setMovingAverageColor(series, color);
        // Node line = series.getNode().lookup(".chart-series-line");
        // Color color = movingAverageColorPicker.getValue();
        // String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());  
        // String rgb = String.format("%d, %d, %d",
        //         (int) (color.getRed() * 255),
        //         (int) (color.getGreen() * 255),
        //         (int) (color.getBlue() * 255));

        // Platform.runLater(()
        //         -> {
        //     // b6bcd1
        //     Node ns = mainChart.lookup(".default-color" + legendColorIndex + ".chart-line-symbol");
        //     ns.setStyle("-fx-background-color: rgba(" + rgb + ", 1.0);");
        // });
        // legendColorIndex++;
        
        // line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");
    }

    @FXML
    private void loadRelativeStrengthIndex() {
        XYChart.Series<String, Number> rsiSeries = new XYChart.Series<>();
        int period = 14;
        int minPeriod = 0;
        rsiSeries.setName(period + "RSI");
        int n = Historical.getNumCandles();
        double maxRsi = 0.0;
        double minRsi = Double.POSITIVE_INFINITY;
        double prevAvgGain = 0.0;
        double prevAvgLoss = 0.0;
       
        for ( int i = 0; i < n; i++ ) {
            if ( i < period ) {
                minPeriod++;
            } else {
                minPeriod = period;
            }
            double avgGain = 0.0;
            double avgLoss = 0.0;
            for ( int j = 0; j < minPeriod; j++ ) {
                double open = Historical.candles.get(i - j).getOpen();
                double close = Historical.candles.get(i - j).getClose();
                double pnl = close - open;
                pnl = pnl / open;
                if ( pnl > 0 ) {
                    avgGain += pnl;
                } else { 
                    avgLoss += pnl;
                }
            }

            // Average Gain = [(previous Average Gain) x 13 + current Gain] / 14.
            // Average Loss = [(previous Average Loss) x 13 + current Loss] / 14.

            if ( i != 0 ) {
                avgGain = ((prevAvgGain) * 13 + avgGain) / 14;
                avgLoss = ((prevAvgLoss) * 13 + avgLoss) / 14;
            } else {
                avgGain /= minPeriod;
                avgLoss /= minPeriod;
            }

            prevAvgGain = avgGain;
            prevAvgLoss = avgLoss;
    
            // step one 
            double relativeStrength = 0.0;
            if ( avgLoss != 0 ) {
                relativeStrength = 100 - (100 / 1.0 + (avgGain / avgLoss));
            }
            // relativeStrength *= 0.01;

            if ( relativeStrength > maxRsi ) {
                maxRsi = relativeStrength;
            }

            if ( relativeStrength < minRsi ) {
                minRsi = relativeStrength;
            }
            
            String shortDate = Historical.candles.get(i).getDatetime().toString().substring(4,10);
            rsiSeries.getData().add(new XYChart.Data<>(shortDate, relativeStrength));
        }
        indicatorAxis.setLowerBound(minRsi);
        indicatorAxis.setUpperBound(maxRsi);
        indicatorChart.getData().add(rsiSeries);
        indicatorChart.setVisible(true);
        indicatorChart.setMaxHeight(50);
        chart.setChartBackgroundColor(indicatorChart);
        volumeChart.setMaxHeight(481 - 169);
        mainChart.setMaxHeight(481 - 130);
        mainChart.setLegendVisible(false);
    }

    @FXML
    public void loadEquityCurveCSV() throws IOException {
        this.priceSeries = new XYChart.Series<String, Number>();
        priceAxis.setLowerBound(1200);
        priceAxis.setUpperBound(6000);
        priceAxis.setTickUnit((6000 - 1200) / 10);
        priceSeries.setName("Equity Curve");
        try (BufferedReader br = new BufferedReader(new FileReader("chart.csv"))) {
            Boolean first = true;
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] values = line.split(COMMA_DELIMITER);

                if (Boolean.TRUE.equals(first)) {
                    values[0] = values[0].substring(3, values[0].length());
                    first = false;
                }
                
                Double balance = Double.parseDouble(values[1]);

                System.out.println(balance);
                priceSeries.getData().add(new XYChart.Data<>(values[0], balance));
            }
        }


        mainChart.getData().add(priceSeries);
        chart.setPriceLineColor(priceSeries);
        
        Platform.runLater(()
                -> {

            Set<Node> nodes = mainChart.lookupAll(".series" + 0);
            for (Node n : nodes) {
                n.setStyle("-fx-background-color: #b6bcd1;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 1px;");
            }

            priceSeries.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #9DAAD9;");
        });

        mainChart.axisSortingPolicyProperty();
        mainChart.setAnimated(false);// disable animation

    }


    @FXML
    private void loadInstrument() throws IOException, ParseException {
        Model pricePredictionModel = new Model();
        pricePredictionModel.setTransferFunctionType(transferFunctionType);
        pricePredictionModel.createPriceHistory(tickerInput.getText(),
                                                timeIntervalComboBox.getSelectionModel().getSelectedItem(),
                                                timeUnitComboxBox.getSelectionModel().getSelectedItem(),
                                                frequencyIntervalComboBox.getSelectionModel().getSelectedItem(),
                                                frequencyUnitComboBox.getSelectionModel().getSelectedItem(), 
                                                true);
        pricePredictionModel.loadDataSet(buildInputParameters(), buildOutputParameters(), 0);
        pricePredictionModel.trainNetwork(Integer.parseInt(maxIterationsInput.getText()), 
                                          Double.parseDouble(maxErrorInput.getText()),  
                                          Double.parseDouble(learningRateInput.getText()));
        pricePredictionModel.loadNetwork();
        pricePredictionModel.evaluateNetwork();
        loadChart( tickerInput.getText() );
        modelOutput.setText(pricePredictionModel.getDebugOutput());
    }

    @FXML
    private void postAccessToken() throws IOException, org.json.simple.parser.ParseException {
        client.retrieveKeyFile();
        client.postAccessToken();
    }

    @FXML
    private void startStreamingSession() throws IOException, ParseException, DeploymentException, java.text.ParseException {
        client.openStream();
    }

    @FXML
    private void clearCanvas() {
        this.graphicsContext.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());
    }
}
