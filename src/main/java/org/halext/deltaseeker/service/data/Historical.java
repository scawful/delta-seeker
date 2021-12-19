package org.halext.deltaseeker.service.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Historical {
    
    static double maxOpen;
    static double maxClose;
    static double maxLow;
    static double maxHigh;
    static double meanClose;
    static double minLow = Double.POSITIVE_INFINITY;
    static long maxVolume;

    public static class Candle {
        long volume;
        double high;
        double low;
        double open;
        double close;
        Long rawDatetime;
        Date datetime;

        Candle(long v, double h, double l, double o, double c, Long rdt, Date dt) {
            this.volume = v;
            this.high = h;
            this.low = l;
            this.open = o;
            this.close = c;
            this.datetime = dt;
            this.rawDatetime = rdt;
        }

        public double getHigh() {
            return this.high;
        }

        public double getLow() {
            return this.low;
        }

        public double getClose() {
            return this.close;
        }

        public double getOpen() {
            return this.open;
        }

        public long getVolume() {
            return this.volume;
        }

        public Date getDatetime() {
            return this.datetime;
        }

    }

    public static class SortCandle implements Comparator<Candle> {
 
        // Method
        // Sorting in ascending order
        public int compare(Candle a, Candle b)
        {
            return a.rawDatetime.compareTo(b.rawDatetime);
        }
    }


    private Historical() {
    }

    public static List<Candle> candles;

    public static void addCandle(long v, double h, double l, double o, double c, Long rdt, Date dt) {
        if ( candles == null ) 
            candles = new ArrayList<>();
        
        if ( o > maxOpen )
            maxOpen = o;

        if ( c > maxClose )
            maxClose = c;

        if ( h > maxHigh )
            maxHigh = h;

        if ( l > maxLow )
            maxLow = l;

        if ( l < minLow ) 
            minLow = l;

        if ( v > maxVolume ) {
            maxVolume = v;
        }

        meanClose += c;

        Candle newCandle = new Candle(v,h,l,o,c,rdt,dt);
        candles.add(newCandle);
    }

    public static double getMaxOpen() {
        return maxOpen;
    }

    public static double getMaxClose() {
        return maxClose;
    }

    public static double getMaxHigh() {
        return maxHigh;
    }

    public static double getMaxLow() {
        return maxLow;
    }

    public static double getMeanClose() {
        return meanClose / candles.size();
    }

    public static double getMinLow() {
        return minLow;
    }

    public static long getMaxVolume() {
        return maxVolume; 
    }

    public static void sortCandles() {
        Collections.sort(candles, new SortCandle());
    }

}
