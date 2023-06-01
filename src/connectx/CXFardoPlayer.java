package connectx;
import java.util.concurrent.TimeoutException;
public class CXFardoPlayer implements CXPlayer{
    private boolean first;
    private Integer selectedColumn;
    private Integer startingDepth = 9;
    private int TIMEOUT;
    private long START;
    private int M;
    private int N;
    private int K;

	/* Default empty constructor */

    //temporanea copia del paleyr L0


	public CXFardoPlayer() {
	}
	public void initPlayer(int M, int N, int K,  boolean first, int timeout_in_secs) {
        this.first = first;
        this.TIMEOUT = timeout_in_secs;
        this.M = M;
        this.N = N;
        this.K = K;
	}



	public int selectColumn(CXBoard B) {
		AlphaBeta(B, first, Integer.MIN_VALUE, Integer.MAX_VALUE, startingDepth);
        //IterativeDeepening(B, first);
        return selectedColumn;

	}


	public String playerName() {
		return "CXFardoPlayer";
	}
    private void checktime() throws TimeoutException {
		if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (1/ N))
			throw new TimeoutException();
	} 



    //static eval dummy
    public int staticEval(CXBoard B,boolean playerA){
        // usando AphaBeta posso assumere: non ho vinto e tocca all'avversario
        if (playerA) {


            return 1;
        }
        else {
            return -1;
        }
    }



    //un alpha beta pruning che funziona!
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
                        if (depth == startingDepth) {
                            selectedColumn = columns[0];
                        }
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
                        if (depth == startingDepth) {
                            selectedColumn = columns[0];
                        }
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
    



    
    /* 
    public void IterativeDeepening (CXBoard T, boolean playerA) {
        START = System.currentTimeMillis();
        try {
            int depth = 1;
            while(true) {
                checktime();
                AlphaBeta(T, playerA, Integer.MIN_VALUE, Integer.MAX_VALUE, depth);
                depth++;
            }
        } catch (TimeoutException e) {
            return;
        }
        
    }
    
    */
}
