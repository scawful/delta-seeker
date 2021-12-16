package org.halext.deltaseeker;

import java.io.IOException;

import org.halext.deltaseeker.service.Model;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class TrainingController {

    @FXML TextField tickerInput;

    @FXML
    private void generateModel() throws IOException {
       
        Model.createPriceHistory( tickerInput.getText() );
    }
}
