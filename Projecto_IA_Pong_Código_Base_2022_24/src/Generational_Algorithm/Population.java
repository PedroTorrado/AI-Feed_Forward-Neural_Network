import java.util.ArrayList;
import java.util.List;

import breakout.BreakoutBoard;
import utils.GameController;

public class Population {
	
	//TODO : Change everything to evaluate GameController ' s

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

    public void crossoverAndMutation(List<NeuralNetworkGameController> parents) {
        List<NeuralNetworkGameController> offspring = new ArrayList<>();

        // Perform crossover and mutation for each pair of parents
        for (int i = 0; i < parents.size(); i += 2) {
            // Get the two parents for crossover
            NeuralNetworkGameController parent1 = parents.get(i);
            NeuralNetworkGameController parent2 = parents.get(i + 1);

            // Perform crossover to create offspring
            NeuralNetworkGameController child1 = crossover(parent1, parent2);
            NeuralNetworkGameController child2 = crossover(parent2, parent1); // Perform crossover with parents swapped

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

    private void replaceAgents(List<NeuralNetworkGameController> offspring) {

        // Replace agents in the population with the offspring
        for (int i = 0; i < offspring.size(); i++) {
            BreakoutBoardList.set(i, offspring.get(i));
        }
    }

	private void mutate(NeuralNetworkGameController child) {
        double mutationRate = 0.005; // Mutation rate of 0.5%

        // Mutate the hidden weights with the specified probability
        for (int i = 0; i < child.hiddenWeights.length; i++) {
            for (int j = 0; j < child.hiddenWeights[i].length; j++) {
                if (Math.random() < mutationRate) {
                    // Add a small random value to the weight
                    child.hiddenWeights[i][j] += (Math.random() * 0.1) - 0.05; // Mutation range: -0.05 to 0.05
                }
            }
        }

        // Mutate the hidden biases with the specified probability
        for (int i = 0; i < child.hiddenBiases.length; i++) {
            if (Math.random() < mutationRate) {
                // Add a small random value to the bias
                child.hiddenBiases[i] += (Math.random() * 0.1) - 0.05; // Mutation range: -0.05 to 0.05
            }
        }

        // Mutate the output weights with the specified probability
        for (int i = 0; i < child.outputWeights.length; i++) {
            for (int j = 0; j < child.outputWeights[i].length; j++) {
                if (Math.random() < mutationRate) {
                    // Add a small random value to the weight
                    child.outputWeights[i][j] += (Math.random() * 0.1) - 0.05; // Mutation range: -0.05 to 0.05
                }
            }
        }

        // Mutate the output biases with the specified probability
        for (int i = 0; i < child.outputBiases.length; i++) {
            if (Math.random() < mutationRate) {
                // Add a small random value to the bias
                child.outputBiases[i] += (Math.random() * 0.1) - 0.05; // Mutation range: -0.05 to 0.05
            }
        }
    }

    
    public NeuralNetworkGameController crossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {
        List<NeuralNetworkGameController> offspring = new ArrayList<>();
        NeuralNetworkGameController child;

        // Perform crossover for each pair of parents
        for (int i = 0; i < 2; i += 2) {
            if (i + 1 < 2) {
                // Perform crossover operation between parent1 and parent2
                // Create offspring using genetic material from both parents
            	child = performCrossover(parent1, parent2);
            }
        }
		return child;
    }

    private NeuralNetworkGameController performCrossover(NeuralNetworkGameController parent1, NeuralNetworkGameController parent2) {
        double[][] parent1HiddenWeights = parent1.hiddenWeights;
        double[][] parent2HiddenWeights = parent2.hiddenWeights;
        double[][] parent1OutputWeights = parent1.outputWeights;
        double[][] parent2OutputWeights = parent2.outputWeights;

        // Determine the crossover point
        int crossoverPoint = parent1HiddenWeights.length / 2;

        // Create a new child NeuralNetworkGameController instance
        NeuralNetworkGameController child = new NeuralNetworkGameController(inputDim, hiddenDim, outputDim, values); // You need to pass appropriate values here

        // Perform crossover: take the first half from parent1 and the second half from parent2
        for (int i = crossoverPoint; i < parent1HiddenWeights.length; i++) {
            // Swap hidden weights between parents
            double[] tempHiddenWeights = parent1HiddenWeights[i];
            parent1HiddenWeights[i] = parent2HiddenWeights[i];
            parent2HiddenWeights[i] = tempHiddenWeights;

            // Swap output weights between parents
            double[] tempOutputWeights = parent1OutputWeights[i];
            parent1OutputWeights[i] = parent2OutputWeights[i];
            parent2OutputWeights[i] = tempOutputWeights;
        }

        // Set the weights to the child NeuralNetworkGameController
        child.setHiddenWeights(parent1HiddenWeights);
        child.setOutputWeights(parent1OutputWeights);

        // Return the child NeuralNetworkGameController
        return child;
    }
    
    
    //TODO
    public BreakoutBoard runWithNeuralNetwork(BreakoutBoard initialBoard, NeuralNetworkGameController nnController) {
        // Create a copy of the initial BreakoutBoard
        BreakoutBoard currentBoard = new BreakoutBoard(initialBoard.getController(), initialBoard.isWithGui(), initialBoard.getSeed());

        // Initialize the game with the same state as the initial board
        currentBoard.setBall(new Ball(initialBoard.getBall()));
        currentBoard.setPaddle(new Paddle(initialBoard.getPaddle()));
        Brick[] initialBricks = initialBoard.getBricks();
        Brick[] copiedBricks = new Brick[Commons.N_OF_BRICKS];
        for (int i = 0; i < Commons.N_OF_BRICKS; i++) {
            copiedBricks[i] = new Brick(initialBricks[i]);
        }
        currentBoard.setBricks(copiedBricks);
        currentBoard.setInGame(true);
        currentBoard.setTime(0);
        currentBoard.setKills(0);

        // Run the simulation until the game reaches a terminal state or a predefined number of steps
        while (currentBoard.isInGame() && currentBoard.getTime() <= 100000) {
            // Get the current state of the game
            int[] currentState = currentBoard.getState();

            // Preprocess the current state if necessary
            // No preprocessing required in this example

            // Use the neural network to get predictions for the next move
            double[] predictions = nnController.predict(currentState);

            // Determine the action based on the predictions (e.g., move the paddle)
            int action = determineAction(predictions);

            // Take the action in the game
            currentBoard.makeMove(action);
            currentBoard.getBall().move();
            currentBoard.getPaddle().move();
            currentBoard.checkCollision();
            currentBoard.setTime(currentBoard.getTime() + 1);
        }

        return currentBoard;
    }

}
