package org.halext.deltaseeker;

import java.io.IOException;

import javax.websocket.DeploymentException;

import org.halext.deltaseeker.service.Client;
import org.json.simple.parser.ParseException;

import javafx.fxml.FXML;

public class PrimaryController {

    Client client = new Client();

    @FXML
    private void switchToTrainingMode() throws IOException {
        DeltaSeeker.setRoot("training");
    }

    @FXML
    private void postAccessToken() throws IOException, org.json.simple.parser.ParseException {
        client.retrieveKeyFile();
        client.postAccessToken();
    }

    @FXML
    private void startStreamingSession() throws IOException, ParseException, DeploymentException, java.text.ParseException {
        client.openStream();
    }
}
