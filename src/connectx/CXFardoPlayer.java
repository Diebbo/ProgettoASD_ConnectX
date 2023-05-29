package connectx;
import java.util.Random;
public class CXFardoPlayer implements CXPlayer{
    private Random rand;
    private boolean first;

	/* Default empty constructor */

    //temporanea copia del paleyr L0


	public CXFardoPlayer() {
	}

	public void initPlayer(int M, int N, int K,  boolean first, int timeout_in_secs) {
		// New random seed for each game
		rand = new Random(System.currentTimeMillis());
        this.first = first;
	}

	/* Selects a random column */
	public int selectColumn(CXBoard B) {
		Integer[] L = B.getAvailableColumns();
		return L[rand.nextInt(L.length)];
	}

	public String playerName() {
		return "CXFardoPlayer";
	}
    //fine copia temporanea


    //static eval copiata di brutto dal player L1 lol
    public int staticEval(CXBoard B){
        return 0;
        //metti qui il tuo codice :)
    }

    //un alpha beta pruning che forse ha tipo circa senso

    public int AlphaBeta(CXBoard T, boolean playerA, int alpha, int beta, int depth) {
        if (depth == 0 || T.gameState() != CXGameState.OPEN) {
            int eval = staticEval(T);
            return eval;
        } else if (playerA) { // MAX player
            int eval = Integer.MIN_VALUE;
            Integer[] columns = T.getAvailableColumns();
            for (Integer c : columns) {
                T.markColumn(c);
                eval = Math.max(eval, AlphaBeta(T, false, alpha, beta, depth - 1));
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
                eval = Math.min(eval, AlphaBeta(T, true, alpha, beta, depth - 1));
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
