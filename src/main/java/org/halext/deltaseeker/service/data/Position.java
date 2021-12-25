package org.halext.deltaseeker.service.data;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Position {
//     "positions": [
//     {
//       "shortQuantity": 0,
//       "averagePrice": 0,
//       "currentDayProfitLoss": 0,
//       "currentDayProfitLossPercentage": 0,
//       "longQuantity": 0,
//       "settledLongQuantity": 0,
//       "settledShortQuantity": 0,
//       "agedQuantity": 0,
//       "instrument": "The type <Instrument> has the following subclasses [Equity, FixedIncome, MutualFund, CashEquivalent, Option] descriptions are listed below\"",
//       "marketValue": 0
//     }
//   ],

    private SimpleDoubleProperty averagePrice;
    private SimpleDoubleProperty currentDayProfitLoss;
    private SimpleDoubleProperty currentDayProfitLossPercentage;
    private SimpleDoubleProperty longQuantity;
    private SimpleDoubleProperty settledLongQuantity;
    private SimpleDoubleProperty settledShortQuantity;
    private SimpleDoubleProperty agedQuantity;
    private SimpleDoubleProperty marketValue;

    private SimpleStringProperty cusip;
    private SimpleStringProperty symbol;

    public String getCusip() {
        return this.cusip.get();
    }

    public void setCusip(String cusip) {
        this.cusip = new SimpleStringProperty(cusip);
    }

    public String getSymbol() {
        return this.symbol.get();
    }

    public void setSymbol(String symbol) {
        this.symbol = new SimpleStringProperty(symbol);
    }

    public String getDescription() {
        return this.description.get();
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }

    public String getExchange() {
        return this.exchange.get();
    }

    public void setExchange(String exchange) {
        this.exchange = new SimpleStringProperty(exchange);
    }

    public String getAssetType() {
        return this.assetType.get();
    }

    public void setAssetType(String assetType) {
        this.assetType = new SimpleStringProperty(assetType);
    }

    private SimpleStringProperty description;
    private SimpleStringProperty exchange;
    private SimpleStringProperty assetType; 

    private Instrument instrument;
    private SimpleDoubleProperty shortQuantity;

    public Instrument getInstrument() {
        return this.instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
        this.symbol = new SimpleStringProperty(instrument.getSymbol());
        this.assetType = new SimpleStringProperty(instrument.getAssetType());
    }

    public double getShortQuantity() {
        return this.shortQuantity.get();
    }

    public void setShortQuantity(double shortQuantity) {
        this.shortQuantity = new SimpleDoubleProperty(shortQuantity);
    }

    public double getAveragePrice() {
        return this.averagePrice.get();
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = new SimpleDoubleProperty(averagePrice);
    }

    public double getCurrentDayProfitLoss() {
        return this.currentDayProfitLoss.get();
    }

    public void setCurrentDayProfitLoss(double currentDayProfitLoss) {
        this.currentDayProfitLoss = new SimpleDoubleProperty(currentDayProfitLoss);
    }

    public double getCurrentDayProfitLossPercentage() {
        return this.currentDayProfitLossPercentage.get();
    }

    public void setCurrentDayProfitLossPercentage(double currentDayProfitLossPercentage) {
        this.currentDayProfitLossPercentage = new SimpleDoubleProperty(currentDayProfitLossPercentage);
    }

    public double getLongQuantity() {
        return this.longQuantity.get();
    }

    public void setLongQuantity(double longQuantity) {
        this.longQuantity = new SimpleDoubleProperty(longQuantity);
    }

    public double getSettledLongQuantity() {
        return this.settledLongQuantity.get();
    }

    public void setSettledLongQuantity(double settledLongQuantity) {
        this.settledLongQuantity = new SimpleDoubleProperty(settledLongQuantity);
    }

    public double getSettledShortQuantity() {
        return this.settledShortQuantity.get();
    }

    public void setSettledShortQuantity(double settledShortQuantity) {
        this.settledShortQuantity = new SimpleDoubleProperty(settledShortQuantity);
    }

    public double getAgedQuantity() {
        return this.agedQuantity.get();
    }

    public void setAgedQuantity(double agedQuantity) {
        this.agedQuantity = new SimpleDoubleProperty(agedQuantity);
    }

    public double getMarketValue() {
        return this.marketValue.get();
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = new SimpleDoubleProperty(marketValue);
    }

}
