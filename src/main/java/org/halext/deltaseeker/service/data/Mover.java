package org.halext.deltaseeker.service.data;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Mover {
    //Mover:
    // {
    //     "change": 0,
    //     "description": "string",
    //     "direction": "'up' or 'down'",
    //     "last": 0,
    //     "symbol": "string",
    //     "totalVolume": 0
    // }

    private SimpleStringProperty description;
    private SimpleStringProperty direction;
    private SimpleDoubleProperty last;
    private SimpleStringProperty symbol;
    private SimpleLongProperty totalVolume;
    private SimpleDoubleProperty change;

    public double getChange() {
        return this.change.get();
    }

    public void setChange(double change) {
        this.change = new SimpleDoubleProperty(change);
    }

    public String getDescription() {
        return this.description.get();
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }

    public String getDirection() {
        return this.direction.get();
    }

    public void setDirection(String direction) {
        this.direction = new SimpleStringProperty(direction);
    }

    public double getLast() {
        return this.last.get();
    }

    public void setLast(double last) {
        this.last = new SimpleDoubleProperty(last);
    }

    public String getSymbol() {
        return this.symbol.get();
    }

    public void setSymbol(String symbol) {
        this.symbol = new SimpleStringProperty(symbol);
    }

    public double getTotalVolume() {
        return this.totalVolume.get();
    }

    public void setTotalVolume(long totalVolume) {
        this.totalVolume = new SimpleLongProperty(totalVolume);
    }



}
