package connectx.FardoPlayer;

import connectx.CXBoard;
import connectx.CXGameState;

public class ColumSelector {
    // private boolean maximizingPlayer;

    public ColumSelector() {
        // this.maximizingPlayer = maximizingPlayer;
    }
    
    public Integer alphaBeta(CXBoard board, boolean maximizingPlayer, int alpha, int beta){
        if (board.gameState() != CXGameState.OPEN)
            return evaulateBoard(board, maximizingPlayer);
        
        System.out.println(board.toString());
        Integer eval;

        if (maximizingPlayer){
            eval = Integer.MIN_VALUE;

            for (Integer i : board.getAvailableColumns()){
                board.markColumn(i);
                
                eval = Integer.max(alphaBeta(board, false, alpha, beta), eval);
                alpha = Integer.max(alpha, eval);
                if (beta <= alpha){
                    break;
                }
            }
        } else {
            eval = Integer.MAX_VALUE;

            for (Integer i : board.getAvailableColumns()){
                board.markColumn(i);
                
                eval = Integer.min(alphaBeta(board, true, alpha, beta), eval);
                beta = Integer.min(beta, eval);
                if (beta <= alpha){
                    break;
                }
            }
        }

        return eval;
    }

    public int evaulateBoard(CXBoard board, boolean maximizingPlayer){
    /*
    *  -5000 per la loss
    *   5000 win
    *   1 pareggio
    *   0 indefinito
    */

        int eval = board.gameState() == CXGameState.WINP1 &&  maximizingPlayer ? 5000 : 0;
        eval = board.gameState() == CXGameState.WINP2 && !maximizingPlayer ? 5000 : eval;
        eval = board.gameState() == CXGameState.WINP1 && !maximizingPlayer ? -5000 : eval;
        eval = board.gameState() == CXGameState.WINP2 && maximizingPlayer ? -5000 : eval;
        eval = board.gameState() == CXGameState.DRAW ? 1 : eval;
        //eval = board.gameState() == CXGameState.OPEN ? 0 : eval;

        return eval;
    }

    
}
