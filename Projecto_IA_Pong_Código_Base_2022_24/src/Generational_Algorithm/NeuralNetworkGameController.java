package Generational_Algorithm;

import utils.GameController;

public class NeuralNetworkGameController implements GameController{

	private int inputDim;
	private int hiddenDim;
	private int outputDim;
	
	/**
	 * @return the hiddenWeights
	 */
	public double[][] getHiddenWeights() {
		return hiddenWeights;
	}

	/**
	 * @param hiddenWeights the hiddenWeights to set
	 */
	public void setHiddenWeights(double[][] hiddenWeights) {
		this.hiddenWeights = hiddenWeights;
	}


	/**
	 * @return the outputWeights
	 */
	public double[][] getOutputWeights() {
		return outputWeights;
	}

	/**
	 * @param outputWeights the outputWeights to set
	 */
	public void setOutputWeights(double[][] outputWeights) {
		this.outputWeights = outputWeights;
	}



	public double[][] hiddenWeights;
	public double[] hiddenBiases;
	public double[][] outputWeights;
	public double[] outputBiases;
	
	public NeuralNetworkGameController() {
		initializeParameters();
	}
	
	private void initializeParameters() {
		hiddenWeights = new double[hiddenDim][hiddenDim];
		hiddenBiases = new double[hiddenDim];
		outputWeights = new double[outputDim][outputDim];
		outputBiases = new double[outputDim];
		
		for(int i = 0; i < hiddenDim; i++) {
			for(int j = 0; j < hiddenDim; j++) {
				hiddenWeights[i][j] = Math.random()*10;
			}
		}
		for(int i = 0; i < hiddenDim; i++) {
			hiddenBiases[i] = Math.random()*10;
		}
		for(int i = 0; i < outputDim; i++) {
			for(int j = 0; j < outputDim; j++) {
				outputWeights[i][j] = Math.random()*10;
			}
		}
		for(int i = 0; i < outputDim; i++) {
			outputBiases[i] = Math.random()*10;
		}
	}
	
	public double[] forward(int[] inputValues) {
		double[] accHidden = new double[hiddenDim];
		for(int i = 0; i< hiddenDim; i++) {
			for(int j = 0; j<inputDim; j++) {
				accHidden[i] += inputValues[j]*hiddenWeights[j][i];
			}
			accHidden[i] = sigmoid(accHidden[i]+hiddenBiases[i]);
		}
		double[] accOutput = new double[outputDim];
		for(int i= 0; i<outputDim; i++) {
			for(int j=0; j<hiddenDim; j++) {
				accOutput[i] += accHidden[j]*outputWeights[j][i];
			}
			accOutput[i] = sigmoid(accOutput[i]+outputBiases[i]);
		}
		return accOutput;
	}
	
	private double sigmoid(double x) {
		return 1/(1+Math.exp(-x));
	}
	
	
	
//	@Override
//	public String toString() {
//		String result = "Neural Network: \nNumber of inputs: " + inputDim + "\n"
//				+ "Weights between input and hidden layer with " + hiddenDim + " neurons: \n";
//		String hidden = "";
//		for (int input = 0; input < inputDim; input++) {
//			for (int i = 0; i < hiddenDim; i++) {
//				hidden += " w" + (input + 1) + (i + 1) + ": " + hiddenWeights[input][i] + "\n";
//			}
//		}
//		result += hidden;
//		String biasHidden = "Hidden biases: \n";
//		for (int i = 0; i < hiddenDim; i++) {
//			biasHidden += " b " + (i + 1) + ": " + hiddenBiases[i] + "\n";
//		}
//		result += biasHidden;
//		String output = "Weights between hidden and output layer with " + outputDim + " neurons: \n";
//		for (int hiddenw = 0; hiddenw < hiddenDim; hiddenw++) {
//			for (int i = 0; i < outputDim; i++) {
//				output += " w" + (hiddenw + 1) + "o" + (i + 1) + ": " + outputWeights[hiddenw][i] + "\n";
//			}
//		}
//		result += output;
//		String biasOutput = "Ouput biases: \n";
//		for (int i = 0; i < outputDim; i++) {
//			biasOutput += " bo" + (i + 1) + ": " + outputBiases[i] + "\n";
//		}
//		result += biasOutput;
//		return result;
//	}
	public int nextMove(int[] currentState) {
	    double[] forwardResult = forward(currentState);
	    
	    // Check if the forward result array has at least two elements
	    if (forwardResult.length >= 2) {
	        if (forwardResult[0] < forwardResult[1]) {
	            return 1;
	        } else {
	            return 2;
	        }
	    } else {
	        // Handle the case when the forward result array doesn't have enough elements
	        // You might want to return a default value or handle the situation differently based on your application logic
	        return -1; // Default value, change as needed
	    }
	}

}