package org.halext.deltaseeker.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.halext.deltaseeker.service.data.Historical;
import org.json.simple.parser.ParseException;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.error.MeanSquaredError;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

public class Model {

    // Bounds for normalization
    private static final double LOWER_BOUND = 0.1;
    private static final double UPPER_BOUND = 0.8;

    // NeuralNetwork components 
    private NeuralNetwork<BackPropagation> neuralNet;
    private TransferFunctionType transferFunction;
    private DataSet trainingSet;

    private ArrayList<String> transferFunctionList;

    // API Objects 
    private Parser parser;
    private Client client;

    private String ticker;
    private Integer numInputs;
    private Integer numOutputs;
    private int[] inputs;
    private int[] outputs;
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

    /**
     * Create an input vector for the neural network at a specific position
     * 0 - open
     * 1 - close 
     * 2 - high 
     * 3 - low 
     * 
     * @param pos
     * @return
     */
    private double[] createInputVector(int pos) {
        ArrayList<Double> normalizedPrices = new ArrayList<>();
        for ( int i = 0; i < numInputs; i++ ) {
            switch ( inputs[i] ) {
                case 0:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos).getOpen(), Historical.getMaxOpen()));
                    break;
                case 1:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos).getClose(), Historical.getMaxClose()));
                    break;
                case 2:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos).getHigh(), Historical.getMaxHigh()));
                    break;
                case 3:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos).getLow(), Historical.getMaxLow()));
                    break;
                case 4:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos).getVolume(), Historical.getMaxVolume()));
                    break;
                default:
                    break;
            }
        }
        return normalizedPrices.stream().mapToDouble(i -> i).toArray();
    }

    /**
     * Create an output vector 
     * 
     * @param pos
     * @param offset
     * @return
     */
    private double[] createOutputVector(int pos, int offset) {
        ArrayList<Double> normalizedPrices = new ArrayList<>();
        for ( int i = 0; i < numOutputs; i++ ) {
            switch ( outputs[i] ) {
                case 0:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos + offset).getOpen(), Historical.getMaxOpen()));
                    break;
                case 1:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos + offset).getClose(), Historical.getMaxClose()));
                    break;
                case 2:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos + offset).getHigh(), Historical.getMaxHigh()));
                    break;
                case 3:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos + offset).getLow(), Historical.getMaxLow()));
                    break;
                case 4:
                    normalizedPrices.add(normalizePrice(Historical.candles.get(pos + offset).getVolume(), Historical.getMaxVolume()));
                    break;
                default:
                    break;
            }
        }
        return normalizedPrices.stream().mapToDouble(i -> i).toArray();
    }

    private void outputPredictions(int pos, double[] networkOutput) {
        for ( int i = 0; i < numOutputs; i++ ) {
            switch ( outputs[i] ) {
                case 0: 
                    double denormalizedPrediction = denormalizePrice(networkOutput[i], Historical.getMaxOpen());
                    debugModelOutput += "[Open] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    debugModelOutput += " - Actual: " + Historical.candles.get(pos).getOpen() + "\n";
                    break;
                case 1:
                    denormalizedPrediction = denormalizePrice(networkOutput[i], Historical.getMaxClose());
                    debugModelOutput += "[Close] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    debugModelOutput += " - Actual: " + Historical.candles.get(pos).getClose() + "\n";
                    break;
                case 2:
                    denormalizedPrediction = denormalizePrice(networkOutput[i], Historical.getMaxHigh());
                    debugModelOutput += "[High] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    debugModelOutput += " - Actual: " + Historical.candles.get(pos).getHigh() + "\n";
                    break;
                case 3:
                    denormalizedPrediction = denormalizePrice(networkOutput[i], Historical.getMaxLow());
                    debugModelOutput += "[Low] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    debugModelOutput += " - Actual: " + Historical.candles.get(pos).getLow() + "\n";
                    break;
                case 4:
                    denormalizedPrediction = denormalizePrice(networkOutput[i], Historical.getMaxVolume());
                    debugModelOutput += "[Volume] Predicted: " + BigDecimal.valueOf(denormalizedPrediction).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    debugModelOutput += " - Actual: " + Historical.candles.get(pos).getVolume() + "\n";
                    break;
                default:
                    break;
            }
        }
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
            this.ticker = ticker;
            client.retrieveKeyFile();
            parser.parsePriceHistory( client.getPriceHistory( ticker, "ytd", "1", "daily", "1", true ) );
            parser.parseInstrumentData( client.getInstrument(ticker), ticker );
            debugModelOutput += "Data Set: YTD DAILY\n";

            loadDataSet(new int[]{ 0 }, new int[]{ 1, 2, 3 }, 0);
            // loadDataSet(new int[]{ 0, 1, 2, 3 }, new int[]{ 0, 1, 2, 3 }, 1);

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    public void loadDataSet(int[] input, int[] output, int outputOffset) {
        // create training set
        this.numInputs = input.length;
        this.numOutputs = output.length;
        this.inputs = input;
        this.outputs = output;
        this.trainingSet = new DataSet(numInputs, numOutputs);

        int n = parser.getNumCandles();
        for ( int i = 0; i < n - 1; i++ ) {
            trainingSet.addRow(new DataSetRow(createInputVector(i), createOutputVector(i, outputOffset)));
        }

    }

    public void trainNetwork(int maxIterations, double maxError, double learningRate) {
        neuralNet = new MultiLayerPerceptron(transferFunction, numInputs, 9, numOutputs);      

        // learn the training set
        BackPropagation backPropagation = new BackPropagation();
        backPropagation.setMaxIterations(maxIterations);
        backPropagation.setMaxError(maxError);
        backPropagation.setLearningRate(learningRate);
        neuralNet.learn(trainingSet, backPropagation);

        // save the trained network into file
        neuralNet.save("or_perceptron.nnet");
    }

    @SuppressWarnings("unchecked")
    public void loadNetwork() throws IOException, ParseException {
        // load the saved network
        int n = parser.getNumCandles();
        neuralNet = MultiLayerPerceptron.createFromFile("or_perceptron.nnet");

        // normalize test input for model to attempt to predict 
        double[] inputVector = createInputVector(n - 1);

        neuralNet.setInput( inputVector );

        // calculate network
        neuralNet.calculate();

        // get network output
        double[] networkOutput = neuralNet.getOutput();

        // report predictions to debug model output 
        debugModelOutput += "Open: " + Historical.candles.get(n - 1).getOpen() + " on ";
        debugModelOutput += Historical.candles.get(n - 1).getDatetime().toString().substring(0, 10);
        debugModelOutput += " " + Historical.candles.get(n - 1).getDatetime().toString().substring(
                            Historical.candles.get(n - 1).getDatetime().toString().length() - 4, 
                            Historical.candles.get(n - 1).getDatetime().toString().length()
                            ) + "\n";

        outputPredictions(n - 1, networkOutput);
        debugModelOutput += "Volatility: " + parser.getVolatility( client.getQuote( ticker ), ticker ) + "\n";
    }

    public void evaluateNetwork() {

        // debugModelOutput += "Performance indicators for neural network.\n";

        MeanSquaredError mse = new MeanSquaredError();

        for (DataSetRow testSetRow : trainingSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            double[] desiredOutput = testSetRow.getDesiredOutput();
            mse.calculatePatternError(networkOutput, desiredOutput);
        }

        debugModelOutput += "Mean squared error is: " + mse.getTotalError();
    }

    
    public void setTransferFunctionType( String function ) {
        if ( function == "GAUSSIAN" ) {
            transferFunction = TransferFunctionType.GAUSSIAN;
        } else if ( function == "LINEAR") {
            transferFunction = TransferFunctionType.LINEAR;
        } else if ( function == "LOG") {
            transferFunction = TransferFunctionType.LOG;
        } else if ( function == "RAMP") {
            transferFunction = TransferFunctionType.RAMP;
        } else if ( function == "SGN") {
            transferFunction = TransferFunctionType.SGN;
        } else if ( function == "SIN") {
            transferFunction = TransferFunctionType.SIN;
        } else if ( function == "STEP") {
            transferFunction = TransferFunctionType.STEP;
        } else if ( function == "TANH") {
            transferFunction = TransferFunctionType.TANH;
        } else if ( function == "TRAPEZOID") {
            transferFunction = TransferFunctionType.TRAPEZOID;
        } else {
            transferFunction = TransferFunctionType.SIGMOID;
        }
    }

    public String getDebugOutput() {
        return debugModelOutput;
    }

    public void getTransferFunctionList(ArrayList<String> list) {
        this.transferFunctionList = list;
    }
}
