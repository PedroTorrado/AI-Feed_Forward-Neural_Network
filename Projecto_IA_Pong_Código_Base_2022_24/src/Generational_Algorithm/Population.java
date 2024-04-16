import java.util.ArrayList;
import java.util.List;

import breakout.BreakoutBoard;
import utils.GameController;

public class Population {

    private List<BreakoutBoard> BreakoutBoardList;

    public Population(int size) {
    	BreakoutBoardList = new ArrayList<>();
        initializePopulation(size);
    }

    private void initializePopulation(int size) {
        for (int i = 0; i < size; i++) {
        	GameController controller = new BreakoutController();
            BreakoutBoard agent = new BreakoutBoard(controller, true, 10); //(GameController , boolean , int ) 
            BreakoutBoardList.add(agent);
        }
    }

    public double[] evaluateFitness() {
        double[] fitnessValues = new double[BreakoutBoardList.size()];

        for (int i = 0; i < BreakoutBoardList.size(); i++) {
            BreakoutBoard breakoutBoard = BreakoutBoardList.get(i);
            breakoutBoard.runSimulation(); // Run simulation for each agent
            double fitness = breakoutBoard.getFitness(); // Get fitness value after simulation
            fitnessValues[i] = fitness; // Store fitness value in the array
        }

        return fitnessValues; // Return the array of fitness values
    }




    public List<BreakoutBoard> selectParents(double[] fitnessValues) {
        List<BreakoutBoard> parents = new ArrayList<>();

        int tournamentSize = 2; // Tournament size (number of agents competing in each tournament)

        for (int i = 0; i < 2; i++) { // We need 2 parents for crossover
            List<Integer> tournamentIndices = new ArrayList<>();

            // Randomly select agents for the tournament
            for (int j = 0; j < tournamentSize; j++) {
                int randomIndex = (int) (Math.random() * BreakoutBoardList.size());
                tournamentIndices.add(randomIndex);
            }

            // Find the index of the agent with the highest fitness in the tournament
            double maxFitness = Double.MIN_VALUE;
            int winnerIndex = -1;
            for (int index : tournamentIndices) {
                if (fitnessValues[index] > maxFitness) {
                    maxFitness = fitnessValues[index];
                    winnerIndex = index;
                }
            }

            // Add the winner as a parent
            if (winnerIndex != -1) {
                parents.add(BreakoutBoardList.get(winnerIndex));
            }
        }

        return parents;
    }



    public void crossoverAndMutation(List<BreakoutBoard> parents) {
        List<BreakoutBoard> offspring = new ArrayList<>();

        // Perform crossover and mutation for each pair of parents
        for (int i = 0; i < parents.size(); i += 2) {
            // Get the two parents for crossover
            BreakoutBoard parent1 = parents.get(i);
            BreakoutBoard parent2 = parents.get(i + 1);

            // Perform crossover to create offspring
            BreakoutBoard child1 = crossover(parent1, parent2);
            BreakoutBoard child2 = crossover(parent2, parent1); // Perform crossover with parents swapped

            // Apply mutation to the offspring
            mutate(child1);
            mutate(child2);

            // Add the offspring to the list
            offspring.add(child1);
            offspring.add(child2);
        }

        // Replace some agents in the population with the generated offspring
        replaceAgents(offspring);
    }
    
    private void mutate(BreakoutBoard child) {
        double mutationRate = 0.005; // Mutation rate of 0.5%

        // Mutate the seed attribute with the specified probability
        if (Math.random() < mutationRate) {
            int randomSeed = (int) (Math.random() * Integer.MAX_VALUE); //TODO How to perform the mutation??
            child.setSeed(randomSeed);
        }
    }
    
    public List<BreakoutBoard> crossover(List<BreakoutBoard> parents) {
        List<BreakoutBoard> offspring = new ArrayList<>();

        // Perform crossover for each pair of parents
        for (int i = 0; i < parents.size(); i += 2) {
            if (i + 1 < parents.size()) {
                BreakoutBoard parent1 = parents.get(i);
                BreakoutBoard parent2 = parents.get(i + 1);

                // Perform crossover operation between parent1 and parent2
                // Create offspring using genetic material from both parents
                BreakoutBoard child = performCrossover(parent1, parent2);

                offspring.add(child);
            }
        }

        return offspring;
    }

	private BreakoutBoard performCrossover(BreakoutBoard parent1, BreakoutBoard parent2) {
		// TODO Auto-generated method stub
		return null;
	}
}
