package Generational_Algorithm;

import breakout.BreakoutBoard;
import utils.GameController;

import java.util.ArrayList;
import java.util.List;

public class Population {

    private List<BreakoutBoard> breakoutBoardList;
    private BreakoutBoard bestIndividual;
    private double bestFitness;

    public Population(int size, NeuralNetworkGameController nnController) {
        breakoutBoardList = new ArrayList<>();
        initializePopulation(size, nnController);
        bestIndividual = null;
        bestFitness = Double.MIN_VALUE;
    }

    private void initializePopulation(int size, NeuralNetworkGameController nnController) {
        for (int i = 0; i < size; i++) {
            // Creating breakout boards with random seeds
            BreakoutBoard agent = new BreakoutBoard(nnController, false, (int) (System.currentTimeMillis() * 1000));
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

            // Update best individual and its fitness
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndividual = breakoutBoard;
            }
        }

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
        double mutationRate = 1; // Adjust as needed

        for (int i = 0; i < child.hiddenWeights.length; i++) {
            for (int j = 0; j < child.hiddenWeights[i].length; j++) {
                if (Math.random() < mutationRate) {
                    child.hiddenWeights[i][j] += (Math.random() * 0.5) + 0.1;
                }
            }
        }
        for (int i = 0; i < child.outputWeights.length; i++) {
            for (int j = 0; j < child.outputWeights[i].length; j++) {
                if (Math.random() < mutationRate) {
                    child.outputWeights[i][j] += (Math.random() * 0.5) + 0.1;
                }
            }
        }
    }

    public BreakoutBoard getBestIndividual() {
        return bestIndividual;
    }

    public double getBestFitness() {
        return bestFitness;
    }
}
