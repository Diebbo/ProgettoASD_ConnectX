// Version: L2
package connectx.L2;
import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXCell;
import connectx.CXCellState;
import java.util.concurrent.TimeoutException;
import java.util.List;
import java.util.ArrayList;

public class L2 implements CXPlayer{
    private boolean first;
    private Integer startingDepth = 10;
    private int TIMEOUT;
    private long START;
    private int M; // numero di righe
    private int N; // numero di colonne
    private int K; // numero di pedine da allineare per vincere

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
        
		
        //IterativeDeepening(B, first);
        if (B.numOfMarkedCells()==0) {
            return N/2;
        }
        else {
            return alphaBetaCaller(B, startingDepth, first);
        }
        

	}


	public String playerName() {
		return "L2";
	}

    private void checktime() throws TimeoutException {
		if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (1/ N))
			throw new TimeoutException();
	} 



    private int stateConverter(CXGameState state) {
        if (state == CXGameState.WINP1) {
            return Integer.MAX_VALUE;
        }
        else if (state == CXGameState.WINP2) {
            return Integer.MIN_VALUE;
        }
        else {
            return 0;
        }
    }


    private int alphaBetaCaller (CXBoard B, int depth, boolean playerA) {
        Integer[] columns = B.getAvailableColumns();
        int eval;
        int move = columns[0];
        if (playerA) {
            eval = Integer.MIN_VALUE;
        }
        else {
            eval = Integer.MAX_VALUE;
        } 
        for (Integer c : columns) {
            B.markColumn(c);
            int recEval = AlphaBetaForbiddenColumns(B, !playerA, Integer.MIN_VALUE, Integer.MAX_VALUE, depth-1);
            
            B.unmarkColumn();
            /* sto center bias funziona come me (molto male :( )
            int n = N/2;
            int centerBias  = c-n; // se è grande è lontano dal centro
            if (centerBias < 0) {
                centerBias = -centerBias;
            }
            */
            
            if (playerA) {
                //recEval = recEval - centerBias;
                System.out.println("colonna: " + c + " eval: " + recEval);
                if (recEval > eval) {
                    eval = recEval;
                    move = c;
                }
                
            }
            else {
                //recEval = recEval + centerBias;
                System.out.println("colonna: " + c + " eval: " + recEval);
                if (recEval < eval) {
                    eval = recEval;
                    move = c;
                }
            }
        }
        return move;
    }








    //static eval basata sul numero di colonne proibite
    public int staticEval(CXBoard B,boolean playerA){
        // usando AphaBeta posso assumere: non ho vinto e tocca a me.
        if (playerA) { ///////////////////////////////////////////////////////Analisi per il maximizer

            int punteggio = 0;
            Integer[] columns = B.getAvailableColumns();
            //TEMPORANEO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (columns.length == 1){
                //rimanme una sola linea di gioco possibilie, la porto fino alla sua conclusione e restituisco un valore esatto.
                int iterazioni = 0;
                int c = columns[0];
                while((!B.fullColumn(c)) && B.gameState() == CXGameState.OPEN){
                    B.markColumn(c);
                    iterazioni++;
                }
                CXGameState state = B.gameState();
                for (int i = 0; i <= iterazioni; i++) {
                    B.unmarkColumn();
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
            //FINE TEMPORANEO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            /////////////////////////////////////colonne proibite.
            int colonne_proibite = 0;
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
                        return Integer.MIN_VALUE+1;
                    }
                    B.unmarkColumn();
                    
                }
                B.unmarkColumn();
            }
            punteggio = punteggio - (100*colonne_proibite*colonne_proibite);
            /////////////////////////////////////////////////fine colonne proibite.

            //colonne obbligate MIE, posso assumere di averne libere più di una.
            List<Integer> obbligatorie = new ArrayList<Integer>();
            B.markColumn(columns[0]);
            Boolean isHead = true;
            for (Integer c : columns) {
                if (isHead) {
                    isHead = false;
                }
                else {
                    B.markColumn(c);
                    CXGameState state = B.gameState();
                    if (state == CXGameState.WINP2) {
                        obbligatorie.add(c);
                    }
                    B.unmarkColumn();
                }
            }
            B.unmarkColumn();
            //controllo la prima che avevo precedentemente escluso a tavolino
            B.markColumn(columns[1]);
            B.markColumn(columns[0]);
            CXGameState state = B.gameState();
            if (state == CXGameState.WINP2) {
                obbligatorie.add(columns[0]);
            }
            B.unmarkColumn();
            B.unmarkColumn();
            if (obbligatorie.size() > 1) {
                return Integer.MIN_VALUE+2;
            }
            punteggio = punteggio - 1000*obbligatorie.size();

            //Inizio center bias
            CXCell ultimaMossa = B.getLastMove();
            // colonna = ultimaMossa.j
            
            int n = N/2;
            int centerBias  = ultimaMossa.j-n; // se è grande è lontano dal centro
            if (centerBias < 0) {
                centerBias = -centerBias;
            }
            centerBias =  centerBias*10;
            punteggio = punteggio - centerBias;
            ///////////Adjacensies check (è una valutazione più sulla mossa che sulla posizione ma cionondimeno la ritengo rilevante)
            //controllo orizzontale, ricordo che SO di non aver vinto.
            int y = ultimaMossa.i;
            int x = ultimaMossa.j;
            int laterali = 0;
            for (int i = Math.max(0,x-K+2); i<Math.min(M,x+K-2);i++) {
                if (B.cellState(y,i) == CXCellState.P2) {
                    if (i<x) {
                        laterali = 0;
                    }
                    else {
                        break;
                    }
                }
                else {
                    if (B.cellState(y,i) == CXCellState.P1) {
                        laterali++;
                    }
                }
            }
            //controllo verticale
            int verticali = 0;
            for (int i = 0; i< Math.min(y,K-2); i++) {
                if (B.cellState(y-i,x) == CXCellState.P2) {
                    break;
                }
                else {
                    if (B.cellState(y-i,x) == CXCellState.P1) {
                        verticali++;
                    }
                }
            }
            //controllo diagonale da fare (rn non ho voglia)
            int obliqui1 = 0;














            punteggio = punteggio + 1000*laterali + 1000*verticali + 1000*obliqui1;
            return punteggio;
        }

        else {//////////////////////////////////////////////////////////Analisi per il minimizer
            int punteggio = 0;
            int colonne_proibite = 0;
            Integer[] columns = B.getAvailableColumns();
            //TEMPORANEO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (columns.length == 1){
                //rimanme una sola linea di gioco possibilie, la porto fino alla sua conclusione e restituisco un valore esatto.
                int iterazioni = 0;
                int c = columns[0];
                while((!B.fullColumn(c)) && B.gameState() == CXGameState.OPEN){
                    B.markColumn(c);
                    iterazioni++;
                }
                CXGameState state = B.gameState();
                for (int i = 0; i <= iterazioni; i++) {
                    B.unmarkColumn();
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
            //FINE TEMPORANEO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            /////////////////////////////////////colonne proibite.
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
                        return Integer.MAX_VALUE-1;
                    }
                    B.unmarkColumn();
                    
                }
                B.unmarkColumn();
            }
            punteggio = punteggio + (100*colonne_proibite*colonne_proibite);
            ///////////////////////////////////////////////fine colonne proibite.

            ///////////colonne obbligate MIE, posso assumere di averne libere più di una.
            List<Integer> obbligatorie = new ArrayList<Integer>();
            B.markColumn(columns[0]);
            Boolean isHead = true;
            for (Integer c : columns) {
                if (isHead) {
                    isHead = false;
                }
                else {
                    B.markColumn(c);
                    CXGameState state = B.gameState();
                    if (state == CXGameState.WINP1) {
                        obbligatorie.add(c);
                    }
                    B.unmarkColumn();
                }
            }
            B.unmarkColumn();
            ///////////controllo la prima che avevo precedentemente escluso a tavolino
            B.markColumn(columns[1]);
            B.markColumn(columns[0]);
            CXGameState state = B.gameState();
            if (state == CXGameState.WINP1) {
                obbligatorie.add(columns[0]);
            }
            B.unmarkColumn();
            B.unmarkColumn();
            if (obbligatorie.size() > 1) {
                return Integer.MAX_VALUE-2;
            }
            punteggio = punteggio + 1000*obbligatorie.size();

            //Inizio center bias
            CXCell ultimaMossa = B.getLastMove();
            // colonna = ultimaMossa.j
            int n = N/2;
            int centerBias  = ultimaMossa.j-n; // se è grande è lontano dal centro
            if (centerBias < 0) {
                centerBias = -centerBias;
            }
            centerBias =  centerBias*10;
            punteggio = punteggio + centerBias;
            //Fine center bias
            //controllo laterale
            int y = ultimaMossa.i;
            int x = ultimaMossa.j;
            int laterali = 0;
            for (int i = Math.max(0,x-K+2); i<Math.min(M,x+K-2);i++) {
                if (B.cellState(y,i) == CXCellState.P1) {
                    if (i<x) {
                        laterali = 0;
                    }
                    else {
                        break;
                    }
                }
                else {
                    if (B.cellState(y,i) == CXCellState.P2) {
                        laterali++;
                    }
                }
            }
            //controllo verticale
            int verticali = 0;
            for (int i = 0; i< Math.min(y,K-2); i++) {
                if (B.cellState(y-i,x) == CXCellState.P1) {
                    break;
                }
                else {
                    if (B.cellState(y-i,x) == CXCellState.P2) {
                        verticali++;
                    }
                }
            }
            //controllo diagonale da fare (rn non ho voglia)
            int obliqui1 = 0;
            

            punteggio = punteggio - 1000*laterali - 1000*verticali - 1000*obliqui1;
            return punteggio;

            
        }
    }









    //un alpha beta pruning che funziona e deve essere chiamato dalla funzione alphaBetaCaller!!!
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

                        //lol questa condizione andrebbe scambiata con quella sotto :)
                        int eval = staticEval(T, playerA);
                        return eval;
                    } 
                    else { 
                        Integer[] columns = T.getAvailableColumns();
                        if (columns.length == 1){
                            //rimanme una sola linea di gioco possibilie, la porto fino alla sua conclusione e restituisco un valore esatto.
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
                            return stateConverter(state);
                        }
                        else {
                            if (playerA) { /////////////////////// MAX player
                                int eval = Integer.MIN_VALUE;
                                for (Integer c : columns) {
                                    T.markColumn(c);
                                    int recEval = AlphaBetaForbiddenColumns(T, false, alpha, beta, depth - 1); //calcolo il massimo in questo modo per poter aggiornare la colonna selezionata
                                    if (recEval > eval) {                                              //quando necessario.
                                        eval = recEval;
                                    }
                                    alpha = Math.max(eval, alpha);
                                    T.unmarkColumn();
                                    if (beta <= alpha) { // β cutoff
                                        break;
                                    }
                                }
                                //un return che abbassa leggermente il peso delle mosse mentre si propagano (perdere in una mossa è peggio che perdere in 2 mosse)
                                //return eval;
                                ///* 
                                if (eval > 1) {
                                    return eval-1;
                                }
                                else {
                                    if (eval < -1) {
                                        return eval+1;
                                    }
                                    else {
                                        return eval;
                                    }
                                }
                                //*/
                            } 

                            else { //////////////////////////// MIN player
                                int eval = Integer.MAX_VALUE;
                                for (Integer c : columns) {
                                    T.markColumn(c);
                                    int recEval = AlphaBetaForbiddenColumns(T, true, alpha, beta, depth - 1); //calcolo il minimo in questo modo per poter aggiornare la colonna selezionata
                                    if (recEval < eval) {                                             //quando necessario.
                                        eval = recEval;
                                    }
                                    beta = Math.min(eval, beta);
                                    T.unmarkColumn();
                                    if (beta <= alpha) { // α cutoff
                                        break;
                                    }
                                }
                                //un return che abbassa leggermente il peso delle mosse mentre si propagano (perdere in una mossa è peggio che perdere in 2 mosse)
                                //return eval;
                                ///* 
                                if (eval > 1) {
                                    return eval-1;
                                }
                                else {
                                    if (eval < -1) {
                                        return eval+1;
                                    }
                                    else {
                                        return eval;
                                    }
                                }
                                //*/
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

