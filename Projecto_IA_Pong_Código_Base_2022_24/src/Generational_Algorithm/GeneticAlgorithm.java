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
        for (int generation = 1; generation <= maxGenerations; generation++) {
            // Evaluate fitness of individuals in the population
            double[] fitnessValues = population.evaluateFitness();

            // Select parents for crossover
            List<NeuralNetworkGameController> parents = population.selectParents(fitnessValues);

            // Perform crossover to create offspring
            List<NeuralNetworkGameController> offspring = population.crossoverAndMutation(parents);

            // Optionally, monitor and print statistics (e.g., best fitness, average fitness) for each generation
            System.out.println("Generation " + generation + ": Best fitness = " + population.getBestFitness());       
        }
        BreakoutBoard bestIndividual = population.getBestIndividual();
        if (bestIndividual != null) {
            visualizeResult(bestIndividual);
        }
    }

    // Method to visualize the result using the Breakout game GUI
    private static void visualizeResult(BreakoutBoard board) {
        // Create an instance of the Breakout class and pass the BreakoutBoard instance to it
        new Breakout(new BreakoutController(), 100);
    }
}
