package org.halext.deltaseeker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.websocket.DeploymentException;

import org.halext.deltaseeker.service.Client;
import org.halext.deltaseeker.service.Model;
import org.halext.deltaseeker.service.Parser;
import org.halext.deltaseeker.service.data.Historical;
import org.halext.deltaseeker.service.data.Watchlist;
import org.halext.deltaseeker.service.data.Watchlist.Symbol;
import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class PrimaryController {

    private static final String COMMA_DELIMITER = ",";
    Client client = new Client();
    Parser parser = new Parser();

    ArrayList<Watchlist> watchlists;

    @FXML NumberAxis priceAxis; 
    @FXML TextField tickerInput;
    @FXML Label modelOutput;
    @FXML LineChart<String, Number> mainChart;
    @FXML TableView<Symbol> watchlistTable;
    @FXML ComboBox<String> watchlistComboBox;
    @FXML TextField maxIterationsInput;
    @FXML TextField maxErrorInput;
    @FXML TextField learningRateInput;
    @FXML TextField movingAveragePeriod;
    @FXML MenuBar menuBar;

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

    @FXML
    public void initialize() {
        mainChart.lookup(".chart-plot-background").setStyle("-fx-background-color: #30384b;");
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
            
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void buildWatchlist(int position) {
        TableColumn<Symbol, String> symbolColumn = new TableColumn<>("Symbol");
        TableColumn<Symbol, String> typeColumn = new TableColumn<>("Type");
        TableColumn<Symbol, String> bidColumn = new TableColumn<>("Bid");
        TableColumn<Symbol, String> askColumn = new TableColumn<>("Ask");

        watchlistTable.setEditable(true);
        symbolColumn.setMinWidth(100);
        symbolColumn.setCellValueFactory(new PropertyValueFactory<Symbol, String>("symbol"));

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
    public void selectItem() throws ParseException {
        // check the table's selected item and get selected item
        if (watchlistTable.getSelectionModel().getSelectedItem() != null) {
            Symbol symbol = watchlistTable.getSelectionModel().getSelectedItem();
            Model pricePredictionModel = new Model();
            try {
                pricePredictionModel.createPriceHistory( symbol.getSymbol() );
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
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Double minLow = Historical.getMinLow();
        Double maxHigh = Historical.getMaxHigh();
        priceAxis.setLowerBound(minLow.intValue() - 1);
        priceAxis.setUpperBound(maxHigh.intValue() + 1);
        series.setName(ticker);
        for ( int i = 0; i < Historical.candles.size(); i++ )
        {
            String shortDate = Historical.candles.get(i).getDatetime().toString().substring(4,10);
            series.getData().add(new XYChart.Data<>(shortDate, Historical.candles.get(i).getOpen() ));
        }
        mainChart.getData().add(series);
        series.getNode().setStyle("-fx-stroke: #405050;");
        
        Platform.runLater(()
                -> {

            Set<Node> nodes = mainChart.lookupAll(".series" + 0);
            for (Node n : nodes) {
                n.setStyle("-fx-background-color: black, white;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 1px;");
            }

            series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: #9DAAD9;");
        });

        mainChart.axisSortingPolicyProperty();
        mainChart.setAnimated(false);// disable animation
    }
    
    @FXML
    public void loadMovingAverage() {
        int period = Integer.parseInt(movingAveragePeriod.getText());
        int minPeriod = 0;
        XYChart.Series<String, Number> series = new XYChart.Series<>();

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

        Node line = series.getNode().lookup(".chart-series-line");

        Color color = Color.RED; // or any other color

        String rgb = String.format("%d, %d, %d",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));

        line.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");

        Platform.runLater(()
                -> {
            Set<Node> nodes = mainChart.lookupAll(".series" + 1);
            for (Node nma : nodes) {
                nma.setStyle("-fx-background-color: transparent;\n"
                        + "    -fx-background-radius: 1px;\n"
                        + "    -fx-padding: 1px;");
            }
        });

    }

    @FXML
    public void loadEquityCurveCSV() throws IOException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        HashMap<String, String> balances = new HashMap<>();

        Double minimumBalance = Double.POSITIVE_INFINITY;
        Double maximumBalance = Double.NEGATIVE_INFINITY;
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
                
                balances.put(values[0], values[1]);
                Double balance = Double.parseDouble(values[1]);

                if ( balance > maximumBalance ) {
                    maximumBalance = balance;
                }

                if ( balance < minimumBalance ) {
                    minimumBalance = balance;
                }

                System.out.println(balance);
                series.getData().add(new XYChart.Data<>(values[0], balance));
            }
        }
        priceAxis.setLowerBound(minimumBalance.intValue() - 100);
        priceAxis.setUpperBound(maximumBalance.intValue() + 100);

        series.setName("Equity Curve");
        
        mainChart.getData().add(series);
        //series.getNode().setStyle("-fx-stroke: #405050;");
        
        Platform.runLater(()
                -> {

            Set<Node> nodes = mainChart.lookupAll(".series" + 0);
            for (Node n : nodes) {
                n.setStyle("-fx-background-color: #405050;\n"
                        + "    -fx-background-insets: 0, 2;\n"
                        + "    -fx-background-radius: 5px;\n"
                        + "    -fx-padding: 1px;");
            }

            series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke: black;");
        });

        mainChart.axisSortingPolicyProperty();
        mainChart.setAnimated(false);// disable animation
    }


    @FXML
    private void loadInstrument() throws IOException, ParseException {
        Model pricePredictionModel = new Model();
        pricePredictionModel.createPriceHistory( tickerInput.getText() );
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
}
