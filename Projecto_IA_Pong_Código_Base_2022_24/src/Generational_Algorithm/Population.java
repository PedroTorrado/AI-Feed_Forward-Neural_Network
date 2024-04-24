package Generational_Algorithm;

import java.util.ArrayList;
import java.util.List;

import breakout.BreakoutBoard;

public class Population {

    private List<BreakoutBoard> breakoutBoardList;

    public Population(int size, NeuralNetworkGameController nnController) {
        breakoutBoardList = new ArrayList<>();
        initializePopulation(size, nnController);
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
        }

        return fitnessValues;
    }

    public List<BreakoutBoard> selectParents(double[] fitnessValues) {
        List<BreakoutBoard> parents = new ArrayList<>();

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

            if (winnerIndex != -1) {
                parents.add(breakoutBoardList.get(winnerIndex));
            }
        }

        return parents;
    }

    public void crossoverAndMutation(List<BreakoutBoard> parents) {
        List<BreakoutBoard> offspring = new ArrayList<>();

        for (int i = 0; i < parents.size(); i += 2) {
            BreakoutBoard parent1 = parents.get(i);
            BreakoutBoard parent2 = parents.get(i + 1);

            BreakoutBoard child1 = crossover(parent1, parent2);
            BreakoutBoard child2 = crossover(parent2, parent1);

            mutate(child1);
            mutate(child2);

            offspring.add(child1);
            offspring.add(child2);
        }

        replaceAgents(offspring);
    }

    private void replaceAgents(List<BreakoutBoard> offspring) {
        for (int i = 0; i < offspring.size(); i++) {
            breakoutBoardList.set(i, offspring.get(i));
        }
    }

    private void mutate(BreakoutBoard child) {
        double mutationRate = 0.005;

        // Perform mutation on child (if needed)
        // Example: child.mutate(mutationRate);
    }

    private BreakoutBoard crossover(BreakoutBoard parent1, BreakoutBoard parent2) {
        // Perform crossover between parent1 and parent2 to produce a child
        // Example: BreakoutBoard child = parent1.crossover(parent2);
        return null; // Placeholder, replace with actual crossover implementation
    }
}
