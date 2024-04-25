package Generational_Algorithm;

import breakout.Breakout;
import breakout.BreakoutBoard;
import utils.GameController;

import java.util.List;

public class GeneticAlgorithm {

    public static void main(String[] args) {
        // Initialize the population with an instance of NeuralNetworkGameController
        NeuralNetworkGameController nnController = new NeuralNetworkGameController(null);
        Population population = new Population(100, nnController); // Example population size of 100

        int maxGenerations = 1000;

        // Main loop of the genetic algorithm
        public void runGeneticAlgorithm(int generation, Population population) {
        	  if (generation > maxGenerations) {
        	    return; // Base case: Reached max generations
        	  }

        	  // Evaluate fitness of individuals
        	  double[] fitnessValues = population.evaluateFitness();

        	  // Select parents for crossover
        	  List<NeuralNetworkGameController> parents = population.selectParents(fitnessValues);

        	  // Perform crossover to create offspring
        	  List<NeuralNetworkGameController> offspring = population.crossoverAndMutation(parents);

        	  // Update population with offspring
        	  population.updatePopulation(offspring);

        	  // Optionally, monitor and print statistics
        	  System.out.println("Generation " + generation + ": Best fitness = " + population.getBestFitness());

        	  // Recursive call for the next generation
        	  runGeneticAlgorithm(generation + 1, population);
        	}
        BreakoutBoard bestIndividual = population.getBestIndividual();
        NeuralNetworkGameController NN = population.getBestNN();
        int bestSeed = population.getSeed();

        if (bestIndividual != null) {
            visualizeResult(NN, bestSeed);
        }
    }

    // Method to visualize the result using the Breakout game GUI
    private static void visualizeResult(NeuralNetworkGameController NN, int seed) {
        // Create an instance of the Breakout class and pass the BreakoutBoard instance to it
        new Breakout(NN, seed);
    }
}
