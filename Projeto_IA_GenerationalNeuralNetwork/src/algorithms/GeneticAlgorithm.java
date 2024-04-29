package algorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class GeneticAlgorithm {

	public static final double MUTATION_RATE = 0.5;
	public int generations = 1000000; // Use a more descriptive name
	public int populationSize = 800; // Use a more descriptive name
	private FeedForwardNeuralNetwork[] population; // Use final for array initialization
	private String fileName;
	private double listvalue;
	
	public GeneticAlgorithm(int inputDim, int hiddenDim, int outputDim) throws IOException {
		population = new FeedForwardNeuralNetwork[populationSize];
		for (int i = 0; i < populationSize; i++) {
			population[i] = new FeedForwardNeuralNetwork(inputDim, hiddenDim, outputDim);
		}
		test(0, inputDim, hiddenDim, outputDim);
		listvalue = population[0].calculateFitness();

	}

	public void test(int i, int inputDim, int hiddenDim, int outputDim) throws IOException {
		fileName = "PacmanPastTests.txt";
//		fileName = "BreakoutPastTests.txt";
		String content = readFile(fileName);
		System.out.println(content);
		double[] bestValues = toDouble(content);
		population[i] = new FeedForwardNeuralNetwork(inputDim, hiddenDim, outputDim, bestValues);
		System.out.println(population[0].calculateFitness());
	}

	public FeedForwardNeuralNetwork beginSearch(int inputDim, int hiddenDim, int outputDim) throws IOException {

		FeedForwardNeuralNetwork bestIndividual = population[0]; // Keep the fittest individual (assuming bestIndividual
																	// is already a FeedForwardNeuralNetwork object)
		double bestIndividualOneFitness = 0;

		for (int i = 0; i < generations; i++) {

			System.out.println("Generation " + i);

			// Sort the population for efficient selection and update bestIndividual
			Arrays.sort(population);
			FeedForwardNeuralNetwork parent1 = selectParents(10);
			FeedForwardNeuralNetwork parent2 = selectParents(10);

			// System.out.println("Parent 1: " + parent1.calculateFitness());
			// System.out.println("Parent 2: " + parent2.calculateFitness());

			// Perform crossover to generate child networks
			double[] child1Arr = crossover(parent1.getNNArray(), parent2.getNNArray(), 4);
			double[] child2Arr = crossover(parent2.getNNArray(), parent1.getNNArray(), 4);

			// Create child networks with crossed-over weights and biases
			FeedForwardNeuralNetwork child1 = new FeedForwardNeuralNetwork(inputDim, hiddenDim, outputDim, child1Arr);
			FeedForwardNeuralNetwork child2 = new FeedForwardNeuralNetwork(inputDim, hiddenDim, outputDim, child2Arr);

			// Apply mutation to the child networks
			mutation(child1);
			// System.out.println("Child 1: " + child1.calculateFitness());
			mutation(child2);
			// System.out.println("Child 1: " + child2.calculateFitness());

			population[0] = bestIndividual;

			// Directly add children to the end of the sorted population
			if (population[population.length - 1].calculateFitness() < child1.calculateFitness()) {
				population[population.length - 1] = child1;
			}
			if (population[population.length - 2].calculateFitness() < child2.calculateFitness()) {
				population[population.length - 2] = child2;
			}

			Arrays.sort(population);
			bestIndividual = population[0]; // Update bestIndividual after sorting
			
			writeToRecord(fileName, bestIndividual);

			System.out.println("Best Fitness: " + bestIndividual.calculateFitness());

			if (i == 0) {
				bestIndividualOneFitness = bestIndividual.calculateFitness();
			}
		}
		System.out.println("First Best Fitness: " + bestIndividualOneFitness);
		System.out.println("Final Best Fitness: " + bestIndividual.calculateFitness());

		return bestIndividual;
	}

	private FeedForwardNeuralNetwork selectParents(int sizeOfSearch) {
		FeedForwardNeuralNetwork[] result = new FeedForwardNeuralNetwork[sizeOfSearch];
		for (int i = 1; i < sizeOfSearch; i++) {
			result[i] = population[i];
		}
		int selectedIndex = (int) (Math.random() * sizeOfSearch - 1) + 1;
		return result[selectedIndex];
	}

	public static double[] crossover(double[] parent1Arr, double[] parent2Arr, int k) {
		int size = parent1Arr.length;
		double[] childArr = new double[size];

		// Validate k value
		if (k < 1 || k > size - 2) {
			throw new IllegalArgumentException("k inválido");
		}

		// Allocate space to store crossover points
		int[] crossoverPoints = new int[k + 1];

		// Choose k random crossover points (excluding first and last element)
		for (int i = 0; i <= k; i++) {
			crossoverPoints[i] = (int) (Math.random() * (size - 2)) + 1;
		}

		// Ensure crossover points are in ascending order
		Arrays.sort(crossoverPoints);

		// Copy from parent1 before the first crossover point
		for (int i = 0; i < crossoverPoints[0]; i++) {
			childArr[i] = parent1Arr[i];
		}

		// Loop through remaining crossover points, swapping between parents
		for (int i = 1; i <= k; i++) {
			int from = crossoverPoints[i - 1];
			int to = crossoverPoints[i];
			for (int j = from; j < to; j++) {
				childArr[j] = parent2Arr[j];
			}
		}

		// Copy from parent1 after the last crossover point
		for (int i = crossoverPoints[k]; i < size; i++) {
			childArr[i] = parent1Arr[i];
		}

		return childArr;
	}

	public void mutation(FeedForwardNeuralNetwork child) {
		double[] childArr = child.getNNArray();
		for (int i = 0; i < childArr.length; i++) {
			if (Math.random() < MUTATION_RATE) {
				childArr[i] += Math.random(); // Add random noise
			}
		}
	}

	private void writeToRecord(String fileName, FeedForwardNeuralNetwork data) throws IOException {
		  fileName = "PacmanPastTests.txt";
//		  fileName = "BreakoutPastTests.txt";

		  // Open the file for writing using FileWriter (overwrite mode)
		  FileWriter fileWriter = new FileWriter(fileName); // No append mode argument

		  try {
		    // Convert network data to String representation (consider a custom method for better formatting)
		    String string = data.toString();
		    // System.out.println(string);
		    String dataString = string;

		    // Write the data to the file
		    fileWriter.write(dataString);
		    fileWriter.write("\n"); // Add a newline character for readability (optional)
		  } finally {
		    // Ensure resources are closed even if an exception occurs
		    if (fileWriter != null) {
		      fileWriter.close();
		    }
		  }
		}


	public String readFile(String fileName) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;

		// Open the file using FileReader and BufferedReader
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		try {
			// Read line by line and append to StringBuilder
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line).append("\n"); // Add newline after each line
			}
		} finally {
			// Ensure resources are closed
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (fileReader != null) {
				fileReader.close();
			}
		}

		// Return the entire file content as String
		return sb.toString().trim(); // Remove trailing newline if any
	}

	public static double[] toDouble(String dataString) {

	    int estimatedValues = (dataString.length()) / 2 * 5; // Adjust estimate based on format
	    double[] allValues = new double[estimatedValues];
	    int currentIndex = 0;

	    for (String line : dataString.split(" ")) {
	        // Skip comments, empty lines, and headers
	        if (line.isEmpty() || line.startsWith("//") || line.startsWith("#") ||
	                line.startsWith("**") || line.contains(":")) {
	            continue;
	        }

	        // Parse numerical data within the line
	        String[] values = line.trim().split("\\s+");
	        for (String value : values) {
	            try {
	                allValues[currentIndex++] = Double.parseDouble(value);
	            } catch (NumberFormatException e) {
	                // Handle potential errors (optional)
	                // System.err.println("Error parsing value: " + value);
	            }
	        }
	    }
	    // Resize the array to the actual number of parsed values (optional)
	    return Arrays.copyOf(allValues, currentIndex);
	}


}
