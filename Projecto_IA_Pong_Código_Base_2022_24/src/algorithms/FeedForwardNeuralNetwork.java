package algorithms;

import java.util.Comparator;

import breakout.BreakoutBoard;
import utils.Commons;
import utils.GameController;

public class FeedForwardNeuralNetwork implements GameController, Comparable<FeedForwardNeuralNetwork> {

	private int inputDim;
	private int hiddenDim;
	private int outputDim;

	private double[][] hiddenWeights;
	private double[] hiddenBiases;
	private double[][] outputWeights;
	private double[] outputBiases;

	public FeedForwardNeuralNetwork() {
		this.inputDim = Commons.BREAKOUT_STATE_SIZE;
		this.hiddenDim = Commons.BREAKOUT_HIDDEN_DIM;
		this.outputDim = Commons.BREAKOUT_NUM_ACTIONS;
		initializeParameters();
	}

	public FeedForwardNeuralNetwork(int ID, int HD, int OD, double[] v) {
		this.inputDim = ID;
		this.hiddenDim = HD;
		this.outputDim = OD;

		int total = (ID + 1) * HD + (HD + 1) * OD;
		if (v.length != total) {
			throw new IllegalArgumentException("Out of Bounds Array");
		}

		initializeParameters(v);
	}

	private void initializeParameters(double[] v) {
		hiddenWeights = new double[inputDim][hiddenDim];
		hiddenBiases = new double[hiddenDim];
		outputWeights = new double[hiddenDim][outputDim];
		outputBiases = new double[outputDim];

		int acc = 0;

		for (int i = 0; i < inputDim; i++) {
			for (int j = 0; j < hiddenDim; j++) {
				hiddenWeights[i][j] = v[acc++];
			}
		}

		for (int i = 0; i < hiddenDim; i++) {
			hiddenBiases[i] = v[acc++];
		}

		for (int i = 0; i < hiddenDim; i++) {
			for (int j = 0; j < outputDim; j++) {
				outputWeights[i][j] = v[acc++];
			}
		}

		for (int i = 0; i < outputDim; i++) {
			outputBiases[i] = v[acc++];
		}

	}

	private void initializeParameters() {
		hiddenWeights = new double[inputDim][hiddenDim];
		hiddenBiases = new double[hiddenDim];
		outputWeights = new double[hiddenDim][outputDim];
		outputBiases = new double[outputDim];

		// Initialize biases for hidden layer with a small positive value
		for (int i = 0; i < hiddenDim; i++) {
			hiddenBiases[i] = (Math.random() * 0.7) + 0.15;
		}

		for (int i = 0; i < outputDim; i++) {
			outputBiases[i] = (Math.random() * 0.7) + 0.15;
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

	public double[] forward(int[] currentState) {
		if (hiddenWeights == null) { // Check if hiddenWeights is null before access
			throw new RuntimeException("Neural network not initialized properly. hiddenWeights is null.");
		}
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
		return outputLayer;
	}

	private double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x));
	}

	public double[] getNNArray() {
		double[] res = new double[(this.inputDim + 1) * this.hiddenDim + (this.hiddenDim + 1) * this.outputDim];

		int acc = 0;

		for (int i = 0; i < inputDim; i++) {
			for (int j = 0; j < hiddenDim; j++) {
				res[acc++] = hiddenWeights[i][j];
			}
		}

		for (int i = 0; i < hiddenDim; i++) {
			res[acc++] = hiddenBiases[i];
		}

		for (int i = 0; i < hiddenDim; i++) {
			for (int j = 0; j < outputDim; j++) {
				res[acc++] = outputWeights[i][j];
			}
		}

		for (int i = 0; i < outputDim; i++) {
			res[acc++] = outputBiases[i];
		}

		return res;

	}

	public double[] getHiddenBiases() {
		return hiddenBiases;
	}

	public double[] getOutputBiases() {
		return outputBiases;
	}

	public double calculateFitness() {
		BreakoutBoard b = new BreakoutBoard(this, false, Commons.SEED);
		b.runSimulation();
		return b.getFitness();
	}

	@Override
	public int nextMove(int[] currentState) {
		double[] outputLayer = forward(currentState);
		// Determine the action based on output values
		if (outputLayer[0] < outputLayer[1]) {
			return BreakoutBoard.RIGHT; // Move to the right
		} else {
			return BreakoutBoard.LEFT; // Move to the left
		}

	}
	
	public class FitnessComparator implements Comparator<FeedForwardNeuralNetwork> {
		@Override
		public int compare(FeedForwardNeuralNetwork o1, FeedForwardNeuralNetwork o2) {
			// Sort by fitness in descending order (higher fitness comes first)
			if (o1.calculateFitness() > o2.calculateFitness()) {
				return -1;
			} else if (o1.calculateFitness() < o2.calculateFitness()) {
				return 1;
			} else {
				return 0; // Handle equal fitness cases if necessary
			}
		}
	}
	
	@Override
	public int compareTo(FeedForwardNeuralNetwork feedForwardNeuralNetwork) {
		if (this.calculateFitness() == feedForwardNeuralNetwork.calculateFitness()) {
			return 0;
		} else if (this.calculateFitness() <= feedForwardNeuralNetwork.calculateFitness()) {
			return 1;
		}
		return -1;
	}

}

