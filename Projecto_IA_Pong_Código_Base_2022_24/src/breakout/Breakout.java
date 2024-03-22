package breakout;

import javax.swing.JFrame;

import pacman.RandomController;
import utils.GameController;
import AlgoritmoGenetico.AlgoritmoGenetico;

public class Breakout extends JFrame {

	private static final long serialVersionUID = 1L;

	public Breakout(GameController network, int i) {
		add(new BreakoutBoard(network, true, i));
		setTitle("Breakout");

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		pack();
		setVisible(true);
	}
	
	public static void main(String[] args) {
        AlgoritmoGenetico ag = new AlgoritmoGenetico();

        BreakoutBoard sol = ag.iniciaProcura();
        if (sol == null) {
            System.out.println("Nao foi encontrada nenhuma solucao");
        } else {
            System.out.println(sol);
        }
    }
}
