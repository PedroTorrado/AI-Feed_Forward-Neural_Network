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
	        seed = (int) (Math.random() * 10000000); // Maintain randomization for potential diversity

	        // Assuming NeuralNetworkGameController has constructors for network size
	        NeuralNetworkGameController agent = new NeuralNetworkGameController();
	        // Access weight and bias arrays from agent (assuming appropriate getter/setter methods)
	        double[][] hiddenWeights = agent.hiddenWeights;
	        double[][] outputWeights = agent.outputWeights;
	        double[] hiddenBiases = agent.getHiddenBiases(); 	
	        double[] outputBiases = agent.getOutputBiases();

	        // Random initialization with appropriate scaling (e.g., Xavier/He)
	        randomizeWeightsAndBiases(hiddenWeights, outputWeights, hiddenBiases, outputBiases);

	        NeuralNetworkList.add(agent);
	    }
	}

	// Helper method for random initialization (replace with your preferred scaling strategy)
	private void randomizeWeightsAndBiases(double[][] hiddenWeights, double[][] outputWeights,
	                                       double[] hiddenBiases, double[] outputBiases) {
	    // Choose your preferred initialization strategy here
	    double upperBound = Math.sqrt(6.0 / (hiddenWeights[0].length + 1)); // Xavier initialization
	    double lowerBound = -upperBound;

	    for (int i = 0; i < hiddenWeights.length; i++) {
	        for (int j = 0; j < hiddenWeights[i].length; j++) {
	            hiddenWeights[i][j] = Math.random() * (upperBound - lowerBound) + lowerBound;
	        }
	    }

	    // Similar logic for output weights and biases (adjust upperBound/lowerBound if needed)
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

    // Identify top 'e' fittest individuals (adjust 'e')
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

    // Calculate total fitness
    double totalFitness = 0;
    for (double fitness : fitnessValues) {
      totalFitness += fitness;
    }

    // Perform roulette wheel selection
    for (int i = 0; i < 2; i++) {
      double randomValue = Math.random() * totalFitness;
      double cumulativeFitness = 0;
      int selectedParentIndex = -1;

      for (int j = 0; j < fitnessValues.length; j++) {
        cumulativeFitness += fitnessValues[j];
        if (cumulativeFitness >= randomValue) {
          selectedParentIndex = j;
          break;
        }
      }

      if (selectedParentIndex != -1) {
        NeuralNetworkGameController selectedBoard = NeuralNetworkList.get(selectedParentIndex);
        parents.add(selectedBoard);
      }
    }

    return parents;
  }

  public List<NeuralNetworkGameController> crossoverAndMutation(List<NeuralNetworkGameController> parents) {
    List<NeuralNetworkGameController> offspring = new ArrayList<>();

    // Ensure that there are at least two parents left to process
    for (int i = 0; i < parents.size() - 1; i += 2) {
      NeuralNetworkGameController parent1 = parents.get(i);
      NeuralNetworkGameController parent2 = parents.get(i + 1);

      NeuralNetworkGameController child1 = crossover(parent1, parent2);
      NeuralNetworkGameController child2 = crossover(parent2, parent1);
      mutation(child1);
      mutation(child2);

      offspring.add(child1);
      offspring.add(child2);
    }

    return offspring;
  }

  public static NeuralNetworkGameController crossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {

	  // Select a random crossover point (excluding the first and last layers)
	  int crossoverPoint = (int) (Math.random() * (parent1.getNumLayers() - 2)) + 1;

	  System.out.println("----- Crossover Details -----");
	  System.out.println("Parent 1 Layers: " + parent1.getNumLayers());
	  System.out.println("Parent 2 Layers: " + parent2.getNumLayers());
	  
	  BreakoutBoard parent1Game = new BreakoutBoard(parent1, false, seed); // Set appropriate parameters for parent1Game
	  BreakoutBoard parent2Game = new BreakoutBoard(parent2, false, seed); // Set appropriate parameters for parent2Game
	  
	  parent1Game.runSimulation();
	  parent2Game.runSimulation();
	  
	  System.out.println("Parent 1 Fitness: " + parent1Game.getFitness()); // Replace with your fitness evaluation function
	  System.out.println("Parent 2 Fitness: " + parent2Game.getFitness()); // Replace with your fitness evaluation function

	  // Create a new child network with the same structure as parents
	  NeuralNetworkGameController child = new NeuralNetworkGameController(); // Ensure correct number of layers for child

	  // Copy weights from parent1 up to the crossover point (exclusive)
	  for (int layer = 0; layer < crossoverPoint; layer++) {
	    double[][] weights = parent1.getWeights(layer);
	    double[] biases = parent1.getBiases(layer);
	    System.out.println("Copying weights for layer: " + layer); // Debugging print
	    child.setWeights(layer, weights);
	    child.setBiases(layer, biases);
	  }

	  // Copy weights from parent2 starting from the crossover point (inclusive)
	  for (int layer = crossoverPoint; layer < child.getNumLayers(); layer++) {
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
      return bestIndividual;  // No need to create a new instance here
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
    // Remove the last (population size - elite individuals) elements from the population
    int numElite = 1; // Assuming you want to keep the top 2 fittest individuals
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
