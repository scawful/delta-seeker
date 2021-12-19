package org.halext.deltaseeker.service;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.halext.deltaseeker.service.data.Quote;
import org.halext.deltaseeker.service.data.Historical;
import org.halext.deltaseeker.service.data.Instrument;
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

        JSONObject masterObject = (JSONObject) jo.get(ticker);
        JSONObject fundamentalObject = (JSONObject) masterObject.get("fundamental");

        Instrument.setSymbol((String) fundamentalObject.remove("symbol"));

        for (Object key : fundamentalObject.keySet()) {
            Instrument.insertFundamental((String) key, fundamentalObject.get(key));
        }
    }

    public void parseQuoteData( JSONObject jo ) {
        
    }

    public ArrayList<Watchlist> parseWatchlistData( JSONArray jo ) {
        ArrayList<Watchlist> watchlists = new ArrayList<Watchlist>();

        for ( int i = 0; i < jo.size(); i++ ) {
            JSONObject wrapper = (JSONObject) jo.get(i);
            Watchlist watchlist = new Watchlist((String)wrapper.get("name"), (String)wrapper.get("watchlistId"));

            JSONArray items = (JSONArray) wrapper.get("watchlistItems");
            for ( int j = 0; j < items.size(); j++ ) {
                JSONObject eachInstrument = (JSONObject) items.get(j);
                watchlist.addItem("sequenceId", eachInstrument.get("sequenceId"));
                JSONObject instrumentInformation = (JSONObject) eachInstrument.get("instrument");
                watchlist.addSymbol((String) instrumentInformation.get("symbol"));
                watchlist.addItem("assetType", instrumentInformation.get("assetType"));
            }
            watchlists.add(watchlist);
        } 
        return watchlists;
    }

    public double getVolatility( JSONObject jo, String ticker ) {
        JSONObject quote = (JSONObject) jo.get(ticker);        
        return (double) quote.get("volatility");
    }

    public int getNumCandles() {
        return Historical.candles.size();
    }
}
