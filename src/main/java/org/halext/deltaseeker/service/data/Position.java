package org.halext.deltaseeker.service.data;

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

    private double averagePrice;
    private double currentDayProfitLoss;
    private double currentDayProfitLossPercentage;
    private double longQuantity;
    private double settledLongQuantity;
    private double settledShortQuantity;
    private double agedQuantity;
    private double marketValue;

    private Instrument instrument;
    private double shortQuantity;

    public Instrument getInstrument() {
        return this.instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public double getShortQuantity() {
        return this.shortQuantity;
    }

    public void setShortQuantity(double shortQuantity) {
        this.shortQuantity = shortQuantity;
    }

    public double getAveragePrice() {
        return this.averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public double getCurrentDayProfitLoss() {
        return this.currentDayProfitLoss;
    }

    public void setCurrentDayProfitLoss(double currentDayProfitLoss) {
        this.currentDayProfitLoss = currentDayProfitLoss;
    }

    public double getCurrentDayProfitLossPercentage() {
        return this.currentDayProfitLossPercentage;
    }

    public void setCurrentDayProfitLossPercentage(double currentDayProfitLossPercentage) {
        this.currentDayProfitLossPercentage = currentDayProfitLossPercentage;
    }

    public double getLongQuantity() {
        return this.longQuantity;
    }

    public void setLongQuantity(double longQuantity) {
        this.longQuantity = longQuantity;
    }

    public double getSettledLongQuantity() {
        return this.settledLongQuantity;
    }

    public void setSettledLongQuantity(double settledLongQuantity) {
        this.settledLongQuantity = settledLongQuantity;
    }

    public double getSettledShortQuantity() {
        return this.settledShortQuantity;
    }

    public void setSettledShortQuantity(double settledShortQuantity) {
        this.settledShortQuantity = settledShortQuantity;
    }

    public double getAgedQuantity() {
        return this.agedQuantity;
    }

    public void setAgedQuantity(double agedQuantity) {
        this.agedQuantity = agedQuantity;
    }

    public double getMarketValue() {
        return this.marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

}
