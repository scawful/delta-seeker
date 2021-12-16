package org.halext.deltaseeker;

import java.io.IOException;

import org.halext.deltaseeker.service.Client;

import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        DSApp.setRoot("secondary");
    }

    @FXML
    private void switchToChart() throws IOException {
        DSApp.setRoot("chart");
    }

    @FXML
    private void switchToTrainingMode() throws IOException {
        DSApp.setRoot("training");
    }

    @FXML
    private void postAccessToken() throws IOException, Exception {
        Client.postAccessToken();
    }
}
