package algorithms;

import java.util.Arrays;

import breakout.Breakout;

import utils.Commons;

public class GeneticAlgorithm {

	public static final double MUTATION_RATE = 0.1;
	public int generations = 10000; // Use a more descriptive name
	public int populationSize = 5000; // Use a more descriptive name
	private FeedForwardNeuralNetwork[] population; // Use final for array initialization

	public GeneticAlgorithm() {
		population = new FeedForwardNeuralNetwork[populationSize];
		for (int i = 0; i < populationSize; i++) {
			population[i] = new FeedForwardNeuralNetwork();
		}
	}

	public FeedForwardNeuralNetwork beginSearch() {
		FeedForwardNeuralNetwork bestIndividual = population[0]; // Keep the fittest individual (assuming bestIndividual
																	// is already a FeedForwardNeuralNetwork object)
		double bestIndividualOneFitness = 0;
		
		for (int i = 0; i < generations; i++) {

			System.out.println("Generation " + i);

			// Sort the population for efficient selection and update bestIndividual
			Arrays.sort(population);
			FeedForwardNeuralNetwork parent1 = selectParents(30);
			FeedForwardNeuralNetwork parent2 = selectParents(30);

			//System.out.println("Parent 1: " + parent1.calculateFitness());
			//System.out.println("Parent 2: " + parent2.calculateFitness());

			// Perform crossover to generate child networks
			double[] child1Arr = crossover(parent1.getNNArray(), parent2.getNNArray());
			double[] child2Arr = crossover(parent2.getNNArray(), parent1.getNNArray());

			// Create child networks with crossed-over weights and biases
			FeedForwardNeuralNetwork child1 = new FeedForwardNeuralNetwork(Commons.BREAKOUT_STATE_SIZE,
					Commons.BREAKOUT_HIDDEN_DIM, Commons.BREAKOUT_NUM_ACTIONS, child1Arr);
			FeedForwardNeuralNetwork child2 = new FeedForwardNeuralNetwork(Commons.BREAKOUT_STATE_SIZE,
					Commons.BREAKOUT_HIDDEN_DIM, Commons.BREAKOUT_NUM_ACTIONS, child2Arr);

			// Apply mutation to the child networks
			mutation(child1);
			//System.out.println("Child 1: " + child1.calculateFitness());
			mutation(child2);
			//System.out.println("Child 1: " + child2.calculateFitness());

			population[0] = bestIndividual;

			// Directly add children to the end of the sorted population
			population[population.length - 1] = child1;
			population[population.length - 2] = child2;

			Arrays.sort(population);
			bestIndividual = population[0]; // Update bestIndividual after sorting

			//System.out.println("Best Fitness: " + bestIndividual.calculateFitness());
			
			
			if(i==0) {
				bestIndividualOneFitness = bestIndividual.calculateFitness();
			}

		}
		System.out.println("First Best Fitness: " + bestIndividualOneFitness); 
		System.out.println("Final Best Fitness: " + bestIndividual.calculateFitness());

		return bestIndividual;
	}

	// Implement these methods based on your specific genetic algorithm
	private FeedForwardNeuralNetwork selectParents(int sizeOfSearch) {
		  
		  FeedForwardNeuralNetwork[] result = new FeedForwardNeuralNetwork[sizeOfSearch];
		  for (int i = 1; i < sizeOfSearch; i++) {
		    result[i] = population[i];
		  }
		  int selectedIndex = (int) (Math.random() * sizeOfSearch-1)+1;
		  
		  return result[selectedIndex];
	}

	public static double[] crossover(double[] parent1Arr, double[] parent2Arr) {
		int size = parent1Arr.length;
		double[] childArr = new double[size];

		// Choose two random crossover points (excluding first and last element)
		int crossoverPoint1 = (int) (Math.random() * (size - 2)) + 1;
		int crossoverPoint2 = (int) (Math.random() * (size - 2)) + 1;

		// Ensure crossover points are in ascending order
		if (crossoverPoint1 > crossoverPoint2) {
			int temp = crossoverPoint1;
			crossoverPoint1 = crossoverPoint2;
			crossoverPoint2 = temp;
		}

		// Copy from parent1 before first crossover point
		for (int i = 0; i < crossoverPoint1; i++) {
			childArr[i] = parent1Arr[i];
		}

		// Copy from parent2 between crossover points
		for (int i = crossoverPoint1; i < crossoverPoint2; i++) {
			childArr[i] = parent2Arr[i];
		}

		// Copy from parent1 after second crossover point
		for (int i = crossoverPoint2; i < size; i++) {
			childArr[i] = parent1Arr[i];
		}

		return childArr;
	}

	private void mutation(FeedForwardNeuralNetwork child) {
		double[] childArr = child.getNNArray();
		for (int i = 0; i < childArr.length; i++) {
			if (Math.random() < MUTATION_RATE) {
				childArr[i] += Math.random(); // Add random noise
			}
		}
	}

}
