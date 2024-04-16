import java.util.ArrayList;
import java.util.List;

import breakout.BreakoutBoard;
import utils.GameController;

public class GeneticAlgorithm {

    public static void main(String[] args) {
        // Initialize the population
        Population population = new Population(100); // Example population size of 100

        int maxGenerations = 1000;
        
		// Main loop of the genetic algorithm
        for (int generation = 1; generation <= maxGenerations ; generation++) {
            // Evaluate fitness of individuals in the population
            population.evaluateFitness();

            // Select parents for crossover
            List<BreakoutBoard> parents = population.selectParents(population.evaluateFitness());

            // Perform crossover to create offspring
            List<BreakoutBoard> offspring = population.crossover(parents);

            // Mutate the offspring
            population.crossoverAndMutation(offspring);

            // Optionally, monitor and print statistics (e.g., best fitness, average fitness) for each generation
            System.out.println("Generation " + generation + ": Best fitness = " + population.getBestFitness());
        }

        // Once the algorithm terminates (after reaching a stopping criterion), retrieve and use the best individual
        BreakoutBoard bestIndividual = population.getBestIndividual();
        // Use the best individual for further tasks or analysis
    }
}
