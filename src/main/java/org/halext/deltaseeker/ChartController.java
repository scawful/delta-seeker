package org.halext.deltaseeker;

import java.io.IOException;

import javafx.fxml.FXML;

public class ChartController {

    @FXML
    private void switchToPrimary() throws IOException {
        DSApp.setRoot("primary");
    }
}

