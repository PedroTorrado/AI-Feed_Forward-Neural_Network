package Generational_Algorithm;

import java.util.ArrayList;
import java.util.List;

import breakout.BreakoutBoard;

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
            BreakoutBoard agent = new BreakoutBoard(nnController, true, 10);
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

        int tournamentSize = 2;

        for (int i = 0; i < 2; i++) {
            List<Integer> tournamentIndices = new ArrayList<>();

            for (int j = 0; j < tournamentSize; j++) {
                int randomIndex = (int) (Math.random() * breakoutBoardList.size());
                tournamentIndices.add(randomIndex);
            }

            double maxFitness = Double.MIN_VALUE;
            int winnerIndex = -1;
            for (int index : tournamentIndices) {
                if (fitnessValues[index] > maxFitness) {
                    maxFitness = fitnessValues[index];
                    winnerIndex = index;
                }
            }
        }

        return parents;
    }


    public List<NeuralNetworkGameController> crossoverAndMutation(List<NeuralNetworkGameController> parents) {
        List<NeuralNetworkGameController> offspring = new ArrayList<>();

        for (int i = 0; i < parents.size(); i += 2) {
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
        NeuralNetworkGameController child = new NeuralNetworkGameController();
        child.setHiddenWeights(p2.getHiddenWeights());
        child.setOutputWeights(p2.getOutputWeights());
        return child;
    }

    private void mutation(NeuralNetworkGameController child) {
        double mutationRate = 0.05; // Adjust as needed

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
