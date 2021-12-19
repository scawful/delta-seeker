package org.halext.deltaseeker;

import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class PrimaryController {

    Client client = new Client();
    Parser parser = new Parser();

    ArrayList<Watchlist> watchlists;

    @FXML NumberAxis priceAxis; 
    @FXML TextField tickerInput;
    @FXML Label modelOutput;
    @FXML LineChart<String, Number> mainChart;
    @FXML TableView<Symbol> watchlistTable;
    @FXML ComboBox<String> watchlistComboBox;

    @FXML public void handleMouseClick(MouseEvent arg0) {
        System.out.println("clicked on " + watchlistComboBox.getSelectionModel().getSelectedItem());
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
                selectItem();
            }
        });
        
    }

    @FXML
    public void selectItem() {
        // check the table's selected item and get selected item
        if (watchlistTable.getSelectionModel().getSelectedItem() != null) {
            Symbol symbol = watchlistTable.getSelectionModel().getSelectedItem();
            Model pricePredictionModel = new Model();
            try {
                pricePredictionModel.createPriceHistory( symbol.getSymbol() );
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
        priceAxis.setLowerBound(Historical.getMinLow());
        priceAxis.setUpperBound(Historical.getMaxHigh());
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
    private void loadInstrument() throws IOException {
        Model pricePredictionModel = new Model();
        pricePredictionModel.createPriceHistory( tickerInput.getText() );
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
