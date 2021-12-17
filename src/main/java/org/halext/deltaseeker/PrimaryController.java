package org.halext.deltaseeker;

import java.io.IOException;

import org.halext.deltaseeker.service.Client;

import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToTrainingMode() throws IOException {
        DeltaSeeker.setRoot("training");
    }

    @FXML
    private void postAccessToken() throws IOException, Exception {
        Client.retrieveKeyFile();
        Client.postAccessToken();
    }
}
