package Generational_Algorithm;

import java.util.List;
import breakout.BreakoutBoard;

public class GeneticAlgorithm {

    public static void main(String[] args) {
        // Initialize the population with an instance of NeuralNetworkGameController
        NeuralNetworkGameController nnController = new NeuralNetworkGameController(/* pass appropriate arguments */);
        Population population = new Population(100, nnController); // Example population size of 100

        int maxGenerations = 1000;
        
        // Main loop of the genetic algorithm
        for (int generation = 1; generation <= maxGenerations ; generation++) {
            // Evaluate fitness of individuals in the population
            population.evaluateFitness();

            // Select parents for crossover
            List<NeuralNetworkGameController> parents = population.selectParents(population.evaluateFitness());

            // Perform crossover to create offspring
            List<NeuralNetworkGameController> offspring = population.crossoverAndMutation(parents);

            // Optionally, monitor and print statistics (e.g., best fitness, average fitness) for each generation
            System.out.println("Generation " + generation + ": Best fitness = " + population.getBestFitness());
        }

        // Once the algorithm terminates (after reaching a stopping criterion), retrieve and use the best individual
        BreakoutBoard bestIndividual = population.getBestIndividual();
        BreakoutBoard gameBoard = new BreakoutBoard(nnController, true, 100);

        gameBoard.runSimulation();
        // Use the best individual for further tasks or analysis
    }
}
