package connectx.L2;
import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import java.util.Random;
public class L2 implements CXPlayer{
    private Random rand;
    private boolean first;
    private Integer selectedColumn;
    private Integer startingDepth = 5;

	/* Default empty constructor */

    //temporanea copia del paleyr L0


	public L2() {
	}

	public void initPlayer(int M, int N, int K,  boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand = new Random(System.currentTimeMillis());
        this.first = first;
	}

	/* Selects a random column */
	public int selectColumn(CXBoard B) {
		AlphaBeta(B, first, 0, 0, startingDepth);
        return selectedColumn;

	}

	public String playerName() {
		return "L2";
	}
    //fine copia temporanea


    //static eval copiata di brutto dal player L1 lol
    public int staticEval(CXBoard B,boolean playerA){

        if (playerA) {


            return 1;
        }
        else {
            return -1;
        }
    }

    //un alpha beta pruning che forse ha tipo circa senso

    public int AlphaBeta(CXBoard T, boolean playerA, int alpha, int beta, int depth) {
        if (T.gameState() == CXGameState.WINP1) {
            return Integer.MAX_VALUE;
        }
        else {
            if (T.gameState() == CXGameState.WINP2) {
                return Integer.MIN_VALUE;
            }
            else {
                if (T.gameState() == CXGameState.DRAW) {
                    return 0;
                }
                else {
                    if (depth == 0) {
                        int eval = staticEval(T, playerA);
                        return eval;
                    } else if (playerA) { // MAX player
                        int eval = Integer.MIN_VALUE;
                        Integer[] columns = T.getAvailableColumns();
                        for (Integer c : columns) {
                            T.markColumn(c);

                            int recEval = AlphaBeta(T, false, alpha, beta, depth - 1); //calcolo il massimo in questo modo per poter aggiornare la colonna selezionata
                            if (recEval > eval) {                                              //quando necessario.
                                eval = recEval;
                                if (depth == startingDepth) {
                                    selectedColumn = c;
                                }
                            }


                            alpha = Math.max(eval, alpha);
                            T.unmarkColumn();
                            if (beta <= alpha) { // β cutoff
                                break;
                            }
                        }
                        
                        return eval;
                    } else { // MIN player
                        int eval = Integer.MAX_VALUE;
                        Integer[] columns = T.getAvailableColumns();
                        for (Integer c : columns) {
                            T.markColumn(c);

                            int recEval = AlphaBeta(T, true, alpha, beta, depth - 1); //calcolo il minimo in questo modo per poter aggiornare la colonna selezionata
                            if (recEval < eval) {                                             //quando necessario.
                                eval = recEval;
                                if (depth == startingDepth) {
                                    selectedColumn = c;
                                }
                            }

                            beta = Math.min(eval, beta);
                            T.unmarkColumn();
                            if (beta <= alpha) { // α cutoff
                                break;
                            }
                        }
                        
                        return eval;
                    }
                }
            }
        }
        
    }
    

    

}

