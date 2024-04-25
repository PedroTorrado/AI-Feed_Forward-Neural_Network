package Generational_Algorithm;

import java.util.ArrayList;
import java.util.List;

import breakout.BreakoutBoard;

public class Population {

  private List<NeuralNetworkGameController> NeuralNetworkList;
  private NeuralNetworkGameController bestIndividual;
  private double bestFitness;
  private int seed;

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
	        double[][] hiddenWeights = agent.getHiddenWeights();
	        double[][] outputWeights = agent.getOutputWeights();
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

      NeuralNetworkGameController child = crossover(parent1, parent2);
      mutation(child);

      offspring.add(child);
    }

    return offspring;
  }

  private NeuralNetworkGameController crossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {
	  // Use the existing bestIndividual for initialization (assuming it has weights)
	  NeuralNetworkGameController child = new NeuralNetworkGameController(bestIndividual); 
	  double[][] childHiddenWeights = child.getHiddenWeights();
	  double[][] childOutputWeights = child.getOutputWeights();

	  // Choose a random crossover point for both hidden and output weights
	  int hiddenCrossoverPoint = (int) (Math.random() * childHiddenWeights.length);
	  int outputCrossoverPoint = (int) (Math.random() * childOutputWeights.length);

	  // Copy weights from parent1 up to the crossover point for hidden layer
	  for (int i = 0; i < hiddenCrossoverPoint; i++) {
	    System.arraycopy(parent1.getHiddenWeights()[i], 0, childHiddenWeights[i], 0, parent1.getHiddenWeights()[i].length);
	  }
	  // Copy weights from parent2 after the crossover point for hidden layer
	  for (int i = hiddenCrossoverPoint; i < childHiddenWeights.length; i++) {
	    System.arraycopy(parent2.getHiddenWeights()[i], 0, childHiddenWeights[i], 0, parent2.getHiddenWeights()[i].length);
	  }

	  // Copy weights from parent1 up to the crossover point for output layer (similar logic)
	  for (int i = 0; i < outputCrossoverPoint; i++) {
	    System.arraycopy(parent1.getOutputWeights()[i], 0, childOutputWeights[i], 0, parent1.getOutputWeights()[i].length);
	  }
	  // Copy weights from parent2 after the crossover point for output layer
	  for (int i = outputCrossoverPoint; i < childOutputWeights.length; i++) {
	    System.arraycopy(parent2.getOutputWeights()[i], 0, childOutputWeights[i], 0, childOutputWeights[i].length); // This was fixed from a previous typo (should be childOutputWeights[i])
	  }

	  child.setHiddenWeights(childHiddenWeights);
	  child.setOutputWeights(childOutputWeights);
	  return child;
	}



  private void mutation(NeuralNetworkGameController child) {
    double mutationRate = 0.05; // Adjust as needed
    double[][] childHiddenWeights = child.getHiddenWeights();
    double[][] childOutputWeights = child.getOutputWeights();

    for (int i = 0; i < childHiddenWeights.length; i++) {
      for (int j = 0; j < childHiddenWeights[i].length; j++) {
        if (Math.random() < mutationRate) {
          childHiddenWeights[i][j] += (Math.random() * 0.5) + 0.1;
        }
      }
    }
    for (int i = 0; i < childOutputWeights.length; i++) {
      for (int j = 0; j < childOutputWeights[i].length; j++) {
        if (Math.random() < mutationRate) {
          childOutputWeights[i][j] += (Math.random() * 0.5) + 0.1;
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
    int numElite = 2; // Assuming you want to keep the top 2 fittest individuals
    int elementsToRemove = NeuralNetworkList.size() - numElite;
    for (int i = 0; i < elementsToRemove; i++) {
      NeuralNetworkList.remove(NeuralNetworkList.size() - 1);
    }
    // Add the new offspring to the beginning of the population
    NeuralNetworkList.addAll(0, offspring);
  }
}
