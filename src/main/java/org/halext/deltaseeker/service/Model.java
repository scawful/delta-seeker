package org.halext.deltaseeker.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.halext.deltaseeker.service.data.Historical;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.error.MeanSquaredError;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

public class Model {

    // Bounds for normalization
    private static final double LOWER_BOUND = 0.1;
    private static final double UPPER_BOUND = 0.8;

    // NeuralNetwork components 
    private NeuralNetwork<BackPropagation> neuralNet;
    private DataSet trainingSet;

    // API Objects 
    private Parser parser;
    private Client client;

    private String debugModelOutput;


    /**
     * Normalize price using maxiumum value and bounds for very small or very large values 
     * @param price
     * @param max
     * @return
     */
    private static double normalizePrice( double price, double max ) {
        return (price / max) * (UPPER_BOUND + LOWER_BOUND);
    }

    /**
     * Denormalize price from normalizePrice routine 
     * @param price
     * @param max
     * @return
     */
    private static double denormalizePrice( double price, double max ) {
        return (price * max) / (UPPER_BOUND + LOWER_BOUND);
    }

    public Model() {
        neuralNet = new NeuralNetwork<>();
        client = new Client();
        parser = new Parser();
        trainingSet = null;
        debugModelOutput = "";
    }
    
    public void createPriceHistory( String ticker ) throws IOException {
        try {
            client.retrieveKeyFile();
            parser.parsePriceHistory( client.getPriceHistory( ticker, "ytd", "1", "daily", "1", true ) );
            System.out.println("Volatility: " + parser.getVolatility( client.getQuote( ticker )));

            loadDataSet();
            trainNetwork();
            loadNetwork();
            evaluateNetwork();
            
            parser.parseInstrumentData( client.getInstrument(ticker) );
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    public void loadDataSet() {
        // create training set
        trainingSet = new DataSet(1, 3);

        int n = parser.getNumCandles();
        for ( int i = 0; i < n - 1; i++ ) {
            double openNormal = normalizePrice(Historical.candles.get(i).getOpen(), Historical.getMaxOpen());
            double closeNormal = normalizePrice(Historical.candles.get(i).getClose(), Historical.getMaxClose());
            double highNormal = normalizePrice(Historical.candles.get(i).getHigh(), Historical.getMaxHigh());
            double lowNormal = normalizePrice(Historical.candles.get(i).getLow(), Historical.getMaxLow());
            trainingSet.addRow(new DataSetRow(new double[] { openNormal } , 
                                                new double[] { closeNormal, highNormal, lowNormal } ));
        }

    }

    public void trainNetwork() {
        neuralNet = new MultiLayerPerceptron(1, 9, 3);      

        // learn the training set
        BackPropagation backPropagation = new BackPropagation();
        backPropagation.setMaxIterations(1000);
        backPropagation.setMaxError(0.001);
        backPropagation.setLearningRate(0.7);
        neuralNet.learn(trainingSet, backPropagation);

        // save the trained network into file
        neuralNet.save("or_perceptron.nnet");
    }

    @SuppressWarnings("unchecked")
    public void loadNetwork() {
        // load the saved network
        int n = parser.getNumCandles();
        neuralNet = MultiLayerPerceptron.createFromFile("or_perceptron.nnet");

        double openNormal = normalizePrice(Historical.candles.get(n - 1).getOpen(), Historical.getMaxOpen());
        neuralNet.setInput( openNormal );
        // System.out.println("Open: " + Historical.candles.get(n - 1).getOpen() + " on " + Historical.candles.get(n - 1).getDatetime().toString());
        debugModelOutput += "Open: " + Historical.candles.get(n - 1).getOpen() + " on " + Historical.candles.get(n - 1).getDatetime().toString() + "\n";

        // calculate network
        neuralNet.calculate();

        // get network output
        double[] networkOutput = neuralNet.getOutput();
        double denormalizedPrediction = denormalizePrice(networkOutput[0], Historical.getMaxOpen());
        System.out.print("[Close] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(3, RoundingMode.HALF_UP)
        .doubleValue());
        System.out.println(" - Actual: " + Historical.candles.get(n - 1).getClose());

        debugModelOutput += "[Close] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(3, RoundingMode.HALF_UP)
        .doubleValue();
        debugModelOutput += " - Actual: " + Historical.candles.get(n - 1).getClose() + "\n";

        denormalizedPrediction = denormalizePrice(networkOutput[1], Historical.getMaxHigh());
        System.out.print("[High] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(3, RoundingMode.HALF_UP)
        .doubleValue());
        System.out.println(" - Actual: " + Historical.candles.get(n-1).getHigh() );

        debugModelOutput += "[High] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(3, RoundingMode.HALF_UP)
        .doubleValue();
        debugModelOutput += " - Actual: " + Historical.candles.get(n-1).getHigh() + "\n";

        denormalizedPrediction = denormalizePrice(networkOutput[2], Historical.getMaxLow());
        System.out.print("[Low] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(3, RoundingMode.HALF_UP)
        .doubleValue());
        System.out.println(" - Actual: " + Historical.candles.get(n-1).getLow() );

        debugModelOutput += "[Low] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(3, RoundingMode.HALF_UP)
        .doubleValue();
        debugModelOutput += " - Actual: " + Historical.candles.get(n-1).getLow() + "\n";
    }

    public void evaluateNetwork() {

        System.out.println("Calculating performance indicators for neural network.");

        MeanSquaredError mse = new MeanSquaredError();

        for (DataSetRow testSetRow : trainingSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            double[] desiredOutput = testSetRow.getDesiredOutput();
            mse.calculatePatternError(networkOutput, desiredOutput);
        }

        System.out.println("Mean squared error is: " + mse.getTotalError());
    }

    public String getDebugOutput() {
        return debugModelOutput;
    }
}
