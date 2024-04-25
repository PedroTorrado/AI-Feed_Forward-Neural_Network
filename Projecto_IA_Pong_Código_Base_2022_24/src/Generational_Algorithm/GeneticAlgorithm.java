package Generational_Algorithm;

import breakout.Breakout;
import breakout.BreakoutBoard;
import utils.GameController;

import java.util.List;

public class GeneticAlgorithm {

    public static void main(String[] args) {
        // Initialize the population with an instance of NeuralNetworkGameController
        NeuralNetworkGameController nnController = new NeuralNetworkGameController();
        Population population = new Population(100, nnController); // Example population size of 100

        int maxGenerations = 1000;
        double[] fitnessValues = population.evaluateFitness();

        List<NeuralNetworkGameController> parents = population.selectParents(fitnessValues);

        // Main loop of the genetic algorithm
        for (int generation = 1; generation <= maxGenerations; generation++) {

            // Perform crossover to create offspring
            parents = population.crossoverAndMutation(parents);
            
            // Optionally, monitor and print statistics (e.g., best fitness, average fitness) for each generation
            System.out.println("Generation " + generation + ": Best fitness = " + population.getBestFitness());       
        }
        NeuralNetworkGameController bestIndividual = population.getBestIndividual();
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
