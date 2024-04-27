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
		double upperBound = 1000;
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

	public List<NeuralNetworkGameController> selectParents(double[] fitnessValues, int tournamentSize) {
		List<NeuralNetworkGameController> parents = new ArrayList<>();

		int numParents = 2; // Assuming you want to select 2 parents

		for (int i = 0; i < numParents; i++) {
			// Randomly select individuals for the tournament
			int[] tournamentIndices = new int[tournamentSize];
			for (int j = 0; j < tournamentSize; j++) {
				tournamentIndices[j] = (int) (Math.random() * fitnessValues.length);
			}

			// Find the index of the individual with the highest fitness in the tournament
			int bestIndex = 0;
			for (int j = 1; j < tournamentSize; j++) {
				if (fitnessValues[tournamentIndices[j]] > fitnessValues[tournamentIndices[bestIndex]]) {
					bestIndex = j;
				}
			}

			parents.add(NeuralNetworkList.get(tournamentIndices[bestIndex]));
		}

		return parents;
	}

	public List<NeuralNetworkGameController> crossoverAndMutation(List<NeuralNetworkGameController> parents) {
		List<NeuralNetworkGameController> offspring = new ArrayList<>();

		NeuralNetworkGameController parent1 = parents.get(0);
		NeuralNetworkGameController parent2 = parents.get(1);

		NeuralNetworkGameController child1 = crossover(parent1, parent2);
		mutation(child1);

		offspring.add(child1);
		return offspring;
	}

	public static NeuralNetworkGameController crossover(NeuralNetworkGameController parent1,
			NeuralNetworkGameController parent2) {

		BreakoutBoard parent1Game = new BreakoutBoard(parent1, false, seed); // Set appropriate parameters for
		// parent1Game
		parent1Game.runSimulation();

		BreakoutBoard parent2Game = new BreakoutBoard(parent2, false, seed); // Set appropriate parameters for
		// parent2Game

		parent2Game.runSimulation();

		System.out.println("Parent 1 Fitness: " + parent1Game.getFitness());
		System.out.println("Parent 2 Fitness: " + parent2Game.getFitness());

		// Create a new child network with the same structure as parents
		NeuralNetworkGameController child = new NeuralNetworkGameController();

		// Perform crossover for weights and biases
		for (int layer = 0; layer < child.getNumLayers() - 1; layer++) {
			double[][] weights;
			double[] biases;

			if (layer < parent1.getNumLayers() / 2) {
				// Copy weights and biases from parent1
				weights = parent1.getWeights(layer);
				biases = parent1.getBiases(layer);
			} else {
				// Copy weights and biases from parent2
				weights = parent2.getWeights(layer);
				biases = parent2.getBiases(layer);
			}

			// Set weights and biases for the child
			child.setWeights(layer, weights);
			child.setBiases(layer, biases);

			BreakoutBoard childGame = new BreakoutBoard(child, false, seed); // Set appropriate parameters for childGame
			childGame.runSimulation();

			System.out.println("Child Fitness: " + childGame.getFitness()); // Replace with your fitness evaluation
																			// function
		}

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
		// Ensure parents exist (optional, for safety)
		if (offspring.size() < 2) {
			throw new IllegalArgumentException("Requires at least two offspring for population update");
		}

		// Get current best fitness
		double bestFitness = getBestFitness();

		// Remove the last (population size - elite individuals - 2) elements from the
		// population
		int numElite = 2; // Assuming you already keep the top 2 fittest individuals
		int elementsToRemove = NeuralNetworkList.size() - numElite - offspring.size();
		for (int i = 0; i < elementsToRemove; i++) {
			NeuralNetworkGameController lastIndividual = NeuralNetworkList.remove(NeuralNetworkList.size() - 1);
			BreakoutBoard lastIndiividualBoard = new BreakoutBoard(lastIndividual, false, seed);
			if (lastIndiividualBoard.getFitness() < bestFitness) {
				// Remove individual with lower fitness than bestFitness
				NeuralNetworkList.remove(lastIndividual);
				elementsToRemove++; // Decrement elementsToRemove since we removed another individual
			}
		}

		// Add parents to indices 0 and 1
		NeuralNetworkList.addAll(0, offspring.subList(0, 2));

		// Add remaining offspring to the end of the population
		NeuralNetworkList.addAll(offspring.subList(2, offspring.size()));
	}

	public int getSize() {
		return NeuralNetworkList.size();
	}

}
