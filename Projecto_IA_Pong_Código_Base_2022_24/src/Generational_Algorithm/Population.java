package Generational_Algorithm;

import java.util.ArrayList;
import java.util.List;

import breakout.BreakoutBoard;

public class Population {

	private List<NeuralNetworkGameController> NeuralNetworkList;
	private NeuralNetworkGameController bestIndividual;
	private double bestFitness;
	private static int seed;

	public Population(int size, NeuralNetworkGameController nnController) {
		NeuralNetworkList = new ArrayList<>();
		initializePopulation(size, nnController);
		bestIndividual = null;
		bestFitness = Double.MIN_VALUE;
		seed = this.seed;
	}

	private void initializePopulation(int size, NeuralNetworkGameController nnController) {
		for (int i = 0; i < size; i++) {
			seed = (int) (Math.random() * 1000); // Maintain randomization for potential diversity

			// Assuming NeuralNetworkGameController has constructors for network size
			NeuralNetworkGameController agent = new NeuralNetworkGameController();
			// Access weight and bias arrays from agent (assuming appropriate getter/setter
			// methods)
			double[][] hiddenWeights = agent.hiddenWeights;
			double[][] outputWeights = agent.outputWeights;
			double[] hiddenBiases = agent.getHiddenBiases();
			double[] outputBiases = agent.getOutputBiases();

			// Random initialization with appropriate scaling (e.g., Xavier/He)
			randomizeWeightsAndBiases(hiddenWeights, outputWeights, hiddenBiases, outputBiases);

			NeuralNetworkList.add(agent);
		}
	}

	// Helper method for random initialization (replace with your preferred scaling
	// strategy)
	private void randomizeWeightsAndBiases(double[][] hiddenWeights, double[][] outputWeights, double[] hiddenBiases,
			double[] outputBiases) {
		// Choose your preferred initialization strategy here
		double upperBound = Math.sqrt(6.0 / (hiddenWeights[0].length + 1)); // Xavier initialization
		double lowerBound = -upperBound;

		for (int i = 0; i < hiddenWeights.length; i++) {
			for (int j = 0; j < hiddenWeights[i].length; j++) {
				hiddenWeights[i][j] = Math.random() * (upperBound - lowerBound) + lowerBound;
			}
		}

		// Similar logic for output weights and biases (adjust upperBound/lowerBound if
		// needed)
		for (int i = 0; i < outputWeights.length; i++) {
			for (int j = 0; j < outputWeights[i].length; j++) {
				outputWeights[i][j] = Math.random() * (upperBound - lowerBound) + lowerBound;
			}
		}

		for (int i = 0; i < hiddenBiases.length; i++) {
			hiddenBiases[i] = Math.random() * (upperBound - lowerBound) + lowerBound;
		}

		for (int i = 0; i < outputBiases.length; i++) {
			outputBiases[i] = Math.random() * (upperBound - lowerBound) + lowerBound;
		}
	}

	public double[] evaluateFitness() {
		double[] fitnessValues = new double[NeuralNetworkList.size()];

		for (int i = 0; i < NeuralNetworkList.size(); i++) {
			BreakoutBoard breakoutBoard = new BreakoutBoard(NeuralNetworkList.get(i), false, seed);
			breakoutBoard.runSimulation();
			double fitness = breakoutBoard.getFitness();
			fitnessValues[i] = fitness;

			// Update best individual and its fitness
			if (fitness > bestFitness) {
				bestFitness = fitness;
				bestIndividual = NeuralNetworkList.get(i);
			}
		}

		// Identify top '2' fittest individuals
		int numElite = 2;
		List<NeuralNetworkGameController> bestFromPreviousGen = new ArrayList<>(NeuralNetworkList.subList(0, numElite));

		// Add elite individuals to the beginning of the NeuralNetworkList
		NeuralNetworkList.addAll(0, bestFromPreviousGen);

		// Calculate average fitness
		double totalFitness = 0;
		for (double fitness : fitnessValues) {
			totalFitness += fitness;
		}
		double averageFitness = totalFitness / fitnessValues.length;

		return fitnessValues;
	}

	public List<NeuralNetworkGameController> selectParents(double[] fitnessValues) {
		List<NeuralNetworkGameController> parents = new ArrayList<>();
		int numParents = 2; // Number of parents to select

		// Find the 2 best parents based on fitness:
		int bestIndex1 = -1, bestIndex2 = -1; // Indexes of the best parents
		double bestFitness1 = Double.NEGATIVE_INFINITY, bestFitness2 = Double.NEGATIVE_INFINITY;

		fitnessValues = filterFitnessValues(fitnessValues, 99259);
		
		// Find the 2 best agents (assuming NeuralNetworkList provides access to
		// NeuralNetworkGameController objects):
		for (int i = 0; i < fitnessValues.length; i++) {
			if (fitnessValues[i] > bestFitness1) { // Check for strictly greater than
				bestFitness2 = bestFitness1;
				bestIndex2 = bestIndex1;

				bestFitness1 = fitnessValues[i];
				bestIndex1 = i;
			} else if (fitnessValues[i] > bestFitness2 && fitnessValues[i] != bestFitness1) { // Ensure not the same
																								// fitness value
				bestFitness2 = fitnessValues[i];
				bestIndex2 = i;
			}
		}

		// Check if any valid parents were found:
		if (bestIndex1 == -1 || bestIndex2 == -1) {
			System.err.println(
					"Error: No valid parents found with distinct fitness values. Consider adjusting fitness values.");
			return parents;
		}

		// Add the best 2 parents to the list (assuming NeuralNetworkList provides
		// access):
		parents.add(NeuralNetworkList.get(bestIndex1));
		parents.add(NeuralNetworkList.get(bestIndex2));

		return parents;
	}

	public double[] filterFitnessValues(double[] fitnessValues, double threshold) {
		  // Create a new array to store the filtered fitness values
		  int filteredCount = 0; // Keep track of the number of elements above the threshold
		  for (double fitness : fitnessValues) {
		    if (fitness > threshold) {
		      filteredCount++;
		    }
		  }

		  double[] filteredFitness = new double[filteredCount]; // Allocate correct size
		  int j = 0; // Index for the filtered array

		  // Populate the filtered array with elements exceeding the threshold
		  for (double fitness : fitnessValues) {
		    if (fitness > threshold) {
		      filteredFitness[j++] = fitness;
		    }
		  }

		  return filteredFitness;
		}

	public List<NeuralNetworkGameController> crossoverAndMutation(List<NeuralNetworkGameController> parents) {
		List<NeuralNetworkGameController> offspring = new ArrayList<>();

		NeuralNetworkGameController parent1 = parents.get(0);
		NeuralNetworkGameController parent2 = parents.get(1);

		NeuralNetworkGameController child1 = crossover(parent1, parent2);
		NeuralNetworkGameController child2 = crossover(parent2, parent1);
		mutation(child1);
		mutation(child2);

		offspring.add(child1);
		offspring.add(child2);
		return offspring;
	}

	public static NeuralNetworkGameController crossover(NeuralNetworkGameController parent1,
			NeuralNetworkGameController parent2) {

		// Select a random crossover point (excluding the first and last layers)
		int crossoverPoint = (int) (Math.random() * (parent1.getNumLayers() - 2)) + 1;

		System.out.println("----- Crossover Details -----");
		System.out.println("Parent 1 Layers: " + parent1.getNumLayers());
		System.out.println("Parent 2 Layers: " + parent2.getNumLayers());

		BreakoutBoard parent1Game = new BreakoutBoard(parent1, false, seed); // Set appropriate parameters for
																				// parent1Game
		BreakoutBoard parent2Game = new BreakoutBoard(parent2, false, seed); // Set appropriate parameters for
																				// parent2Game

		parent1Game.runSimulation();
		parent2Game.runSimulation();

		System.out.println("Parent 1 Fitness: " + parent1Game.getFitness()); 
		System.out.println("Parent 2 Fitness: " + parent2Game.getFitness()); 
		
		// Create a new child network with the same structure as parents
		NeuralNetworkGameController child = parent1; // Ensure correct number of layers for child

		// Copy weights from parent1 up to the crossover point (exclusive)
		for (int layer = 0; layer < 1; layer++) {
			double[][] weights = parent1.getWeights(layer);
			double[] biases = parent1.getBiases(layer);
			System.out.println("Copying weights for layer: " + layer); // Debugging print
			child.setWeights(layer, weights);
			child.setBiases(layer, biases);
		}

		// Copy weights from parent2 starting from the crossover point (inclusive)
		for (int layer = 2; layer < 2; layer++) {
			double[][] weights = parent2.getWeights(layer);
			double[] biases = parent2.getBiases(layer);
			System.out.println("Copying weights for layer: " + layer); // Debugging print
			child.setWeights(layer, weights);
			child.setBiases(layer, biases);
		}

		BreakoutBoard childGame = new BreakoutBoard(child, false, seed); // Set appropriate parameters for childGame
		childGame.runSimulation();

		System.out.println("Child Fitness: " + childGame.getFitness()); // Replace with your fitness evaluation function

		return child;
	}

	private void mutation(NeuralNetworkGameController child) {
		double mutationRate = 0.05; // Adjust as needed

		for (int i = 0; i < child.hiddenWeights.length; i++) {
			for (int j = 0; j < child.hiddenWeights[i].length; j++) {
				if (Math.random() < mutationRate) {
					child.hiddenWeights[i][j] += (Math.random() * 0.5) - 0.25; // Adjust range and offset as needed
				}
			}
		}

		for (int i = 0; i < child.outputWeights.length; i++) {
			for (int j = 0; j < child.outputWeights[i].length; j++) {
				if (Math.random() < mutationRate) {
					child.outputWeights[i][j] += (Math.random() * 0.5) - 0.25; // Adjust range and offset as needed
				}
			}
		}
	}

	public NeuralNetworkGameController getBestIndividual() {
		return bestIndividual;
	}

	public NeuralNetworkGameController getBestNN() {
		// Check if the bestIndividual is set, otherwise return null
		if (bestIndividual != null) {
			return bestIndividual; // No need to create a new instance here
		} else {
			return null;
		}
	}

	public double getBestFitness() {
		return bestFitness;
	}

	public int getSeed() {
		return this.seed;
	}

	public void updatePopulation(List<NeuralNetworkGameController> offspring) {
		// Remove the last (population size - elite individuals) elements from the
		// population
		int numElite = 2; // Assuming you want to keep the top 2 fittest individuals
		int elementsToRemove = numElite;
		for (int i = 0; i < elementsToRemove; i++) {
			NeuralNetworkList.remove(NeuralNetworkList.size() - 1);
		}
		// Add the new offspring to the beginning of the population
		NeuralNetworkList.addAll(0, offspring);
	}

	public int getSize() {
		return NeuralNetworkList.size();
	}

}
