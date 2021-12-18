package org.halext.deltaseeker.service;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.util.Date;
import java.util.Iterator;

import org.halext.deltaseeker.service.data.Historical;
import org.halext.deltaseeker.service.data.Quote;
import org.halext.deltaseeker.service.data.Instrument;

public class Parser {

    public Parser() {

    }
    
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

    // @SuppressWarnings("unchecked")
    public void parseInstrumentData( JSONObject jo ) {

        JSONObject masterObject = (JSONObject) jo.get("TLT");
        JSONObject fundamentalObject = (JSONObject) masterObject.get("fundamental");

        Instrument.setSymbol((String) fundamentalObject.remove("symbol"));

        for (Object key : fundamentalObject.keySet()) {
            Instrument.insertFundamental((String) key, fundamentalObject.get(key));
        }
    }

    public double getVolatility( JSONObject jo ) {
        JSONObject quote = (JSONObject) jo.get("TLT");        
        return (double) quote.get("volatility");
    }

    public int getNumCandles() {
        return Historical.candles.size();
    }
}
