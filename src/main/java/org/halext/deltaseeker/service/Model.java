package org.halext.deltaseeker.service;

import java.io.IOException;

public class Model {
    
    public static void createPriceHistory( String ticker ) throws IOException {
        try {
            Parser.parsePriceHistory( Client.getPriceHistory( ticker ) );
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }
}
