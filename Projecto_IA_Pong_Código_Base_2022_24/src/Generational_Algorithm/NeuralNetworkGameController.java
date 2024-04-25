package Generational_Algorithm;

import breakout.BreakoutBoard;
import utils.Commons;
import utils.GameController;

public class NeuralNetworkGameController implements GameController {

    private int inputDim;
    private int hiddenDim;
    private int outputDim;

    private double[][] hiddenWeights;
    private double[] hiddenBiases;
    private double[][] outputWeights;
    private double[] outputBiases;

    public NeuralNetworkGameController(BreakoutBoard breakoutBoard) {
        this.inputDim = Commons.BREAKOUT_STATE_SIZE;
        this.hiddenDim = 1; // Example value, adjust as needed
        this.outputDim = Commons.BREAKOUT_NUM_ACTIONS;
        initializeParameters();
    }

    private void initializeParameters() {
        hiddenWeights = new double[inputDim][hiddenDim];
        hiddenBiases = new double[hiddenDim];
        outputWeights = new double[hiddenDim][outputDim];
        outputBiases = new double[outputDim];

        // Initialize weights and biases with random values
        for (int i = 0; i < inputDim; i++) {
            for (int j = 0; j < hiddenDim; j++) {
                hiddenWeights[i][j] = Math.random() * 100;
            }
        }
        for (int i = 0; i < hiddenDim; i++) {
            hiddenBiases[i] = Math.random() * 100;
        }
        for (int i = 0; i < hiddenDim; i++) {
            for (int j = 0; j < outputDim; j++) {
                outputWeights[i][j] = Math.random() * 100;
            }
        }
        for (int i = 0; i < outputDim; i++) {
            outputBiases[i] = Math.random() * 100;
        }
    }

    public int nextMove(int[] currentState) {
        double[] hiddenLayer = new double[hiddenDim];
        double[] outputLayer = new double[outputDim];

        // Compute activations of hidden layer
        for (int i = 0; i < hiddenDim; i++) {
            double sum = 0.0;
            for (int j = 0; j < inputDim; j++) {
                sum += currentState[j] * hiddenWeights[j][i];
            }
            hiddenLayer[i] = sigmoid(sum + hiddenBiases[i]);
        }

        // Compute activations of output layer
        for (int i = 0; i < outputDim; i++) {
            double sum = 0.0;
            for (int j = 0; j < hiddenDim; j++) {
                sum += hiddenLayer[j] * outputWeights[j][i];
            }
            outputLayer[i] = sigmoid(sum + outputBiases[i]);
        }

        // Determine the action based on output values
        if (outputLayer[0] < outputLayer[1]) {
            return BreakoutBoard.RIGHT; // Move to the right
        } else {
            return BreakoutBoard.LEFT; // Move to the left
        }
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    // Getter and setter methods for hiddenWeights
    public double[][] getHiddenWeights() {
        return hiddenWeights;
    }

    public void setHiddenWeights(double[][] hiddenWeights) {
        this.hiddenWeights = hiddenWeights;
    }

    // Getter and setter methods for outputWeights
    public double[][] getOutputWeights() {
        return outputWeights;
    }

    public void setOutputWeights(double[][] outputWeights) {
        this.outputWeights = outputWeights;
    }
}
