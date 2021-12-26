package org.halext.deltaseeker.service.data;

import javafx.beans.property.SimpleDoubleProperty;
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
    private SimpleDoubleProperty totalVolume;
    private SimpleDoubleProperty change;

    public SimpleDoubleProperty getChange() {
        return this.change;
    }

    public void setChange(SimpleDoubleProperty change) {
        this.change = change;
    }

    public SimpleStringProperty getDescription() {
        return this.description;
    }

    public void setDescription(SimpleStringProperty description) {
        this.description = description;
    }

    public SimpleStringProperty getDirection() {
        return this.direction;
    }

    public void setDirection(SimpleStringProperty direction) {
        this.direction = direction;
    }

    public SimpleDoubleProperty getLast() {
        return this.last;
    }

    public void setLast(SimpleDoubleProperty last) {
        this.last = last;
    }

    public SimpleStringProperty getSymbol() {
        return this.symbol;
    }

    public void setSymbol(SimpleStringProperty symbol) {
        this.symbol = symbol;
    }

    public SimpleDoubleProperty getTotalVolume() {
        return this.totalVolume;
    }

    public void setTotalVolume(SimpleDoubleProperty totalVolume) {
        this.totalVolume = totalVolume;
    }



}
