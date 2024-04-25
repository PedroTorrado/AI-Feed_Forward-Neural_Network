package Generational_Algorithm;

import breakout.BreakoutBoard;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private List<BreakoutBoard> breakoutBoardList;
    private BreakoutBoard bestIndividual;
    private double bestFitness;
    private int seed;

    public Population(int size, NeuralNetworkGameController nnController) {
        breakoutBoardList = new ArrayList<>();
        initializePopulation(size, nnController);
        bestIndividual = null;
        bestFitness = Double.MIN_VALUE;
        seed = this.seed;
    }

    private void initializePopulation(int size, NeuralNetworkGameController nnController) {
        for (int i = 0; i < size; i++) {
            // Creating breakout boards with random seeds
        	seed = (int) (Math.random()*10000000);
            BreakoutBoard agent = new BreakoutBoard(nnController, false, seed);
            breakoutBoardList.add(agent);
        }
    }

    public double[] evaluateFitness() {
        double[] fitnessValues = new double[breakoutBoardList.size()];

        for (int i = 0; i < breakoutBoardList.size(); i++) {
            BreakoutBoard breakoutBoard = breakoutBoardList.get(i);
            breakoutBoard.runSimulation();
            double fitness = breakoutBoard.getFitness();
            fitnessValues[i] = fitness;

            // Debugging: Print fitness values for each agent
            //System.out.println("Agent " + i + " Fitness: " + fitness);

            // Update best individual and its fitness
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndividual = breakoutBoard;
            }
        }

        // Debugging: Print best fitness
        //System.out.println("Best Fitness: " + bestFitness);

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
                BreakoutBoard selectedBoard = breakoutBoardList.get(selectedParentIndex);
                NeuralNetworkGameController selectedController = new NeuralNetworkGameController(selectedBoard);
                parents.add(selectedController);
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

            NeuralNetworkGameController child1 = crossover(parent1, parent2);
            NeuralNetworkGameController child2 = crossover(parent2, parent1);

            mutation(child1);
            mutation(child2);

            offspring.add(child1);
            offspring.add(child2);
        }

        return offspring;
    }


    private NeuralNetworkGameController crossover(NeuralNetworkGameController p1, NeuralNetworkGameController p2) {
        NeuralNetworkGameController child = new NeuralNetworkGameController(bestIndividual);
        child.setHiddenWeights(p2.getHiddenWeights());
        child.setOutputWeights(p2.getOutputWeights());
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

    public BreakoutBoard getBestIndividual() {
        return bestIndividual;
    }
    
    public NeuralNetworkGameController getBestNN() {
    	  // Check if the bestIndividual is set, otherwise return null
    	  if (bestIndividual != null) {
    	    return new NeuralNetworkGameController(bestIndividual);
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
}
