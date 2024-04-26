package Generational_Algorithm;

import breakout.BreakoutBoard;
import utils.Commons;
import utils.GameController;

public class NeuralNetworkGameController implements GameController {

	private int inputDim;
	private int hiddenDim;
	private int outputDim;

	double[][] hiddenWeights;
	private double[] hiddenBiases;
	double[][] outputWeights;
	private double[] outputBiases;

	public NeuralNetworkGameController() {
		this.inputDim = Commons.BREAKOUT_STATE_SIZE;
		this.hiddenDim = 10; // Example value, adjust as needed
		this.outputDim = Commons.BREAKOUT_NUM_ACTIONS;
		initializeParameters();
	}

	private void initializeParameters() {
		hiddenWeights = new double[inputDim][hiddenDim];
		hiddenBiases = new double[hiddenDim];
		outputWeights = new double[hiddenDim][outputDim];
		outputBiases = new double[outputDim];

		// Initialize biases for hidden layer with a small positive value
		for (int i = 0; i < hiddenDim; i++) {
			hiddenBiases[i] = 0.1;
		}

		// Glorot initialization for weights
		double weightScale = Math.sqrt(2.0 / (inputDim + hiddenDim));
		for (int i = 0; i < inputDim; i++) {
			for (int j = 0; j < hiddenDim; j++) {
				hiddenWeights[i][j] = Math.random() * weightScale * 2 - weightScale;
			}
		}

		weightScale = Math.sqrt(2.0 / (hiddenDim + outputDim));
		for (int i = 0; i < hiddenDim; i++) {
			for (int j = 0; j < outputDim; j++) {
				outputWeights[i][j] = Math.random() * weightScale * 2 - weightScale;
			}
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

	public void setHiddenWeights(double[][] hiddenWeights) {
		this.hiddenWeights = hiddenWeights;
	}

	// Getter and setter methods for outputWeights
	public double[][] getOutputWeights() {
		return outputWeights;
	}

	public double[][] setOutputWeights(int layer) {
		return this.outputWeights;
	}

	public NeuralNetworkGameController(NeuralNetworkGameController other) {
		// Deep copy hidden and output weights
		this.hiddenWeights = new double[other.hiddenWeights.length][];
		for (int i = 0; i < other.hiddenWeights.length; i++) {
			this.hiddenWeights[i] = new double[other.hiddenWeights[i].length];
			for (int j = 0; j < other.hiddenWeights[i].length; j++) {
				this.hiddenWeights[i][j] = other.hiddenWeights[i][j];
			}
		}

		// Deep copy for output weights
		this.outputWeights = new double[other.outputWeights.length][];
		for (int i = 0; i < other.outputWeights.length; i++) {
			this.outputWeights[i] = new double[other.outputWeights[i].length];
			System.arraycopy(other.outputWeights[i], 0, this.outputWeights[i], 0, other.outputWeights[i].length);
		}

		// Deep copy for hidden biases (assuming other.hiddenBiases is not null)
		this.hiddenBiases = new double[other.hiddenBiases.length];
		System.arraycopy(other.hiddenBiases, 0, this.hiddenBiases, 0, other.hiddenBiases.length);

		// Deep copy for output biases (assuming other.outputBiases is not null)
		this.outputBiases = new double[other.outputBiases.length];
		System.arraycopy(other.outputBiases, 0, this.outputBiases, 0, other.outputBiases.length);
	}

	public double[] getHiddenBiases() {
		return hiddenBiases;
	}

	public double[] getOutputBiases() {
		return outputBiases;
	}

	public int getInputSize() {
		return inputDim;
	}

	public int getHiddenLayerSize() {
		return hiddenDim;
	}

	public int getOutputSize() {
		return outputDim;
	}

	public void setWeights(int layer, double[][] weights) {
		if (layer >= 0 && layer < this.getNumLayers()) {
			// Check for valid layer index
			if (layer == this.getNumLayers() - 1) {
				// Assuming weights are for the output layer (modify if using for hidden layers)
				this.outputWeights = weights;
			} else {
				// Otherwise, assign to hidden weights
				this.hiddenWeights = weights;
			}
		} else {
			throw new IllegalArgumentException("Invalid layer index: " + layer);
		}
	}

	public double[] getBiases(int layer) {
		if (layer >= 0 && layer < this.getNumLayers()) {
			// Check for valid layer index
			if (layer == this.getNumLayers() - 1) {
				// Assuming biases are for hidden layers, return hidden biases for the last
				// layer
				return this.hiddenBiases;
			} else {
				// Otherwise, return hidden biases
				return this.hiddenBiases; // Modify to return output biases if using output biases
			}
		} else {
			throw new IllegalArgumentException("Invalid layer index: " + layer);
		}
	}

	public void setBiases(int layer, double[] biases) {
		if (layer >= 0 && layer < this.getNumLayers()) {
			// Check for valid layer index
			if (layer == this.getNumLayers() - 1) {
				// Assuming biases are for hidden layers, set hidden biases for the last layer
				this.hiddenBiases = biases;
			} else {
				// Otherwise, set hidden biases
				this.hiddenBiases = biases; // Modify to set output biases if using output biases
			}
		} else {
			throw new IllegalArgumentException("Invalid layer index: " + layer);
		}
	}

	public int getNumLayers() {
		// Assuming there are two hidden layers (input + hidden + output)
		return 3; // Modify if the network structure differs
	}

	public double[][] getWeights(int layer) {
		if (layer < 0 || layer >= this.hiddenWeights.length) {
			throw new IllegalArgumentException("Invalid layer index: " + layer);
		}
		return this.hiddenWeights; // Return the entire inner array for the specified layer
	}
}
