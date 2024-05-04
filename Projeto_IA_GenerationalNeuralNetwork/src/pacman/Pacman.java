package pacman;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFrame;

import breakout.Breakout;
import utils.Commons;
import utils.GameController;

public class Pacman extends JFrame {

	public Pacman(GameController c, boolean b, int seed) {
		EventQueue.invokeLater(() -> {
			add(new PacmanBoard(c, b, seed));

			setTitle("Pacman");
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setSize(380, 420);
			setLocationRelativeTo(null);
			setVisible(true);
		});
	}
	
	public static void main(String[] args) throws IOException {
		Pacman b = new Pacman(new algorithms.GeneticAlgorithm(Commons.PACMAN_STATE_SIZE, 10, Commons.PACMAN_NUM_ACTIONS).beginSearch(Commons.PACMAN_STATE_SIZE, 10, Commons.PACMAN_NUM_ACTIONS), true, Commons.SEED);
	}

}