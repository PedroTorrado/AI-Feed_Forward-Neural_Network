package algorithms;

import java.util.Comparator;

import breakout.BreakoutBoard;
import pacman.PacmanBoard;
import utils.Commons;
import utils.GameController;

public class FeedForwardNeuralNetwork implements GameController, Comparable<FeedForwardNeuralNetwork> {

	public int inputDim;
	public int hiddenDim;
	public int outputDim;

	private double[][] hiddenWeights;
	private double[] hiddenBiases;
	private double[][] outputWeights;
	private double[] outputBiases;

	boolean PACMAN = true;

	public FeedForwardNeuralNetwork(int inputDim, int hiddenDim, int outputDim) {
		this.inputDim = inputDim;
		this.hiddenDim = hiddenDim;
		this.outputDim = outputDim;

		hiddenWeights = new double[inputDim][hiddenDim];
		hiddenBiases = new double[hiddenDim];
		outputWeights = new double[hiddenDim][outputDim];
		outputBiases = new double[outputDim];

		initializeParameters();
	}

	private void checkGame() {
		if (this.inputDim == Commons.PACMAN_STATE_SIZE) {
			this.PACMAN = true;
		} else {
			this.PACMAN = false;
		}

	}

	public FeedForwardNeuralNetwork(int ID, int HD, int OD, double[] v) {
		this.inputDim = ID;
		this.hiddenDim = HD;
		this.outputDim = OD;

		hiddenWeights = new double[inputDim][hiddenDim];
		hiddenBiases = new double[hiddenDim];
		outputWeights = new double[hiddenDim][outputDim];
		outputBiases = new double[outputDim];

		initializeParameters(v);
	}

	private void initializeParameters(double[] v) {

		int acc = 0;

		for (int i = 0; i < inputDim; i++) {
			for (int j = 0; j < hiddenDim; j++) {
				// System.out.println(i + " " + j);
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

		// System.out.println("inputDim: " + inputDim);
		// System.out.println("hiddenDim: " + hiddenDim);

		// Compute activations of hidden layer
		for (int i = 0; i < hiddenDim; i++) {
			double sum = 0.0;
			for (int j = 0; j < inputDim; j++) {
				// System.out.println(i + " " + j);
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
		int arraySize = inputDim * hiddenDim + hiddenDim * outputDim + hiddenDim + outputDim;
		double[] res = new double[arraySize];

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

	@Override
	public String toString() {
	  StringBuilder sb = new StringBuilder();

	  // Hidden weights (consider formatting for readability)
	  for (int i = 0; i < hiddenWeights.length; i++) {
	    sb.append(" ");
	    for (int j = 0; j < hiddenWeights[i].length; j++) {
	      sb.append(String.format("%.4f", hiddenWeights[i][j]));
	      if (j < hiddenWeights[i].length - 1) {
	        sb.append(" ");
	      }
	    }
	    sb.append(" ");
	  }

	  // Hidden biases
	  sb.append(" ");
	  for (int i = 0; i < hiddenBiases.length; i++) {
	    sb.append(String.format("%.4f", hiddenBiases[i]));
	    if (i < hiddenBiases.length - 1) {
	      sb.append(" ");
	    }
	  }
	  sb.append(" ");

	  // Output weights
	  for (int i = 0; i < outputWeights.length; i++) {
	    sb.append(" ");
	    for (int j = 0; j < outputWeights[i].length; j++) {
	      sb.append(String.format("%.4f", outputWeights[i][j]));
	      if (j < outputWeights[i].length - 1) {
	        sb.append(" ");
	      }
	    }
	    sb.append(" ");
	  }

	  // Output biases
	  sb.append(" ");
	  for (int i = 0; i < outputBiases.length; i++) {
	    sb.append(String.format("%.4f", outputBiases[i]));
	    if (i < outputBiases.length - 1) {
	      sb.append(" ");
	    }
	  }
	  sb.append(" ");

	  return sb.toString();
	}

	public double[] getHiddenBiases() {
		return hiddenBiases;
	}

	public double[] getOutputBiases() {
		return outputBiases;
	}

	public double calculateFitness() {
		if (!PACMAN) {
			PACMAN = true;
			BreakoutBoard b = new BreakoutBoard(this, false, Commons.SEED);
			b.runSimulation();
			return b.getFitness();
		} else {
			PacmanBoard b = new PacmanBoard(this, false, Commons.SEED);
			b.runSimulation();
			return b.getFitness();
		}
	}

	@Override
	public int nextMove(int[] currentState) {
		checkGame();
		if (PACMAN) {
			double[] outputLayer = forward(currentState);
			// Determine the action based on output values
			if (outputLayer[0] < outputLayer[1]) {
				return PacmanBoard.RIGHT; // Move to the right
			} else if (outputLayer[1] < outputLayer[2]) {
				return PacmanBoard.LEFT; // Move to the left
			} else if (outputLayer[2] < outputLayer[3]) {
				return PacmanBoard.UP; // Move up
			} else {
				return PacmanBoard.DOWN; // Move down
			}
		} else {
			double[] outputLayer = forward(currentState);
			// Determine the action based on output values
			if (outputLayer[0] < outputLayer[1]) {
				return BreakoutBoard.RIGHT; // Move to the right
			} else {
				return BreakoutBoard.LEFT; // Move to the left
			}
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
