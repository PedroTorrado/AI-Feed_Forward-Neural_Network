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

		  // Find top 10 fittest individuals:
		  List<RankedAgent> topFitness = new ArrayList<>();
		  for (int i = 0; i < fitnessValues.length; i++) {
		    RankedAgent agent = new RankedAgent(fitnessValues[i], NeuralNetworkList.get(i));
		    
		    // Insert based on fitness (maintain top 10)
		    if (topFitness.size() < 10) {
		      topFitness.add(agent);
		    } else {
		      RankedAgent lowest = topFitness.get(topFitness.size() - 1);
		      if (agent.fitness > lowest.fitness) {
		        topFitness.remove(lowest);
		        topFitness.add(agent);
		      }
		    }
		  }

		  // Check if any elements remain after filtering
		  if (topFitness.isEmpty()) {
		    System.err.println("Warning: All fitness values below threshold. Consider adjusting selection method.");
		    // Handle the case where no elements meet the threshold (e.g., return empty list or random selection)
		    return parents;
		  }

		  // Roulette wheel selection (unchanged):
		  double totalFitness = 0;
		  List<RankedAgent> rouletteCandidates = new ArrayList<>(topFitness); // Convert to a list for roulette selection

		  // Calculate total fitness of top 10
		  for (RankedAgent agent : rouletteCandidates) {
		    totalFitness += agent.fitness;
		  }

		  // Perform roulette wheel selection twice (for 2 parents)
		  for (int i = 0; i < numParents; i++) {
		    double randomValue = Math.random() * totalFitness;
		    double partialSum = 0;
		    for (RankedAgent agent : rouletteCandidates) {
		      partialSum += agent.fitness;
		      if (partialSum >= randomValue) {
		        parents.add(agent.agent);
		        break;
		      }
		    }
		  }

		  return parents;
		}

		// Helper class to store agent and its fitness
		private static class RankedAgent {
		  double fitness;
		  NeuralNetworkGameController agent;

		  public RankedAgent(double fitness, NeuralNetworkGameController agent) {
		    this.fitness = fitness;
		    this.agent = agent;
		  }
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

		System.out.println("Parent 1 Fitness: " + parent1Game.getFitness()); // Replace with your fitness evaluation
																				// function
		System.out.println("Parent 2 Fitness: " + parent2Game.getFitness()); // Replace with your fitness evaluation
																				// function

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
