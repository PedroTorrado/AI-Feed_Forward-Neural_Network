package Generational_Algorithm;

import java.util.List;

import breakout.Breakout;

public class GeneticAlgorithm {

  public static void main(String[] args) {
    // Initialize the population size (adjust as needed)
    int populationSize = 10000;

    // Create an instance of NeuralNetworkGameController (assuming it defines the network)
    NeuralNetworkGameController nnController = new NeuralNetworkGameController();
    Population population = new Population(populationSize, nnController);

    // Set the maximum number of generations for evolution
    int maxGenerations = 1000;

    // Evaluate the initial population fitness
    double[] fitnessValues = population.evaluateFitness();

    // Select parents based on fitness (modify `selectParents` if needed)
    List<NeuralNetworkGameController> parents = population.selectParents(fitnessValues);

    // Main loop of the genetic algorithm
    for (int generation = 1; generation <= maxGenerations; generation++) {

      // Perform crossover and mutation to create offspring (implement in Population)
      List<NeuralNetworkGameController> offspring = population.crossoverAndMutation(parents);

      // Update the population with the new offspring (implement in Population)
      population.updatePopulation(offspring);

      // Print population size after update for monitoring
      //System.out.println("Population size after update: " + population.getSize());

      // Check for empty population (optional) - handle if necessary
      if (population.getSize() == 0) {
        System.err.println("Error: Population is empty after update!");
      }
      
      System.out.println("Generation: " + generation + " Best Fitness: " + population.getBestFitness());

    }

    // Retrieve the best individual after evolution
    NeuralNetworkGameController bestIndividual = population.getBestIndividual();
    int bestSeed = population.getSeed(); // Assuming Population stores the best seed

    if (bestIndividual != null) {
      // Visualize the result using Breakout game GUI (modify if needed)
      visualizeResult(bestIndividual, bestSeed);
    }
  }

  // Method to visualize the result using the Breakout game GUI (replace with your visualization method)
  private static void visualizeResult(NeuralNetworkGameController NN, int seed) {
    // Create an instance of the Breakout class and pass the NN and seed
    new Breakout(NN, seed);
  }
}
