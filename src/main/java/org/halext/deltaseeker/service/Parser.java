package org.halext.deltaseeker.service;

import org.json.simple.JSONObject;

import javafx.beans.property.SimpleDoubleProperty;

import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.halext.deltaseeker.service.data.Quote;
import org.halext.deltaseeker.service.data.Historical;
import org.halext.deltaseeker.service.data.Instrument;
import org.halext.deltaseeker.service.data.Order;
import org.halext.deltaseeker.service.data.Position;
import org.halext.deltaseeker.service.data.Watchlist;

public class Parser {

    public Parser() {

    }
    
    /**
     * Inserts candle data into Historical object 
     * 
     * @param jo
     */
    public void parsePriceHistory( JSONObject jo ) {
        JSONArray candles = (JSONArray) jo.get("candles");

        if ( Historical.getNumCandles() != 0 ) {
            Historical.clearCandles();
        }

		@SuppressWarnings("unchecked")
		Iterator<JSONObject> it = candles.iterator();
		while (it.hasNext()) {
            JSONObject candle = (JSONObject) it.next();
            long volume = (long) candle.get("volume");
            double high = (double) candle.get("high");
            double low = (double) candle.get("low");
            double open = (double) candle.get("open");
            double close = (double) candle.get("close");
            Long rawDatetime = (Long) candle.get("datetime");
            Date datetime = new java.util.Date((long) candle.get("datetime") );
            Historical.addCandle(volume, high, low, open, close, rawDatetime, datetime);
		}   
        
        Historical.sortCandles();
    }

    /**
     * Insert instrument details and fundamental data 
     * 
     * @param jo
     */
    public void parseInstrumentData( JSONObject jo, String ticker ) {
        Instrument instrument = new Instrument();
        JSONObject masterObject = (JSONObject) jo.get(ticker);
        JSONObject fundamentalObject = (JSONObject) masterObject.get("fundamental");

        instrument.setSymbol((String) fundamentalObject.remove("symbol"));

        for (Object key : fundamentalObject.keySet()) {
            instrument.insertFundamental((String) key, fundamentalObject.get(key));
        }
    }

    public Instrument parseIndividualInstrument( JSONObject jo ) {
        Instrument instrument = new Instrument();
        instrument.setSymbol((String) jo.get("symbol"));
        instrument.setAssetType((String) jo.get("assetType"));
        instrument.setCusip((String) jo.get("cusip"));
        instrument.setDescription((String) jo.get("description"));
    // Equity:
    // {
    //     "assetType": "'EQUITY' or 'OPTION' or 'INDEX' or 'MUTUAL_FUND' or 'CASH_EQUIVALENT' or 'FIXED_INCOME' or 'CURRENCY'",
    //     "cusip": "string",
    //     "symbol": "string",
    //     "description": "string"
    // }
    
    // CashEquivalent:
    // {
    //     "assetType": "'EQUITY' or 'OPTION' or 'INDEX' or 'MUTUAL_FUND' or 'CASH_EQUIVALENT' or 'FIXED_INCOME' or 'CURRENCY'",
    //     "cusip": "string",
    //     "symbol": "string",
    //     "description": "string",
    //     "type": "'SAVINGS' or 'MONEY_MARKET_FUND'"
    // }
        
    // Option:
    // {
    //     "assetType": "'EQUITY' or 'OPTION' or 'INDEX' or 'MUTUAL_FUND' or 'CASH_EQUIVALENT' or 'FIXED_INCOME' or 'CURRENCY'",
    //     "cusip": "string",
    //     "symbol": "string",
    //     "description": "string",
    //     "type": "'VANILLA' or 'BINARY' or 'BARRIER'",
    //     "putCall": "'PUT' or 'CALL'",
    //     "underlyingSymbol": "string",
    //     "optionMultiplier": 0,
    //     "optionDeliverables": [
    //     {
    //         "symbol": "string",
    //         "deliverableUnits": 0,
    //         "currencyType": "'USD' or 'CAD' or 'EUR' or 'JPY'",
    //         "assetType": "'EQUITY' or 'OPTION' or 'INDEX' or 'MUTUAL_FUND' or 'CASH_EQUIVALENT' or 'FIXED_INCOME' or 'CURRENCY'"
    //     }
    //     ]
    // }
        return instrument;
    }

    public void parseQuoteData( JSONObject jo ) {
        
    }

    public List<Watchlist> parseWatchlistData( JSONArray jo ) {
        ArrayList<Watchlist> watchlists = new ArrayList<>();

        for ( int i = 0; i < jo.size(); i++ ) {
            JSONObject wrapper = (JSONObject) jo.get(i);
            Watchlist watchlist = new Watchlist((String)wrapper.get("name"), (String)wrapper.get("watchlistId"));

            JSONArray items = (JSONArray) wrapper.get("watchlistItems");
            for ( int j = 0; j < items.size(); j++ ) {
                JSONObject eachInstrument = (JSONObject) items.get(j);
                watchlist.addItem("sequenceId", eachInstrument.get("sequenceId"));
                JSONObject instrumentInformation = (JSONObject) eachInstrument.get("instrument");
                watchlist.addSymbol((String) instrumentInformation.get("symbol"), (String) instrumentInformation.get("assetType"));
            }
            watchlists.add(watchlist);
        } 
        return watchlists;
    }

    public List<Order> parseOrders( JSONArray jo ) {
        ArrayList<Order> orders = new ArrayList<>();
        
        for ( int i = 0; i < jo.size(); i++ ) {
            JSONObject orderGet = (JSONObject) jo.get(i);
            Double price = (Double) orderGet.get("price");
            String orderType = (String) orderGet.get("orderType");
            String enteredTime = (String) orderGet.get("enteredTime");
            Double quantity = (Double) orderGet.get("quantity");
            JSONArray orderLegCollection = (JSONArray) orderGet.get("orderLegCollection");
            JSONObject orderLeg = (JSONObject) orderLegCollection.get(0);
            JSONObject instrument = (JSONObject) orderLeg.get("instrument");
            String symbol = (String) instrument.get("symbol");
            String assetType = (String) instrument.get("assetType");

            orders.add(new Order(symbol, assetType, price, orderType, quantity, enteredTime));
        }
        
        return orders;
    }

    public List<Position> parsePositions( JSONObject jo ) {
        ArrayList<Position> positions = new ArrayList<>();

        // Account:
        // {
        //     "securitiesAccount": "The type <securitiesAccount> has the following subclasses [MarginAccount, CashAccount] descriptions are listed below"
        // }

        JSONObject account = (JSONObject) jo.get("securitiesAccount");

        JSONArray positionsArray = (JSONArray) account.get("positions");
        for ( int i = 0; i < positionsArray.size(); i++ ) {
            Position newPosition = new Position();
            JSONObject positionsObject = (JSONObject) positionsArray.get(i);
            newPosition.setAveragePrice((double) positionsObject.get("averagePrice"));
            newPosition.setCurrentDayProfitLoss((double) positionsObject.get("currentDayProfitLoss"));
            newPosition.setCurrentDayProfitLossPercentage((double) positionsObject.get("currentDayProfitLossPercentage"));
            newPosition.setLongQuantity((double) positionsObject.get("longQuantity"));
            JSONObject instrumentJSON = (JSONObject) positionsObject.get("instrument");
            newPosition.setInstrument(parseIndividualInstrument(instrumentJSON));
            positions.add(newPosition);
        }
        return positions;
    }

    public double getVolatility( JSONObject jo, String ticker ) {
        JSONObject quote = (JSONObject) jo.get(ticker);        
        return (double) quote.get("volatility");
    }

    public int getNumCandles() {
        return Historical.candles.size();
    }
}
