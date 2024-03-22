package AlgoritmoGenetico;

import java.util.Arrays;

import breakout.BreakoutBoard;
import utils.GameController;

public class AlgoritmoGenetico {


    private static final int NUM_POPULACAO = 100;
    private static final int NUM_GERACOES = 1000;
    private static final double MUTATION_RATE = 0.1;

    private int seed;

    private BreakoutBoard[] populacao;

    private GameController controller;
    
    public AlgoritmoGenetico(){
        populacao = new BreakoutBoard[NUM_POPULACAO];
        criaPopulacao();
    }

    private void criaPopulacao(){
        for(int i = 0; i <NUM_POPULACAO; i++){
            BreakoutBoard bb = new BreakoutBoard(); //TODO
        }
    }

    public BreakoutBoard iniciaProcura(){
        for(int i = 0; i < NUM_GERACOES; i++){
            BreakoutBoard[] novaPopulacao = new BreakoutBoard[NUM_POPULACAO];
            Arrays.sort(populacao);

            System.out.println("Numero de geração: " + i);

            for(int k = 0; k < NUM_POPULACAO; k++){
                System.out.println(populacao[k]);
            }
/*
            if(populacao[0].getFitness() == //TODO){
                System.out.println("Resultado encontrado na geração: " + i);
                return populacao[0];
            }
*/
                        
            for(int j = 0; j < NUM_POPULACAO/2; j++){
                BreakoutBoard paiUm = selectParent();
                BreakoutBoard paiDois = selectParent();
                BreakoutBoard[] filhos = crossOver(paiUm, paiDois);
                novaPopulacao[j] = filhos[0];
                novaPopulacao[j + 50] = filhos[1];
            }
            for(int z = 0; z < NUM_POPULACAO; z++){
                double rand = Math.random();
                if(rand < 0.05){
                    novaPopulacao[z].changeRandValue();
                }
            }
            populacao = novaPopulacao;
        }

        return null;
    }

    private BreakoutBoard selectParent(){
        BreakoutBoard paiUm = populacao[(int) (Math.random()*NUM_POPULACAO)];
        BreakoutBoard paiDois = populacao[(int) (Math.random()*NUM_POPULACAO)];
        if(paiUm.getFitness() > paiDois.getFitness()){
            return paiUm;
        }
        return paiDois;
    }

    private BreakoutBoard[] crossOver(BreakoutBoard a, BreakoutBoard b){

        int rand = (int) (Math.random() * TAMANHO_TABULEIRO);
        while(rand == 0 || rand == TAMANHO_TABULEIRO){
            rand = (int) (Math.random() * TAMANHO_TABULEIRO);
        }
        int[] aVector = new int[TAMANHO_TABULEIRO];
        int[] bVector = new int[TAMANHO_TABULEIRO];

        for(int i = 0; i < TAMANHO_TABULEIRO; i++){
            if(i < rand) {
                //Encontrar qual a forma de avaliar cada jogo, calculo que não seja por vetores
                aVector[i] = a.getPosicoes_rainhas()[i];
                bVector[i] = b.getPosicoes_rainhas()[i];
            } else {
                //Encontrar qual a forma de avaliar cada jogo, calculo que não seja por vetores
                aVector[i] = b.getPosicoes_rainhas()[i];
                bVector[i] = a.getPosicoes_rainhas()[i];
            }
        }

        BreakoutBoard[] parents = new BreakoutBoard[2];
        parents[0] = new BreakoutBoard(controller, false, //TODO);
        parents[1] = new BreakoutBoard(controller, false, //TODO);

        return rainhas;

    }

}
