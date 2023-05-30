// Version: L2
package connectx.L2;
import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import java.util.concurrent.TimeoutException;

public class L2 implements CXPlayer{
    private boolean first;
    private Integer selectedColumn;
    private Integer startingDepth = 10;
    private int TIMEOUT;
    private long START;
    private int M;
    private int N;
    private int K;

	/* Default empty constructor */

    //temporanea copia del paleyr L0


	public L2() {
	}
	public void initPlayer(int M, int N, int K,  boolean first, int timeout_in_secs) {
        this.first = first;
        this.TIMEOUT = timeout_in_secs;
        this.M = M;
        this.N = N;
        this.K = K;
	}



	public int selectColumn(CXBoard B) {
		AlphaBetaForbiddenColumns(B, first, Integer.MIN_VALUE, Integer.MAX_VALUE, startingDepth);
        //IterativeDeepening(B, first);
        return selectedColumn;

	}


	public String playerName() {
		return "L2";
	}
    private void checktime() throws TimeoutException {
		if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (1/ N))
			throw new TimeoutException();
	} 



    //static eval basata sul numero di colonne proibite
    public int staticEval(CXBoard B,boolean playerA){
        // usando AphaBeta posso assumere: non ho vinto e tocca a me.
        if (playerA) {
            int colonne_proibite = 0;
            Integer[] columns = B.getAvailableColumns();
            for (Integer c : columns) {
                B.markColumn(c);
                CXGameState state = B.gameState();
                if (state != CXGameState.OPEN) {
                    if (state == CXGameState.WINP1)  {
                        B.unmarkColumn();
                        return Integer.MAX_VALUE;
                    }
                    else {
                        return 0;
                    }
                }
                if ((!B.fullColumn(c))&& B.gameState() == CXGameState.OPEN) {
                    B.markColumn(c);
                    if (B.gameState() == CXGameState.WINP2) {
                        B.unmarkColumn();
                        B.unmarkColumn();
                        //c diventa una colonna proibita PER ME p1
                        colonne_proibite++;
                        return Integer.MAX_VALUE-1;
                    }
                    B.unmarkColumn();
                    
                }
                B.unmarkColumn();
            }

            return 1 - (colonne_proibite*colonne_proibite);
        }
        else {
            int colonne_proibite = 0;
            Integer[] columns = B.getAvailableColumns();
            for (Integer c : columns) {
                B.markColumn(c);
                CXGameState state = B.gameState();
                if (state != CXGameState.OPEN) {
                    if (state == CXGameState.WINP2)  {
                        B.unmarkColumn();
                        return Integer.MIN_VALUE;
                    }
                    else {
                        return 0;
                    }   
                }
                if ((!B.fullColumn(c)) && B.gameState() == CXGameState.OPEN) {
                    B.markColumn(c);
                    if (B.gameState() == CXGameState.WINP1) {
                        B.unmarkColumn();
                        B.unmarkColumn();
                        //c diventa una colonna proibita PER ME p2
                        colonne_proibite++;
                        return Integer.MIN_VALUE+1;
                    }
                    B.unmarkColumn();
                    
                }
                B.unmarkColumn();
            }
            return -1 + (colonne_proibite*colonne_proibite);
        }
    }





    //un alpha beta pruning che funziona!
    public int AlphaBetaForbiddenColumns(CXBoard T, boolean playerA, int alpha, int beta, int depth) {
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


                    } 
                    else { 
                        Integer[] columns = T.getAvailableColumns();
                        if (columns.length == 1){
                            if (depth == startingDepth) {
                                selectedColumn = columns[0];
                            }
                            //rimanme una sola linea di gioco possibilie, la porto fino alla sua conclusione
                            int iterazioni = 0;
                            int c = columns[0];
                            while((!T.fullColumn(c)) && T.gameState() == CXGameState.OPEN){
                                T.markColumn(c);
                                iterazioni++;
                            }
                            CXGameState state = T.gameState();
                            for (int i = 0; i <= iterazioni; i++) {
                                T.unmarkColumn();
                            }
                            if (state == CXGameState.WINP1) {
                                return Integer.MAX_VALUE;
                            }
                            else {
                                if (state == CXGameState.WINP2) {
                                    return Integer.MIN_VALUE;
                                }
                                else {
                                    
                                    return 0;
                                    
                                }
                            }
                        }
                        else {
                            if (playerA) { // MAX player
                            int eval = Integer.MIN_VALUE;
                            if (depth == startingDepth) {
                                selectedColumn = columns[0];
                            }
                            for (Integer c : columns) {
                                T.markColumn(c);
                                
                                int recEval = AlphaBetaForbiddenColumns(T, false, alpha, beta, depth - 1); //calcolo il massimo in questo modo per poter aggiornare la colonna selezionata
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


                            } 
                            else { // MIN player
                                int eval = Integer.MAX_VALUE;
                                if (depth == startingDepth) {
                                    selectedColumn = columns[0];
                                }
                                for (Integer c : columns) {
                                    T.markColumn(c);

                                    int recEval = AlphaBetaForbiddenColumns(T, true, alpha, beta, depth - 1); //calcolo il minimo in questo modo per poter aggiornare la colonna selezionata
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

