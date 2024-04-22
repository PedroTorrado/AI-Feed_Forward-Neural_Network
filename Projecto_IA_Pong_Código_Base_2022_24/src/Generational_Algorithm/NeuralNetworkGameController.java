package Generational_Algorithm;

import utils.GameController;

public class NeuralNetworkGameController implements GameController {
	private int inputDim;
	private int hiddenDim;
	private int outputDim;
	public double[][] hiddenWeights;
	public double[] hiddenBiases;
	public double[][] outputWeights;
	public double[] outputBiases;
	
	public NeuralNetworkGameController(int inputDim, int hiddenDim, int outputDim, double[] values) {
		
		this.inputDim = inputDim;
		this.hiddenDim = hiddenDim;
		this.outputDim = outputDim;
		
		int index = 0;
		
		hiddenWeights = new double[inputDim][hiddenDim];
		for(int i = 0; i < inputDim; i++) {
			for(int j = 0; j< hiddenDim; j++) {
				hiddenWeights[i][j] = values[index];
				index++;
			}
		}
		
		hiddenBiases = new double[hiddenDim];
		
		for(int i = 0; i < hiddenDim; i++) {
			hiddenBiases[i] = values[index];
			index++;
		}
		
		outputWeights = new double[hiddenDim][outputDim];
		for(int i = 0; i < hiddenDim; i++) {
			for(int j = 0; j< outputDim; j++) {
				outputWeights[i][j] = values[index];
				index++;
			}
		}
		outputBiases = new double[outputDim];
		for(int i = 0; i < outputDim; i++) {
			outputBiases[i] = values[index];
			index++;
		}
		
		//[w1,1; w1,2; w2,1; w2,2; B1; B2; w1,o; w2,o; Bo]
	}
	
	public static NeuralNetworkGameController getNN(int inputDim, int hiddenDim, int outputDim, double[] values) {
        return new NeuralNetworkGameController(inputDim, hiddenDim, outputDim, values);
    }
	
	private static double sigmoid(double x){
	    return 1 / (1 + Math.exp(-x));
	}
	
	public double[] forward(double[] inputValues) {
		double[] valuesHidden = new double[hiddenDim];
		for(int i = 0; i < hiddenDim; i++){
			for(int j = 0; j < inputDim; j++){
				valuesHidden[i] += hiddenWeights[j][i] * inputValues[j]; 
			}
		valuesHidden[i] = sigmoid(valuesHidden[i] + hiddenBiases[i]); 
		}
		
		double[] valuesOutput = new double[outputDim];
		for(int i = 0; i < outputDim; i++){
			for(int j = 0; j < hiddenDim; j++){
				valuesOutput[i] += outputWeights[j][i] * valuesHidden[j]; 
			}
			valuesOutput[i] = sigmoid(valuesOutput[i] + outputBiases[i]); 
		}
		return valuesOutput;
	}
	
	
	@Override
	public String toString() {
		String result = "Neural Network: \nNumber of inputs: " + inputDim + "\n"
				+ "Weights between input and hidden layer with " + hiddenDim + "neurons: \n";
		String hidden = "";
		for (int input = 0; input < inputDim; input++) {
			for (int i = 0; i < hiddenDim; i++) {
				hidden += " w" + (input + 1) + (i + 1) + ": " + hiddenWeights[input][i] + "\n";
			}
		}
		result += hidden;
		String biasHidden = "Hidden biases: \n";
		for (int i = 0; i < hiddenDim; i++) {
			biasHidden += " b " + (i + 1) + ": " + hiddenBiases[i] + "\n";
		}
		result += biasHidden;
		String output = "Weights between hidden and output layer with " + outputDim + " neurons: \n";
		for (int hiddenw = 0; hiddenw < hiddenDim; hiddenw++) {
			for (int i = 0; i < outputDim; i++) {
				output += " w" + (hiddenw + 1) + "o" + (i + 1) + ": " + outputWeights[hiddenw][i] + "\n";
			}
		}
		result += output;
		String biasOutput = "Ouput biases: \n";
		for (int i = 0; i < outputDim; i++) {
			biasOutput += " bo" + (i + 1) + ": " + outputBiases[i] + "\n";
		}
		result += biasOutput;
		return result;
	}

	@Override
	public int nextMove(int[] currentState) {
	    // Preprocess the input state (if needed)
	    double[] inputValues = preprocessInput(currentState);

	    // Forward propagate the input through the neural network
	    double[] outputValues = forward(inputValues);

	    // Interpret the output to determine the next move
	    int nextMove = interpretOutput(outputValues);

	    return nextMove;
	}

	public void setHiddenWeights(double[][] hiddenWeights) {
	    this.hiddenWeights = hiddenWeights;
	}

	public void setOutputWeights(double[][] outputWeights) {
	    this.outputWeights = outputWeights;
	}

}
